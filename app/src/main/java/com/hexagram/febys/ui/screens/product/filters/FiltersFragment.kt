package com.hexagram.febys.ui.screens.product.filters

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentFiltersBinding
import com.hexagram.febys.models.api.filters.Filters
import com.hexagram.febys.models.api.request.ProductListingRequest
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.navigateTo

class FiltersFragment : BaseFragment() {
    companion object {
        const val KEY_APPLY_FILTER = "keyApplyFilter"
    }

    private lateinit var binding: FragmentFiltersBinding
    private val args by navArgs<FiltersFragmentArgs>()

    private val filterAdapter = FiltersAdapter()
    private lateinit var filters: ProductListingRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filters = args.appliedFilters.deepCopy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initUiListener()
    }

    private fun initUi() {
        binding.rvFilters.adapter = filterAdapter
        val isClearBtnVisible = filters.variantAttrs.isNotEmpty()
        if (isClearBtnVisible) enableClearOption() else disableClearOption()
        updateUi(args.filters)
    }

    private fun initUiListener() {
        binding.ivClose.setOnClickListener { goBack() }

        binding.labelClear.setOnClickListener {
            disableClearOption()
            filters.variantAttrs.clear()
        }

        binding.btnShow.setOnClickListener {
            setFragmentResult(KEY_APPLY_FILTER, bundleOf(KEY_APPLY_FILTER to filters))
            goBack()
        }

        binding.radioBtnFilter.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rd_newItem -> {
                    enableClearOption()
                }
                R.id.rd_lowPrice -> {
                    enableClearOption()
                }
                R.id.rd_highPrice -> {
                    enableClearOption()
                }
            }
        }

        filterAdapter.onItemClick = {
            val gotoFilterDetailList = FiltersFragmentDirections
                .actionSearchFilterFragmentToFiltersDetailListFragment(it, filters)
            navigateTo(gotoFilterDetailList)
        }
    }

    private fun disableClearOption() {
        binding.radioBtnFilter.clearCheck()
        binding.labelClear.setTextColor(Color.GRAY)
        filters.variantAttrs.clear()
    }

    private fun enableClearOption() {
        binding.labelClear.setTextColor(Color.BLACK)
        binding.labelClear.isEnabled = true
    }

    private fun updateUi(filters: Filters) {
        val filterList = filters.attributes.attributes.sortedBy { it.name }
        filterAdapter.submitList(filterList)
    }
}