package com.hexagram.febys.models.api.vendor

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.contact.ContactDetails
import com.hexagram.febys.models.api.rating.Rating
import com.hexagram.febys.models.api.rating.VendorReview
import com.hexagram.febys.models.api.role.Role
import com.hexagram.febys.models.api.social.Social
import com.hexagram.febys.models.api.template.Template
import kotlinx.parcelize.Parcelize

@Parcelize
data class Vendor @JvmOverloads constructor(
    val _id: String,
    val email: String,
    val name: String,
    @SerializedName("shop_name")
    val shopName: String,
    val official: Boolean,
    val socials: List<Social>?,
    val template: List<Template>?,
    @SerializedName("template_photo")
    val templatePhoto: String?,
    val role: Role,
    val stats: VendorStats,
    @SerializedName("business_info")
    val businessInfo: BusinessInfo,
    @SerializedName("contact_details")
    val contactDetails: ContactDetails,
    var isFollow: Boolean = false,
    @SerializedName("value_score")
    val valueScore: List<Rating>,
    @SerializedName("pricing_score")
    val pricingScore: List<Rating>,
    @SerializedName("quality_score")
    val qualityScore: List<Rating>,
    @SerializedName("ratings_and_reviews")
    val _ratingsAndReviews: List<VendorReview>,
    @SerializedName("rating_and_review")
    val ratingAndReview: VendorReview? = null,
) : Parcelable{
    val ratingsAndReviews get() = _ratingsAndReviews.reversed()
}
