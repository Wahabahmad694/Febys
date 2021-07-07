package com.hexagram.febys.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.hexagram.febys.R
import com.hexagram.febys.databinding.PasswordEditTextBinding

class PasswordEditText : ConstraintLayout {
    val binding =
        PasswordEditTextBinding.inflate(LayoutInflater.from(context), this, true)

    val text
        get() = binding.etPassword.text

    var error: String? = null
        set(value) {
            binding.etPassword.error = error
            field = value
        }

    var isPasswordVisible = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    init {
        uiListener()
    }

    private fun uiListener() {
        binding.btnPasswordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            val resId = if (isPasswordVisible) {
                binding.etPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                R.drawable.ic_password_show
            } else {
                binding.etPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                R.drawable.ic_password_hide
            }

            binding.btnPasswordToggle.setImageResource(resId)

            // move cursor to the end of line
            binding.etPassword.setSelection(binding.etPassword.text.toString().length)
        }
    }

    inline fun addTextChangedListener(
        crossinline beforeTextChanged: (
            text: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) -> Unit = { _, _, _, _ -> },
        crossinline onTextChanged: (
            text: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) -> Unit = { _, _, _, _ -> },
        crossinline afterTextChanged: (text: Editable?) -> Unit = {}
    ): TextWatcher {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                afterTextChanged.invoke(s)
            }

            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                beforeTextChanged.invoke(text, start, count, after)
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                onTextChanged.invoke(text, start, before, count)
            }
        }
        binding.etPassword.addTextChangedListener(textWatcher)

        return textWatcher
    }

    fun clearError() = binding.etPassword.clearError()
}