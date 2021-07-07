package com.hexagram.febys.ui.screens.search

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.hexagram.febys.databinding.ItemSearchViewPagerBinding
import com.hexagram.febys.network.response.Category

class SearchViewPagerAdapter :
    PagingDataAdapter<Category, SearchViewPagerAdapter.SearchViewPagerViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Category>() {

            override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    var interaction: Interaction? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewPagerViewHolder {
        return SearchViewPagerViewHolder(
            ItemSearchViewPagerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchViewPagerViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return, position)

    }

    inner class SearchViewPagerViewHolder(
        private val binding: ItemSearchViewPagerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Category, position: Int) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(position, item)
            }

            binding.tvNameItemSearchViewPager.text = item.name
            binding.ivRight.isVisible = item.totalChild > 0
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Category)
    }
}