package com.hexagram.febys.models.api.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.vendor.Vendor
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product constructor(
    val _id: String,
    @SerializedName("vendor_id")
    val vendorId: String,
    val active: Boolean,
    val brief: String,
    @SerializedName("category_active")
    val categoryActive: Boolean,
    @SerializedName("category_id")
    val categoryId: Int,
    val descriptions: List<Description>,
    val name: String,
    @SerializedName("update_product")
    val updateProduct: Boolean,
    @SerializedName("vendor_active")
    val vendorActive: Boolean,
    val variants: List<Variant>,
    val vendor: Vendor,
    @SerializedName("question_answers")
    private val _qaThreads: MutableList<QAThread>,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
) : Parcelable {
    val qaThreads
        get() = _qaThreads.asReversed()
}

