package com.hexagram.febys.ui.screens.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentMenuBinding
import com.hexagram.febys.utils.navigateTo

class MenuFragment : BaseFragment() {
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
            val gotofebysPlus =
                MenuFragmentDirections.actionMenuFragmentToFebysPlusFragment()
            navigateTo(gotofebysPlus)
        }
        binding.labelPawnShop.setOnClickListener {
            val goToPawnShop = NavGraphDirections.toWebViewFragment("", "https://www.youtube.com/")
            navigateTo(goToPawnShop)
        }
        binding.labelDiscountMall.setOnClickListener {
            val goToDiscountMall =
                NavGraphDirections.toWebViewFragment("", "https://mail.google.com/")
            navigateTo(goToDiscountMall)
        }
        binding.labelAfricanMarket.setOnClickListener {
            val goToAfricanMarket =
                NavGraphDirections.toWebViewFragment("", "https://www.google.com/")
            navigateTo(goToAfricanMarket)
        }
        binding.labelMadeInGhana.setOnClickListener {
            val goToMadeInGhana =
                NavGraphDirections.toWebViewFragment("", "https://www.google.com/")
            navigateTo(goToMadeInGhana)
        }
    }
}