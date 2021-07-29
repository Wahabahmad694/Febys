package com.hexagram.febys.ui.screens.product.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.SliderFragment
import com.hexagram.febys.databinding.FragmentProductDetailBinding
import com.hexagram.febys.databinding.LayoutProductDescriptionBinding
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.network.response.ProductDescription
import com.hexagram.febys.network.response.ProductVariant
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : SliderFragment() {
    private lateinit var binding: FragmentProductDetailBinding
    private val productDetailViewModel: ProductDetailViewModel by viewModels()
    private val args: ProductDetailFragmentArgs by navArgs()

    private val productSizesAdapter = ProductSizesAdapter()
    private val sizesBottomSheet get() = BottomSheetBehavior.from(binding.bottomSheetSizes.root)

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
        uiListeners()
        observersSetup()
    }

    private fun initUi() {
        closeSizesBottomSheet()

        productSizesAdapter.interaction = {
            updateVariant(it)
        }

        binding.bottomSheetSizes.rvProductSizes.apply {
            setHasFixedSize(true)
            adapter = productSizesAdapter
            addItemDecoration(
                DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)
            )
        }
    }

    private fun uiListeners() {
        binding.ivProductFav.setOnClickListener {
            if (isUserLoggedIn) {
                binding.variant?.let {
                    productDetailViewModel.toggleFav(it.id)
                    val isFav = productDetailViewModel.isFavProduct(it.id)
                    updateFavIcon(isFav)
                }
            } else {
                val gotoLogin = NavGraphDirections.actionToLoginFragment()
                navigateTo(gotoLogin)
            }
        }

        binding.containerProductSize.setOnClickListener {
            binding.variant?.let {
                showSizesBottomSheet()
            }
        }

        binding.bottomSheetSizes.btnClose.setOnClickListener {
            closeSizesBottomSheet()
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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (sizesBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN) {
                closeSizesBottomSheet()
                return@addCallback
            }

            goBack()
        }

        sizesBottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val isClosed = newState == BottomSheetBehavior.STATE_HIDDEN
                if (isClosed && binding.bgDim.isVisible) {
                    binding.bgDim.fadeVisibility(false, 200)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}  // do nothing
        })
    }

    private fun observersSetup() {
        productDetailViewModel.observeProductDetail.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    showLoader()
                }
                is DataState.Error -> {
                    hideLoader()
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    hideLoader()
                    updateUi(it.data)
                }
            }
        }
    }

    private fun updateUi(product: Product) {
        binding.product = product

        val variant = product.product_variants.first { it.isDefault }
        val variantPosition = product.product_variants.indexOf(variant)
        productSizesAdapter.updateSelectedVariant(variantPosition)

        productSizesAdapter.submitList(product.product_variants)

        val shortDescription = ProductDescription(
            0, product.shortDescriptionHTML, getString(R.string.label_description)
        )
        updateProductDescription(shortDescription)
        updateProductDescription(product.descriptions)

        updateVariant(variant)
    }

    private fun updateProductDescription(description: ProductDescription) {
        val productDescriptionsBinding = LayoutProductDescriptionBinding.inflate(
            layoutInflater, binding.containerProductDescriptions, false
        )

        productDescriptionsBinding.description = description

        productDescriptionsBinding.productDescriptionToggle.setOnClickListener {
            productDescriptionsBinding.containerProductDescription.toggleVisibility()
            productDescriptionsBinding.ivDescriptionArrow.updateArrowByVisibility(
                productDescriptionsBinding.containerProductDescription.isVisible
            )
            binding.scrollView.scrollToDescendant(productDescriptionsBinding.containerProductDescription)
        }

        binding.containerProductDescriptions.addView(productDescriptionsBinding.root)
    }

    private fun updateProductDescription(descriptions: List<ProductDescription>) {
        descriptions.forEach {
            updateProductDescription(it)
        }
    }

    private fun updateVariant(variant: ProductVariant) {
        binding.variant = variant
        setupProductImagesSlider(variant.images)

        val isFav = productDetailViewModel.isFavProduct(variant.id)
        updateFavIcon(isFav)

        val size = variant.variant_attributes.first { it.name.lowercase() == "size" }.value
        updateSize(size)
    }

    private fun setupProductImagesSlider(images: List<String>) {
        binding.sliderProductImages.adapter = ProductSliderPageAdapter(images, this)
        binding.dotsIndicator.setViewPager2(binding.sliderProductImages)
    }

    private fun updateFavIcon(isFav: Boolean) {
        binding.ivProductFav.setImageResource(if (isFav) R.drawable.ic_fav else R.drawable.ic_un_fav)
    }

    private fun updateSize(size: String) {
        binding.tvProductSize.text = getString(R.string.size, size)
    }

    private fun ImageView.updateArrowByVisibility(visibility: Boolean) {
        val arrow = if (visibility)
            R.drawable.ic_arrow_up
        else
            R.drawable.ic_arrow_down

        setImageResource(arrow)
    }

    private fun showSizesBottomSheet() {
        binding.bgDim.fadeVisibility(true)
        sizesBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun closeSizesBottomSheet() {
        binding.bgDim.fadeVisibility(false)
        sizesBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
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