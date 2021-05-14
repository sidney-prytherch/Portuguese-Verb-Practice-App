package com.sid.app.verbpractice.helper

import android.widget.TextView

class CrosswordCell(val view: TextView, val y: Int, val x: Int) {
    lateinit var node: Crossword.Node
}