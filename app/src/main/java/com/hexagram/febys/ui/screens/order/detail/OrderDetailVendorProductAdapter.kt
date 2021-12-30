package com.hexagram.febys.ui.screens.order.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.R
import com.hexagram.febys.databinding.ItemOrderDetailVendorForReviewBinding
import com.hexagram.febys.databinding.ItemOrderDetailVendorsBinding
import com.hexagram.febys.models.api.cart.VendorProducts
import com.hexagram.febys.utils.OrderStatus
import com.hexagram.febys.utils.applySpaceItemDecoration
import com.hexagram.febys.utils.load
import com.hexagram.febys.utils.setBackgroundRoundedColor

class OrderDetailVendorProductAdapter : RecyclerView.Adapter<IBindViewHolder>() {
    private var vendors = listOf<VendorProducts>()
    var onCancelOrderClick: ((vendorId: String) -> Unit)? = null
    var onAddReviewClick: ((vendorProducts: VendorProducts) -> Unit)? = null
    var onItemClick: ((vendorProducts: VendorProducts) -> Unit)? = null
    var cancelableOrder: Boolean = true
    private var review: Boolean = false

    inner class ReviewVH(
        private val binding: ItemOrderDetailVendorForReviewBinding
    ) : IBindViewHolder(binding.root) {
        override fun bind(position: Int) = with(binding) {
            val vendorProducts = vendors[position]
            val vendor = vendorProducts.vendor

            vendorName.text = vendor.shopName
            vendorType.text = vendor.role.name
            vendorImg.load(vendor.businessInfo.logo)

            val orderDetailProductAdapter = OrderDetailProductAdapter()
            rvOrderDetailProducts.adapter = orderDetailProductAdapter
            orderDetailProductAdapter.submitList(vendorProducts.products, review)
            orderDetailProductAdapter.onItemClick = {
                onItemClick?.invoke(vendorProducts)
            }

            root.setOnClickListener {
                onItemClick?.invoke(vendorProducts)
            }
        }
    }

    inner class VH(
        private val binding: ItemOrderDetailVendorsBinding
    ) : IBindViewHolder(binding.root) {
        override fun bind(position: Int) = with(binding) {
            val vendorProducts = vendors[position]
            val vendor = vendorProducts.vendor
            val reverted = vendorProducts.reverted == true

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
            containerOrderAmountByVendor.isVisible = vendorProducts.amount != null && !reverted

            orderTrackingCode.text = vendorProducts.courier?.trackingId
            containerOrderTrackingCode.isVisible = vendorProducts.courier != null && !reverted
                    && vendorProducts.status in arrayOf(OrderStatus.SHIPPED)

            orderDeliveryService.load(vendorProducts.courier?.service?.logo)
            containerOrderDeliveryService.isVisible = vendorProducts.courier != null && !reverted
                    && vendorProducts.status in arrayOf(OrderStatus.SHIPPED)

            orderCancelReason.text = vendorProducts.revertDetails?.reason
            containerOrderCancelReason.isVisible = vendorProducts.revertDetails != null && reverted

            btnCancelOrder.isVisible = cancelableOrder
                    && vendorProducts.status in arrayOf(OrderStatus.PENDING, OrderStatus.ACCEPTED)

            btnAddReview.isVisible =
                !vendorProducts.hasReviewed && vendorProducts.status in arrayOf(OrderStatus.DELIVERED)

            btnReturnItems.isVisible =
                vendorProducts.status in arrayOf(OrderStatus.DELIVERED) && !reverted

            btnCancelOrder.setOnClickListener { onCancelOrderClick?.invoke(vendor._id) }
            btnAddReview.setOnClickListener { onAddReviewClick?.invoke(vendorProducts) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IBindViewHolder {
        return if (review) {
            ReviewVH(
                ItemOrderDetailVendorForReviewBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        } else {
            VH(
                ItemOrderDetailVendorsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: IBindViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return vendors.size
    }

    fun submitList(list: List<VendorProducts>, review: Boolean = false) {
        this.review = review
        vendors = list
        notifyItemRangeChanged(0, vendors.size)
    }
}