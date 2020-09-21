package com.sid.app.verbpractice.ui.dictionary

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.sid.app.verbpractice.MainActivity
import com.sid.app.verbpractice.R
import com.sid.app.verbpractice.enums.Person
import com.sid.app.verbpractice.helper.*
import kotlinx.android.synthetic.main.conjugation_view.view.*
import kotlinx.android.synthetic.main.fragment_practice.view.*


class ConjugationViewerFragment : Fragment() {

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
    private var currentConjugation = -1
    private var currentPage = 0

    private var gray = 0
    private val allPersons = arrayOf(Person.FIRST_SING, Person.SECOND_SING, Person.THIRD_SING, Person.FIRST_PLUR, Person.SECOND_PLUR, Person.THIRD_PLUR)
    private val allPersonStrings = arrayOf("Eu", "Tu", "Você/Ele/Ela", "Nós", "Vós", "Vocês/Eles/Elas")
    private lateinit var personStrings: Array<String>
    private lateinit var enabledPersons: Array<Person>
    private lateinit var enabledThirdPersons: BooleanArray
    private lateinit var rowsEnabled: BooleanArray
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
        val view = inflater.inflate(R.layout.fragment_practice, container, false)

        conjugationsArrayParcel = (arguments?.get("conjugations") as ConjugationArrayParcel)
        conjugations = conjugationsArrayParcel.conjugations.map { conjugationParcel ->
            Conjugation(conjugationParcel)
        }.toTypedArray()

        // get views and colors
        conjugationTable = view.conjugationTable
        tenseTextView = view.tenseTextView
        verbTextView = view.verbTextView
        englishVerbTextView = view.englishVerbTextView
        gray = ContextCompat.getColor(view.context, R.color.lightGray)
        finishButton = view.finishButton
        nextButton = view.nextButton

        // get preferences
        enabledThirdPersons = mContext.getThirdPersonSwitches()
        val personsFrequencies = mContext.getFrequencies()

        // get values based on preferences
        rowsEnabled = personsFrequencies.map { freq -> freq > 0 }.toBooleanArray()
        rowCount = rowsEnabled.filter { it }.size
        enabledPersons = rowsEnabled.mapIndexed { i, isEnabled -> if (isEnabled) allPersons[i] else null }.filterNotNull().toTypedArray()
        personStrings = rowsEnabled.mapIndexed { i, isEnabled -> if (isEnabled) allPersonStrings[i] else null }.filterNotNull().toTypedArray()

        // set up finish button
        finishButton.setOnClickListener {
            NavHostFragment.findNavController(parentFragmentManager.primaryNavigationFragment!!).navigate(R.id.action_conjugations_to_dictionary)
        }

        view.timer.visibility = View.GONE
        view.count.visibility = View.GONE

        view.checkButton.text = getString(R.string.previous)
        view.checkButton.setOnClickListener {
            goToPrevious()
        }

        nextButton.setOnClickListener {
            goToNext()
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
        goToPage(currentPage + 1)
    }

    private fun goToPrevious() {
        if (currentPage == 1) {
            return
        }
        goToPage(currentPage - 1)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentPage", currentPage)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getInt("currentPage")
            currentConjugation = (currentPage - 1) % conjugations.size
            goToPage(currentPage)
        }
    }

    private fun goToPage(pageIndex: Int) {
        // when this is called, page leaving will already be saved there
        currentPage = pageIndex
        currentConjugation = (pageIndex - 1) % conjugations.size
        if (pageIndex == conjugations.size) {
            nextButton.visibility = View.GONE
            finishButton.visibility = View.VISIBLE
        } else {
            finishButton.visibility = View.GONE
            nextButton.visibility = View.VISIBLE
        }
        loadPageFromResults()
    }

    private fun loadPageFromResults() {
        val conjugation = conjugations[currentConjugation]
        val verb = StringHelper.capitalize(conjugation.verb)
        val portuguesePersonString = Array(rowCount) {i ->
            personStrings[i]
        }
        // 1 : fly
        // 2 : flies
        // 3 : flew
        // 4 : flown
        // 5 : flying
        val englishParts = conjugation.enVerb.split("|")
        val englishConjugations = ConjugatorEnglish.conjugate(
            englishParts[6],
            conjugation.tense,
            portuguesePersonString,
            englishParts[5],
            englishParts[4],
            englishParts[1],
            englishParts[2],
            englishParts[3]
        )
        tenseTextView.text = ConjugatorPortuguese.getVerbFormString(conjugation.tense, resources)
        verbTextView.text = verb
        englishVerbTextView.text = StringHelper.capitalize(conjugation.enVerb.split("~")[0])
        for (i in 0 until rowCount) {
            val person = conjugationViewsPersons[i]
            val view = conjugationViews[i]
            val answer = getDefaultAnswerFromConjugation(conjugation.personMap[allPersons[person]] ?: "")
            val portConj = "${personStrings[i]} $answer"

            var engCong = englishConjugations[i]
            val formattedEnglishConjugation = SpannableString(engCong.replace("<", "").replace(">", ""))
            while (engCong.indexOf("<") != -1) {
                val start = engCong.indexOf("<")
                val end = engCong.indexOf(">") - 1
                formattedEnglishConjugation.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                engCong = engCong.replaceFirst("<", "").replaceFirst(">", "")
            }

            view.portConj.text = portConj
            view.englishVerb.text = formattedEnglishConjugation
        }
    }

    private fun setUpGrid() {
        conjugationViews = Array(rowCount) { LinearLayout(context) }
        conjugationViewsPersons = Array(rowCount) {-1}
        conjugationTable.weightSum = (rowCount).toFloat()
        var missingRows = 0
        for (i in 0 until 6) {
            if (rowsEnabled[i]) {
                val index = i - missingRows
                val cellLayout = getDefaultCellLayout()
                conjugationViews[index] = cellLayout
                conjugationViewsPersons[index] = i
                conjugationTable.addView(cellLayout)
            } else {
                missingRows++
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun getDefaultCellLayout(): LinearLayout {
        return layoutInflater.inflate(R.layout.conjugation_view, null) as LinearLayout
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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