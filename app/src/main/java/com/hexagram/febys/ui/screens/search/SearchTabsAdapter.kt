package com.hexagram.febys.ui.screens.search

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hexagram.febys.ui.screens.store.menu.StoreListFragment
import com.hexagram.febys.ui.screens.vendor.VendorListingFragment

class SearchTabsAdapter(fragment: Fragment, private val tabsList: List<String>) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = tabsList.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> {
                StoreListFragment.newInstance()
            }
            2 -> {
                VendorListingFragment.newInstance()
            }
            3 -> {
                VendorListingFragment.newInstance(true)
            }
            else -> {
                CategoryNameFragment.newInstance()
            }
        }
    }
}