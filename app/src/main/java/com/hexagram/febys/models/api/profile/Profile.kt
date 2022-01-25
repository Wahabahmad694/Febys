package com.hexagram.febys.models.api.profile

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.cart.Cart
import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.models.api.vouchers.Voucher
import com.hexagram.febys.models.api.wishlist.FavSkuIds
import com.hexagram.febys.ui.screens.payment.models.Wallet
import kotlinx.parcelize.Parcelize

@Parcelize
data class Profile(
    @SerializedName("consumer_info")
    val consumerInfo: Consumer,
    @SerializedName("wishList")
    val wishlist: FavSkuIds,
    @SerializedName("shipping_details")
    val shippingAddresses: List<ShippingAddress>,
    val cart: Cart,
    val followings: Followings,
    @SerializedName("recently_viewed")
    val recentlyViewed: RecentlyViewed,
    val vouchers: List<Voucher>,
    val wallet: Wallet
) : Parcelable
