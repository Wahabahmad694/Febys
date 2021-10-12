package com.hexagram.febys.ui.screens.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemCategoryNameBinding
import com.hexagram.febys.network.response.Category

class CategoryNamePagerAdapter :
    PagingDataAdapter<Category, CategoryNamePagerAdapter.CategoryNamePagerViewHolder>(diffCallback) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryNamePagerViewHolder {
        return CategoryNamePagerViewHolder(
            ItemCategoryNameBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryNamePagerViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return, position)

    }

    inner class CategoryNamePagerViewHolder(
        private val binding: ItemCategoryNameBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Category, position: Int) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(position, item)
            }

            binding.tvNameItemSearchViewPager.text = item.name
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Category)
    }
}
