package com.hexagram.febys.ui.screens.vendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentVendorDetailBinding
import com.hexagram.febys.models.api.rating.Rating
import com.hexagram.febys.models.api.social.Social
import com.hexagram.febys.models.api.vendor.Vendor
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VendorDetailFragment : BaseFragment() {
    private lateinit var binding: FragmentVendorDetailBinding
    private val vendorViewModel: VendorViewModel by viewModels()
    private val args: VendorDetailFragmentArgs by navArgs()

    private val reviewsAdapter = VendorReviewsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vendorViewModel.fetchVendorDetail(args.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentVendorDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListener()
        setObserver()
    }

    private fun initUi() {
        binding.rvReviews.isNestedScrollingEnabled = false
        binding.rvReviews.adapter = reviewsAdapter
        binding.rvReviews.applySpaceItemDecoration(R.dimen._16sdp)
    }

    private fun uiListener() {
        binding.ivClose.setOnClickListener { goBack() }

        binding.btnToggleFollow.setOnClickListener {
            if (!isUserLoggedIn) {
                val gotoLogin = NavGraphDirections.actionToLoginFragment()
                navigateTo(gotoLogin)
                return@setOnClickListener
            }

            if (binding.isFollowing == null) return@setOnClickListener

            if (binding.isFollowing!!)
                showUnfollowConfirmationPopup {
                    vendorViewModel.unFollowVendor(args.id)
                    binding.isFollowing = false
                }
            else {
                vendorViewModel.followVendor(args.id)
                binding.isFollowing = true
            }
        }
    }

    private fun showUnfollowConfirmationPopup(confirmCallback: () -> Unit) {
        val resId = R.drawable.ic_vendor_follow
        val title = getString(R.string.label_delete_warning)
        val msg = getString(R.string.msg_for_unfollow_vendor)
        showWarningDialog(resId, title, msg) { confirmCallback() }
    }

    private fun setObserver() {
        vendorViewModel.observerVendorDetail.observe(viewLifecycleOwner) {
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

    private fun updateUi(vendor: Vendor) {
        binding.apply {
            title.text = vendor.shopName
            if (vendor.official) {
                ivBadge.isVisible = true
            }
            if(vendor.templatePublished){
            profileImg.load(vendor.templatePhoto)
            vendor.template
                ?.firstOrNull { it.section == "1,1" }
                ?.images?.firstOrNull()?.url?.let { headerImg.load(it) }
            }
            vendorName.text = vendor.name
            type.text = vendor.role.name
            address.text = vendor.contactDetails.address
            Social.addAllTo(
                vendor.socials, binding.containerSocialMediaFollow, binding.labelNoSocialLink
            )
            isFollowing = args.isFollow

            val storeRating = vendor.stats.rating.score
            binding.storeRatingBar.rating = storeRating.toFloat()
            binding.storeRatingBar.stepSize = 0.5f
            binding.tvStoreRating.text = getString(R.string.store_rating, storeRating)
            binding.containerVendorStoreRating.tvAverageRating.text = storeRating.toString()

            val hasReviews = vendor.ratingsAndReviews.isNotEmpty()
                    || vendor.pricingScore.isNotEmpty()
                    || vendor.valueScore.isNotEmpty()
                    || vendor.qualityScore.isNotEmpty()

            binding.noReviews.isVisible = !hasReviews
            binding.containerVendorStoreRating.root.isVisible = hasReviews

            if (hasReviews) {
                updateRatings(vendor.pricingScore, vendor.valueScore, vendor.qualityScore)
                reviewsAdapter.submitList(vendor.ratingsAndReviews)
            }
        }
    }

    private fun updateRatings(
        pricingScore: List<Rating>, valueScore: List<Rating>, qualityScore: List<Rating>
    ) {
        with(binding) {
            containerVendorStoreRating.containerPriceRating.isVisible = pricingScore.isNotEmpty()
            containerVendorStoreRating.containerValueRating.isVisible = valueScore.isNotEmpty()
            containerVendorStoreRating.containerQualityRating.isVisible = qualityScore.isNotEmpty()
        }

        updateAverages(pricingScore, valueScore, qualityScore)
        updatePricingRatings(pricingScore)
        updateValueRatings(valueScore)
        updateQualityRatings(qualityScore)
    }

    private fun updateAverages(
        pricingScore: List<Rating>, valueScore: List<Rating>, qualityScore: List<Rating>
    ) {
        var totalPriceScore = 0.0
        var totalPriceScoreCount = 0
        pricingScore.forEach {
            totalPriceScore += it.score.times(it.count)
            totalPriceScoreCount += it.count
        }
        val priceAverage = totalPriceScore.div(totalPriceScoreCount)

        var totalValueScore = 0.0
        var totalValueScoreCount = 0
        valueScore.forEach {
            totalValueScore += it.score.times(it.count)
            totalValueScoreCount += it.count
        }
        val valueAverage = totalValueScore.div(totalValueScoreCount)

        var totalQualityScore = 0.0
        var totalQualityScoreCount = 0
        qualityScore.forEach {
            totalQualityScore += it.score.times(it.count)
            totalQualityScoreCount += it.count
        }
        val qualityAverage = totalQualityScore.div(totalQualityScoreCount)

        binding.containerVendorStoreRating.apply {
            averagePriceRating.text =
                getString(R.string.label_average_price_rating, priceAverage.toFixedDecimal(1))
            averageValueRating.text =
                getString(R.string.label_average_value_rating, valueAverage.toFixedDecimal(1))
            averageQualityRating.text =
                getString(R.string.label_average_quality_rating, qualityAverage.toFixedDecimal(1))
        }
    }

    private fun updatePricingRatings(pricingScore: List<Rating>) =
        with(binding.containerVendorStoreRating) {
            var totalCount = 0
            pricingScore.forEach { totalCount += it.count }
            if (totalCount == 0) totalCount = 1

            val star5Count = pricingScore.firstOrNull { it.score.toInt() == 5 }?.count
            updateRatingRow(start5RatingBar, star5Count, totalCount)

            val star4Count = pricingScore.firstOrNull { it.score.toInt() == 4 }?.count
            updateRatingRow(start4RatingBar, star4Count, totalCount)

            val star3Count = pricingScore.firstOrNull { it.score.toInt() == 3 }?.count
            updateRatingRow(start3RatingBar, star3Count, totalCount)

            val star2Count = pricingScore.firstOrNull { it.score.toInt() == 2 }?.count
            updateRatingRow(start2RatingBar, star2Count, totalCount)

            val star1Count = pricingScore.firstOrNull { it.score.toInt() == 1 }?.count
            updateRatingRow(start1RatingBar, star1Count, totalCount)
        }

    private fun updateValueRatings(valueScore: List<Rating>) =
        with(binding.containerVendorStoreRating) {
            var totalCount = 0
            valueScore.forEach { totalCount += it.count }
            if (totalCount == 0) totalCount = 1

            val star5Count = valueScore.firstOrNull { it.score.toInt() == 5 }?.count
            updateRatingRow(start5RatingBarValue, star5Count, totalCount)

            val star4Count = valueScore.firstOrNull { it.score.toInt() == 4 }?.count
            updateRatingRow(start4RatingBarValue, star4Count, totalCount)

            val star3Count = valueScore.firstOrNull { it.score.toInt() == 3 }?.count
            updateRatingRow(start3RatingBarValue, star3Count, totalCount)

            val star2Count = valueScore.firstOrNull { it.score.toInt() == 2 }?.count
            updateRatingRow(start2RatingBarValue, star2Count, totalCount)

            val star1Count = valueScore.firstOrNull { it.score.toInt() == 1 }?.count
            updateRatingRow(start1RatingBarValue, star1Count, totalCount)
        }

    private fun updateQualityRatings(qualityScore: List<Rating>) =
        with(binding.containerVendorStoreRating) {
            var totalCount = 0
            qualityScore.forEach { totalCount += it.count }
            if (totalCount == 0) totalCount = 1

            val star5Count = qualityScore.firstOrNull { it.score.toInt() == 5 }?.count
            updateRatingRow(start5QualityBarValue, star5Count, totalCount)

            val star4Count = qualityScore.firstOrNull { it.score.toInt() == 4 }?.count
            updateRatingRow(start4QualityBarValue, star4Count, totalCount)

            val star3Count = qualityScore.firstOrNull { it.score.toInt() == 3 }?.count
            updateRatingRow(start3QualityBarValue, star3Count, totalCount)

            val star2Count = qualityScore.firstOrNull { it.score.toInt() == 2 }?.count
            updateRatingRow(start2QualityBarValue, star2Count, totalCount)

            val star1Count = qualityScore.firstOrNull { it.score.toInt() == 1 }?.count
            updateRatingRow(start1QualityBarValue, star1Count, totalCount)
        }

    private fun updateRatingRow(
        progressBar: LinearProgressIndicator, count: Int?, total: Int
    ) {
        val avg = count?.times(100.0)?.div(total)?.toInt() ?: 0
        progressBar.progress = avg
    }
}