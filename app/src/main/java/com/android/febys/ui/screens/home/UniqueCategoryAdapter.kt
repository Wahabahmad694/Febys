package com.android.febys.ui.screens.home

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.android.febys.databinding.ItemUniqueCategoryBinding
import com.android.febys.network.domain.models.UniqueCategory

class UniqueCategoryAdapter :
    ListAdapter<UniqueCategory, UniqueCategoryAdapter.UniqueCategoryViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<UniqueCategory>() {

            override fun areItemsTheSame(
                oldItem: UniqueCategory,
                newItem: UniqueCategory
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: UniqueCategory,
                newItem: UniqueCategory
            ): Boolean {
                return oldItem.productName == newItem.productName && oldItem.imgUrl == newItem.imgUrl
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

            binding.imageUrl = item.imgUrl
            binding.tvProductNameUniqueCategory.text = item.productName
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: UniqueCategory)
    }
}
