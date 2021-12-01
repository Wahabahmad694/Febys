package com.hexagram.febys.ui.screens.order.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.R
import com.hexagram.febys.databinding.ItemOrderDetailVendorsBinding
import com.hexagram.febys.models.api.cart.VendorProducts
import com.hexagram.febys.utils.OrderStatus
import com.hexagram.febys.utils.applySpaceItemDecoration
import com.hexagram.febys.utils.load
import com.hexagram.febys.utils.setBackgroundRoundedColor

class OrderDetailVendorProductAdapter : RecyclerView.Adapter<OrderDetailVendorProductAdapter.VH>() {
    private var vendors = listOf<VendorProducts>()

    inner class VH(
        private val binding: ItemOrderDetailVendorsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val vendorProducts = vendors[position]
            val vendor = vendorProducts.vendor

            vendorName.text = vendor.shopName
            vendorType.text = vendor.role.name
            vendorImg.load(vendor.businessInfo.logo)

            val orderDetailProductAdapter = OrderDetailProductAdapter()
            rvOrderDetailProducts.applySpaceItemDecoration(R.dimen._16sdp)
            rvOrderDetailProducts.adapter = orderDetailProductAdapter
            orderDetailProductAdapter.submitList(vendorProducts.products)

            orderStatus.text = OrderStatus.getStatusForDisplay(vendorProducts.status)
            val color = OrderStatus.getStatusColor(vendorProducts.status)
            orderStatus.setBackgroundRoundedColor(color)
            containerOrderStatus.isVisible = vendorProducts.status != null

            orderAmountByVendor.text = vendorProducts.amount?.getFormattedPrice()
            containerOrderAmountByVendor.isVisible = vendorProducts.amount != null

            orderTrackingCode.text = vendorProducts.courier?.trackingId
            containerOrderTrackingCode.isVisible = vendorProducts.courier != null

            orderDeliveryService.load(vendorProducts.courier?.service?.logo)
            containerOrderDeliveryService.isVisible = vendorProducts.courier != null

            btnCancelOrder.isVisible =
                vendorProducts.status in arrayOf(OrderStatus.PENDING, OrderStatus.ACCEPTED)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ItemOrderDetailVendorsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return vendors.size
    }

    fun submitList(list: List<VendorProducts>) {
        vendors = list
        notifyItemRangeChanged(0, vendors.size)
    }
}