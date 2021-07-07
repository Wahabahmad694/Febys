package com.hexagram.febys.ui.screens.productDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hexagram.febys.R
import com.hexagram.febys.base.SliderFragment
import com.hexagram.febys.databinding.FragmentProductDetailBinding
import com.hexagram.febys.databinding.LayoutProductDetailDescriptionHtmlBinding
import com.hexagram.febys.databinding.LayoutProductDetailDescriptionTitleBinding
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.network.response.ProductDescription
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.toggleVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : com.hexagram.febys.base.SliderFragment() {
    private lateinit var binding: FragmentProductDetailBinding
    private val productDetailViewModel: ProductDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        productDetailViewModel.fetchProductDetail(19)
    }

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
        productDetailViewModel.observeProductDetail.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    updateUi(it.data)
                }
            }
        }
    }

    private fun updateUi(product: Product) {
        binding.product = product
        val images = mutableListOf<String>()
        product.product_variants.forEach {
            images.addAll(it.images)
        }
        setupProductImagesSlider(images)

        setupProductDescription(product.descriptions)
    }

    private fun setupProductImagesSlider(images: List<String>) {
        binding.sliderProductImages.adapter = ProductSliderPageAdapter(images, this)
        binding.dotsIndicator.setViewPager2(binding.sliderProductImages)
    }

    private fun setupProductDescription(descriptions: List<ProductDescription>) {
        descriptions.forEach {
            val descriptionTitleBinding = LayoutProductDetailDescriptionTitleBinding.inflate(
                layoutInflater, binding.containerProductDescription, false
            )
            descriptionTitleBinding.title = it.attribute

            val descriptionContentBinding = LayoutProductDetailDescriptionHtmlBinding.inflate(
                layoutInflater, binding.containerProductDescription, false
            )
            descriptionContentBinding.html = it.contentHTML

            binding.containerProductDescription.addView(descriptionTitleBinding.root)
            binding.containerProductDescription.addView(descriptionContentBinding.root)
        }
    }

    private fun ImageView.updateArrowByVisibility(visibility: Boolean) {
        val arrow = if (visibility)
            R.drawable.ic_arrow_up
        else
            R.drawable.ic_arrow_down

        setImageResource(arrow)
    }

    override fun getSlider() = listOf(binding.sliderProductImages)

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