package com.hexagram.febys.ui.screens.product.filters.priceRange

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentPriceRangeFilterBinding
import com.hexagram.febys.utils.goBack

class PriceRangeFilterFragment : BaseFragment() {

    private lateinit var binding: FragmentPriceRangeFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPriceRangeFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUiListener()
    }

    private fun initUiListener() {
        binding.ivBack.setOnClickListener { goBack() }
    }

}