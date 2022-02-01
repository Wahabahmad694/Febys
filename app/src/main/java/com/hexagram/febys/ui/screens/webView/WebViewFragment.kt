package com.hexagram.febys.ui.screens.webView

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import androidx.activity.addCallback
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.WebViewFragmentBinding
import com.hexagram.febys.utils.goBack


class WebViewFragment : BaseFragment() {
    lateinit var binding: WebViewFragmentBinding
    private val args: WebViewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = WebViewFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListener()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupFileWebView() {
        binding.apply {
            webView.webChromeClient = WebChromeClient()
            webView.settings.javaScriptEnabled = true
            webView.settings.builtInZoomControls = true
            webView.settings.setSupportZoom(true)
            webView.settings.allowFileAccess = true
        }
    }

    private fun initUi() {
        binding.tvTitle.text = args.title

        val url = args.url
//        val path =
//            "<iframe src='$url' width='100%' height='100%' style='border: rounded;'></iframe>"
//        binding.webView.loadUrl("https://qa.febys.com/about-us")
//        binding.webView.settings.javaScriptEnabled = true
//        binding.webView.settings.builtInZoomControls = true
//        binding.webView.settings.setSupportZoom(true)
//        binding.webView.webChromeClient = WebChromeClient()
//        binding.webView.webViewClient = FebysWebViewClient()

        setupFileWebView()
        binding.webView.loadUrl("https://www.google.com/")


//        if (args.isHtml) {
//            showToast("If working")
//            BindingAdapter.loadHtml(binding.webView, args.url)
//        } else {
//            showToast("else working")
//            binding.webView.loadUrl("https://qa.febys.com/about-us")
//        }

//        binding.webView.settings.
//        binding.webView.webViewClient = FebysWebViewClient()
//        binding.webView.webChromeClient = WebChromeClient()
//        binding.webView.settings.apply { javaScriptEnabled = true }
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener { handleBackPress() }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) { handleBackPress() }
    }

    private fun handleBackPress(): Boolean {
        return if (binding.webView.canGoBack()) {
            binding.webView.goBack()
            true
        } else {
            goBack()
            false
        }
    }
}
