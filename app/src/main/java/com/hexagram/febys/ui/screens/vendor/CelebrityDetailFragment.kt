package com.hexagram.febys.ui.screens.vendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.hexagram.febys.network.response.Product
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
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener {
            goBack()
        }

        binding.btnRefine.setOnClickListener {
            // goto filter screen
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

    fun setProductItemCount(count: Int) {
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