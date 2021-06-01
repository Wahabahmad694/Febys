package com.android.febys.ui.screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.android.febys.R
import com.android.febys.databinding.FragmentSearchBinding
import com.android.febys.ui.base.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment() {
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

        val tabsList = viewModel.fetchTabs()
        binding.viewPagerSearchFragment.adapter = SearchTabsAdapter(this, tabsList)

        TabLayoutMediator(
            binding.tabLayoutSearchFragment, binding.viewPagerSearchFragment
        ) { tab, position ->
            tab.text = tabsList[position]
        }.attach()
    }
}