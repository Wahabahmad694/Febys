package com.hexagram.febys.ui.screens.suggestionSearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemSearchSuggestionBinding
import com.hexagram.febys.models.api.suggestedSearch.SuggestedProduct
import com.hexagram.febys.utils.load
import com.hexagram.febys.utils.show

class SearchSuggestionAdapter(

    private val onProductClickListener: (SuggestedProduct) -> Unit,
) :
    PagingDataAdapter<SuggestedProduct, SearchSuggestionAdapter.SuggestedSearchProduct>(
        OfferDiffUtils
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SuggestedSearchProduct {
        val binding = ItemSearchSuggestionBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return SuggestedSearchProduct(binding)
    }

    override fun onBindViewHolder(holder: SuggestedSearchProduct, position: Int) {
        val item = getItem(position)!!

        holder.bindItems(item)
        holder.setOnClickEvents(item)
    }

    inner class SuggestedSearchProduct(private val binding: ItemSearchSuggestionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItems(product: SuggestedProduct) {
            binding.apply {

                ivProduct.load(product.productImage)
                tvProductName.text = product.productName
                tvCategoryName.text = product.categoryName
                tvStoreName.text = product.vendorName
                if (product.hasPromotion) {
                    tvProductOffPrice.text = product.originalPrice.getFormattedPrice()
                    binding.tvProductOffPrice.show()
                    binding.tvProductOffPriceSeparator.show()
                }
                tvProductPrice.text = product.price.getFormattedPrice()

            }

        }

        fun setOnClickEvents(product: SuggestedProduct) {
            binding.apply {
                root.setOnClickListener {
                    onProductClickListener(product)
                }
            }
        }
    }

    companion object {
        private object OfferDiffUtils : DiffUtil.ItemCallback<SuggestedProduct>() {
            override fun areItemsTheSame(
                oldItem: SuggestedProduct,
                newItem: SuggestedProduct
            ): Boolean {
                // Id is unique.
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: SuggestedProduct,
                newItem: SuggestedProduct
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}