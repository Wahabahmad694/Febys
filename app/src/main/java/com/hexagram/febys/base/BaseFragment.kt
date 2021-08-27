package com.hexagram.febys.base

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.dataSource.ICartDataSource
import com.hexagram.febys.dataSource.IUserDataSource
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
        get() = userDataSource.getUser() != null

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
            getTvCartCount()?.isVisible = cartCount != null && cartCount > 0
            getTvCartCount()?.text = cartCount?.toString() ?: ""
        }
    }

    fun navigateToCart() {
        val navigateToCart = NavGraphDirections.actionToCartFragment()
        navigateTo(navigateToCart)
    }

    fun signOut() {
        _observeUserLoggedIn.postValue(isUserLoggedIn)
    }

    open fun getTvCartCount(): TextView? = null
    open fun getIvCart(): View? = null

}