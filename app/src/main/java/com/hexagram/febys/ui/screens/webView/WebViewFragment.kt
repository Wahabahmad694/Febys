package com.hexagram.febys.ui.screens.webView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.WebViewFragmentBinding


class WebViewFragment : BaseFragment() {
    lateinit var binding: WebViewFragmentBinding
    private val args:WebViewFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    isEnabled = false
                    activity?.onBackPressed()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = WebViewFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiListener()
    }

    private fun uiListener() {
        binding.webView.loadUrl(args.url)
        binding.webView.webViewClient = MyWeb()
        binding.ivBack.setOnClickListener { requireActivity().onBackPressed() }
    }
}

class MyWeb : WebViewClient() {
    override fun shouldOverrideUrlLoading(
        view: WebView?, request: WebResourceRequest?,
    ): Boolean {
        view?.loadUrl(request?.url.toString())
        return true
    }

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (url != null) {
            view?.loadUrl(url)
        }
        return true
    }
}
