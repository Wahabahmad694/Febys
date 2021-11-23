package com.hexagram.febys.ui.screens.order.listing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemOrderListingBinding
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.utils.Utils

class OrderListingAdapter : RecyclerView.Adapter<OrderListingAdapter.ViewHolder>() {
    private var orders = listOf<Order>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemOrderListingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    fun submitList(orders: List<Order>) {
        this.orders = orders
        notifyItemRangeChanged(0, orders.size)
    }

    inner class ViewHolder(
        private val binding: ItemOrderListingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) = with(binding) {
            val order = orders[position]
            tvOrderId.text = order.orderId
            tvOrderDate.text = Utils.DateTime.formatDate(
                order.createdAt, Utils.DateTime.FORMAT_MONTH_DATE_YEAR_HOUR_MIN
            )
        }
    }
}