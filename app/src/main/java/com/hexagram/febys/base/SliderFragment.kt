package com.hexagram.febys.base

import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

abstract class SliderFragment : BaseFragment() {
    private val timer = Timer()

    abstract fun getSlider(): List<ViewPager2>
    abstract fun getRotateInterval(): Long

    override fun onResume() {
        super.onResume()

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                lifecycleScope.launch(Dispatchers.Main) {
                    getSlider().forEach { slider ->
                        val totalIndex = slider.adapter?.itemCount?.minus(1) ?: -1
                        val currentPageIndex = slider.currentItem

                        if (totalIndex == 0) return@launch

                        if (currentPageIndex == totalIndex) {
                            slider.currentItem = 0
                        } else {
                            slider.currentItem = currentPageIndex + 1
                        }
                    }
                }
            }
        }, getRotateInterval(), getRotateInterval())
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

}