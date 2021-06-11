package com.android.febys.ui.screens.auth.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.android.febys.databinding.FragmentSignupBinding
import com.android.febys.ui.screens.auth.AuthFragment
import com.android.febys.ui.screens.auth.AuthViewModel
import com.android.febys.utils.goBack
import com.android.febys.utils.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupFragment : AuthFragment() {
    private lateinit var binding: FragmentSignupBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiListeners()
    }

    private fun uiListeners() {
        binding.ivBack.setOnClickListener {
            goBack()
        }

        binding.btnSignUp.setOnClickListener {
            val navigateToOtpVerification =
                SignupFragmentDirections.actionSignupFragmentToOtpVerificationDialog(false)
            navigateTo(navigateToOtpVerification)
        }

        binding.tvGotoLogin.setOnClickListener {
            goBack()
        }
    }
}