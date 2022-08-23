package com.hexagram.febys.ui.screens.shippingType

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentShippingTypeBinding
import com.hexagram.febys.utils.goBack

class ShippingMethodFragment : BaseFragment() {

    private lateinit var binding: FragmentShippingTypeBinding
    private val shippingMethodAdapter = ShippingMethodAdapter()

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
    }

    private fun initUi() {
        binding.rvShippingMethod.adapter = shippingMethodAdapter
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener {
            goBack()
        }
    }


}