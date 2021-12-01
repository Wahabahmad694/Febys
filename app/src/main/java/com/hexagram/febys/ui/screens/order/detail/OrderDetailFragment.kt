package com.hexagram.febys.ui.screens.order.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentOrderDetailBinding
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.order.OrderViewModel
import com.hexagram.febys.ui.screens.order.cancel.CancelOrderBottomSheet
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
        binding.rvVendorWithProducts.adapter = orderDetailVendorProductAdapter
        binding.rvVendorWithProducts.applySpaceItemDecoration(R.dimen._16sdp)
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener { goBack() }

        orderDetailVendorProductAdapter.onCancelOrderClick = { vendorId ->
            gotoCancelOrder(args.orderId, vendorId)
        }

        setFragmentResultListener(CancelOrderBottomSheet.REQ_KEY_IS_ORDER_CANCELED) { _, bundle ->
            val isOrderCanceled =
                bundle.getBoolean(CancelOrderBottomSheet.REQ_KEY_IS_ORDER_CANCELED)
            if (isOrderCanceled) orderViewModel.fetchOrder(args.orderId)
        }
    }

    private fun gotoCancelOrder(orderId: String, vendorId: String) {
        val gotoCancelOrder = OrderDetailFragmentDirections
            .actionOrderDetailFragmentToCancelOrderBottomSheet(orderId, vendorId)
        navigateTo(gotoCancelOrder)
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
    }

    private fun updateUi(order: Order) = with(binding) {
        tvOrderId.text = order.orderId
        tvOrderDate.text =
            Utils.DateTime.formatDate(order.createdAt, FORMAT_MONTH_DATE_YEAR_HOUR_MIN)

        orderDetailVendorProductAdapter.submitList(order.vendorProducts)
    }
}