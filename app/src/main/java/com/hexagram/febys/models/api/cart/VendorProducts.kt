package com.hexagram.febys.models.api.cart

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.price.Price
import com.hexagram.febys.models.api.rating.VendorReview
import com.hexagram.febys.models.api.vendor.Vendor
import kotlinx.parcelize.Parcelize

@Parcelize
data class VendorProducts(
    val vendor: Vendor,
    val products: MutableList<CartProduct>,
    val amount: Price?,
    val status: String?,
    val hasReviewed: Boolean,
    @SerializedName("return_fee_percentage")
    val returnFeePercentage: Double?,
    val reverted: Boolean?,
    @SerializedName("returns_detail")
    val returns: List<ReturnDetails>,
    @SerializedName("_id")
    val id: String,
    val courier: Courier?,
    @SerializedName("revert_details")
    val revertDetails: RevertedDetail?,
    @SerializedName("rating_and_review")
    val ratingAndReview: VendorReview?
) : Parcelable {
    fun deepCopy(): VendorProducts {
        val json = Gson().toJson(this)
        return Gson().fromJson(json, VendorProducts::class.java)
    }
}

@Parcelize
data class ReturnDetails(
    val _id: String,
    @SerializedName("vendor_id")
    val vendorId: String,
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("consumer_id")
    val consumerId: String,
    @SerializedName("total_amount")
    val totalAmount: Price,
    @SerializedName("total_commission")
    val totalCommission: Price,
    @SerializedName("return_fee_percentage")
    val returnFeePercentage: Double,
    val reason: String,
    val comments: String,
    val status: String,
    val items: List<ReturnItem>,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
) : Parcelable

@Parcelize
data class ReturnItem(
    val _id: String,
    @SerializedName("sku_id")
    val skuId: String,
    @SerializedName("qty")
    val quantity: Int,
    val price: Price,
    val commission: Price,
) : Parcelable
