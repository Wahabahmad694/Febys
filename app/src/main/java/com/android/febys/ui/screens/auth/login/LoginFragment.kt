package com.android.febys.ui.screens.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.android.febys.databinding.FragmentLoginBinding
import com.android.febys.ui.screens.auth.AuthFragment
import com.android.febys.ui.screens.auth.AuthViewModel
import com.android.febys.utils.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : AuthFragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // todo remove testing navigation once actual work done
        val navigateToSignUp = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
        navigateTo(navigateToSignUp)
    }
}