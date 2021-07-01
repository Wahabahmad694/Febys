package com.android.febys.ui.screens.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.android.febys.base.BaseDialog
import com.android.febys.databinding.DialogErrorBinding
import com.android.febys.utils.goBack

class ErrorDialog : BaseDialog() {
    private lateinit var binding: DialogErrorBinding
    private val args: ErrorDialogArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener {
            goBack()
        }

        binding.btnDismiss.setOnClickListener {
            goBack()
        }
    }

    override fun cancelable() = args.isCancelable
}