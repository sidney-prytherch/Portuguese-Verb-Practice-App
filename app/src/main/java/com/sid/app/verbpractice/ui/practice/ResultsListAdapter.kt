package com.sid.app.verbpractice.ui.practice

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sid.app.verbpractice.R
import com.sid.app.verbpractice.helper.ConjugatorPortuguese
import com.sid.app.verbpractice.helper.ResultParcel
import com.sid.app.verbpractice.helper.StringHelper
import com.sid.app.verbpractice.ui.practice.PracticeFragment.Companion.getDefaultAnswerFromConjugation
import kotlinx.android.synthetic.main.recyclerview_results.view.*
import java.util.*
import kotlin.collections.ArrayList


class ResultsListAdapter(private val context: Context?) : RecyclerView.Adapter<ResultsListAdapter.ViewHolder>() {

    private var results:List<ResultParcel> = ArrayList()

    fun setResults(results: List<ResultParcel>) {
        this.results = ArrayList(results)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val resultsRows: LinearLayout = itemView.resultRows
        val numberTextView: TextView = itemView.number
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
        val inputString = if (wasCorrect) {
            holder.resultsRows.correction.visibility = View.GONE
            holder.resultsRows.input.setBackgroundColor(ContextCompat.getColor(context!!, R.color.correct))
            "You were correct: $input"
        } else {
            holder.resultsRows.correction.visibility = View.VISIBLE
            val correctAnswer = """${result.personsString} ${getDefaultAnswerFromConjugation(result.verbConjugation)}"""
            val correctionString = """The correct answer is: $correctAnswer"""
            val formattedCorrectionString = SpannableString(correctionString)
            formattedCorrectionString.setSpan(UnderlineSpan(), correctionString.length - correctAnswer.length, correctionString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            holder.resultsRows.correction.text = formattedCorrectionString
            holder.resultsRows.input.setBackgroundColor(ContextCompat.getColor(context!!, R.color.incorrect))
            holder.resultsRows.correction.setBackgroundColor(ContextCompat.getColor(context, R.color.incorrect))
            if (result.input.isBlank()) {
                "You left this blank."
            } else {
                "You were incorrect. You said: $input"
            }
        }
        val formattedInputString = SpannableString(inputString)
        if (!result.input.isBlank()) {
            formattedInputString.setSpan(UnderlineSpan(), inputString.length - input.length, inputString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        holder.resultsRows.input.text = formattedInputString
        if (result.isFirst) {
            val verbInfoString = """${StringHelper.capitalize(result.verb)} in the ${ConjugatorPortuguese.getVerbFormString(result.tense, context.resources)
                .toLowerCase(Locale.ROOT)} tense"""
            holder.numberTextView.text = result.count.toString()
            holder.resultsRows.verbInfo.text = verbInfoString
            holder.resultsRows.verbInfo.visibility = View.VISIBLE
            holder.numberTextView.visibility = View.VISIBLE
        } else {
            holder.numberTextView.text = ""
            holder.resultsRows.verbInfo.text = ""
            holder.resultsRows.verbInfo.visibility = View.GONE
            holder.numberTextView.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return results.size
    }
}
