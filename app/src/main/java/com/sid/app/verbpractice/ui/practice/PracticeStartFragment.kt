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

package com.sid.app.verbpractice.ui.practice

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.sid.app.verbpractice.MainActivity
import com.sid.app.verbpractice.R
import com.sid.app.verbpractice.helper.VerbSettingsManager
import kotlinx.android.synthetic.main.fragment_practice_start.view.*

/**
 * Shows the main title screen with a button that navigates to About
 */
class PracticeStartFragment : Fragment() {

    private lateinit var mContext: MainActivity
    private lateinit var verbPools: Array<RadioButton>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mContext = context as MainActivity
        } catch (e: ClassCastException) {
            throw e
        }
    }

    private fun setPrefListener(checkBox: CheckBox, preference: String, shouldBeChecked: Boolean) {
        checkBox.isChecked = shouldBeChecked
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            mContext.onSetBooleanPreference(preference, isChecked)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_practice_start, container, false)

        verbPools = arrayOf(
            view.rbSelectedVerbs,
            view.rbTop100, // > 2
            view.rbTop500, // > 1
            view.rbTop2000, // > 0
            view.rbAllVerbs // > -1
        )

        val defaultTypes = mContext.getVerbTypes()
        val defaultSubtypes = mContext.getVerbSubtypes()

        setPrefListener(view.cbArVerbs, VerbSettingsManager.AR_ENABLED, defaultTypes.contains(1))
        setPrefListener(view.cbErVerbs, VerbSettingsManager.ER_ENABLED, defaultTypes.contains(2))
        setPrefListener(view.cbIrVerbs, VerbSettingsManager.IR_ENABLED, defaultTypes.contains(3))
        setPrefListener(view.cbIrregVerbs, VerbSettingsManager.IRREG_ENABLED, defaultTypes.contains(4))

        setPrefListener(view.cbRegVerbs, VerbSettingsManager.REG_SUBTYPE, defaultSubtypes.contains(1))
        setPrefListener(view.cbRadical, VerbSettingsManager.RADICAL_SUBTYPE, defaultSubtypes.contains(2))
        setPrefListener(view.cbOrthographic, VerbSettingsManager.ORTHOGRAPHIC_SUBTYPE, defaultSubtypes.contains(3))

        verbPools.forEachIndexed { index, radioButton ->
            radioButton.isChecked = false
            radioButton.setOnClickListener {
                mContext.onSetIntPreference(VerbSettingsManager.VERB_POOL, index)
                verbPools.forEachIndexed { i, buttonToDeselect -> if (i != index) buttonToDeselect.isChecked = false }
            }
        }

        verbPools[mContext.getVerbPool()].isChecked = true

        view.rbQuiz.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                view.rbWordsearch.isChecked = false
            }
        }

        view.rbWordsearch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                view.rbQuiz.isChecked = false
            }
        }

        view.startButton.setOnClickListener {
            val bundle = bundleOf("isWordsearch" to view.rbWordsearch.isChecked)
            Navigation.findNavController(view).navigate(R.id.action_start_to_loading, bundle)
        }
        return view
    }
}
