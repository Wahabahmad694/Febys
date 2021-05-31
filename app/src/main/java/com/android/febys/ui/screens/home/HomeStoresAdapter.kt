package com.android.febys.ui.screens.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.febys.R
import com.android.febys.databinding.ItemStoreYouFollowBinding

class HomeStoresAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return VH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_store_you_follow, parent, false
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

    fun submitList(list: List<String>) {
        differ.submitList(list)
    }

    class VH constructor(
        itemView: View, private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemStoreYouFollowBinding.bind(itemView)

        fun bind(item: String, position: Int) {
            binding.root.setOnClickListener {
                interaction?.onItemSelected(position, item)
            }

            binding.imageUrl = item
            binding.tvStoreName.text = "Deal-Train store"
            binding.tvStoreSlogan.text = "Patch belted denim dress"
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: String)
    }
}
