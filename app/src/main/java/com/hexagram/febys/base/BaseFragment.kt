package com.hexagram.febys.base

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.hexagram.febys.dataSource.ICartDataSource
import com.hexagram.febys.dataSource.IUserDataSource
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

        setupObserver()
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

    fun signOut() {
        _observeUserLoggedIn.postValue(isUserLoggedIn)
    }

    open fun getTvCartCount(): TextView? = null

}