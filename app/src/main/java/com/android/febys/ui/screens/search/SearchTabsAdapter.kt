package com.android.febys.ui.screens.search

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SearchTabsAdapter(fragment: Fragment, private val tabsList: List<String>) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = tabsList.size

    override fun createFragment(position: Int): Fragment {
        return SearchViewPagerFragment.newInstance(tabsList[position], position)
    }
}