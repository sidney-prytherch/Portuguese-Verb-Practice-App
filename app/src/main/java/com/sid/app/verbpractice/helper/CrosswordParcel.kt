package com.sid.app.verbpractice.helper

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CrosswordParcel(
    val crosswordLetters: CharArray,
    val acrossRoots: IntArray,
    val downRoots: IntArray,
    val crosswordHints: Array<String>,
    val crosswordWords: Array<String>,
    val crosswordPtInfinitives: Array<String>,
    val crosswordEnTranslations: Array<String>,
    val wordStartCoords: Array<Triple<Int, Int, Boolean>>
): Parcelable
//class WordsearchParcel(
//    val wordsearchCells: Array<Array<WordsearchCellParcel>>
//) : Parcelable