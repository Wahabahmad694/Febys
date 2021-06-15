package com.android.febys.ui.screens.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.android.febys.base.BaseDialog
import com.android.febys.databinding.DialogOtpVerificationBinding
import com.android.febys.network.DataState
import com.android.febys.network.response.ResponseOtpVerification
import com.android.febys.network.response.ResponseSignup
import com.android.febys.ui.screens.auth.AuthViewModel
import com.android.febys.utils.getErrorMessage
import com.android.febys.utils.navigateTo
import com.android.febys.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtpVerificationDialog : BaseDialog() {
    private lateinit var binding: DialogOtpVerificationBinding
    private val viewModel: AuthViewModel by viewModels()
    private val args: OtpVerificationDialogArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogOtpVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiListeners()
        setupObserver()
    }

    private fun uiListeners() {
        binding.otpView.setOtpCompletionListener { otp ->
            viewModel.verifyUser(otp)
        }

        binding.btnSkip.setOnClickListener {
            navigateToHomeScreen()
        }
    }

    private fun setupObserver() {
        viewModel.observeOtpResponse.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {
                    val msg = getErrorMessage(it)
                    showToast(msg)
                }
                is DataState.Data -> {
                    handleResponse(it.data)
                }
            }
        }
    }

    private fun handleResponse(response: ResponseOtpVerification) {
        when (response) {
            is ResponseOtpVerification.Fail -> {
                val otpError =
                    response.signupErrors.find { it.field == "otp" }?.error ?: ""
            }
            is ResponseOtpVerification.Success -> {
                navigateToHomeScreen()
            }
        }
    }

    private fun navigateToHomeScreen() {
        val navigateToHomeScreen =
            OtpVerificationDialogDirections.actionOtpVerificationDialogToHomeFragment()
        navigateTo(navigateToHomeScreen)
    }

    override fun cancelable() = args.isCancelable
}