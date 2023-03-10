package com.hexagram.febys.ui.screens.product.detail

import android.animation.ValueAnimator
import android.app.DownloadManager
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.SliderFragment
import com.hexagram.febys.databinding.*
import com.hexagram.febys.models.api.product.*
import com.hexagram.febys.models.api.rating.Rating
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.cart.CartViewModel
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.product.additional.AdditionalProductAdapter
import com.hexagram.febys.ui.screens.product.filters.FiltersType
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
    private val askQuestionBottomSheet get() = BottomSheetBehavior.from(binding.bottomSheetAskQuestion.root)
    private val replyQuestionBottomSheet get() = BottomSheetBehavior.from(binding.bottomSheetReplyQuestion.root)

    private var reviewsSorting = ReviewsSorting.RECENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        productDetailViewModel.fetchProductDetail(args.productId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListeners()
        observersSetup()

        productDetailViewModel.fetchRecommendProducts(args.productId)
        productDetailViewModel.fetchSimilarProducts(args.productId)
    }

    private fun initUi() {
        closeBottomSheet(variantFirstAttrBottomSheet)
        closeBottomSheet(variantSecondAttrBottomSheet)
        closeBottomSheet(askQuestionBottomSheet)
        closeBottomSheet(replyQuestionBottomSheet)

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
            handleFavClick()
        }
        binding.bgDim.setOnClickListener {
            closeBottomsSheetElseGoBack()
        }

        binding.containerProductVariantFirstAttr.setOnClickListener {
            binding.variant?.let {
                showBottomSheet(variantFirstAttrBottomSheet)
            }
        }
        binding.containerVendorDetail.setOnClickListener {
            val vendorId = binding.product?.vendor?._id ?: return@setOnClickListener
            val gotoVendorDetail = NavGraphDirections.toVendorDetailFragment(vendorId, false)
            navigateTo(gotoVendorDetail)
        }
        binding.btnPayNow.setOnClickListener {
            if (isUserLoggedIn) {
                handleAddToCartClick()
                val gotoCheckout =
                    ProductDetailFragmentDirections.actionProductDetailFragmentToCheckoutFragment()
                navigateTo(gotoCheckout)
            } else {
                gotoLogin()
            }

        }

        binding.containerProductVariantSecondAttr.setOnClickListener {
            binding.variant?.let {
                showBottomSheet(variantSecondAttrBottomSheet)
            }
        }

        binding.bottomSheetVariantFirstAttr.btnClose.setOnClickListener {
            closeBottomSheet(variantFirstAttrBottomSheet)
        }

        binding.bottomSheetVariantSecondAttr.btnClose.setOnClickListener {
            closeBottomSheet(variantSecondAttrBottomSheet)
        }

        binding.bottomSheetAskQuestion.btnClose.setOnClickListener {
            closeBottomSheet(askQuestionBottomSheet)
        }

        binding.bottomSheetReplyQuestion.btnClose.setOnClickListener {
            closeBottomSheet(replyQuestionBottomSheet)
        }

        productVariantFirstAttrAdapter.interaction = { selectedFirstAttr ->
            binding.product?.let {
                updateVariantByFirstAttr(selectedFirstAttr, it)
            }

            closeBottomSheet(variantFirstAttrBottomSheet)
        }

        binding.containerProductShippingInfo.returnChip.setOnClickListener {
            val resId = R.drawable.ic_pdf
            val title = getString(R.string.label_delete_warning)
            val msg = getString(R.string.msg_for_download_pdf)

            showWarningDialog(resId, title, msg) {
                downloadPolicy()
            }
        }

        productVariantSecondAttrAdapter.interaction = { selectedSecondAttr ->
            binding.product?.let { product ->
                productDetailViewModel.selectedSecondAttr = selectedSecondAttr
                val variant = productDetailViewModel.getVariantBySecondAttr(product)
                variant?.let { updateVariant(it) }
            }
            closeBottomSheet(variantSecondAttrBottomSheet)
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

        askQuestionBottomSheet.onStateChange { state ->
            onBottomSheetStateChange(state)
        }

        replyQuestionBottomSheet.onStateChange { state ->
            onBottomSheetStateChange(state)
        }

        binding.bgDim.setOnClickListener {
            // do nothing, just add to avoid click on views that are behind of bg dim when bg dim is visible
        }

        binding.btnAddToCart.setOnClickListener {
            showAnimation()
            showSnackBar()
            handleAddToCartClick()
        }

        binding.ivBack.setOnClickListener {
            goBack()
        }

        binding.seeMoreQAndA.setOnClickListener {
            val threads =
                (productDetailViewModel.observeProductDetail.value as? DataState.Data)
                    ?.data?.qaThreads
                    ?: mutableListOf()

            gotoQAThreads(threads.toTypedArray())
        }
        binding.seeMoreRatingAndReviews.setOnClickListener {
            val product =
                (productDetailViewModel.observeProductDetail.value as? DataState.Data)
                    ?.data ?: return@setOnClickListener

            val rating = product.stats.rating
            val scores = product.scores
            val ratingsAndReviews = product.ratingsAndReviews

            val gotoRatingsAndReviews = ProductDetailFragmentDirections
                .actionProductDetailFragmentToRatingAndReviewsFragment(
                    consumer?.id?.toString(),
                    reviewsSorting,
                    rating,
                    scores.toTypedArray(),
                    ratingsAndReviews.toTypedArray()
                )

            navigateTo(gotoRatingsAndReviews)
        }

        binding.containerRatingAndReviews.reviews.radioGroupSorting.setOnCheckedChangeListener { _, checkedId ->
            handleReviewsSortingOrderCheckedChange(checkedId)
        }

        binding.bottomSheetReplyQuestion.btnPostAnswer.setOnClickListener {
            handlePostAnswerClick()
        }

        binding.containerAskAboutProduct.setOnClickListener {
            handleAskAboutProductClick()
        }

        binding.bottomSheetAskQuestion.btnAskQuestion.setOnClickListener {
            handleAskQuestionClick()
        }

        setFragmentResultListener(QAThreadsFragment.REQ_KEY_QA_THREAD_UPDATE) { _, bundle ->
            val updatedQAThreads = bundle
                .getParcelableArrayList<QAThread>(QAThreadsFragment.REQ_KEY_QA_THREAD_UPDATE)
                ?.toMutableList()
                ?: return@setFragmentResultListener

            updateQuestionAnswersThread(updatedQAThreads.toMutableList())
            productDetailViewModel.updateQAThreads(updatedQAThreads)
        }

        setFragmentResultListener(RatingAndReviewsFragment.REQ_KEY_RATING_AND_REVIEWS_UPDATE) { _, bundle ->
            val updatedReviews = bundle
                .getParcelableArrayList<RatingAndReviews>(RatingAndReviewsFragment.REQ_KEY_RATING_AND_REVIEWS_UPDATE)
                ?.toMutableList()
                ?: return@setFragmentResultListener

            updateReviews(updatedReviews)
            productDetailViewModel.updateReviews(updatedReviews)
        }

        setFragmentResultListener(RatingAndReviewsFragment.REQ_KEY_REVIEW_SORTING_UPDATE) { _, bundle ->
            val sorting = bundle
                .getSerializable(RatingAndReviewsFragment.REQ_KEY_REVIEW_SORTING_UPDATE) as? ReviewsSorting
                ?: return@setFragmentResultListener

            val ratingsAndReviews =
                binding.product?.ratingsAndReviews ?: return@setFragmentResultListener

            reviewsSorting = sorting
            val checkedId = getCheckedIdOfReviewSorting()
            binding.containerRatingAndReviews.reviews.radioGroupSorting.check(checkedId)
            updateReviews(ratingsAndReviews)
        }
    }

    private fun showAnimation() {
        val anim = ValueAnimator.ofFloat(1f, 1.2f)
        anim.duration = 100
        anim.addUpdateListener { animation ->
            binding.tvCartCount.scaleX = animation.animatedValue as Float
            binding.tvCartCount.scaleY = animation.animatedValue as Float

        }
        anim.repeatCount = 1
        anim.repeatMode = ValueAnimator.REVERSE
        anim.start()
    }

    private fun showSnackBar() {
        val tv = view?.findViewById(com.google.android.material.R.id.snackbar_action) as? TextView
        tv?.typeface = ResourcesCompat.getFont(requireContext(), R.font.helvetica_neue)
        tv?.setTypeface(null, Typeface.BOLD)
        Snackbar.make(binding.root, getString(R.string.msg_item_added), Snackbar.LENGTH_SHORT)
            .setAction(getString(R.string.msg_view_bag)) {
                val gotoCart = NavGraphDirections.actionToCartFragment()
                navigateTo(gotoCart)
            }
            .setTextColor(Color.WHITE)
            .setAnchorView(binding.btnAddToCart)
            .setActionTextColor(Color.WHITE)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.black))
            .show()
    }

    private fun downloadPolicy() {
        val uri = Uri.parse(binding.variant?.refund?.policy)
        val mManager =
            requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request: DownloadManager.Request? = DownloadManager.Request(uri)
            .setTitle("Febys Return & Refund Policy")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, " ReturnPolicy.pdf")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDescription("Downloading...")
            .setMimeType("application/pdf");

        request?.allowScanningByMediaScanner()
        mManager.enqueue(request)
    }

    private fun handleFavClick() {
        if (isUserLoggedIn) toggleFavAndUpdateIcon() else gotoLogin()
    }

    private fun handleAddToCartClick() {
        val product = binding.product ?: return
        val skuId = binding.variant?.skuId ?: return
        cartViewModel.addToCart(product, skuId)
    }

    private fun handleReviewsSortingOrderCheckedChange(checkedId: Int) {
        val product = binding.product ?: return
        reviewsSorting =
            if (checkedId == R.id.rb_most_recent) ReviewsSorting.RECENT else ReviewsSorting.RATING
        updateReviews(product.ratingsAndReviews)
    }

    private fun getCheckedIdOfReviewSorting() =
        if (reviewsSorting == ReviewsSorting.RECENT) R.id.rb_most_recent else R.id.rb_top_reviews

    private fun handlePostAnswerClick() {
        val answer = binding.bottomSheetReplyQuestion.etAnswer.text.toString().trim()
        val threadId = binding.thread?._id ?: return
        if (answer.isEmpty()) return
        productDetailViewModel.replyQuestion(args.productId, answer, threadId)
    }

    private fun handleAskAboutProductClick() {
        if (!isUserLoggedIn) gotoLogin() else showBottomSheet(askQuestionBottomSheet)
    }

    private fun handleAskQuestionClick() {
        val question = binding.bottomSheetAskQuestion.etQuestion.text.toString().trim()
        if (question.isNotEmpty()) {
            productDetailViewModel.askQuestion(args.productId, question)
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

        productDetailViewModel.observeQAThreads.observe(viewLifecycleOwner) {
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

                    binding.bottomSheetAskQuestion.etQuestion.setText("")
                    closeBottomSheet(askQuestionBottomSheet)

                    binding.bottomSheetReplyQuestion.etAnswer.setText("")
                    closeBottomSheet(replyQuestionBottomSheet)

                    updateQuestionAnswersThread(it.data)
                }
            }
        }

        productDetailViewModel.observeReviews.observe(viewLifecycleOwner) {
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
                    updateReviews(it.data)
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
                    updateAdditionalProducts()
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
                    updateAdditionalProducts()
                }
            }
        }
    }

    private fun updateAdditionalProducts() {
        binding.containerAdditionalProducts.removeAllViews()
        productDetailViewModel.getRecommendProducts()?.let {
            addAdditionalProduct(
                FiltersType.RECOMMENDED_PRODUCT,
                getString(R.string.label_customer_recommend),
                if (it.size > 4) it.subList(0, 4) else it
            )
        }
        productDetailViewModel.getSimilarProducts()?.let {
            addAdditionalProduct(
                FiltersType.SIMILAR_PRODUCT,
                getString(R.string.label_compare_with_similar_items),
                if (it.size > 4) it.subList(0, 4) else it
            )
        }
    }

    private fun updateUi(product: Product) {
        binding.product = product

        val variant = productDetailViewModel.selectedVariant
            ?: product.variants.firstOrNull { it.skuId == args.skuId }
            ?: product.variants.firstOrNull { it.default }
            ?: product.variants[0]

        variant.getFirstVariantAttr()?.value?.let { selectedFirstAttr ->
            productDetailViewModel.selectedFirstAttr = selectedFirstAttr

            val firstAttrList = productDetailViewModel.getFirstAttrList(product)

            if (binding.product?.vendor?.official!!) {
                binding.ivBadge.isVisible = true
            }

            productVariantFirstAttrAdapter
                .submitList(selectedFirstAttr, firstAttrList)

            variant.getSecondVariantAttr()?.value?.let { selectedSecondAttr ->
                productDetailViewModel.selectedSecondAttr = selectedSecondAttr

                val secondAttrList =
                    productDetailViewModel.getSecondAttrList(selectedFirstAttr, product)

                productVariantSecondAttrAdapter.submitList(selectedSecondAttr, secondAttrList)
            }
        }

        updateVariant(variant)

        updateProductDescription(product.descriptions)

        updateQuestionAnswersThread(product.qaThreads)

        Rating.addRatingToUi(
            product.stats.rating, product.scores, binding.containerRatingAndReviews.ratings
        )

        updateReviews(product.ratingsAndReviews)

        val storeRating = product.vendor.stats.rating.score
        binding.storeRatingBar.rating = storeRating.toFloat()
        binding.storeRatingBar.stepSize = 0.5f
        binding.tvStoreRating.text = getString(R.string.store_rating, storeRating)

        if (args.threadId != null) {
            binding.seeMoreQAndA.performClick()
        }
    }

    private fun updateQuestionAnswersThread(qaThreads: MutableList<QAThread>) {
        binding.seeMoreQAndA.isVisible = qaThreads.size >= 3
        if (qaThreads.isEmpty()) return

        binding.containerQAndAThread.removeAllViews()

        addQuestionAnswersToLayout(qaThreads[0])
        if (qaThreads.size >= 2) {
            addQuestionAnswersToLayout(qaThreads[1])
        }
    }

    private fun addQuestionAnswersToLayout(qaThread: QAThread, position: Int = -1) {
        val chat = qaThread.chat

        if (chat.isNullOrEmpty()) return

        val parent = binding.containerQAndAThread
        val layoutQuestionAnswersThread = ItemQuestionAnswersThreadBinding
            .inflate(layoutInflater, parent, false)

        layoutQuestionAnswersThread.root.tag = qaThread._id
        layoutQuestionAnswersThread.question.text = chat[0].message
        layoutQuestionAnswersThread.voteUp.text = qaThread.upVotes.size.toString()
        layoutQuestionAnswersThread.voteDown.text = qaThread.downVotes.size.toString()

        val consumerId = consumer?.id.toString()

        val voteUpDrawable = if (qaThread.upVotes.contains(consumerId)) {
            R.drawable.ic_vote_up_fill
        } else {
            R.drawable.ic_vote_up
        }
        layoutQuestionAnswersThread.voteUp.setDrawableRes(voteUpDrawable)

        val voteDownDrawable = if (qaThread.downVotes.contains(consumerId)) {
            R.drawable.ic_vote_down_fill
        } else {
            R.drawable.ic_vote_down
        }
        layoutQuestionAnswersThread.voteDown.setDrawableRes(voteDownDrawable)

        layoutQuestionAnswersThread.reply.setOnClickListener {
            if (isUserLoggedIn) {
                binding.thread = qaThread
                showBottomSheet(replyQuestionBottomSheet)
            } else {
                gotoLogin()
            }
        }

        layoutQuestionAnswersThread.voteUp.setOnClickListener {
            if (isUserLoggedIn) {
                productDetailViewModel.questionsVoteUp(
                    args.productId,
                    qaThread._id,
                    qaThread.upVotes.contains(consumer?.id.toString())
                )
            } else {
                gotoLogin()
            }
        }

        layoutQuestionAnswersThread.voteDown.setOnClickListener {
            if (isUserLoggedIn) {
                productDetailViewModel.questionVoteDown(
                    args.productId,
                    qaThread._id,
                    qaThread.downVotes.contains(consumer?.id.toString())
                )
            } else {
                gotoLogin()
            }
        }

        val answersAdapter = AnswersAdapter()
        layoutQuestionAnswersThread.rvAnswers.adapter = answersAdapter
        layoutQuestionAnswersThread.rvAnswers.isNestedScrollingEnabled = false
        val answers = if (chat.size > 1) chat.subList(1, chat.size) else mutableListOf()
        layoutQuestionAnswersThread.hasAnswers = answers.isNotEmpty()
        answersAdapter.submitList(answers)

        addView(parent, layoutQuestionAnswersThread.root, position)
    }

    private fun addAdditionalProduct(
        filterType: FiltersType, title: String, products: List<Product>, position: Int = -1,
    ) {
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
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
                isNestedScrollingEnabled = false
                layoutManager = GridLayoutManager(context, 2)
                adapter = additionalAdapter
            }
            additionalAdapter.onItemClick = { item ->
                val gotoProductDetail =
                    NavGraphDirections.actionToProductDetail(item._id, item.variants[0].skuId)
                navigateTo(gotoProductDetail)
            }
            additionalAdapter.toggleFavIfUserLoggedIn = { skuId ->
                isUserLoggedIn.also {
                    if (it) {
                        productDetailViewModel.toggleFav(skuId)
                    } else {
                        val navigateToLogin = NavGraphDirections.actionToLoginFragment()
                        navigateTo(navigateToLogin)
                    }
                }
            }
            layoutAdditionalProductBinding.btnAdditionalProductShopAll.setOnClickListener {
                var actionToProductListing: NavDirections? = null
                if (filterType == FiltersType.SIMILAR_PRODUCT) {
                    actionToProductListing = ProductDetailFragmentDirections
                        .actionProductDetailFragmentToSimilarProductListing(args.productId, title)
                } else if (filterType == FiltersType.RECOMMENDED_PRODUCT) {
                    actionToProductListing = ProductDetailFragmentDirections
                        .actionProductDetailFragmentToRecommendedProductListing(
                            args.productId,
                            title
                        )
                }

                actionToProductListing?.let { navigateTo(it) }
            }
        }

        addView(parent, layoutAdditionalProductBinding.root, position)
    }

    private fun updateReviews(reviews: List<RatingAndReviews>) {
        binding.seeMoreRatingAndReviews.isInvisible = reviews.size < 3
        val sortedReviews = getSortedReviews(reviews)
        binding.containerReviews.apply {
            removeAllViews()
            isVisible = sortedReviews.isNotEmpty()
            val consumerId = consumer?.id?.toString() ?: ""
            sortedReviews.forEach { item ->
                val reviewBinding = ItemReviewsBinding.inflate(
                    layoutInflater, binding.containerReviews, false
                )

                reviewBinding.userImage.load(item.consumer.profileImage)
                reviewBinding.userName.text = item.consumer.fullName
                reviewBinding.userRatingBar.rating = item.score.toFloat()
                reviewBinding.userRatingBar.stepSize = 0.5f
                reviewBinding.tvReview.text = item.review.comment
                reviewBinding.date.text =
                    Utils.DateTime.formatDate(
                        item.createdAt,
                        Utils.DateTime.FORMAT_MONTH_DATE_YEAR
                    )

                reviewBinding.tvReview.isVisible = item.review.comment.isNotEmpty()

                reviewBinding.voteUp.text = item.upVotes.size.toString()
                reviewBinding.voteDown.text = item.downVotes.size.toString()

                val isUserVoteUp = item.upVotes.contains(consumerId)
                val voteUpDrawable = getDrawableForVoteUp(isUserVoteUp)
                reviewBinding.voteUp.setDrawableRes(voteUpDrawable)

                val isUserVoteDown = item.downVotes.contains(consumerId)
                val voteDownDrawable = getDrawableForVoteDown(isUserVoteDown)
                reviewBinding.voteDown.setDrawableRes(voteDownDrawable)

                reviewBinding.voteUp.setOnClickListener {
                    handleReviewVoteUpClick(item)
                }

                reviewBinding.voteDown.setOnClickListener {
                    handleReviewVoteDownClick(item)
                }

                addView(binding.containerReviews, reviewBinding.root)
            }
        }
    }

    private fun getDrawableForVoteDown(userVoteDown: Boolean): Int {
        return if (userVoteDown) R.drawable.ic_vote_down_fill else R.drawable.ic_vote_down
    }

    private fun getDrawableForVoteUp(userVoteUp: Boolean): Int {
        return if (userVoteUp) R.drawable.ic_vote_up_fill else R.drawable.ic_vote_up
    }

    private fun handleReviewVoteUpClick(review: RatingAndReviews) {
        if (isUserLoggedIn) {
            productDetailViewModel
                .reviewVoteUp(review._id, review.upVotes.contains(consumer?.id.toString()))
        } else {
            gotoLogin()
        }
    }

    private fun handleReviewVoteDownClick(review: RatingAndReviews) {
        if (isUserLoggedIn) {
            productDetailViewModel
                .reviewVoteDown(review._id, review.downVotes.contains(consumer?.id.toString()))
        } else {
            gotoLogin()
        }
    }

    private fun getSortedReviews(reviews: List<RatingAndReviews>): List<RatingAndReviews> {
        val sortedReviews = when (reviewsSorting) {
            ReviewsSorting.RECENT -> productDetailViewModel.reviewsByMostRecent(reviews)
            ReviewsSorting.RATING -> productDetailViewModel.reviewsByRating(reviews)
        }

        return if (reviews.size > 3) sortedReviews.subList(0, 3) else sortedReviews
    }

    private fun updateVariant(variant: Variant) {
        binding.variant = variant
        productDetailViewModel.selectedVariant = variant

        if (variant.availability) {
            binding.icOutStock.hide()
            binding.btnAddToCart.isEnabled = true
            binding.btnPayNow.isEnabled = true
        } else {
            binding.icOutStock.show()
            binding.btnAddToCart.isEnabled = false
            binding.btnPayNow.isEnabled = false
        }
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

    private fun updateProductDescription(description: Description, openByDefault: Boolean = false) {
        val productDescriptionsBinding = LayoutProductDescriptionBinding.inflate(
            layoutInflater, binding.containerProductDescriptions, false
        )

        productDescriptionsBinding.description = description
        productDescriptionsBinding.labelDescription.text = description.title.capitalizeWords

        productDescriptionsBinding.productDescriptionToggle.setOnClickListener {
            productDescriptionsBinding.containerProductDescription.toggleVisibility()
            productDescriptionsBinding.ivDescriptionArrow.updateArrowByVisibility(
                productDescriptionsBinding.containerProductDescription.isVisible
            )
            binding.scrollView.scrollToDescendant(productDescriptionsBinding.containerProductDescription)
        }

        if (openByDefault) {
            productDescriptionsBinding.containerProductDescription.toggleVisibility()
            productDescriptionsBinding.ivDescriptionArrow.updateArrowByVisibility(
                productDescriptionsBinding.containerProductDescription.isVisible
            )
            productDescriptionsBinding.containerProductDescription.isVisible = true
            productDescriptionsBinding.ivDescriptionArrow.updateArrowByVisibility(true)
        }

        addView(binding.containerProductDescriptions, productDescriptionsBinding.root)
    }

    private fun updateProductDescription(descriptions: List<Description>) {
        for ((index, description) in descriptions.withIndex()) {
            updateProductDescription(description, index == 0)
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

    private fun showBottomSheet(bottomSheet: BottomSheetBehavior<View>) {
        binding.bgDim.fadeVisibility(true)
        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun closeBottomSheet(bottomSheet: BottomSheetBehavior<View>) {
        binding.bgDim.fadeVisibility(false)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun closeBottomsSheetElseGoBack() {
        listOf(
            variantFirstAttrBottomSheet,
            variantSecondAttrBottomSheet,
            askQuestionBottomSheet
        ).forEach {
            if (it.state != BottomSheetBehavior.STATE_HIDDEN) {
                closeBottomSheet(it)
                return
            }
        }

        goBack()
    }

    private fun gotoQAThreads(threads: Array<QAThread>) {
        val userId = consumer?.id?.toString()
        val action = ProductDetailFragmentDirections
            .actionProductDetailFragmentToQAThreadsFragment(
                userId,
                args.productId,
                args.threadId,
                threads
            )
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
        val images: List<String>, fa: Fragment,
    ) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = images.size

        override fun createFragment(position: Int): Fragment =
            ProductSliderPageFragment.newInstance(images[position], images)
    }
}