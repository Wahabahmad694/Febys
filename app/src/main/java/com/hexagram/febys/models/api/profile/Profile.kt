package com.hexagram.febys.models.api.profile

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.cart.CartResponse
import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.models.api.vouchers.Voucher
import com.hexagram.febys.models.api.wishlist.FavSkuIds
import kotlinx.parcelize.Parcelize

@Parcelize
data class Profile(
    @SerializedName("consumer_info")
    val consumerInfo: Consumer,
    @SerializedName("wishList")
    val wishlist: FavSkuIds,
    @SerializedName("shipping_details")
    val shippingAddresses: List<ShippingAddress>,
    val cart: CartResponse,
    val followings: Followings,
    @SerializedName("recently_viewed")
    val recentlyViewed: RecentlyViewed,
    val vouchers: List<Voucher>,
) : Parcelable
