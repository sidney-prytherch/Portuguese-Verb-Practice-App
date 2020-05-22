package com.sid.app.verbpractice.helper

import android.os.Parcelable
import com.sid.app.verbpractice.enums.Person
import com.sid.app.verbpractice.enums.VerbForm
import kotlinx.android.parcel.Parcelize

@Parcelize
class ResultParcel(
    val verb: String,
    val tense: VerbForm,
    val person: Person,
    val personsString: String,
    val verbConjugation: String?,
    var input: String,
    val isFirst: Boolean,
    val count: Int
) : Parcelable