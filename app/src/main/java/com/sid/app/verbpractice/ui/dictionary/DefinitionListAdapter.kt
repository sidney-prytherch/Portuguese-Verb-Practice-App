package com.sid.app.verbpractice.ui.dictionary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.sid.app.verbpractice.MainActivity
import com.sid.app.verbpractice.R
import com.sid.app.verbpractice.db.entity.PortugueseVerbDefinition
import kotlinx.coroutines.*
import kotlin.collections.ArrayList


class DefinitionListAdapter(private val context: Context?) : RecyclerView.Adapter<DefinitionListAdapter.ViewHolder>() {

    private var isBusy = false
    private var definitions: List<PortugueseVerbDefinition> = ArrayList()

    fun setDefinitions(definitions: List<PortugueseVerbDefinition>) {
        this.definitions = definitions
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordView: CheckBox = itemView.findViewById(R.id.verbTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_definitions, parent, false)
        return ViewHolder(
            itemView
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        isBusy = true
        holder.wordView.text = definitions[position].definition
        holder.wordView.isChecked = definitions[position].added == 1
        holder.wordView.setOnCheckedChangeListener { _, isChecked ->
            if (!isBusy) {
                doThing(definitions[position].definition_id, if (isChecked) 1 else 0, position)
            }
        }
        isBusy = false
    }

    private fun doThing(definitionId: Int, isChecked: Int, position: Int) = CoroutineScope(Dispatchers.Main).launch {
        if (context is MainActivity) {
            val task = async(Dispatchers.IO) {
                context.updateCheckedInDbForDefinition(definitionId, isChecked)
            }
            task.await()
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return definitions.size
    }
}





//
//class WordListAdapter internal constructor(context: Context?)//, listener: OnCheckboxChangeListener)
// : RecyclerView.Adapter<WordViewHolder>() {
//
//    class WordViewHolder(itemView: View)//, listener: OnCheckboxChangeListener)
//        : RecyclerView.ViewHolder(itemView) {
//        val wordItemView: TextView
////        private val definitionView: RecyclerView
////        val adapter: DefinitionListAdapter
//        fun bind(verb: PortugueseVerb) { // Get the state
//            val expanded = verb.getExpanded()
//            // Set the visibility based on state
//            definitionView.visibility = if (expanded) View.VISIBLE else View.GONE
//        }
//
//        init {
//            wordItemView = itemView.findViewById(R.id.textView)
//            definitionView = itemView.findViewById(R.id.definitions_recyclerview)
//            definitionView.visibility = View.GONE
//            adapter = DefinitionListAdapter(wordItemView.context, listener)
//            definitionView.adapter = adapter
//            definitionView.layoutManager = LinearLayoutManager(wordItemView.context)
//        }
//    }
//
//    RecyclerView.Adapter<MyAdapter.ViewHolder>() {
//
//    // Provide a reference to the views for each data item
//    // Complex data items may need more than one view per item, and
//    // you provide access to all the views for a data item in a view holder.
//    // Each data item is just a string in this case that is shown in a TextView.
//    class ViewHolder(val item: View) : RecyclerView.ViewHolder(item)
//
//
//    // Create new views (invoked by the layout manager)
//    override fun onCreateViewHolder(parent: ViewGroup,
//                                    viewType: Int): ViewHolder {
//        // create a new view
//        val itemView = LayoutInflater.from(parent.context)
//            .inflate(R.layout.list_view_item, parent, false)
//
//
//        return ViewHolder(itemView)
//    }
//
//    // Replace the contents of a view (invoked by the layout manager)
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        // - get element from your dataset at this position
//        // - replace the contents of the view with that element
//        holder.item.findViewById<TextView>(R.id.user_name_text).text = myDataset[position]
//
//        holder.item.findViewById<ImageView>(R.id.user_avatar_image)
//                .setImageResource(listOfAvatars[position % listOfAvatars.size])
//
//        holder.item.setOnClickListener {
//            val bundle = bundleOf(USERNAME_KEY to myDataset[position])
//
//        }
//    }
//
//    // Return the size of your dataset (invoked by the layout manager)
//    override fun getItemCount() = myDataset.size
//
//    companion object {
//        const val USERNAME_KEY = "userName"
//    }
//}
//
//private val listOfAvatars = listOf(
//    R.drawable.avatar_1_raster,
//    R.drawable.avatar_2_raster,
//    R.drawable.avatar_3_raster,
//    R.drawable.avatar_4_raster,
//    R.drawable.avatar_5_raster,
//    R.drawable.avatar_6_raster
//)

//    class WordViewHolder(itemView: View, listener: OnCheckboxChangeListener) : RecyclerView.ViewHolder(itemView) {
//        val wordItemView: TextView
//        private val definitionView: RecyclerView
//        val adapter: DefinitionListAdapter
//        fun bind(verb: PortugueseVerb) { // Get the state
//            val expanded = verb.getExpanded()
//            // Set the visibility based on state
//            definitionView.visibility = if (expanded) View.VISIBLE else View.GONE
//        }
//
//        init {
//            wordItemView = itemView.findViewById(R.id.textView)
//            definitionView = itemView.findViewById(R.id.definitions_recyclerview)
//            definitionView.visibility = View.GONE
//            adapter = DefinitionListAdapter(wordItemView.context, listener)
//            definitionView.adapter = adapter
//            definitionView.layoutManager = LinearLayoutManager(wordItemView.context)
//        }
//    }
//
//    private val mListener: OnCheckboxChangeListener = listener
//    var words: List<PortugueseVerb>? = ArrayList()
//        private set
//    private val mInflater: LayoutInflater = LayoutInflater.from(context)
//    private var mWords // Cached copy of words
//            : MutableList<PortugueseVerb>? = null
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
//        val itemView = mInflater.inflate(R.layout.recyclerview_verbs, parent, false)
//        return WordViewHolder(itemView, mListener)
//    }
//
//    fun setWords(verbs: MutableList<PortugueseVerb>?) {
//        mWords = verbs
//        if (words != null) {
//            words.addAll(mWords)
//            notifyDataSetChanged()
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return if (words == null) {
//            0
//        } else words!!.size
//    }
//
//    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
//        if (words != null) {
//            val current = words!![position]
//            holder.wordItemView.text = current.getVerb()
//            holder.bind(current)
//            holder.adapter.setDefinitions(current.getDefinitions())
//            holder.wordItemView.setOnClickListener {
//                // Get the current state of the item
//                val expanded = current.getExpanded()
//                // Change the state
//                current.setExpanded(!expanded)
//                // Notify the adapter that item has changed
//                notifyItemChanged(position)
//                Log.v("expand", "" + position)
//            }
//        }
//    }
//
//    val filter: Filter
//        get() = object : Filter() {
//            override fun performFiltering(charSequence: CharSequence): FilterResults {
//                val charString = charSequence.toString()
//                var filteredList: MutableList<PortugueseVerb>? = ArrayList()
//                if (charString.isEmpty()) {
//                    filteredList = mWords
//                } else {
//                    for (row in mWords!!) {
//                        val verb = row.getVerb()
//                        if (verb.toLowerCase().substring(0, Math.min(charString.length, verb.length)) == charString.toLowerCase()) {
//                            filteredList!!.add(row)
//                        }
//                    }
//                }
//                val filterResults = FilterResults()
//                filterResults.values = filteredList
//                return filterResults
//            }
//
//            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
//                words = filterResults.values as ArrayList<PortugueseVerb>
//                notifyDataSetChanged()
//            }
//        }
//
//}