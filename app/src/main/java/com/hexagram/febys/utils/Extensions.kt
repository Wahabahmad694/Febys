package com.hexagram.febys.utils

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hexagram.febys.base.BaseActivity
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.toggleVisibility() {
    isVisible = !isVisible
}

fun View.fadeVisibility(isVisible: Boolean, duration: Long = 400) {
    val transition: Transition = Fade()
    transition.duration = duration
    transition.addTarget(this)
    TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
    this.isVisible = isVisible
}

fun Boolean.applyToViews(vararg views: View) {
    views.forEach { view -> view.isVisible = this }
}

/**
 * return scroll position range 0 to 1
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

fun Activity.hideKeyboard() {
    WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.ime())
}

fun Fragment.showToast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    activity?.showToast(msg, length)
}

fun Fragment.navigateTo(directions: NavDirections) = findNavController().navigate(directions)

fun Fragment.goBack() = findNavController().popBackStack()

fun Fragment.showLoader() {
    (activity as? BaseActivity)?.showLoader()
}

fun Fragment.hideLoader() {
    (activity as? BaseActivity)?.hideLoader()
}

fun Fragment.hideKeyboard() {
    activity?.hideKeyboard()
}

fun Fragment.hideViews(vararg views: View) {
    views.forEach { view -> view.hide() }
}

fun Fragment.showViews(vararg views: View) {
    views.forEach { view -> view.show() }
}

fun Fragment.showErrorDialog(msg: String) {
    ErrorDialog<String>(DataState.ApiError(msg)).show(childFragmentManager, ErrorDialog.TAG)
}

fun RecyclerView.applySpaceItemDecoration(
    @DimenRes verticalDimenRes: Int? = null,
    @DimenRes horizontalDimenRes: Int? = null
) {
    val verticalSpace =
        verticalDimenRes?.let { resources.getDimension(verticalDimenRes).toInt() } ?: 0

    val horizontalSpace =
        horizontalDimenRes?.let { resources.getDimension(horizontalDimenRes).toInt() } ?: 0

    addItemDecoration(
        SpaceItemDecoration(
            vertical = verticalSpace,
            horizontal = horizontalSpace
        )
    )
}

fun EditText.clearError() {
    error = null
}

fun EditText.onSearch(callback: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            clearFocus()
            callback.invoke()
            return@setOnEditorActionListener true
        }
        false
    }
}

fun <T : View> BottomSheetBehavior<T>.onStateChange(callback: (state: Int) -> Unit) {
    addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            callback(newState)
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}  // do nothing
    })
}

fun Double.toFixedDecimal(decimalCount: Int): String {
    return String.format("%.${decimalCount}f", this)
}

fun SimpleDraweeView.load(imageUrl: String?) {
    if (imageUrl.isNullOrEmpty()) return
    hierarchy.fadeDuration = 200
    setImageURI(imageUrl)
}