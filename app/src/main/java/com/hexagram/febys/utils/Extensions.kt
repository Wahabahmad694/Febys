package com.hexagram.febys.utils

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseActivity
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.dialog.InfoDialog
import com.hexagram.febys.ui.screens.dialog.WarningDialog
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

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

fun <T> Fragment.showErrorToast(error: DataState.Error<T>, length: Int = Toast.LENGTH_SHORT) {
    val msg = when (error) {
        is DataState.ApiError -> error.message
        is DataState.NetworkError -> getString(R.string.label_no_internet)
        else -> getString(R.string.error_something_went_wrong)
    }
    activity?.showToast(msg, length)
}

fun Fragment.showErrorToast(error: LoadState.Error, length: Int = Toast.LENGTH_SHORT) {
    val msg = if (error.error.cause is IOException) {
        getString(R.string.label_no_internet)
    } else {
        error.error.localizedMessage
            ?: error.error.message
            ?: getString(R.string.error_something_went_wrong)
    }
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

fun Fragment.showWarningDialog(
    @DrawableRes resId: Int, title: String, msg: String, onOkayClick: () -> Unit
) {
    WarningDialog(resId, title, msg) { onOkayClick() }.show(childFragmentManager, InfoDialog.TAG)
}
fun Fragment.showInfoDialoge(
    @DrawableRes resId: Int, title: String, msg: String, onOkayClick: () -> Unit
) {
    InfoDialog(resId, title, msg) { onOkayClick() }.show(childFragmentManager, InfoDialog.TAG)
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

fun Double.roundOffDecimal(): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(this).toDouble()
}

fun SimpleDraweeView.load(imageUrl: String?) {
    if (imageUrl.isNullOrEmpty()) return
    hierarchy.fadeDuration = 200
    setImageURI(imageUrl)
}

fun TextView.setDrawableRes(
    @DrawableRes left: Int = 0,
    @DrawableRes top: Int = 0,
    @DrawableRes right: Int = 0,
    @DrawableRes bottom: Int = 0,
) {
    this.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
}

fun TextView.setBackgroundRoundedColor(color: Int, cornerRadius: Float = 16f) {
    val shape = GradientDrawable()
    shape.cornerRadius = cornerRadius
    shape.setColor(color)
    this.background = shape
}

fun TextView.showHtml(html: String) {
    val spannedText =
        Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
    this.setText(spannedText, TextView.BufferType.SPANNABLE)
}

val String.capitalizeWords
    get() = this.lowercase().split(" ").joinToString(" ") { it.capitalize() }

fun String.capitalize(): String {
    var capitalizeString = ""
    this.split(" ").forEach { word ->
        capitalizeString += word.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        } + " "
    }

    return capitalizeString.trim()
}

fun String.toAscii(): Int {
    var ascii = 0
    this.forEach {
        ascii += it.code
    }

    return ascii
}