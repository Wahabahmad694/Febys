package com.android.febys.ui.screens.auth.credential

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.febys.R
import com.android.febys.base.BaseFragment
import com.android.febys.databinding.FragmentResetCredentialBinding
import com.android.febys.utils.goBack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetCredentialFragment : BaseFragment() {
    private lateinit var binding: FragmentResetCredentialBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetCredentialBinding.inflate(inflater, container, false)
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
    }
}