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
import android.util.Log
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
import com.sid.app.verbpractice.databinding.FragmentPracticeStartBinding


/**
 * Shows the main title screen with a button that navigates to About
 */
class PracticeStartFragment : Fragment() {

    private var _binding: FragmentPracticeStartBinding? = null
    private val binding get() = _binding!!
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
    ): View {
        _binding = FragmentPracticeStartBinding.inflate(inflater, container, false)
        val view = binding.root

        verbPools = arrayOf(
            binding.rbSelectedVerbs,
            binding.rbTop100, // > 2
            binding.rbTop500, // > 1
            binding.rbTop2000, // > 0
            binding.rbAllVerbs // > -1
        )

        //TODO why was this necessary?
        mContext.checkInitialization()

        val defaultTypes = mContext.getVerbTypes()
        val defaultSubtypes = mContext.getVerbSubtypes()
        val defaultPracticeMode = mContext.getDefaultPracticeMode()

        Log.v("default", defaultPracticeMode.toString())

        setPrefListener(binding.cbArVerbs, VerbSettingsManager.AR_ENABLED, defaultTypes.contains(1))
        setPrefListener(binding.cbErVerbs, VerbSettingsManager.ER_ENABLED, defaultTypes.contains(2))
        setPrefListener(binding.cbIrVerbs, VerbSettingsManager.IR_ENABLED, defaultTypes.contains(3))
        setPrefListener(binding.cbIrregVerbs, VerbSettingsManager.IRREG_ENABLED, defaultTypes.contains(4))

        setPrefListener(binding.cbRegVerbs, VerbSettingsManager.REG_SUBTYPE, defaultSubtypes.contains(1))
        setPrefListener(binding.cbRadical, VerbSettingsManager.RADICAL_SUBTYPE, defaultSubtypes.contains(2))
        setPrefListener(binding.cbOrthographic, VerbSettingsManager.ORTHOGRAPHIC_SUBTYPE, defaultSubtypes.contains(3))

        verbPools.forEachIndexed { index, radioButton ->
            radioButton.isChecked = false
            radioButton.setOnClickListener {
                mContext.onSetIntPreference(VerbSettingsManager.VERB_POOL, index)
                verbPools.forEachIndexed { i, buttonToDeselect -> if (i != index) buttonToDeselect.isChecked = false }
            }
        }

        verbPools[mContext.getVerbPool()].isChecked = true

        binding.rbQuiz.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mContext.setDefaultPracticeMode(0)
                binding.rbWordsearch.isChecked = false
                binding.rbCrossword.isChecked = false
            }
        }

        binding.rbWordsearch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mContext.setDefaultPracticeMode(1)
                binding.rbQuiz.isChecked = false
                binding.rbCrossword.isChecked = false
            }
        }

        binding.rbCrossword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mContext.setDefaultPracticeMode(2)
                binding.rbQuiz.isChecked = false
                binding.rbWordsearch.isChecked = false
            }
        }

        binding.rbQuiz.isChecked = defaultPracticeMode == 0
        binding.rbWordsearch.isChecked = defaultPracticeMode == 1
        binding.rbCrossword.isChecked = defaultPracticeMode == 2

        binding.startButton.setOnClickListener {
            val practiceMode = when {
                binding.rbQuiz.isChecked -> 0
                binding.rbWordsearch.isChecked -> 1
                else -> 2
            }
            val bundle = bundleOf("practiceMode" to practiceMode)
            Navigation.findNavController(view).navigate(R.id.action_start_to_loading, bundle)
        }
        return view
    }
}
