package com.hexagram.febys.ui.screens.shippingType

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentShippingTypeBinding

class ShippingTypeFragment : BaseFragment() {

    private lateinit var binding: FragmentShippingTypeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentShippingTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}