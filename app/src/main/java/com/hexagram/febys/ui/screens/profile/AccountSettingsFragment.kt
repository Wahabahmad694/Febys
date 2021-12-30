package com.hexagram.febys.ui.screens.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentAccountSettingsBinding
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.hideLoader
import com.hexagram.febys.utils.showLoader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountSettingsFragment : BaseFragment() {
    private lateinit var binding: FragmentAccountSettingsBinding
    private val accountSettingViewModel by viewModels<AccountSettingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAccountSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiListeners()
        setObserver()
        setData()
    }

    private fun uiListeners() {
        binding.tvProfileName.setText(consumer?.fullName)
        binding.ivBack.setOnClickListener { goBack() }
        binding.btnEdit.setOnClickListener {
            enableFields()
        }

    }

    private fun enableFields() {
        binding.etEmail.isEnabled = true
        binding.etFirstName.isEnabled = true
        binding.etLastName.isEnabled = true
        binding.etPhone.isEnabled = true
        binding.btnEdit.setText(getString(R.string.label_save))

    }

    private fun setObserver() {
        accountSettingViewModel.observeProfile.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    showLoader()
                }
                is DataState.Error -> {
                    hideLoader()
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    hideLoader()
                    it.data
                }
            }
        }
    }

    private fun setData() {
        binding.etFirstName.setText(consumer?.firstName)
        binding.etLastName.setText(consumer?.lastName)
        binding.etEmail.setText(consumer?.email)
        binding.etPhone.setText(consumer?.phoneNumber?.noWithCountryCode)
    }

}