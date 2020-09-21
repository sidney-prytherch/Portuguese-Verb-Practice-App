package com.sid.app.verbpractice.helper

import android.os.Parcelable
import com.sid.app.verbpractice.enums.Person
import com.sid.app.verbpractice.enums.VerbForm
import kotlinx.android.parcel.Parcelize

@Parcelize
class WordsearchCellParcel(
    val letter: Char,
    val direction: Array<Direction>,
    val relatedCellCoords: Array<Array<Int>>,
    val hints: Array<String?>
) : Parcelable