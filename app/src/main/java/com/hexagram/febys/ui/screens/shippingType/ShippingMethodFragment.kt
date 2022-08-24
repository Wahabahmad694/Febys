package com.hexagram.febys.ui.screens.shippingType

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentShippingTypeBinding
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.utils.goBack

class ShippingMethodFragment : BaseFragment() {

    private lateinit var binding: FragmentShippingTypeBinding
    private val shippingMethodAdapter = ShippingMethodAdapter()

    private val args: ShippingMethodFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentShippingTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListener()
        updateUi(args.estimates)
    }

    private fun updateUi(estimates: Order) {
        val estimate = estimates.swooveEstimates.responses.estimates
        binding.emptyView.root.isVisible = estimate.isEmpty()
        shippingMethodAdapter.submitList(estimate)
    }

    private fun initUi() {
        binding.rvShippingMethod.adapter = shippingMethodAdapter
        shippingMethodAdapter.onItemClick = {
            markAsSelected(binding.rvShippingMethod.rootView)
        }
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener {
            goBack()
        }
        binding.btnSaveMethod.setOnClickListener {
            goBack()
        }
    }

    fun markAsNotSelected(backgroundView: View) {
        backgroundView.background =
            ContextCompat.getDrawable(backgroundView.context, R.drawable.bg_border_dark_grey)
    }

    fun markAsSelected(backgroundView: View) {
        backgroundView.background =
            ContextCompat.getDrawable(backgroundView.context, R.drawable.bg_border_dark_red)
    }

}