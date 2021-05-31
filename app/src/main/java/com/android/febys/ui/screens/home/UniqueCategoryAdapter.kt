package com.android.febys.ui.screens.home

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.android.febys.R
import com.android.febys.databinding.ItemUniqueCategoryBinding
import com.android.febys.models.UniqueCategory

class UniqueCategoryAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<UniqueCategory>() {

        override fun areItemsTheSame(oldItem: UniqueCategory, newItem: UniqueCategory): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: UniqueCategory, newItem: UniqueCategory): Boolean {
            return oldItem.productName == newItem.productName && oldItem.imgUrl == newItem.imgUrl
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return VH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_unique_category, parent, false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VH -> {
                holder.bind(differ.currentList[position], position)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<UniqueCategory>) {
        differ.submitList(list)
    }

    class VH constructor(
        itemView: View, private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemUniqueCategoryBinding.bind(itemView)

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
