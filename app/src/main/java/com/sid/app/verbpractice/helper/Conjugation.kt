package com.sid.app.verbpractice.helper

import android.util.Log
import com.sid.app.verbpractice.enums.Person
import com.sid.app.verbpractice.enums.VerbForm

class Conjugation() {
    lateinit var verb: String
    lateinit var enVerb: String
    lateinit var tense: VerbForm
    lateinit var personMap: HashMap<Person, String?>
    var person = -1

    constructor(conjugation: PotentialConjugation): this() {
        verb = conjugation.verb
        enVerb = conjugation.enVerb
        tense = conjugation.tense
        personMap = conjugation.personMap
    }

    constructor(conjugationParcel: ConjugationParcel) : this() {
        verb = conjugationParcel.verb
        enVerb = conjugationParcel.enVerb
        tense = conjugationParcel.tense
        personMap = hashMapOf(*(conjugationParcel.persons).zip(conjugationParcel.verbConjugations).toTypedArray())
        person = conjugationParcel.person
        Log.v("parcelTest", verb)
        Log.v("parcelTest", enVerb)
        Log.v("parcelTest", tense.toString())
        conjugationParcel.persons.forEachIndexed { index, person ->
            Log.v("parcelTest", person.toString() + ": " + conjugationParcel.verbConjugations[index])
        }
    }
}