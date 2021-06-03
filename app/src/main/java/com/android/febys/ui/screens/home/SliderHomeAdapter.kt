package com.android.febys.ui.screens.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.febys.R
import com.android.febys.databinding.ItemSliderHomeBinding
import com.android.febys.network.response.Banner
import com.asksira.loopingviewpager.LoopingPagerAdapter


class SliderHomeAdapter(
    itemList: List<Banner>,
    isInfinite: Boolean
) : LoopingPagerAdapter<Banner>(itemList, isInfinite) {

    override fun inflateView(
        viewType: Int,
        container: ViewGroup,
        listPosition: Int
    ): View {
        return LayoutInflater.from(container.context)
            .inflate(R.layout.item_slider_home, container, false)
    }

    override fun bindView(
        convertView: View, listPosition: Int, viewType: Int
    ) {
        val binding = ItemSliderHomeBinding.bind(convertView)
        binding.imageUrl = getItem(listPosition)?.images?.get(0) ?: return
        binding.executePendingBindings()
    }
}