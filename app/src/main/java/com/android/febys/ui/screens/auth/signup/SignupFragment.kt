package com.android.febys.ui.screens.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.android.febys.R
import com.android.febys.databinding.FragmentSignupBinding
import com.android.febys.network.DataState
import com.android.febys.network.requests.RequestSignup
import com.android.febys.network.response.ResponseSignup
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
            binding.tilFirstName.clearError()
            if (firstName.isEmpty()) {
                binding.tilFirstName.error = getString(R.string.error_enter_first_name)
            }

            updateSignupButtonVisibility()
        }

        binding.etLastName.addTextChangedListener {
            lastName = it.toString()
            binding.tilLastName.clearError()
            if (lastName.isEmpty()) {
                binding.tilLastName.error = getString(R.string.error_enter_last_name)
            }

            updateSignupButtonVisibility()
        }

        binding.etEmailAddress.addTextChangedListener {
            email = it.toString()
            binding.tilEmailAddress.clearError()
            if (email.isEmpty()) {
                binding.tilEmailAddress.error = getString(R.string.error_enter_email)
            } else if (!Validator.isValidEmail(email)) {
                binding.tilEmailAddress.error = getString(R.string.error_enter_valid_email)
            }

            updateSignupButtonVisibility()
        }

        binding.etPhone.addTextChangedListener {
            phone = it.toString()
            binding.tilPhone.clearError()
            if (phone.isEmpty()) {
                binding.tilPhone.error = getString(R.string.error_enter_phone)
            } else if (!Validator.isValidPhone(phone)) {
                binding.tilPhone.error = getString(R.string.error_enter_valid_phone)
            }

            updateSignupButtonVisibility()
        }

        binding.etPassword.addTextChangedListener {
            password = it.toString()
            binding.tilPassword.clearError()
            if (password.isEmpty()) {
                binding.tilPassword.error = getString(R.string.error_enter_password)
            }

            updateSignupButtonVisibility()
        }

        binding.etConfirmPassword.addTextChangedListener {
            confirmPassword = it.toString()
            binding.tilConfirmPassword.clearError()
            if (confirmPassword.isEmpty()) {
                binding.tilConfirmPassword.error = getString(R.string.error_enter_confirm_password)
            } else if (confirmPassword != password) {
                binding.tilConfirmPassword.error =
                    getString(R.string.error_confirm_password_not_match)
            }

            updateSignupButtonVisibility()
        }


        binding.ivBack.setOnClickListener {
            goBack()
        }

        binding.btnSignUp.setOnClickListener {
            signup()
        }

        binding.tvGotoLogin.setOnClickListener {
            goBack()
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
                    handleResponse(it.data)
                }
            }
        }
    }

    private fun handleResponse(response: ResponseSignup) {
        when (response) {
            is ResponseSignup.Fail -> {
                val emailError =
                    response.signupErrors.find { it.field == "email" }?.error ?: ""
                val phoneError =
                    response.signupErrors.find { it.field == "phone_number" }?.error ?: ""

                binding.tilEmailAddress.error = emailError
                binding.tilPhone.error = phoneError
            }
            is ResponseSignup.Success -> {
                navigateToOTPVerification()
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