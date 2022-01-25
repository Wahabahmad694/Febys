package com.hexagram.febys.utils

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class FebysWebViewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        view?.loadUrl(request?.url.toString())
        return true
    }

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (url != null) view?.loadUrl(url)
        return true
    }
}