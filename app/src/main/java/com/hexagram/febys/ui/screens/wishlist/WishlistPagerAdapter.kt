package com.hexagram.febys.ui.screens.wishlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemWishListBinding
import com.hexagram.febys.network.response.OldProduct

class WishlistPagerAdapter :
    PagingDataAdapter<OldProduct, WishlistPagerAdapter.WishlistViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<OldProduct>() {

            override fun areItemsTheSame(oldItem: OldProduct, newItem: OldProduct): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: OldProduct, newItem: OldProduct): Boolean {
                return oldItem.id == newItem.id
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
        holder.bind(getItem(position) ?: return, position)
    }

    inner class WishlistViewHolder(val binding: ItemWishListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OldProduct, position: Int) {
            binding.root.setOnClickListener {
                interaction?.onItemSelected(position, item)
            }

            binding.ivClose.setOnClickListener {
                val variantId = item.productVariants[0].id
                interaction?.removeFav(variantId)
            }

            binding.product = item
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: OldProduct)
        fun removeFav(variantId: Int)
    }
}