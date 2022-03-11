package com.sid.app.verbpractice.ui.practice.crossword

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.sid.app.verbpractice.MainActivity
import com.sid.app.verbpractice.R
import com.sid.app.verbpractice.databinding.FragmentCrosswordBinding
import com.sid.app.verbpractice.databinding.FragmentCrosswordGridBinding
import com.sid.app.verbpractice.databinding.FragmentWordsearchRowBinding
import com.sid.app.verbpractice.databinding.KeyboardBinding
import com.sid.app.verbpractice.databinding.PopupLettersABinding
import com.sid.app.verbpractice.databinding.PopupLettersCBinding
import com.sid.app.verbpractice.databinding.PopupLettersEBinding
import com.sid.app.verbpractice.databinding.PopupLettersIBinding
import com.sid.app.verbpractice.databinding.PopupLettersOBinding
import com.sid.app.verbpractice.databinding.PopupLettersUBinding
import com.sid.app.verbpractice.databinding.WordsearchHintBinding
import com.sid.app.verbpractice.helper.*

/**
 * Shows the main title screen with a button that navigates to About
 */
class CrosswordFragment : Fragment() {

    private var _binding: FragmentCrosswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var keyboardBinding: KeyboardBinding
    private lateinit var fragmentCrosswordGridBinding: FragmentCrosswordGridBinding
    private lateinit var fragmentWordsearchRowBinding: FragmentWordsearchRowBinding
//    private lateinit var popupLettersABinding: PopupLettersABinding
//    private lateinit var popupLettersCBinding: PopupLettersCBinding
//    private lateinit var popupLettersEBinding: PopupLettersEBinding
//    private lateinit var popupLettersIBinding: PopupLettersIBinding
//    private lateinit var popupLettersOBinding: PopupLettersOBinding
//    private lateinit var popupLettersUBinding: PopupLettersUBinding
//    private lateinit var wordsearchHintBinding: WordsearchHintBinding
    private lateinit var crosswordParcel: CrosswordParcel
    private lateinit var scrollView: ScrollView
    private lateinit var hintViews: Array<View>
    private lateinit var mContext: MainActivity
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
        keyboardBinding.root.visibility = View.GONE
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
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentCrosswordBinding.inflate(inflater, container, false)
        val view = binding.root
        keyboardBinding = KeyboardBinding.bind(binding.root)
        fragmentCrosswordGridBinding = FragmentCrosswordGridBinding.bind(binding.root)
        fragmentWordsearchRowBinding = FragmentWordsearchRowBinding.bind(binding.root)

        (activity as MainActivity?)?.supportActionBar?.setTitle(R.string.title_crossword)

        endButtons = arrayOf(binding.returnHome, binding.playAgain)

        binding.returnHome.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_crossword_to_practice_start)
        }

        binding.playAgain.setOnClickListener {
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
        val allCrosswordRows = arrayOf(
            fragmentCrosswordGridBinding.row0,
            fragmentCrosswordGridBinding.row1,
            fragmentCrosswordGridBinding.row2,
            fragmentCrosswordGridBinding.row3,
            fragmentCrosswordGridBinding.row4,
            fragmentCrosswordGridBinding.row5,
            fragmentCrosswordGridBinding.row6,
            fragmentCrosswordGridBinding.row7,
            fragmentCrosswordGridBinding.row8,
            fragmentCrosswordGridBinding.row9,
            fragmentCrosswordGridBinding.row10,
            fragmentCrosswordGridBinding.row11,
            fragmentCrosswordGridBinding.row12,
            fragmentCrosswordGridBinding.row13
        )
        Array(14 - crosswordSize) { i -> allCrosswordRows[crosswordSize + i] }.forEach { row ->
            row.root.visibility = View.GONE
        }

        val crosswordRows = Array(crosswordSize) {
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
                allCells[i].setOnClickListener {
                    selectWord(crosswordCells[row][i])
                }
                allCells[i]
            }.mapIndexed { index, textView -> CrosswordCell(textView, row, index) }.toTypedArray()
        }.toTypedArray()


        val specialKeys = arrayOf(
            keyboardBinding.keyA,
            keyboardBinding.keyC,
            keyboardBinding.keyE,
            keyboardBinding.keyI,
            keyboardBinding.keyO,
            keyboardBinding.keyU
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
            arrayOf<Button>(
                popupViews[0].findViewById(R.id.acuteA),
                popupViews[0].findViewById(R.id.circumflexA),
                popupViews[0].findViewById(R.id.graveA),
                popupViews[0].findViewById(R.id.tildeA)
            ),
            arrayOf(popupViews[1].findViewById(R.id.cadillaC)),
            arrayOf(
                popupViews[2].findViewById(R.id.acuteE),
                popupViews[2].findViewById(R.id.circumflexE),
                popupViews[2].findViewById(R.id.graveE)
            ),
            arrayOf(
                popupViews[3].findViewById(R.id.acuteI),
                popupViews[3].findViewById(R.id.graveI)
            ),
            arrayOf(
                popupViews[4].findViewById(R.id.acuteO),
                popupViews[4].findViewById(R.id.circumflexO),
                popupViews[4].findViewById(R.id.graveO),
                popupViews[4].findViewById(R.id.tildeO)
            ),
            arrayOf(
                popupViews[5].findViewById(R.id.acuteU),
                popupViews[5].findViewById(R.id.graveU)
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
            keyboardBinding.keyA,
            keyboardBinding.keyB,
            keyboardBinding.keyC,
            keyboardBinding.keyD,
            keyboardBinding.keyE,
            keyboardBinding.keyF,
            keyboardBinding.keyG,
            keyboardBinding.keyH,
            keyboardBinding.keyI,
            keyboardBinding.keyJ,
            keyboardBinding.keyK,
            keyboardBinding.keyL,
            keyboardBinding.keyM,
            keyboardBinding.keyN,
            keyboardBinding.keyO,
            keyboardBinding.keyP,
            keyboardBinding.keyQ,
            keyboardBinding.keyR,
            keyboardBinding.keyS,
            keyboardBinding.keyT,
            keyboardBinding.keyU,
            keyboardBinding.keyV,
            keyboardBinding.keyW,
            keyboardBinding.keyX,
            keyboardBinding.keyY,
            keyboardBinding.keyZ
        )
        keys.forEach { key ->
            key.setOnClickListener {
                focusedCell?.view?.text = (it as Button).text
                moveToNextCell(crosswordParcel)
                checkCrossword()
            }
        }
        keyboardBinding.backspace.setOnClickListener {
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

        scrollView = binding.hintScrollView

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
            val hintViewPortVerb = hintView.findViewById<TextView>(R.id.portuguese_verb)
            val hintViewAnswerText = hintView.findViewById<TextView>(R.id.answer_text)
            hintView.findViewById<TextView>(R.id.english_verb_info).text = hint
            hintViewPortVerb.text = crosswordParcel.crosswordPtInfinitives[index]
            hintView.findViewById<TextView>(R.id.english_hint).text = formattedEnglishConjugation
            hintViewAnswerText.text = crosswordParcel.crosswordWords[index]

            hintView.findViewById<Button>(R.id.answer_button).setOnClickListener {
                it.visibility = View.GONE
                hintViewAnswerText.visibility = View.VISIBLE
            }
            hintView.findViewById<ImageButton>(R.id.show_english_verb_info).setOnClickListener {
                hintView.findViewById<LinearLayout>(R.id.english_info).visibility = View.VISIBLE
                it.visibility = View.GONE
                hintView.findViewById<ImageButton>(R.id.hide_english_verb_info).visibility = View.VISIBLE
            }
            hintView.findViewById<ImageButton>(R.id.hide_english_verb_info).setOnClickListener {
                hintViewPortVerb.visibility = View.GONE
                it.visibility = View.GONE
                hintView.findViewById<ImageButton>(R.id.show_english_verb_info).visibility = View.VISIBLE
                hintView.findViewById<LinearLayout>(R.id.english_info).visibility = View.GONE
                hintView.findViewById<ImageButton>(R.id.hide_portuguese_verb_info).visibility = View.GONE
                hintView.findViewById<ImageButton>(R.id.show_portuguese_verb_info).visibility = View.VISIBLE
            }
            hintView.findViewById<ImageButton>(R.id.show_portuguese_verb_info).setOnClickListener {
                hintViewPortVerb.visibility = View.VISIBLE
                it.visibility = View.GONE
                hintView.findViewById<ImageButton>(R.id.hide_portuguese_verb_info).visibility = View.VISIBLE
            }
            hintView.findViewById<ImageButton>(R.id.hide_portuguese_verb_info).setOnClickListener {
                hintViewPortVerb.visibility = View.GONE
                it.visibility = View.GONE
                hintView.findViewById<ImageButton>(R.id.show_portuguese_verb_info).visibility = View.VISIBLE

            }
            hintView
        }.toTypedArray()

        binding.switchOrientation.setOnClickListener {
            selectWord(focusedCell!!)
        }

        binding.nextButton.setOnClickListener {
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
                binding.switchOrientation.isClickable = true
                return
            }
        } else {
            if (crosswordParcel.acrossRoots[(selectedCell.row) * crosswordSize + selectedCell.col] != -1) {
                binding.switchOrientation.isClickable = true
                return
            }
        }
        binding.switchOrientation.isClickable = false
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}