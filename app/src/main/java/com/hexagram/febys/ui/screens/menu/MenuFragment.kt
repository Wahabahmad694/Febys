package com.hexagram.febys.ui.screens.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentMenuBinding
import com.hexagram.febys.utils.navigateTo
import com.hexagram.febys.utils.showInfoDialoge

class MenuFragment : BaseFragment() {

    object SpecialMarketFilters {
        const val THRIFT_MARKET = "Thrift Market"
        const val MADE_IN_GHANA = "Made in Ghana"
        const val AFRICAN_MARKET = "African Market"
        const val DISCOUNT_MALL = "Discount Mall"
    }

    private lateinit var binding: FragmentMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUiListener()
    }

    private fun initUiListener() {
        binding.labelFebysPlus.setOnClickListener {
            val gotoFebysPlus =
                MenuFragmentDirections.actionMenuFragmentToFebysPlusFragment()
            navigateTo(gotoFebysPlus)
        }
        binding.labelThriftMarket.setOnClickListener {
            val gotoThrift =
                MenuFragmentDirections.actionMenuFragmentToSpecialProductListing(
                    getString(R.string.label_thrift_market),
                    SpecialMarketFilters.THRIFT_MARKET
                )
            navigateTo(gotoThrift)
        }
        binding.labelDiscountMall.setOnClickListener {
            val gotoDiscountMall =
                MenuFragmentDirections.actionMenuFragmentToSpecialProductListing(
                    getString(R.string.label_discount_mall),
                    SpecialMarketFilters.DISCOUNT_MALL
                )
            navigateTo(gotoDiscountMall)
        }
        binding.labelMadeInGhana.setOnClickListener {
            val gotoMadeInGhana =
                MenuFragmentDirections.actionMenuFragmentToSpecialProductListing(
                    getString(R.string.label_made_in_ghana),
                    SpecialMarketFilters.MADE_IN_GHANA
                )
            navigateTo(gotoMadeInGhana)
        }
        binding.labelAfricanMarket.setOnClickListener {
            val gotoAfrican =
                MenuFragmentDirections.actionMenuFragmentToSpecialProductListing(
                    getString(R.string.label_african_market),
                    SpecialMarketFilters.AFRICAN_MARKET
                )
            navigateTo(gotoAfrican)
        }
        binding.labelPawnShop.setOnClickListener { showDialog() }
    }

    private fun showDialog() {
        val resId = R.drawable.ic_coming_soon
        val title = getString(R.string.label_coming_soon)
        val msg = getString(R.string.msg_for_pawn_shop)

        showInfoDialoge(resId, title, msg) {
            //todo withdraw amount
        }
    }
}