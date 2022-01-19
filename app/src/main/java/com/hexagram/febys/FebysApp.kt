package com.hexagram.febys

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.core.ImageTranscoderType
import com.facebook.imagepipeline.core.MemoryChunkType
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import com.paypal.checkout.config.SettingsConfig
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.UserAction
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FebysApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Fresco.initialize(
            this,
            ImagePipelineConfig.newBuilder(applicationContext)
                .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                .experiment().setNativeCodeDisabled(true)
                .build()
        )

        val environment = if (BuildConfig.DEBUG) Environment.SANDBOX else Environment.LIVE
        // todo change client id
        val clientId = if (BuildConfig.DEBUG)
            "AVxMDtg2UkfX0IFBK86r_l_EcCeloAcMmOQf7vbOuPQsr10I5QJBf-u4YVn504puI-GyLQ0ZcKRYBG2T"
        else
            "AVxMDtg2UkfX0IFBK86r_l_EcCeloAcMmOQf7vbOuPQsr10I5QJBf-u4YVn504puI-GyLQ0ZcKRYBG2T"
        val config = CheckoutConfig(
            application = this,
            clientId = clientId,
            environment = environment,
            returnUrl = "${BuildConfig.APPLICATION_ID}://paypalpay",
            currencyCode = CurrencyCode.USD,
            userAction = UserAction.PAY_NOW,
            settingsConfig = SettingsConfig(loggingEnabled = true)
        )
        PayPalCheckout.setConfig(config)
    }
}