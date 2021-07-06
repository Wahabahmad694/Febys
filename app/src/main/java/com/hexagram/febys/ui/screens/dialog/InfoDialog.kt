package com.hexagram.febys.ui.screens.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import com.hexagram.febys.base.BaseDialog
import com.hexagram.febys.databinding.DialogInfoBinding

class InfoDialog(
    @DrawableRes private val infoRes: Int,
    private val infoTitle: String,
    private val infoMsg: String
) : com.hexagram.febys.base.BaseDialog() {

    companion object {
        const val TAG = "InfoDialog"
    }

    private lateinit var binding: DialogInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogInfoBinding.inflate(inflater, container, false)
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
    }

    private fun uiListener() {
        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    override fun cancelable() = true
}