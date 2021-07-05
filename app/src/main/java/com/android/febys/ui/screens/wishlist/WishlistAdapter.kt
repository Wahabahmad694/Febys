package com.android.febys.ui.screens.wishlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.febys.databinding.ItemWishListBinding
import com.android.febys.network.response.Product

class WishlistAdapter : ListAdapter<Product, WishlistAdapter.WishlistViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Product>() {

            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }
        }
    }

    var interaction: Interaction? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        return WishlistViewHolder(
            ItemWishListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class WishlistViewHolder(val binding: ItemWishListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Product, position: Int) {
            binding.root.setOnClickListener {
                interaction?.onItemSelected(position, item)
            }

            binding.product = item
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Product)
    }
}