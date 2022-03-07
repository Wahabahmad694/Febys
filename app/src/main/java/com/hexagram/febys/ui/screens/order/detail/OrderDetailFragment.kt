package com.hexagram.febys.ui.screens.order.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentOrderDetailBinding
import com.hexagram.febys.models.api.cart.VendorProducts
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.order.OrderViewModel
import com.hexagram.febys.ui.screens.order.cancel.CancelOrderBottomSheet
import com.hexagram.febys.ui.screens.order.refund.ReturnOrderFragment
import com.hexagram.febys.ui.screens.order.review.AddEditReviewFragment
import com.hexagram.febys.utils.*
import com.hexagram.febys.utils.Utils.DateTime.FORMAT_MONTH_DATE_YEAR_HOUR_MIN
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailFragment : BaseFragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private val orderViewModel: OrderViewModel by viewModels()
    private val args: OrderDetailFragmentArgs by navArgs()
    private val orderDetailVendorProductAdapter = OrderDetailVendorProductAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderViewModel.fetchOrder(args.orderId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListener()
        setObserver()
    }

    private fun initUi() {
        binding.labelOrders.text = args.title ?: getString(R.string.label_order_detail)
        binding.rvVendorWithProducts.isNestedScrollingEnabled = false
        binding.rvVendorWithProducts.adapter = orderDetailVendorProductAdapter
        binding.rvVendorWithProducts.applySpaceItemDecoration(R.dimen._16sdp)
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener { goBack() }

        orderDetailVendorProductAdapter.gotoVendorDetail =
            { vendorId -> gotoVendorDetail(vendorId, false) }

        orderDetailVendorProductAdapter.onReturnItemClick = { vendorProduct: VendorProducts ->
            gotoReturnOrder(vendorProduct)
        }
        orderDetailVendorProductAdapter.onCancelOrderClick = { vendorId ->
            gotoCancelOrder(args.orderId, vendorId)
        }
        orderDetailVendorProductAdapter.onAddReviewClick = { vendorProducts ->
            gotoAddReview(vendorProducts)
        }

        orderDetailVendorProductAdapter.onItemClick = { vendorProducts ->
            gotoAddReview(vendorProducts)
        }

        setFragmentResultListener(CancelOrderBottomSheet.REQ_KEY_IS_ORDER_CANCELED) { _, bundle ->
            val isOrderCanceled =
                bundle.getBoolean(CancelOrderBottomSheet.REQ_KEY_IS_ORDER_CANCELED)
            if (isOrderCanceled) orderViewModel.fetchOrder(args.orderId)
        }

        setFragmentResultListener(ReturnOrderFragment.REQ_KEY_IS_ORDER_RETURN) { _, bundle ->
            val isOrderReturned =
                bundle.getBoolean(ReturnOrderFragment.REQ_KEY_IS_ORDER_RETURN)
            if (isOrderReturned) orderViewModel.fetchOrder(args.orderId)
        }

        setFragmentResultListener(AddEditReviewFragment.REQ_KEY_REFRESH) { _, bundle ->
            val refresh =
                bundle.getBoolean(AddEditReviewFragment.REQ_KEY_REFRESH, false)

            if (refresh) {
                orderViewModel.fetchOrder(args.orderId)
            }
        }
    }

    private fun gotoReturnOrder(vendorProduct: VendorProducts) {
        val actionToReturnFragment = OrderDetailFragmentDirections
            .actionOrderDetailFragmentToReturnOrderFragment(args.orderId, arrayOf(vendorProduct))
        navigateTo(actionToReturnFragment)
    }

    private fun gotoCancelOrder(orderId: String, vendorId: String) {
        val gotoCancelOrder = OrderDetailFragmentDirections
            .actionOrderDetailFragmentToCancelOrderBottomSheet(orderId, vendorId)
        navigateTo(gotoCancelOrder)
    }

    private fun gotoAddReview(vendorProducts: VendorProducts) {
        val actionOrderDetailFragmentToAddEditReviewFragment = OrderDetailFragmentDirections
            .actionOrderDetailFragmentToAddEditReviewFragment(args.orderId, vendorProducts)
        navigateTo(actionOrderDetailFragmentToAddEditReviewFragment)
    }

    private fun setObserver() {
        orderViewModel.observeOrder.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    showLoader()
                }
                is DataState.Error -> {
                    hideLoader()
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    hideLoader()
                    updateUi(it.data)
                }
            }
        }

        orderViewModel.observeTimer.observe(viewLifecycleOwner) {
            val showTimer = it.isNullOrEmpty().not()
            orderDetailVendorProductAdapter.updateCancelable(showTimer)
            binding.containerTimer.root.isVisible = showTimer
            binding.containerTimer.tvTime.text = it
        }
    }

    private fun updateUi(order: Order) = with(binding) {
        tvOrderId.text = order.orderId
        tvOrderDate.text =
            Utils.DateTime.formatDate(order.createdAt, FORMAT_MONTH_DATE_YEAR_HOUR_MIN)

        val vendorProducts = if (args.review) order.vendorProducts.filter { it.hasReviewed }
        else order.vendorProducts
        orderDetailVendorProductAdapter.submitList(vendorProducts, args.review)

        binding.containerOrderSummary.root.isVisible = !(args.review || args.returnDetail)
        order.addToOrderSummary(binding.containerOrderSummary)
    }

    private fun gotoVendorDetail(vendorId: String, isFollow: Boolean) {
        val direction = NavGraphDirections.toVendorDetailFragment(vendorId, isFollow)
        navigateTo(direction)
    }

}