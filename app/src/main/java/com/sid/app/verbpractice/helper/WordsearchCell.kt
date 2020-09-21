package com.sid.app.verbpractice.helper

import android.widget.TextView

class WordsearchCell(val view: TextView, val y: Int, val x: Int) {
    lateinit var node: Wordsearch.Node
}