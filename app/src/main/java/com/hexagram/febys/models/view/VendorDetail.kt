package com.hexagram.febys.models.view

import androidx.annotation.DrawableRes

data class VendorDetail(
    val id: Int,
    val name: String,
    val headerImage: String,
    val profileImage: String,
    val type: String,
    val address: String,
    var isFollow: Boolean,
    val socialLinks: List<SocialLink>,
    val endorsements: List<Endorsement>
)

data class SocialLink(
    @DrawableRes val image: Int,
    val link: String
)

data class Endorsement(
    val id: Int,
    val name: String,
    val profileImage: String,
)
