package com.hexagram.febys.ui.screens.vendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentVendorDetailBinding
import com.hexagram.febys.models.view.SocialLink
import com.hexagram.febys.models.view.VendorDetail
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

    private fun updateUi(vendorDetail: VendorDetail) {
        binding.apply {
            vendorName.text = vendorDetail.name
            profileImg.load(vendorDetail.profileImage)
            headerImg.load(vendorDetail.headerImage)
            title.text = vendorDetail.name
            vendorName.text = vendorDetail.name
            slogan.text = vendorDetail.slogan
            type.text = vendorDetail.type
            address.text = vendorDetail.address
            addSocialLinks(vendorDetail.socialLinks)

            binding.isFollowing = vendorDetail.isFollow
        }
    }

    private fun addSocialLinks(socialLinks: List<SocialLink>) {
        SocialLink.addAllTo(socialLinks, binding.containerSocialMediaFollow)
    }
}