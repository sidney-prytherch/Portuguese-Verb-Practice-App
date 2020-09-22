package com.sid.app.verbpractice.ui.practice.wordsearch

import android.content.Context
import android.graphics.*
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sid.app.verbpractice.MainActivity
import com.sid.app.verbpractice.R
import com.sid.app.verbpractice.helper.*
import com.sid.app.verbpractice.ui.practice.PracticeFragment
import kotlinx.android.synthetic.main.conjugation_cell_view.view.*
import kotlinx.android.synthetic.main.fragment_wordsearch.view.*
import kotlinx.android.synthetic.main.fragment_wordsearch_grid.view.*
import kotlinx.android.synthetic.main.fragment_wordsearch_row.view.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Shows the main title screen with a button that navigates to About
 */
class WordsearchFragment : Fragment() {

    private lateinit var mContext: MainActivity
    private lateinit var wordsearchCells:Array<Array<WordsearchCell>>
    private lateinit var constraintLayout:ConstraintLayout
    private var lines = mutableListOf<FloatArray>()
    private var wordsearchSize = 12

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
        var previouslyClickedCell: WordsearchCell? = null
        val view = inflater.inflate(R.layout.fragment_wordsearch, container, false)

        val wordsearchCellData = (arguments?.get("wordsearch") as WordsearchParcel).wordsearchCells



        constraintLayout = view.container

        wordsearchSize = when (mContext.getGridSizePreference()) {
            0 -> 8
            1 -> 10
            3 -> 14
            else -> 12
        }

        val weightParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            14.0F/wordsearchSize
        )

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
        Array(14 - wordsearchSize) {i -> allWordsearchRows[wordsearchSize + i]}.forEach { row ->
            row.visibility = View.GONE
        }

        val wordsearchRows = Array(wordsearchSize) {
            allWordsearchRows[it].layoutParams = weightParams
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
            Array(14 - wordsearchSize) {i -> allCells[wordsearchSize + i]}.forEach { cell ->
                cell.visibility = View.GONE
            }
            Array(wordsearchSize) {i ->
                allCells[i].layoutParams = weightParams
                allCells[i]
            }.mapIndexed { index, textView -> WordsearchCell(textView, row, index) }.toTypedArray()
        }.toTypedArray()

        val transparent = ContextCompat.getColor(view.context, R.color.transparency)
        val green = ContextCompat.getColor(view.context, R.color.green)

        //val wordsearch = Wordsearch(14, arrayOf(""))

        wordsearchCells.forEachIndexed { y, row ->
            row.forEachIndexed { x, cell ->
                //cell.node = wordsearch.ws[y][x]
                cell.view.setOnClickListener {clickedCellView ->
                    val setToNull = previouslyClickedCell != null
                    val cell2 = previouslyClickedCell
                    if (cell2 != null && cellsFormLine(cell, cell2)) {// && cell.node.isRelatedTo(cell2.node)) {
                        val xDiff = cell2.x - cell.x
                        val yDiff = cell2.y - cell.y
                        val length = sqrt(xDiff.toFloat().pow(2) + yDiff.toFloat().pow(2)) + 1

                        val x1 = (cell.x + .15F) / wordsearchSize
                        val y1 = (cell.y + .15F) / wordsearchSize
                        val x2 = (cell.x + length - .15F) / wordsearchSize
                        val y2 = (cell.y + 1 - .15F) / wordsearchSize

                        lines.add(floatArrayOf(x1, y1,x2, y2, arcTan(xDiff, yDiff)))
                        repaint(view.canvas)
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


        wordsearchCells.forEachIndexed { i, row ->
            row.forEachIndexed { j, cell ->
                cell.view.text = wordsearchCellData[i][j].letter.toString()
            }
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return view
    }

    private fun avg(a: Float, b: Float) : Float {
        return (a + b) / 2
    }

    private fun repaint(canvasView: ImageView) {
        val bitmap = Bitmap.createBitmap(canvasView.width, canvasView.height, Bitmap.Config.ARGB_8888)
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
            val rect = RectF(canvasView.width * x1, canvasView.height * y1, canvasView.width * x2, canvasView.height * y2)
            Log.v("testingRadius", (canvasView.height * y2 - canvasView.height * y1).toString())
            borderPaint.color = ContextCompat.getColor(canvasView.context, when (rot) {
                0F -> R.color.purpleb
                45F -> R.color.redpurpleb
                90F -> R.color.redb
                135F -> R.color.orangeb
                180F -> R.color.yellowb
                225F -> R.color.greenb
                270F -> R.color.bluegreenb
                else -> R.color.blueb
            })

            fillPaint.color = ContextCompat.getColor(canvasView.context, when (rot) {
                0F -> R.color.purplet
                45F -> R.color.redpurplet
                90F -> R.color.redt
                135F -> R.color.oranget
                180F -> R.color.yellowt
                225F -> R.color.greent
                270F -> R.color.bluegreent
                else -> R.color.bluet
            })
            val radius = (canvasView.height * y2 - canvasView.height * y1) / 1.98F
            borderPath.addRoundRect(rect, radius, radius, Path.Direction.CW)
            borderPath.fillType = Path.FillType.EVEN_ODD
            canvas.save()
            canvas.rotate(rot, canvasView.width * (x1 + .35F/wordsearchSize), canvasView.height * (y1 + .35F/wordsearchSize))
            canvas.drawPath(borderPath, borderPaint)
            canvas.drawRoundRect(rect, 32F, 32F, fillPaint)
            canvas.restore()
        }
        canvasView.setImageBitmap(bitmap)
    }

    private fun cellOrder(from: WordsearchCell, to: WordsearchCell) : IntArray {
        return intArrayOf(
            if (from.x < to.x) 1 else if (from.x > to.x) -1 else 0,
            if (from.y < to.y) 1 else if (from.y > to.y) -1 else 0
        )
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