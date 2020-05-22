package com.sid.app.verbpractice.helper

import com.sid.app.verbpractice.enums.VerbForm

object ConjugatorEnglish {
    fun getSubject(portuguese: String): String {
        when (portuguese) {
            "Eu" -> return "I"
            "Tu" -> return "You"
            "Você" -> return "You"
            "A senhora" -> return "You (formal, fem.)"
            "O senhor" -> return "You (formal, masc.)"
            "Ele" -> return "He"
            "Ela" -> return "She"
            "Nós" -> return "We"
            "Vós" -> return "You all (obsolete)"
            "Vocês" -> return "You all"
            "As senhoras" -> return "You (formal, fem. pl.)"
            "Os senhores" -> return "You (formal, masc. pl.)"
            "Eles" -> return "They (masc.)"
            "Elas" -> return "They (fem.)"
        }
        return ""
    }

    fun conjugate(infinitive: String?, verbForms: Array<VerbForm?>?): Array<Array<String>> {
        return arrayOf()
    }
}