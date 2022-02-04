package com.hexagram.febys.ui.screens.order.refund

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.R
import com.hexagram.febys.databinding.FragmentReturnOrderBinding
import com.hexagram.febys.ui.screens.order.OrderViewModel
import com.hexagram.febys.utils.applySpaceItemDecoration
import com.hexagram.febys.utils.goBack
import com.paypal.pyplcheckout.home.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReturnOrderFragment : BaseFragment() {
    private lateinit var binding: FragmentReturnOrderBinding
    private val orderViewModel: OrderViewModel by viewModels()
    private val args: ReturnOrderFragmentArgs by navArgs()
    private val returnOrderVendorProductAdapter = ReturnOrderVendorProductAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentReturnOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListener()
    }

    private fun initUi() {
        binding.rvVendorWithProducts.isNestedScrollingEnabled = false
        binding.rvVendorWithProducts.adapter = returnOrderVendorProductAdapter
        binding.rvVendorWithProducts.applySpaceItemDecoration(R.dimen._16sdp)
        returnOrderVendorProductAdapter.submitList(args.vendorProducts.toList())
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener { goBack() }
    }
}