package com.hexagram.febys.utils

fun String.isStringEmpty(): Boolean = this.isEmpty() || this.isBlank()
fun String.isStringNotEmpty(): Boolean = !(this.isEmpty() || this.isBlank())