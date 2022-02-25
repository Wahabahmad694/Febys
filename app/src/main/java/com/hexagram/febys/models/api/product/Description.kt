package com.hexagram.febys.models.api.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Description(
    val _id: String,
    val title: String,
    @SerializedName("content")
    val _content: String
) : Parcelable {
    val content: String
        get() {
            return "<html><head><style>img{max-width:100%%;height:auto !important;width:auto !important;} h1,h2,h3,h4,h5,h6,p{font-family: Arial; word-wrap:break-word;};</style></head><body style='margin:0; padding:0;'>${_content}</body></html>"
        }
}
    