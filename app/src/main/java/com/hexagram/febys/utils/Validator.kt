package com.hexagram.febys.utils

import android.util.Patterns

object Validator {
    fun isValidEmail(email: String): Boolean =
        email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPhone(number: String): Boolean =
        number.isNotEmpty() && Patterns.PHONE.matcher(number).matches()

    fun isValidName(name: String): Boolean =
        name.trim().isNotEmpty()

    fun isValidPassword(password: String): Boolean =
        password.trim().isNotEmpty() && password.length >= 8

    fun isValidAddress(address: String): Boolean = address.trim().isNotEmpty()

    fun isValidCity(city: String): Boolean = city.trim().isNotEmpty()

    fun isValidState(state: String): Boolean = state.trim().isNotEmpty()

    fun isValidCountry(country: String): Boolean = country.trim().isNotEmpty()

    fun isValidPostalCode(postalCode: String): Boolean = postalCode.trim().isNotEmpty()

    private fun containsEmoji(string: String): Boolean {
        val emojiList = listOf(
            0x1F600..0x1F64F,
            0x1F300..0x1F5FF,
            0x1F680..0x1F6FF,
            0x2600..0x26FF,
            0x2700..0x27BF,
            0xFE00..0xFE0F,
            0x1F900..0x1F9FF,
            0x1F1E6..0x1F1FF
        )
        for (ch in string) {
            emojiList.forEach {
                if (ch.code in it) {
                    return true
                }
            }
        }

        return false
    }
}