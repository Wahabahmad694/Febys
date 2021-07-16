package com.hexagram.febys.network.response

import com.google.gson.annotations.SerializedName

data class Product constructor(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("short_description")
    val shortDescriptionHTML: String,
    @SerializedName("description")
    val descriptionHTML: String,
    @SerializedName("warranty")
    val _warranty: Int,
    @SerializedName("warranty_duration")
    val warrantyDuration: String,
    @SerializedName("product_return_refundable")
    val _productReturnRefundable: Int,
    @SerializedName("product_return_refundable_policy")
    val product_return_refundable_policy: String,
    @SerializedName("deliver")
    val delivery: String,
    @SerializedName("tags")
    val _tags: String,
    @SerializedName("fulfillment")
    val fulfillment: String,
    @SerializedName("is_complete")
    val _IsComplete: Int,
    @SerializedName("deleted")
    val deleted: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("category")
    val category: Category,
    @SerializedName("descriptions")
    val descriptions: List<ProductDescription>,
    @SerializedName("variants")
    val product_variants: List<ProductVariant>,
) {
    val warranty
        get() = _warranty == 1

    val isComplete
        get() = _IsComplete == 1

    val isDeleted
        get() = deleted == 1

    val productReturnRefundable
        get() = _productReturnRefundable == 1

    val tags
        get() = _tags.split(",").toList()
}
