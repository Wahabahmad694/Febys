package com.android.febys.ui.screens.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.android.febys.base.BaseDialog
import com.android.febys.databinding.DialogOtpVerficationBinding
import com.android.febys.utils.goBack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtpVerificationDialog : BaseDialog() {
    private lateinit var binding: DialogOtpVerficationBinding
    private val args: OtpVerificationDialogArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogOtpVerficationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiListeners()
    }

    private fun uiListeners() {
        binding.otpView.setOtpCompletionListener { otp ->
            // send otp to backend
        }

        binding.btnSkip.setOnClickListener {
            goBack()
        }
    }

    override fun cancelable() = args.isCancelable
}