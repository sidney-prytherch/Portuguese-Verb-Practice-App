/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sid.app.verbpractice.ui.dictionary

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sid.app.verbpractice.MainActivity
import com.sid.app.verbpractice.R
import com.sid.app.verbpractice.db.entity.PortugueseVerb
import com.sid.app.verbpractice.viewmodel.PortugueseVerbViewModel
import kotlinx.android.synthetic.main.fragment_dictionary.*
import kotlinx.android.synthetic.main.fragment_dictionary.view.*


class DictionaryFragment : Fragment() {

    private lateinit var mWordViewModel: PortugueseVerbViewModel
    private lateinit var adapter: WordListAdapter
    private lateinit var searchBar: EditText
    private lateinit var filterResults: BooleanArray
    private var commonVerbVal = 1
    private var text = ""
    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mainActivity = context as MainActivity
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement SetAndGetIndicativeSettings"))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val actionBar = mainActivity.supportActionBar
        if (actionBar != null) {
            actionBar.elevation = 0f
        }
        return inflater.inflate(R.layout.fragment_dictionary, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("search", searchBar.text.toString())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        text = savedInstanceState?.getString("search") ?: ""
    }

    override fun onResume() {
        super.onResume()
        searchBar.setText(text)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.verbs_recyclerview

        filterResults = mainActivity.getFilterSettings()
        commonVerbVal = mainActivity.getCommonVerbValue()

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        mWordViewModel = ViewModelProvider(this).get(PortugueseVerbViewModel::class.java)

        adapter = WordListAdapter(context)
        searchBar = view.findViewById(R.id.search_bar)
        recyclerView.adapter = adapter
        mWordViewModel.allPortugueseVerbs.observe(viewLifecycleOwner, Observer { words:List<PortugueseVerb> ->
            adapter.setWords(words)
            resetFilter(filterResults, commonVerbVal)
        })

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                resetFilter(filterResults, commonVerbVal)
            }
        })

        clearButton.setOnClickListener {
            searchBar.setText("")
        }
    }

    fun resetFilter(selectedResults:BooleanArray, verbCommonVal: Int) {
        filterResults = selectedResults
        commonVerbVal = verbCommonVal
        adapter.showSelected = selectedResults[0]
        adapter.showUnselected = selectedResults[1]
        adapter.verbCommonVal = verbCommonVal
        adapter.filter.filter(searchBar.text.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.dictionary_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.resetAllWords -> {
                adapter.resetAllWords()
                return true
            }
            R.id.filterWords -> {
                NavHostFragment.findNavController(parentFragmentManager.primaryNavigationFragment!!).navigate(R.id.action_dictionary_to_filter)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}