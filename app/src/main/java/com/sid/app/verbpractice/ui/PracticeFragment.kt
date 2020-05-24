package com.sid.app.verbpractice.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.sid.app.verbpractice.MainActivity
import com.sid.app.verbpractice.R
import com.sid.app.verbpractice.db.entity.PortugueseVerb
import com.sid.app.verbpractice.enums.Person
import com.sid.app.verbpractice.helper.*
import kotlinx.android.synthetic.main.conjugation_cell_view.view.*
import kotlinx.android.synthetic.main.fragment_practice.view.*
import java.util.*
import kotlin.math.max


class PracticeFragment : Fragment() {

    private lateinit var countDownTimer:CountDownTimer
    lateinit var barTimer: ProgressBar
    lateinit var textTimer: TextView
    private lateinit var textCount: TextView
    private lateinit var tenseTextView: TextView
    private lateinit var verbTextView: TextView
    private lateinit var englishVerbTextView: TextView
    private var conjugationViews: Array<LinearLayout> = arrayOf()
    private var conjugationViewsPersons: Array<Int> = arrayOf()
    private lateinit var conjugationTable: LinearLayout
    private lateinit var finishButton: AppCompatButton
    private lateinit var nextButton: AppCompatButton

    private lateinit var mContext: MainActivity

    private lateinit var conjugationsArrayParcel: ConjugationArrayParcel
    private lateinit var conjugations: Array<Conjugation>
    private var results: List<ResultParcel> = mutableListOf()
    private var currentConjugation = -1
    private var currentPage = 0
    private var secs = 0

    private var totalCount = 0
    private var soFarCount = 0

    private var gray = 0
    private val allPersons = arrayOf(Person.FIRST_SING, Person.SECOND_SING, Person.THIRD_SING, Person.FIRST_PLUR, Person.SECOND_PLUR, Person.THIRD_PLUR)
    private lateinit var enabledPersons: Array<Person>
    private lateinit var enabledThirdPersons: BooleanArray
    private lateinit var rowsEnabled: BooleanArray
    private var isCountSettings = true
    private var isFullConjugation = true
    private var rowCount = -1

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

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_practice, container, false)

        conjugationsArrayParcel = (arguments?.get("conjugations") as ConjugationArrayParcel)
        conjugations = conjugationsArrayParcel.conjugations.map { conjugationParcel ->
            Conjugation(conjugationParcel)
        }.toTypedArray()

        // get views and colors
        barTimer = view.barTimer
        textTimer = view.textTimer
        conjugationTable = view.conjugationTable
        tenseTextView = view.tenseTextView
        verbTextView = view.verbTextView
        englishVerbTextView = view.englishVerbTextView
        gray = ContextCompat.getColor(view.context, R.color.lightGray)
        val timer = view.timer
        val count = view.count
        finishButton = view.finishButton
        nextButton = view.nextButton

        // get preferences
        isFullConjugation = mContext.getIsFullConjugation()
        enabledThirdPersons = mContext.getThirdPersonSwitches()
        isCountSettings = mContext.getIsCountSetting()
        val personsFrequencies = mContext.getFrequencies()

        // get values based on preferences
        rowsEnabled = personsFrequencies.map { freq -> freq > 0 }.toBooleanArray()
        rowCount = when (isFullConjugation) {
            true -> rowsEnabled.filter { it }.size
            false -> 1
        }
        enabledPersons = rowsEnabled.mapIndexed { i, isEnabled -> if (isEnabled) allPersons[i] else null }.filterNotNull().toTypedArray()

        // set up finish button
        finishButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
            val (previousYear, previousDay) = mContext.getLastUsedDate()
            if (previousDay != currentDay || previousYear != currentYear) {
                mContext.resetTimesUsedToday()
                mContext.setLastUsedDate(currentYear, currentDay)
            } else {
                mContext.increaseTimesUsedToday()
            }

            mContext.updateWidget()

            updateResults()
            val resultArray = ResultArrayParcel(results.toTypedArray())
            val bundle = bundleOf("results" to resultArray)
            NavHostFragment.findNavController(parentFragmentManager.primaryNavigationFragment!!).navigate(R.id.action_practice_to_results, bundle)
        }

        // set up check button
        view.checkButton.setOnClickListener {
            val conjugation = conjugations[currentConjugation]

            if (isFullConjugation) {
                conjugationViewsPersons.forEachIndexed { i, person ->
                    checkAnswer(conjugation.personMap[allPersons[person]] ?: "", conjugationViews[i].ptVerbInput)
                }
            } else {
                checkAnswer(conjugation.personMap[allPersons[conjugation.person]] ?: "", conjugationViews[0].ptVerbInput)
            }
        }

        // set up counter and timer settings
        if (isCountSettings) {
            totalCount = when (mContext.getTimeAndCountPreference()[1]) {
                0 -> 5
                1 -> 10
                3 -> 20
                4 -> 25
                else -> 15
            }
            soFarCount = totalCount
            val totalCountStr = totalCount.toString()
            textCount = view.textCount
            textCount.text = totalCountStr
            view.textCountTotal.text = totalCountStr
            count.visibility = View.VISIBLE
            timer.visibility = View.GONE
            nextButton.setOnClickListener {
                goToNext()
            }
        } else {
            val mins = when (mContext.getTimeAndCountPreference()[0]) {
                0 -> 1
                1 -> 3
                3 -> 7
                4 -> 10
                else -> 5
            }
            barTimer.max = 60 * mins
            val rotate = AnimationUtils.loadAnimation(context, R.anim.rotate)
            barTimer.animation = rotate
            nextButton.setOnClickListener { goToNext() }
            timer.visibility = View.VISIBLE
            count.visibility = View.GONE
            startTimer(mins * 60)
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    goToPrevious()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // set up conjugation table and go to the first problem
        setUpGrid()
        goToPage(1)

        return view
    }

    private fun goToNext() {
        updateResults()
        goToPage(currentPage + 1)
    }

    private fun goToPrevious() {
        if (currentPage == 1) {
            return
        }
        updateResults()
        goToPage(currentPage - 1)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentPage", currentPage)
        outState.putInt("currentTime", secs)
        updateResults()
        outState.putParcelable("resultArrayParcel", ResultArrayParcel(results.toTypedArray()))
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getInt("currentPage")
            currentConjugation = (currentPage - 1) % conjugations.size
            secs = savedInstanceState.getInt("currentTime")
            if (isCountSettings) {
                soFarCount = totalCount + 1 - currentPage
                textCount.text = soFarCount.toString()
                if (soFarCount == 1) {
                    nextButton.visibility = View.GONE
                    finishButton.visibility = View.VISIBLE
                }
            } else {
                countDownTimer.cancel()
                startTimer(max(secs, 1))
            }
            val resultArrayParcel = savedInstanceState.getParcelable<ResultArrayParcel?>("resultArrayParcel")
            results = resultArrayParcel?.results?.toMutableList() ?: results

            goToPage(currentPage)
        }
    }

    private fun checkAnswer(answer: String, verbInput: EditText) {
        if (verbInput.text.toString() in answer.split("/")) {
            verbInput.setBackgroundResource(R.drawable.verb_input_edittext_correct)
        } else {
            verbInput.setBackgroundResource(R.drawable.verb_input_edittext_incorrect)
        }
    }

    private fun startTimer(secs: Int) {
        countDownTimer = object : CountDownTimer((secs * 1000).toLong(), 500) {
            // 500 means, onTick function will be called at every 500 milliseconds
            override fun onTick(leftTimeInMilliseconds: Long) {
                try {
                    val seconds = leftTimeInMilliseconds / 1000
                    barTimer.progress = seconds.toInt()
                    val secondHand = seconds % 60
                    val time = """${seconds / 60}:${if (secondHand < 10) "0" else ""}${secondHand}"""
                    textTimer.text = time
                    this@PracticeFragment.secs = seconds.toInt()
                } catch (e: IllegalStateException) {
                    nextButton.visibility = View.GONE
                    finishButton.visibility = View.VISIBLE
                }
            }

            override fun onFinish() {
                nextButton.visibility = View.GONE
                finishButton.visibility = View.VISIBLE
            }
        }.start()
    }

    private fun goToPage(pageIndex: Int) {
        // when this is called, page leaving will already be saved there
        currentPage = pageIndex
        currentConjugation = (pageIndex - 1) % conjugations.size
        if (isCountSettings) {
            soFarCount = totalCount + 1 - currentPage
            textCount.text = soFarCount.toString()
            if (soFarCount == 1) {
                nextButton.visibility = View.GONE
                finishButton.visibility = View.VISIBLE
            } else {
                finishButton.visibility = View.GONE
                nextButton.visibility = View.VISIBLE
            }
        }

        if (results.size == (currentPage - 1) * rowCount) {
            val conjugation = conjugations[currentConjugation]
            val persons = conjugation.personMap.keys.toTypedArray()
            for (i in 0 until rowCount) {
                val person = if (isFullConjugation) conjugationViewsPersons[i] else conjugation.person
                results = results + ResultParcel(
                    conjugation.verb,
                    conjugation.tense,
                    persons[i],
                    ConjugatorPortuguese.getSubject(person, enabledThirdPersons),
                    conjugation.personMap[allPersons[person]],
                    "",
                    i == 0,
                    currentPage
                )
            }
        }
        loadPageFromResults()
    }

    private fun loadPageFromResults() {
        val startIndex = (currentPage - 1) * rowCount
        val conjugation = conjugations[currentConjugation]
        val verb = StringHelper.capitalize(conjugation.verb)
        tenseTextView.text = ConjugatorPortuguese.getVerbFormString(conjugation.tense, resources)
        verbTextView.text = verb
        englishVerbTextView.text = StringHelper.capitalize(conjugation.enVerb.split("~")[0])
        for (i in 0 until rowCount) {
            val view = conjugationViews[i]
            val result = results[i + startIndex]
            val englishVerbString = formatEnglishVerbString(result.personsString, conjugation.verb, conjugation.enVerb)

            view.ptVerbInput.setText(result.input)
            view.ptSubject.text = result.personsString
            view.englishVerb.text = englishVerbString
        }
    }

    private fun formatEnglishVerbString(ptSubject: String, ptVerb: String, enVerb: String): String {
        return """${ConjugatorEnglish.getSubject(ptSubject)} $ptVerb (${enVerb.split("~")[1]})"""
    }

    private fun updateResults() {
        val startIndex = (currentPage - 1) * rowCount
        for (i in 0 until rowCount) {
            results[i + startIndex].input = conjugationViews[i].ptVerbInput.text.toString()
        }
    }

    private fun setUpGrid() {
        conjugationViews = Array(rowCount) { LinearLayout(context) }
        conjugationViewsPersons = Array(rowCount) {-1}
        conjugationTable.weightSum = (rowCount).toFloat()

        if (isFullConjugation) {
            var missingRows = 0
            for (i in 0 until 6) {
                if (rowsEnabled[i]) {
                    val index = i - missingRows
                    val cellLayout = getDefaultCellLayout()
                    cellLayout.showAnswer.setOnClickListener {
                        val answer = getDefaultAnswerFromConjugation(conjugations[currentConjugation].personMap[allPersons[i]] ?: "")
                        cellLayout.ptVerbInput.setText(answer)
                    }
                    conjugationViews[index] = cellLayout
                    conjugationViewsPersons[index] = i
                    conjugationTable.addView(cellLayout)
                } else {
                    missingRows++
                }
            }
        } else {
            val cellLayout = getDefaultCellLayout()
            cellLayout.showAnswer.setOnClickListener {
                val conjugation = conjugations[currentConjugation]
                val answer = getDefaultAnswerFromConjugation(conjugation.personMap[allPersons[conjugation.person]] ?: "")
                cellLayout.ptVerbInput.setText(answer)
            }
            conjugationViews[0] = cellLayout
            conjugationTable.addView(cellLayout)
        }
    }

    @SuppressLint("InflateParams")
    private fun getDefaultCellLayout(): LinearLayout {
        val cellLayout: LinearLayout = layoutInflater.inflate(R.layout.conjugation_cell_view, null) as LinearLayout
        cellLayout.ptVerbInput.addTextChangedListener {
            cellLayout.ptVerbInput.setBackgroundResource(R.drawable.verb_input_edittext)
        }
        return cellLayout
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.practice_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.showAnswers -> {
                val conjugation = conjugations[currentConjugation]
                for (i in 0 until rowCount) {
                    val person = if (isFullConjugation) conjugationViewsPersons[i] else conjugation.person
                    val answer = getDefaultAnswerFromConjugation(conjugation.personMap[allPersons[person]] ?: "")
                    conjugationViews[i].ptVerbInput.setText(answer)
                }
                return true
            }
            android.R.id.home -> {
                goToPrevious()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun getDefaultAnswerFromConjugation(conjugationAnswer: String?): String {
            if (conjugationAnswer == null) {
                return ""
            }
            val answer = conjugationAnswer.split("/")[0]
            return if (answer.indexOf("{d}") == -1) answer else ""
        }
    }
}