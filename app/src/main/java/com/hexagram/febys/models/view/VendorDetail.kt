package com.hexagram.febys.models.view

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.core.view.setPadding
import com.hexagram.febys.R
import com.hexagram.febys.utils.Utils

data class VendorDetail constructor(
    val id: Int,
    val name: String,
    val slogan: String,
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
) {
    companion object {
        fun addAllTo(socialLinks: List<SocialLink>, viewGroup: ViewGroup) {
            viewGroup.removeAllViews()
            socialLinks.forEach { socialLink ->
                val imageView = ImageView(viewGroup.context)
                val layoutParam = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                imageView.setBackgroundResource(R.drawable.bg_social_link)
                imageView.setImageResource(socialLink.image)
                imageView.setPadding(16)
                imageView.setOnClickListener {
                    Utils.openLink(it.context, socialLink.link)
                }
                viewGroup.addView(imageView, layoutParam)
            }
        }
    }
}

data class Endorsement(
    val id: Int,
    val name: String,
    val profileImage: String,
)
