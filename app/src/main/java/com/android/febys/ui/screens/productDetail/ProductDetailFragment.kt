package com.android.febys.ui.screens.productDetail

import android.graphics.Color
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.android.febys.R
import com.android.febys.base.SliderFragment
import com.android.febys.databinding.FragmentProductDetailBinding
import com.android.febys.dto.ProductDetailDTO
import com.android.febys.network.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : SliderFragment() {
    private lateinit var binding: FragmentProductDetailBinding
    private val viewModel: ProductDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        observersSetup()

        viewModel.fetchProductDetail("abc")
    }

    private fun initUi() {
        binding.llContainerProductDescriptionToggle.setOnClickListener {
            binding.llContainerProductDescription.isVisible =
                !binding.llContainerProductDescription.isVisible

            val arrow = if (binding.llContainerProductDescription.isVisible)
                R.drawable.ic_arrow_up
            else
                R.drawable.ic_arrow_down

            binding.ivDescriptionArrow.setImageResource(arrow)
        }
    }


    private fun observersSetup() {
        viewModel.observeProductDetail.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {

                }
                is DataState.Data -> {
                    updateUi(it.data)
                }
            }
        }
    }

    private fun updateUi(productDetail: ProductDetailDTO) {
        binding.productDetail = productDetail
        setupProductImagesSlider(productDetail.images)
    }

    private fun setupProductImagesSlider(images: List<String>) {
        binding.sliderProductImages.adapter = ProductSliderPageAdapter(images, this)
        binding.dotsIndicator.setViewPager2(binding.sliderProductImages)
    }

    override fun getSlider() = binding.sliderProductImages

    override fun getRotateInterval() = 5000L

    private inner class ProductSliderPageAdapter(
        val images: List<String>, fa: Fragment
    ) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = images.size

        override fun createFragment(position: Int): Fragment =
            ProductSliderPageFragment.newInstance(images[position])
    }
}