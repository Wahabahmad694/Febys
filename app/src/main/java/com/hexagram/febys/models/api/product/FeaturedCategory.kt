package com.hexagram.febys.models.api.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeaturedCategory(
    val id: Int,
    val name: String,
    val logo: String,
    val featured: Int,
    val products: List<Product>
) : Parcelable
