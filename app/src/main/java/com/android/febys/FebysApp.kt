package com.android.febys

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.stetho.BuildConfig
import com.facebook.stetho.Stetho
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FebysApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG)
            Stetho.initializeWithDefaults(this)

        Fresco.initialize(this)
    }
}