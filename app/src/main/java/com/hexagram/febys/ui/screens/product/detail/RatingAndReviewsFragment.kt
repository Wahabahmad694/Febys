package com.hexagram.febys.ui.screens.product.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseBottomSheet
import com.hexagram.febys.databinding.FragmentRatingAndReviewsBinding
import com.hexagram.febys.models.api.product.RatingAndReviews
import com.hexagram.febys.models.api.rating.Rating
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.hideLoader
import com.hexagram.febys.utils.navigateTo
import com.hexagram.febys.utils.showLoader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RatingAndReviewsFragment : BaseBottomSheet() {

    companion object {
        const val REQ_KEY_RATING_AND_REVIEWS_UPDATE = "reqKeyRatingAndReviewsUpdate"
        const val REQ_KEY_REVIEW_SORTING_UPDATE = "reqKeyReviewSorting"
    }

    private lateinit var binding: FragmentRatingAndReviewsBinding
    private val args: RatingAndReviewsFragmentArgs by navArgs()
    private val productDetailViewModel: ProductDetailViewModel by viewModels()

    private val reviewsAdapter = ReviewsAdapter()
    private var reviewsSorting = ReviewsSorting.RECENT

    private var originalReviewsList = listOf<RatingAndReviews>()

    private val isUserLoggedIn
        get() = !args.userId.isNullOrEmpty()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRatingAndReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListeners()
        setupObserver()
    }

    private fun initUi() {
        reviewsAdapter.consumerId = args.userId ?: ""
        binding.containerRatingAndReviews.reviews.rvReviews.adapter = reviewsAdapter

        reviewsSorting = args.sorting
        val checkedId = getCheckedIdOfReviewSorting()
        binding.containerRatingAndReviews.reviews.radioGroupSorting.check(checkedId)

        Rating.addRatingToUi(
            args.rating, args.scores.toList(), binding.containerRatingAndReviews.ratings
        )

        updateReviews(args.reviews.toList())
    }

    private fun uiListeners() {
        binding.ivBack.setOnClickListener { goBack() }

        reviewsAdapter.upVote = { ratingAndReviews: RatingAndReviews, isRevoke: Boolean ->
            if (isUserLoggedIn) {
                productDetailViewModel.reviewVoteUp(
                    ratingAndReviews._id, ratingAndReviews.upVotes.contains(args.userId ?: "")
                )
            } else {
                gotoLogin()
            }
        }

        reviewsAdapter.downVote = { ratingAndReviews: RatingAndReviews, isRevoke: Boolean ->
            if (isUserLoggedIn) {
                productDetailViewModel.reviewVoteDown(
                    ratingAndReviews._id, ratingAndReviews.downVotes.contains(args.userId ?: "")
                )
            } else {
                gotoLogin()
            }
        }

        binding.containerRatingAndReviews.reviews.radioGroupSorting.setOnCheckedChangeListener { _, checkedId ->
            handleReviewsSortingOrderCheckedChange(checkedId)
        }
    }

    private fun handleReviewsSortingOrderCheckedChange(checkedId: Int) {
        reviewsSorting =
            if (checkedId == R.id.rb_most_recent) ReviewsSorting.RECENT else ReviewsSorting.RATING
        updateReviews(originalReviewsList)
    }

    private fun setupObserver() {
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

                    val bundle = Bundle()
                    bundle.putParcelableArrayList(
                        REQ_KEY_RATING_AND_REVIEWS_UPDATE, ArrayList(it.data)
                    )
                    setFragmentResult(REQ_KEY_RATING_AND_REVIEWS_UPDATE, bundle)
                }
            }
        }
    }

    private fun getCheckedIdOfReviewSorting() =
        if (reviewsSorting == ReviewsSorting.RECENT) R.id.rb_most_recent else R.id.rb_top_reviews

    private fun updateReviews(reviews: List<RatingAndReviews>) {
        originalReviewsList = reviews
        val sortedReviews = getSortedReviews(reviews)

        reviewsAdapter.submitList(sortedReviews)

        val bundle = Bundle()
        bundle.putSerializable(REQ_KEY_REVIEW_SORTING_UPDATE, reviewsSorting)
        setFragmentResult(REQ_KEY_REVIEW_SORTING_UPDATE, bundle)
    }

    private fun getSortedReviews(reviews: List<RatingAndReviews>): List<RatingAndReviews> {
        return when (reviewsSorting) {
            ReviewsSorting.RECENT -> productDetailViewModel.reviewsByMostRecent(reviews)
            ReviewsSorting.RATING -> productDetailViewModel.reviewsByRating(reviews)
        }
    }

    private fun gotoLogin() {
        val gotoLogin = NavGraphDirections.actionToLoginFragment()
        navigateTo(gotoLogin)
    }

    override fun fullScreen() = true
}