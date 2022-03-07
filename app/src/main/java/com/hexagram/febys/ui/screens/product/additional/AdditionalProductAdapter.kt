package com.hexagram.febys.ui.screens.product.additional

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemAdditionalProductBinding
import com.hexagram.febys.models.api.product.Product

class AdditionalProductAdapter :
    RecyclerView.Adapter<AdditionalProductAdapter.ItemAdditionalProductViewHolder>() {
    var products = listOf<Product>()

    var onItemClick: ((Product) -> Unit)? = null

    inner class ItemAdditionalProductViewHolder(
        private val binding: ItemAdditionalProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Product, position: Int) = with(binding) {
            product = item
            root.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ItemAdditionalProductViewHolder {
        return ItemAdditionalProductViewHolder(
            ItemAdditionalProductBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemAdditionalProductViewHolder, position: Int) {
        holder.bind(products[position], position)
    }

    override fun getItemCount() = products.size

    fun submitList(list: List<Product>) {
        products = list
        notifyItemRangeChanged(0, products.size)
    }
}