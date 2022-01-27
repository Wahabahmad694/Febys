package com.hexagram.febys.ui.screens.product.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
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
import com.hexagram.febys.models.api.category.Category
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.request.ProductListingRequest
import com.hexagram.febys.models.api.vendor.Vendor
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
                        filtersViewModel.filters!!, filtersViewModel.appliedFilters
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
            if (it is DataState.Data) {
                if (getFilterType() == FiltersType.CATEGORY) {
                    updateVendorFilters(it.data.vendors.vendors)
                } else {
                    updateCategoriesFilter(it.data.availableCategories ?: return@observe)
                }
            }
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
                filtersViewModel.appliedFilters = filters
                filtersViewModel.notifyFilters()
                updateProductListingPagingData(true)
            }
        }
    }


    private fun updateVendorFilters(vendors: List<Vendor>) {
        binding.filtersVendorOrCategories.removeAllViews()
        vendors.forEach { vendor ->
            if (vendor.name.isNotEmpty()) {
                val checkBox = makeCheckBox(vendor._id.toAscii(), vendor.name)
                checkBox.isChecked =
                    filtersViewModel.appliedFilters.vendorIds.contains(vendor._id)
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        filtersViewModel.appliedFilters.vendorIds.add(vendor._id)
                    } else {
                        filtersViewModel.appliedFilters.vendorIds.remove(vendor._id)
                    }

                    updateProductListingPagingData(true)
                }
                binding.filtersVendorOrCategories.addView(checkBox)
            }
        }
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
                    showErrorToast(state)
                }
                binding.emptyView.root.isVisible =
                    it.refresh is LoadState.NotLoading && productListingPagerAdapter.itemCount < 1
            }
        }
    }

    private fun updateProductListingPagingData(refresh: Boolean = false) {
        viewLifecycleOwner.lifecycleScope.launch {
            productListingViewModel.filters = filtersViewModel.appliedFilters
            productListingViewModel.filters.filterType = getFilterType()

            getProductPagingData(refresh).collectLatest { pagingData ->
                productListingPagerAdapter.submitData(pagingData)
            }
        }
    }

    private fun fetchFilters() {
        filtersViewModel.fetchFilters(
            getFilterType(),
            getCategoryId(),
            getVendorId(),
            getSearchStr()
        )
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
    open fun getSearchStr(): String? = null
}