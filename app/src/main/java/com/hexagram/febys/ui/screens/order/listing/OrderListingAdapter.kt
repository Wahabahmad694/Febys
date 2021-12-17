package com.hexagram.febys.ui.screens.order.listing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemOrderListingBinding
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.utils.Utils

class OrderListingAdapter :
    PagingDataAdapter<Order, OrderListingAdapter.ViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Order>() {

            override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem.orderId == newItem.orderId
            }
        }
    }

    var onItemClick: ((order: Order) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemOrderListingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }


    inner class ViewHolder(
        private val binding: ItemOrderListingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) = with(binding) {
            val order = getItem(position)!!
            tvOrderId.text = order.orderId
            tvOrderDate.text = Utils.DateTime.formatDate(
                order.createdAt, Utils.DateTime.FORMAT_MONTH_DATE_YEAR_HOUR_MIN
            )

            root.setOnClickListener {
                onItemClick?.invoke((order))
            }
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Product)
        fun removeFav(skuId: String)
    }
}