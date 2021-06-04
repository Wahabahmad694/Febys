package com.android.febys.base

import androidx.viewpager2.widget.ViewPager2
import java.util.*

abstract class SliderFragment : BaseFragment() {
    private val timer = Timer()

    abstract fun getSlider(): ViewPager2
    abstract fun getRotateInterval(): Long

    override fun onResume() {
        super.onResume()

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val slider = getSlider()
                val totalIndex = slider.adapter?.itemCount?.minus(1) ?: -1
                val currentPageIndex = slider.currentItem

                if (totalIndex == 0) return

                if (currentPageIndex == totalIndex) {
                    slider.currentItem = 0
                } else {
                    slider.currentItem = currentPageIndex + 1
                }
            }
        }, getRotateInterval(), getRotateInterval())
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

}