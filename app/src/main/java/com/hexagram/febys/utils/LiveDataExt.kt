package com.hexagram.febys.utils

import android.os.Looper
import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.update(incoming: T) {
    if (value != incoming) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            value = incoming
        }
        else {
            postValue(value)
        }
    }
}