package com.hexagram.febys.ui.screens.product.listing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemProductListBinding
import com.hexagram.febys.models.api.product.Product

class ProductListingPagerAdapter :
    PagingDataAdapter<Product, ProductListingPagerAdapter.ProductPagerViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Product>() {

            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem._id == newItem._id
            }
        }
    }

    var interaction: Interaction? = null
    private var fav = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductPagerViewHolder {
        return ProductPagerViewHolder(
            ItemProductListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductPagerViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return, position)
    }

    fun totalSize() = itemCount

    fun submitFav(fav: MutableSet<String>) {
        this.fav = fav
    }

    inner class ProductPagerViewHolder(
        private val binding: ItemProductListBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Product, position: Int) {
            binding.root.setOnClickListener {
                interaction?.onItemSelected(position, item)
            }

            val skuId = item.variants[0].skuId

            binding.isFav = skuId in fav

            binding.ivFavToggle.setOnClickListener {
                val isUserLoggedIn = interaction?.toggleFavIfUserLoggedIn(skuId) ?: false
                if (!isUserLoggedIn) return@setOnClickListener

                if (skuId in fav) {
                    fav.remove(skuId)
                } else {
                    fav.add(skuId)
                }
                notifyItemChanged(position)
            }

            binding.product = item
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Product)
        fun toggleFavIfUserLoggedIn(skuId: String): Boolean
    }
}