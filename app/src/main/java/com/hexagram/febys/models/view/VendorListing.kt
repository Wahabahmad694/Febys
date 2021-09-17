package com.hexagram.febys.models.view

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName

sealed class VendorListing {
    data class VendorListingHeader(@StringRes val title: Int) : VendorListing()

    open class Vendor @JvmOverloads constructor(
        val id: Int,
        @SerializedName("name")
        val vendorName: String,
        @SerializedName("role")
        val _vendorType: String,
        @SerializedName("rating")
        val _vendorRating: Float = 2.5f,
        @SerializedName("business_logo")
        val img: String
    ) : VendorListing() {
        val vendorType get() = _vendorType.substringBefore(" ")
        val vendorRating get() = _vendorRating.times(20).toInt()
    }

    class FollowingVendor @JvmOverloads constructor(
        id: Int,
        vendorName: String,
        _vendorType: String,
        vendorRating: Float = 2.5f,
        img: String,
        val isFollow: Boolean = true
    ) : VendorListing.Vendor(id, vendorName, _vendorType, vendorRating, img) {

        companion object {
            fun fromVendors(vendors: List<Vendor>): List<FollowingVendor> {
                val list = mutableListOf<FollowingVendor>()
                vendors.forEach {
                    list.add(fromVendor(it))
                }

                return list
            }

            fun fromVendor(vendor: Vendor): FollowingVendor {
                return FollowingVendor(
                    vendor.id,
                    vendor.vendorName,
                    vendor._vendorType,
                    vendor._vendorRating,
                    vendor.img,
                )
            }
        }
    }
}