package com.hexagram.febys.utils

import android.util.Patterns

object Validator {
    fun isValidEmail(email: String): Boolean =
        email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPhone(number: String): Boolean =
        number.isNotEmpty() && Patterns.PHONE.matcher(number).matches()

    fun isValidName(name: String): Boolean = name.trim().isNotEmpty()

    fun isValidPassword(password: String): Boolean = password.trim().isNotEmpty()

    fun isValidAddress(address: String): Boolean = address.trim().isNotEmpty()

    fun isValidCity(city: String): Boolean = city.trim().isNotEmpty()

    fun isValidState(state: String): Boolean = state.trim().isNotEmpty()

    fun isValidCountry(country: String): Boolean = country.trim().isNotEmpty()

    fun isValidPostalCode(postalCode: String): Boolean = postalCode.trim().isNotEmpty()
}