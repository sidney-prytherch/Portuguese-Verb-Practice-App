package com.sid.app.verbpractice.ui.practice.crossword

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sid.app.verbpractice.MainActivity
import com.sid.app.verbpractice.R
import com.sid.app.verbpractice.helper.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.conjugation_cell_view.view.*
import kotlinx.android.synthetic.main.fragment_crossword.view.*
import kotlinx.android.synthetic.main.fragment_practice_start.view.*
import kotlinx.android.synthetic.main.fragment_crossword_grid.view.*
import kotlinx.android.synthetic.main.fragment_wordsearch_row.view.*
import java.util.*

/**
 * Shows the main title screen with a button that navigates to About
 */
class CrosswordFragment : Fragment() {

    private lateinit var mContext: MainActivity
    private lateinit var hintsLinearLayout: LinearLayout
    private lateinit var canvas: ImageView
    private lateinit var continueButtons: Array<Button>
    private lateinit var buttonDivider: View
    private lateinit var wordAtIndexIsPlaced: IntArray
    private lateinit var separationViews: Array<View>
    private lateinit var hintViews: Array<View>
    private lateinit var crosswordCells: Array<Array<CrosswordCell>>
    private lateinit var wordCoordinates: Array<Pair<Pair<Int, Int>, Pair<Int, Int>>>
    private lateinit var constraintLayout: ConstraintLayout
    private var placedWordsCount = -1
    private var lines = mutableListOf<FloatArray>()
    private var crosswordSize = 12

    override fun onStop() {
        super.onStop()
        mContext.showNavBar()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mContext = context as MainActivity
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        " must implement ChangeIndicativeTensesDialogListener")
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_crossword, container, false)
        (activity as MainActivity?)?.supportActionBar?.setTitle(R.string.title_crossword)

        val crosswordParcel = (arguments?.get("crossword") as CrosswordParcel)

        val letters = crosswordParcel.crosswordLetters
        crosswordSize = when (mContext.getGridSizePreference()) {
            0 -> 8
            1 -> 10
            3 -> 14
            else -> 12
        }
        val weightParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            14.0F / crosswordSize
        )
        val allCrosswordRows = arrayOf(
            view.crossword.row0,
            view.crossword.row1,
            view.crossword.row2,
            view.crossword.row3,
            view.crossword.row4,
            view.crossword.row5,
            view.crossword.row6,
            view.crossword.row7,
            view.crossword.row8,
            view.crossword.row9,
            view.crossword.row10,
            view.crossword.row11,
            view.crossword.row12,
            view.crossword.row13
        )
        Array(14 - crosswordSize) { i -> allCrosswordRows[crosswordSize + i] }.forEach { row ->
            row.visibility = View.GONE
        }

        val crosswordRows = Array(crosswordSize) {
            allCrosswordRows[it].layoutParams = weightParams
            allCrosswordRows[it]
        }

        crosswordCells = crosswordRows.mapIndexed { row, it ->
            val allCells = arrayOf(
                it.cell0,
                it.cell1,
                it.cell2,
                it.cell3,
                it.cell4,
                it.cell5,
                it.cell6,
                it.cell7,
                it.cell8,
                it.cell9,
                it.cell10,
                it.cell11,
                it.cell12,
                it.cell13
            )
//            it.row.weightSum = crosswordSize.toFloat()
            Array(14 - crosswordSize) { i -> allCells[crosswordSize + i] }.forEach { cell ->
                cell.visibility = View.GONE
            }
            Array(crosswordSize) { i ->
                val longArrayIndex = row * crosswordSize + i
                if (letters[longArrayIndex] == ' ') {
                    allCells[i].setBackgroundColor(ContextCompat.getColor(mContext, R.color.crossword))
                }
                allCells[i].text = letters[longArrayIndex].toString()
                allCells[i].layoutParams = weightParams
                allCells[i].setOnClickListener {
                    Log.v("coordinates", crosswordParcel.wordStartCoords.size.toString())
                    crosswordParcel.wordStartCoords.forEach {
                        Log.v("coordinates", "${if (it.third) "across" else "  down"}: (${it.first}, ${it.second})")
                    }
                    val acrossHintIndex = crosswordParcel.wordStartCoords.indexOf(Triple(row, crosswordParcel.acrossRoots[longArrayIndex], true))
                    val downHintIndex = crosswordParcel.wordStartCoords.indexOf(Triple(crosswordParcel.downRoots[longArrayIndex], i, false))
                    var bigString = "clicked ($row, ${crosswordParcel.acrossRoots[longArrayIndex]}): $acrossHintIndex, (${crosswordParcel.downRoots[longArrayIndex]}, $i): $downHintIndex\n"
                    if (acrossHintIndex > -1) {
                        bigString += crosswordParcel.crosswordEnTranslations[acrossHintIndex] + "\n"
                    }
                    if (downHintIndex > -1) {
                        bigString += crosswordParcel.crosswordEnTranslations[downHintIndex]
                    }
                    view.hintView.text = bigString
                }
                allCells[i]
            }.mapIndexed { index, textView -> CrosswordCell(textView, row, index) }.toTypedArray()
        }.toTypedArray()

        mContext.hideNavBar()
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

}