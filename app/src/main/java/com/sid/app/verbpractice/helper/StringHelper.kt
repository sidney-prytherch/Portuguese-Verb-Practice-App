package com.sid.app.verbpractice.helper

import java.util.*

object StringHelper {
    fun capitalize(word: String?): String {
        return when {
            word != null && word.isNotEmpty() -> word[0].toUpperCase() + word.substring(1).toLowerCase(Locale.ROOT)
            else -> ""
        }
    }
}