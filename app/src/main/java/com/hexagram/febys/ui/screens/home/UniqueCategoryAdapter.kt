package com.hexagram.febys.ui.screens.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemUniqueCategoryBinding
import com.hexagram.febys.models.api.category.UniqueCategory

class UniqueCategoryAdapter :
    ListAdapter<UniqueCategory, UniqueCategoryAdapter.UniqueCategoryViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<UniqueCategory>() {

            override fun areItemsTheSame(
                oldItem: UniqueCategory, newItem: UniqueCategory
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: UniqueCategory, newItem: UniqueCategory
            ): Boolean {
                return oldItem._id == newItem._id
            }
        }
    }

    var interaction: Interaction? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UniqueCategoryViewHolder {
        return UniqueCategoryViewHolder(
            ItemUniqueCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: UniqueCategoryViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class UniqueCategoryViewHolder(val binding: ItemUniqueCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UniqueCategory, position: Int) {
            binding.root.setOnClickListener {
                interaction?.onItemSelected(position, item)
            }

            binding.item = item
            binding.executePendingBindings()
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: UniqueCategory)
    }
}
