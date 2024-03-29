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

package com.sid.app.verbpractice.ui.options

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import com.sid.app.verbpractice.MainActivity
import com.sid.app.verbpractice.R
import com.sid.app.verbpractice.helper.VerbSettingsManager
import com.sid.app.verbpractice.databinding.FragmentOptionsBinding


/**
 * Shows a register form to showcase UI state persistence. It has a button that goes to Registered
 */
class OptionsFragment : Fragment(), SeekBar.OnSeekBarChangeListener {

    private var _binding: FragmentOptionsBinding? = null
    private val binding get() = _binding!!

    private var disabledColor: Int = 0
    private var enabledColor: Int = 0
    private val frequencyKeys = arrayOf(
        VerbSettingsManager.EU_FREQUENCY,
        VerbSettingsManager.TU_FREQUENCY,
        VerbSettingsManager.VC_ELE_ELA_FREQUENCY,
        VerbSettingsManager.NOS_FREQUENCY,
        VerbSettingsManager.VOS_FREQUENCY,
        VerbSettingsManager.VCS_ELES_ELAS_FREQUENCY
    )

    var switches = arrayOf<SwitchCompat>()
//    var thirdPersonSwitches = arrayOf<SwitchCompat>()
    private lateinit var conjugationsSwitch: SwitchCompat
    private lateinit var fullConjugations: TextView
    private lateinit var partialConjugations: TextView
    private lateinit var portugalSwitch: SwitchCompat
    private lateinit var portugal: TextView
    private lateinit var brazil: TextView
    private var bars = arrayOf<AppCompatSeekBar>()
    lateinit var verbSettingsManager: VerbSettingsManager
    private lateinit var mContext: MainActivity
    private lateinit var enabledVerbTypes: Array<CheckBox>
    private lateinit var tenseTextBoxes: Array<TextView>
    private lateinit var personsTextView: TextView

    private lateinit var indicatives: Array<String>
    private lateinit var perfects: Array<String>
    private lateinit var progressives: Array<String>
    private lateinit var subjunctives: Array<String>
    private lateinit var persons: Array<String>
    private lateinit var timeOrCountSwitch: SwitchCompat
    private lateinit var timeOption: TextView
    private lateinit var countOption: TextView
    private lateinit var timeOptions: LinearLayout
    private lateinit var countOptions: LinearLayout


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mContext = context as MainActivity
        } catch (e: ClassCastException) {
            throw e
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOptionsBinding.inflate(inflater, container, false)
        val view = binding.root
        setHasOptionsMenu(true)

        indicatives = arrayOf(
            resources.getString(R.string.present),
            resources.getString(R.string.preterite),
            resources.getString(R.string.imperfect),
            resources.getString(R.string.simple_pluperfect),
            resources.getString(R.string.simple_future),
            resources.getString(R.string.conditional)
        )
        perfects = arrayOf(
            resources.getString(R.string.future),
            resources.getString(R.string.present_perfect),
            resources.getString(R.string.pluperfect),
            resources.getString(R.string.future_perfect),
            resources.getString(R.string.conditional_perfect),
            resources.getString(R.string.past_intent)
        )
        progressives = arrayOf(
            resources.getString(R.string.present_progressive),
            resources.getString(R.string.preterite_progressive),
            resources.getString(R.string.imperfect_progressive),
            resources.getString(R.string.simple_pluperfect_progressive),
            resources.getString(R.string.future_progressive),
            resources.getString(R.string.conditional_progressive),
            resources.getString(R.string.present_perfect_progressive),
            resources.getString(R.string.pluperfect_progressive),
            resources.getString(R.string.future_perfect_progressive),
            resources.getString(R.string.conditional_perfect_progressive)
        )
        subjunctives = arrayOf(
            resources.getString(R.string.present_subjunctive),
            resources.getString(R.string.imperfect_subjunctive),
            resources.getString(R.string.future_subjunctive),
            resources.getString(R.string.present_perfect_subjunctive),
            resources.getString(R.string.pluperfect_subjunctive),
            resources.getString(R.string.future_perfect_subjunctive)
        )

        persons = arrayOf(
            resources.getString(R.string.vc),
            resources.getString(R.string.vcs),
            resources.getString(R.string.ele_ela),
            resources.getString(R.string.eles_elas),
            resources.getString(R.string.senhor),
            resources.getString(R.string.senhores)
        )


        enabledVerbTypes = arrayOf(
            binding.arVerbType,
            binding.erVerbType,
            binding.irVerbType,
            binding.irregVerbType
        )

        switches = arrayOf(
            binding.euSwitch,
            binding.tuSwitch,
            binding.vcEleElaSwitch,
            binding.nosSwitch,
            binding.vosSwitch,
            binding.vcsElesElasSwitch
        )

//        thirdPersonSwitches = arrayOf(
//            view.vcEnabledSwitch,
//            view.eleElaEnabledSwitch,
//            view.senhorEnabledSwitch,
//            view.vcsEnabledSwitch,
//            view.elesElasEnabledSwitch,
//            view.senhoresEnabledSwitch
//        )

        bars = arrayOf(
            binding.euBar,
            binding.tuBar,
            binding.vcEleElaBar,
            binding.nosBar,
            binding.vosBar,
            binding.vcsElesElasBar
        )

        val enabledVerbTypeKeys = arrayOf(
            VerbSettingsManager.AR_ENABLED,
            VerbSettingsManager.ER_ENABLED,
            VerbSettingsManager.IR_ENABLED,
            VerbSettingsManager.IRREG_ENABLED
        )

        val enabledVerbTypesDefaults = mContext.getVerbTypes()

        enabledVerbTypes.forEachIndexed { index, checkBox ->
            checkBox.isChecked = enabledVerbTypesDefaults.contains(index + 1)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                mContext.onSetBooleanPreference(enabledVerbTypeKeys[index], isChecked)
            }
        }

        tenseTextBoxes = arrayOf(
            binding.setSimpIndTenses,
            binding.setCompIndTenses,
            binding.setProgIndTenses,
            binding.setSubjTenses
        )

        personsTextView = binding.setEnabledThirdPersons

        resetSimpIndTextView()
        resetCompIndTextView()
        resetProgIndTextView()
        resetSubjTextView()
        resetPersonsTextView()

        disabledColor = ContextCompat.getColor(view.context, R.color.disabled)
        enabledColor = ContextCompat.getColor(view.context, R.color.black)
        fullConjugations = binding.fullConjugations
        partialConjugations = binding.partialConjugations
        conjugationsSwitch = binding.conjugationsSwitch

        val isFullDefault =  mContext.getIsFullConjugation()
        setFullOrPartialConjugations(isFullDefault)
        conjugationsSwitch.isChecked = isFullDefault

        portugalSwitch = binding.portugalSwitch
        brazil = binding.brazil
        portugal = binding.portugal
        val isPortugal =  mContext.getIsPortugal()
        setPortugalOrBrazil(isPortugal)
        portugalSwitch.isChecked = isPortugal

        conjugationsSwitch.setOnCheckedChangeListener { _, _ ->
            val isChecked = conjugationsSwitch.isChecked
            setFullOrPartialConjugations(isChecked)
            mContext.onSetBooleanPreference(VerbSettingsManager.IS_FULL_CONJUGATION, isChecked)
        }

        portugalSwitch.setOnCheckedChangeListener { _, _ ->
            val isChecked = portugalSwitch.isChecked
            setPortugalOrBrazil(isChecked)
            mContext.onSetBooleanPreference(VerbSettingsManager.IS_PORTUGAL, isChecked)
        }

//        val thirdPersonSingSwitchesEnabled = mContext.getThirdPersonSwitches()

//        thirdPersonSwitches.forEachIndexed { index, switch ->
//            switch.isChecked = thirdPersonSingSwitchesEnabled[index]
//            switch.setOnCheckedChangeListener { _, _ ->
//                mContext.onSetBooleanPreference(thirdPersonKeys[index], switch.isChecked)
//            }
//        }

        val frequencies = mContext.getFrequencies()

        switches.forEachIndexed { index, switch ->
            bars[index].progress = frequencies[index]
            switch.isChecked = frequencies[index] != 0
            switch.setOnCheckedChangeListener { _, _ ->
                val isChecked = switch.isChecked
                val frequency = if (isChecked) 5 else 0
                bars[index].progress = frequency
                mContext.onSetIntPreference(frequencyKeys[index], frequency)
            }
        }
        bars.forEach { bar ->
            bar.setOnSeekBarChangeListener(this)
        }

        timeOption = binding.timeSwitchOption
        countOption = binding.countSwitchOption
        timeOptions = binding.timeOptions
        countOptions = binding.countOptions
        timeOrCountSwitch = binding.timeOrCountSwitch


        val isTimeDefault =  mContext.getIsCountSetting()
        setTimeOrCount(isTimeDefault)
        timeOrCountSwitch.isChecked = isTimeDefault

        val (timePreference, countPreference) = mContext.getTimeAndCountPreference()
        val gridSizePreference = mContext.getGridSizePreference()


        timeOrCountSwitch.setOnCheckedChangeListener { _, _ ->
            val isChecked = timeOrCountSwitch.isChecked
            setTimeOrCount(isChecked)
            mContext.onSetBooleanPreference(VerbSettingsManager.IS_COUNT, isChecked)
        }

        val timeSpinner = binding.timeSpinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.times_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            timeSpinner.adapter = adapter
        }
        timeSpinner.setSelection(timePreference)
        timeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mContext.onSetIntPreference(VerbSettingsManager.TIME_PREFERENCE, position)
            }
        }

        val countSpinner = binding.countSpinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.counts_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            countSpinner.adapter = adapter
        }
        countSpinner.setSelection(countPreference)
        countSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mContext.onSetIntPreference(VerbSettingsManager.COUNT_PREFERENCE, position)
            }
        }

        val gridSizeSpinner = binding.wordsearchSpinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.wordsearch_grid_size_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            gridSizeSpinner.adapter = adapter
        }
        gridSizeSpinner.setSelection(gridSizePreference)
        gridSizeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mContext.onSetIntPreference(VerbSettingsManager.GRID_SIZE_PREFERENCE, position)
            }
        }

        view.findViewById<Button>(R.id.setSimpIndTensesButton).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_title_to_set_simp_ind)
        }
        view.findViewById<Button>(R.id.setCompIndTensesButton).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_title_to_set_comp_ind)
        }
        view.findViewById<Button>(R.id.setProgIndTensesButton).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_title_to_set_prog_ind)
        }
        view.findViewById<Button>(R.id.setSubjTensesButton).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_title_to_set_subj)
        }

        view.findViewById<Button>(R.id.setEnabledThirdPersonsButton).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_title_to_set_persons)
        }

        verbSettingsManager =
            VerbSettingsManager(PreferenceManager.getDefaultSharedPreferences(context))

        return view
    }

    private fun setFullOrPartialConjugations(isFull: Boolean) {
        when (isFull) {
            true -> {
                fullConjugations.setTextColor(enabledColor)
                partialConjugations.setTextColor(disabledColor)
                bars.forEach { bar -> bar.visibility = View.GONE }
            }
            false -> {
                partialConjugations.setTextColor(enabledColor)
                fullConjugations.setTextColor(disabledColor)
                bars.forEach { bar -> bar.visibility = View.VISIBLE }
            }
        }
    }

    private fun setPortugalOrBrazil(isPortugal: Boolean) {
        when (isPortugal) {
            true -> {
                portugal.setTextColor(enabledColor)
                brazil.setTextColor(disabledColor)
            }
            false -> {
                brazil.setTextColor(enabledColor)
                portugal.setTextColor(disabledColor)
            }
        }
    }

    private fun setTimeOrCount(isCount: Boolean) {
        when (isCount) {
            true -> {
                countOption.setTextColor(enabledColor)
                timeOption.setTextColor(disabledColor)
                countOptions.visibility = View.VISIBLE
                timeOptions.visibility = View.GONE
                timeOptions.visibility = View.GONE
            }
            false -> {
                timeOption.setTextColor(enabledColor)
                countOption.setTextColor(disabledColor)
                timeOptions.visibility = View.VISIBLE
                countOptions.visibility = View.GONE
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        switches[bars.indexOf(seekBar)].isChecked = true
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        val index = bars.indexOf(seekBar)
        val progress = seekBar?.progress ?: 0
        switches[index].isChecked = progress != 0
        mContext.onSetIntPreference(frequencyKeys[index], progress)
    }

    fun resetSimpIndTextView() {
        tenseTextBoxes[0].text =
            mContext.getSimpIndTenses().mapIndexed { index, boolean ->
                if (boolean) " \u2022 " + indicatives[index] else null
            }.filterNotNull().joinToString("\n")
    }
    fun resetCompIndTextView() {
        tenseTextBoxes[1].text =
            mContext.getCompIndTenses().mapIndexed { index, boolean ->
                if (boolean) " \u2022 " + perfects[index] else null
            }.filterNotNull().joinToString("\n")
    }
    fun resetProgIndTextView() {
        tenseTextBoxes[2].text =
            mContext.getProgIndTenses().mapIndexed { index, boolean ->
                if (boolean) " \u2022 " + progressives[index] else null
            }.filterNotNull().joinToString("\n")
    }
    fun resetSubjTextView() {
        tenseTextBoxes[3].text =
            mContext.getSubjTenses().mapIndexed { index, boolean ->
                if (boolean) " \u2022 " + subjunctives[index] else null
            }.filterNotNull().joinToString("\n")
    }
    fun resetPersonsTextView() {
        personsTextView.text =
            mContext.getPersons().mapIndexed { index, boolean ->
                if (boolean) " \u2022 " + persons[index] else null
            }.filterNotNull().joinToString("\n")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.explainOptions -> {
                NavHostFragment.findNavController(parentFragmentManager.primaryNavigationFragment!!).navigate(R.id.action_options_to_tutorial)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
