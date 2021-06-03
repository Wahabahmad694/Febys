package com.android.febys.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.android.febys.R
import com.android.febys.databinding.FragmentHomeBinding
import com.android.febys.network.domain.models.Product
import com.android.febys.base.BaseFragment
import com.android.febys.network.DataState
import com.android.febys.utils.*
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    private val uniqueCategoryAdapter = UniqueCategoryAdapter()
    private val todayDealsAdapter = HomeProductsAdapter()
    private val featuredCategoryProductsAdapter = HomeProductsAdapter()
    private val trendingProductsAdapter = HomeProductsAdapter()
    private val storeYouFollowAdapter = HomeStoresAdapter()
    private val under100DollarsItemAdapter = HomeProductsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        setupObserver()

        viewModel.fetchUniqueCategory()
        viewModel.fetchAllBanner()
        viewModel.fetchTodayDeals()
        viewModel.fetchFeaturedCategories()
        viewModel.fetchFeaturedCategoryProducts()
        viewModel.fetchAllSeasonalOffers()
        viewModel.fetchTrendingProducts()
        viewModel.fetchStoresYouFollow()
        viewModel.fetchUnder100DollarsItems()
    }

    private fun initUi() {
        // unique category
        binding.rvUniqueCategories.adapter = uniqueCategoryAdapter
        // custom scroll bar position
        binding.rvUniqueCategories.setOnScrollChangeListener { _, _, _, _, _ ->
            val horizontalScrollPosition =
                binding.rvUniqueCategories.getHorizontalScrollPosition()
            val param =
                (binding.ivIcScrollUniqueCategory.layoutParams as ConstraintLayout.LayoutParams)
            param.horizontalBias = horizontalScrollPosition
            binding.ivIcScrollUniqueCategory.layoutParams = param
        }

        // today deals
        binding.rvTodayDeals.applySpaceItemDecoration(horizontalDimenRes = R.dimen.margin_large)
        binding.rvTodayDeals.setHasFixedSize(true)
        binding.rvTodayDeals.adapter = todayDealsAdapter

        // featured category products
        binding.rvFeaturedCategoryProducts.applySpaceItemDecoration(horizontalDimenRes = R.dimen.margin_large)
        binding.rvFeaturedCategoryProducts.setHasFixedSize(true)
        binding.rvFeaturedCategoryProducts.adapter = featuredCategoryProductsAdapter

        // store you follow
        binding.rvStoreYouFollow.applySpaceItemDecoration(horizontalDimenRes = R.dimen.margin_large)
        binding.rvStoreYouFollow.setHasFixedSize(true)
        binding.rvStoreYouFollow.adapter = storeYouFollowAdapter

        // trending products
        binding.rvTrendingProducts.applySpaceItemDecoration(horizontalDimenRes = R.dimen.margin_large)
        binding.rvTrendingProducts.setHasFixedSize(true)
        binding.rvTrendingProducts.adapter = trendingProductsAdapter

        // under 100$ items
        binding.rvUnder100DollarsItems.applySpaceItemDecoration(horizontalDimenRes = R.dimen.margin_large)
        binding.rvUnder100DollarsItems.setHasFixedSize(true)
        binding.rvUnder100DollarsItems.adapter = under100DollarsItemAdapter
    }

    private fun setupObserver() {
        // unique category
        viewModel.observeUniqueCategories.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {
                    val error = getErrorMessage(it)
                    showToast(error)
                }
                is DataState.Data -> {
                    val uniqueCategories = it.data
                    uniqueCategoryAdapter.submitList(uniqueCategories)
                }
            }
        }

        // slider
        viewModel.observeSliderImages.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {
                    val error = getErrorMessage(it)
                    showToast(error)
                }
                is DataState.Data -> {
                    val sliderImages = it.data
                    val adapter = SliderHomeAdapter(sliderImages, true)
                    binding.imageSliderHome.adapter = adapter
                }
            }
        }

        //today deals
        observeAndSubmitProductList(
            todayDealsAdapter,
            viewModel.observeTodayDeals
        )

        // featured categories
        viewModel.observeFeaturedCategories.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {

                }
                is DataState.Data -> {
                    val featuredCategories = it.data
                    featuredCategories.forEach { category ->
                        val chip = makeChip(category.id, category.name)
                        binding.chipGroupFeaturedCategories.addView(chip)
                    }
                }
            }
        }

        // featured category products
        observeAndSubmitProductList(
            featuredCategoryProductsAdapter,
            viewModel.observeFeaturedCategoryProducts
        )

        // seasonal offers
        viewModel.observeSeasonalOffers.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {
                    val error = getErrorMessage(it)
                    showToast(error)
                }
                is DataState.Data -> {
                    binding.imageSliderHome.show()
                    val seasonalOffers = it.data
                    // todo update seasonal offer ui
                }
            }
        }

        // trending products
        observeAndSubmitProductList(
            trendingProductsAdapter,
            viewModel.observeTrendingProducts
        )

        // store you follow
        viewModel.observeStoreYouFollow.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {

                }
                is DataState.Data -> {
                    val storesList = it.data
                    storeYouFollowAdapter.submitList(storesList)
                }
            }
        }

        // under $100 items
        observeAndSubmitProductList(
            under100DollarsItemAdapter,
            viewModel.observeUnder100DollarsItems
        )
    }

    private fun makeChip(id: Int, text: String): Chip {
        val chip =
            layoutInflater.inflate(
                R.layout.item_featured_category, binding.chipGroupFeaturedCategories, false
            ) as Chip

        chip.id = id
        chip.text = text

        return chip
    }

    private fun observeAndSubmitProductList(
        adapter: HomeProductsAdapter,
        observable: LiveData<DataState<List<Product>>>
    ) {
        observable.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {

                }
                is DataState.Data -> {
                    val products = it.data
                    adapter.submitList(products)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        binding.imageSliderHome.resumeAutoScroll()
    }

    override fun onPause() {
        binding.imageSliderHome.pauseAutoScroll()
        super.onPause()
    }
}