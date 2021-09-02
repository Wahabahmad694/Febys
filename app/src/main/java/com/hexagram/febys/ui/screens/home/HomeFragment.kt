package com.hexagram.febys.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.SliderFragment
import com.hexagram.febys.databinding.FragmentHomeBinding
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.*
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : SliderFragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private val uniqueCategoryAdapter = UniqueCategoryAdapter()
    private val todayDealsAdapter = HomeProductsAdapter()
    private val featuredCategoryProductsAdapter = HomeProductsAdapter()
    private val trendingProductsAdapter = HomeProductsAdapter()
    private val storeYouFollowAdapter = HomeStoresAdapter()
    private val under100DollarsItemAdapter = HomeProductsAdapter()

    private var lastCheckedCategoryId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeViewModel.fetchHomeModel()
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
            val categoryId = binding.radioGroupFeaturedCategories.checkedRadioButtonId
            binding.radioGroupFeaturedCategories.children.forEach {
                if (it.id == categoryId) {
                    categoryTitle = (it as RadioButton).text.toString()
                    return@forEach
                }
            }

            val gotoCategoryListing = HomeFragmentDirections
                .actionHomeFragmentToCategoryProductListingFragment(categoryTitle, categoryId)
            navigateTo(gotoCategoryListing)
        }

        fun updateFav() {
            val fav = homeViewModel.getFav()
            todayDealsAdapter.submitFav(fav)
            featuredCategoryProductsAdapter.submitFav(fav)
            trendingProductsAdapter.submitFav(fav)
            under100DollarsItemAdapter.submitFav(fav)
        }

        updateFav()

        val homeProductAdapterInteraction = object : HomeProductsAdapter.Interaction {
            override fun onItemSelected(position: Int, item: Product) {
                val gotoProductDetail = NavGraphDirections.actionToProductDetail(item.id)
                navigateTo(gotoProductDetail)
            }

            override fun onFavToggleClick(variantId: Int) {
                if (isUserLoggedIn) {
                    homeViewModel.toggleFav(variantId)
                    updateFav()
                } else {
                    val navigateToLogin = NavGraphDirections.actionToLoginFragment()
                    navigateTo(navigateToLogin)
                }
            }
        }

        todayDealsAdapter.interaction = homeProductAdapterInteraction
        featuredCategoryProductsAdapter.interaction = homeProductAdapterInteraction
        trendingProductsAdapter.interaction = homeProductAdapterInteraction
        under100DollarsItemAdapter.interaction = homeProductAdapterInteraction
    }

    private fun setupObserver() {
        homeViewModel.observeHomeModel.observe(viewLifecycleOwner) {
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
                    val homeModel = it.data

                    setupUniqueCategory(homeModel.uniqueCategories)
                    setupBanner(homeModel.banners)
                    setupTodayDeals(homeModel.todayDeals)
                    setupFeaturedCategories(homeModel.featuredCategories)
                    setupSeasonalOffers(homeModel.seasonalOffers)
                    setupTrendingProducts(homeModel.trendingProducts)
                    setupStoreYouFollow(homeModel.storeYouFollow)
                    setupUnder100DollarsItems(homeModel.under100DollarsItems)
                }
            }
        }
    }

    private fun setupTodayDeals(todayDeals: List<Product>) {
        todayDealsAdapter.submitList(todayDeals)

        val isVisible = todayDeals.isNotEmpty()
        binding.tvTodayDeals.isVisible = isVisible
        binding.tvTodayDealsSlogan.isVisible = isVisible
        binding.rvTodayDeals.isVisible = isVisible
        binding.btnShopNowTodayDeals.isVisible = isVisible
    }

    private fun setupFeaturedCategories(featuredCategories: List<Category>) {
        featuredCategories.forEach { category ->
            if (category.products.isNotEmpty()) {
                val radioButton = makeRadioButton(category.id, category.name)
                binding.radioGroupFeaturedCategories.addView(radioButton)
            }
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
            featuredCategories.firstOrNull { it.products.isNotEmpty() }?.let { category ->
                binding.radioGroupFeaturedCategories.check(category.id)
            }
        }

        val isVisible = binding.radioGroupFeaturedCategories.childCount > 0
        binding.tvFeaturedCategories.isVisible = isVisible
        binding.tvFeaturedCategoriesSlogan.isVisible = isVisible
        binding.rvFeaturedCategoryProducts.isVisible = isVisible
        binding.btnShopNowFeaturedCategories.isVisible = isVisible
    }

    private fun setupUniqueCategory(uniqueCategories: List<UniqueCategory>) {
        uniqueCategoryAdapter.submitList(uniqueCategories)

        val isVisible = uniqueCategories.isNotEmpty()
        binding.ivIcScrollUniqueCategory.isVisible = isVisible
        binding.ivBgScrollUniqueCategory.isVisible = isVisible
        binding.tvUniqueCategories.isVisible = isVisible
        binding.rvUniqueCategories.isVisible = isVisible
    }

    private fun setupBanner(banners: List<Banner>) {
        val sliderImages =
            banners.filter { banner -> banner.type == "headerImages" }
        binding.imageSliderHome.adapter = HomeSliderPageAdapter(sliderImages, this)
        binding.dotsIndicator.setViewPager2(binding.imageSliderHome)

        val isVisible = sliderImages.isNotEmpty()
        binding.imageSliderHome.isVisible = isVisible
        binding.dotsIndicator.isVisible = isVisible
    }

    private fun setupSeasonalOffers(seasonalOffers: List<SeasonalOffer>) {
        binding.sliderSeasonalOffer.adapter =
            HomeSeasonalOfferSliderPageAdapter(seasonalOffers, this)
        binding.sliderSeasonalOfferDotsIndicator.setViewPager2(binding.sliderSeasonalOffer)

        val isVisible = seasonalOffers.isNotEmpty()
        binding.tvSeasonalOffers.isVisible = isVisible
        binding.sliderSeasonalOffer.isVisible = isVisible
        binding.sliderSeasonalOfferDotsIndicator.isVisible = isVisible
    }

    private fun setupTrendingProducts(trendingProducts: List<Product>) {
        trendingProductsAdapter.submitList(trendingProducts)

        val isVisible = trendingProducts.isNotEmpty()
        binding.tvTrendingProducts.isVisible = isVisible
        binding.tvTrendingProductsSlogan.isVisible = isVisible
        binding.rvTrendingProducts.isVisible = isVisible
        binding.btnShopNowTrendingProducts.isVisible = isVisible
    }

    private fun setupStoreYouFollow(storeYouFollow: List<String>) {
        storeYouFollowAdapter.submitList(storeYouFollow)

        val isVisible = storeYouFollow.isNotEmpty()
        binding.tvStoreYouFollow.isVisible = isVisible
        binding.tvStoreYouFollowSlogan.isVisible = isVisible
        binding.rvStoreYouFollow.isVisible = isVisible
        binding.btnShopNowStoreYouFollow.isVisible = isVisible
    }

    private fun setupUnder100DollarsItems(under100DollarsItems: List<Product>) {
        under100DollarsItemAdapter.submitList(under100DollarsItems)

        val isVisible = under100DollarsItems.isNotEmpty()
        binding.tvLabelUnder100DollarsItems.isVisible = isVisible
        binding.tvLabelUnder100DollarsItemsSlogan.isVisible = isVisible
        binding.rvUnder100DollarsItems.isVisible = isVisible
        binding.btnShopNowUnder100DollarsItems.isVisible = isVisible
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

    override fun getSlider() =
        listOf(binding.imageSliderHome, binding.sliderSeasonalOffer)

    override fun getRotateInterval(): Long = 5000L

    override fun getTvCartCount(): TextView = binding.tvCartCount
    override fun getIvCart(): View = binding.ivCart

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