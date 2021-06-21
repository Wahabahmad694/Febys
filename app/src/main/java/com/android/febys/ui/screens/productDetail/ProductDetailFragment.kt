package com.android.febys.ui.screens.productDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.febys.R
import com.android.febys.base.SliderFragment
import com.android.febys.databinding.FragmentProductDetailBinding
import com.android.febys.dto.ProductDetail
import com.android.febys.network.DataState
import com.android.febys.utils.toggleVisibility
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
        binding.productDescriptionToggle.setOnClickListener {
            binding.containerProductDescription.toggleVisibility()
            binding.ivDescriptionArrow.updateArrowByVisibility(binding.containerProductDescription.isVisible)
            binding.scrollView.scrollToDescendant(binding.containerProductDescription)
        }

        binding.productManufactureToggle.setOnClickListener {
            binding.containerProductManufacture.toggleVisibility()
            binding.ivManufactureArrow.updateArrowByVisibility(binding.containerProductManufacture.isVisible)
            binding.scrollView.scrollToDescendant(binding.containerProductManufacture)
        }

        binding.productReviewsToggle.setOnClickListener {
            binding.containerProductReviews.toggleVisibility()
            binding.ivReviewsArrow.updateArrowByVisibility(binding.containerProductReviews.isVisible)
            binding.scrollView.scrollToDescendant(binding.containerProductReviews)
        }

        binding.productQNdAToggle.setOnClickListener {
            binding.containerProductQNdA.toggleVisibility()
            binding.ivQAndAArrow.updateArrowByVisibility(binding.containerProductQNdA.isVisible)
            binding.scrollView.scrollToDescendant(binding.containerProductQNdA)
        }

        binding.productShippingFeeToggle.setOnClickListener {
            binding.containerProductShippingFee.toggleVisibility()
            binding.ivShippingFeeArrow.updateArrowByVisibility(binding.containerProductShippingFee.isVisible)
            binding.scrollView.scrollToDescendant(binding.containerProductShippingFee)
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

    private fun updateUi(productDetail: ProductDetail) {
        binding.productDetail = productDetail
        setupProductImagesSlider(productDetail.images)
    }

    private fun setupProductImagesSlider(images: List<String>) {
        binding.sliderProductImages.adapter = ProductSliderPageAdapter(images, this)
        binding.dotsIndicator.setViewPager2(binding.sliderProductImages)
    }

    private fun ImageView.updateArrowByVisibility(visibility: Boolean) {
        val arrow = if (visibility)
            R.drawable.ic_arrow_up
        else
            R.drawable.ic_arrow_down

        setImageResource(arrow)
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