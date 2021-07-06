package com.hexagram.febys.ui.screens.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseDialog
import com.hexagram.febys.databinding.DialogErrorBinding
import com.hexagram.febys.network.DataState

class ErrorDialog<T>(
    private val error: DataState.Error<T>
) : com.hexagram.febys.base.BaseDialog() {

    companion object {
        const val TAG = "ErrorDialog"
    }

    private lateinit var binding: DialogErrorBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (error) {
            is DataState.ApiError -> {
                setupApiErrorDialog(error.message)
            }
            is DataState.ExceptionError -> {
                setupExceptionDialog()
            }
            is DataState.NetworkError -> {
                setupNetworkErrorDialog()
            }
        }

        uiListener()
    }

    private fun uiListener() {
        binding.ivClose.setOnClickListener {
            dismiss()
        }

        binding.btnOkay.setOnClickListener {
            dismiss()
        }
    }

    private fun setupApiErrorDialog(message: String) {
        binding.isNetworkError = false
        binding.ivError.setImageResource(R.drawable.ic_error)
        binding.tvErrorTitle.text = getString(R.string.label_try_again)
        binding.tvErrorMsg.text = message
    }

    private fun setupExceptionDialog() {
        binding.isNetworkError = false
        binding.ivError.setImageResource(R.drawable.ic_error)
        binding.tvErrorTitle.text = getString(R.string.label_try_again)
        binding.tvErrorMsg.text = getString(R.string.error_something_went_wrong)
    }

    private fun setupNetworkErrorDialog() {
        binding.isNetworkError = true
        binding.ivError.setImageResource(R.drawable.ic_no_internet)
        binding.tvErrorTitle.text = getString(R.string.label_connection_error)
        binding.tvErrorMsg.text = getString(R.string.label_no_internet)
    }

    override fun cancelable() = true
}