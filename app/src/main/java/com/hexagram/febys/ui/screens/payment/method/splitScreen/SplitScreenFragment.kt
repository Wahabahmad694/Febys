package com.hexagram.febys.ui.screens.payment.method.splitScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentInsufficientBalanceBinding
import com.hexagram.febys.utils.goBack

class SplitScreenFragment : BaseFragment() {
    private lateinit var binding: FragmentInsufficientBalanceBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentInsufficientBalanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        binding.btnSplitPay.setOnClickListener { goBack() }
        binding.btnCancel.setOnClickListener { goBack() }
    }
}