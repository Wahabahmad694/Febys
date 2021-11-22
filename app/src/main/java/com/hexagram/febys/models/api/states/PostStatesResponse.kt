package com.hexagram.febys.models.api.states

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostStatesResponse(
    val states: List<State>
):Parcelable