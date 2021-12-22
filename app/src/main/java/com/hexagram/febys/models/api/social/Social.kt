package com.hexagram.febys.models.api.social

import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.hexagram.febys.R
import com.hexagram.febys.utils.Utils
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

    companion object {
        fun addAllTo(social: List<Social>?, viewGroup: ViewGroup, emptyView: View) {
            viewGroup.removeAllViews()

            emptyView.isVisible = social.isNullOrEmpty()
            if (social.isNullOrEmpty()) return

            social.forEach { socialLink ->
                val imageView = ImageView(viewGroup.context)
                val layoutParam = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                imageView.setImageResource(socialLink.imageRes)
                imageView.setPadding(4)
                imageView.setOnClickListener { Utils.openLink(it.context, socialLink.url) }
                viewGroup.addView(imageView, layoutParam)
            }
        }
    }
}