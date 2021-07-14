package com.hexagram.febys.models.view

import com.hexagram.febys.network.response.*

data class HomeModel(
    val uniqueCategories: List<UniqueCategory>,
    val banners: List<Banner>,
    val todayDeals: List<Product>,
    val featuredCategories: List<Category>,
    val seasonalOffers: List<SeasonalOffer>,
    val trendingProducts: List<Product>,
    val storeYouFollow: List<String>,
    val under100DollarsItems: List<Product>
)