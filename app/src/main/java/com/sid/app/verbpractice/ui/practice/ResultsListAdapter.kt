package com.sid.app.verbpractice.ui.practice

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sid.app.verbpractice.R
import com.sid.app.verbpractice.helper.ConjugatorPortuguese
import com.sid.app.verbpractice.helper.ResultParcel
import com.sid.app.verbpractice.helper.StringHelper
import com.sid.app.verbpractice.ui.practice.PracticeFragment.Companion.getDefaultAnswerFromConjugation
import com.sid.app.verbpractice.databinding.RecyclerviewResultsBinding

import java.util.*
import kotlin.collections.ArrayList


class ResultsListAdapter(private val context: Context?) : RecyclerView.Adapter<ResultsListAdapter.ViewHolder>() {

    private var results:List<ResultParcel> = ArrayList()

    fun setResults(results: List<ResultParcel>) {
        this.results = ArrayList(results)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val resultsRows: LinearLayout = itemView.findViewById(R.id.resultRows)
        val numberTextView: TextView = itemView.findViewById(R.id.number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_results, parent, false)
        return ViewHolder(
            itemView
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]
        val wasCorrect = result.input == getDefaultAnswerFromConjugation(result.verbConjugation)
        val input = """${result.personsString} ${result.input}""".trim()
        val correctionTextView = holder.resultsRows.findViewById<TextView>(R.id.correction)
        val inputTextView = holder.resultsRows.findViewById<TextView>(R.id.input)
        val verbInfoTextView = holder.resultsRows.findViewById<TextView>(R.id.verbInfo)
        val inputString = if (wasCorrect) {
            correctionTextView.visibility = View.GONE
            inputTextView.setBackgroundColor(ContextCompat.getColor(context!!, R.color.correct))
            "You were correct: $input"
        } else {
            correctionTextView.visibility = View.VISIBLE
            val correctAnswer = """${result.personsString} ${getDefaultAnswerFromConjugation(result.verbConjugation)}"""
            val correctionString = """The correct answer is: $correctAnswer"""
            val formattedCorrectionString = SpannableString(correctionString)
            formattedCorrectionString.setSpan(UnderlineSpan(), correctionString.length - correctAnswer.length, correctionString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            correctionTextView.text = formattedCorrectionString
            inputTextView.setBackgroundColor(ContextCompat.getColor(context!!, R.color.incorrect))
            correctionTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.incorrect))
            if (result.input.isBlank()) {
                "You left this blank."
            } else {
                "You were incorrect. You said: $input"
            }
        }
        val formattedInputString = SpannableString(inputString)
        if (result.input.isNotBlank()) {
            formattedInputString.setSpan(UnderlineSpan(), inputString.length - input.length, inputString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        inputTextView.text = formattedInputString
        if (result.isFirst) {
            val verbInfoString = """${StringHelper.capitalize(result.verb)} in the ${ConjugatorPortuguese.getVerbFormString(result.tense, context.resources)
                .toLowerCase(Locale.ROOT)} tense"""
            holder.numberTextView.text = result.count.toString()
            verbInfoTextView.text = verbInfoString
            verbInfoTextView.visibility = View.VISIBLE
            holder.numberTextView.visibility = View.VISIBLE
        } else {
            holder.numberTextView.text = ""
            verbInfoTextView.text = ""
            verbInfoTextView.visibility = View.GONE
            holder.numberTextView.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return results.size
    }
}
