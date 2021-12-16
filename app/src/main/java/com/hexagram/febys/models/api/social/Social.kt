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
            return when (name) {
                "LinkedIn" -> {
                    R.drawable.ic_social_link_fb
                }
                "Facebook" -> {
                    R.drawable.ic_social_link_fb
                }
                "Instagram" -> {
                    R.drawable.ic_social_link_fb
                }
                "Youtube" -> {
                    R.drawable.ic_social_link_fb
                }
                "Pinterest" -> {
                    R.drawable.ic_social_link_fb
                }
                "Twitter" -> {
                    R.drawable.ic_social_link_fb
                }
                else -> {
                    R.drawable.ic_social_link_fb
                }
            }
        }
}