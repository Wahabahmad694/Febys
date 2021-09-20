package com.hexagram.febys.models.view

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import kotlin.random.Random
import kotlin.random.nextInt

sealed class VendorListing {
    data class VendorListingHeader(@StringRes val title: Int) : VendorListing()

    open class Vendor @JvmOverloads constructor(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("email")
        val email: String,
        @SerializedName("preferred_vendor_or_shop_name")
        val preferredVendorOrShopName: String,
        @SerializedName("role")
        val _role: String,
        @SerializedName("profileImage")
        val profileImage: String?,
        @SerializedName("business_logo")
        val businessLogo: String,
        @SerializedName("business_address")
        val businessAddress: String,
        @SerializedName("rating")
        val _vendorRating: Float = 2.5f
    ) : VendorListing() {
        val role get() = _role.substringBefore(" ")
        val vendorRating get() = _vendorRating.times(20).toInt()
    }

    class FollowingVendor @JvmOverloads constructor(
        id: Int,
        name: String,
        email: String,
        preferredVendorOrShopName: String,
        _role: String,
        profileImage: String?,
        businessLogo: String,
        businessAddress: String,
        _vendorRating: Float = 2.5f,
        var isFollow: Boolean = true
    ) : VendorListing.Vendor(
        id,
        name,
        email,
        preferredVendorOrShopName,
        _role,
        profileImage,
        businessLogo,
        businessAddress,
        _vendorRating
    ) {

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
                    vendor.name,
                    vendor.email,
                    vendor.preferredVendorOrShopName,
                    vendor._role,
                    vendor.profileImage,
                    vendor.businessLogo,
                    vendor.businessAddress,
                    vendor._vendorRating
                )
            }
        }
    }
}