package com.android.febys.ui.screens.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.android.febys.R
import com.android.febys.base.BaseFragment
import com.android.febys.network.DataState
import com.android.febys.ui.screens.dialog.ErrorDialog
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

abstract class AuthFragment : BaseFragment() {
    private lateinit var googleClient: GoogleSignInClient

    private var googleSignInCallback: ((token: String) -> Unit)? = null
    private lateinit var googleSignInActivityForResult: ActivityResultLauncher<Intent>

    private var fbSignInCallback: ((token: String) -> Unit)? = null
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        googleSignInActivityForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    googleSignInCallback?.invoke(account.idToken!!)
                    googleClient.signOut()
                } catch (e: ApiException) {}
            }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client))
            .requestEmail()
            .build()

        googleClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    fun signInWithGoogle(callback: (idToken: String) -> Unit) {
        googleSignInCallback = callback
        val signInIntent = googleClient.signInIntent
        googleSignInActivityForResult.launch(signInIntent)
    }

    fun signInWithFacebook(callback: (idToken: String) -> Unit) {
        fbSignInCallback = callback

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(result: LoginResult?) {
                    result?.accessToken?.token?.let {
                        fbSignInCallback?.invoke(it)
                        LoginManager.getInstance().logOut()
                    }
                }

                override fun onCancel() {}

                override fun onError(exception: FacebookException) {}
            })

        LoginManager.getInstance().logIn(this, mutableListOf())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}