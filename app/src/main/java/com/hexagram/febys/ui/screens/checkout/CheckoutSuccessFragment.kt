package com.hexagram.febys.ui.screens.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentCheckoutSuccessBinding
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.navigateTo

class CheckoutSuccessFragment : BaseFragment() {
    private lateinit var binding: FragmentCheckoutSuccessBinding
    private val args: CheckoutSuccessFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckoutSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListener()
    }

    private fun initUi() {
        binding.tvOrderId.text = args.orderId
    }

    private fun uiListener() {
        binding.ivClose.setOnClickListener {
            goBack()
        }

        binding.btnMyOrders.setOnClickListener {
            gotoOrderListing()
        }
    }

    private fun gotoOrderListing() {
        val gotoOrderListing =
            CheckoutSuccessFragmentDirections.actionCheckoutSuccessFragmentToOrderListingFragment()
        navigateTo(gotoOrderListing)
    }
}