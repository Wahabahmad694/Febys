package com.hexagram.febys.ui.screens.product.filters.priceRange

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentPriceRangeFilterBinding
import com.hexagram.febys.utils.goBack

class PriceRangeFilterFragment : BaseFragment() {
    private lateinit var binding: FragmentPriceRangeFilterBinding
    private val args: PriceRangeFilterFragmentArgs by navArgs()

    private var min = 0
    private var max = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPriceRangeFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        uiListener()
    }

    private fun initUi() {
        min = args.filters.minPrice ?: -1
        max = args.filters.maxPrice ?: -1

        if (args.filters.minPrice != null) {
            binding.etMinPrice.setText("${args.filters.minPrice}")
        }

        if (args.filters.maxPrice != null) {
            binding.etMaxPrice.setText("${args.filters.maxPrice}")
        }

        val enableClearOption = max != -1 || max != -1
        if (enableClearOption) enableClearOption() else disableClearOption()
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener { goBack() }

        binding.labelClear.setOnClickListener {
            disableClearOption()
        }

        binding.etMinPrice.doAfterTextChanged {
            if (!it.isNullOrEmpty()) {
                min = it.toString().toInt()
                enableClearOption()
            } else {
                min = -1
            }
        }

        binding.etMaxPrice.doAfterTextChanged {
            if (!it.isNullOrEmpty()) {
                max = it.toString().toInt()
                enableClearOption()
            } else {
                max = -1
            }
        }

        binding.btnShow.setOnClickListener {
            args.filters.minPrice = if (min == -1) null else min
            args.filters.maxPrice = if (max == -1) null else max
            goBack()
        }
    }

    private fun disableClearOption() {
        binding.labelClear.setTextColor(Color.GRAY)
        binding.labelClear.isEnabled = false
        binding.etMinPrice.setText("")
        binding.etMaxPrice.setText("")
        min = -1
        max = -1
    }

    private fun enableClearOption() {
        binding.labelClear.setTextColor(Color.BLACK)
        binding.labelClear.isEnabled = true
    }
}