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
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.progressindicator.LinearProgressIndicator
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
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

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
            if (isUserLoggedIn) {
                toggleFavAndUpdateIcon()
            } else {
                gotoLogin()
            }
        }

        binding.containerProductVariantFirstAttr.setOnClickListener {
            binding.variant?.let {
                showBottomSheet(variantFirstAttrBottomSheet)
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
            val product = binding.product
            val skuId = binding.variant?.skuId
            if (product != null && skuId != null) {
                cartViewModel.addToCart(product, skuId)
            }
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
            val product = binding.product ?: return@setOnCheckedChangeListener
            when (checkedId) {
                R.id.rb_most_recent -> {
                    reviewsSorting = ReviewsSorting.RECENT
                }
                R.id.rb_top_reviews -> {
                    reviewsSorting = ReviewsSorting.RATING
                }
            }
            updateReviews(product.ratingsAndReviews)
        }

        binding.bottomSheetReplyQuestion.btnPostAnswer.setOnClickListener {
            val answer = binding.bottomSheetReplyQuestion.etAnswer.text.toString().trim()
            val threadId = binding.thread?._id
            if (threadId == null || answer.isEmpty()) return@setOnClickListener
            productDetailViewModel.replyQuestion(args.productId, answer, threadId)
        }

        binding.containerAskAboutProduct.setOnClickListener {
            if (!isUserLoggedIn) {
                gotoLogin()
            } else {
                showBottomSheet(askQuestionBottomSheet)
            }
        }

        binding.bottomSheetAskQuestion.btnAskQuestion.setOnClickListener {
            val question = binding.bottomSheetAskQuestion.etQuestion.text.toString().trim()
            if (question.isNotEmpty()) {
                productDetailViewModel.askQuestion(args.productId, question)
            }
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
            val checkedId = when (reviewsSorting) {
                ReviewsSorting.RECENT -> R.id.rb_most_recent
                ReviewsSorting.RATING -> R.id.rb_top_reviews
            }
            binding.containerRatingAndReviews.reviews.radioGroupSorting.check(checkedId)
            updateReviews(ratingsAndReviews)
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

                productVariantSecondAttrAdapter.submitList(selectedSecondAttr, secondAttrList)
            }
        }

        updateVariant(variant)

        updateProductDescription(product.descriptions)

        updateQuestionAnswersThread(product.qaThreads)

        updateRating(product.stats.rating, product.scores)

        updateReviews(product.ratingsAndReviews)

        val storeRating = product.stats.rating.score
        binding.storeRatingBar.rating = storeRating.toFloat()
        binding.storeRatingBar.stepSize = 0.5f
        binding.tvStoreRating.text = getString(R.string.store_rating, storeRating)
    }

    private fun updateQuestionAnswersThread(qaThreads: MutableList<QAThread>) {
        binding.seeMoreQAndA.isVisible = qaThreads.isEmpty()
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

    private fun addAdditionalProduct(title: String, products: List<Product>, position: Int = -1) {
        if (products.isEmpty()) return

        val parent = binding.containerAdditionalProducts
        parent.removeAllViews()

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
        }

        addView(parent, layoutAdditionalProductBinding.root, position)
    }

    private fun updateRating(rating: Rating, scores: List<Rating>) {
        binding.containerRatingAndReviews.ratings.apply {
            averageRating.text =
                getString(R.string.average_rating_based_on, rating.score, rating.count)

            fun updateRatingRow(
                progressBar: LinearProgressIndicator, total: TextView, count: Int?
            ) {
                val avg = count?.times(100.0)?.div(rating.count)?.toInt() ?: 0
                progressBar.progress = avg
                total.text = (count ?: 0).toString()
            }

            val star1Count = scores.firstOrNull { it.score.toInt() == 1 }?.count
            updateRatingRow(start1RatingBar, star1Total, star1Count)

            val star2Count = scores.firstOrNull { it.score.toInt() == 2 }?.count
            updateRatingRow(start2RatingBar, star2Total, star2Count)

            val star3Count = scores.firstOrNull { it.score.toInt() == 3 }?.count
            updateRatingRow(start3RatingBar, star3Total, star3Count)

            val star4Count = scores.firstOrNull { it.score.toInt() == 4 }?.count
            updateRatingRow(start4RatingBar, star4Total, star4Count)

            val star5Count = scores.firstOrNull { it.score.toInt() == 5 }?.count
            updateRatingRow(start5RatingBar, star5Total, star5Count)
        }
    }

    private fun updateReviews(reviews: List<RatingAndReviews>) {
        binding.seeMoreRatingAndReviews.isVisible = reviews.isNotEmpty()

        val sortedReviews = when (reviewsSorting) {
            ReviewsSorting.RECENT -> productDetailViewModel.reviewsByMostRecent(reviews)
            ReviewsSorting.RATING -> productDetailViewModel.reviewsByRating(reviews)
        }
        val list = if (reviews.size > 3) sortedReviews.subList(0, 3) else sortedReviews

        binding.containerReviews.apply {
            removeAllViews()
            isVisible = list.isNotEmpty()
            if (list.isEmpty()) return@apply

            val consumerId = consumer?.id?.toString() ?: ""
            list.forEach { item ->
                val reviewBinding = ItemReviewsBinding.inflate(
                    layoutInflater, binding.containerReviews, false
                )

                reviewBinding.userName.text = item.consumer.firstName
                reviewBinding.userRatingBar.progress = item.score.toInt()
                reviewBinding.tvReview.text = item.review.comment
                reviewBinding.date.text =
                    Utils.DateTime.formatDate(
                        item.createdAt,
                        Utils.DateTime.FORMAT_MONTH_DATE_YEAR
                    )

                reviewBinding.tvReview.isVisible = item.review.comment.isNotEmpty()

                reviewBinding.voteUp.text = item.upVotes.size.toString()
                reviewBinding.voteDown.text = item.downVotes.size.toString()

                val voteUpDrawable = if (item.upVotes.contains(consumerId)) {
                    R.drawable.ic_vote_up_fill
                } else {
                    R.drawable.ic_vote_up
                }
                reviewBinding.voteUp.setDrawableRes(voteUpDrawable)

                val voteDownDrawable = if (item.downVotes.contains(consumerId)) {
                    R.drawable.ic_vote_down_fill
                } else {
                    R.drawable.ic_vote_down
                }
                reviewBinding.voteDown.setDrawableRes(voteDownDrawable)

                reviewBinding.voteUp.setOnClickListener {
                    if (isUserLoggedIn) {
                        productDetailViewModel.reviewVoteUp(
                            item._id,
                            item.upVotes.contains(consumer?.id.toString())
                        )
                    } else {
                        gotoLogin()
                    }
                }
                reviewBinding.voteDown.setOnClickListener {
                    if (isUserLoggedIn) {
                        productDetailViewModel.reviewVoteDown(
                            item._id,
                            item.downVotes.contains(consumer?.id.toString())
                        )
                    } else {
                        gotoLogin()
                    }
                }

                addView(binding.containerReviews, reviewBinding.root)
            }
        }
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
        productDescriptionsBinding.labelDescription.text = description.title.capitalize()

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
            .actionProductDetailFragmentToQAThreadsFragment(userId, args.productId, threads)
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