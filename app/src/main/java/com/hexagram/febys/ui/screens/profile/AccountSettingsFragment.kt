package com.hexagram.febys.ui.screens.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
    private val pickImage = 100
    private var imageUri: Uri? =null
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
        binding.camera.setOnClickListener {
            loadImage()
        }
        binding.tvProfileName.setText(consumer?.fullName)
        binding.ivBack.setOnClickListener { goBack() }
        binding.btnEdit.setOnClickListener { enableFields() }
    }

    private fun loadImage() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, pickImage)
    }

    private fun enableFields() {
        binding.etEmail.isEnabled = true
        binding.etFirstName.isEnabled = true
        binding.etLastName.isEnabled = true
        binding.etPhone.isEnabled = true
        binding.btnEdit.text = getString(R.string.label_save)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            binding.profileImg.setImageURI(imageUri)
        }
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