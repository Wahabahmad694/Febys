package com.hexagram.febys.ui.screens.order.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemOrderDetailProductsBinding
import com.hexagram.febys.models.api.cart.CartProduct

class OrderDetailProductAdapter : RecyclerView.Adapter<OrderDetailProductAdapter.VH>() {
    private var products = listOf<CartProduct>()

    inner class VH(
        private val binding: ItemOrderDetailProductsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val cartProduct = products[position]
            quantity = cartProduct.quantity
            product = cartProduct.product
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ItemOrderDetailProductsBinding.inflate(
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