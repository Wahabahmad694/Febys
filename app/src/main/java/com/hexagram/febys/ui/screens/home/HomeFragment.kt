package com.hexagram.febys.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hexagram.febys.R
import com.hexagram.febys.base.SliderFragment
import com.hexagram.febys.databinding.FragmentHomeBinding
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.Banner
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.network.response.SeasonalOffer
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.applySpaceItemDecoration
import com.hexagram.febys.utils.getHorizontalScrollPosition
import com.hexagram.febys.utils.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : SliderFragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    private val uniqueCategoryAdapter = UniqueCategoryAdapter()
    private val todayDealsAdapter = HomeProductsAdapter()
    private val featuredCategoryProductsAdapter = HomeProductsAdapter()
    private val trendingProductsAdapter = HomeProductsAdapter()
    private val storeYouFollowAdapter = HomeStoresAdapter()
    private val under100DollarsItemAdapter = HomeProductsAdapter()

    private var lastCheckedCategoryId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.fetchUniqueCategory()
        viewModel.fetchAllBanner()
        viewModel.fetchTodayDeals()
        viewModel.fetchFeaturedCategories()
        viewModel.fetchAllSeasonalOffers()
        viewModel.fetchTrendingProducts()
        viewModel.fetchStoresYouFollow()
        viewModel.fetchUnder100DollarsItems()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        savedInstanceState?.let {
            binding.scrollViewHome.y = it.getFloat(KEY_HOME_SCROLL_POSITION, 0f)
        }
        savedInstanceState?.let {
            lastCheckedCategoryId = it.getInt(KEY_LAST_CHECKED_FEATURED_CATEGORY_ID, -1)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initUiListener()
        setupObserver()
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
        binding.rvTodayDeals.applySpaceItemDecoration(horizontalDimenRes = R.dimen._24dp)
        binding.rvTodayDeals.setHasFixedSize(true)
        binding.rvTodayDeals.adapter = todayDealsAdapter

        // featured category products
        binding.rvFeaturedCategoryProducts.applySpaceItemDecoration(horizontalDimenRes = R.dimen._24dp)
        binding.rvFeaturedCategoryProducts.setHasFixedSize(true)
        binding.rvFeaturedCategoryProducts.adapter = featuredCategoryProductsAdapter

        // store you follow
        binding.rvStoreYouFollow.applySpaceItemDecoration(horizontalDimenRes = R.dimen._24dp)
        binding.rvStoreYouFollow.setHasFixedSize(true)
        binding.rvStoreYouFollow.adapter = storeYouFollowAdapter

        // trending products
        binding.rvTrendingProducts.applySpaceItemDecoration(horizontalDimenRes = R.dimen._24dp)
        binding.rvTrendingProducts.setHasFixedSize(true)
        binding.rvTrendingProducts.adapter = trendingProductsAdapter

        // under 100$ items
        binding.rvUnder100DollarsItems.applySpaceItemDecoration(horizontalDimenRes = R.dimen._24dp)
        binding.rvUnder100DollarsItems.setHasFixedSize(true)
        binding.rvUnder100DollarsItems.adapter = under100DollarsItemAdapter
    }

    private fun initUiListener() {
        binding.btnShopNowTodayDeals.setOnClickListener {
            val gotoTodayDealsListingFragment = HomeFragmentDirections
                .actionHomeFragmentToTodayDealsListingFragment(getString(R.string.label_today_deals))
            navigateTo(gotoTodayDealsListingFragment)
        }

        binding.btnShopNowTrendingProducts.setOnClickListener {
            val gotoTodayDealsListingFragment = HomeFragmentDirections
                .actionHomeFragmentToTrendingProductListingFragment(getString(R.string.label_trending_products))
            navigateTo(gotoTodayDealsListingFragment)
        }

        binding.btnShopNowUnder100DollarsItems.setOnClickListener {
            val gotoTodayDealsListingFragment = HomeFragmentDirections
                .actionHomeFragmentToUnder100DollarsItemListingFragment(getString(R.string.label_under_100_dollar_items))
            navigateTo(gotoTodayDealsListingFragment)
        }

        binding.btnShopNowFeaturedCategories.setOnClickListener {
            var categoryTitle = getString(R.string.label_featured_categories)
            binding.radioGroupFeaturedCategories.children.forEach {
                if (it.id == binding.radioGroupFeaturedCategories.checkedRadioButtonId) {
                    categoryTitle = (it as RadioButton).text.toString()
                    return@forEach
                }
            }

            val gotoCategoryListing = HomeFragmentDirections
                .actionHomeFragmentToCategoryProductListingFragment(categoryTitle)
            navigateTo(gotoCategoryListing)
        }
    }

    private fun setupObserver() {
        // unique category
        viewModel.observeUniqueCategories.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
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
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    val sliderImages = it.data.filter { banner -> banner.type == "headerImages" }
                    binding.imageSliderHome.adapter = HomeSliderPageAdapter(sliderImages, this)
                    binding.dotsIndicator.setViewPager2(binding.imageSliderHome)
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
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    val featuredCategories = it.data
                    featuredCategories.forEach { category ->
                        val radioButton = makeRadioButton(category.id, category.name)
                        binding.radioGroupFeaturedCategories.addView(radioButton)
                    }

                    binding.radioGroupFeaturedCategories.setOnCheckedChangeListener { _, id ->
                        val products =
                            featuredCategories.find { category -> category.id == id }?.products
                        featuredCategoryProductsAdapter.submitList(products ?: emptyList())

                        lastCheckedCategoryId = id
                    }

                    // set auto select 1
                    if (lastCheckedCategoryId != -1) {
                        binding.radioGroupFeaturedCategories.check(lastCheckedCategoryId)
                    } else {
                        featuredCategories.firstOrNull()?.let { category ->
                            binding.radioGroupFeaturedCategories.check(category.id)
                        }
                    }
                }
            }
        }

        // seasonal offers
        viewModel.observeSeasonalOffers.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    binding.sliderSeasonalOffer.adapter =
                        HomeSeasonalOfferSliderPageAdapter(it.data, this)
                    binding.sliderSeasonalOfferDotsIndicator.setViewPager2(binding.sliderSeasonalOffer)
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
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
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

    private fun makeRadioButton(id: Int, text: String): RadioButton {
        val radioButton =
            layoutInflater.inflate(
                R.layout.layout_chip_type_radio_btn, binding.radioGroupFeaturedCategories, false
            ) as RadioButton

        radioButton.id = id
        radioButton.text = text

        return radioButton
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
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    val products = it.data
                    adapter.submitList(products)
                }
            }
        }
    }

    override fun getSlider() =
        listOf(binding.imageSliderHome, binding.sliderSeasonalOffer)

    override fun getRotateInterval(): Long = 5000L

    private inner class HomeSliderPageAdapter(
        val banners: List<Banner>, fa: Fragment
    ) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = banners.size

        override fun createFragment(position: Int): Fragment =
            HomeSliderPageFragment.newInstance(banners[position])
    }

    private inner class HomeSeasonalOfferSliderPageAdapter(
        val offers: List<SeasonalOffer>, fa: Fragment
    ) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = offers.size

        override fun createFragment(position: Int): Fragment =
            HomeSeasonalOfferSliderPageFragment.newInstance(offers[position])
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putFloat(KEY_HOME_SCROLL_POSITION, binding.scrollViewHome.y)
        outState.putInt(KEY_LAST_CHECKED_FEATURED_CATEGORY_ID, lastCheckedCategoryId)
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val KEY_HOME_SCROLL_POSITION = "scrollPosition"
        const val KEY_LAST_CHECKED_FEATURED_CATEGORY_ID = "lastCheckedFeaturedCategoryId"
    }
}