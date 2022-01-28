package com.hexagram.febys.ui.screens.product.filters

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentFiltersBinding
import com.hexagram.febys.models.api.filters.Filters
import com.hexagram.febys.models.api.request.ProductListingRequest
import com.hexagram.febys.ui.screens.product.filters.detail.FiltersDetailListFragment
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.navigateTo

class FiltersFragment : BaseFragment() {
    companion object {
        const val KEY_APPLY_FILTER = "keyApplyFilter"
    }

    private lateinit var binding: FragmentFiltersBinding
    private val args by navArgs<FiltersFragmentArgs>()

    private val filterAdapter = FiltersAdapter()
    private lateinit var appliedFilters: ProductListingRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appliedFilters = args.appliedFilters.deepCopy()
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

        val isClearBtnVisible =
            appliedFilters.variantAttrs.isNotEmpty()
                    || appliedFilters.vendorIds.isNotEmpty()
                    || appliedFilters.categoryIds.isNotEmpty()
                    || appliedFilters.sortByPrice != null
                    || (appliedFilters.minPrice != null && appliedFilters.minPrice != -1)
                    || (appliedFilters.maxPrice != null && appliedFilters.maxPrice != -1)
        if (isClearBtnVisible) enableClearOption() else disableClearOption()
        updateUi(args.filters)
    }

    private fun initUiListener() {
        binding.ivClose.setOnClickListener { goBack() }

        binding.itemPriceRange.root.setOnClickListener {
            val gotoPriceFilter =
                FiltersFragmentDirections.actionSearchFilterFragmentToPriceRangeFilterFragment(
                    appliedFilters
                )
            navigateTo(gotoPriceFilter)
        }
        binding.labelClear.setOnClickListener {
            disableClearOption()
            appliedFilters.variantAttrs.clear()
        }

        binding.btnShow.setOnClickListener {
            setFragmentResult(KEY_APPLY_FILTER, bundleOf(KEY_APPLY_FILTER to appliedFilters))
            goBack()
        }

        binding.radioBtnFilter.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rd_newItem -> {
                    enableClearOption()
                    appliedFilters.sortByPrice = null
                }
                R.id.rd_lowPrice -> {
                    enableClearOption()
                    appliedFilters.sortByPrice = ProductListingRequest.KEY_ASC
                }
                R.id.rd_highPrice -> {
                    enableClearOption()
                    appliedFilters.sortByPrice = ProductListingRequest.KEY_DESC
                }
            }
        }

        filterAdapter.onItemClick = {
            val gotoFilterDetailList = FiltersFragmentDirections
                .actionSearchFilterFragmentToFiltersDetailListFragment(it, appliedFilters)
            navigateTo(gotoFilterDetailList)
        }

        setFragmentResultListener(FiltersDetailListFragment.KEY_APPLY_SUB_FILTER) { _, bundle ->
            val filters =
                bundle.getParcelable<ProductListingRequest>(FiltersDetailListFragment.KEY_APPLY_SUB_FILTER)

            if (filters != null) {
                appliedFilters = filters
                initUi()
            }
        }
    }

    private fun disableClearOption() {
        binding.radioBtnFilter.clearCheck()
        binding.labelClear.setTextColor(Color.GRAY)
        appliedFilters = ProductListingRequest()
    }

    private fun enableClearOption() {
        binding.labelClear.setTextColor(Color.BLACK)
        binding.labelClear.isEnabled = true
    }

    private fun updateUi(filters: Filters?) {
        val filterList = filters?.attributes?.attributes?.sortedBy { it.name }
        if (filterList != null) {
            filterAdapter.submitList(filterList)
        }

        if (this.appliedFilters.sortByPrice == ProductListingRequest.KEY_ASC) {
            binding.radioBtnFilter.check(R.id.rd_lowPrice)
        }

        if (this.appliedFilters.sortByPrice == ProductListingRequest.KEY_DESC) {
            binding.radioBtnFilter.check(R.id.rd_highPrice)
        }
    }
}