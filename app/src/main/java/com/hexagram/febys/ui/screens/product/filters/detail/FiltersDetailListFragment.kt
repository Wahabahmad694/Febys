package com.hexagram.febys.ui.screens.product.filters.detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentFiltersDetailListBinding
import com.hexagram.febys.models.api.product.Attr
import com.hexagram.febys.models.api.request.ProductListingRequest
import com.hexagram.febys.utils.goBack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FiltersDetailListFragment : BaseFragment() {
    companion object {
        const val KEY_APPLY_SUB_FILTER = "keyApplySubFilter"
    }

    private lateinit var binding: FragmentFiltersDetailListBinding
    private val filterDetailListAdapter = FilterDetailListAdapter()

    private val args: FiltersDetailListFragmentArgs by navArgs()

    private lateinit var currentAttr: Attr
    private lateinit var appliedFilters: ProductListingRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentAttr = args.filter.deepCopy()
        appliedFilters = args.appliedFilters.deepCopy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFiltersDetailListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initUiListener()
    }

    private fun initUi() {
        binding.rvFiltersDetailList.adapter = filterDetailListAdapter
        binding.tvFilterName.text = currentAttr.name

        val selectedFilter = appliedFilters.variantAttrs
        val filters = currentAttr.values.sortedBy { it }
        filterDetailListAdapter.submitList(filters, selectedFilter)


        val isClearBtnVisible = selectedFilter.isNotEmpty()
        if (isClearBtnVisible) enableClearOption() else disableClearOption()
    }

    private fun initUiListener() {
        binding.ivBack.setOnClickListener { goBack() }

        filterDetailListAdapter.filterClickCallback = {
            enableClearOption()
        }

        binding.labelClear.setOnClickListener {
            disableClearOption()
        }

        binding.btnApply.setOnClickListener {
            setFragmentResult(KEY_APPLY_SUB_FILTER, bundleOf(KEY_APPLY_SUB_FILTER to appliedFilters))
            goBack()
        }
    }

    private fun disableClearOption() {
        binding.labelClear.setTextColor(Color.GRAY)
        binding.labelClear.isEnabled = false
        filterDetailListAdapter.clearSelectedFilter(currentAttr.values)
        appliedFilters.variantAttrs.remove(currentAttr.name)
    }

    private fun enableClearOption() {
        binding.labelClear.setTextColor(Color.BLACK)
        binding.labelClear.isEnabled = true
    }
}