package com.hexagram.febys.ui.screens.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.facebook.drawee.backends.pipeline.Fresco
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.SliderFragment
import com.hexagram.febys.databinding.FragmentHomeBinding
import com.hexagram.febys.models.api.banners.Banner
import com.hexagram.febys.models.api.category.UniqueCategory
import com.hexagram.febys.models.api.product.FeaturedCategory
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.vendor.Vendor
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.Offer
import com.hexagram.febys.network.response.SeasonalOffer
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.*
import com.zendesk.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zendesk.answerbot.AnswerBot
import zendesk.answerbot.AnswerBotEngine
import zendesk.chat.Chat
import zendesk.chat.ChatEngine
import zendesk.core.AnonymousIdentity
import zendesk.core.Identity
import zendesk.core.Zendesk
import zendesk.messaging.MessagingActivity
import zendesk.support.Guide
import zendesk.support.Support
import zendesk.support.SupportEngine


@AndroidEntryPoint
class HomeFragment : SliderFragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private val uniqueCategoryAdapter = UniqueCategoryAdapter()
    private val todayDealsAdapter = HomeProductsAdapter()
    private val featuredCategoryProductsAdapter = HomeProductsAdapter()
    private val featuredStoreListingAdapter = FeaturedStoreListingAdapter()
    private val trendingProductsAdapter = HomeProductsAdapter()
    private val storeYouFollowAdapter = HomeProductsAdapter()
    private val under100DollarsItemAdapter = HomeProductsAdapter()
    private val editorsPickItemAdapter = HomeProductsAdapter()

    private var lastCheckedCategoryId = -1
    private var lastCheckedFeatureStoreId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeViewModel.fetchHomeModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        savedInstanceState?.let {
            lastCheckedCategoryId = it.getInt(KEY_LAST_CHECKED_FEATURED_CATEGORY_ID, -1)
        }
        savedInstanceState?.let {
            lastCheckedFeatureStoreId = it.getInt(KEY_LAST_CHECKED_FEATURED_STORE_ID, -1)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initUiListener()
        setupObserver()
        setFloatingButton()
    }

    private fun setFloatingButton() {
        val uri = "res:///${R.drawable.gif_zendesk_chat}"
        val draweeViewBuilder = Fresco.newDraweeControllerBuilder()
        draweeViewBuilder.setUri(uri)
        draweeViewBuilder.autoPlayAnimations = true

        binding.ivFabChat.controller = draweeViewBuilder.build()
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

        // featured stores
        binding.rvFeaturedStores.applySpaceItemDecoration(horizontalDimenRes = R.dimen._24dp)
        binding.rvFeaturedStores.setHasFixedSize(true)
        binding.rvFeaturedStores.adapter = featuredStoreListingAdapter


        // trending products
        binding.rvTrendingProducts.applySpaceItemDecoration(horizontalDimenRes = R.dimen._24dp)
        binding.rvTrendingProducts.setHasFixedSize(true)
        binding.rvTrendingProducts.adapter = trendingProductsAdapter

        // under 100$ items
        binding.rvUnder100DollarsItems.applySpaceItemDecoration(horizontalDimenRes = R.dimen._24dp)
        binding.rvUnder100DollarsItems.setHasFixedSize(true)
        binding.rvUnder100DollarsItems.adapter = under100DollarsItemAdapter

        // editors pick items
        binding.rvEditorsPickItems.applySpaceItemDecoration(horizontalDimenRes = R.dimen._24dp)
        binding.rvEditorsPickItems.setHasFixedSize(true)
        binding.rvEditorsPickItems.adapter = editorsPickItemAdapter

        // same day delivery items
        binding.rvSameDayDeliveryItems.applySpaceItemDecoration(horizontalDimenRes = R.dimen._24dp)
        binding.rvSameDayDeliveryItems.setHasFixedSize(true)
        binding.rvSameDayDeliveryItems.adapter = editorsPickItemAdapter
    }

    private fun initUiListener() {
        uniqueCategoryAdapter.interaction = object : UniqueCategoryAdapter.Interaction {
            override fun onItemSelected(position: Int, item: UniqueCategory) {
                gotoCategoryListing(item.name, item.categoryId)
            }
        }

        binding.btnShopNowTodayDeals.setOnClickListener {
            val gotoTodayDealsListingFragment = HomeFragmentDirections
                .actionHomeFragmentToTodayDealsListingFragment(getString(R.string.label_today_deals))
            navigateTo(gotoTodayDealsListingFragment)
        }

        binding.ivFabChat.setOnClickListener {
            if (isUserLoggedIn) {
                gotoChat()
            } else gotoLogin()
        }
        binding.ivWishList.setOnClickListener {
            if (isUserLoggedIn) {
                val gotoWishList = NavGraphDirections.toWishListFragment()
                navigateTo(gotoWishList)
            } else gotoLogin()
        }

        featuredStoreListingAdapter.gotoVendorDetail =
            { vendorId -> gotoVendorDetail(vendorId, false) }

        binding.btnShopNowTrendingProducts.setOnClickListener {
            val gotoTodayDealsListingFragment = HomeFragmentDirections
                .actionHomeFragmentToTrendingProductListingFragment(getString(R.string.label_trending_products))
            navigateTo(gotoTodayDealsListingFragment)
        }
        binding.btnShopNowEditorsPickItems.setOnClickListener {
            val gotoEditorPickListingFragment = HomeFragmentDirections
                .actionHomeFragmentToEditorsPickItemListingFragment(getString(R.string.label_editors_pick_items))
            navigateTo(gotoEditorPickListingFragment)
        }

        binding.btnSameDayDeliveryItems.setOnClickListener {
            val gotoSameDayDeliveryListingFragment = HomeFragmentDirections
                .actionHomeFragmentToEditorsPickItemListingFragment(getString(R.string.label_same_day_delivery_items))
            navigateTo(gotoSameDayDeliveryListingFragment)
        }

        binding.btnShopNowUnder100DollarsItems.setOnClickListener {
            val gotoTodayDealsListingFragment = HomeFragmentDirections
                .actionHomeFragmentToUnder100DollarsItemListingFragment(getString(R.string.label_under_100_dollar_items))
            navigateTo(gotoTodayDealsListingFragment)
        }
        binding.btnShopNowStoreYouFollow.setOnClickListener {
            val gotoStoreYouFollowListingFragment = HomeFragmentDirections
                .actionHomeFragmentToStoreYouFollowItemListingFragment(getString(R.string.label_store_you_follow))
            navigateTo(gotoStoreYouFollowListingFragment)
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

            gotoCategoryListing(categoryTitle, categoryId)
        }

        fun updateFav() {
            val fav = homeViewModel.getFav()
            todayDealsAdapter.submitFav(fav)
            featuredCategoryProductsAdapter.submitFav(fav)
            trendingProductsAdapter.submitFav(fav)
            storeYouFollowAdapter.submitFav(fav)
            under100DollarsItemAdapter.submitFav(fav)
            editorsPickItemAdapter.submitFav(fav)
        }

        updateFav()

        val homeProductAdapterInteraction = object : HomeProductsAdapter.Interaction {
            override fun onItemSelected(position: Int, item: Product) {
                val gotoProductDetail =
                    NavGraphDirections.actionToProductDetail(item._id, item.variants[0].skuId)
                navigateTo(gotoProductDetail)
            }

            override fun onFavToggleClick(skuId: String) {
                if (isUserLoggedIn) {
                    homeViewModel.toggleFav(skuId)
                    updateFav()
                    updateFavIcon()
                } else {
                    val navigateToLogin = NavGraphDirections.actionToLoginFragment()
                    navigateTo(navigateToLogin)
                }
            }
        }

        todayDealsAdapter.interaction = homeProductAdapterInteraction
        featuredCategoryProductsAdapter.interaction = homeProductAdapterInteraction
        storeYouFollowAdapter.interaction = homeProductAdapterInteraction
        trendingProductsAdapter.interaction = homeProductAdapterInteraction
        under100DollarsItemAdapter.interaction = homeProductAdapterInteraction
        editorsPickItemAdapter.interaction = homeProductAdapterInteraction
        binding.rvUniqueCategories.setOnScrollChangeListener { _, _, _, _, _ ->
            val horizontalScrollPosition =
                binding.rvUniqueCategories.getHorizontalScrollPosition()
            val param =
                (binding.ivIcScrollUniqueCategory.layoutParams as ConstraintLayout.LayoutParams)
            param.horizontalBias = horizontalScrollPosition
            binding.ivIcScrollUniqueCategory.layoutParams = param
            binding.ivIcScrollUniqueCategory.visibility =
                if (uniqueCategoryAdapter.itemCount >= 5) View.VISIBLE else View.GONE
            binding.ivBgScrollUniqueCategory.visibility =
                if (uniqueCategoryAdapter.itemCount >= 5) View.VISIBLE else View.GONE
        }
    }

    private fun gotoLogin() {
        val gotoLogin = NavGraphDirections.actionToLoginFragment()
        navigateTo(gotoLogin)
    }

    private fun gotoCategoryListing(categoryTitle: String, categoryId: Int) {
        val gotoCategoryListing = HomeFragmentDirections
            .actionHomeFragmentToCategoryProductListingFragment(categoryTitle, categoryId)
        navigateTo(gotoCategoryListing)
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
                    setupUnder100DollarsItems(homeModel.under100DollarsItems)
                    setupSameDayDeliveryItems(homeModel.sameDayDeliveryItems)
                    setupEditorsPickItem(homeModel.editorsPickItems)
//                    setupFeaturedStores(
//                        homeModel.featuredVendorStores,
//                        homeModel.featureCelebrityStores
//                    )
                }
            }
        }

        homeViewModel.observeStoreYouFollow.observe(viewLifecycleOwner) { setupStoreYouFollow(it) }
    }

    @SuppressLint("ResourceType")
    private fun setupFeaturedStores(
        featuredVendorStores: List<Vendor>,
        featureCelebrityStores: List<Vendor>
    ) {
        binding.radioGroupFeaturedStores.removeAllViews()
        val vendorButton = makeRadioButton(1, "Vendor")
        binding.radioGroupFeaturedStores.addView(vendorButton)

        val celebrityButton = makeRadioButton(2, "Celebrity")
        binding.radioGroupFeaturedStores.addView(celebrityButton)

        binding.radioGroupFeaturedStores.setOnCheckedChangeListener { _, id ->
            val stores = when (id) {
                1 -> featuredVendorStores
                else -> featureCelebrityStores
            }

            featuredStoreListingAdapter.submitList(stores)
            lastCheckedFeatureStoreId = id
        }

        // set auto select 1
        if (lastCheckedFeatureStoreId != -1) {
            binding.radioGroupFeaturedStores.check(lastCheckedFeatureStoreId)
        } else {
            binding.radioGroupFeaturedStores.check(1)
        }

        val isVisible = binding.radioGroupFeaturedStores.childCount > 0
        isVisible.applyToViews(
            binding.tvFeaturedStores,
            binding.tvFeaturedStoresSlogan,
            binding.rvFeaturedStores,
        )
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
        val pagerAdapter = PagerAdapter(this, sliderImages)
        binding.imageSliderHome.adapter = pagerAdapter
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
        val isVisible = seasonalOffers.isNotEmpty()
        isVisible.applyToViews(
            binding.tvSeasonalOffers,
            binding.sliderSeasonalOffer,
            binding.sliderSeasonalOfferDotsIndicator
        )

        if (seasonalOffers.isEmpty()) return

        binding.tvSeasonalOffersSlogan.text = seasonalOffers[0].name
        val pagerAdapter = PagerAdapter(this, seasonalOffers[0].offers, seasonalOffers[0].name)
        binding.sliderSeasonalOffer.adapter = pagerAdapter
        binding.sliderSeasonalOfferDotsIndicator.setViewPager2(binding.sliderSeasonalOffer)
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

    private fun setupEditorsPickItem(editorsPickItem: List<Product>) {
        editorsPickItemAdapter.submitList(editorsPickItem)

        val isVisible = editorsPickItem.isNotEmpty()
        isVisible.applyToViews(
            binding.tvLabelEditorPickItems,
            binding.tvLabelEditorPickItemsSlogan,
            binding.rvEditorsPickItems,
            binding.btnShopNowEditorsPickItems
        )
    }

    private fun setupSameDayDeliveryItems(sameDayDeliveryItem: List<Product>) {
        editorsPickItemAdapter.submitList(sameDayDeliveryItem)

        val isVisible = sameDayDeliveryItem.isNotEmpty()
        isVisible.applyToViews(
            binding.tvLabelSameDayDelivery,
            binding.tvLabelSameDayDeliverySlogan,
            binding.rvSameDayDeliveryItems,
            binding.btnSameDayDeliveryItems
        )
    }

    private fun setupStoreYouFollow(storeYouFollow: List<Product>) {
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

    override fun onResume() {
        super.onResume()
        homeViewModel.fetchStoreYouFollow()
        updateFavIcon()
    }

    private fun updateFavIcon() {
        val favRes =
            if (homeViewModel.getFav().isEmpty()) R.drawable.ic_heart else R.drawable.ic_fav_heart

        binding.ivWishList.setImageResource(favRes)
    }

    fun refreshData() {
        homeViewModel.fetchHomeModel(true) {
            lastCheckedCategoryId = -1
            binding.scrollViewHome.fullScroll(ScrollView.FOCUS_UP)
            binding.rvUniqueCategories.smoothScrollToPosition(0)
            binding.rvTodayDeals.smoothScrollToPosition(0)
            binding.rvFeaturedCategoryProducts.smoothScrollToPosition(0)
            binding.rvTrendingProducts.smoothScrollToPosition(0)
            binding.rvUnder100DollarsItems.smoothScrollToPosition(0)
            binding.rvStoreYouFollow.smoothScrollToPosition(0)
        }
    }

    private fun gotoVendorDetail(vendorId: String, isFollow: Boolean) {
        val direction = NavGraphDirections.toVendorDetailFragment(vendorId, isFollow)
        navigateTo(direction)
    }

    private fun gotoChat() {
        Zendesk.INSTANCE.init(
            requireContext(), "https://synavos4743.zendesk.com",
            "4bec6af177f6381b817a1beb0a6a856c2fb7ad17e8f03dfb",
            "mobile_sdk_client_f2062f768e45a11d080d"
        )

        Support.INSTANCE.init(Zendesk.INSTANCE)

        Chat.INSTANCE.init(requireContext(), "kK7tIQMiIaBGQQog0HSzbxISgnUnC7Gq")
        AnswerBot.INSTANCE.init(Zendesk.INSTANCE, Guide.INSTANCE);


        Logger.setLoggable(true)

        val identity: Identity = AnonymousIdentity.Builder()
            .withNameIdentifier(consumer?.fullName) // name is optional
            .withEmailIdentifier(consumer?.email) // email is optional
            .build()

        Zendesk.INSTANCE.setIdentity(identity)

        val answerEngine = AnswerBotEngine.engine()
        val supportEngine = SupportEngine.engine()
        val chatEngine = ChatEngine.engine()

        lifecycleScope.launch {
            delay(1000)
            MessagingActivity.builder()
                .withEngines(answerEngine, supportEngine, chatEngine)
                .withBotLabelString("Alicia")
                .show(requireContext())
        }


    }

    override fun getSlider() =
        listOf(binding.imageSliderHome, binding.sliderSeasonalOffer)

    override fun getRotateInterval(): Long = 5000L

    override fun getTvCartCount(): TextView = binding.tvCartCount
    override fun getIvCart(): View = binding.ivCart

    private inner class PagerAdapter<out T>(
        fa: Fragment, val list: List<T>, val categoryName: String? = null
    ) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = list.size

        override fun createFragment(position: Int): Fragment {
            val item = list[position]
            return if (item is Banner) {
                HomeSliderPageFragment.newInstance(item)
            } else {
                HomeSliderPageFragment.newInstance(categoryName!!, item as Offer)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_LAST_CHECKED_FEATURED_CATEGORY_ID, lastCheckedCategoryId)
        outState.putInt(KEY_LAST_CHECKED_FEATURED_STORE_ID, lastCheckedFeatureStoreId)
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val KEY_LAST_CHECKED_FEATURED_CATEGORY_ID = "lastCheckedFeaturedCategoryId"
        const val KEY_LAST_CHECKED_FEATURED_STORE_ID = "lastCheckedFeaturedStoreId"
    }
}