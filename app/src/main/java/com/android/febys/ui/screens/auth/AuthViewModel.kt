package com.android.febys.ui.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.febys.base.BaseViewModel
import com.android.febys.repos.IAuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: IAuthRepo
) : BaseViewModel() {
}