package com.hexagram.febys.ui.screens.order.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemOrderDetailProductForReviewBinding
import com.hexagram.febys.databinding.ItemOrderDetailProductsBinding
import com.hexagram.febys.models.api.cart.CartProduct
import com.hexagram.febys.models.api.cart.ReturnDetails
import com.hexagram.febys.utils.OrderStatus
import com.hexagram.febys.utils.setBackgroundRoundedColor

class OrderDetailProductAdapter constructor(
    private val showSelectedBtn: Boolean = false,
    private val selectedSkuId: String? = null,
    private val returns: List<ReturnDetails>? = null
) : RecyclerView.Adapter<IBindViewHolder>() {
    private var products = listOf<CartProduct>()
    var onItemClick: (() -> Unit)? = null
    var onSelectClick: ((skuId: CartProduct) -> Unit)? = null
    private var review: Boolean = false

    inner class VH(
        private val binding: ItemOrderDetailProductsBinding
    ) : IBindViewHolder(binding.root) {
        override fun bind(position: Int) = with(binding) {
            val cartProduct = products[position]
            val skuId = cartProduct.product.variants.firstOrNull()?.skuId
            quantity = cartProduct.quantity
            product = cartProduct.product
            isSelected = skuId == selectedSkuId
            ivIsSelected.isVisible = showSelectedBtn && cartProduct.refundable
            if (showSelectedBtn && cartProduct.refundable) {
                root.setOnClickListener { onSelectClick?.invoke(cartProduct) }
            }

            val status = returns?.firstOrNull { it.items.firstOrNull()?.skuId == skuId }?.status
            tvStatus.text = OrderStatus.getStatusForDisplay(status)
            val color = OrderStatus.getStatusColor(status)
            tvStatus.setBackgroundRoundedColor(color)
            tvStatus.isVisible = !status.isNullOrEmpty()
            endViewWithMargin26.isVisible = !status.isNullOrEmpty()
            endViewWithMargin16.isVisible = status.isNullOrEmpty()
        }
    }

    inner class ReviewVH(
        private val binding: ItemOrderDetailProductForReviewBinding
    ) : IBindViewHolder(binding.root) {
        override fun bind(position: Int) = with(binding) {
            val cartProduct = products[position]
            product = cartProduct.product
            bottomLine.isVisible = position < products.size - 1
            root.setOnClickListener { onItemClick?.invoke() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IBindViewHolder {
        return if (review) {
            ReviewVH(
                ItemOrderDetailProductForReviewBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        } else {
            VH(
                ItemOrderDetailProductsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: IBindViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun submitList(list: List<CartProduct>, review: Boolean = false) {
        this.review = review
        products = list
        notifyItemRangeChanged(0, products.size)
    }
}