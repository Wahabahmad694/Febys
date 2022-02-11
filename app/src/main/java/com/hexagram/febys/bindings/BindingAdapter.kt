package com.hexagram.febys.bindings

import android.graphics.Paint
import android.util.Base64
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.facebook.drawee.view.SimpleDraweeView
import com.hexagram.febys.utils.load


object BindingAdapter {

    @JvmStatic
    @BindingAdapter("binding:image_url")
    fun imageUrl(imageView: SimpleDraweeView, imageUrl: String?) {
        imageView.load(imageUrl)
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

    @JvmStatic
    @BindingAdapter("binding:is_visible")
    fun isVisible(view: View, isVisible: Boolean) {
        view.isVisible = isVisible
    }

    @JvmStatic
    @BindingAdapter("binding:load_html")
    fun loadHtml(webView: WebView, html: String) {
        val encodedHtml = Base64.encodeToString(html.toByteArray(), Base64.NO_PADDING)
        webView.loadData(encodedHtml, "text/html", "base64")
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            setSupportZoom(true)
        }
    }
}
