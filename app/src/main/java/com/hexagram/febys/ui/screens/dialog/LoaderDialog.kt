package com.hexagram.febys.ui.screens.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.backends.pipeline.Fresco
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseDialog
import com.hexagram.febys.databinding.LayoutLoaderBinding

class LoaderDialog : BaseDialog() {
    companion object {
        const val TAG = "LoaderDialog"
    }

    private lateinit var binding: LayoutLoaderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = LayoutLoaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLoaderImage()
    }

    private fun setLoaderImage() {
        val uri = "res:///${R.drawable.febys_loader}"
        val draweeViewBuilder = Fresco.newDraweeControllerBuilder()
        draweeViewBuilder.setUri(uri)
        draweeViewBuilder.autoPlayAnimations = true

        binding.ivLoader.controller = draweeViewBuilder.build()
    }


    override fun cancelable() = false
}