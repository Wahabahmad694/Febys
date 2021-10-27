package com.hexagram.febys.ui.screens.vendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.core.view.setPadding
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
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VendorDetailFragment : BaseFragment() {
    private lateinit var binding: FragmentVendorDetailBinding
    private val vendorViewModel: VendorViewModel by viewModels()
    private val args: VendorDetailFragmentArgs by navArgs()

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

        uiListener()
        setObserver()
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
            slogan.text = vendor.role.name
            type.isVisible = vendor.official
            address.text = vendor.contactDetails.address
            addSocialLinks(vendor.socials)

            binding.isFollowing = args.isFollow
        }
    }

    private fun addSocialLinks(socialLinks: List<Social>?) {
        if (socialLinks.isNullOrEmpty()) return

        binding.containerSocialMediaFollow.removeAllViews()
        socialLinks.forEach { socialLink ->
            val imageView = ImageView(binding.containerSocialMediaFollow.context)
            val layoutParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            imageView.setBackgroundResource(R.drawable.bg_social_link)
            imageView.setImageResource(socialLink.imageRes)
            imageView.setPadding(16)
            imageView.setOnClickListener {
                Utils.openLink(it.context, socialLink.url)
            }
            binding.containerSocialMediaFollow.addView(imageView, layoutParam)
        }
    }
}