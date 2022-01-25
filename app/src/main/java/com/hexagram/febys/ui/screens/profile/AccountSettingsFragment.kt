package com.hexagram.febys.ui.screens.profile

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragmentWithPermission
import com.hexagram.febys.databinding.FragmentAccountSettingsBinding
import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.requests.RequestUpdateUser
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.MediaFileUtils
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.hideLoader
import com.hexagram.febys.utils.showLoader
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AccountSettingsFragment : BaseFragmentWithPermission() {
    private lateinit var binding: FragmentAccountSettingsBinding
    private val accountSettingViewModel by viewModels<AccountSettingViewModel>()

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
        binding.camera.setOnClickListener {
            choseOption()
        }
        binding.ivBack.setOnClickListener { goBack() }
        binding.btnEdit.setOnClickListener {
            isInEditMode = !isInEditMode
            updateField()
            if (!isInEditMode) {
                val updateUser = getUpdatedConsumer()
                updateUser?.let { accountSettingViewModel.updateProfile(it) }
                showToast("User Profile updated")

            }
        }
    }

    private fun getUpdatedConsumer(): RequestUpdateUser? {
        return consumer?.let {
            RequestUpdateUser(
                it.id,
                binding.etFirstName.text.toString(),
                binding.etLastName.text.toString(),
                binding.etPhone.text.toString(),
                "PK",
                accountSettingViewModel.uploadedFilePath
            )
        }
    }

    private fun startCrop(imageUri: Uri) {
        CropImage.activity()
            .setAllowRotation(false)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAllowFlipping(false)
            .setInitialCropWindowPaddingRatio(0.0f)
            .setCropMenuCropButtonTitle("Done")
            .setAspectRatio(1, 1)
            .setOutputCompressQuality(50)
            .start(requireContext(), this)
    }

    private fun updateField() {
        binding.etEmail.setTextColor(Color.GRAY)
        binding.etFirstName.isEnabled = isInEditMode
        binding.etLastName.isEnabled = isInEditMode
        binding.etPhone.isEnabled = isInEditMode
        binding.camera.isVisible = isInEditMode

        binding.btnEdit.text =
            if (isInEditMode) getString(R.string.label_save) else getString(R.string.label_edit)

        val color = if (isInEditMode) Color.GRAY else Color.BLACK
        binding.etEmail.setTextColor(color)
    }

    private fun choseOption() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.label_chose_option))

        val option = arrayOf("Gallery", "Camera", "Cancel")
        builder.setItems(
            option
        ) { dialog, which ->
            when (which) {
                0 -> {
                    loadImage()
                }
                1 -> {
                    captureImage()
                }
                2 -> {
                    dialog.dismiss()
                }
            }
        }

        val dialog: AlertDialog = builder.create()
        if (isInEditMode) {
            dialog.show()
        }
    }

    private fun captureImage() {
        resultCameraLauncher.launch(
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        )
    }

    private fun loadImage() {

        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        resultGalleryLauncher.launch(
            galleryIntent
        )

    }


    private val resultGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let {
                    val filePath = MediaFileUtils.handleUri(requireContext(), it)
                    startCrop(it!!)
                    binding.profileImg.setImageURI(it)
                    accountSettingViewModel.updateProfileImage(filePath!!)
                }
            }
        }


    private val resultCameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let {
                    val filePath = MediaFileUtils.handleUri(requireContext(), it)
                    binding.profileImg.setImageURI(it)
                    startCrop(it!!)
                    binding.profileImg.setImageURI(it)
                    accountSettingViewModel.updateProfileImage(filePath!!)
                }
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
                    it.data
                    accountSettingViewModel.uploadedFilePath = it.data.firstOrNull()
                }
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri: Uri = result.uri
                val filePath = MediaFileUtils.handleUri(requireContext(), resultUri)
                binding.profileImg.setImageURI(resultUri)
                accountSettingViewModel.updateProfileImage(filePath!!)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setData(consumer: Consumer?) {
        binding.profileImg.setImageURI(consumer?.profileImage)
        binding.tvProfileName.setText(consumer?.fullName)
        binding.etFirstName.setText(consumer?.firstName)
        binding.etLastName.setText(consumer?.lastName)
        binding.etEmail.setText(consumer?.email)
        binding.etPhone.setText(consumer?.phoneNumber?.number)
    }

}