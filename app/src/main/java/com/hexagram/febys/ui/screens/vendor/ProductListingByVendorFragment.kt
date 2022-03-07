package com.hexagram.febys.ui.screens.vendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
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
import com.hexagram.febys.databinding.FragmentProductListingByVendorBinding
import com.hexagram.febys.models.api.category.Category
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.request.ProductListingRequest
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.product.filters.FilterViewModel
import com.hexagram.febys.ui.screens.product.filters.FiltersFragment
import com.hexagram.febys.ui.screens.product.filters.FiltersType
import com.hexagram.febys.ui.screens.product.listing.ProductListingPagerAdapter
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductListingByVendorFragment : BaseFragment() {
    private lateinit var binding: FragmentProductListingByVendorBinding
    private val vendorViewModel: VendorViewModel by viewModels()
    private val filtersViewModel by viewModels<FilterViewModel>()
    private val args: ProductListingByVendorFragmentArgs by navArgs()

    private val productListingPagerAdapter = ProductListingPagerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductListingByVendorBinding.inflate(inflater, container, false)
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
            adapter = this@ProductListingByVendorFragment.productListingPagerAdapter
        }

        binding.tvVendorName.text = args.title
        binding.tvVendorType.text = args.type
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

        binding.tvVendorName.setOnClickListener {
            gotoVendorDetail(args.id)
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
                        vendorViewModel.toggleFav(skuId)
                    } else {
                        val navigateToLogin = NavGraphDirections.actionToLoginFragment()
                        navigateTo(navigateToLogin)
                    }
                }
            }
        }
    }

    private fun setObserver() {
        setupPagerAdapter()

        vendorViewModel.observeItemCount.observe(viewLifecycleOwner) {
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

    private fun setupPagerAdapter() {
        binding.rvProductList.adapter = productListingPagerAdapter
        val fav = vendorViewModel.getFav()
        productListingPagerAdapter.submitFav(fav)

        updateProductListingPagingData()

        viewLifecycleOwner.lifecycleScope.launch {
            productListingPagerAdapter.loadStateFlow.collectLatest {
                val state = it.refresh
                if (state is LoadState.Loading) {
                    showLoader()
                } else {
                    hideLoader()
                }

                if (state is LoadState.Error) {
                    showErrorToast(state)
                }
                binding.emptyView.root.isVisible =
                    it.refresh is LoadState.NotLoading && productListingPagerAdapter.itemCount < 1
            }
        }
    }

    private fun updateProductListingPagingData(refresh: Boolean = false) {
        viewLifecycleOwner.lifecycleScope.launch {
            vendorViewModel.filters = filtersViewModel.appliedFilters
            vendorViewModel.filters.filterType = FiltersType.VENDOR

            vendorViewModel.vendorProductListing(args.id, refresh) {
                setProductItemCount(it.totalRows)
            }.collectLatest { pagingData ->
                productListingPagerAdapter.submitData(pagingData)
            }
        }
    }

    private fun setProductItemCount(count: Int) {
        vendorViewModel.updateItemCount(count)
    }

    private fun gotoVendorDetail(vendorId: String) {
        val direction = NavGraphDirections
            .toVendorDetailFragment(vendorId, args.isFollow)
        navigateTo(direction)
    }

    override fun getTvCartCount() = binding.tvCartCount
    override fun getIvCart() = binding.ivCart
}