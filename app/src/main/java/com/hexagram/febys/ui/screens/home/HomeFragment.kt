package com.hexagram.febys.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.SliderFragment
import com.hexagram.febys.databinding.FragmentHomeBinding
import com.hexagram.febys.models.api.banners.Banner
import com.hexagram.febys.models.api.category.UniqueCategory
import com.hexagram.febys.models.api.product.FeaturedCategory
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.SeasonalOffer
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
                /*newChanges val gotoProductDetail = NavGraphDirections.actionToProductDetail(item._id)
                navigateTo(gotoProductDetail)*/
            }

            override fun onFavToggleClick(skuId: String) {
                if (isUserLoggedIn) {
                    homeViewModel.toggleFav(skuId)
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

    private fun setupUniqueCategory(uniqueCategories: List<UniqueCategory>) {
        uniqueCategoryAdapter.submitList(uniqueCategories)

        val isVisible = uniqueCategories.isNotEmpty()
        isVisible.applyToViews(
            binding.ivIcScrollUniqueCategory,
            binding.ivBgScrollUniqueCategory,
            binding.tvUniqueCategories,
            binding.rvUniqueCategories
        )
    }

    private fun setupBanner(banners: List<Banner>) {
        val sliderImages =
            banners.filter { banner -> banner.type == "headerImages" && banner._for == "mobile" }
        binding.imageSliderHome.adapter = PagerAdapter(sliderImages, this)
        binding.dotsIndicator.setViewPager2(binding.imageSliderHome)

        val isVisible = sliderImages.isNotEmpty()
        isVisible.applyToViews(binding.imageSliderHome, binding.dotsIndicator)
    }

    private fun setupTodayDeals(todayDeals: List<Product>) {
        todayDealsAdapter.submitList(todayDeals)

        val isVisible = todayDeals.isNotEmpty()
        isVisible.applyToViews(
            binding.tvTodayDeals,
            binding.tvTodayDealsSlogan,
            binding.rvTodayDeals,
            binding.btnShopNowTodayDeals
        )
    }

    private fun setupFeaturedCategories(featuredCategories: List<FeaturedCategory>) {
        featuredCategories.forEach { category ->
            if (category.products.isNotEmpty()) {
                val radioButton = makeRadioButton(category.id, category.name)
                binding.radioGroupFeaturedCategories.addView(radioButton)
            }
        }

        binding.radioGroupFeaturedCategories.setOnCheckedChangeListener { _, id ->
            val products = featuredCategories.find { category -> category.id == id }?.products
            featuredCategoryProductsAdapter.submitList(products)

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
        isVisible.applyToViews(
            binding.tvFeaturedCategories,
            binding.tvFeaturedCategoriesSlogan,
            binding.rvFeaturedCategoryProducts,
            binding.btnShopNowFeaturedCategories
        )
    }

    private fun setupSeasonalOffers(seasonalOffers: List<SeasonalOffer>) {
        binding.sliderSeasonalOffer.adapter = PagerAdapter(seasonalOffers, this)
        binding.sliderSeasonalOfferDotsIndicator.setViewPager2(binding.sliderSeasonalOffer)

        val isVisible = seasonalOffers.isNotEmpty()
        isVisible.applyToViews(
            binding.tvSeasonalOffers,
            binding.sliderSeasonalOffer,
            binding.sliderSeasonalOfferDotsIndicator
        )
    }

    private fun setupTrendingProducts(trendingProducts: List<Product>) {
        trendingProductsAdapter.submitList(trendingProducts)

        val isVisible = trendingProducts.isNotEmpty()
        isVisible.applyToViews(
            binding.tvTrendingProducts,
            binding.tvTrendingProductsSlogan,
            binding.rvTrendingProducts,
            binding.btnShopNowTrendingProducts
        )
    }

    private fun setupStoreYouFollow(storeYouFollow: List<String>) {
        storeYouFollowAdapter.submitList(storeYouFollow)

        val isVisible = storeYouFollow.isNotEmpty()
        isVisible.applyToViews(
            binding.tvStoreYouFollow,
            binding.tvStoreYouFollowSlogan,
            binding.rvStoreYouFollow,
            binding.btnShopNowStoreYouFollow
        )
    }

    private fun setupUnder100DollarsItems(under100DollarsItems: List<Product>) {
        under100DollarsItemAdapter.submitList(under100DollarsItems)

        val isVisible = under100DollarsItems.isNotEmpty()
        isVisible.applyToViews(
            binding.tvLabelUnder100DollarsItems,
            binding.tvLabelUnder100DollarsItemsSlogan,
            binding.rvUnder100DollarsItems,
            binding.btnShopNowUnder100DollarsItems
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

    override fun getSlider() =
        listOf(binding.imageSliderHome, binding.sliderSeasonalOffer)

    override fun getRotateInterval(): Long = 5000L

    override fun getTvCartCount(): TextView = binding.tvCartCount
    override fun getIvCart(): View = binding.ivCart

    private inner class PagerAdapter<out T>(
        val list: List<T>, fa: Fragment
    ) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = list.size

        override fun createFragment(position: Int): Fragment {
            val item = list[position]
            return if (item is Banner) {
                HomeSliderPageFragment.newInstance(item)
            } else {
                HomeSeasonalOfferSliderPageFragment.newInstance(item as SeasonalOffer)
            }
        }
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