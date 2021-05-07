package com.sid.app.verbpractice.helper

import android.util.Log
import com.sid.app.verbpractice.enums.EnglishHelper
import com.sid.app.verbpractice.enums.Person
import com.sid.app.verbpractice.enums.VerbForm
import java.util.*

object ConjugatorEnglish {

    private fun getGeneralizedSubject(ptString: String): String {
        return when (ptString) {
            "Eu" -> "I"
            "Tu" -> "you"
            "Você", "Você/Ele/Ela", "A senhora", "O senhor", "Ele", "Ela" -> "(you/he/she)"
            "Nós" -> "we"
            "Vós" -> "you all (obsolete)"
            "Vocês", "As senhoras", "Os senhores", "Vocês/Eles/Elas", "Eles", "Elas" -> "(you all/they)"
            else -> ""
        }
    }

    private fun getSubject(ptString: String): String {
        return when (ptString) {
            "Eu" -> "I"
            "Tu" -> "you"
            "Você" -> "you"
            "Você/Ele/Ela" -> "you/he/she"
            "A senhora" -> "you (formal, fem.)"
            "O senhor" -> "you (formal, masc.)"
            "Ele" -> "he"
            "Ela" -> "she"
            "Nós" -> "we"
            "Vós" -> "you all (obsolete)"
            "Vocês" -> "you all"
            "Vocês/Eles/Elas" -> "you all/they"
            "As senhoras" -> "you all (formal, fem.)"
            "Os senhores" -> "you all (formal, masc.)"
            "Eles" -> "they (masc.)"
            "Elas" -> "they (fem.)"
            else -> ""
        }
    }

    private fun getPersonFromPtString(ptString: String): Person {
        return when (ptString) {
            "Eu" -> Person.FIRST_SING
            "Tu", "Você", "A senhora", "O senhor" -> Person.SECOND_SING
            "Ele", "Ela", "Você/Ele/Ela" -> Person.THIRD_SING
            "Nós" -> Person.FIRST_PLUR
            "Eles", "Elas", "Vocês/Eles/Elas" -> Person.THIRD_PLUR
            else -> Person.SECOND_PLUR
        }
    }

    private fun getObjectPronounFromPtString(ptString: String): String {
        return when (ptString) {
            "Eu" -> "me"
            "Tu", "Você" -> "you"
            "Você/Ele/Ela" -> "you/him/her"
            "A senhora" -> "you (formal, fem.)"
            "O senhor" -> "you (formal, masc.)"
            "Ele" -> "him"
            "Ela" -> "her"
            "Nós" -> "us"
            "Vós" -> "you all (obsolete)"
            "Vocês" -> "you all"
            "Vocês/Eles/Elas" -> "you all/them"
            "As senhoras" -> "you all (formal, fem.)"
            "Os senhores" -> "you all (formal, masc.)"
            "Eles" -> "them (masc.)"
            "Elas" -> "them (fem.)"
            else -> ""
        }
    }

    fun conjugate(infinitiveUnformatted: String, verbForm: VerbForm, ptPersonStrings: Array<String>,
                          gerund: String, pastPart: String, presOne: String, presTwo: String, past: String): Array<String> {
        return conjugateHelper(infinitiveUnformatted, verbForm, ptPersonStrings, gerund, pastPart, presOne, presTwo, past, false)
    }

    fun conjugate(infinitiveUnformatted: String, verbForm: VerbForm, ptPersonStrings: Array<String>,
                  gerund: String, pastPart: String, presOne: String, presTwo: String, past: String, generalPronouns: Boolean): Array<String> {
        return conjugateHelper(infinitiveUnformatted, verbForm, ptPersonStrings, gerund, pastPart, presOne, presTwo, past, generalPronouns)
    }

    private fun conjugateHelper(infinitiveUnformatted: String, verbForm: VerbForm, ptPersonStrings: Array<String>,
        gerund: String, pastPart: String, presOne: String, presTwo: String, past: String, generalPronouns: Boolean): Array<String> {
        val infinitive = infinitiveUnformatted.toLowerCase(Locale.ROOT)
        val persons = ptPersonStrings.map { getPersonFromPtString(it) }.toTypedArray()
        val subjectStrings = ptPersonStrings.map{ if (generalPronouns) getGeneralizedSubject(it) else getSubject(it) }.toTypedArray()
        val objectStrings = ptPersonStrings.map{ getObjectPronounFromPtString(it) }.toTypedArray()
        val subjectStringsSimplified = subjectStrings.map{ it.replace(Regex("\\(.+\\)"), "").trim() }.toTypedArray()
        val objectStringsSimplified = objectStrings.map{ it.replace(Regex("\\(.+\\)"), "").trim() }.toTypedArray()
        val verbData =
            if (gerund != "") VerbData(infinitive, gerund, pastPart, presOne, presTwo, past)
            else VerbData("do", "doing", "done", "do", "does", "did")
        val conjugatedVerbs = conjugateVerbForm(verbData, verbForm)
        return persons.mapIndexed { i, it ->
            StringHelper.specialCapWords(conjugatedVerbs[it]
                ?.replaceFirst("_", subjectStrings[i])
                ?.replace("_", subjectStringsSimplified[i])
                ?.replaceFirst("~", objectStrings[i])
                ?.replace("~", objectStringsSimplified[i])
                ?: "").replace("\\", "/")
        }.toTypedArray()
    }

    private fun conjugateVerbForm(verbData: VerbData, verbForm: VerbForm): Map<Person, String> {
        val allPersons = arrayOf(
            Person.FIRST_SING, Person.SECOND_SING, Person.THIRD_SING,
            Person.FIRST_PLUR, Person.SECOND_PLUR, Person.THIRD_PLUR
        )
        val conjugation = when (verbForm) {
            VerbForm.PRES_IND -> getPresInd(verbData).map { "<$it>" }.toTypedArray()
            VerbForm.PRET_IND -> getPretInd(verbData).map { "<$it>" }.toTypedArray()
            VerbForm.IMP_IND -> getConjugatedHelper(EnglishHelper.TO_BE_PAST).map {
                "<_ used to ${verbData.infinitive}>/<$it ${verbData.gerund}> (while...) [simp]"
            }.toTypedArray()
            VerbForm.SIMP_PLUP_IND -> Array(6) { "<_ had ${verbData.pastPart}> [simp]" }
            VerbForm.SIMP_FUT_IND -> Array(6) { "<_ will ${verbData.infinitive}>" }
            VerbForm.COND_IND -> Array(6) {"<_ would ${verbData.infinitive}> (if...)"}
            VerbForm.FUT_IND -> getConjugatedHelper(EnglishHelper.TO_BE_PRES).map {
                "<$it going to ${verbData.infinitive}>"
            }.toTypedArray()
            VerbForm.PRES_PERF -> getConjugatedHelper(EnglishHelper.TO_HAVE_PRES).map {
                "<$it been ${verbData.gerund}>/<$it ${verbData.pastPart}> (a lot lately) [ter]"
            }.toTypedArray()
            VerbForm.PLUP -> Array(6) { "<_ had ${verbData.pastPart}> [comp]" }
            VerbForm.FUT_PERF -> Array(6) { "<_ will have ${verbData.pastPart}>" }
            VerbForm.COND_PERF -> Array(6) { "<_ would have ${verbData.pastPart}> (if...)" }
            VerbForm.PAST_INTENT -> getConjugatedHelper(EnglishHelper.TO_BE_PAST).map {
                "<$it going to ${verbData.infinitive}>/<_ would ${verbData.infinitive}>"
            }.toTypedArray()
            VerbForm.PRES_PROG -> getConjugatedHelper(EnglishHelper.TO_BE_PRES).map {
                "<$it ${verbData.gerund}>"
            }.toTypedArray()
            VerbForm.PRET_PROG -> Array(6) { "<_ had been ${verbData.gerund}>" }
            VerbForm.IMP_PROG -> getConjugatedHelper(EnglishHelper.TO_BE_PAST).map {
                "<_ used to ${verbData.infinitive}>/<$it ${verbData.gerund}> (while...) [comp]"
            }.toTypedArray()
            VerbForm.SIMP_PLUP_PROG -> getConjugatedHelper(EnglishHelper.TO_HAVE_PRES).map {
                "<$it been ${verbData.gerund}>/<$it ${verbData.pastPart}> (a lot lately\\repeatedly lately) [estar]"
            }.toTypedArray()
            VerbForm.FUT_PROG -> Array(6) { "<_ will be ${verbData.gerund}>" }
            VerbForm.COND_PROG -> Array(6) { "<_ would be ${verbData.gerund}> (if...)" }
            VerbForm.PRES_PERF_PROG -> getConjugatedHelper(EnglishHelper.TO_HAVE_PRES).map {
                "<$it been ${verbData.gerund}> [unnatural - present perfect compound tense is more common]"
            }.toTypedArray()
            VerbForm.PLUP_PROG -> getConjugatedHelper(EnglishHelper.TO_HAVE_PRES).map {
                "<$it been ${verbData.gerund}> (during...\\while...)"
            }.toTypedArray()
            VerbForm.FUT_PERF_PROG -> Array(6) { "<_ will have been ${verbData.gerund}> [very unnatural tense - avoid]" }
            VerbForm.COND_PERF_PROG -> Array(6) { "<_ would have been ${verbData.gerund}> (if...) [very unnatural tense - avoid]" }
            VerbForm.PRES_SUBJ -> getPresInd(verbData).map {
                "(...that) <$it>/<_ may ${verbData.infinitive}>/maybe <_ will ${verbData.infinitive}>/I want <~ to ${verbData.infinitive}>"
            }.toTypedArray()
            VerbForm.PRES_PERF_SUBJ -> Array(6) { "(...that) <_ had ${verbData.pastPart}>/<_ may have ${verbData.pastPart}>/<_ probably will have ${verbData.pastPart}> (by then)" }
            VerbForm.IMP_SUBJ -> Array(6) { "I would like <~ to ${verbData.infinitive}>/I wanted <~ to ${verbData.infinitive}>" }
            VerbForm.PLUP_SUBJ -> Array(6) { "I would be feeling (something) if <_ had ${verbData.pastPart}> (before...)/if <_ had ${verbData.pastPart}>, (something would've happened)" }
            VerbForm.FUT_SUBJ -> getPresInd(verbData).map {
                "when <$it>, (something will happen)/if <$it>, (at some point in the future, something will happen)"
            }.toTypedArray()
            VerbForm.FUT_PERF_SUBJ -> getConjugatedHelper(EnglishHelper.TO_HAVE_PRES).map {
                "when <$it ${verbData.pastPart}>, (something will happen)/as soon as <$it ${verbData.pastPart}>, (something will happen)"
            }.toTypedArray()
            VerbForm.IMP_AFF -> Array(6) {verbData.infinitive}.map { "<$it>" }.toTypedArray()
            VerbForm.IMP_NEG -> Array(6) {verbData.infinitive}.map { "<$it>" }.toTypedArray()
            VerbForm.PERS_INF -> Array(6) {"for <_ to ${verbData.infinitive}>"}
        }
        return mapOf(*allPersons.zip(conjugation).toTypedArray())
    }

    private fun getConjugatedHelper(helper: EnglishHelper): Array<String> {
        return when (helper) {
            EnglishHelper.TO_BE_PRES -> arrayOf("_ am", "_ are", "_ is", "_ are", "_ are", "_ are")
            EnglishHelper.TO_BE_PAST -> arrayOf("_ was", "_ were", "_ was", "_ were", "_ were", "_ were")
            EnglishHelper.TO_HAVE_PRES -> arrayOf("_ have", "_ have", "_ has", "_ have", "_ have", "_ have")
        }
    }

    private fun getPresInd(verbData: VerbData): Array<String> {
        return when (verbData.infinitive) {
            "be" -> arrayOf("_ am", "_ are", "_ is", "_ are", "_ are", "_ are")
            else -> Array(6) { "_ ${if (it == 2) verbData.presTwo else verbData.presOne}" }
        }
    }

    private fun getPretInd(verbData: VerbData): Array<String> {
        return when (verbData.infinitive) {
            "be" -> arrayOf("_ was", "_ were", "_ was", "_ were", "_ were", "_ were")
            else -> Array(6) { "_ ${verbData.past}" }
        }
    }
    //ivate class verbData(val       TO DO                 DOING                DONE                  DO                 DOES                DID
    private class VerbData(val infinitive: String, val gerund: String, val pastPart: String, val presOne: String, val presTwo: String, val past: String)
}