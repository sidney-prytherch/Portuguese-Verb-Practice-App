package com.sid.app.verbpractice.helper

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class WordsearchParcel(
    val wordsearchHints: Array<String>,
    val wordsearchWords: Array<String>,
    val wordsearchPtInfinitives: Array<String>,
    val wordsearchEnTranslations: Array<String>,
    val wordsearchCoordinates: IntArray,
    val wordsearchLetters: CharArray
): Parcelable
//class WordsearchParcel(
//    val wordsearchCells: Array<Array<WordsearchCellParcel>>
//) : Parcelable