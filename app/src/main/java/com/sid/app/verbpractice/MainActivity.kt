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

package com.sid.app.verbpractice

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sid.app.verbpractice.db.entity.PortugueseVerb
import com.sid.app.verbpractice.enums.VerbForm
import com.sid.app.verbpractice.helper.VerbSettingsManager
import com.sid.app.verbpractice.ui.dictionary.WordFilterFragment
import com.sid.app.verbpractice.ui.options.SetCompIndTensesFragment
import com.sid.app.verbpractice.ui.options.SetProgIndTensesFragment
import com.sid.app.verbpractice.ui.options.SetSimpIndTensesFragment
import com.sid.app.verbpractice.ui.options.SetSubjTensesFragment
import com.sid.app.verbpractice.viewmodel.PortugueseVerbViewModel
import kotlinx.android.synthetic.main.activity_main.*

/**
 * An activity that inflates a layout that has a [BottomNavigationView].
 */
class MainActivity : AppCompatActivity(),
    SetSimpIndTensesFragment.SetAndGetIndicativeSettings,
    SetCompIndTensesFragment.SetAndGetPerfectSettings,
    SetProgIndTensesFragment.SetAndGetProgressiveSettings,
    SetSubjTensesFragment.SetAndGetSubjunctiveSettings,
    WordFilterFragment.SetAndGetFilterSettings {

    private lateinit var mWordViewModel: PortugueseVerbViewModel
    private lateinit var verbSettingsManager: VerbSettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
        verbSettingsManager = VerbSettingsManager(PreferenceManager.getDefaultSharedPreferences(this))

        mWordViewModel = ViewModelProvider(this).get(PortugueseVerbViewModel::class.java)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            super.onRestoreInstanceState(savedInstanceState)
        }
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        val navController = Navigation.findNavController(this, R.id.nav_host_container)
        setupWithNavController(bottom_nav, navController)
        setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.nav_host_container).navigateUp()//currentNavController?.value?.navigateUp() ?: false
    }

    fun setNavBarToPractice() {
        bottom_nav.menu.findItem(R.id.practiceStart).isChecked = true
    }

    override fun onSetSimpIndTenses(dialog: DialogFragment, result: BooleanArray) {
        verbSettingsManager.setBool(VerbSettingsManager.PRES_IND, result[0])
        verbSettingsManager.setBool(VerbSettingsManager.PRET_IND, result[1])
        verbSettingsManager.setBool(VerbSettingsManager.IMP_IND, result[2])
        verbSettingsManager.setBool(VerbSettingsManager.SIMP_PLUP_IND, result[3])
        verbSettingsManager.setBool(VerbSettingsManager.SIMP_FUT_IND, result[4])
        verbSettingsManager.setBool(VerbSettingsManager.COND_IND, result[5])
    }

    override fun getSimpIndTenses(): BooleanArray {
        return booleanArrayOf(
            verbSettingsManager.getBool(VerbSettingsManager.PRES_IND),
            verbSettingsManager.getBool(VerbSettingsManager.PRET_IND),
            verbSettingsManager.getBool(VerbSettingsManager.IMP_IND),
            verbSettingsManager.getBool(VerbSettingsManager.SIMP_PLUP_IND),
            verbSettingsManager.getBool(VerbSettingsManager.SIMP_FUT_IND),
            verbSettingsManager.getBool(VerbSettingsManager.COND_IND)
        )
    }

    override fun onSetCompIndTenses(dialog: DialogFragment, result: BooleanArray) {
        verbSettingsManager.setBool(VerbSettingsManager.FUT_IND, result[0])
        verbSettingsManager.setBool(VerbSettingsManager.PRES_PERF, result[1])
        verbSettingsManager.setBool(VerbSettingsManager.PLUP, result[2])
        verbSettingsManager.setBool(VerbSettingsManager.FUT_PERF, result[3])
        verbSettingsManager.setBool(VerbSettingsManager.COND_PERF, result[4])
        verbSettingsManager.setBool(VerbSettingsManager.PAST_INTENT, result[5])
    }

    override fun getCompIndTenses(): BooleanArray {
        return booleanArrayOf(
            verbSettingsManager.getBool(VerbSettingsManager.FUT_IND),
            verbSettingsManager.getBool(VerbSettingsManager.PRES_PERF),
            verbSettingsManager.getBool(VerbSettingsManager.PLUP),
            verbSettingsManager.getBool(VerbSettingsManager.FUT_PERF),
            verbSettingsManager.getBool(VerbSettingsManager.COND_PERF),
            verbSettingsManager.getBool(VerbSettingsManager.PAST_INTENT)
        )
    }

    override fun onSetProgIndTenses(dialog: DialogFragment, result: BooleanArray) {
        verbSettingsManager.setBool(VerbSettingsManager.PRES_PROG, result[0])
        verbSettingsManager.setBool(VerbSettingsManager.PRET_PROG, result[1])
        verbSettingsManager.setBool(VerbSettingsManager.IMP_PROG, result[2])
        verbSettingsManager.setBool(VerbSettingsManager.SIMP_PLUP_PROG, result[3])
        verbSettingsManager.setBool(VerbSettingsManager.FUT_PROG, result[4])
        verbSettingsManager.setBool(VerbSettingsManager.COND_PROG, result[5])
        verbSettingsManager.setBool(VerbSettingsManager.PRES_PERF_PROG, result[6])
        verbSettingsManager.setBool(VerbSettingsManager.PLUP_PROG, result[7])
        verbSettingsManager.setBool(VerbSettingsManager.FUT_PERF_PROG, result[8])
        verbSettingsManager.setBool(VerbSettingsManager.COND_PERF_PROG, result[9])
    }

    override fun getProgIndTenses(): BooleanArray {
        return booleanArrayOf(
            verbSettingsManager.getBool(VerbSettingsManager.PRES_PROG) ,
            verbSettingsManager.getBool(VerbSettingsManager.PRET_PROG),
            verbSettingsManager.getBool(VerbSettingsManager.IMP_PROG),
            verbSettingsManager.getBool(VerbSettingsManager.SIMP_PLUP_PROG),
            verbSettingsManager.getBool(VerbSettingsManager.FUT_PROG),
            verbSettingsManager.getBool(VerbSettingsManager.COND_PROG),
            verbSettingsManager.getBool(VerbSettingsManager.PRES_PERF_PROG),
            verbSettingsManager.getBool(VerbSettingsManager.PLUP_PROG),
            verbSettingsManager.getBool(VerbSettingsManager.FUT_PERF_PROG),
            verbSettingsManager.getBool(VerbSettingsManager.COND_PERF_PROG)
        )
    }

    override fun onSetSubjTenses(dialog: DialogFragment, result: BooleanArray) {
        verbSettingsManager.setBool(VerbSettingsManager.PRES_SUBJ, result[0])
        verbSettingsManager.setBool(VerbSettingsManager.PRES_PERF_SUBJ, result[1])
        verbSettingsManager.setBool(VerbSettingsManager.IMP_SUBJ, result[2])
        verbSettingsManager.setBool(VerbSettingsManager.PLUP_SUBJ, result[3])
        verbSettingsManager.setBool(VerbSettingsManager.FUT_SUBJ, result[4])
        verbSettingsManager.setBool(VerbSettingsManager.FUT_PERF_SUBJ, result[5])
    }

    override fun getSubjTenses(): BooleanArray {
        return booleanArrayOf(
            verbSettingsManager.getBool(VerbSettingsManager.PRES_SUBJ),
            verbSettingsManager.getBool(VerbSettingsManager.PRES_PERF_SUBJ),
            verbSettingsManager.getBool(VerbSettingsManager.IMP_SUBJ),
            verbSettingsManager.getBool(VerbSettingsManager.PLUP_SUBJ),
            verbSettingsManager.getBool(VerbSettingsManager.FUT_SUBJ),
            verbSettingsManager.getBool(VerbSettingsManager.FUT_PERF_SUBJ)
        )
    }

    fun getLastUsedDate(): Array<Int> {
        return arrayOf(
            verbSettingsManager.getInt(VerbSettingsManager.PREV_YEAR, 0),
            verbSettingsManager.getInt(VerbSettingsManager.PREV_DAY, 0)
        )
    }

    fun setLastUsedDate(previousYear: Int, previousDay: Int) {
        verbSettingsManager.setInt(VerbSettingsManager.PREV_YEAR, previousYear)
        verbSettingsManager.setInt(VerbSettingsManager.PREV_DAY, previousDay)
    }

    private fun getTimesUsedToday(): Int {
        return verbSettingsManager.getInt(VerbSettingsManager.USED_COUNT, 0)
    }

    fun resetTimesUsedToday() {
        verbSettingsManager.setInt(VerbSettingsManager.USED_COUNT, 1)
    }

    fun increaseTimesUsedToday() {
        verbSettingsManager.setInt(VerbSettingsManager.USED_COUNT, getTimesUsedToday() + 1)
    }

    fun getAllTenses(): Array<VerbForm> {
        return arrayOf(
            checkTense(VerbSettingsManager.PRES_IND, VerbForm.PRES_IND),
            checkTense(VerbSettingsManager.PRET_IND, VerbForm.PRET_IND),
            checkTense(VerbSettingsManager.IMP_IND, VerbForm.IMP_IND),
            checkTense(VerbSettingsManager.SIMP_PLUP_IND, VerbForm.SIMP_PLUP_IND),
            checkTense(VerbSettingsManager.SIMP_FUT_IND, VerbForm.SIMP_FUT_IND),
            checkTense(VerbSettingsManager.COND_IND, VerbForm.COND_IND),
            checkTense(VerbSettingsManager.FUT_IND, VerbForm.FUT_IND),
            checkTense(VerbSettingsManager.PRES_PERF, VerbForm.PRES_PERF),
            checkTense(VerbSettingsManager.PLUP, VerbForm.PLUP),
            checkTense(VerbSettingsManager.FUT_PERF, VerbForm.FUT_PERF),
            checkTense(VerbSettingsManager.COND_PERF, VerbForm.COND_PERF),
            checkTense(VerbSettingsManager.PAST_INTENT, VerbForm.PAST_INTENT),
            checkTense(VerbSettingsManager.PRES_PROG, VerbForm.PRES_PROG),
            checkTense(VerbSettingsManager.PRET_PROG, VerbForm.PRET_PROG),
            checkTense(VerbSettingsManager.IMP_PROG, VerbForm.IMP_PROG),
            checkTense(VerbSettingsManager.SIMP_PLUP_PROG, VerbForm.SIMP_PLUP_PROG),
            checkTense(VerbSettingsManager.FUT_PROG, VerbForm.FUT_PROG),
            checkTense(VerbSettingsManager.COND_PROG, VerbForm.COND_PROG),
            checkTense(VerbSettingsManager.PRES_PERF_PROG, VerbForm.PRES_PERF_PROG),
            checkTense(VerbSettingsManager.PLUP_PROG, VerbForm.PLUP_PROG),
            checkTense(VerbSettingsManager.FUT_PERF_PROG, VerbForm.FUT_PERF_PROG),
            checkTense(VerbSettingsManager.COND_PERF_PROG, VerbForm.COND_PERF_PROG),
            checkTense(VerbSettingsManager.PRES_SUBJ, VerbForm.PRES_SUBJ),
            checkTense(VerbSettingsManager.PRES_PERF_SUBJ, VerbForm.PRES_PERF_SUBJ),
            checkTense(VerbSettingsManager.IMP_SUBJ, VerbForm.IMP_SUBJ),
            checkTense(VerbSettingsManager.PLUP_SUBJ, VerbForm.PLUP_SUBJ),
            checkTense(VerbSettingsManager.FUT_SUBJ, VerbForm.FUT_SUBJ),
            checkTense(VerbSettingsManager.FUT_PERF_SUBJ, VerbForm.FUT_PERF_SUBJ)
        ).filterNotNull().toTypedArray()
    }

    private fun checkTense(tense: String, verbForm: VerbForm): VerbForm? {
        return if (verbSettingsManager.getBool(tense)) verbForm else null
    }

    suspend fun updateCheckedInDb(verbId: Int, added: Int) {
        mWordViewModel.updateChecked(added, verbId)
    }

    suspend fun updateCheckedInDbForDefinition(definitionId: Int, added: Int) {
        mWordViewModel.updateCheckedForDefinition(added, definitionId)
    }

    suspend fun resetAllChecked() {
        mWordViewModel.resetAllChecked()
    }

    suspend fun getRandomVerb(): PortugueseVerb? {
        return mWordViewModel.getRandomVerb()
    }

    fun onSetBooleanPreference(key: String, result: Boolean) {
        verbSettingsManager.setBool(key, result)
    }

    suspend fun getRandomVerbs(): List<PortugueseVerb>? {
        return mWordViewModel.getRandomVerbs()
    }

    suspend fun getSpecificVerb(verb: String): List<PortugueseVerb>? {
        return mWordViewModel.getSpecificVerb(verb)
    }

    fun getVerbTypes(): BooleanArray {
        return booleanArrayOf(
            verbSettingsManager.getBool(VerbSettingsManager.AR_ENABLED),
            verbSettingsManager.getBool(VerbSettingsManager.ER_ENABLED),
            verbSettingsManager.getBool(VerbSettingsManager.IR_ENABLED),
            verbSettingsManager.getBool(VerbSettingsManager.IRREG_ENABLED)
        )
    }

    fun getThirdPersonSwitches(): BooleanArray {
        return booleanArrayOf(
            verbSettingsManager.getBool(VerbSettingsManager.VC_ENABLED, true),
            verbSettingsManager.getBool(VerbSettingsManager.ELE_ELA_ENABLED, true),
            verbSettingsManager.getBool(VerbSettingsManager.SENHOR_ENABLED, true),
            verbSettingsManager.getBool(VerbSettingsManager.VCS_ENABLED, true),
            verbSettingsManager.getBool(VerbSettingsManager.ELES_ELAS_ENABLED, true),
            verbSettingsManager.getBool(VerbSettingsManager.SENHORES_ENABLED, true)
        )
    }

    fun getTimeAndCountPreference(): IntArray {
        return intArrayOf(
            verbSettingsManager.getInt(VerbSettingsManager.TIME_PREFERENCE, 2),
            verbSettingsManager.getInt(VerbSettingsManager.COUNT_PREFERENCE, 2)
        )
    }

    fun onSetIntPreference(key: String, result: Int) {
        verbSettingsManager.setInt(key, result)
    }

    fun getIsFullConjugation(): Boolean {
        return verbSettingsManager.getBool(VerbSettingsManager.IS_FULL_CONJUGATION)
    }

    fun getIsPortugal(): Boolean {
        return verbSettingsManager.getBool(VerbSettingsManager.IS_PORTUGAL)
    }

    fun getIsCountSetting(): Boolean {
        return verbSettingsManager.getBool(VerbSettingsManager.IS_COUNT)
    }

    fun getFrequencies(): IntArray {
        return intArrayOf(
            verbSettingsManager.getInt(VerbSettingsManager.EU_FREQUENCY, 5),
            verbSettingsManager.getInt(VerbSettingsManager.TU_FREQUENCY, 5),
            verbSettingsManager.getInt(VerbSettingsManager.VC_ELE_ELA_FREQUENCY, 5),
            verbSettingsManager.getInt(VerbSettingsManager.NOS_FREQUENCY, 5),
            verbSettingsManager.getInt(VerbSettingsManager.VOS_FREQUENCY, 0),
            verbSettingsManager.getInt(VerbSettingsManager.VCS_ELES_ELAS_FREQUENCY, 5)
        )
    }

    fun updateWidget() {
        val intent = Intent(this, NewAppWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids: IntArray = AppWidgetManager.getInstance(application).getAppWidgetIds(ComponentName(application, NewAppWidget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }

    override fun onSetFilterSettings(result: BooleanArray) {
        verbSettingsManager.setBool(VerbSettingsManager.SHOW_SELECTED, result[0])
        verbSettingsManager.setBool(VerbSettingsManager.SHOW_UNSELECTED, result[1])
    }

    override fun getFilterSettings(): BooleanArray {
        return booleanArrayOf(
            verbSettingsManager.getBool(VerbSettingsManager.SHOW_SELECTED, true),
            verbSettingsManager.getBool(VerbSettingsManager.SHOW_UNSELECTED, true)
        )
    }

    override fun onSetCommonVerbValue(result: Int) {
        verbSettingsManager.setInt(VerbSettingsManager.VERB_FREQUENCY, result)
    }

    override fun getCommonVerbValue(): Int {
        return verbSettingsManager.getInt(VerbSettingsManager.VERB_FREQUENCY, 1)
    }
}
