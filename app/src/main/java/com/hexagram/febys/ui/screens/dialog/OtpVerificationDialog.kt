package com.hexagram.febys.ui.screens.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseDialog
import com.hexagram.febys.databinding.DialogOtpVerificationBinding
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtpVerificationDialog : com.hexagram.febys.base.BaseDialog() {
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

        binding.ivClose.setOnClickListener {
            navigateToHomeScreen()
        }
    }

    private fun setupObserver() {
        viewModel.observeOtpResponse.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
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