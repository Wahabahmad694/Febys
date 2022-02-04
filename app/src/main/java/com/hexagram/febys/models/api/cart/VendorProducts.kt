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
