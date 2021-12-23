package com.hexagram.febys.ui.screens.product.filters.detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentFiltersDetailListBinding
import com.hexagram.febys.ui.screens.product.filters.FilterViewModel
import com.hexagram.febys.utils.goBack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FiltersDetailListFragment : BaseFragment() {
    private lateinit var binding: FragmentFiltersDetailListBinding
    private val filterDetailListAdapter = FilterDetailListAdapter()

    private val searchViewModel by viewModels<FilterViewModel>()

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
        binding.labelClear.setOnClickListener {
            disableClearOption()
        }

        filterDetailListAdapter.submitList(args.filter.values)
    }

    private fun disableClearOption() {
        binding.labelClear.setTextColor(Color.GRAY)
        binding.labelClear.isEnabled = false
    }

    private fun initUiListener() {
        binding.ivBack.setOnClickListener { goBack() }

        filterDetailListAdapter.filterClickCallback = {

        }
    }
}