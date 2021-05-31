package com.android.febys.ui.screens.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.febys.R
import com.android.febys.databinding.ItemProductHomeBinding
import com.android.febys.models.Product

class HomeProductsAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.productName == newItem.productName
                    && oldItem.imgUrl == newItem.imgUrl
                    && oldItem.productPrice == newItem.productPrice
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return VH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_product_home, parent, false
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

    fun submitList(list: List<Product>) {
        differ.submitList(list)
    }

    class VH constructor(
        itemView: View, private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemProductHomeBinding.bind(itemView)

        fun bind(item: Product, position: Int) {
            binding.root.setOnClickListener {
                interaction?.onItemSelected(position, item)
            }

            binding.product = item
            binding.isFav = item.isFav

            binding.ivFav.setOnClickListener {
                item.isFav = !item.isFav
                binding.isFav = item.isFav
            }
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Product)
    }
}
