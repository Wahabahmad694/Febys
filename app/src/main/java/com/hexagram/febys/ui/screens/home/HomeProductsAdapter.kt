package com.hexagram.febys.ui.screens.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemProductHomeBinding
import com.hexagram.febys.models.api.product.Product

class HomeProductsAdapter :
    ListAdapter<Product, HomeProductsAdapter.ProductHomeViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Product>() {

            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem._id == newItem._id
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }
        }
    }

    var interaction: Interaction? = null
    private var fav = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHomeViewHolder {
        return ProductHomeViewHolder(
            ItemProductHomeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductHomeViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    fun submitFav(fav: MutableSet<String>) {
        this.fav = fav
        notifyDataSetChanged()
    }

    inner class ProductHomeViewHolder(val binding: ItemProductHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Product, position: Int) {
            binding.root.setOnClickListener {
                interaction?.onItemSelected(position, item)
            }

            binding.product = item

            val skuId = item.variants[0].skuId
            binding.isFav = skuId in fav

            binding.ivFav.setOnClickListener {
                interaction?.onFavToggleClick(skuId)
            }
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Product)
        fun onFavToggleClick(skuId: String)
    }
}
