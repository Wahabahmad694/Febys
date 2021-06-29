package com.android.febys.ui.screens.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.android.febys.base.BaseFragment
import com.android.febys.databinding.FragmentAccountBinding
import com.android.febys.ui.screens.auth.AuthViewModel
import com.android.febys.utils.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountFragment : BaseFragment() {
    private lateinit var binding: FragmentAccountBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiListeners()
    }

    private fun uiListeners() {
        binding.btnSignOut.setOnClickListener {
            authViewModel.signOut {
                val navigateToLogin =
                    AccountFragmentDirections.actionAccountFragmentToLoginFragment()
                navigateTo(navigateToLogin)
            }
        }
    }
}