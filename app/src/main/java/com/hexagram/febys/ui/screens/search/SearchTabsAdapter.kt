package com.hexagram.febys.ui.screens.search

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hexagram.febys.ui.screens.vendor.VendorListingFragment

class SearchTabsAdapter(fragment: Fragment, private val tabsList: List<String>) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = tabsList.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            2 -> {
                VendorListingFragment.newInstance()
            }
            else -> {
                CategoryNameFragment.newInstance()
            }
        }
    }
}