package com.hexagram.febys.ui.screens.imagePreview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentImagePreviewBinding
import com.hexagram.febys.utils.goBack

class ImagePreviewFragment : BaseFragment() {
    private lateinit var binding: FragmentImagePreviewBinding
    private val args: ImagePreviewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentImagePreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiListeners()

    }

    private fun uiListeners() {
        binding.ivClose.setOnClickListener { goBack() }
        val images = args.image
        val imageList = ArrayList<String>()

        images.forEach {
            imageList.add(it)
        }
        binding.ivProductImage.adapter = ZoomProductSliderPageAdapter(images = imageList, this)
        binding.dotsIndicator.setViewPager2(binding.ivProductImage)
    }

    private inner class ZoomProductSliderPageAdapter(
        val images: List<String>, fa: Fragment,
    ) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = images.size

        override fun createFragment(position: Int): Fragment =
            ZoomableProductFragment.newInstance(images[position], images)
    }
}