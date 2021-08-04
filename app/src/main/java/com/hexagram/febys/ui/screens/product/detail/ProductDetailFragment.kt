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

    private val productVariantAdapter = ProductVariantAdapter()
    private val variantBottomSheet get() = BottomSheetBehavior.from(binding.bottomSheetVariant.root)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        productDetailViewModel.fetchProductDetail(args.productId)
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
        closeVariantBottomSheet()

        productVariantAdapter.interaction = {
            updateVariant(it)
            closeVariantBottomSheet()
        }

        binding.bottomSheetVariant.rvProductVariant.apply {
            setHasFixedSize(true)
            adapter = productVariantAdapter
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

        binding.containerProductVariant.setOnClickListener {
            binding.variant?.let {
                showVariantBottomSheet()
            }
        }

        binding.bottomSheetVariant.btnClose.setOnClickListener {
            closeVariantBottomSheet()
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

        binding.containerProductShippingInfo.productShippingFeeToggle.setOnClickListener {
            binding.containerProductShippingInfo.containerProductShippingFee.toggleVisibility()
            binding.containerProductShippingInfo.ivShippingFeeArrow.updateArrowByVisibility(
                binding.containerProductShippingInfo.containerProductShippingFee.isVisible
            )
            binding.scrollView.scrollToDescendant(binding.containerProductShippingInfo.containerProductShippingFee)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (variantBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN) {
                closeVariantBottomSheet()
                return@addCallback
            }

            goBack()
        }

        variantBottomSheet.onStateChange { state ->
            val isClosed = state == BottomSheetBehavior.STATE_HIDDEN
            if (isClosed && binding.bgDim.isVisible) {
                binding.bgDim.fadeVisibility(false, 200)
            }
        }

        binding.bgDim.setOnClickListener {
            // do nothing, just add to avoid click on views that are behind of bg dim when bg dim is visible
        }

        binding.ivBack.setOnClickListener {
            goBack()
        }
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

        val variant =
            product.product_variants.firstOrNull { it.id == args.variantId }
                ?: product.product_variants[0]
        val variantPosition = product.product_variants.indexOf(variant)
        productVariantAdapter.updateSelectedVariant(variantPosition)
        productVariantAdapter.submitList(product.product_variants)
        updateVariant(variant)

        val shortDescription = ProductDescription(
            0, product.descriptionHTML, getString(R.string.label_description)
        )
        updateProductDescription(shortDescription)
        updateProductDescription(product.descriptions)
    }

    private fun updateVariant(variant: ProductVariant) {
        binding.variant = variant
        setupProductImagesSlider(variant.images)

        val isFav = productDetailViewModel.isFavProduct(variant.id)
        updateFavIcon(isFav)

        val variantAttribute = variant.variant_attributes.first()
        updateVariantSelectedText(variantAttribute.name, variantAttribute.value)
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

    private fun setupProductImagesSlider(images: List<String>) {
        binding.sliderProductImages.adapter = ProductSliderPageAdapter(images, this)
        binding.dotsIndicator.setViewPager2(binding.sliderProductImages)
    }

    private fun updateFavIcon(isFav: Boolean) {
        binding.ivProductFav.setImageResource(if (isFav) R.drawable.ic_fav else R.drawable.ic_un_fav)
    }

    private fun updateVariantSelectedText(variantName: String, variantValue: String) {
        binding.tvProductVariant.text =
            getString(R.string.variant_selected, variantName, variantValue)
    }

    private fun ImageView.updateArrowByVisibility(visibility: Boolean) {
        val arrow = if (visibility)
            R.drawable.ic_arrow_up
        else
            R.drawable.ic_arrow_down

        setImageResource(arrow)
    }

    private fun showVariantBottomSheet() {
        binding.bgDim.fadeVisibility(true)
        variantBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun closeVariantBottomSheet() {
        binding.bgDim.fadeVisibility(false)
        variantBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
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