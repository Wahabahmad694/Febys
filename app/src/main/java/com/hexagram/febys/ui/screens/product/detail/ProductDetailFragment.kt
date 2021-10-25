package com.hexagram.febys.ui.screens.product.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.SliderFragment
import com.hexagram.febys.databinding.FragmentProductDetailBinding
import com.hexagram.febys.databinding.ItemQuestionAnswersThreadBinding
import com.hexagram.febys.databinding.LayoutAdditionalProductBinding
import com.hexagram.febys.databinding.LayoutProductDescriptionBinding
import com.hexagram.febys.models.api.product.Description
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.QuestionAnswers
import com.hexagram.febys.models.api.product.Variant
import com.hexagram.febys.models.view.QuestionAnswersThread
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.cart.CartViewModel
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.product.additional.AdditionalProductAdapter
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : SliderFragment() {
    private lateinit var binding: FragmentProductDetailBinding
    private val productDetailViewModel: ProductDetailViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()
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

        productDetailViewModel.fetchRecommendProducts()
        productDetailViewModel.fetchSimilarProducts(args.productId)
    }

    private fun initUi() {
        closeVariantBottomSheet(variantFirstAttrBottomSheet)
        closeVariantBottomSheet(variantSecondAttrBottomSheet)

        binding.containerAskAboutProduct.isVisible = isUserLoggedIn

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
                toggleFavAndUpdateIcon()
            } else {
                gotoLogin()
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
            binding.product?.let {
                updateVariantByFirstAttr(selectedFirstAttr, it)
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
            closeBottomsSheetElseGoBack()
        }

        variantFirstAttrBottomSheet.onStateChange { state ->
            onBottomSheetStateChange(state)
        }

        variantSecondAttrBottomSheet.onStateChange { state ->
            onBottomSheetStateChange(state)
        }

        binding.bgDim.setOnClickListener {
            // do nothing, just add to avoid click on views that are behind of bg dim when bg dim is visible
        }

        binding.btnAddToCart.setOnClickListener {
            /*newChanges val product = binding.product
            val variantId = binding.variant?.id
            if (product != null && variantId != null) {
                cartViewModel.addToCart(product, variantId)
            }*/
        }

        binding.ivBack.setOnClickListener {
            goBack()
        }

        binding.seeMoreQAndA.setOnClickListener {
            /*newChanges val threads = binding.product?.questionAnswersThread
            if (!threads.isNullOrEmpty()) gotoQAThreads(threads.toTypedArray())*/
        }

        binding.btnAskAboutProduct.setOnClickListener {
            askQuestion()
        }
    }

    private fun updateVariantByFirstAttr(firstAttr: String, product: Product) {
        productDetailViewModel.selectedFirstAttr = firstAttr

        val secondAttrList =
            productDetailViewModel.getSecondAttrList(firstAttr, product)

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

    private fun toggleFavAndUpdateIcon() {
        val skuId = binding.variant?.skuId ?: return
        productDetailViewModel.toggleFav(skuId)
        updateFavIcon(skuId)
    }

    private fun onBottomSheetStateChange(state: Int) {
        val isClosed = state == BottomSheetBehavior.STATE_HIDDEN
        if (isClosed) {
            binding.bgDim.fadeVisibility(false, 200)
        }
    }

    private fun observersSetup() {
        productDetailViewModel.observeOldProductDetail.observe(viewLifecycleOwner) {
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

        productDetailViewModel.observeAskQuestion.observe(viewLifecycleOwner) {
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
                    addQuestionAnswersToLayout(it.data, 0)
                }
            }
        }

        productDetailViewModel.recommendProducts.observe(viewLifecycleOwner) {
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
                    addAdditionalProduct(getString(R.string.label_customer_recommend), it.data, 0)
                }
            }
        }

        productDetailViewModel.similarProducts.observe(viewLifecycleOwner) {
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
                    addAdditionalProduct(
                        getString(R.string.label_compare_with_similar_items), it.data
                    )
                }
            }
        }
    }

    private fun updateUi(product: Product) {
        binding.product = product

        val variant = productDetailViewModel.selectedVariant
            ?: product.variants.firstOrNull { it.skuId == args.skuId }
            ?: product.variants[0]

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

        updateProductDescription(product.descriptions)

        updateQuestionAnswersThread(product.questionAnswers)
    }

    private fun updateQuestionAnswersThread(listOfQuestionAnswers: MutableList<QuestionAnswers>) {
        if (listOfQuestionAnswers.isEmpty()) return

        addQuestionAnswersToLayout(listOfQuestionAnswers[0])
        if (listOfQuestionAnswers.size >= 2) {
            addQuestionAnswersToLayout(listOfQuestionAnswers[1])
        }
    }

    private fun addQuestionAnswersToLayout(questionAnswers: QuestionAnswers, position: Int = -1) {
        if (questionAnswers.chat.isEmpty()) return

        val parent = binding.containerQAndAThread
        val layoutQuestionAnswersThread = ItemQuestionAnswersThreadBinding
            .inflate(layoutInflater, parent, false)

        layoutQuestionAnswersThread.apply {
            val chat = questionAnswers.chat

            root.tag = questionAnswers._id

            val question = chat[0]
            this.question.text = chat[0].message
            edit.isVisible = isUserLoggedIn && user?.id.toString() == question.sender._id
            delete.isVisible = isUserLoggedIn && user?.id.toString() == question.sender._id
            reply.isVisible = isUserLoggedIn

            voteUp.text = questionAnswers.upVotes.size.toString()
            voteDown.text = questionAnswers.downVotes.size.toString()

            edit.setOnClickListener {
                // todo move to edit question screen
            }

            delete.setOnClickListener {
                // todo remove thread
            }

            reply.setOnClickListener {
                // todo move to reply screen
            }

            val answersAdapter = AnswersAdapter()
            rvAnswers.adapter = answersAdapter
            rvAnswers.isNestedScrollingEnabled = false
            rvAnswers.addItemDecoration(
                DividerItemDecoration(
                    context, (rvAnswers.layoutManager as LinearLayoutManager).orientation
                )
            )
            val answers = if (chat.size > 1) chat.subList(1, chat.size) else mutableListOf()
            answersAdapter.submitList(answers)
        }
        addView(parent, layoutQuestionAnswersThread.root, position)
    }

    private fun addAdditionalProduct(title: String, products: List<Product>, position: Int = -1) {
        if (products.isEmpty()) return

        val parent = binding.containerAdditionalProducts
        val layoutAdditionalProductBinding = LayoutAdditionalProductBinding
            .inflate(layoutInflater, parent, false)

        layoutAdditionalProductBinding.apply {
            additionalProductTitle.text = title
            val additionalAdapter = AdditionalProductAdapter()
            additionalAdapter.submitList(products)
            rvAdditionalProducts.apply {
                setHasFixedSize(true)
                isNestedScrollingEnabled = false
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
                layoutManager = GridLayoutManager(context, 2)
                adapter = additionalAdapter
            }
        }

        addView(parent, layoutAdditionalProductBinding.root, position)
    }

    private fun updateVariant(variant: Variant) {
        binding.variant = variant
        productDetailViewModel.selectedVariant = variant

        setupProductImagesSlider(variant.images)

        updateFavIcon(variant.skuId)

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
        binding.containerProductVariantFirstAttr.isVisible = false
        binding.containerProductVariantSecondAttr.isVisible = false
    }

    private fun updateProductDescription(description: Description) {
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

    private fun updateProductDescription(descriptions: List<Description>) {
        descriptions.forEach {
            updateProductDescription(it)
        }
    }

    private fun setupProductImagesSlider(images: List<String>) {
        binding.sliderProductImages.adapter = ProductSliderPageAdapter(images, this)
        binding.dotsIndicator.setViewPager2(binding.sliderProductImages)
    }

    private fun updateFavIcon(skuId: String) {
        val isFav = productDetailViewModel.isFavProduct(skuId)
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

    private fun closeBottomsSheetElseGoBack() {
        if (variantFirstAttrBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN
        ) {
            closeVariantBottomSheet(variantFirstAttrBottomSheet)
            return
        }

        if (variantSecondAttrBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN) {
            closeVariantBottomSheet(variantSecondAttrBottomSheet)
            return
        }

        goBack()
    }

    private fun askQuestion() {
        if (!isUserLoggedIn) {
            gotoLogin()
            return
        }
        val question = binding.etAskAboutProduct.text.toString()

        if (question.isEmpty()) return

        productDetailViewModel.askQuestion(args.productId, question)
        binding.etAskAboutProduct.text = null
    }

    private fun gotoQAThreads(threads: Array<QuestionAnswersThread>) {
        val userId = user?.id?.toString()
        val action = ProductDetailFragmentDirections
            .actionProductDetailFragmentToQAThreadsFragment(userId, threads)
        navigateTo(action)
    }

    private fun gotoLogin() {
        val gotoLogin = NavGraphDirections.actionToLoginFragment()
        navigateTo(gotoLogin)
    }

    override fun onStart() {
        super.onStart()
        updateFavIcon(binding.variant?.skuId ?: return)
    }

    private fun addView(parent: ViewGroup, view: View, position: Int = -1) {
        if (position == -1) parent.addView(view) else parent.addView(view, position)
    }

    override fun getSlider() = listOf(binding.sliderProductImages)

    override fun getRotateInterval() = 5000L

    override fun getTvCartCount(): TextView = binding.tvCartCount
    override fun getIvCart(): View = binding.ivCart

    private inner class ProductSliderPageAdapter(
        val images: List<String>, fa: Fragment
    ) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = images.size

        override fun createFragment(position: Int): Fragment =
            ProductSliderPageFragment.newInstance(images[position])
    }
}