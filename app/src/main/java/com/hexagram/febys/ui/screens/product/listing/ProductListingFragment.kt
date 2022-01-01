package com.hexagram.febys.ui.screens.product.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentProductListingBinding
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.request.ProductListingRequest
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.product.filters.FilterViewModel
import com.hexagram.febys.ui.screens.product.filters.FiltersFragment
import com.hexagram.febys.ui.screens.product.filters.FiltersType
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
abstract class ProductListingFragment : BaseFragment() {
    protected lateinit var binding: FragmentProductListingBinding
    protected val productListingViewModel: ProductListingViewModel by viewModels()
    private val filtersViewModel by viewModels<FilterViewModel>()

    private val productListingPagerAdapter = ProductListingPagerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListeners()
        setObserver()
        setupPagerAdapter()
        fetchFilters()
    }

    private fun initUi() {
        binding.productListingTitle = getListingTitle()

        binding.rvProductList.apply {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
            layoutManager = GridLayoutManager(context, 2)
            adapter = this@ProductListingFragment.productListingPagerAdapter
        }
    }

    private fun uiListeners() {
        binding.ivBack.setOnClickListener {
            goBack()
        }

        binding.btnRefine.setOnClickListener {
            if (filtersViewModel.filters != null) {
                val gotoRefineProduct = NavGraphDirections
                    .toProductFilterFragment(
                        filtersViewModel.filters!!, productListingViewModel.filters
                    )
                navigateTo(gotoRefineProduct)
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
                        productListingViewModel.toggleFav(skuId)
                    } else {
                        val navigateToLogin = NavGraphDirections.actionToLoginFragment()
                        navigateTo(navigateToLogin)
                    }
                }
            }
        }
    }

    private fun setObserver() {
        filtersViewModel.observeFilters.observe(viewLifecycleOwner) {
            binding.containerFilter.isVisible = it is DataState.Data
        }

        productListingViewModel.observeItemCount.observe(viewLifecycleOwner) {
            binding.tvProductListingCount.text = if (it == 0) {
                resources.getString(R.string.label_no_item)
            } else {
                resources.getQuantityString(R.plurals.items_count, it, it)
            }
        }

        setFragmentResultListener(FiltersFragment.KEY_APPLY_FILTER) { _, bundle ->
            val filters =
                bundle.getParcelable<ProductListingRequest>(FiltersFragment.KEY_APPLY_FILTER)

            if (filters != null) {
                productListingViewModel.filters = filters
                updateProductListingPagingData(true)
            }
        }
    }

    private fun setupPagerAdapter() {
        binding.rvProductList.adapter = productListingPagerAdapter
        val fav = productListingViewModel.getFav()
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
                    showToast(getString(R.string.error_something_went_wrong))
                }
                binding.emptyView.root.isVisible =
                    it.refresh is LoadState.NotLoading && productListingPagerAdapter.itemCount < 1
            }
        }
    }

    private fun updateProductListingPagingData(refresh: Boolean = false) {
        viewLifecycleOwner.lifecycleScope.launch {
            getProductPagingData(refresh).collectLatest { pagingData ->
                productListingPagerAdapter.submitData(pagingData)
            }
        }
    }

    private fun fetchFilters() {
        filtersViewModel.appliedFilters.categoryId = getCategoryId()
        filtersViewModel.appliedFilters.vendorId = getVendorId()
        filtersViewModel.fetchFilters(getFilterType())
    }

    fun setProductItemCount(count: Int) {
        productListingViewModel.updateItemCount(count)
    }

    override fun getTvCartCount(): TextView = binding.tvCartCount
    override fun getIvCart(): View = binding.ivCart

    abstract fun getListingTitle(): String

    abstract fun getProductPagingData(refresh: Boolean): Flow<PagingData<Product>>

    abstract fun getFilterType(): FiltersType
    open fun getVendorId(): String? = null
    open fun getCategoryId(): Int? = null
}