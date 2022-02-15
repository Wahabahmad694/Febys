package com.hexagram.febys.ui.screens.profile

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragmentWithPermission
import com.hexagram.febys.databinding.FragmentAccountSettingsBinding
import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.models.api.contact.PhoneNo
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.requests.RequestUpdateUser
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountSettingsFragment : BaseFragmentWithPermission() {
    private lateinit var binding: FragmentAccountSettingsBinding
    private val accountSettingViewModel by viewModels<AccountSettingViewModel>()

    private var permissionCallback: ((Boolean) -> Unit)? = null
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            permissionCallback?.invoke(isGranted)
        }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uriContent = result.uriContent
            val uriFilePath = result.getUriFilePath(requireContext()) // optional usage
            if (uriContent == null && uriFilePath == null) return@registerForActivityResult

            accountSettingViewModel.updateProfileImage(uriFilePath!!)
        } else {
            val error = result.error?.message ?: ""
            showToast(error)
        }
    }

    private var isInEditMode = false
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
        setData(consumer)
    }

    private fun uiListeners() {
        binding.btnChangeProfileImg.setOnClickListener {
            if (isInEditMode) showImageChooseOptions()
        }

        binding.ivBack.setOnClickListener { goBack() }

        binding.ivEdit.setOnClickListener {
            isInEditMode = !isInEditMode
            updateField()
            if (!isInEditMode) {
                val updateUser = getUpdatedConsumer()
                updateUser?.let { accountSettingViewModel.updateProfile(it) }
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            if (isInEditMode) {
                isInEditMode = false
                setData(consumer)
                updateField()
            } else {
                goBack()
            }
        }
    }

    private fun showSuccessDialog() {
        val resId = R.drawable.ic_thanks_info
        val title = getString(R.string.msg_thank_you)
        val msg = getString(R.string.label_profile_is_updated)

        showInfoDialoge(resId, title, msg) { goBack() }
    }

    private fun getUpdatedConsumer(): RequestUpdateUser? {
        return consumer?.let {
            RequestUpdateUser(
                it.id,
                binding.etFirstName.text.toString(),
                binding.etLastName.text.toString(),
                binding.etPhone.text.toString(),
                binding.ccpPhoneCode.selectedCountryNameCode,
                consumer?.profileImage,
                consumer?.notificationsStatus
            )
        }
    }

    private fun showImageChooseOptions() {
        val selectImageOptions = arrayOf("Gallery", "Camera", "Cancel")
        val selectImageOptionHandler = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                0 -> {
                    requestPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) {
                        if (it) startCrop(includeGallery = true, includeCamera = false)
                    }
                }
                1 -> {
                    requestPermission(requireContext(), Manifest.permission.CAMERA) {
                        if (it) startCrop(includeGallery = false, includeCamera = true)
                    }
                }
                2 -> {
                    dialog.dismiss()
                }
            }
        }

        AlertDialog.Builder(context)
            .setTitle(getString(R.string.label_chose_option))
            .setItems(selectImageOptions, selectImageOptionHandler)
            .create()
            .show()
    }

    private fun requestPermission(
        context: Context, permission: String, permissionCallback: (Boolean) -> Unit
    ) {
        this.permissionCallback = permissionCallback
        when {
            ContextCompat.checkSelfPermission(
                context, permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                this.permissionCallback?.invoke(true)
            }
            shouldShowRequestPermissionRationale(permission) -> {
                showRationalPermissionDialog(permission)
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun showRationalPermissionDialog(permission: String) {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.label_permission_required))
            .setMessage(getString(R.string.label_permission_msg))
            .setPositiveButton(R.string.okay) { dialog, _ ->
                dialog.dismiss()
                requestPermissionLauncher.launch(permission)
            }
            .setNegativeButton(R.string.label_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun startCrop(includeGallery: Boolean, includeCamera: Boolean) {
        cropImage.launch(
            options {
                setImageSource(includeGallery, includeCamera)
                setAllowRotation(false)
                setGuidelines(CropImageView.Guidelines.ON)
                setAllowFlipping(false)
                setInitialCropWindowPaddingRatio(0.0f)
                setCropMenuCropButtonTitle(getString(R.string.label_done))
                setAspectRatio(1, 1)
                setOutputCompressQuality(50)
            }
        )
    }

    private fun updateField() {
        binding.etEmail.setTextColor(Color.GRAY)
        binding.etFirstName.isEnabled = isInEditMode
        binding.etLastName.isEnabled = isInEditMode
        binding.etPhone.isEnabled = isInEditMode
        binding.btnChangeProfileImg.isVisible = isInEditMode
        binding.ccpPhoneCode.isActivated = isInEditMode

        binding.ivEdit.setImageResource(
            if (isInEditMode) R.drawable.ic_mark_tic else R.drawable.ic_edit
        )

        val color = if (isInEditMode) Color.GRAY else Color.BLACK
        binding.etEmail.setTextColor(color)
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

        accountSettingViewModel.updateProfile.observe(viewLifecycleOwner) {
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
                    setData(it.data)
                    showSuccessDialog()
                }
            }
        }

        accountSettingViewModel.updateProfileImage.observe(viewLifecycleOwner) {
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
                    setData(consumer)
                }
            }
        }
    }

    private fun updateDefaultCCP(contact: PhoneNo?) {
        binding.ccpPhoneCode.setDefaultCountryUsingNameCode(
            contact?.countryCode ?: Utils.DEFAULT_COUNTRY_CODE
        )
        binding.ccpPhoneCode.resetToDefaultCountry()
        val countryCodeWithPlus = binding.ccpPhoneCode.selectedCountryCodeWithPlus
        binding.etPhone.setText(contact?.number?.replace(countryCodeWithPlus, ""))
    }

    private fun setData(consumer: Consumer?) {
        binding.profileImg.load(consumer?.profileImage)
        binding.tvProfileName.text = consumer?.fullName
        binding.etFirstName.setText(consumer?.firstName)
        binding.etLastName.setText(consumer?.lastName)
        binding.etEmail.setText(consumer?.email)
        updateDefaultCCP(consumer?.phoneNumber)
    }
}