package com.hexagram.febys.base

import androidx.appcompat.app.AppCompatActivity
import com.hexagram.febys.ui.screens.dialog.LoaderDialog

abstract class BaseActivity : AppCompatActivity() {
    private val loaderDialog = LoaderDialog()

    fun showLoader() {
        if (!loaderDialog.isAdded)
            loaderDialog.showNow(supportFragmentManager, LoaderDialog.TAG)
    }

    fun hideLoader() {
        if (loaderDialog.isAdded)
            loaderDialog.dismiss()
    }
}