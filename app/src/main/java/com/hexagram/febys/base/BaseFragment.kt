package com.hexagram.febys.base

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.dataSource.ICartDataSource
import com.hexagram.febys.dataSource.IUserDataSource
import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.utils.navigateTo
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseFragment : Fragment() {
    @Inject
    lateinit var userDataSource: IUserDataSource

    @Inject
    lateinit var cartDataSource: ICartDataSource

    val isUserLoggedIn: Boolean
        get() = userDataSource.getConsumer() != null

    val consumer: Consumer?
        get() = userDataSource.getConsumer()

    private val _observeUserLoggedIn = MutableLiveData(false)
    val observesUserLoggedIn = _observeUserLoggedIn

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListener()
        setupObserver()
    }

    private fun setupListener() {
        getIvCart()?.setOnClickListener {
            navigateToCart()
        }

        getTvCartCount()?.setOnClickListener {
            navigateToCart()
        }
    }

    private fun setupObserver() {
        if (_observeUserLoggedIn.value != isUserLoggedIn) {
            _observeUserLoggedIn.postValue(isUserLoggedIn)
        }

        cartDataSource.observeCartCount().observe(viewLifecycleOwner) { cartCount ->
            getTvCartCount()?.text = cartCount?.toString() ?: "0"
        }
    }

    private fun navigateToCart() {
        val navigateToCart = NavGraphDirections.actionToCartFragment()
        navigateTo(navigateToCart)
    }

    fun signOut() {
        _observeUserLoggedIn.postValue(isUserLoggedIn)
    }

    fun specificTextColorChange(text:String,start:Int,end:Int,textView: TextView){
        val spannable: Spannable = SpannableString(text)

        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.red)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView.setText(spannable, TextView.BufferType.SPANNABLE)
    }

    open fun getTvCartCount(): TextView? = null
    open fun getIvCart(): View? = null
}