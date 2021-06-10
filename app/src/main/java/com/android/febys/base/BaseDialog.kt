package com.android.febys.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment

abstract class BaseDialog : DialogFragment() {
    abstract fun cancelable(): Boolean

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        isCancelable = cancelable()
    }
}