package com.hexagram.febys.models.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.vendor.Endorsement
import kotlinx.parcelize.Parcelize

@Parcelize
data class EndorsementResponse(
    @SerializedName("vendors")
    val endorsement: List<Endorsement>
): Parcelable
