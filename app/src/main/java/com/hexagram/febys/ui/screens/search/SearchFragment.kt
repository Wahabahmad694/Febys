package com.hexagram.febys.ui.screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.hexagram.febys.R
import com.hexagram.febys.databinding.FragmentSearchBinding
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

        initUi()
    }

    private fun initUi() {
        initMenus()
    }

    private fun initMenus() {
        val menuList = resources.getStringArray(R.array.search_menus).toList()
        binding.viewPagerSearchFragment.adapter = SearchTabsAdapter(this, menuList)

        TabLayoutMediator(
            binding.tabLayoutSearchFragment, binding.viewPagerSearchFragment
        ) { tab, position ->
            tab.text = menuList[position]
        }.attach()
    }
}