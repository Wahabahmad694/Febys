package com.android.febys.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.android.febys.dataSource.IUserDataSource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseFragment : Fragment() {
    @Inject
    lateinit var userDataSource: IUserDataSource

    val isUserLoggedIn: Boolean
        get() = userDataSource.getUser() != null

    private val _observeUserLoggedIn = MutableLiveData(false)
    val observesUserLoggedIn = _observeUserLoggedIn

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (_observeUserLoggedIn.value != isUserLoggedIn) {
            _observeUserLoggedIn.postValue(isUserLoggedIn)
        }
    }

    fun signOut() {
        _observeUserLoggedIn.postValue(isUserLoggedIn)
    }

}