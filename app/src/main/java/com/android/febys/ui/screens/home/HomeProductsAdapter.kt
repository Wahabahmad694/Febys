package com.android.febys.ui.screens.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.febys.databinding.ItemProductHomeBinding
import com.android.febys.network.domain.models.Product

class HomeProductsAdapter :
    ListAdapter<Product, HomeProductsAdapter.ProductHomeViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Product>() {

            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.productName == newItem.productName
                        && oldItem.imgUrl == newItem.imgUrl
                        && oldItem.productPrice == newItem.productPrice
            }
        }
    }

    var interaction: Interaction? = null

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

    inner class ProductHomeViewHolder(val binding: ItemProductHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Product, position: Int) {
            binding.root.setOnClickListener {
                interaction?.onItemSelected(position, item)
            }

            binding.product = item
            binding.isFav = item.isFav

            binding.ivFav.setOnClickListener {
                item.isFav = !item.isFav
                binding.isFav = item.isFav
            }
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Product)
    }
}
