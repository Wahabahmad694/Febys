package com.hexagram.febys.utils

import android.util.Patterns

object Validator {
    fun isValidEmail(email: String): Boolean =
        email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPhone(number: String): Boolean =
        number.isNotEmpty() && Patterns.PHONE.matcher(number).matches()

    fun isValidName(name: String): Boolean = name.isNotEmpty()

    fun isValidPassword(password: String): Boolean = password.isNotEmpty()

    fun isValidAddress(address: String): Boolean = address.isNotEmpty() && address.length > 6

    fun isValidCity(city: String): Boolean = city.isNotEmpty() && city.length > 3

    fun isValidState(state: String): Boolean = state.isNotEmpty() && state.length > 3

    fun isValidPostalCode(postalCode: String): Boolean =
        postalCode.isNotEmpty() && postalCode.length >= 4
}