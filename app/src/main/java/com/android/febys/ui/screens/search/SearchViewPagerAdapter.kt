package com.android.febys.ui.screens.search

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.android.febys.R
import com.android.febys.databinding.ItemSearchViewPagerBinding
import com.android.febys.models.responses.Category

class SearchViewPagerAdapter : PagingDataAdapter<Category, RecyclerView.ViewHolder>(diffCallback) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return VH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_search_view_pager, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VH -> {
                holder.bind(getItem(position) ?: return, position)
            }
        }
    }

    inner class VH constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemSearchViewPagerBinding.bind(itemView)

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
