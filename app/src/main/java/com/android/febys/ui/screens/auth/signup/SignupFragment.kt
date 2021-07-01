package com.android.febys.ui.screens.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.febys.R
import com.android.febys.databinding.FragmentSignupBinding
import com.android.febys.enum.SocialLogin
import com.android.febys.network.DataState
import com.android.febys.network.requests.RequestSignup
import com.android.febys.ui.screens.auth.AuthFragment
import com.android.febys.ui.screens.auth.AuthViewModel
import com.android.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupFragment : AuthFragment() {
    private lateinit var binding: FragmentSignupBinding
    private val viewModel: AuthViewModel by viewModels()

    private var firstName: String = ""
    private var lastName: String = ""
    private var email: String = ""
    private var phone: String = ""
    private var password: String = ""
    private var confirmPassword: String = ""

    private var isSocialLogin = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiListeners()
        setupObserver()
    }

    private fun uiListeners() {
        binding.etFirstName.addTextChangedListener {
            firstName = it.toString()
            updateSignupButtonVisibility()
        }

        binding.etLastName.addTextChangedListener {
            lastName = it.toString()
            updateSignupButtonVisibility()
        }

        binding.etEmailAddress.addTextChangedListener {
            email = it.toString()
            binding.etEmailAddress.clearError()
            if (email.isNotEmpty() && !Validator.isValidEmail(email)) {
                binding.etEmailAddress.error = getString(R.string.error_enter_valid_email)
            }

            updateSignupButtonVisibility()
        }

        binding.etPhone.addTextChangedListener {
            phone = it.toString()
            binding.etPhone.clearError()
            if (phone.isNotEmpty() && !Validator.isValidPhone(phone)) {
                binding.etPhone.error = getString(R.string.error_enter_valid_phone)
            }

            updateSignupButtonVisibility()
        }

        binding.etPassword.addTextChangedListener {
            password = it.toString()
            updateSignupButtonVisibility()
        }

        binding.etConfirmPassword.addTextChangedListener {
            confirmPassword = it.toString()
            binding.etConfirmPassword.clearError()
            if (confirmPassword != password) {
                binding.etConfirmPassword.error =
                    getString(R.string.error_confirm_password_not_match)
            }

            updateSignupButtonVisibility()
        }


        binding.ivBack.setOnClickListener {
            goBack()
        }

        binding.btnSignUp.setOnClickListener {
            isSocialLogin = false
            signup()
        }

        binding.tvGotoLogin.setOnClickListener {
            goBack()
        }

        binding.ivGoogle.setOnClickListener {
            signInWithGoogle { token ->
                token?.let {
                    viewModel.socialLogin(token, SocialLogin.GOOGLE)
                    isSocialLogin = true
                }
            }
        }

        binding.ivFacebook.setOnClickListener {
            signInWithFacebook { token ->
                token?.let {
                    viewModel.socialLogin(token, SocialLogin.FACEBOOK)
                    isSocialLogin = true
                }
            }
        }
    }

    private fun setupObserver() {
        viewModel.observeSignupResponse.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {
                    val msg = getErrorMessage(it)
                    showToast(msg)
                }
                is DataState.Data -> {
                    if (isSocialLogin) {
                        findNavController().popBackStack(R.id.loginFragment, true)
                    } else {
                        navigateToOTPVerification()
                    }
                }
            }
        }
    }

    private fun signup() {
        val requestSignup = RequestSignup(
            firstName, lastName, email, phone, password
        )

        viewModel.signup(requestSignup)
    }

    private fun updateSignupButtonVisibility() {
        var enableButton = true

        if (!Validator.isValidName(firstName)) {
            enableButton = false
        }

        if (!Validator.isValidName(lastName)) {
            enableButton = false
        }

        if (!Validator.isValidEmail(email)) {
            enableButton = false
        }

        if (!Validator.isValidPhone(phone)) {
            enableButton = false
        }

        if (!Validator.isValidPassword(password)) {
            enableButton = false
        }

        if (confirmPassword.isEmpty() || confirmPassword != password) {
            enableButton = false
        }

        binding.btnSignUp.isEnabled = enableButton
    }

    private fun navigateToOTPVerification() {
        val navigateToOtpVerification =
            SignupFragmentDirections.actionSignupFragmentToOtpVerificationDialog(false)
        navigateTo(navigateToOtpVerification)
    }
}