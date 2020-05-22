package com.sid.app.verbpractice.helper

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ConjugationArrayParcel(val conjugations: Array<ConjugationParcel>): Parcelable