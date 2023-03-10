package com.hexagram.febys.ui.screens.search

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentSearchBinding
import com.hexagram.febys.utils.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment() {
    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListeners()
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

        // set bold text to by default selected tab
        binding.tabLayoutSearchFragment
            .getTabAt(0)?.view?.children?.forEach {
                if (it is TextView) {
                    it.setTypeface(null, Typeface.BOLD)
                }
            }

        // region bold text on selected tab
        binding.tabLayoutSearchFragment.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.view.children.forEach {
                    if (it is TextView) {
                        it.setTypeface(null, Typeface.BOLD)
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // do nothing
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.view.children.forEach {
                    if (it is TextView) {
                        it.setTypeface(null, Typeface.NORMAL)
                    }
                }
            }
        })
        // endregion bold text on selected tab
    }

    private fun uiListeners() {

        binding.llSearchContainerSearchFragment.setOnClickListener {
            val gotoSearchProduct =
                SearchFragmentDirections.actionSearchFragmentToSearchProductFragment()
            navigateTo(gotoSearchProduct)
        }
    }


    override fun getTvCartCount(): TextView = binding.tvCartCount
    override fun getIvCart(): View = binding.ivCart
}