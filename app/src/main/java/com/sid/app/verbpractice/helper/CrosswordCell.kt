package com.sid.app.verbpractice.helper

import android.widget.TextView

class CrosswordCell(val view: TextView, val row: Int, val col: Int) {
    lateinit var node: Crossword.Node
}