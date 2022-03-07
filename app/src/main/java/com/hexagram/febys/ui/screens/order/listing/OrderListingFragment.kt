package com.hexagram.febys.ui.screens.order.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentOrderListingBinding
import com.hexagram.febys.ui.screens.order.OrderViewModel
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderListingFragment : BaseFragment() {
    private lateinit var binding: FragmentOrderListingBinding
    private val orderViewModel: OrderViewModel by viewModels()
    private val args: OrderListingFragmentArgs by navArgs()
    private val orderListingAdapter = OrderListingAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListener()
        setObservers()
    }

    private fun initUi() {
        setupOrderPagerAdapter()
        binding.labelOrders.text = args.title ?: getString(R.string.order_listing)
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener { goBack() }

        orderListingAdapter.onItemClick = {
            val review = args.status?.firstOrNull() == OrderStatus.REVIEWED
            val returnDetail = args.status?.firstOrNull() in arrayOf(
                OrderStatus.RETURNED, OrderStatus.PENDING_RETURN
            )
            val gotoOrderDetail = OrderListingFragmentDirections
                .actionOrderListingFragmentToOrderDetailFragment(
                    args.titleForDetail,
                    it.orderId,
                    review,
                    returnDetail
                )
            navigateTo(gotoOrderDetail)
        }
    }

    private fun setupOrderPagerAdapter() {
        binding.rvOrders.adapter = orderListingAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            orderListingAdapter.loadStateFlow.collectLatest {
                val state = it.refresh
                if (state is LoadState.Loading) {
                    showLoader()
                } else {
                    hideLoader()
                }

                if (state is LoadState.Error) {
                    showErrorToast(state)
                }
                binding.emptyView.root.isVisible =
                    it.refresh is LoadState.NotLoading && orderListingAdapter.itemCount < 1
            }
        }
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            orderViewModel.fetchOrderList(args.status).collectLatest {
                orderListingAdapter.submitData(it)
            }
        }
    }
}