package com.sid.app.verbpractice.helper

import android.util.Log
import com.sid.app.verbpractice.enums.Person
import com.sid.app.verbpractice.enums.VerbForm

class Conjugation() {
    lateinit var verb: String
    lateinit var tense: VerbForm
    lateinit var personMap: HashMap<Person, String?>
    var person = -1

    constructor(conjugation: PotentialConjugation): this() {
        verb = conjugation.verb
        tense = conjugation.tense
        personMap = conjugation.personMap
    }

    constructor(conjugationParcel: ConjugationParcel) : this() {
        verb = conjugationParcel.verb
        tense = conjugationParcel.tense
        personMap = hashMapOf(*(conjugationParcel.persons).zip(conjugationParcel.verbConjugations).toTypedArray())
        person = conjugationParcel.person
        conjugationParcel.persons.forEachIndexed { index, person ->
            Log.v("parcelTest", person.toString() + ": " + conjugationParcel.verbConjugations[index])
        }
    }
}