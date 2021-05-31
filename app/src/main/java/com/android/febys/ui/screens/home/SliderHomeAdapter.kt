package com.android.febys.ui.screens.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.febys.R
import com.android.febys.databinding.ItemSliderHomeBinding
import com.smarteist.autoimageslider.SliderViewAdapter


class SliderHomeAdapter : SliderViewAdapter<SliderHomeAdapter.VH>() {
    private var sliderItems = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup) = VH(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_slider_home, parent, false
        )
    )

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        viewHolder.bind(sliderItems[position])
    }

    override fun getCount() = sliderItems.size

    fun submitList(list: List<String>) {
        sliderItems = list.toMutableList()
        notifyDataSetChanged()
    }

    inner class VH(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        private val binding = ItemSliderHomeBinding.bind(itemView)

        fun bind(url: String) {
            binding.imageUrl = url
        }
    }
}