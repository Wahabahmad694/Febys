package com.hexagram.febys.ui.screens.order.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemOrderDetailProductForReviewBinding
import com.hexagram.febys.databinding.ItemOrderDetailProductsBinding
import com.hexagram.febys.models.api.cart.CartProduct

class OrderDetailProductAdapter : RecyclerView.Adapter<IBindViewHolder>() {
    private var products = listOf<CartProduct>()
    var onItemClick: (() -> Unit)? = null
    private var review: Boolean = false

    inner class VH(
        private val binding: ItemOrderDetailProductsBinding
    ) : IBindViewHolder(binding.root) {
        override fun bind(position: Int) = with(binding) {
            val cartProduct = products[position]
            quantity = cartProduct.quantity
            product = cartProduct.product
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