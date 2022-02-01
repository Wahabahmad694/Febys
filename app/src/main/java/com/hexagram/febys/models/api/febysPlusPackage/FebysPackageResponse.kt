package com.hexagram.febys.models.api.febysPlusPackage

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FebysPackageResponse(
    val packages: List<Package>
):Parcelable