package com.android.febys.ui.screens.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.febys.R
import com.android.febys.base.BaseDialog
import com.android.febys.databinding.DialogOtpVerificationBinding
import com.android.febys.network.DataState
import com.android.febys.ui.screens.auth.AuthViewModel
import com.android.febys.utils.getErrorMessage
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
                    binding.otpView.text?.clear()
                }
                is DataState.Data -> {
                    navigateToHomeScreen()
                }
            }
        }
    }

    private fun navigateToHomeScreen() {
        findNavController().popBackStack(R.id.loginFragment, true)
    }

    override fun cancelable() = args.isCancelable
}