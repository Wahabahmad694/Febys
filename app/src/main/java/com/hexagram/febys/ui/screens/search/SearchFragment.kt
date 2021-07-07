package com.hexagram.febys.ui.screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hexagram.febys.databinding.FragmentSearchBinding
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.showToast
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : com.hexagram.febys.base.BaseFragment() {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObserver()
        viewModel.fetchTabs()
    }

    private fun setObserver() {
        viewModel.observeTabs.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    updateTabs(it.data)
                }
            }
        }
    }

    private fun updateTabs(tabsList: List<String>) {
        binding.viewPagerSearchFragment.adapter = SearchTabsAdapter(this, tabsList)

        TabLayoutMediator(
            binding.tabLayoutSearchFragment, binding.viewPagerSearchFragment
        ) { tab, position ->
            tab.text = tabsList[position]
        }.attach()
    }
}