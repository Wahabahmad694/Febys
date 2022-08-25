package com.hexagram.febys.ui.screens.shippingType

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentShippingTypeBinding
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.utils.goBack

class ShippingMethodFragment : BaseFragment() {

    companion object {
        const val ESTIMATE = "ESTIMATE"
    }

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
        estimate.forEach {
            if (it.estimateTypeDetails.name == estimates.swooveEstimates.responses.optimalEstimate.estimateTypeDetails.name) {
                it.selected = true
            }
        }
        binding.emptyView.root.isVisible = estimate.isEmpty()
        shippingMethodAdapter.submitList(estimate.toMutableList())
    }

    private fun initUi() {
        binding.rvShippingMethod.adapter = shippingMethodAdapter

    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener {
            goBack()
        }
        binding.btnSaveMethod.setOnClickListener {
            val estimate = shippingMethodAdapter.selectedItem()
            setFragmentResult(ESTIMATE, bundleOf("ESTIMATE" to estimate))
            goBack()
        }
    }


}