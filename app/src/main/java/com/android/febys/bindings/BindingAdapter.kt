package com.android.febys.bindings

import android.R.attr.fadeDuration
import android.R.attr.scaleType
import android.graphics.Paint
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.android.febys.R
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchy
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.view.SimpleDraweeView


object BindingAdapter {

    @JvmStatic
    @BindingAdapter("binding:image_url")
    fun imageUrl(imageView: SimpleDraweeView, imageUrl: String) {
        imageView.setImageURI(imageUrl)
    }

    @JvmStatic
    @BindingAdapter("binding:strike_through")
    fun strikeThrough(textView: TextView, strikeThrough: Boolean) {
        if (strikeThrough) {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }
}