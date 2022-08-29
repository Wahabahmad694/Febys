package com.hexagram.febys.ui.screens.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hexagram.febys.base.BaseDialog
import com.hexagram.febys.databinding.DialogShippingMethodErrorBinding
import com.hexagram.febys.network.DataState
import com.hexagram.febys.utils.goBack

class ErrorShippingMethodDialog<T>(
    private val error: DataState.Error<T>,
    private val onOkayClick: (() -> Unit)? = null,
    private val onCloseClick: (() -> Unit)? = null,
) : BaseDialog() {

    companion object {
        const val TAG = "ErrorDialog"
    }

    private lateinit var binding: DialogShippingMethodErrorBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = DialogShippingMethodErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        uiListener()
    }

    private fun uiListener() {
        binding.ivClose.setOnClickListener {
            onCloseClick?.invoke()
            dismiss()
            goBack()
        }

        binding.btnOkay.setOnClickListener {
            onOkayClick?.invoke()
            dismiss()
        }
    }


    override fun cancelable() = false
}