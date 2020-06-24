package com.sid.app.verbpractice.ui.dictionary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.os.bundleOf
import androidx.fragment.app.findFragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.sid.app.verbpractice.MainActivity
import com.sid.app.verbpractice.R
import com.sid.app.verbpractice.db.entity.PortugueseVerb
import kotlinx.android.synthetic.main.recyclerview_verbs.view.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList


class WordListAdapter(private val context: Context?) : RecyclerView.Adapter<WordListAdapter.ViewHolder>(), Filterable {

    private var isBusy = false
    var showSelected = true
    var showUnselected = true
    var verbCommonVal = 1
    private var words:List<PortugueseVerb> = ArrayList()
    private var allWords: List<PortugueseVerb> = ArrayList()

    fun setWords(allVerbs: List<PortugueseVerb>) {
        this.allWords = allVerbs
        words = ArrayList(allVerbs)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordView: CheckBox = itemView.verbTextView
        val defView: TextView = itemView.definition
        val practiceButton: AppCompatImageButton = itemView.practiceVerb
        val conjugationButton: AppCompatImageButton = itemView.viewConjugations
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_verbs, parent, false)
        return ViewHolder(
            itemView
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        isBusy = true
        holder.wordView.text = words[position].verb
        val definition = """- ${words[position].main_def}"""
        holder.defView.text = definition
        holder.wordView.isChecked = words[position].added == 1
        holder.wordView.setOnCheckedChangeListener { _, isChecked ->
            if (!isBusy) {
                updateChecked(words[position].verb_id, if (isChecked) 1 else 0, position)
            }
        }
        holder.practiceButton.setOnClickListener {
            val bundle = bundleOf("verb" to words[position].verb)
            NavHostFragment.findNavController(it.findFragment()).navigate(R.id.action_dictionary_to_practice, bundle)
        }
        holder.conjugationButton.setOnClickListener {
            val bundle = bundleOf("verb" to words[position].verb, "isConjugationView" to true)
            NavHostFragment.findNavController(it.findFragment()).navigate(R.id.action_dictionary_to_practice, bundle)
        }
        isBusy = false
    }

    private fun updateChecked(verbId: Int, isChecked: Int, position: Int) = CoroutineScope(Dispatchers.Main).launch {
        if (context is MainActivity) {
            val task = async(Dispatchers.IO) {
                context.updateCheckedInDb(verbId, isChecked)
            }
            task.await()
            notifyItemChanged(position)
        }
    }

    fun resetAllWords() = CoroutineScope(Dispatchers.Main).launch {
        if (context is MainActivity) {
            context.resetAllChecked()
        }
    }

    override fun getItemCount(): Int {
        return words.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                words = filterResults.values as List<PortugueseVerb>
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase(Locale.ROOT)

                val filterResults = FilterResults()

                val preFilteredWords = if (queryString.isNullOrBlank()) {
                    if (showSelected && showUnselected) {
                        allWords.filter { it.verb_group >= verbCommonVal }
                    } else if (!showUnselected && showSelected) {
                        allWords.filter { it.added == 1 && it.verb_group >= verbCommonVal }
                    } else if (showUnselected && !showSelected) {
                        allWords.filter { it.added == 0 && it.verb_group >= verbCommonVal }
                    } else {
                        allWords.filter { it.added == -1 }
                    }
                } else {
                    allWords
                }

                filterResults.values = if (queryString.isNullOrBlank()) {
                    preFilteredWords
                } else {
                    val orderedSearchedList = preFilteredWords.filter {
                        it.verb.toLowerCase(Locale.ROOT).contains(queryString)
                    }.sortedBy { !it.verb.startsWith(queryString, true) }.toMutableList()
                    orderedSearchedList.addAll(preFilteredWords.filter {
                        it.main_def.toLowerCase(Locale.ROOT).contains(queryString)
                    })
                    orderedSearchedList.toList()
                }
                return filterResults
            }
        }
    }
}

