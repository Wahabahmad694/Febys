package com.android.febys.utils

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Scroll position range 0 to 1
 */
fun RecyclerView.getHorizontalScrollPosition(): Float {
    val offset = computeHorizontalScrollOffset()
    val range = computeHorizontalScrollRange()
    val extent = computeHorizontalScrollExtent()
    return offset.toFloat().div(range.minus(extent))
}

fun Activity.showToast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, length).show()
}

fun Fragment.showToast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, msg, length).show()
}

fun <T> Activity.getErrorMessage(e: Resource.Error<T>): String {
    return when (e.errorMessage) {
        is ErrorMessage.ErrorRes -> {
            getString(e.errorMessage.resId)
        }
        is ErrorMessage.ErrorString -> {
            e.errorMessage.message
        }
    }
}

fun <T> Fragment.getErrorMessage(e: Resource.Error<T>): String {
    return when (e.errorMessage) {
        is ErrorMessage.ErrorRes -> {
            getString(e.errorMessage.resId)
        }
        is ErrorMessage.ErrorString -> {
            e.errorMessage.message
        }
    }
}