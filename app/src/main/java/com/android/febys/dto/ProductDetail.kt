package com.android.febys.dto

data class ProductDetail(
    val id: String,
    val name: String,
    val isFav: Boolean,
    val images: List<String>,
    val shortDescription: String,
    val price: Double,
    val verifiedLogo: String,
    val sizes: List<Double>,
    val protections: Map<Int, Double>,
    val description: String,
    val brandName: String,
    val weight: Double,
    val dimensions: Pair<Double, Double>,
    val modelNo: String,
    val specialFeature: String,
)