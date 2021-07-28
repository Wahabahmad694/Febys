package com.hexagram.febys.base

import android.graphics.Rect
import android.view.MotionEvent
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.hexagram.febys.ui.screens.dialog.LoaderDialog
import com.hexagram.febys.utils.hideKeyboard

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

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getLocalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    hideKeyboard()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}