package com.sid.app.verbpractice.helper

import android.os.Parcelable
import com.sid.app.verbpractice.enums.Person
import com.sid.app.verbpractice.enums.VerbForm
import kotlinx.android.parcel.Parcelize

@Parcelize
class ConjugationParcel(
    val verb: String,
    val enVerb: String,
    val tense: VerbForm,
    val persons: Array<Person>,
    val verbConjugations: Array<String?>,
    var person: Int
) : Parcelable