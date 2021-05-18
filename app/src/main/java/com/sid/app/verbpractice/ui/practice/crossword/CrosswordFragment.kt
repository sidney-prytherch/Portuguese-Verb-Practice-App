package com.sid.app.verbpractice.ui.practice.crossword

import android.content.Context
import android.graphics.BlendModeColorFilter
import android.graphics.ColorFilter
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.sid.app.verbpractice.MainActivity
import com.sid.app.verbpractice.R
import com.sid.app.verbpractice.helper.*
import kotlinx.android.synthetic.main.fragment_crossword.*
import kotlinx.android.synthetic.main.fragment_crossword.view.*
import kotlinx.android.synthetic.main.fragment_crossword_grid.view.*
import kotlinx.android.synthetic.main.fragment_wordsearch.view.*
import kotlinx.android.synthetic.main.fragment_wordsearch_row.view.*
import kotlinx.android.synthetic.main.keyboard.view.*
import kotlinx.android.synthetic.main.popup_letters_a.view.*
import kotlinx.android.synthetic.main.popup_letters_c.view.*
import kotlinx.android.synthetic.main.popup_letters_e.view.*
import kotlinx.android.synthetic.main.popup_letters_i.view.*
import kotlinx.android.synthetic.main.popup_letters_o.view.*
import kotlinx.android.synthetic.main.popup_letters_u.view.*
import kotlinx.android.synthetic.main.wordsearch_hint.view.*

/**
 * Shows the main title screen with a button that navigates to About
 */
class CrosswordFragment : Fragment() {


    private lateinit var crosswordParcel: CrosswordParcel
    private lateinit var scrollView: ScrollView
    private lateinit var hintViews: Array<View>
    private lateinit var mContext: MainActivity
    private lateinit var switchOrientationButton: ImageButton
    private lateinit var crosswordCells: Array<Array<CrosswordCell>>
    private lateinit var endButtons: Array<Button>
    private var focusedCell: CrosswordCell? = null
    private var currentHintIndex = -1
    private var crosswordSize = 12
    private var isAcross = true

    override fun onStop() {
        super.onStop()
        mContext.showNavBar()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharArray(
            "placedLetters",
            crosswordCells.flatten().map { it.view.text[0] }.toCharArray()
        )
        outState.putInt("selectedRow", focusedCell?.row ?: 0)
        outState.putInt("selectedCol", focusedCell?.col ?: 0)
        outState.putInt("currentHintIndex", currentHintIndex)
        outState.putBoolean("isAcross", isAcross)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            val placedLetters = savedInstanceState.getCharArray("placedLetters")
            if (placedLetters != null) {
                crosswordCells.forEachIndexed { rowIndex, row ->
                    row.forEachIndexed { colIndex, crosswordCell ->
                        crosswordCell.view.text =
                            placedLetters[rowIndex * crosswordSize + colIndex].toString()
                    }
                }
            }
            checkCrossword()
            currentHintIndex = savedInstanceState.getInt("currentHintIndex")
            selectWord(
                crosswordCells[savedInstanceState.getInt("selectedRow")][savedInstanceState.getInt(
                    "selectedCol"
                )], isAcross != savedInstanceState.getBoolean("isAcross")
            )
        }
    }

    private fun checkCrossword() {
        crosswordCells.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, crosswordCell ->
                if (crosswordCell.view.text[0] != crosswordParcel.crosswordLetters[rowIndex * crosswordSize + colIndex]) {
//                    Log.v("inequal1", crosswordCell.view.text[0].toString().replace("", "!").replace(" ", "_"))
//                    Log.v("inequal2", crosswordParcel.crosswordLetters[rowIndex * crosswordSize + colIndex].toString().replace("", "!").replace(" ", "_"))
//                    Log.v("inequal3", "")
                    return
                }
            }
        }
        for (button in endButtons) {
            button.visibility = View.VISIBLE
        }
        keyboard.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        mContext.hideNavBar()
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

    private fun getOrderedHints(): IntArray {
        return crosswordParcel.wordStartCoords.mapIndexed { index, triple -> triple to index }
            .sortedWith(compareBy({ it.first.third }, { it.first.first }, { it.first.second }))
            .map { it.second }.toIntArray()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_crossword, container, false)

        (activity as MainActivity?)?.supportActionBar?.setTitle(R.string.title_crossword)

        endButtons = arrayOf(view.returnHome, view.playAgain)

        view.returnHome.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_crossword_to_practice_start)
        }

        view.playAgain.setOnClickListener {
            val bundle = bundleOf("practiceMode" to 2)
            Navigation.findNavController(view)
                .navigate(R.id.action_crossword_to_practice_loading, bundle)
        }

        crosswordParcel = (arguments?.get("crossword") as CrosswordParcel)
        val orderedHintIndices = getOrderedHints()

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
            view.crossword.key0,
            view.crossword.key1,
            view.crossword.key2,
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
                    allCells[i].setBackgroundColor(
                        ContextCompat.getColor(
                            mContext,
                            R.color.crossword
                        )
                    )
                }
                allCells[i].text = " "
                //allCells[i].text = letters[longArrayIndex].toString()
                allCells[i].layoutParams = weightParams
                allCells[i].setOnClickListener {
                    selectWord(crosswordCells[row][i])
                }
                allCells[i]
            }.mapIndexed { index, textView -> CrosswordCell(textView, row, index) }.toTypedArray()
        }.toTypedArray()


        val specialKeys = arrayOf(
            view.keyboard.keyA,
            view.keyboard.keyC,
            view.keyboard.keyE,
            view.keyboard.keyI,
            view.keyboard.keyO,
            view.keyboard.keyU
        )
        val popUpResources = arrayOf(
            R.layout.popup_letters_a,
            R.layout.popup_letters_c,
            R.layout.popup_letters_e,
            R.layout.popup_letters_i,
            R.layout.popup_letters_o,
            R.layout.popup_letters_u
        )
        val popupViews = popUpResources.map {
            inflater.inflate(it, container, false)
        }
        val accentedLetters = arrayOf(
            arrayOf(
                popupViews[0].acuteA,
                popupViews[0].circumflexA,
                popupViews[0].graveA,
                popupViews[0].tildeA
            ),
            arrayOf(popupViews[1].cadillaC),
            arrayOf(
                popupViews[2].acuteE,
                popupViews[2].circumflexE,
                popupViews[2].graveE
            ),
            arrayOf(
                popupViews[3].acuteI,
                popupViews[3].graveI
            ),
            arrayOf(
                popupViews[4].acuteO,
                popupViews[4].circumflexO,
                popupViews[4].graveO,
                popupViews[4].tildeO
            ),
            arrayOf(
                popupViews[5].acuteU,
                popupViews[5].graveU
            )
        )
        val popupWindows = popupViews.map {
            createPopupWindow(it)
        }

        specialKeys.forEachIndexed { index, key ->
            for (accentButton in accentedLetters[index]) {
                accentButton.setOnClickListener {
                    focusedCell?.view?.text = accentButton.text
                    popupWindows[index].dismiss()
                    moveToNextCell(crosswordParcel)
                    checkCrossword()
                }
            }
            key.setOnLongClickListener {
                popupWindows[index].height = it.height
                popupWindows[index].width = it.width * accentedLetters[index].size
                val buttonDrawable = it.background
                it.setBackgroundResource(R.drawable.held_button)
                popupWindows[index].showAsDropDown(
                    it,
                    (it.width * (accentedLetters[index].size - 1)) / -2,
                    -2 * it.height,
                    Gravity.CENTER
                )
                popupWindows[index].setOnDismissListener {
                    it.background = buttonDrawable
                }
                true
            }
        }

        val keys = arrayOf(
            view.keyboard.keyA,
            view.keyboard.keyB,
            view.keyboard.keyC,
            view.keyboard.keyD,
            view.keyboard.keyE,
            view.keyboard.keyF,
            view.keyboard.keyG,
            view.keyboard.keyH,
            view.keyboard.keyI,
            view.keyboard.keyJ,
            view.keyboard.keyK,
            view.keyboard.keyL,
            view.keyboard.keyM,
            view.keyboard.keyN,
            view.keyboard.keyO,
            view.keyboard.keyP,
            view.keyboard.keyQ,
            view.keyboard.keyR,
            view.keyboard.keyS,
            view.keyboard.keyT,
            view.keyboard.keyU,
            view.keyboard.keyV,
            view.keyboard.keyW,
            view.keyboard.keyX,
            view.keyboard.keyY,
            view.keyboard.keyZ
        )
        keys.forEach { key ->
            key.setOnClickListener {
                focusedCell?.view?.text = (it as Button).text
                moveToNextCell(crosswordParcel)
                checkCrossword()
            }
        }
        view.keyboard.backspace.setOnClickListener {
            val selectedCell = focusedCell
            if (selectedCell !== null) {
                if (selectedCell.view.text != " ") {
                    selectedCell.view.text = " "
                } else {
                    val nextCell = shiftToNewCell(selectedCell, -1)
                    if (nextCell != null) {
                        nextCell.view.text = " "
                    }
                }
            }
        }

        switchOrientationButton = view.switchOrientation

        scrollView = view.hintScrollView

        hintViews = crosswordParcel.crosswordHints.mapIndexed { index, hint ->
            var finalHint = crosswordParcel.crosswordEnTranslations[index]
            val hintView = View.inflate(context, R.layout.wordsearch_hint, null) as View
            val formattedEnglishConjugation =
                SpannableString(
                    crosswordParcel.crosswordEnTranslations[index].replace("<", "").replace(">", "")
                )
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
            hintView.english_verb_info.text = hint
            hintView.portuguese_verb.text = crosswordParcel.crosswordPtInfinitives[index]
            hintView.english_hint.text = formattedEnglishConjugation
            hintView.answer_text.text = crosswordParcel.crosswordWords[index]

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
            hintView
        }.toTypedArray()

        switchOrientationButton.setOnClickListener {
            selectWord(focusedCell!!)
        }

        view.nextButton.setOnClickListener {
            val nextIndex =
                orderedHintIndices[(orderedHintIndices.indexOf(currentHintIndex) + 1) % orderedHintIndices.size]
            val forceSwitch = isAcross != crosswordParcel.wordStartCoords[nextIndex].third
            selectWord(
                crosswordCells[crosswordParcel.wordStartCoords[nextIndex].first][crosswordParcel.wordStartCoords[nextIndex].second],
                forceSwitch
            )
        }

        mContext.hideNavBar()
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return view
    }

    private fun shiftToNewCell(selectedCell: CrosswordCell, shift: Int): CrosswordCell? {
        val nextCell = if (isAcross) {
            if (((selectedCell.col + shift) !in 0 until crosswordSize) ||
                crosswordParcel.acrossRoots[selectedCell.row * crosswordSize + selectedCell.col + shift] == -1
            ) {
                return null
            }
            crosswordCells[selectedCell.row][selectedCell.col + shift]

        } else {
            if (((selectedCell.row + shift) !in 0 until crosswordSize) ||
                crosswordParcel.downRoots[(selectedCell.row + shift) * crosswordSize + selectedCell.col] == -1
            ) {
                return null
            }
            crosswordCells[selectedCell.row + shift][selectedCell.col]
        }
        selectedCell.view.setBackgroundResource(R.drawable.crossword_cell_highlighted)
        nextCell.view.setBackgroundResource(R.drawable.crossword_cell_selected)
        focusedCell = nextCell
        onCellSelected(nextCell, crosswordParcel)
        return nextCell
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectWord(crosswordCells[0][0])
    }

    private fun onCellSelected(selectedCell: CrosswordCell, crosswordParcel: CrosswordParcel) {
        if (isAcross) {
            if (crosswordParcel.downRoots[(selectedCell.row) * crosswordSize + selectedCell.col] != -1) {
                switchOrientationButton.isClickable = true
                return
            }
        } else {
            if (crosswordParcel.acrossRoots[(selectedCell.row) * crosswordSize + selectedCell.col] != -1) {
                switchOrientationButton.isClickable = true
                return
            }
        }
        switchOrientationButton.isClickable = false
    }

    private fun moveToNextCell(crosswordParcel: CrosswordParcel) {
        val selectedCell = focusedCell ?: return
        val nextCell = shiftToNewCell(selectedCell, 1)
        if (nextCell != null && nextCell.view.text != " ") {
            moveToNextCell(crosswordParcel)
        }
    }

    private fun selectWord(cell: CrosswordCell, forceSwitch: Boolean = false) {
        val longArrayIndex = cell.row * crosswordSize + cell.col
        val acrossHintIndex = crosswordParcel.wordStartCoords.indexOf(
            Triple(
                cell.row,
                crosswordParcel.acrossRoots[longArrayIndex],
                true
            )
        )
        val downHintIndex = crosswordParcel.wordStartCoords.indexOf(
            Triple(
                crosswordParcel.downRoots[longArrayIndex],
                cell.col,
                false
            )
        )
        if (acrossHintIndex > -1 || downHintIndex > -1) {
            if (focusedCell == crosswordCells[cell.row][cell.col]) {
                if ((isAcross && downHintIndex == -1) || (!isAcross && acrossHintIndex == -1)) {
                    return
                }
            }
            val oldCell = focusedCell
            if (oldCell != null) {
                if (isAcross) {
                    setColorToRow(
                        oldCell.row,
                        crosswordParcel.acrossRoots[oldCell.row * crosswordSize + oldCell.col],
                        crosswordParcel,
                        R.drawable.crossword_cell
                    )
                } else {
                    setColorToCol(
                        crosswordParcel.downRoots[oldCell.row * crosswordSize + oldCell.col],
                        oldCell.col,
                        crosswordParcel,
                        R.drawable.crossword_cell
                    )
                }
            }
            focusedCell = crosswordCells[cell.row][cell.col]
            onCellSelected(crosswordCells[cell.row][cell.col], crosswordParcel)
            if (oldCell == focusedCell || forceSwitch) {
                isAcross = !isAcross
            }
        }

        if (acrossHintIndex > -1 && (isAcross || downHintIndex == -1)) {
            currentHintIndex = acrossHintIndex
            isAcross = true
            setColorToRow(
                cell.row,
                crosswordParcel.acrossRoots[longArrayIndex],
                crosswordParcel,
                R.drawable.crossword_cell_highlighted
            )
            cell.view.setBackgroundResource(R.drawable.crossword_cell_selected)
            scrollView.removeAllViews()
            scrollView.addView(hintViews[acrossHintIndex])
        } else if (downHintIndex > -1) {
            currentHintIndex = downHintIndex
            isAcross = false
            setColorToCol(
                crosswordParcel.downRoots[longArrayIndex],
                cell.col,
                crosswordParcel,
                R.drawable.crossword_cell_highlighted
            )
            cell.view.setBackgroundResource(R.drawable.crossword_cell_selected)
            scrollView.removeAllViews()
            scrollView.addView(hintViews[downHintIndex])
        }
    }

    private fun setColorToRow(
        row: Int,
        startCol: Int,
        crosswordParcel: CrosswordParcel,
        backgroundID: Int
    ) {
        var tempCol = startCol
        while (tempCol < crosswordSize && startCol == crosswordParcel.acrossRoots[row * crosswordSize + tempCol]) {
            crosswordCells[row][tempCol++].view.setBackgroundResource(backgroundID)
        }
    }

    private fun setColorToCol(
        startRow: Int,
        col: Int,
        crosswordParcel: CrosswordParcel,
        backgroundID: Int
    ) {
        var tempRow = startRow
        while (tempRow < crosswordSize && startRow == crosswordParcel.downRoots[tempRow * crosswordSize + col]) {
            crosswordCells[tempRow++][col].view.setBackgroundResource(backgroundID)
        }
    }

    private fun createPopupWindow(popup: View): PopupWindow {
        val popupWindow = PopupWindow(
            popup,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        return popupWindow
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

}