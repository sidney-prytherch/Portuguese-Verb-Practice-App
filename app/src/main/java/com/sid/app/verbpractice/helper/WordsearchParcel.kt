package com.sid.app.verbpractice.helper

import android.os.Parcelable
import com.sid.app.verbpractice.enums.Person
import com.sid.app.verbpractice.enums.VerbForm
import kotlinx.android.parcel.Parcelize

@Parcelize
class WordsearchParcel(
    val wordsearchCells: Array<Array<WordsearchCellParcel>>
) : Parcelable