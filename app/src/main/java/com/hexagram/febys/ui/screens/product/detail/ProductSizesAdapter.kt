package com.hexagram.febys.ui.screens.product.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.R
import com.hexagram.febys.databinding.ItemProductSizeBinding
import com.hexagram.febys.network.response.ProductVariant

class ProductSizesAdapter : RecyclerView.Adapter<ProductSizesAdapter.ProductSizesViewHolder>() {
    private var sizesList = emptyList<ProductVariant>()
    private var selectedVariant = 0

    var interaction: ((variant: ProductVariant) -> Unit)? = null

    inner class ProductSizesViewHolder(
        private val binding: ItemProductSizeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(variant: ProductVariant, position: Int) {
            binding.root.setOnClickListener {
                interaction?.invoke(variant)
                updateSelectedVariant(position)
            }

            val size = variant.variant_attributes.first { it.name.lowercase() == "size" }.value
            binding.tvSize.text = size

            val style = if (position == selectedVariant) R.style.TextTheme_HelveticaNeue_TS14_Bold
            else R.style.TextTheme_HelveticaNeue_TS14_Grey

            binding.tvSize.setTextAppearance(style)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSizesViewHolder {
        return ProductSizesViewHolder(
            ItemProductSizeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductSizesViewHolder, position: Int) {
        holder.bind(sizesList[position], position)
    }

    override fun getItemCount() = sizesList.size

    fun submitList(list: List<ProductVariant>) {
        sizesList = list
        notifyDataSetChanged()
    }

    fun updateSelectedVariant(position: Int) {
        if (sizesList.isNotEmpty())
            notifyItemChanged(selectedVariant)

        selectedVariant = position

        if (sizesList.isNotEmpty())
            notifyItemChanged(selectedVariant)
        notifyItemChanged(selectedVariant)
    }
}