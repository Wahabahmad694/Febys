package com.hexagram.febys.ui.screens.order.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemAddEditProductReviewBinding
import com.hexagram.febys.models.api.cart.CartProduct

class AddEditProductReviewAdapter : RecyclerView.Adapter<AddEditProductReviewAdapter.VH>() {
    private var products = listOf<CartProduct>()

    var ratingCallback: ((skuId: String, rating: Int) -> Unit)? = null
    var commentCallback: ((skuId: String, comment: String) -> Unit)? = null

    inner class VH(
        private val binding: ItemAddEditProductReviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val cartProduct = products[position]
            val variant = cartProduct.product.variants[0]

            product = cartProduct.product
            productRating.ratingBar.progress = cartProduct.ratingAndReview?.score?.toInt() ?: 5
            etComment.setText(cartProduct.ratingAndReview?.review?.comment)

            productRating.ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
                if (fromUser) ratingCallback?.invoke(variant.skuId, rating.toInt())
            }

            etComment.doAfterTextChanged {
                commentCallback?.invoke(variant.skuId, it.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ItemAddEditProductReviewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun submitList(list: List<CartProduct>) {
        products = list
        notifyItemRangeChanged(0, products.size)
    }
}