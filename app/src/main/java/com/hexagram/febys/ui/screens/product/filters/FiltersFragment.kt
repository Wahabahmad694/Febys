package com.hexagram.febys.ui.screens.product.filters

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentFiltersBinding
import com.hexagram.febys.models.api.filters.Filters
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.hideLoader
import com.hexagram.febys.utils.navigateTo
import com.hexagram.febys.utils.showLoader

class FiltersFragment : BaseFragment() {
    private lateinit var binding: FragmentFiltersBinding
    private val searchViewModel by viewModels<SearchFilterViewModel>()

    private val filterAdapter = SearchFilterAdapter()

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
        setObserver()
    }

    private fun initUi() {
        binding.rvFilters.adapter = filterAdapter
    }

    private fun initUiListener() {
        binding.ivClose.setOnClickListener { goBack() }
        binding.labelClear.setOnClickListener {
            disableClearOption()
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
                .actionSearchFilterFragmentToFiltersDetailListFragment(it)
            navigateTo(gotoFilterDetailList)
        }
    }

    private fun disableClearOption() {
        binding.radioBtnFilter.clearCheck()
        binding.labelClear.setTextColor(Color.GRAY)
    }

    private fun enableClearOption() {
        binding.labelClear.setTextColor(Color.BLACK)
        binding.labelClear.isEnabled = true
    }

    private fun setObserver() {
        searchViewModel.observeFilters.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    showLoader()
                }
                is DataState.Error -> {
                    hideLoader()
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    hideLoader()
                    updateUi(it.data)
                }
            }
        }
    }

    private fun updateUi(filters: Filters) {
        val listOfNames = filters.attributes.getAttributeName()
        filterAdapter.submitList(listOfNames)
    }
}