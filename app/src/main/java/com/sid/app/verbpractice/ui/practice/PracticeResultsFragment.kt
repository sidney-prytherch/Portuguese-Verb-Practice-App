package com.sid.app.verbpractice.ui.practice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sid.app.verbpractice.MainActivity
import com.sid.app.verbpractice.R
import com.sid.app.verbpractice.helper.ResultArrayParcel
import com.sid.app.verbpractice.databinding.FragmentPracticeResultsBinding


class PracticeResultsFragment : Fragment() {

    private lateinit var adapter: ResultsListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_practice_results, container, false)

        (activity as MainActivity?)?.supportActionBar?.setTitle(R.string.title_results)

        val results = (arguments?.get("results") as ResultArrayParcel).results.toList()
        val singleVerb = (arguments?.getString("verb"))

        val recyclerView: RecyclerView = view.findViewById(R.id.resultsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        adapter = ResultsListAdapter(context)
        recyclerView.adapter = adapter
        adapter.setResults(results)

        view.findViewById<AppCompatButton>(R.id.restart).setOnClickListener {
            if (singleVerb == null) {
                Navigation.findNavController(view).navigate(R.id.action_results_to_loading)
            } else {
                val bundle = bundleOf("verb" to singleVerb)
                Navigation.findNavController(view).navigate(R.id.action_results_to_loading, bundle)
            }
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}