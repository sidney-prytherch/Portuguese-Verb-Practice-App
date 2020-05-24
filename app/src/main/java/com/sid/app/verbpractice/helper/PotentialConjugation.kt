package com.sid.app.verbpractice.helper

import android.util.Log
import com.sid.app.verbpractice.enums.Person
import com.sid.app.verbpractice.enums.VerbForm

class PotentialConjugation(val verb: String, val enVerb: String, val tense: VerbForm, val personMap: HashMap<Person, String?>) {
    var isValidPick = true
    val selectedPersons = mutableListOf<Person>()
    fun getRandomPerson(
        isFullConjugation: Boolean,
        personFrequencyMap: Map<Person, Int>
    ): Person? {
        if (!isFullConjugation) {
            val validPersons = personFrequencyMap.filterKeys { it in personMap.keys }.filterKeys { it !in selectedPersons }
            val validAccumFrequencies = validPersons.values.toTypedArray()
            validAccumFrequencies.forEachIndexed {i , _ ->
                if (i != 0) {
                    validAccumFrequencies[i] += validAccumFrequencies[i-1]
                }
            }
            val accumFrequenciesMap = mapOf(*validPersons.keys.toTypedArray().zip(validAccumFrequencies).toTypedArray())
            val lastValue = validAccumFrequencies[validAccumFrequencies.size - 1]
            val randomValue = (0 until lastValue).random()
            for ((person, freq) in accumFrequenciesMap) {
                if (randomValue < freq) {
                    selectedPersons += person
                    if (validPersons.filterKeys { it != person }.isEmpty()) {
                        isValidPick = false
                    }
                    return person
                }
            }
            Log.v("error log", "getRandomPerson didn't return a person correctly :o")
            return accumFrequenciesMap.keys.toTypedArray().random()

        }
        return null
    }
}