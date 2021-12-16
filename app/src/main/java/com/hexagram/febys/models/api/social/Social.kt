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
                    R.drawable.ic_social_link_linkedin
                }
                "Instagram" -> {
                    R.drawable.ic_social_link_instagram
                }
                "Youtube" -> {
                    R.drawable.ic_social_link_youtube
                }
                "Pinterest" -> {
                    R.drawable.ic_social_link_pinterest
                }
                "Twitter" -> {
                    R.drawable.ic_social_link_twitter
                }
                else -> {
                    R.drawable.ic_social_link_fb
                }
            }
        }
}