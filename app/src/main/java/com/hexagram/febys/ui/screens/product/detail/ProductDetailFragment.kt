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

    private val productVariantFirstAttrAdapter = ProductVariantAdapter()
    private val productVariantSecondAttrAdapter = ProductVariantAdapter()
    private val variantFirstAttrBottomSheet get() = BottomSheetBehavior.from(binding.bottomSheetVariantFirstAttr.root)
    private val variantSecondAttrBottomSheet get() = BottomSheetBehavior.from(binding.bottomSheetVariantSecondAttr.root)

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
        closeVariantBottomSheet(variantFirstAttrBottomSheet)
        closeVariantBottomSheet(variantSecondAttrBottomSheet)

        binding.bottomSheetVariantFirstAttr.rvProductVariant.apply {
            setHasFixedSize(true)
            adapter = productVariantFirstAttrAdapter
            addItemDecoration(
                DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)
            )
        }

        binding.bottomSheetVariantSecondAttr.rvProductVariant.apply {
            setHasFixedSize(true)
            adapter = productVariantSecondAttrAdapter
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

        binding.containerProductVariantFirstAttr.setOnClickListener {
            binding.variant?.let {
                showVariantBottomSheet(variantFirstAttrBottomSheet)
            }
        }

        binding.containerProductVariantSecondAttr.setOnClickListener {
            binding.variant?.let {
                showVariantBottomSheet(variantSecondAttrBottomSheet)
            }
        }

        binding.bottomSheetVariantFirstAttr.btnClose.setOnClickListener {
            closeVariantBottomSheet(variantFirstAttrBottomSheet)
        }

        binding.bottomSheetVariantSecondAttr.btnClose.setOnClickListener {
            closeVariantBottomSheet(variantSecondAttrBottomSheet)
        }

        productVariantFirstAttrAdapter.interaction = { selectedFirstAttr ->
            binding.product?.let { product ->
                productDetailViewModel.selectedFirstAttr = selectedFirstAttr

                val secondAttrList =
                    productDetailViewModel.getSecondAttrList(selectedFirstAttr, product)

                val variant = if (secondAttrList.isEmpty()) {
                    productDetailViewModel.getVariantByFirstAttr(product)
                } else {
                    productVariantSecondAttrAdapter
                        .submitList(secondAttrList.first(), secondAttrList)

                    productDetailViewModel.selectedSecondAttr = secondAttrList.first()
                    productDetailViewModel.getVariantBySecondAttr(product)
                }

                variant?.let { updateVariant(it) }
            }
            closeVariantBottomSheet(variantFirstAttrBottomSheet)
        }

        productVariantSecondAttrAdapter.interaction = { selectedSecondAttr ->
            binding.product?.let { product ->
                productDetailViewModel.selectedSecondAttr = selectedSecondAttr
                val variant = productDetailViewModel.getVariantBySecondAttr(product)
                variant?.let { updateVariant(it) }
            }
            closeVariantBottomSheet(variantSecondAttrBottomSheet)
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
            if (variantFirstAttrBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN) {
                closeVariantBottomSheet(variantFirstAttrBottomSheet)
                return@addCallback
            }

            if (variantSecondAttrBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN) {
                closeVariantBottomSheet(variantSecondAttrBottomSheet)
                return@addCallback
            }

            goBack()
        }

        variantFirstAttrBottomSheet.onStateChange { state ->
            val isClosed = state == BottomSheetBehavior.STATE_HIDDEN
            if (isClosed && binding.bgDim.isVisible) {
                binding.bgDim.fadeVisibility(false, 200)
            }
        }

        variantSecondAttrBottomSheet.onStateChange { state ->
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

        variant.getFirstVariantAttr()?.value?.let { selectedFirstAttr ->
            productDetailViewModel.selectedFirstAttr = selectedFirstAttr

            val firstAttrList = productDetailViewModel.getFirstAttrList(product)

            productVariantFirstAttrAdapter
                .submitList(selectedFirstAttr, firstAttrList)

            variant.getSecondVariantAttr()?.value?.let { selectedSecondAttr ->
                productDetailViewModel.selectedSecondAttr = selectedSecondAttr

                val secondAttrList =
                    productDetailViewModel.getSecondAttrList(selectedFirstAttr, product)

                productVariantSecondAttrAdapter.submitList(
                    selectedSecondAttr, secondAttrList
                )
            }

        }
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

        val firstVariantAttr = variant.getFirstVariantAttr()
        firstVariantAttr?.let {
            updateVariantFirstAttrSelectedText(it.name, it.value)
        }

        if (firstVariantAttr == null) updateOnlyOneVariant()

        val secondVariantAttr = variant.getSecondVariantAttr()
        secondVariantAttr?.let {
            updateVariantSecondAttrSelectedText(it.name, it.value)
        }

        binding.containerProductVariantSecondAttr.isVisible = secondVariantAttr != null
    }

    private fun updateOnlyOneVariant() {
        binding.tvProductVariantFirstAttr.text = getString(R.string.only_one_variant)
        binding.containerProductVariantFirstAttr.setOnClickListener(null)
        binding.containerProductVariantSecondAttr.isVisible = false
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

        addView(binding.containerProductDescriptions, productDescriptionsBinding.root)
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

    private fun updateVariantFirstAttrSelectedText(variantName: String, variantValue: String) {
        binding.firstAttrName = variantName
        binding.tvProductVariantFirstAttr.text =
            getString(R.string.variant_selected, variantName, variantValue)
    }

    private fun updateVariantSecondAttrSelectedText(variantName: String, variantValue: String) {
        binding.secondAttrName = variantName
        binding.tvProductVariantSecondAttr.text =
            getString(R.string.variant_selected, variantName, variantValue)
    }

    private fun ImageView.updateArrowByVisibility(visibility: Boolean) {
        val arrow = if (visibility)
            R.drawable.ic_arrow_up
        else
            R.drawable.ic_arrow_down

        setImageResource(arrow)
    }

    private fun showVariantBottomSheet(bottomSheet: BottomSheetBehavior<View>) {
        binding.bgDim.fadeVisibility(true)
        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun closeVariantBottomSheet(bottomSheet: BottomSheetBehavior<View>) {
        binding.bgDim.fadeVisibility(false)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun addView(parent: ViewGroup, view: View) = parent.addView(view)

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