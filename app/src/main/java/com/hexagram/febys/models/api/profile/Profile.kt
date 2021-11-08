package com.hexagram.febys.models.api.profile

import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.cart.CartResponse
import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.models.api.wishlist.FavSkuIds

data class Profile(
    @SerializedName("consumer_info")
    val consumerInfo: Consumer,
    @SerializedName("wishList")
    val wishlist: FavSkuIds,
    val cart: CartResponse
)
