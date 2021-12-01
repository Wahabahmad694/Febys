package com.hexagram.febys.base

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.hexagram.febys.ui.screens.dialog.LoaderDialog
import com.hexagram.febys.utils.hideKeyboard

abstract class BaseActivity : AppCompatActivity() {
    private val loaderDialog = LoaderDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    fun showLoader() {
        if (!loaderDialog.isAdded)
            loaderDialog.showNow(supportFragmentManager, LoaderDialog.TAG)
    }

    fun hideLoader() {
        if (loaderDialog.isAdded)
            loaderDialog.dismiss()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val focusedView = currentFocus
            if (focusedView is EditText) {
                val outRect = Rect()
                focusedView.getLocalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    focusedView.clearFocus()
                    hideKeyboard()
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}