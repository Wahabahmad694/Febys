package com.hexagram.febys.ui.screens.product.filters.detailsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentFiltersDetailListBinding
import com.hexagram.febys.models.api.filters.Filters
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.product.filters.SearchFilterViewModel
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.hideLoader
import com.hexagram.febys.utils.showLoader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FiltersDetailListFragment : BaseFragment() {
    private lateinit var binding: FragmentFiltersDetailListBinding
    private val filterDetailListAdapter = FilterDetailListAdapter()

    private val searchViewModel by viewModels<SearchFilterViewModel>()

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
        setObserver()
    }

    private fun initUi() {
        binding.rvFiltersDetailList.adapter = filterDetailListAdapter
        binding.tvFilterName.text = args.filter
    }

    private fun initUiListener() {
        binding.ivBack.setOnClickListener { goBack() }

        filterDetailListAdapter.filterClickCallback = {

        }

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
        val listOfNames = filters.attributes.getAttributeValuesByFilter(args.filter)
        filterDetailListAdapter.submitList(listOfNames)
    }
}