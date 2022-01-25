package com.hexagram.febys.ui.screens.payment.utils

import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.hexagram.febys.BuildConfig

class PayStackWebViewClient(refId: String) : WebViewClient() {
    private val callbackUrl =
        BuildConfig.backendBaseUrl + "v1/payments/transaction/paystack/callback"
    private val redirectURL = "$callbackUrl?reference=$refId"
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        val url: Uri? = request?.url
        if (url?.host == callbackUrl) {
            return true
        } else if (url.toString() == "https://standard.paystack.co/close") {
            view?.loadUrl(redirectURL)
            return false
        }

        return super.shouldOverrideUrlLoading(view, request)
    }
}