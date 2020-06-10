package com.sid.app.verbpractice.ui.Practice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sid.app.verbpractice.R
import com.sid.app.verbpractice.helper.ConjugatorPortuguese
import com.sid.app.verbpractice.helper.ResultParcel
import com.sid.app.verbpractice.helper.StringHelper
import com.sid.app.verbpractice.ui.Practice.PracticeFragment.Companion.getDefaultAnswerFromConjugation
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
        val inputString = """You said: ${result.personsString} ${result.input}"""
        val correctionString = """The correct answer is: ${result.personsString} ${getDefaultAnswerFromConjugation(result.verbConjugation)}"""
        holder.resultsRows.input.text = inputString
        holder.resultsRows.correction.text = correctionString
        if (result.isFirst) {
            val verbInfoString = """${StringHelper.capitalize(result.verb)} in the ${ConjugatorPortuguese.getVerbFormString(result.tense, context!!.resources)
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
