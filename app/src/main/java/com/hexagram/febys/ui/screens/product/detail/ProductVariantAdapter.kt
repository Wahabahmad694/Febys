package com.hexagram.febys.ui.screens.product.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.R
import com.hexagram.febys.databinding.ItemProductVariantBinding

class ProductVariantAdapter : RecyclerView.Adapter<ProductVariantAdapter.ProductSizesViewHolder>() {
    private var selectedVariantAttr = ""
    private var variants = emptyList<String>()

    var interaction: ((variant: String) -> Unit)? = null

    inner class ProductSizesViewHolder(
        private val binding: ItemProductVariantBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(variantAttr: String, position: Int) {
            binding.root.setOnClickListener {
                if (variantAttr == selectedVariantAttr) return@setOnClickListener
                interaction?.invoke(variantAttr)
                updateSelectedVariantAttr(variantAttr)
            }

            binding.tvVariant.text = variantAttr

            val style =
                if (variantAttr == selectedVariantAttr) R.style.TextTheme_HelveticaNeue_TS14_Bold
                else R.style.TextTheme_HelveticaNeue_TS14_Grey

            binding.tvVariant.setTextAppearance(style)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSizesViewHolder {
        return ProductSizesViewHolder(
            ItemProductVariantBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductSizesViewHolder, position: Int) {
        holder.bind(variants[position], position)
    }

    override fun getItemCount() = variants.size

    fun submitList(
        selectedVariantAttr: String, variants: List<String>,
    ) {
        this.selectedVariantAttr = selectedVariantAttr
        this.variants = variants
        notifyDataSetChanged()
    }

    fun updateSelectedVariantAttr(selectedVariantAttr: String) {
        this.selectedVariantAttr = selectedVariantAttr
        notifyDataSetChanged()
    }
}