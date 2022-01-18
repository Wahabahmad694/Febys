package com.hexagram.febys.ui.screens.webView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.bindings.BindingAdapter
import com.hexagram.febys.databinding.WebViewFragmentBinding
import com.hexagram.febys.utils.FebysWebViewClient
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

    private fun initUi() {
        binding.tvTitle.text = args.title

        if (args.isHtml) {
            BindingAdapter.loadHtml(binding.webView, args.url)
        } else {
            binding.webView.loadUrl(args.url)
            binding.webView.webViewClient = FebysWebViewClient()
        }
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
