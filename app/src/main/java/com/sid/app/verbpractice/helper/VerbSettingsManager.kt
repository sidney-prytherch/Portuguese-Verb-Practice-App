package com.sid.app.verbpractice.helper

import android.content.SharedPreferences
import android.util.Log


class VerbSettingsManager(private val sharedPreferences: SharedPreferences) {

    fun getBool(preferenceKey: String):Boolean {
        return getBool(preferenceKey, false)
    }

    fun getBool(preferenceKey: String, default: Boolean):Boolean {
        return sharedPreferences.getBoolean(preferenceKey, default)
    }

    fun getInt(preferenceKey: String, defValue: Int):Int {
        return sharedPreferences.getInt(preferenceKey, defValue)
    }

    fun setBool(preferenceKey: String, value: Boolean) {
        sharedPreferences.edit()
            .putBoolean(preferenceKey, value)
            .apply()
    }

    fun setInt(preferenceKey: String, value: Int) {
        sharedPreferences.edit()
            .putInt(preferenceKey, value)
            .apply()
    }


    companion object {
        const val AR_ENABLED = "ar_enabled"
        const val ER_ENABLED = "er_enabled"
        const val IR_ENABLED = "ir_enabled"
        const val IRREG_ENABLED = "irreg_enabled"

        const val PRES_IND = "pres_ind"
        const val PRET_IND = "pret_ind"
        const val IMP_IND = "imp_ind"
        const val SIMP_PLUP_IND = "simp_plup_ind"
        const val SIMP_FUT_IND = "simp_fut_ind"
        const val COND_IND = "cond_ind"

        const val FUT_IND = "fut_ind"
        const val PRES_PERF = "pres_perf"
        const val PLUP = "plup"
        const val FUT_PERF = "fut_perf"
        const val COND_PERF = "cond_perf"

        const val PRES_PROG = "pres_prog"
        const val PRET_PROG = "pret_prog"
        const val IMP_PROG = "imp_prog"
        const val SIMP_PLUP_PROG = "simp_plup_prog"
        const val FUT_PROG = "fut_prog"
        const val COND_PROG = "cond_prog"
        const val PRES_PERF_PROG = "pres_perf_prog"
        const val PLUP_PROG = "plup_prog"
        const val FUT_PERF_PROG = "fut_perf_prog"
        const val COND_PERF_PROG = "cond_perf_prog"

        const val PRES_SUBJ = "pres_subj"
        const val PRES_PERF_SUBJ = "pres_perf_subj"
        const val IMP_SUBJ = "imp_subj"
        const val PLUP_SUBJ = "plup_subj"
        const val FUT_SUBJ = "fut_subj"
        const val FUT_PERF_SUBJ = "fut_perf_subj"

        const val IMP_AFF = "imp_aff"
        const val IMP_NEG = "imp_neg"
        const val PERS_INF = "pers_inf"

        const val VC_ENABLED = "vc_enabled"
        const val ELE_ELA_ENABLED = "ele_ela_enabled"
        const val SENHOR_ENABLED = "senhor_enabled"
        const val VCS_ENABLED = "vcs_enabled"
        const val ELES_ELAS_ENABLED = "eles_elas_enabled"
        const val SENHORES_ENABLED = "senhores_enabled"

        const val IS_FULL_CONJUGATION = "is_full_conjugation"
        const val IS_COUNT = "is_count"
        const val TIME_PREFERENCE = "time_preference"
        const val COUNT_PREFERENCE = "count_preference"

        const val IS_PORTUGAL = "is_portugal"

        const val EU_FREQUENCY = "eu_frequency"
        const val TU_FREQUENCY = "tu_frequency"
        const val VC_ELE_ELA_FREQUENCY = "vc_ele_ela_frequency"
        const val NOS_FREQUENCY = "nos_frequency"
        const val VOS_FREQUENCY = "vos_frequency"
        const val VCS_ELES_ELAS_FREQUENCY = "vcs_eles_elas_frequency"

        const val PREV_YEAR = "prev_year"
        const val PREV_DAY = "prev_day"
        const val USED_COUNT = "used_count"

        const val VERB_FREQUENCY = "verb_frequency"
        const val SHOW_SELECTED = "show_selected"
        const val SHOW_UNSELECTED = "show_unselected"
    }
}