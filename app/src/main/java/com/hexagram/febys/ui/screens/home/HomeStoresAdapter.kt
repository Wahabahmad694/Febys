package com.hexagram.febys.ui.screens.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemStoreYouFollowBinding

class HomeStoresAdapter :
    ListAdapter<String, HomeStoresAdapter.StoreYouFollowViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<String>() {

            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    var interaction: Interaction? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreYouFollowViewHolder {
        return StoreYouFollowViewHolder(
            ItemStoreYouFollowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: StoreYouFollowViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class StoreYouFollowViewHolder(val binding: ItemStoreYouFollowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String, position: Int) {
            binding.root.setOnClickListener {
                interaction?.onItemSelected(position, item)
            }

            binding.imageUrl = item
            binding.storeName = "Deal-Train store"
            binding.storeSlogan = "Patch belted denim dress"
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: String)
    }
}
