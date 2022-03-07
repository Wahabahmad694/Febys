package com.hexagram.febys.bindings

import android.graphics.Paint
import android.view.View
import android.webkit.WebSettings
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
        webView.apply {
            this.isFocusable = true
            this.isFocusableInTouchMode = true
            this.settings.javaScriptEnabled = true
            this.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            this.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
            this.settings.cacheMode = WebSettings.LOAD_DEFAULT
            this.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            this.settings.domStorageEnabled = true
            this.settings.loadsImagesAutomatically = true;
            this.settings.setAppCacheEnabled(true)
            this.settings.databaseEnabled = true
            this.settings.setSupportMultipleWindows(false)
            this.loadDataWithBaseURL(
                null,
                "<style>img{display: inline;height: auto;max-width: 100%;}</style>$html",
                "text/html",
                "UTF-8",
                null
            )
        }
    }
}
