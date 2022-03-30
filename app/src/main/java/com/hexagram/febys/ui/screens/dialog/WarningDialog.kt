package com.hexagram.febys.ui.screens.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.hexagram.febys.base.BaseDialog
import com.hexagram.febys.databinding.DialogWarningBinding

class WarningDialog constructor(
    @DrawableRes
    private val infoRes: Int,
    private val infoTitle: String,
    private val infoMsg: String,
    private val showNoBtn: Boolean = true,
    private val okayCallback: (() -> Unit)? = null
) : BaseDialog() {

    companion object {
        const val TAG = "InfoDialog"
    }

    private lateinit var binding: DialogWarningBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = DialogWarningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUi()
        uiListener()
    }

    private fun updateUi() {
        binding.ivInfo.setImageResource(infoRes)
        binding.tvInfoTitle.text = infoTitle
        binding.tvInfoMsg.text = infoMsg
        binding.btnYes.isVisible = okayCallback != null
        binding.btnNo.isVisible = showNoBtn
    }

    private fun uiListener() {
        binding.ivClose.setOnClickListener {
            dismiss()
        }

        binding.btnYes.setOnClickListener {
            okayCallback?.invoke()
            dismiss()
        }
        binding.btnNo.setOnClickListener {
            dismiss()
        }
    }

    override fun cancelable() = true
}