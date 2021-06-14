package com.android.febys.ui.screens.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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
import com.google.android.material.textfield.TextInputLayout
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

            validate()
        }

        binding.etLastName.addTextChangedListener {
            lastName = it.toString()
            binding.tilLastName.clearError()
            if (lastName.isEmpty()) {
                binding.tilLastName.error = getString(R.string.error_enter_last_name)
            }

            validate()
        }

        binding.etEmailAddress.addTextChangedListener {
            email = it.toString()
            binding.tilEmailAddress.clearError()
            if (!Validator.isValidEmail(email)) {
                binding.tilEmailAddress.error = getString(R.string.error_enter_valid_email)
            }
            if (email.isEmpty()) {
                binding.tilEmailAddress.error = getString(R.string.error_enter_email)
            }

            validate()
        }

        binding.etPhone.addTextChangedListener {
            phone = it.toString()
            binding.tilPhone.clearError()
            if (!Validator.isValidPhone(phone)) {
                binding.tilPhone.error = getString(R.string.error_enter_valid_phone)
            }
            if (phone.isEmpty()) {
                binding.tilPhone.error = getString(R.string.error_enter_phone)
            }

            validate()
        }

        binding.etPassword.addTextChangedListener {
            password = it.toString()
            binding.tilPassword.clearError()
            if (password.isEmpty()) {
                binding.tilPassword.error = getString(R.string.error_enter_password)
            }

            validate()
        }

        binding.etConfirmPassword.addTextChangedListener {
            confirmPassword = it.toString()
            binding.tilConfirmPassword.clearError()
            if (confirmPassword != password) {
                binding.tilConfirmPassword.error =
                    getString(R.string.error_confirm_password_not_match)
            }
            if (confirmPassword.isEmpty()) {
                binding.tilConfirmPassword.error = getString(R.string.error_enter_confirm_password)
            }

            validate()
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
                viewModel.saveUser(response.userDTO) {
                    navigateToOTPVerification()
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

    private fun validate() {
        var isValid = true

        if (firstName.isEmpty()) {
            isValid = false
        }

        if (lastName.isEmpty()) {
            isValid = false
        }

        if (email.isEmpty() || !Validator.isValidEmail(email)) {
            isValid = false
        }

        if (phone.isEmpty() || !Validator.isValidPhone(phone)) {
            isValid = false
        }

        if (password.isEmpty()) {
            isValid = false
        }

        if (confirmPassword != password) {
            isValid = false
        }
        if (confirmPassword.isEmpty()) {
            isValid = false
        }

        binding.btnSignUp.isEnabled = isValid
    }

    private fun navigateToOTPVerification() {
        val navigateToOtpVerification =
            SignupFragmentDirections.actionSignupFragmentToOtpVerificationDialog(false)
        navigateTo(navigateToOtpVerification)
    }
}