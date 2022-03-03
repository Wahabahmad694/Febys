package com.hexagram.febys.ui.screens.vendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
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
import com.hexagram.febys.models.api.category.Category
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.request.ProductListingRequest
import com.hexagram.febys.models.api.social.Social
import com.hexagram.febys.models.api.vendor.Vendor
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.product.filters.FilterViewModel
import com.hexagram.febys.ui.screens.product.filters.FiltersFragment
import com.hexagram.febys.ui.screens.product.filters.FiltersType
import com.hexagram.febys.ui.screens.product.listing.ProductListingPagerAdapter
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CelebrityDetailFragment : BaseFragment()  {
    private lateinit var binding: FragmentCelebrityDetailBinding
    private val celebrityViewModel: VendorViewModel by viewModels()
    private val filtersViewModel by viewModels<FilterViewModel>()
    private val args: CelebrityDetailFragmentArgs by navArgs()

    private val productListingPagerAdapter = ProductListingPagerAdapter()
    private val endorsementAdapter = EndorsementAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        celebrityViewModel.fetchVendorDetail(args.id)
        celebrityViewModel.fetchVendorEndorsement(args.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCelebrityDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListener()
        setObserver()
        fetchFilters()
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
            applySpaceItemDecoration(horizontalDimenRes = R.dimen._8sdp)
            adapter = endorsementAdapter
        }
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener {
            goBack()
        }

        binding.btnRefine.setOnClickListener {
            if (filtersViewModel.filters != null) {
                val gotoRefineProduct = NavGraphDirections
                    .toProductFilterFragment(
                        filtersViewModel.filters!!, filtersViewModel.appliedFilters
                    )
                navigateTo(gotoRefineProduct)
            }
        }

        binding.btnToggleFollow.setOnClickListener {
            if (!isUserLoggedIn) {
                val gotoLogin = NavGraphDirections.actionToLoginFragment()
                navigateTo(gotoLogin)
                return@setOnClickListener
            }

            if (binding.isFollowing == null) return@setOnClickListener
            binding.isFollowing = !binding.isFollowing!!


            if (binding.isFollowing!!){
                celebrityViewModel.followVendor(args.id)
                binding.btnToggleFollow.backgroundTintList = ContextCompat.getColorStateList(requireContext(),R.color.red)
            }

            else{
                celebrityViewModel.unFollowVendor(args.id)
            }
        }

        productListingPagerAdapter.interaction = object : ProductListingPagerAdapter.Interaction {
            override fun onItemSelected(position: Int, item: Product) {
                val gotoProductDetail =
                    NavGraphDirections.actionToProductDetail(item._id, item.variants[0].skuId)
                navigateTo(gotoProductDetail)
            }

            override fun toggleFavIfUserLoggedIn(skuId: String): Boolean {
                return isUserLoggedIn.also {
                    if (it) {
                        celebrityViewModel.toggleFav(skuId)
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
            binding.ivIcScrollBar.visibility =
                if (endorsementAdapter.itemCount >= 6) View.VISIBLE else View.GONE
            binding.ivBgScrollBar.visibility =
                if (endorsementAdapter.itemCount >= 6) View.VISIBLE else View.GONE
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

        celebrityViewModel.observerVendorEndorsement.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading,
                is DataState.Error,
                -> {
                    // do nothing
                }
                is DataState.Data -> {
                    val endorsement = it.data
                    binding.labelMyEndorsements.isVisible = endorsement.isNotEmpty()
                    binding.rvMyEndorsements.isVisible = endorsement.isNotEmpty()
                    binding.scrollBarEndorsements.isVisible = endorsement.isNotEmpty()

                    endorsementAdapter.submitList(endorsement)
                }
            }
        }

        celebrityViewModel.observeItemCount.observe(viewLifecycleOwner) {
            binding.tvProductListingCount.text = if (it == 0) {
                resources.getString(R.string.label_no_item)
            } else {
                resources.getQuantityString(R.plurals.items_count, it, it)
            }
        }

        filtersViewModel.observeFilters.observe(viewLifecycleOwner) {
            binding.containerFilter.isVisible = it is DataState.Data
            if (it is DataState.Data) {
                updateCategoriesFilter(it.data.availableCategories ?: return@observe)
            }
        }

        setFragmentResultListener(FiltersFragment.KEY_APPLY_FILTER) { _, bundle ->
            val filters =
                bundle.getParcelable<ProductListingRequest>(FiltersFragment.KEY_APPLY_FILTER)

            if (filters != null) {
                filtersViewModel.appliedFilters = filters
                filtersViewModel.notifyFilters()
                updateProductListingPagingData(true)
            }
        }
    }

    private fun fetchFilters() {
        filtersViewModel.fetchFilters(FiltersType.VENDOR, null, args.id)
    }

    private fun updateCategoriesFilter(categories: List<Category>) {
        binding.filtersVendorOrCategories.removeAllViews()
        categories.forEach { category ->
            if (category.name.isNotEmpty()) {
                val checkBox = makeCheckBox(category.id, category.name)
                checkBox.isChecked =
                    filtersViewModel.appliedFilters.categoryIds.contains(category.id)
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        filtersViewModel.appliedFilters.categoryIds.add(category.id)
                    } else {
                        filtersViewModel.appliedFilters.categoryIds.remove(category.id)
                    }

                    updateProductListingPagingData(true)
                }
                binding.filtersVendorOrCategories.addView(checkBox)
            }
        }
    }

    private fun makeCheckBox(id: Int, text: String): CheckBox {
        val checkButton =
            layoutInflater.inflate(
                R.layout.layout_chip_type_check_box, binding.filtersVendorOrCategories, false
            ) as CheckBox

        checkButton.id = id
        checkButton.text = text

        return checkButton
    }

    private fun updateUi(celebrity: Vendor) {
        binding.apply {
            profileImg.load(celebrity.templatePhoto)
            celebrity.template
                ?.firstOrNull { it.section == "topBanner" }
                ?.images?.firstOrNull()?.url?.let { headerImg.load(it) }
            tvProductListingTitle.text = celebrity.name
            name.text = celebrity.name
            type.text = celebrity.role.name
            address.text = celebrity.businessInfo.address
            Social.addAllTo(
                celebrity.socials, binding.containerSocialMediaFollow, binding.labelNoSocialLink
            )
            isFollowing = args.isFollow
        }
    }

    private fun setupPagerAdapter() {
        binding.rvProductList.adapter = productListingPagerAdapter
        val fav = celebrityViewModel.getFav()
        productListingPagerAdapter.submitFav(fav)

        updateProductListingPagingData()

        viewLifecycleOwner.lifecycleScope.launch {
            productListingPagerAdapter.loadStateFlow.collectLatest {
                val state = it.refresh

                if (state is LoadState.Error) {
                    showErrorToast(state)
                }
            }
        }
    }

    private fun updateProductListingPagingData(refresh: Boolean = false) {
        viewLifecycleOwner.lifecycleScope.launch {
            celebrityViewModel.filters = filtersViewModel.appliedFilters
            celebrityViewModel.filters.filterType = FiltersType.VENDOR

            celebrityViewModel.vendorProductListing(args.id, refresh) {
                setProductItemCount(it.totalRows)
            }.collectLatest { pagingData ->
                productListingPagerAdapter.submitData(pagingData)
            }
        }
    }

    private fun setProductItemCount(count: Int) {
        celebrityViewModel.updateItemCount(count)
    }

    override fun getTvCartCount(): TextView = binding.tvCartCount

    override fun getIvCart(): View = binding.ivCart
}