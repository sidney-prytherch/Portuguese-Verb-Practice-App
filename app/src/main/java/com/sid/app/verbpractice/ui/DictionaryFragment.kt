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

package com.sid.app.verbpractice.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
    private var text = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val actionBar = (activity as MainActivity?)?.supportActionBar
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

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        mWordViewModel = ViewModelProvider(this).get(PortugueseVerbViewModel::class.java)

        adapter = WordListAdapter(context)
        searchBar = view.findViewById(R.id.search_bar)
        recyclerView.adapter = adapter
        mWordViewModel.allPortugueseVerbs.observe(viewLifecycleOwner, Observer { words:List<PortugueseVerb> ->
            adapter.setWords(words)
            val searchFilter = searchBar.text.toString()
            if (searchFilter != "") {
                adapter.filter.filter(searchFilter)
            }
        })

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                adapter.filter.filter(s)
            }
        })

        clearButton.setOnClickListener {
            searchBar.setText("")
        }
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
            R.id.filter -> {
                adapter.specialFilter = true
                adapter.filter.filter(searchBar.text.toString())
                return true
            }
            R.id.unfilter -> {
                adapter.specialFilter = false
                adapter.filter.filter(searchBar.text.toString())
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}