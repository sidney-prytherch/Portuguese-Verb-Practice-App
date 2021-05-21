package com.sid.app.verbpractice.ui.practice.wordsearch

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
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
import androidx.core.os.bundleOf
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.sid.app.verbpractice.MainActivity
import com.sid.app.verbpractice.R
import com.sid.app.verbpractice.helper.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.conjugation_cell_view.view.*
import kotlinx.android.synthetic.main.fragment_practice_start.view.*
import kotlinx.android.synthetic.main.fragment_wordsearch.*
import kotlinx.android.synthetic.main.fragment_wordsearch.view.*
import kotlinx.android.synthetic.main.wordsearch_hint.view.*
import kotlinx.android.synthetic.main.fragment_wordsearch_grid.view.*
import kotlinx.android.synthetic.main.fragment_wordsearch_row.view.*
import kotlinx.android.synthetic.main.wordsearch_hint.*
import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Shows the main title screen with a button that navigates to About
 */
class WordsearchFragment : Fragment() {

    private lateinit var mContext: MainActivity
    private lateinit var hintsLinearLayout: LinearLayout
    private lateinit var canvas: ImageView
    private lateinit var continueButtons: Array<Button>
    private lateinit var buttonDivider: View
    private lateinit var wordAtIndexIsPlaced: IntArray
    private lateinit var separationViews: Array<View>
    private lateinit var hintViews: Array<View>
    private lateinit var wordsearchCells: Array<Array<WordsearchCell>>
    private lateinit var wordCoordinates: Array<Pair<Pair<Int, Int>, Pair<Int, Int>>>
    private lateinit var constraintLayout: ConstraintLayout
    private var placedWordsCount = -1
    private var lines = mutableListOf<FloatArray>()
    private var wordsearchSize = 12

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray("wordAtIndexIsPlaced", wordAtIndexIsPlaced)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        wordAtIndexIsPlaced =
            savedInstanceState?.getIntArray("wordAtIndexIsPlaced") ?: wordAtIndexIsPlaced
        orderLoop@ for (i in wordAtIndexIsPlaced.indices) {
            for (j in wordAtIndexIsPlaced.indices) {
                if (wordAtIndexIsPlaced[j] == i) {
                    placeWord(j)
                    lines.add(
                        getLine(
                            wordCoordinates[j].first.second,
                            wordCoordinates[j].first.first,
                            wordCoordinates[j].second.second,
                            wordCoordinates[j].second.first
                        )
                    )
                    continue@orderLoop
                }
            }
            break
        }
        canvas.post {
            repaint(canvas)
        }
    }

    private fun getLine(a1: Int, b1: Int, a2: Int, b2: Int): FloatArray {
        val xDiff = a2 - a1
        val yDiff = b2 - b1
        val length = sqrt(xDiff.toFloat().pow(2) + yDiff.toFloat().pow(2)) + 1

        val x1 = (a1 + .15F) / wordsearchSize
        val y1 = (b1 + .15F) / wordsearchSize
        val x2 = (a1 + length - .15F) / wordsearchSize
        val y2 = (b1 + 1 - .15F) / wordsearchSize

        return floatArrayOf(x1, y1, x2, y2, arcTan(xDiff, yDiff))
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

    private fun placeWord(index: Int) {
        wordAtIndexIsPlaced[index] = ++placedWordsCount
        hintViews[index].answer_button.visibility = View.GONE
        hintViews[index].answer_text.visibility = View.VISIBLE
        hintViews[index].answer_text.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        hintViews[index].setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.word_found
            )
        )
        hintsLinearLayout.getChildAt(hintsLinearLayout.size - 1).visibility = View.VISIBLE
        hintsLinearLayout.removeView(hintViews[index])
        hintsLinearLayout.addView(hintViews[index])
        hintsLinearLayout.removeView(separationViews[index])
        separationViews[index].visibility = View.GONE
        hintsLinearLayout.addView(separationViews[index])
        if (!wordAtIndexIsPlaced.contains(-1)) {
            continueButtons.forEach { it.visibility = View.VISIBLE }
            buttonDivider.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        var previouslyClickedCell: WordsearchCell? = null
        val view = inflater.inflate(R.layout.fragment_wordsearch, container, false)

        (activity as MainActivity?)?.supportActionBar?.setTitle(R.string.title_wordsearch)

        hintsLinearLayout = view.wordsearch_hints

        val wordsearchParcel = (arguments?.get("wordsearch") as WordsearchParcel)
        val hints = wordsearchParcel.wordsearchHints
        val answers = wordsearchParcel.wordsearchWords
        val ptInfinitives = wordsearchParcel.wordsearchPtInfinitives
        val enTranslations = wordsearchParcel.wordsearchEnTranslations
        val coordinates = wordsearchParcel.wordsearchCoordinates.toList()
        val letters = wordsearchParcel.wordsearchLetters

        wordAtIndexIsPlaced = IntArray(hints.size) { -1 }
        val defaultView = View(context)
        hintViews = Array(hints.size) { defaultView }
        separationViews = Array(hints.size) { defaultView }
        continueButtons = arrayOf(view.play_again, view.return_home)
        buttonDivider = view.button_divider

        wordCoordinates = coordinates.chunked(4)
            .map { (startX, startY, endX, endY) -> (startX to startY) to (endX to endY) }
            .toTypedArray()

//        val hints = wordsearchCellData[0][0].hints.filterNotNull()
//        val words = wordsearchCellData[0][0].words.filterNotNull()
//        val ptInfinitives = wordsearchCellData[0][0].ptInfinitives.filterNotNull()
//        val enTranslations = wordsearchCellData[0][0].enTranslations.filterNotNull()

        //val inflater = requireActivity().layoutInflater
        for (index in hints.indices) {

            var finalHint = enTranslations[index]
            val hintView = View.inflate(context, R.layout.wordsearch_hint, null) as View
            hintViews[index] = hintView
            val formattedEnglishConjugation =
                SpannableString(enTranslations[index].replace("<", "").replace(">", ""))
            while (finalHint.indexOf("<") != -1) {
                val start = finalHint.indexOf("<")
                val end = finalHint.indexOf(">") - 1
                formattedEnglishConjugation.setSpan(
                    UnderlineSpan(),
                    start,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                finalHint = finalHint.replaceFirst("<", "").replaceFirst(">", "")
            }
            hintView.english_verb_info.text = hints[index]
            hintView.portuguese_verb.text = ptInfinitives[index]
            hintView.english_hint.text = formattedEnglishConjugation
            hintView.answer_text.text = answers[index]

            hintView.answer_button.setOnClickListener {
                it.visibility = View.GONE
                hintView.answer_text.visibility = View.VISIBLE
            }
            hintView.show_english_verb_info.setOnClickListener {
                hintView.english_info.visibility = View.VISIBLE
                it.visibility = View.GONE
                hintView.hide_english_verb_info.visibility = View.VISIBLE
            }
            hintView.hide_english_verb_info.setOnClickListener {
                hintView.portuguese_verb.visibility = View.GONE
                it.visibility = View.GONE
                hintView.show_english_verb_info.visibility = View.VISIBLE
                hintView.english_info.visibility = View.GONE
                hintView.hide_portuguese_verb_info.visibility = View.GONE
                hintView.show_portuguese_verb_info.visibility = View.VISIBLE
            }
            hintView.show_portuguese_verb_info.setOnClickListener {
                hintView.portuguese_verb.visibility = View.VISIBLE
                it.visibility = View.GONE
                hintView.hide_portuguese_verb_info.visibility = View.VISIBLE
            }
            hintView.hide_portuguese_verb_info.setOnClickListener {
                hintView.portuguese_verb.visibility = View.GONE
                it.visibility = View.GONE
                hintView.show_portuguese_verb_info.visibility = View.VISIBLE

            }
            view.wordsearch_hints.addView(hintView)

            val divider = View.inflate(context, R.layout.wordsearch_hint_divider_view, null)
            view.wordsearch_hints.addView(divider)
            separationViews[index] = divider
            if (index == hints.size - 1) {
                divider.visibility = View.GONE
            }
        }

        canvas = view.canvas

        constraintLayout = view.container

        wordsearchSize = when (mContext.getGridSizePreference()) {
            0 -> 8
            1 -> 10
            3 -> 14
            else -> 12
        }

        val allWordsearchRows = arrayOf(
            view.wordsearch.row0,
            view.wordsearch.row1,
            view.wordsearch.row2,
            view.wordsearch.row3,
            view.wordsearch.row4,
            view.wordsearch.row5,
            view.wordsearch.row6,
            view.wordsearch.row7,
            view.wordsearch.row8,
            view.wordsearch.row9,
            view.wordsearch.row10,
            view.wordsearch.row11,
            view.wordsearch.row12,
            view.wordsearch.row13
        )
        Array(14 - wordsearchSize) { i -> allWordsearchRows[wordsearchSize + i] }.forEach { row ->
            row.visibility = View.GONE
        }

        val wordsearchRows = Array(wordsearchSize) {
            allWordsearchRows[it]
        }

        wordsearchCells = wordsearchRows.mapIndexed { row, it ->
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
//            it.row.weightSum = wordsearchSize.toFloat()
            Array(14 - wordsearchSize) { i -> allCells[wordsearchSize + i] }.forEach { cell ->
                cell.visibility = View.GONE
            }
            Array(wordsearchSize) { i ->
                allCells[i]
            }.mapIndexed { index, textView -> WordsearchCell(textView, row, index) }.toTypedArray()
        }.toTypedArray()

        val transparent = ContextCompat.getColor(view.context, R.color.transparency)
        val green = ContextCompat.getColor(view.context, R.color.green)

        //val wordsearch = Wordsearch(14, arrayOf(""))

        view.return_home.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_wordsearch_to_practice_start)
        }

        view.play_again.setOnClickListener {
            val bundle = bundleOf("practiceMode" to 1)
            Navigation.findNavController(view)
                .navigate(R.id.action_wordsearch_to_practice_loading, bundle)
        }

        wordsearchCells.forEachIndexed { i, row ->
            row.forEachIndexed { j, cell ->
                cell.view.text =
                    letters[i * wordsearchSize + j].toString()// wordsearchCellData[i][j].letter.toString()
                //cell.node = wordsearch.ws[y][x]
                cell.view.setOnClickListener { clickedCellView ->
                    val setToNull = previouslyClickedCell != null
                    val cell2 = previouslyClickedCell
                    val indexOfCoordPair = indexOfCoordPair(cell, cell2)
                    val coordsAreReversed = indexOfCoordPair < 0
                    val modifiedCoordIndex = abs(indexOfCoordPair) - 1
                    if (cell2 != null && cellsFormLine(
                            cell,
                            cell2
                        ) && indexOfCoordPair != wordCoordinates.size + 1 && wordAtIndexIsPlaced[modifiedCoordIndex] == -1
                    ) {// && cell.node.isRelatedTo(cell2.node)) {
                        val firstCell = if (coordsAreReversed) cell2 else cell
                        val secondCell = if (coordsAreReversed) cell else cell2
                        placeWord(modifiedCoordIndex)
                        lines.add(getLine(firstCell.x, firstCell.y, secondCell.x, secondCell.y))
                        repaint(canvas)
                    }
                    previouslyClickedCell?.view?.setBackgroundColor(transparent)
                    previouslyClickedCell = if (setToNull) {
                        null
                    } else {
                        clickedCellView.setBackgroundColor(green)
                        cell
                    }
                }
            }
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return view
    }

    private fun avg(a: Float, b: Float): Float {
        return (a + b) / 2
    }

    private fun repaint(canvasView: ImageView) {
        val bitmap =
            Bitmap.createBitmap(canvasView.width, canvasView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val fillPaint = Paint()
        val borderPaint = Paint()
        borderPaint.strokeWidth = 6F
        borderPaint.isAntiAlias = true
        borderPaint.style = Paint.Style.STROKE
        fillPaint.style = Paint.Style.FILL
        fillPaint.isAntiAlias = true
        lines.forEach { (x1, y1, x2, y2, rot) ->
            val borderPath = Path()
            val rect = RectF(
                canvasView.width * x1,
                canvasView.height * y1,
                canvasView.width * x2,
                canvasView.height * y2
            )
            Log.v("testingRadius", (canvasView.height * y2 - canvasView.height * y1).toString())
            borderPaint.color = ContextCompat.getColor(
                canvasView.context, when (rot) {
                    0F -> R.color.purpleb
                    45F -> R.color.redpurpleb
                    90F -> R.color.redb
                    135F -> R.color.orangeb
                    180F -> R.color.yellowb
                    225F -> R.color.greenb
                    270F -> R.color.bluegreenb
                    else -> R.color.blueb
                }
            )

            fillPaint.color = ContextCompat.getColor(
                canvasView.context, when (rot) {
                    0F -> R.color.purplet
                    45F -> R.color.redpurplet
                    90F -> R.color.redt
                    135F -> R.color.oranget
                    180F -> R.color.yellowt
                    225F -> R.color.greent
                    270F -> R.color.bluegreent
                    else -> R.color.bluet
                }
            )
            val radius = (canvasView.height * y2 - canvasView.height * y1) / 1.98F
            borderPath.addRoundRect(rect, radius, radius, Path.Direction.CW)
            borderPath.fillType = Path.FillType.EVEN_ODD
            canvas.save()
            canvas.rotate(
                rot,
                canvasView.width * (x1 + .35F / wordsearchSize),
                canvasView.height * (y1 + .35F / wordsearchSize)
            )
            canvas.drawPath(borderPath, borderPaint)
            canvas.drawRoundRect(rect, 32F, 32F, fillPaint)
            canvas.restore()
        }
        canvasView.setImageBitmap(bitmap)
    }

    private fun cellOrder(from: WordsearchCell, to: WordsearchCell): IntArray {
        return intArrayOf(
            if (from.x < to.x) 1 else if (from.x > to.x) -1 else 0,
            if (from.y < to.y) 1 else if (from.y > to.y) -1 else 0
        )
    }

    private fun indexOfCoordPair(from: WordsearchCell?, to: WordsearchCell?): Int {
        if (from == null || to == null) {
            return wordCoordinates.size + 1
        }

        val pair1 = (from.y to from.x)
        val pair2 = (to.y to to.x)

        val oneToTwoIndex = wordCoordinates.indexOf(pair1 to pair2) + 1
        val twoToOneIndex = wordCoordinates.indexOf(pair2 to pair1) + 1

        return if (oneToTwoIndex > 0) oneToTwoIndex else if (twoToOneIndex > 0) -1 * twoToOneIndex else wordCoordinates.size + 1
    }

    private fun cellsFormLine(from: WordsearchCell?, to: WordsearchCell?): Boolean {
        if (from == null || to == null) {
            return false
        }
        val xDiff = abs(from.x - to.x)
        val yDiff = abs(from.y - to.y)
        // if (both are 0) OR (neither are 0 and they don't equal)
        if ((xDiff == 0 && yDiff == 0) || !(xDiff == 0 || yDiff == 0 || xDiff == yDiff)) {
            return false
        }
        return true
    }

    private fun arcTan(x: Int, y: Int): Float {
        return when {
            x < 0 -> when {
                y < 0 -> 225F
                y > 0 -> 135F
                else -> 180F
            }
            x > 0 -> when {
                y < 0 -> 315F
                y > 0 -> 45F
                else -> 0F
            }
            else -> when {
                y < 0 -> 270F
                y > 0 -> 90F
                else -> 0F // impossible
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

}