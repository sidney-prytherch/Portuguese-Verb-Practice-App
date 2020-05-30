package com.sid.app.verbpractice.helper

import java.util.*

object StringHelper {
    fun capitalize(word: String?): String {
        return when {
            word != null && word.isNotEmpty() -> word[0].toUpperCase() + word.substring(1).toLowerCase(Locale.ROOT)
            else -> ""
        }
    }

    fun specialCapWords(words: String?): String {
        if (words == null || words.isEmpty()) {
            return ""
        }
        var retSoFar = ""
        for (word in words.split("/")) {
            retSoFar += specialCap(word) + "/"
        }
        return if (retSoFar.isNotEmpty()) retSoFar.substring(0, retSoFar.length - 1) else ""
    }

    private fun specialCap(word: String?): String {
        if (word != null) {
            var retSoFar = ""
            for (letter in word) {
                when {
                    retSoFar.contains("...") -> {
                        return word
                    }
                    letter.isLetter() -> {
                        return retSoFar + letter.toUpperCase() + word.substring(retSoFar.length + 1, word.length)
                    }
                    else -> {
                        retSoFar += letter
                    }
                }
            }
        }
        return ""
    }
}