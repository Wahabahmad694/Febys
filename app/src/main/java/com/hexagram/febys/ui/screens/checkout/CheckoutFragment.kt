package com.hexagram.febys.ui.screens.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentCartBinding
import com.hexagram.febys.databinding.FragmentCheckoutBinding
import com.hexagram.febys.utils.goBack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckoutFragment : BaseFragment() {
    private lateinit var binding: FragmentCheckoutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiListener()
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener { goBack() }
    }
}