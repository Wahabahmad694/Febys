package com.hexagram.febys.ui.screens.vendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.ui.screens.product.listing.ProductListingPagerAdapter
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductListingByVendorFragment : BaseFragment() {
    private lateinit var binding: FragmentProductListingByVendorBinding
    private val celebrityViewModel: VendorViewModel by viewModels()
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
            // goto filter screen
        }

        binding.tvVendorName.setOnClickListener {
            gotoVendorDetail(args.id)
        }

        productListingPagerAdapter.interaction = object : ProductListingPagerAdapter.Interaction {
            override fun onItemSelected(position: Int, item: Product) {
                val gotoProductDetail = NavGraphDirections.actionToProductDetail(item.id)
                navigateTo(gotoProductDetail)
            }

            override fun toggleFavIfUserLoggedIn(variantId: Int): Boolean {
                return isUserLoggedIn.also {
                    if (it) {
                        celebrityViewModel.toggleFav(variantId)
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
    }

    private fun setupPagerAdapter() {
        binding.rvProductList.adapter = productListingPagerAdapter
        val fav = celebrityViewModel.getFav()
        productListingPagerAdapter.submitFav(fav)

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
                if (state is LoadState.Loading) {
                    showLoader()
                } else {
                    hideLoader()
                }

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

    private fun gotoVendorDetail(vendorId: Int) {
        val direction = ProductListingByVendorFragmentDirections
            .actionProductListingByVendorFragmentToVendorDetailFragment(vendorId)
        navigateTo(direction)
    }

    override fun getTvCartCount() = binding.tvCartCount
    override fun getIvCart() = binding.ivCart
}