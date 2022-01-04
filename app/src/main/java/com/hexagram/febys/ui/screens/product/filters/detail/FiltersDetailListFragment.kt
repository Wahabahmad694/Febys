package com.hexagram.febys.ui.screens.product.filters.detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentFiltersDetailListBinding
import com.hexagram.febys.utils.goBack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FiltersDetailListFragment : BaseFragment() {
    private lateinit var binding: FragmentFiltersDetailListBinding
    private val filterDetailListAdapter = FilterDetailListAdapter()

    private val args: FiltersDetailListFragmentArgs by navArgs()

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
        binding.tvFilterName.text = args.filter.name

        val appliedFilter = args.appliedFilters.variantAttrs[args.filter.name] ?: ""
        val filters = args.filter.values.sortedBy { it }
        filterDetailListAdapter.submitList(filters, appliedFilter)


        val isClearBtnVisible = appliedFilter.isNotEmpty()
        if (isClearBtnVisible) enableClearOption() else disableClearOption()
    }

    private fun initUiListener() {
        binding.ivBack.setOnClickListener { goBack() }

        filterDetailListAdapter.filterClickCallback = {
            args.appliedFilters.variantAttrs[args.filter.name] = it
            enableClearOption()
        }

        binding.labelClear.setOnClickListener {
            disableClearOption()
        }
    }

    private fun disableClearOption() {
        binding.labelClear.setTextColor(Color.GRAY)
        binding.labelClear.isEnabled = false
        filterDetailListAdapter.updateSelectedFilter("")
        args.appliedFilters.variantAttrs.remove(args.filter.name)
    }

    private fun enableClearOption() {
        binding.labelClear.setTextColor(Color.BLACK)
        binding.labelClear.isEnabled = true
    }
}