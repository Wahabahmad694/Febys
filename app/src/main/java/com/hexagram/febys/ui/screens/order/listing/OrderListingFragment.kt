package com.hexagram.febys.ui.screens.order.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentOrderListingBinding
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.order.OrderViewModel
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.hideLoader
import com.hexagram.febys.utils.navigateTo
import com.hexagram.febys.utils.showLoader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderListingFragment : BaseFragment() {
    private lateinit var binding: FragmentOrderListingBinding
    private val orderViewModel: OrderViewModel by viewModels()
    private val args: OrderListingFragmentArgs by navArgs()
    private val orderListingAdapter = OrderListingAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchOrders()
    }

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
        binding.rvOrders.adapter = orderListingAdapter
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener { goBack() }

        orderListingAdapter.onItemClick = {
            val gotoOrderDetail =
                OrderListingFragmentDirections.actionOrderListingFragmentToOrderDetailFragment(it.orderId)
            navigateTo(gotoOrderDetail)
        }
    }

    private fun setObservers() {
        orderViewModel.observeOrders.observe(viewLifecycleOwner) {
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

    private fun updateUi(orders: List<Order>) {
        orderListingAdapter.submitList(orders)
    }

    private fun fetchOrders() {
        orderViewModel.fetchOrders(args.status)
    }
}