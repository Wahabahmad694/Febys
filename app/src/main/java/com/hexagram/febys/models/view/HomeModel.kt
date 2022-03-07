package com.hexagram.febys.models.view

import com.hexagram.febys.models.api.banners.Banner
import com.hexagram.febys.models.api.category.UniqueCategory
import com.hexagram.febys.models.api.product.FeaturedCategory
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.network.response.SeasonalOffer


data class HomeModel(
    val uniqueCategories: List<UniqueCategory>,
    val banners: List<Banner>,
    val todayDeals: List<Product>,
    val featuredCategories: List<FeaturedCategory>,
    val seasonalOffers: List<SeasonalOffer>,
    val trendingProducts: List<Product>,
    val storeYouFollow: List<Product>,
    val under100DollarsItems: List<Product>
)