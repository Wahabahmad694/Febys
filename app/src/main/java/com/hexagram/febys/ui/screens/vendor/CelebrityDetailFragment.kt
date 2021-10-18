package com.hexagram.febys.ui.screens.vendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentCelebrityDetailBinding
import com.hexagram.febys.models.view.SocialLink
import com.hexagram.febys.models.view.VendorDetail
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.OldProduct
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.product.listing.ProductListingPagerAdapter
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CelebrityDetailFragment : BaseFragment() {
    private lateinit var binding: FragmentCelebrityDetailBinding
    private val celebrityViewModel: VendorViewModel by viewModels()
    private val args: CelebrityDetailFragmentArgs by navArgs()

    private val productListingPagerAdapter = ProductListingPagerAdapter()
    private val endorsementAdapter = EndorsementAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        celebrityViewModel.fetchVendorDetail(args.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCelebrityDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListener()
        setObserver()
    }

    private fun initUi() {
        binding.rvProductList.apply {
            isNestedScrollingEnabled = false
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
            layoutManager = GridLayoutManager(context, 2)
            adapter = this@CelebrityDetailFragment.productListingPagerAdapter
        }

        binding.rvMyEndorsements.apply {
            setHasFixedSize(true)
            applySpaceItemDecoration(horizontalDimenRes = R.dimen._24dp)
            adapter = endorsementAdapter
        }
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener {
            goBack()
        }

        binding.btnRefine.setOnClickListener {
            // goto filter screen
        }

        binding.btnToggleFollow.setOnClickListener {
            if (!isUserLoggedIn) {
                val gotoLogin = NavGraphDirections.actionToLoginFragment()
                navigateTo(gotoLogin)
                return@setOnClickListener
            }

            if (binding.isFollowing == null) return@setOnClickListener
            binding.isFollowing = !binding.isFollowing!!

            if (binding.isFollowing!!)
                celebrityViewModel.followVendor(args.id)
            else
                celebrityViewModel.unFollowVendor(args.id)
        }

        productListingPagerAdapter.interaction = object : ProductListingPagerAdapter.Interaction {
            override fun onItemSelected(position: Int, item: OldProduct) {
                val gotoProductDetail = NavGraphDirections.actionToProductDetail(item.id)
                navigateTo(gotoProductDetail)
            }

            override fun toggleFavIfUserLoggedIn(variantId: Int): Boolean {
                return isUserLoggedIn.also {
                    if (it) {
                        celebrityViewModel.toggleFav(/*newChange variantId*/ "")
                    } else {
                        val navigateToLogin = NavGraphDirections.actionToLoginFragment()
                        navigateTo(navigateToLogin)
                    }
                }
            }
        }

        binding.rvMyEndorsements.setOnScrollChangeListener { _, _, _, _, _ ->
            val horizontalScrollPosition =
                binding.rvMyEndorsements.getHorizontalScrollPosition()
            val param =
                (binding.ivIcScrollBar.layoutParams as ConstraintLayout.LayoutParams)
            param.horizontalBias = horizontalScrollPosition
            binding.ivIcScrollBar.layoutParams = param
        }
    }

    private fun setObserver() {
        setupPagerAdapter()

        celebrityViewModel.observerVendorDetail.observe(viewLifecycleOwner) {
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
            profileImg.load(vendorDetail.profileImage)
            headerImg.load(vendorDetail.headerImage)
            tvProductListingTitle.text = vendorDetail.name
            name.text = vendorDetail.name
            type.text = vendorDetail.type
            address.text = vendorDetail.address
            addSocialLinks(vendorDetail.socialLinks)
            endorsementAdapter.submitList(vendorDetail.endorsements)

            binding.isFollowing = vendorDetail.isFollow
        }
    }

    private fun addSocialLinks(socialLinks: List<SocialLink>) {
        SocialLink.addAllTo(socialLinks, binding.containerSocialMediaFollow)
    }

    private fun setupPagerAdapter() {
        binding.rvProductList.adapter = productListingPagerAdapter
        val fav = celebrityViewModel.getFav()
        productListingPagerAdapter.submitFav(/*newChange fav*/ mutableSetOf())

        viewLifecycleOwner.lifecycleScope.launch {
            celebrityViewModel.vendorProductListing(args.id) {
                setProductItemCount(it.totalRows)
            }.collectLatest { pagingData ->
                productListingPagerAdapter.submitData(pagingData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            productListingPagerAdapter.loadStateFlow.collectLatest {
                val state = it.refresh

                if (state is LoadState.Error) {
                    showToast(getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    private fun setProductItemCount(count: Int) {
        binding.tvProductListingCount.text =
            if (count == 0) {
                resources.getString(R.string.label_no_item)
            } else {
                resources.getQuantityString(R.plurals.items_count, count, count)
            }
    }

    override fun getTvCartCount(): TextView = binding.tvCartCount

    override fun getIvCart(): View = binding.ivCart
}