package com.hexagram.febys.models.api.social

import android.os.Parcelable
import com.hexagram.febys.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class Social(
    val _id: String,
    val name: String,
    val url: String
) : Parcelable {
    val imageRes: Int
        get() {
            return R.drawable.ic_social_link_fb
        }
}