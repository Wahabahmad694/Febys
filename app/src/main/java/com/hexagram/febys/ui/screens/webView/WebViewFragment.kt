package com.hexagram.febys.ui.screens.webView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.WebViewFragmentBinding
import com.hexagram.febys.utils.goBack


class WebViewFragment : BaseFragment() {
    lateinit var binding:WebViewFragmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        binding.webView.loadUrl("https://www.google.com")
        binding.ivBack.setOnClickListener { goBack() }
    }

}