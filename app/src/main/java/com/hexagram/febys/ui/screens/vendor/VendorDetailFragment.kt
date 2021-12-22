package com.hexagram.febys.ui.screens.vendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentVendorDetailBinding
import com.hexagram.febys.models.api.social.Social
import com.hexagram.febys.models.api.vendor.Vendor
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.product.detail.ReviewsAdapter
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VendorDetailFragment : BaseFragment() {
    private lateinit var binding: FragmentVendorDetailBinding
    private val vendorViewModel: VendorViewModel by viewModels()
    private val args: VendorDetailFragmentArgs by navArgs()

    private val reviewsAdapter = ReviewsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vendorViewModel.fetchVendorDetail(args.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        reviewsAdapter.consumerId = consumer?.id.toString() ?: ""
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
            binding.isFollowing = !binding.isFollowing!!

            if (binding.isFollowing!!)
                vendorViewModel.followVendor(args.id)
            else
                vendorViewModel.unFollowVendor(args.id)
        }
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
            vendorName.text = vendor.name
            profileImg.load(vendor.businessInfo.logo)
            vendor.templatePhoto?.let { headerImg.load(it) }
            title.text = vendor.name
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

            reviewsAdapter.submitList(vendor.ratingsAndReviews, false)
            binding.noReviews.isVisible = vendor.ratingsAndReviews.isEmpty()
        }
    }
}