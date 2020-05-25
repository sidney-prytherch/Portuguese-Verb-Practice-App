package com.sid.app.verbpractice.helper

import android.content.res.Resources
import com.sid.app.verbpractice.R
import com.sid.app.verbpractice.enums.Person
import com.sid.app.verbpractice.enums.VerbClass
import com.sid.app.verbpractice.enums.VerbForm
import java.util.*
import kotlin.collections.HashMap


object ConjugatorPortuguese {
    private val subjects = arrayOf(
        arrayOf(arrayOf("Eu")),
        arrayOf(arrayOf("Tu")),
        arrayOf(arrayOf("Você"), arrayOf("Ele", "Ela"), arrayOf("O senhor", "A senhora")),
        arrayOf(arrayOf("Nós")),
        arrayOf(arrayOf("Vós")),
        arrayOf(arrayOf("Vocês"), arrayOf("Eles", "Elas"), arrayOf("Os senhores", "As senhoras")))

    fun getVerbFormString(verbForm: VerbForm, resources: Resources): String {
        return when (verbForm) {
            VerbForm.PRES_IND -> resources.getString(R.string.present)
            VerbForm.PRET_IND -> resources.getString(R.string.preterite)
            VerbForm.IMP_IND -> resources.getString(R.string.imperfect)
            VerbForm.SIMP_PLUP_IND -> resources.getString(R.string.simple_pluperfect)
            VerbForm.SIMP_FUT_IND -> resources.getString(R.string.simple_future)
            VerbForm.COND_IND -> resources.getString(R.string.conditional)

            VerbForm.FUT_IND -> "${resources.getString(R.string.future)} (compound)"
            VerbForm.PRES_PERF -> "${resources.getString(R.string.present_perfect)} (compound)"
            VerbForm.PLUP -> "${resources.getString(R.string.pluperfect)} (compound)"
            VerbForm.FUT_PERF -> "${resources.getString(R.string.future_perfect)} (compound)"
            VerbForm.COND_PERF -> "${resources.getString(R.string.conditional_perfect)} (compound)"

            VerbForm.PRES_PROG -> resources.getString(R.string.present_progressive)
            VerbForm.PRET_PROG -> resources.getString(R.string.preterite_progressive)
            VerbForm.IMP_PROG -> resources.getString(R.string.imperfect_progressive)
            VerbForm.SIMP_PLUP_PROG -> resources.getString(R.string.simple_pluperfect_progressive)
            VerbForm.FUT_PROG -> resources.getString(R.string.future_progressive)
            VerbForm.COND_PROG -> resources.getString(R.string.conditional_progressive)
            VerbForm.PRES_PERF_PROG -> resources.getString(R.string.present_perfect_progressive)
            VerbForm.PLUP_PROG -> resources.getString(R.string.pluperfect_progressive)
            VerbForm.FUT_PERF_PROG -> resources.getString(R.string.future_perfect_progressive)
            VerbForm.COND_PERF_PROG -> resources.getString(R.string.conditional_perfect_progressive)

            VerbForm.PRES_SUBJ -> resources.getString(R.string.present_subjunctive)
            VerbForm.PRES_PERF_SUBJ -> resources.getString(R.string.present_perfect_subjunctive)
            VerbForm.IMP_SUBJ -> resources.getString(R.string.imperfect_subjunctive)
            VerbForm.PLUP_SUBJ -> resources.getString(R.string.pluperfect_subjunctive)
            VerbForm.FUT_SUBJ -> resources.getString(R.string.future_subjunctive)
            VerbForm.FUT_PERF_SUBJ -> resources.getString(R.string.future_perfect_subjunctive)

            VerbForm.IMP_AFF -> resources.getString(R.string.imperative_affirmative)
            VerbForm.IMP_NEG -> resources.getString(R.string.imperative_negative)
            VerbForm.PERS_INF -> resources.getString(R.string.personal_infinitive)
        }
    }

    fun getSubject(index: Int, enabledThirdPersons: BooleanArray): String {
        val enabledThirdSing = enabledThirdPersons.filterIndexed { i, _ -> i < 3 }.toBooleanArray()
        val enabledThirdPlur = enabledThirdPersons.filterIndexed { i, _ -> i > 2 }.toBooleanArray()
        return if (index == 2) {
            val enabledThird = if (enabledThirdSing.indexOf(true) == -1) booleanArrayOf(false, true, false) else enabledThirdSing
            (subjects[index].filterIndexed { i, _ -> enabledThird[i] }.random()).random()
        } else if (index == 5) {
            val enabledThird = if (enabledThirdPlur.indexOf(true) == -1) booleanArrayOf(false, true, false) else enabledThirdPlur
            (subjects[index].filterIndexed { i, _ -> enabledThird[i] }.random()).random()
        } else {
            subjects[index][0][0]
        }
    }

    fun getSubject(person: Person, enabledThirdPersons: BooleanArray): String {
        return getSubject(
            when (person) {
                Person.FIRST_SING -> 0
                Person.SECOND_SING -> 1
                Person.THIRD_SING -> 2
                Person.FIRST_PLUR -> 3
                Person.SECOND_PLUR -> 4
                Person.THIRD_PLUR -> 5
            }, enabledThirdPersons
        )
    }

    private fun getEIStem(infinitive: String, verbLength: Int, stem: String): String {
        if (verbLength > 2) {
            when (infinitive.substring(verbLength - 3, verbLength)) {
                "car" -> return infinitive.substring(0, verbLength - 3) + "qu"
                "çar" -> return infinitive.substring(0, verbLength - 3) + "c"
                "gar" -> return infinitive.substring(0, verbLength - 3) + "gu"
            }
        }
        return stem
    }

    private fun getAOStem(infinitive: String, verbLength: Int, stem: String): String {
        if (verbLength > 2) {
            when (infinitive.substring(verbLength - 3, verbLength)) {
                "cer", "cir" -> return infinitive.substring(0, verbLength - 3) + "ç"
                "ger", "gir" -> return infinitive.substring(0, verbLength - 3) + "j"
            }
            if (verbLength > 3) {
                when (infinitive.substring(verbLength - 4, verbLength)) {
                    "guir", "guer" -> return infinitive.substring(0, verbLength - 3)
                }
            }
        }
        return stem
    }

    private fun matches(infinitive: String, verbLength: Int, match: String): Boolean {
        val matchLength = match.length
        return verbLength >= matchLength && infinitive.substring(verbLength - matchLength, verbLength) == match
    }

    private fun getVerbClassAndStems(infinitive: String, verbLength: Int): VerbData {
        val verbClass: VerbClass
        val stem: String
        var aAndOStem: String? = null
        var eAndIStem: String? = null
        val presIndStem: String
        val impIndStem: String
        val pretIndStem: String
        val impAffStem: String
        val plupIndStem: String
        val impSubjStem: String
        val futIndStem: String?
        val condIndStem: String?
        val persInfStem: String
        val presSubjStem: String
        val impNegStem: String
        val futSubjStem: String
        var participle: String? = null
        var gerund: String? = null
        val ending = infinitive.substring(verbLength - 2, verbLength)
        when {
            infinitive.matches(Regex("ter|abster|conter|suster|ater|deter|entreter|manter|obter|reter")) -> {
                verbClass = VerbClass.TER
                stem = infinitive.substring(0, verbLength - 3)
                impAffStem = stem + "t"
                pretIndStem = impAffStem
                impIndStem = pretIndStem
                presIndStem = impIndStem
                impSubjStem = stem + "tiv"
                plupIndStem = impSubjStem
                persInfStem = stem + "ter"
                condIndStem = persInfStem
                futIndStem = condIndStem
                impNegStem = stem + "tenha"
                presSubjStem = impNegStem
                futSubjStem = stem + "tiver"
            }
            infinitive.matches(Regex("delir")) -> {
                verbClass = VerbClass.DELIR
                stem = ""
                impAffStem = "del"
                impSubjStem = impAffStem
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                presIndStem = impIndStem
                pretIndStem = "deli"
                persInfStem = "delir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = "-"
                presSubjStem = impNegStem
            }
            infinitive.matches(Regex("vestir")) -> {
                verbClass = VerbClass.VESTIR
                stem = infinitive.substring(0, verbLength - 6)
                impAffStem = stem + "v"
                presIndStem = impAffStem
                impSubjStem = stem + "vest"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = stem + "vesti"
                persInfStem = stem + "vestir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = stem + "vista"
                presSubjStem = impNegStem
            }
            infinitive.matches(Regex("caber")) -> {
                verbClass = VerbClass.CABER
                stem = ""
                impAffStem = "ca"
                presIndStem = impAffStem
                impIndStem = "cab"
                pretIndStem = "coube"
                impSubjStem = "coub"
                plupIndStem = impSubjStem
                persInfStem = "caber"
                condIndStem = persInfStem
                futIndStem = condIndStem
                impNegStem = "caiba"
                presSubjStem = impNegStem
                futSubjStem = "couber"
            }
            infinitive.matches(Regex("estar")) -> {
                verbClass = VerbClass.ESTAR
                stem = ""
                impAffStem = "est"
                pretIndStem = impAffStem
                impIndStem = pretIndStem
                presIndStem = impIndStem
                impSubjStem = "estiv"
                plupIndStem = impSubjStem
                persInfStem = "estar"
                condIndStem = persInfStem
                futIndStem = condIndStem
                impNegStem = "esteja"
                presSubjStem = impNegStem
                futSubjStem = "estiver"
            }
            infinitive.matches(Regex("gear")) -> {
                verbClass = VerbClass.GEAR
                stem = ""
                presIndStem = "geia"
                impIndStem = "geava"
                pretIndStem = "geou"
                plupIndStem = "geara"
                futIndStem = "geará"
                condIndStem = "gearia"
                impNegStem = "geie"
                impAffStem = impNegStem
                presSubjStem = impAffStem
                impSubjStem = "geasse"
                persInfStem = "gear"
                futSubjStem = persInfStem
            }
            infinitive.matches(Regex("haver")) -> {
                verbClass = VerbClass.HAVER
                stem = ""
                impAffStem = "h"
                presIndStem = impAffStem
                impIndStem = "hav"
                pretIndStem = "houve"
                impSubjStem = "houv"
                plupIndStem = impSubjStem
                persInfStem = "haver"
                condIndStem = persInfStem
                futIndStem = condIndStem
                impNegStem = "haja"
                presSubjStem = impNegStem
                futSubjStem = "houver"
            }
            infinitive.matches(Regex("ir")) -> {
                verbClass = VerbClass.IR
                stem = ""
                impAffStem = ""
                impIndStem = impAffStem
                presIndStem = impIndStem
                impSubjStem = "f"
                plupIndStem = impSubjStem
                pretIndStem = plupIndStem
                persInfStem = "ir"
                condIndStem = persInfStem
                futIndStem = condIndStem
                impNegStem = "v"
                presSubjStem = impNegStem
                futSubjStem = "for"
            }
            infinitive.matches(Regex("nevar")) -> {
                verbClass = VerbClass.NEVAR
                stem = ""
                presIndStem = "neva"
                impIndStem = "nevava"
                pretIndStem = "nevou"
                plupIndStem = "nevara"
                futIndStem = "nevará"
                condIndStem = "nevaria"
                impNegStem = "neve"
                impAffStem = impNegStem
                presSubjStem = impAffStem
                impSubjStem = "nevasse"
                persInfStem = "nevar"
                futSubjStem = persInfStem
            }
            infinitive.matches(Regex("ouvir")) -> {
                verbClass = VerbClass.OUVIR
                stem = ""
                presIndStem = "o"
                impSubjStem = "ouv"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = "ouvi"
                persInfStem = "ouvir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = "ouça"
                presSubjStem = impNegStem
                impAffStem = "ou"
            }
            infinitive.matches(Regex("perder")) -> {
                verbClass = VerbClass.PERDER
                stem = ""
                impAffStem = "per"
                presIndStem = impAffStem
                impSubjStem = "perd"
                plupIndStem = impSubjStem
                pretIndStem = plupIndStem
                impIndStem = pretIndStem
                persInfStem = "perder"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = "perca"
                presSubjStem = impNegStem
            }
            infinitive.matches(Regex("poder")) -> {
                verbClass = VerbClass.PODER
                stem = ""
                impAffStem = "po"
                presIndStem = impAffStem
                impIndStem = "pod"
                pretIndStem = "p"
                impSubjStem = "pud"
                plupIndStem = impSubjStem
                persInfStem = "poder"
                condIndStem = persInfStem
                futIndStem = condIndStem
                impNegStem = "possa"
                presSubjStem = impNegStem
                futSubjStem = "puder"
            }
            infinitive.matches(Regex("pôr")) -> {
                verbClass = VerbClass.PÔR
                stem = ""
                gerund = "pondo"
                participle = "posto"
                persInfStem = "p"
                impAffStem = persInfStem
                pretIndStem = impAffStem
                impIndStem = pretIndStem
                presIndStem = impIndStem
                impSubjStem = "pus"
                plupIndStem = impSubjStem
                condIndStem = "por"
                futIndStem = condIndStem
                impNegStem = "ponha"
                presSubjStem = impNegStem
                futSubjStem = "puser"
            }
            infinitive.matches(Regex("prover")) -> {
                verbClass = VerbClass.PROVER
                stem = ""
                impAffStem = "prov"
                impSubjStem = impAffStem
                plupIndStem = impSubjStem
                pretIndStem = plupIndStem
                impIndStem = pretIndStem
                presIndStem = impIndStem
                persInfStem = "prover"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = "proveja"
                presSubjStem = impNegStem
            }
            infinitive.matches(Regex("saber")) -> {
                verbClass = VerbClass.SABER
                stem = ""
                presIndStem = "s"
                impIndStem = "sab"
                pretIndStem = "soube"
                impSubjStem = "soub"
                plupIndStem = impSubjStem
                persInfStem = "saber"
                condIndStem = persInfStem
                futIndStem = condIndStem
                impNegStem = "saiba"
                presSubjStem = impNegStem
                futSubjStem = "souber"
                impAffStem = "sa"
            }
            infinitive.matches(Regex("ser")) -> {
                verbClass = VerbClass.SER
                stem = ""
                impIndStem = ""
                presIndStem = impIndStem
                impSubjStem = "f"
                plupIndStem = impSubjStem
                pretIndStem = plupIndStem
                persInfStem = "ser"
                condIndStem = persInfStem
                futIndStem = condIndStem
                impNegStem = "seja"
                presSubjStem = impNegStem
                futSubjStem = "for"
                impAffStem = "s"
            }
            infinitive.matches(Regex("tossir")) -> {
                verbClass = VerbClass.TOSSIR
                stem = ""
                impAffStem = "t"
                presIndStem = impAffStem
                impSubjStem = "toss"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = "tossi"
                persInfStem = "tossir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = "tussa"
                presSubjStem = impNegStem
            }
            infinitive.matches(Regex("trazer")) -> {
                verbClass = VerbClass.TRAZER
                stem = ""
                impAffStem = "tra"
                presIndStem = impAffStem
                impIndStem = "traz"
                pretIndStem = "trouxe"
                impSubjStem = "troux"
                plupIndStem = impSubjStem
                condIndStem = "trar"
                futIndStem = condIndStem
                impNegStem = "traga"
                presSubjStem = impNegStem
                futSubjStem = "trouxer"
                persInfStem = "trazer"
            }
            infinitive.matches(Regex("engolir")) -> {
                verbClass = VerbClass.ENGOLIR
                stem = ""
                impAffStem = "eng"
                presIndStem = impAffStem
                impSubjStem = "engol"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = "engoli"
                persInfStem = "engolir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = "engula"
                presSubjStem = impNegStem
            }
            infinitive.matches(Regex("fugir")) -> {
                verbClass = VerbClass.FUGIR
                stem = ""
                impAffStem = "f"
                presIndStem = impAffStem
                impSubjStem = "fug"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = "fugi"
                persInfStem = "fugir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = "fuja"
                presSubjStem = impNegStem
            }
            infinitive.matches(Regex("divertir")) -> {
                verbClass = VerbClass.DIVERTIR
                stem = ""
                impAffStem = "div"
                presIndStem = impAffStem
                impSubjStem = "divert"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = "diverti"
                persInfStem = "divertir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = "divirta"
                presSubjStem = impNegStem
            }
            infinitive.matches(Regex("servir")) -> {
                verbClass = VerbClass.SERVIR
                stem = ""
                impAffStem = "s"
                presIndStem = impAffStem
                impSubjStem = "serv"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = "servi"
                persInfStem = "servir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = "sirva"
                presSubjStem = impNegStem
            }
            infinitive.matches(Regex("saudar")) -> {
                verbClass = VerbClass.SAUDAR
                stem = ""
                impNegStem = "sa"
                impAffStem = impNegStem
                presSubjStem = impAffStem
                presIndStem = presSubjStem
                impSubjStem = "saud"
                plupIndStem = impSubjStem
                pretIndStem = plupIndStem
                impIndStem = pretIndStem
                persInfStem = "saudar"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
            }
            infinitive.matches(Regex("ganir")) -> {
                verbClass = VerbClass.GANIR
                stem = ""
                impSubjStem = "gan"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                presIndStem = impIndStem
                pretIndStem = "gani"
                persInfStem = "ganir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = "gana"
                presSubjStem = impNegStem
                impAffStem = ""
            }
            infinitive.matches(Regex("reunir")) -> {
                verbClass = VerbClass.REUNIR
                stem = ""
                impAffStem = "re"
                presIndStem = impAffStem
                impSubjStem = "reun"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = "reuni"
                persInfStem = "reunir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = "reúna"
                presSubjStem = impNegStem
            }
            infinitive.matches(Regex("remir")) -> {
                verbClass = VerbClass.REMIR
                stem = ""
                presIndStem = "re"
                impSubjStem = "rem"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = "remi"
                persInfStem = "remir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = "redima"
                presSubjStem = impNegStem
                impAffStem = ""
            }
            infinitive.matches(Regex("dormir")) -> {
                verbClass = VerbClass.DORMIR
                stem = ""
                impAffStem = "d"
                presIndStem = impAffStem
                impSubjStem = "dorm"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = "dormi"
                persInfStem = "dormir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = "durma"
                presSubjStem = impNegStem
            }
            infinitive.matches(Regex("barrir")) -> {
                verbClass = VerbClass.BARRIR
                stem = ""
                impSubjStem = "barr"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                presIndStem = impIndStem
                pretIndStem = "barri"
                persInfStem = "barrir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = "barra"
                presSubjStem = impNegStem
                impAffStem = ""
            }
            infinitive.matches(Regex("despir")) -> {
                verbClass = VerbClass.DESPIR
                stem = ""
                impAffStem = "desp"
                impSubjStem = impAffStem
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                presIndStem = impIndStem
                pretIndStem = "despi"
                persInfStem = "despir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = "-"
                presSubjStem = impNegStem
            }
            infinitive.matches(Regex("doer")) -> {
                verbClass = VerbClass.DOER
                stem = ""
                gerund = "doendo"
                participle = "doído"
                presIndStem = "d"
                impIndStem = "doí"
                impSubjStem = "do"
                plupIndStem = impSubjStem
                pretIndStem = plupIndStem
                persInfStem = "doer"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = "doa"
                impAffStem = impNegStem
                presSubjStem = impAffStem
            }
            infinitive.matches(Regex("falir")) -> {
                verbClass = VerbClass.FALIR
                stem = ""
                impAffStem = "fali"
                pretIndStem = impAffStem
                presIndStem = pretIndStem
                impSubjStem = "fal"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                persInfStem = "falir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = "-"
                presSubjStem = impNegStem
            }
            infinitive.matches(Regex("explodir")) -> {
                verbClass = VerbClass.ODIR
                stem = ""
                impSubjStem = "explod"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                presIndStem = impIndStem
                pretIndStem = "explodi"
                persInfStem = "explodir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = "exploda"
                presSubjStem = impNegStem
                impAffStem = ""
            }
            infinitive.matches(Regex("latir")) -> {
                verbClass = VerbClass.ATIR
                stem = ""
                impSubjStem = "lat"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                presIndStem = impIndStem
                pretIndStem = "lati"
                persInfStem = "latir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = "lata"
                presSubjStem = impNegStem
                impAffStem = ""
            }
            infinitive.matches(Regex("vir|devir|advir|avir|convir|desavir|intervir|provir|sobrevir")) -> {
                verbClass = VerbClass.VIR
                stem = infinitive.substring(0, verbLength - 3)
                gerund = stem + "vindo"
                participle = stem + "vindo"
                impAffStem = stem + "v"
                pretIndStem = impAffStem
                impIndStem = pretIndStem
                presIndStem = impIndStem
                impSubjStem = stem + "vi"
                plupIndStem = impSubjStem
                persInfStem = stem + "vir"
                condIndStem = persInfStem
                futIndStem = condIndStem
                impNegStem = stem + "venha"
                presSubjStem = impNegStem
                futSubjStem = stem + "vier"
            }
            infinitive.matches(Regex("ansiar|incendiar|mediar|odiar|remediar|intermediar")) -> {
                verbClass = VerbClass.IAR
                stem = infinitive.substring(0, verbLength - 3)
                impNegStem = stem
                impAffStem = impNegStem
                presSubjStem = impAffStem
                presIndStem = presSubjStem
                impSubjStem = stem + "i"
                plupIndStem = impSubjStem
                pretIndStem = plupIndStem
                impIndStem = pretIndStem
                persInfStem = stem + "iar"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
            }
            infinitive.matches(Regex("dar|desdar")) -> {
                verbClass = VerbClass.DAR
                stem = infinitive.substring(0, verbLength - 3)
                impNegStem = stem + "d"
                impAffStem = impNegStem
                impSubjStem = impAffStem
                presSubjStem = impSubjStem
                plupIndStem = presSubjStem
                impIndStem = plupIndStem
                presIndStem = impIndStem
                pretIndStem = stem + "de"
                persInfStem = stem + "dar"
                condIndStem = persInfStem
                futIndStem = condIndStem
                futSubjStem = stem + "der"
            }
            infinitive.matches(Regex("ler|reler|tresler")) -> {
                verbClass = VerbClass.LER
                stem = infinitive.substring(0, verbLength - 3)
                impAffStem = stem + "l"
                impSubjStem = impAffStem
                plupIndStem = impSubjStem
                pretIndStem = plupIndStem
                impIndStem = pretIndStem
                presIndStem = impIndStem
                persInfStem = stem + "ler"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = stem + "leia"
                presSubjStem = impNegStem
            }
            infinitive.matches(Regex("antever|entrever|prever|rever|ver")) -> {
                verbClass = VerbClass.VER
                stem = infinitive.substring(0, verbLength - 3)
                gerund = stem + "vendo"
                participle = stem + "visto"
                impAffStem = stem + "v"
                impSubjStem = impAffStem
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                presIndStem = impIndStem
                pretIndStem = stem + "vi"
                persInfStem = stem + "ver"
                condIndStem = persInfStem
                futIndStem = condIndStem
                impNegStem = stem + "veja"
                presSubjStem = impNegStem
                futSubjStem = stem + "vir"
            }
            infinitive.matches(Regex("rir|sorrir")) -> {
                verbClass = VerbClass.RIR
                stem = infinitive.substring(0, verbLength - 3)
                impAffStem = stem + "ri"
                pretIndStem = impAffStem
                presIndStem = pretIndStem
                impSubjStem = stem + "r"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                persInfStem = stem + "rir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = stem + "ria"
                presSubjStem = impNegStem
            }
            infinitive.matches(Regex("apoiar|boiar")) -> {
                verbClass = VerbClass.OIAR
                stem = infinitive.substring(0, verbLength - 4)
                impAffStem = stem
                presIndStem = impAffStem
                impSubjStem = stem + "oi"
                plupIndStem = impSubjStem
                pretIndStem = plupIndStem
                impIndStem = pretIndStem
                persInfStem = stem + "oiar"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = stem + "oie"
                presSubjStem = impNegStem
            }
            matches(infinitive, verbLength, "dizer") -> {
                verbClass = VerbClass.DIZER
                stem = infinitive.substring(0, verbLength - 5)
                gerund = stem + "dizendo"
                participle = stem + "dito"
                impAffStem = stem + "di"
                presIndStem = impAffStem
                impIndStem = stem + "diz"
                pretIndStem = stem + "disse"
                impSubjStem = stem + "diss"
                plupIndStem = impSubjStem
                condIndStem = stem + "dir"
                futIndStem = condIndStem
                impNegStem = stem + "diga"
                presSubjStem = impNegStem
                futSubjStem = stem + "disser"
                persInfStem = stem + "dizer"
            }
            matches(infinitive, verbLength, "seguir") -> {
                verbClass = VerbClass.SEGUIR
                stem = infinitive.substring(0, verbLength - 6)
                impAffStem = stem + "s"
                presIndStem = impAffStem
                impSubjStem = stem + "segu"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = stem + "segui"
                persInfStem = stem + "seguir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = stem + "siga"
                presSubjStem = impNegStem
            }
            matches(infinitive, verbLength, "crer") -> {
                verbClass = VerbClass.CRER
                stem = infinitive.substring(0, verbLength - 4)
                impAffStem = stem + "cr"
                impSubjStem = impAffStem
                plupIndStem = impSubjStem
                pretIndStem = plupIndStem
                impIndStem = pretIndStem
                presIndStem = impIndStem
                persInfStem = stem + "crer"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = stem + "creia"
                presSubjStem = impNegStem
            }
            matches(infinitive, verbLength, "cobrir") -> {
                verbClass = VerbClass.COBRIR
                stem = infinitive.substring(0, verbLength - 6)
                gerund = stem + "cobrindo"
                participle = stem + "coberto"
                impAffStem = stem + "c"
                presIndStem = impAffStem
                impSubjStem = stem + "cobr"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = stem + "cobri"
                persInfStem = stem + "cobrir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = stem + "cubra"
                presSubjStem = impNegStem
            }
            matches(infinitive, verbLength, "fazer") -> {
                verbClass = VerbClass.FAZER
                stem = infinitive.substring(0, verbLength - 5)
                gerund = stem + "fazendo"
                participle = stem + "feito"
                impAffStem = stem + "fa"
                presIndStem = impAffStem
                impIndStem = stem + "faz"
                pretIndStem = stem + "f"
                impSubjStem = stem + "fiz"
                plupIndStem = impSubjStem
                condIndStem = stem + "far"
                futIndStem = condIndStem
                impNegStem = stem + "faça"
                presSubjStem = impNegStem
                futSubjStem = stem + "fizer"
                persInfStem = stem + "fazer"
            }
            matches(infinitive, verbLength, "air") -> {
                verbClass = VerbClass.AIR
                stem = infinitive.substring(0, verbLength - 3)
                gerund = stem + "aindo"
                participle = stem + "aído"
                persInfStem = stem + "a"
                impAffStem = persInfStem
                futSubjStem = impAffStem
                pretIndStem = futSubjStem
                presIndStem = pretIndStem
                impIndStem = stem + "aí"
                plupIndStem = stem + "aír"
                condIndStem = stem + "air"
                futIndStem = condIndStem
                impNegStem = stem + "aia"
                presSubjStem = impNegStem
                impSubjStem = stem + "aísse"
            }
            matches(infinitive, verbLength, "abrir") -> {
                verbClass = VerbClass.ABRIR
                stem = infinitive.substring(0, verbLength - 5)
                gerund = stem + "abrindo"
                participle = stem + "aberto"
                impAffStem = stem + "abr"
                impSubjStem = impAffStem
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                presIndStem = impIndStem
                pretIndStem = stem + "abri"
                persInfStem = stem + "abrir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = stem + "abra"
                presSubjStem = impNegStem
            }
            matches(infinitive, verbLength, "por") -> {
                verbClass = VerbClass.POR
                stem = infinitive.substring(0, verbLength - 3)
                gerund = stem + "pondo"
                participle = stem + "posto"
                impAffStem = stem + "p"
                pretIndStem = impAffStem
                impIndStem = pretIndStem
                presIndStem = impIndStem
                impSubjStem = stem + "pus"
                plupIndStem = impSubjStem
                persInfStem = stem + "por"
                condIndStem = persInfStem
                futIndStem = condIndStem
                impNegStem = stem + "ponha"
                presSubjStem = impNegStem
                futSubjStem = stem + "puser"
            }
            matches(infinitive, verbLength, "querer") -> {
                verbClass = VerbClass.QUERER
                stem = infinitive.substring(0, verbLength - 6)
                impIndStem = stem + "quer"
                presIndStem = impIndStem
                impSubjStem = stem + "quis"
                plupIndStem = impSubjStem
                pretIndStem = plupIndStem
                persInfStem = stem + "querer"
                condIndStem = persInfStem
                futIndStem = condIndStem
                impNegStem = stem + "queira"
                presSubjStem = impNegStem
                futSubjStem = stem + "quiser"
                impAffStem = stem + "que"
            }
            matches(infinitive, verbLength, "valer") -> {
                verbClass = VerbClass.VALER
                stem = infinitive.substring(0, verbLength - 5)
                impAffStem = stem + "val"
                impSubjStem = impAffStem
                plupIndStem = impSubjStem
                pretIndStem = plupIndStem
                impIndStem = pretIndStem
                presIndStem = impIndStem
                persInfStem = stem + "valer"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = stem + "valha"
                presSubjStem = impNegStem
            }
            matches(infinitive, verbLength, "gredir") -> {
                verbClass = VerbClass.GREDIR
                stem = infinitive.substring(0, verbLength - 6)
                impAffStem = stem + "gr"
                presIndStem = impAffStem
                impSubjStem = stem + "gred"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = stem + "gredi"
                persInfStem = stem + "gredir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = stem + "grida"
                presSubjStem = impNegStem
            }
            matches(infinitive, verbLength, "edir") -> {
                verbClass = VerbClass.EDIR
                stem = infinitive.substring(0, verbLength - 4)
                impAffStem = stem + "e"
                presIndStem = impAffStem
                impSubjStem = stem + "ed"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = stem + "edi"
                persInfStem = stem + "edir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = stem + "eça"
                presSubjStem = impNegStem
            }
            matches(infinitive, verbLength, "entir") -> {
                verbClass = VerbClass.ENTIR
                stem = infinitive.substring(0, verbLength - 5)
                impAffStem = stem
                presIndStem = impAffStem
                impSubjStem = stem + "ent"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = stem + "enti"
                persInfStem = stem + "entir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = stem + "inta"
                presSubjStem = impNegStem
            }
            matches(infinitive, verbLength, "elir") -> {
                verbClass = VerbClass.ELIR
                stem = infinitive.substring(0, verbLength - 4)
                gerund = stem + "elindo"
                participle = stem + "ulso"
                impAffStem = stem
                presIndStem = impAffStem
                impSubjStem = stem + "el"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = stem + "eli"
                persInfStem = stem + "elir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = stem + "ila"
                presSubjStem = impNegStem
            }
            matches(infinitive, verbLength, "erir") -> {
                verbClass = VerbClass.ERIR
                stem = infinitive.substring(0, verbLength - 4)
                impAffStem = stem
                presIndStem = impAffStem
                impSubjStem = stem + "er"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = stem + "eri"
                persInfStem = stem + "erir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = stem + "ira"
                presSubjStem = impNegStem
            }
            matches(infinitive, verbLength, "ear") -> {
                verbClass = VerbClass.EAR
                stem = infinitive.substring(0, verbLength - 3)
                impNegStem = stem + "e"
                impAffStem = impNegStem
                impSubjStem = impAffStem
                presSubjStem = impSubjStem
                plupIndStem = presSubjStem
                pretIndStem = plupIndStem
                impIndStem = pretIndStem
                presIndStem = impIndStem
                persInfStem = stem + "ear"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
            }
            matches(infinitive, verbLength, "oibir") -> {
                verbClass = VerbClass.OIBIR
                stem = infinitive.substring(0, verbLength - 5)
                presIndStem = stem + "o"
                impSubjStem = stem + "oib"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = stem + "oibi"
                persInfStem = stem + "oibir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = stem + "oíba"
                presSubjStem = impNegStem
                impAffStem = stem + "oíb"
            }
            matches(infinitive, verbLength, "oer") -> {
                verbClass = VerbClass.OER
                stem = infinitive.substring(0, verbLength - 3)
                gerund = stem + "oendo"
                participle = stem + "oído"
                impAffStem = stem
                presIndStem = impAffStem
                impIndStem = stem + "oí"
                impSubjStem = stem + "o"
                plupIndStem = impSubjStem
                pretIndStem = plupIndStem
                persInfStem = stem + "oer"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = stem + "oa"
                presSubjStem = impNegStem
            }
            matches(infinitive, verbLength, "tuir") -> {
                verbClass = VerbClass.TUIR
                stem = infinitive.substring(0, verbLength - 4)
                gerund = stem + "tuindo"
                participle = stem + "tuído"
                persInfStem = stem + "tu"
                impAffStem = persInfStem
                futSubjStem = impAffStem
                pretIndStem = futSubjStem
                presIndStem = pretIndStem
                impIndStem = stem + "tuí"
                plupIndStem = stem + "tuír"
                condIndStem = stem + "tuir"
                futIndStem = condIndStem
                impNegStem = stem + "tua"
                presSubjStem = impNegStem
                impSubjStem = stem + "tuísse"
            }
            matches(infinitive, verbLength, "buir") -> {
                verbClass = VerbClass.BUIR
                stem = infinitive.substring(0, verbLength - 4)
                gerund = stem + "buindo"
                participle = stem + "buído"
                persInfStem = stem + "bu"
                impAffStem = persInfStem
                futSubjStem = impAffStem
                pretIndStem = futSubjStem
                presIndStem = pretIndStem
                impIndStem = stem + "buí"
                plupIndStem = stem + "buír"
                condIndStem = stem + "buir"
                futIndStem = condIndStem
                impNegStem = stem + "bua"
                presSubjStem = impNegStem
                impSubjStem = stem + "buísse"
            }
            matches(infinitive, verbLength, "struir") -> {
                verbClass = VerbClass.STRUIR
                stem = infinitive.substring(0, verbLength - 6)
                gerund = stem + "struindo"
                participle = stem + "struído"
                persInfStem = stem + "stru"
                impAffStem = persInfStem
                futSubjStem = impAffStem
                pretIndStem = futSubjStem
                presIndStem = pretIndStem
                impIndStem = stem + "struí"
                plupIndStem = stem + "struír"
                condIndStem = stem + "struir"
                futIndStem = condIndStem
                impNegStem = stem + "strua"
                presSubjStem = impNegStem
                impSubjStem = stem + "struísse"
            }
            matches(infinitive, verbLength, "ervir") -> {
                verbClass = VerbClass.ERVIR
                stem = infinitive.substring(0, verbLength - 5)
                impAffStem = stem
                presIndStem = impAffStem
                impSubjStem = stem + "erv"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = stem + "ervi"
                persInfStem = stem + "ervir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = stem + "irva"
                presSubjStem = impNegStem
            }
            matches(infinitive, verbLength, "olir") -> {
                verbClass = VerbClass.OLIR
                stem = infinitive.substring(0, verbLength - 4)
                impAffStem = stem
                presIndStem = impAffStem
                impSubjStem = stem + "ol"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                pretIndStem = stem + "oli"
                persInfStem = stem + "olir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = stem + "ula"
                presSubjStem = impNegStem
            }
            matches(infinitive, verbLength, "prazer") -> {
                verbClass = VerbClass.PRAZER
                stem = infinitive.substring(0, verbLength - 6)
                impIndStem = stem + "praz"
                presIndStem = impIndStem
                pretIndStem = stem + "prouve"
                impSubjStem = stem + "prouv"
                plupIndStem = impSubjStem
                persInfStem = stem + "prazer"
                condIndStem = persInfStem
                futIndStem = condIndStem
                impNegStem = stem + "praza"
                impAffStem = impNegStem
                presSubjStem = impAffStem
                futSubjStem = stem + "prouver"
            }
            matches(infinitive, verbLength, "colorir") -> {
                verbClass = VerbClass.DEF_ORIR
                stem = infinitive.substring(0, verbLength - 7)
                impSubjStem = stem + "color"
                plupIndStem = impSubjStem
                impIndStem = plupIndStem
                presIndStem = impIndStem
                pretIndStem = stem + "colori"
                persInfStem = stem + "colorir"
                futSubjStem = persInfStem
                condIndStem = futSubjStem
                futIndStem = condIndStem
                impNegStem = stem + "colora"
                presSubjStem = impNegStem
                impAffStem = stem
            }
            else -> {
                verbClass = if (ending == "ar") VerbClass.REG_AR else if (ending == "er") VerbClass.REG_ER else VerbClass.REG_IR
                when {
                    verbClass === VerbClass.REG_AR -> {
                        stem = infinitive.substring(0, verbLength - 2)
                        eAndIStem = getEIStem(infinitive, verbLength, stem)
                        impAffStem = stem
                        impSubjStem = impAffStem
                        plupIndStem = impSubjStem
                        pretIndStem = plupIndStem
                        impIndStem = pretIndStem
                        presIndStem = impIndStem
                        persInfStem = infinitive
                        futSubjStem = persInfStem
                        condIndStem = futSubjStem
                        futIndStem = condIndStem
                        impNegStem = stem + "e"
                        presSubjStem = impNegStem
                    }
                    verbClass === VerbClass.REG_ER -> {
                        stem = infinitive.substring(0, verbLength - 2)
                        aAndOStem = getAOStem(infinitive, verbLength, stem)
                        impAffStem = stem
                        impSubjStem = impAffStem
                        plupIndStem = impSubjStem
                        pretIndStem = plupIndStem
                        impIndStem = pretIndStem
                        presIndStem = impIndStem
                        persInfStem = infinitive
                        futSubjStem = persInfStem
                        condIndStem = futSubjStem
                        futIndStem = condIndStem
                        impNegStem = stem + "a"
                        presSubjStem = impNegStem
                    }
                    else -> {
                        stem = infinitive.substring(0, verbLength - 2)
                        aAndOStem = getAOStem(infinitive, verbLength, stem)
                        impAffStem = stem
                        impSubjStem = impAffStem
                        plupIndStem = impSubjStem
                        impIndStem = plupIndStem
                        presIndStem = impIndStem
                        pretIndStem = stem + "i"
                        persInfStem = infinitive
                        futSubjStem = persInfStem
                        condIndStem = futSubjStem
                        futIndStem = condIndStem
                        impNegStem = stem + "a"
                        presSubjStem = impNegStem
                    }
                }
            }
        }
        if (participle == null) {
            if (ending == "ar") {
                val substr = infinitive.substring(0, infinitive.length - 1)
                participle = substr + "do"
                gerund = substr + "ndo"
            } else {
                participle = infinitive.substring(0, infinitive.length - 2) + "ido"
                gerund = infinitive.substring(0, infinitive.length - 1) + "ndo"
            }
        }
        return VerbData(verbClass, stem, eAndIStem ?: stem, aAndOStem
                ?: stem, presIndStem, impIndStem, pretIndStem, impAffStem, plupIndStem, impSubjStem,
                futIndStem, condIndStem, persInfStem, presSubjStem, impNegStem, futSubjStem,
                participle, gerund)
    }

    fun conjugate(infinitiveUnformatted: String, verbForms: Array<VerbForm>, portugal: Boolean): HashMap<VerbForm, HashMap<Person, String?>> {
        val infinitive = infinitiveUnformatted.toLowerCase(Locale.ROOT)
        val verbLength = infinitive.length
        val verbsToReturn = HashMap<VerbForm, HashMap<Person, String?>>(verbForms.size)
        if (!infinitive.contains(" ") && !infinitive.contains("-") && !infinitive.contains("+") && verbLength > 1 && verbForms.isNotEmpty() && infinitive[verbLength - 1] == 'r') {
            val verbData = getVerbClassAndStems(infinitive, verbLength)
            val isOrthographicAr = verbData.eAndIStem != verbData.stem
            val isOrthographicNonAr = verbData.aAndOStem != verbData.stem
            for (verbForm in verbForms) {
                val conjugatedVerbs = conjugateVerbForm(verbForm, isOrthographicNonAr, isOrthographicAr, verbData, infinitive, portugal)
                val personToVerbMap = hashMapOf(
                    Person.FIRST_SING to conjugatedVerbs[0],
                    Person.SECOND_SING to conjugatedVerbs[1],
                    Person.THIRD_SING to conjugatedVerbs[2],
                    Person.FIRST_PLUR to conjugatedVerbs[3],
                    Person.SECOND_PLUR to conjugatedVerbs[4],
                    Person.THIRD_PLUR to conjugatedVerbs[5]
                )
                verbsToReturn[verbForm] = personToVerbMap
            }
        }
        return verbsToReturn
    }

    private fun conjugateVerbForm(verbForm: VerbForm, isOrthographicNonAr: Boolean, isOrthographicAr: Boolean, verbData: VerbData, infinitive: String, portugal: Boolean): Array<String?> {
        return when (verbForm) {
            VerbForm.PRES_IND -> {
                if (isOrthographicNonAr) {
                    arrayOf(
                        verbData.aAndOStem + "o",
                        verbData.stem + "es",
                        verbData.stem + "e",
                        verbData.stem + "emos",
                        verbData.stem + "eis",
                        verbData.stem + "em"
                    )
                } else {
                    conjugatePresInd(
                        infinitive,
                        verbData.verbClass,
                        verbData.presIndStem
                    )
                }
            }
            VerbForm.IMP_IND -> conjugateImpInd(
                verbData.verbClass,
                verbData.impIndStem
            )
            VerbForm.PRET_IND -> if (isOrthographicAr) {
                arrayOf(
                    verbData.eAndIStem + "ei",
                    verbData.stem + "aste",
                    verbData.stem + "ou",
                    verbData.stem + "amos/" + verbData.stem + "ámos",
                    verbData.stem + "astes",
                    verbData.stem + "aram"
                )
            } else {
                conjugatePretInd(verbData.verbClass, verbData.pretIndStem)
            }
            VerbForm.SIMP_PLUP_IND -> conjugateSimpPlupInd(
                verbData.verbClass,
                verbData.plupIndStem
            )
            VerbForm.FUT_IND -> conjugateFutInd(
                verbData.verbClass,
                verbData.futIndStem
            )
            VerbForm.SIMP_FUT_IND -> arrayOf(
                "vou $infinitive",
                "vais $infinitive",
                "vai $infinitive",
                "vamos $infinitive",
                "ides $infinitive",
                "vão $infinitive"
            )
            VerbForm.COND_IND -> conjugateCondInd(
                verbData.verbClass,
                verbData.condIndStem
            )
            VerbForm.PRES_PERF -> arrayOf(
                "tenho " + verbData.participle,
                "tens " + verbData.participle,
                "tem " + verbData.participle,
                "temos " + verbData.participle,
                "tendes " + verbData.participle,
                "têm " + verbData.participle
            )
            VerbForm.PLUP -> arrayOf(
                "tinha " + verbData.participle,
                "tinhas " + verbData.participle,
                "tinha " + verbData.participle,
                "tínhamos " + verbData.participle,
                "tínheis " + verbData.participle,
                "tinham " + verbData.participle
            )
            VerbForm.FUT_PERF -> arrayOf(
                "terei " + verbData.participle,
                "terás " + verbData.participle,
                "terá " + verbData.participle,
                "teremos " + verbData.participle,
                "tereis " + verbData.participle,
                "terão " + verbData.participle
            )
            VerbForm.COND_PERF -> arrayOf(
                "teria " + verbData.participle,
                "terias " + verbData.participle,
                "teria " + verbData.participle,
                "teríamos " + verbData.participle,
                "teríeis " + verbData.participle,
                "teriam " + verbData.participle
            )
            VerbForm.PRES_PROG -> if (portugal || verbData.gerund == null) {
                arrayOf(
                    "estou a $infinitive",
                    "estás a $infinitive",
                    "está a $infinitive",
                    "estamos a $infinitive",
                    "estais a $infinitive",
                    "estão a $infinitive"
                )
            } else {
                arrayOf(
                    "estou " + verbData.gerund,
                    "estás " + verbData.gerund,
                    "está " + verbData.gerund,
                    "estamos " + verbData.gerund,
                    "estais " + verbData.gerund,
                    "estão " + verbData.gerund
                )
            }
            VerbForm.IMP_PROG -> if (portugal || verbData.gerund == null) {
                arrayOf(
                    "estava a $infinitive",
                    "estavas a $infinitive",
                    "estava a $infinitive",
                    "estávamos a $infinitive",
                    "estáveis a $infinitive",
                    "estavam a $infinitive"
                )
            } else {
                arrayOf(
                    "estava " + verbData.gerund,
                    "estavas " + verbData.gerund,
                    "estava " + verbData.gerund,
                    "estávamos " + verbData.gerund,
                    "estáveis " + verbData.gerund,
                    "estavam " + verbData.gerund
                )
            }
            VerbForm.PRET_PROG -> if (portugal || verbData.gerund == null) {
                arrayOf(
                    "estive a $infinitive",
                    "estiveste a $infinitive",
                    "esteve a $infinitive",
                    "estivemos a $infinitive",
                    "estivestes a $infinitive",
                    "estiveram a $infinitive"
                )
            } else {
                arrayOf(
                    "estive " + verbData.gerund,
                    "estiveste " + verbData.gerund,
                    "esteve " + verbData.gerund,
                    "estivemos " + verbData.gerund,
                    "estivestes " + verbData.gerund,
                    "estiveram " + verbData.gerund
                )
            }
            VerbForm.SIMP_PLUP_PROG -> if (portugal || verbData.gerund == null) {
                arrayOf(
                    "estivera a $infinitive",
                    "estiveras a $infinitive",
                    "estivera a $infinitive",
                    "estivéramos a $infinitive",
                    "estivéreis a $infinitive",
                    "estiveram a $infinitive"
                )
            } else {
                arrayOf(
                    "estivera " + verbData.gerund,
                    "estiveras " + verbData.gerund,
                    "estivera " + verbData.gerund,
                    "estivéramos " + verbData.gerund,
                    "estivéreis " + verbData.gerund,
                    "estiveram " + verbData.gerund
                )
            }
            VerbForm.FUT_PROG -> if (portugal || verbData.gerund == null) {
                arrayOf(
                    "estarei a $infinitive",
                    "estarás a $infinitive",
                    "estará a $infinitive",
                    "estaremos a $infinitive",
                    "estareis a $infinitive",
                    "estarão a $infinitive"
                )
            } else {
                arrayOf(
                    "estarei " + verbData.gerund,
                    "estarás " + verbData.gerund,
                    "estará " + verbData.gerund,
                    "estaremos " + verbData.gerund,
                    "estareis " + verbData.gerund,
                    "estarão " + verbData.gerund
                )
            }
            VerbForm.COND_PROG -> if (portugal || verbData.gerund == null) {
                arrayOf(
                    "estaria a $infinitive",
                    "estarias a $infinitive",
                    "estaria a $infinitive",
                    "estaríamos a $infinitive",
                    "estaríeis a $infinitive",
                    "estariam a $infinitive"
                )
            } else {
                arrayOf(
                    "estaria " + verbData.gerund,
                    "estarias " + verbData.gerund,
                    "estaria " + verbData.gerund,
                    "estaríamos " + verbData.gerund,
                    "estaríeis " + verbData.gerund,
                    "estariam " + verbData.gerund
                )
            }
            VerbForm.PRES_PERF_PROG -> if (portugal || verbData.gerund == null) {
                arrayOf(
                    "tenho estado a $infinitive",
                    "tens estado a $infinitive",
                    "tem estado a $infinitive",
                    "temos estado a $infinitive",
                    "tendes estado a $infinitive",
                    "têm estado a $infinitive"
                )
            } else {
                arrayOf(
                    "tenho estado " + verbData.gerund,
                    "tens estado " + verbData.gerund,
                    "tem estado " + verbData.gerund,
                    "temos estado " + verbData.gerund,
                    "tendes estado " + verbData.gerund,
                    "têm estado " + verbData.gerund
                )
            }
            VerbForm.PLUP_PROG -> if (portugal || verbData.gerund == null) {
                arrayOf(
                    "tinha estado a $infinitive",
                    "tinhas estado a $infinitive",
                    "tinha estado a $infinitive",
                    "tínhamos estado a $infinitive",
                    "tínheis estado a $infinitive",
                    "tinham estado a $infinitive"
                )
            } else {
                arrayOf(
                    "tinha estado " + verbData.gerund,
                    "tinhas estado " + verbData.gerund,
                    "tinha estado " + verbData.gerund,
                    "tínhamos estado " + verbData.gerund,
                    "tínheis estado " + verbData.gerund,
                    "tinham estado " + verbData.gerund
                )
            }
            VerbForm.FUT_PERF_PROG -> if (portugal || verbData.gerund == null) {
                arrayOf(
                    "terei estado a $infinitive",
                    "terás estado a $infinitive",
                    "terá estado a $infinitive",
                    "teremos estado a $infinitive",
                    "tereis estado a $infinitive",
                    "terão estado a $infinitive"
                )
            } else {
                arrayOf(
                    "terei estado " + verbData.gerund,
                    "terás estado " + verbData.gerund,
                    "terá estado " + verbData.gerund,
                    "teremos estado " + verbData.gerund,
                    "tereis estado " + verbData.gerund,
                    "terão estado " + verbData.gerund
                )
            }
            VerbForm.COND_PERF_PROG -> if (portugal || verbData.gerund == null) {
                arrayOf(
                    "teria estado a $infinitive",
                    "terias estado a $infinitive",
                    "teria estado a $infinitive",
                    "teríamos estado a $infinitive",
                    "teríeis estado a $infinitive",
                    "teriam estado a $infinitive"
                )
            } else {
                arrayOf(
                    "teria estado " + verbData.gerund,
                    "terias estado " + verbData.gerund,
                    "teria estado " + verbData.gerund,
                    "teríamos estado " + verbData.gerund,
                    "teríeis estado " + verbData.gerund,
                    "teriam estado " + verbData.gerund
                )
            }
            VerbForm.PRES_SUBJ -> when {
                isOrthographicAr -> {
                    arrayOf(
                        verbData.eAndIStem + "e",
                        verbData.eAndIStem + "es",
                        verbData.eAndIStem + "e",
                        verbData.eAndIStem + "emos",
                        verbData.eAndIStem + "eis",
                        verbData.eAndIStem + "em"
                    )
                }
                isOrthographicNonAr -> {
                    arrayOf(
                        verbData.aAndOStem + "a",
                        verbData.aAndOStem + "as",
                        verbData.aAndOStem + "a",
                        verbData.aAndOStem + "amos",
                        verbData.aAndOStem + "ais",
                        verbData.aAndOStem + "am"
                    )
                }
                else -> {
                    conjugatePresSubj(verbData.verbClass, verbData.presSubjStem)
                }
            }
            VerbForm.PRES_PERF_SUBJ -> arrayOf(
                "tenha " + verbData.participle,
                "tenhas " + verbData.participle,
                "tenha " + verbData.participle,
                "tenhamos " + verbData.participle,
                "tenhais " + verbData.participle,
                "tenham " + verbData.participle
            )
            VerbForm.IMP_SUBJ -> conjugateImpSubj(
                verbData.verbClass,
                verbData.impSubjStem
            )
            VerbForm.PLUP_SUBJ -> arrayOf(
                "tivesse " + verbData.participle,
                "tivesses " + verbData.participle,
                "tivesse " + verbData.participle,
                "tivéssemos " + verbData.participle,
                "tivésseis " + verbData.participle,
                "tivessem " + verbData.participle
            )
            VerbForm.FUT_SUBJ -> conjugateFutSubj(
                verbData.verbClass,
                verbData.futSubjStem
            )
            VerbForm.FUT_PERF_SUBJ -> arrayOf(
                "tiver " + verbData.participle,
                "tiveres " + verbData.participle,
                "tiver " + verbData.participle,
                "tivermos " + verbData.participle,
                "tiverdes " + verbData.participle,
                "tiveram " + verbData.participle
            )
            VerbForm.IMP_AFF -> when {
                isOrthographicAr -> {
                    arrayOf(
                        null,
                        verbData.stem + "a",
                        verbData.eAndIStem + "e",
                        verbData.eAndIStem + "emos",
                        verbData.stem + "ai",
                        verbData.eAndIStem + "em"
                    )
                }
                isOrthographicNonAr -> {
                    arrayOf(
                        null,
                        verbData.stem + "e",
                        verbData.aAndOStem + "a",
                        verbData.aAndOStem + "amos",
                        verbData.stem + "ei",
                        verbData.aAndOStem + "am"
                    )
                }
                else -> {
                    conjugateImpAff(
                        infinitive,
                        verbData.verbClass,
                        verbData.impAffStem
                    )
                }
            }
            VerbForm.IMP_NEG -> when {
                isOrthographicAr -> {
                    arrayOf(
                        null,
                        verbData.eAndIStem + "es",
                        verbData.eAndIStem + "e",
                        verbData.eAndIStem + "emos",
                        verbData.eAndIStem + "eis",
                        verbData.eAndIStem + "em"
                    )
                }
                isOrthographicNonAr -> {
                    arrayOf(
                        null,
                        verbData.aAndOStem + "as",
                        verbData.aAndOStem + "a",
                        verbData.aAndOStem + "amos",
                        verbData.aAndOStem + "ais",
                        verbData.aAndOStem + "am"
                    )
                }
                else -> {
                    conjugateImpNeg(verbData.verbClass, verbData.impNegStem)
                }
            }
            VerbForm.PERS_INF -> conjugatePersInf(
                verbData.verbClass,
                verbData.persInfStem
            )
        }.map { it }.toTypedArray()
    }

    private fun conjugatePresInd(infinitive: String, verbClass: VerbClass, presIndStem: String): Array<String?> {
        return when (verbClass) {
            VerbClass.REG_AR -> arrayOf(presIndStem + "o", presIndStem + "as", presIndStem + "a", presIndStem + "amos", presIndStem + "ais", presIndStem + "am")
            VerbClass.REG_ER -> arrayOf(presIndStem + "o", presIndStem + "es", presIndStem + "e", presIndStem + "emos", presIndStem + "eis", presIndStem + "em")
            VerbClass.REG_IR, VerbClass.ABRIR, VerbClass.GANIR, VerbClass.BARRIR, VerbClass.DEF_ORIR, VerbClass.ODIR, VerbClass.ATIR -> arrayOf(presIndStem + "o", presIndStem + "es", presIndStem + "e", presIndStem + "imos", presIndStem + "is", presIndStem + "em")
            VerbClass.TER -> arrayOf(presIndStem + "enho", if (infinitive == "ter") presIndStem + "ens" else presIndStem + "éns", if (infinitive == "ter") presIndStem + "em" else presIndStem + "ém",
                    presIndStem + "emos", presIndStem + "endes", presIndStem + "êm")
            VerbClass.FAZER -> arrayOf(presIndStem + "ço", presIndStem + "zes", presIndStem + "z", presIndStem + "zemos", presIndStem + "zeis", presIndStem + "zem")
            VerbClass.AIR -> arrayOf(presIndStem + "io", presIndStem + "is", presIndStem + "i", presIndStem + "ímos", presIndStem + "ís", presIndStem + "em")
            VerbClass.POR, VerbClass.PÔR -> arrayOf(presIndStem + "onho", presIndStem + "ões", presIndStem + "õe", presIndStem + "omos", presIndStem + "ondes", presIndStem + "õem")
            VerbClass.DIZER, VerbClass.TRAZER -> arrayOf(presIndStem + "go", presIndStem + "zes", presIndStem + "z", presIndStem + "zemos", presIndStem + "zeis", presIndStem + "zem")
            VerbClass.SEGUIR -> arrayOf(presIndStem + "igo", presIndStem + "egues", presIndStem + "egue", presIndStem + "eguimos", presIndStem + "eguis", presIndStem + "eguem")
            VerbClass.CRER, VerbClass.LER -> arrayOf(presIndStem + "eio", presIndStem + "ês", presIndStem + "ê", presIndStem + "emos", presIndStem + "edes", presIndStem + "eem")
            VerbClass.COBRIR -> arrayOf(presIndStem + "ubro", presIndStem + "obres", presIndStem + "obre", presIndStem + "obrimos", presIndStem + "obris", presIndStem + "obrem")
            VerbClass.QUERER -> arrayOf(presIndStem + "o", presIndStem + "es", presIndStem + "/" + presIndStem + "e", presIndStem + "emos", presIndStem + "eis", presIndStem + "em")
            VerbClass.VALER -> arrayOf(presIndStem + "ho", presIndStem + "es", presIndStem + "e/" + presIndStem, presIndStem + "emos", presIndStem + "eis", presIndStem + "em")
            VerbClass.GREDIR -> arrayOf(presIndStem + "ido", presIndStem + "ides", presIndStem + "ide", presIndStem + "edimos", presIndStem + "edis", presIndStem + "idem")
            VerbClass.EDIR -> arrayOf(presIndStem + "ço", presIndStem + "des", presIndStem + "de", presIndStem + "dimos", presIndStem + "dis", presIndStem + "dem")
            VerbClass.ENTIR -> arrayOf(presIndStem + "into", presIndStem + "entes", presIndStem + "ente", presIndStem + "entimos", presIndStem + "entis", presIndStem + "entem")
            VerbClass.DELIR, VerbClass.DESPIR -> arrayOf(null, presIndStem + "es", presIndStem + "e", presIndStem + "imos", presIndStem + "is", presIndStem + "em")
            VerbClass.ELIR -> arrayOf(presIndStem + "ilo", presIndStem + "eles", presIndStem + "ele", presIndStem + "elimos", presIndStem + "elis", presIndStem + "elem")
            VerbClass.ERIR -> arrayOf(presIndStem + "iro", presIndStem + "eres", presIndStem + "ere", presIndStem + "erimos", presIndStem + "eris", presIndStem + "erem")
            VerbClass.EAR -> arrayOf(presIndStem + "io", presIndStem + "ias", presIndStem + "ia", presIndStem + "amos", presIndStem + "ais", presIndStem + "iam")
            VerbClass.OIBIR -> arrayOf(presIndStem + "íbo", presIndStem + "íbes", presIndStem + "íbe", presIndStem + "ibimos", presIndStem + "ibis", presIndStem + "íbem")
            VerbClass.OER -> arrayOf(presIndStem + "oo/" + presIndStem + "óis", presIndStem + "ói", presIndStem + "oemos", presIndStem + "oeis", presIndStem + "oem")
            VerbClass.VESTIR -> arrayOf(presIndStem + "isto", presIndStem + "estes", presIndStem + "este", presIndStem + "estimos", presIndStem + "estis", presIndStem + "estem")
            VerbClass.TUIR, VerbClass.BUIR, VerbClass.STRUIR -> arrayOf(presIndStem + "o", presIndStem + "is", presIndStem + "i", presIndStem + "ímos", presIndStem + "ís", presIndStem + "em")
            VerbClass.ERVIR, VerbClass.SERVIR -> arrayOf(presIndStem + "irvo", presIndStem + "erves", presIndStem + "erve", presIndStem + "ervimos", presIndStem + "ervis", presIndStem + "ervem")
            VerbClass.CABER -> arrayOf(presIndStem + "ibo", presIndStem + "bes", presIndStem + "be", presIndStem + "bemos", presIndStem + "beis", presIndStem + "bem")
            VerbClass.ESTAR -> arrayOf(presIndStem + "ou", presIndStem + "ás", presIndStem + "á", presIndStem + "amos/" + presIndStem + "ámos", presIndStem + "ais", presIndStem + "ão")
            VerbClass.GEAR, VerbClass.NEVAR -> arrayOf(null, null, presIndStem, null, null, null)
            VerbClass.HAVER -> arrayOf(presIndStem + "ei", presIndStem + "ás", presIndStem + "á", presIndStem + "avemos/" + presIndStem + "emos", presIndStem + "aveis/" + presIndStem + "eis",
                    presIndStem + "ão")
            VerbClass.IR -> arrayOf(presIndStem + "vou", presIndStem + "vais", presIndStem + "vai", presIndStem + "vamos/" + presIndStem + "imos", presIndStem + "ides", presIndStem + "vão")
            VerbClass.OUVIR -> arrayOf(presIndStem + "uço/" + presIndStem + "iço", presIndStem + "uves", presIndStem + "uve", presIndStem + "uvimos", presIndStem + "uvis", presIndStem + "uvem")
            VerbClass.PERDER -> arrayOf(presIndStem + "co", presIndStem + "des", presIndStem + "de", presIndStem + "demos", presIndStem + "deis", presIndStem + "dem")
            VerbClass.PODER -> arrayOf(presIndStem + "sso", presIndStem + "des", presIndStem + "de", presIndStem + "demos", presIndStem + "deis", presIndStem + "dem")
            VerbClass.PROVER, VerbClass.VER -> arrayOf(presIndStem + "ejo", presIndStem + "ês", presIndStem + "ê", presIndStem + "emos", presIndStem + "edes", presIndStem + "eem")
            VerbClass.SABER -> arrayOf(presIndStem + "ei", presIndStem + "abes", presIndStem + "abe", presIndStem + "abemos", presIndStem + "abeis", presIndStem + "abem")
            VerbClass.SER -> arrayOf(presIndStem + "sou", presIndStem + "és", presIndStem + "é", presIndStem + "somos", presIndStem + "sois", presIndStem + "são")
            VerbClass.TOSSIR -> arrayOf(presIndStem + "usso", presIndStem + "osses", presIndStem + "osse", presIndStem + "ossimos", presIndStem + "ossis", presIndStem + "ossem")
            VerbClass.ENGOLIR, VerbClass.OLIR -> arrayOf(presIndStem + "ulo", presIndStem + "oles", presIndStem + "ole", presIndStem + "olimos", presIndStem + "olis", presIndStem + "olem")
            VerbClass.FUGIR -> arrayOf(presIndStem + "ujo", presIndStem + "oges", presIndStem + "oge", presIndStem + "ugimos", presIndStem + "ugis", presIndStem + "ogem")
            VerbClass.DIVERTIR -> arrayOf(presIndStem + "irto", presIndStem + "ertes", presIndStem + "erte", presIndStem + "ertimos", presIndStem + "ertis", presIndStem + "ertem")
            VerbClass.SAUDAR -> arrayOf(presIndStem + "údo", presIndStem + "údas", presIndStem + "úda", presIndStem + "udamos", presIndStem + "udais", presIndStem + "údam")
            VerbClass.REUNIR -> arrayOf(presIndStem + "úno", presIndStem + "únes", presIndStem + "úne", presIndStem + "unimos", presIndStem + "unis", presIndStem + "únem")
            VerbClass.REMIR -> arrayOf(presIndStem + "dimo/" + presIndStem + "dimes/" + presIndStem + "dime/" + presIndStem + "mimos", presIndStem + "mis", presIndStem + "dimem")
            VerbClass.DORMIR -> arrayOf(presIndStem + "urmo", presIndStem + "ormes", presIndStem + "orme", presIndStem + "ormimos", presIndStem + "ormis", presIndStem + "ormem")
            VerbClass.PRAZER -> arrayOf("{d}" + presIndStem + "o", "{d}" + presIndStem + "es", presIndStem, "{d}" + presIndStem + "emos", "{d}" + presIndStem + "eis", "{d}" + presIndStem + "em")
            VerbClass.DOER -> arrayOf("{d}" + presIndStem + "oo", "{d}" + presIndStem + "óis", presIndStem + "ói", "{d}" + presIndStem + "oemos", "{d}" + presIndStem + "oeis", presIndStem + "oem")
            VerbClass.FALIR -> arrayOf(null, null, null, presIndStem + "mos", presIndStem + "s", null)
            VerbClass.VIR -> arrayOf(presIndStem + "enho", presIndStem + "ens", presIndStem + "em", presIndStem + "imos", presIndStem + "indes", presIndStem + "êm")
            VerbClass.IAR -> arrayOf(presIndStem + "eio", presIndStem + "eias", presIndStem + "eia", presIndStem + "iamos", presIndStem + "iais", presIndStem + "eiam")
            VerbClass.DAR -> arrayOf(presIndStem + "ou", presIndStem + "ás", presIndStem + "á", presIndStem + "amos", presIndStem + "ais", presIndStem + "ão")
            VerbClass.RIR -> arrayOf(presIndStem + "o", presIndStem + "s", presIndStem, presIndStem + "mos", presIndStem + "des", presIndStem + "em")
            VerbClass.OIAR -> arrayOf(presIndStem + "óio", presIndStem + "óias", presIndStem + "óia", presIndStem + "oiamos", presIndStem + "oiais", presIndStem + "óiam")
        }
    }

    private fun conjugateImpInd(verbClass: VerbClass, impIndStem: String): Array<String?> {
        return when (verbClass) {
            VerbClass.REG_AR, VerbClass.EAR, VerbClass.SAUDAR, VerbClass.IAR, VerbClass.DAR, VerbClass.OIAR -> arrayOf(impIndStem + "ava", impIndStem + "avas", impIndStem + "ava", impIndStem + "ávamos", impIndStem + "áveis", impIndStem + "avam")
            VerbClass.REG_ER, VerbClass.REG_IR, VerbClass.FAZER, VerbClass.ABRIR, VerbClass.DIZER, VerbClass.SEGUIR, VerbClass.CRER, VerbClass.COBRIR, VerbClass.QUERER, VerbClass.VALER, VerbClass.GREDIR, VerbClass.EDIR, VerbClass.ENTIR, VerbClass.DELIR, VerbClass.ELIR, VerbClass.ERIR, VerbClass.OIBIR, VerbClass.VESTIR, VerbClass.ERVIR, VerbClass.CABER, VerbClass.HAVER, VerbClass.IR, VerbClass.OUVIR, VerbClass.PERDER, VerbClass.PODER, VerbClass.PROVER, VerbClass.SABER, VerbClass.TOSSIR, VerbClass.TRAZER, VerbClass.ENGOLIR, VerbClass.FUGIR, VerbClass.DIVERTIR, VerbClass.SERVIR, VerbClass.GANIR, VerbClass.REUNIR, VerbClass.REMIR, VerbClass.DORMIR, VerbClass.OLIR, VerbClass.BARRIR, VerbClass.DEF_ORIR, VerbClass.DESPIR, VerbClass.FALIR, VerbClass.ODIR, VerbClass.ATIR, VerbClass.LER, VerbClass.VER, VerbClass.RIR -> arrayOf(impIndStem + "ia", impIndStem + "ias", impIndStem + "ia", impIndStem + "íamos", impIndStem + "íeis", impIndStem + "iam")
            VerbClass.TER, VerbClass.VIR -> arrayOf(impIndStem + "inha", impIndStem + "inhas", impIndStem + "inha", impIndStem + "ínhamos", impIndStem + "ínheis", impIndStem + "inham")
            VerbClass.AIR, VerbClass.OER, VerbClass.TUIR, VerbClass.BUIR, VerbClass.STRUIR -> arrayOf(impIndStem + "a", impIndStem + "as", impIndStem + "a", impIndStem + "amos", impIndStem + "eis", impIndStem + "am")
            VerbClass.POR, VerbClass.PÔR -> arrayOf(impIndStem + "unha", impIndStem + "unhas", impIndStem + "unha", impIndStem + "únhamos", impIndStem + "únheis", impIndStem + "unham")
            VerbClass.ESTAR -> arrayOf(impIndStem + "ava", impIndStem + "avas", impIndStem + "ava", impIndStem + "ávamos", impIndStem + "áveis", impIndStem + "avam")
            VerbClass.GEAR, VerbClass.NEVAR -> arrayOf(null, null, impIndStem, null, null, null)
            VerbClass.SER -> arrayOf(impIndStem + "era", impIndStem + "eras", impIndStem + "era", impIndStem + "éramos", impIndStem + "éreis", impIndStem + "eram")
            VerbClass.PRAZER -> arrayOf("{d}" + impIndStem + "ia", "{d}" + impIndStem + "ias", impIndStem + "ia", "{d}" + impIndStem + "íamos", "{d}" + impIndStem + "íeis", "{d}" + impIndStem + "iam")
            VerbClass.DOER -> arrayOf("{d}" + impIndStem + "a", "{d}" + impIndStem + "as", impIndStem + "a", "{d}" + impIndStem + "amos", "{d}" + impIndStem + "eis", impIndStem + "am")
        }
    }

    private fun conjugatePretInd(verbClass: VerbClass, pretIndStem: String): Array<String?> {
        return when (verbClass) {
            VerbClass.REG_AR, VerbClass.EAR, VerbClass.SAUDAR, VerbClass.IAR, VerbClass.OIAR -> arrayOf(pretIndStem + "ei", pretIndStem + "aste", pretIndStem + "ou", pretIndStem + "amos/" + pretIndStem + "ámos", pretIndStem + "astes", pretIndStem + "aram")
            VerbClass.REG_ER, VerbClass.CRER, VerbClass.VALER, VerbClass.PERDER, VerbClass.LER -> arrayOf(pretIndStem + "i", pretIndStem + "este", pretIndStem + "eu", pretIndStem + "emos", pretIndStem + "estes", pretIndStem + "eram")
            VerbClass.REG_IR, VerbClass.ABRIR, VerbClass.SEGUIR, VerbClass.COBRIR, VerbClass.GREDIR, VerbClass.EDIR, VerbClass.ENTIR, VerbClass.DELIR, VerbClass.ELIR, VerbClass.ERIR, VerbClass.OIBIR, VerbClass.VESTIR, VerbClass.ERVIR, VerbClass.OUVIR, VerbClass.TOSSIR, VerbClass.ENGOLIR, VerbClass.FUGIR, VerbClass.DIVERTIR, VerbClass.SERVIR, VerbClass.GANIR, VerbClass.REUNIR, VerbClass.REMIR, VerbClass.DORMIR, VerbClass.OLIR, VerbClass.BARRIR, VerbClass.DEF_ORIR, VerbClass.DESPIR, VerbClass.FALIR, VerbClass.ODIR, VerbClass.ATIR, VerbClass.VER, VerbClass.RIR -> arrayOf(pretIndStem, pretIndStem + "ste", pretIndStem + "u", pretIndStem + "mos", pretIndStem + "stes", pretIndStem + "ram")
            VerbClass.TER, VerbClass.ESTAR -> arrayOf(pretIndStem + "ive", pretIndStem + "iveste", pretIndStem + "eve", pretIndStem + "ivemos", pretIndStem + "ivestes", pretIndStem + "iveram")
            VerbClass.FAZER -> arrayOf(pretIndStem + "iz", pretIndStem + "izeste", pretIndStem + "ez", pretIndStem + "izemos", pretIndStem + "izestes", pretIndStem + "izeram")
            VerbClass.AIR, VerbClass.TUIR, VerbClass.BUIR, VerbClass.STRUIR -> arrayOf(pretIndStem + "í", pretIndStem + "íste", pretIndStem + "iu", pretIndStem + "ímos", pretIndStem + "ístes", pretIndStem + "íram")
            VerbClass.POR, VerbClass.PÔR -> arrayOf(pretIndStem + "us", pretIndStem + "useste", pretIndStem + "ôs", pretIndStem + "usemos", pretIndStem + "usestes", pretIndStem + "useram")
            VerbClass.DIZER, VerbClass.CABER, VerbClass.HAVER, VerbClass.SABER, VerbClass.TRAZER -> arrayOf(pretIndStem, pretIndStem + "ste", pretIndStem, pretIndStem + "mos", pretIndStem + "stes", pretIndStem + "ram")
            VerbClass.QUERER -> arrayOf(pretIndStem, pretIndStem + "este", pretIndStem, pretIndStem + "emos", pretIndStem + "estes", pretIndStem + "eram")
            VerbClass.OER -> arrayOf(pretIndStem + "í", pretIndStem + "este", pretIndStem + "eu", pretIndStem + "emos", pretIndStem + "estes", pretIndStem + "eram")
            VerbClass.GEAR, VerbClass.NEVAR -> arrayOf(null, null, pretIndStem, null, null, null)
            VerbClass.IR, VerbClass.SER -> arrayOf(pretIndStem + "ui", pretIndStem + "oste", pretIndStem + "oi", pretIndStem + "omos", pretIndStem + "ostes", pretIndStem + "oram")
            VerbClass.PODER -> arrayOf(pretIndStem + "ude", pretIndStem + "udeste", pretIndStem + "ôde", pretIndStem + "udemos", pretIndStem + "udestes", pretIndStem + "uderam")
            VerbClass.PROVER -> arrayOf(pretIndStem + "i", pretIndStem + "iste", pretIndStem + "eu", pretIndStem + "emos", pretIndStem + "estes", pretIndStem + "eram")
            VerbClass.PRAZER -> arrayOf("{d}$pretIndStem", "{d}" + pretIndStem + "ste", pretIndStem, "{d}" + pretIndStem + "mos", "{d}" + pretIndStem + "stes", "{d}" + pretIndStem + "ram")
            VerbClass.DOER -> arrayOf("{d}" + pretIndStem + "í", "{d}" + pretIndStem + "este", pretIndStem + "eu", "{d}" + pretIndStem + "emos", "{d}" + pretIndStem + "estes", pretIndStem + "eram")
            VerbClass.VIR -> arrayOf(pretIndStem + "im", pretIndStem + "ieste", pretIndStem + "eio", pretIndStem + "iemos", pretIndStem + "iestes", pretIndStem + "ieram")
            VerbClass.DAR -> arrayOf(pretIndStem + "i", pretIndStem + "ste", pretIndStem + "u", pretIndStem + "mos", pretIndStem + "stes", pretIndStem + "ram")
        }
    }

    private fun conjugateSimpPlupInd(verbClass: VerbClass, plupIndStem: String): Array<String?> {
        return when (verbClass) {
            VerbClass.REG_AR, VerbClass.EAR, VerbClass.SAUDAR, VerbClass.IAR, VerbClass.OIAR -> arrayOf(plupIndStem + "ara", plupIndStem + "aras", plupIndStem + "ara", plupIndStem + "áramos", plupIndStem + "áreis", plupIndStem + "aram")
            VerbClass.REG_ER, VerbClass.CRER, VerbClass.VALER, VerbClass.OER, VerbClass.PERDER, VerbClass.PROVER, VerbClass.LER -> arrayOf(plupIndStem + "era", plupIndStem + "eras", plupIndStem + "era", plupIndStem + "êramos", plupIndStem + "êreis", plupIndStem + "eram")
            VerbClass.REG_IR, VerbClass.ABRIR, VerbClass.SEGUIR, VerbClass.COBRIR, VerbClass.GREDIR, VerbClass.EDIR, VerbClass.ENTIR, VerbClass.DELIR, VerbClass.ELIR, VerbClass.ERIR, VerbClass.OIBIR, VerbClass.VESTIR, VerbClass.ERVIR, VerbClass.OUVIR, VerbClass.TOSSIR, VerbClass.ENGOLIR, VerbClass.FUGIR, VerbClass.DIVERTIR, VerbClass.SERVIR, VerbClass.GANIR, VerbClass.REUNIR, VerbClass.REMIR, VerbClass.DORMIR, VerbClass.OLIR, VerbClass.BARRIR, VerbClass.DEF_ORIR, VerbClass.DESPIR, VerbClass.FALIR, VerbClass.ODIR, VerbClass.ATIR, VerbClass.VER, VerbClass.RIR -> arrayOf(plupIndStem + "ira", plupIndStem + "iras", plupIndStem + "ira", plupIndStem + "íramos", plupIndStem + "íreis", plupIndStem + "iram")
            VerbClass.TER, VerbClass.FAZER, VerbClass.POR, VerbClass.DIZER, VerbClass.QUERER, VerbClass.CABER, VerbClass.ESTAR, VerbClass.HAVER, VerbClass.PODER, VerbClass.PÔR, VerbClass.TRAZER, VerbClass.VIR, VerbClass.DAR -> arrayOf(plupIndStem + "era", plupIndStem + "eras", plupIndStem + "era", plupIndStem + "éramos", plupIndStem + "éreis", plupIndStem + "eram")
            VerbClass.AIR, VerbClass.TUIR, VerbClass.BUIR, VerbClass.STRUIR -> arrayOf(plupIndStem + "a", plupIndStem + "as", plupIndStem + "a", plupIndStem + "amos", plupIndStem + "eis", plupIndStem + "am")
            VerbClass.GEAR, VerbClass.NEVAR -> arrayOf(null, null, plupIndStem, null, null, null)
            VerbClass.IR, VerbClass.SER -> arrayOf(plupIndStem + "ora", plupIndStem + "oras", plupIndStem + "ora", plupIndStem + "ôramos", plupIndStem + "ôreis", plupIndStem + "oram")
            VerbClass.SABER -> arrayOf(plupIndStem + "era", plupIndStem + "eras", plupIndStem + "era", plupIndStem + "éramos", plupIndStem + "éreis", plupIndStem + "éram")
            VerbClass.PRAZER -> arrayOf("{d}" + plupIndStem + "era", "{d}" + plupIndStem + "eras", plupIndStem + "era", "{d}" + plupIndStem + "éramos", "{d}" + plupIndStem + "éreis",
                    "{d}" + plupIndStem + "eram")
            VerbClass.DOER -> arrayOf("{d}" + plupIndStem + "era", "{d}" + plupIndStem + "eras", plupIndStem + "era", "{d}" + plupIndStem + "êramos", "{d}" + plupIndStem + "êreis", plupIndStem + "eram")
        }
    }

    private fun conjugateFutInd(verbClass: VerbClass, futIndStem: String): Array<String?> {
        return when (verbClass) {
            VerbClass.REG_AR, VerbClass.REG_ER, VerbClass.REG_IR, VerbClass.ESTAR, VerbClass.TER, VerbClass.FAZER, VerbClass.AIR, VerbClass.ABRIR, VerbClass.POR, VerbClass.DIZER, VerbClass.SEGUIR, VerbClass.CRER, VerbClass.COBRIR, VerbClass.QUERER, VerbClass.VALER, VerbClass.GREDIR, VerbClass.EDIR, VerbClass.ENTIR, VerbClass.DELIR, VerbClass.ELIR, VerbClass.ERIR, VerbClass.EAR, VerbClass.OIBIR, VerbClass.OER, VerbClass.VESTIR, VerbClass.TUIR, VerbClass.BUIR, VerbClass.STRUIR, VerbClass.ERVIR, VerbClass.CABER, VerbClass.HAVER, VerbClass.IR, VerbClass.OUVIR, VerbClass.PERDER, VerbClass.PODER, VerbClass.PÔR, VerbClass.PROVER, VerbClass.SABER, VerbClass.SER, VerbClass.TOSSIR, VerbClass.TRAZER, VerbClass.ENGOLIR, VerbClass.FUGIR, VerbClass.DIVERTIR, VerbClass.SERVIR, VerbClass.SAUDAR, VerbClass.GANIR, VerbClass.REUNIR, VerbClass.REMIR, VerbClass.DORMIR, VerbClass.OLIR, VerbClass.BARRIR, VerbClass.DEF_ORIR, VerbClass.DESPIR, VerbClass.FALIR, VerbClass.ODIR, VerbClass.ATIR, VerbClass.VIR, VerbClass.IAR, VerbClass.DAR, VerbClass.LER, VerbClass.VER, VerbClass.RIR, VerbClass.OIAR -> arrayOf(futIndStem + "ei", futIndStem + "ás", futIndStem + "á", futIndStem + "emos", futIndStem + "eis", futIndStem + "ão")
            VerbClass.GEAR, VerbClass.NEVAR -> arrayOf(null, null, futIndStem, null, null, null)
            VerbClass.PRAZER -> arrayOf("{d}" + futIndStem + "ei", "{d}" + futIndStem + "ás", futIndStem + "á", "{d}" + futIndStem + "emos", "{d}" + futIndStem + "eis", "{d}" + futIndStem + "ão")
            VerbClass.DOER -> arrayOf("{d}" + futIndStem + "ei", "{d}" + futIndStem + "ás", futIndStem + "á", "{d}" + futIndStem + "emos", "{d}" + futIndStem + "eis", futIndStem + "ão")
        }
    }

    private fun conjugateCondInd(verbClass: VerbClass, condIndStem: String): Array<String?> {
        return when (verbClass) {
            VerbClass.REG_AR, VerbClass.REG_ER, VerbClass.REG_IR, VerbClass.TER, VerbClass.FAZER, VerbClass.AIR, VerbClass.ABRIR, VerbClass.POR, VerbClass.DIZER, VerbClass.SEGUIR, VerbClass.CRER, VerbClass.COBRIR, VerbClass.QUERER, VerbClass.VALER, VerbClass.GREDIR, VerbClass.EDIR, VerbClass.ENTIR, VerbClass.DELIR, VerbClass.ELIR, VerbClass.ERIR, VerbClass.EAR, VerbClass.OIBIR, VerbClass.OER, VerbClass.VESTIR, VerbClass.TUIR, VerbClass.BUIR, VerbClass.STRUIR, VerbClass.ERVIR, VerbClass.CABER, VerbClass.ESTAR, VerbClass.HAVER, VerbClass.IR, VerbClass.OUVIR, VerbClass.PERDER, VerbClass.PODER, VerbClass.PÔR, VerbClass.PROVER, VerbClass.SABER, VerbClass.SER, VerbClass.TOSSIR, VerbClass.TRAZER, VerbClass.ENGOLIR, VerbClass.FUGIR, VerbClass.DIVERTIR, VerbClass.SERVIR, VerbClass.SAUDAR, VerbClass.GANIR, VerbClass.REUNIR, VerbClass.REMIR, VerbClass.DORMIR, VerbClass.OLIR, VerbClass.BARRIR, VerbClass.DEF_ORIR, VerbClass.DESPIR, VerbClass.FALIR, VerbClass.ODIR, VerbClass.ATIR, VerbClass.VIR, VerbClass.IAR, VerbClass.DAR, VerbClass.LER, VerbClass.VER, VerbClass.RIR, VerbClass.OIAR -> arrayOf(condIndStem + "ia", condIndStem + "ias", condIndStem + "ia", condIndStem + "íamos", condIndStem + "íeis", condIndStem + "iam")
            VerbClass.GEAR, VerbClass.NEVAR -> arrayOf(null, null, condIndStem, null, null, null)
            VerbClass.PRAZER -> arrayOf("{d}" + condIndStem + "ia", "{d}" + condIndStem + "ias", condIndStem + "ia", "{d}" + condIndStem + "íamos", "{d}" + condIndStem + "íeis",
                    "{d}" + condIndStem + "iam")
            VerbClass.DOER -> arrayOf("{d}" + condIndStem + "ia", "{d}" + condIndStem + "ias", condIndStem + "ia", "{d}" + condIndStem + "íamos", "{d}" + condIndStem + "íeis", condIndStem + "iam")
        }
    }

    private fun conjugatePresSubj(verbClass: VerbClass, presSubjStem: String): Array<String?> {
        return when (verbClass) {
            VerbClass.REG_AR, VerbClass.REG_ER, VerbClass.REG_IR, VerbClass.TER, VerbClass.FAZER, VerbClass.AIR, VerbClass.ABRIR, VerbClass.POR, VerbClass.DIZER, VerbClass.SEGUIR, VerbClass.CRER, VerbClass.COBRIR, VerbClass.QUERER, VerbClass.VALER, VerbClass.GREDIR, VerbClass.EDIR, VerbClass.ENTIR, VerbClass.ELIR, VerbClass.ERIR, VerbClass.OIBIR, VerbClass.OER, VerbClass.VESTIR, VerbClass.TUIR, VerbClass.BUIR, VerbClass.STRUIR, VerbClass.ERVIR, VerbClass.CABER, VerbClass.ESTAR, VerbClass.HAVER, VerbClass.OUVIR, VerbClass.PERDER, VerbClass.PODER, VerbClass.PÔR, VerbClass.PROVER, VerbClass.SABER, VerbClass.SER, VerbClass.TOSSIR, VerbClass.TRAZER, VerbClass.ENGOLIR, VerbClass.FUGIR, VerbClass.DIVERTIR, VerbClass.SERVIR, VerbClass.REUNIR, VerbClass.DORMIR, VerbClass.VIR, VerbClass.LER, VerbClass.VER, VerbClass.RIR, VerbClass.OIAR -> arrayOf(presSubjStem, presSubjStem + "s", presSubjStem, presSubjStem + "mos", presSubjStem + "is", presSubjStem + "m")
            VerbClass.DELIR, VerbClass.DESPIR, VerbClass.FALIR -> arrayOf(null, null, null, null, null, null)
            VerbClass.EAR -> arrayOf(presSubjStem + "ie", presSubjStem + "ies", presSubjStem + "ie", presSubjStem + "emos", presSubjStem + "eis", presSubjStem + "iem")
            VerbClass.GEAR, VerbClass.NEVAR -> arrayOf(null, null, presSubjStem, null, null, null)
            VerbClass.IR -> arrayOf(presSubjStem + "á", presSubjStem + "ás", presSubjStem + "á", presSubjStem + "amos", presSubjStem + "ades", presSubjStem + "ão")
            VerbClass.SAUDAR -> arrayOf(presSubjStem + "úde", presSubjStem + "údes", presSubjStem + "úde", presSubjStem + "udemos", presSubjStem + "udeis", presSubjStem + "údem")
            VerbClass.GANIR, VerbClass.OLIR, VerbClass.BARRIR, VerbClass.DEF_ORIR, VerbClass.ODIR, VerbClass.ATIR -> arrayOf("{d}$presSubjStem", "{d}" + presSubjStem + "s", "{d}$presSubjStem", "{d}" + presSubjStem + "mos", "{d}" + presSubjStem + "is", "{d}" + presSubjStem + "m")
            VerbClass.REMIR -> arrayOf("{d}" + presSubjStem + "/" + presSubjStem + "s/" + presSubjStem + "/" + presSubjStem + "mos/" + presSubjStem + "is/" + presSubjStem + "m")
            VerbClass.PRAZER -> arrayOf("{d}$presSubjStem", "{d}" + presSubjStem + "s", presSubjStem, "{d}" + presSubjStem + "mos", "{d}" + presSubjStem + "is", "{d}" + presSubjStem + "m")
            VerbClass.DOER -> arrayOf("{d}$presSubjStem", "{d}" + presSubjStem + "s", presSubjStem, "{d}" + presSubjStem + "mos", "{d}" + presSubjStem + "is", presSubjStem + "m")
            VerbClass.IAR -> arrayOf(presSubjStem + "eie", presSubjStem + "eies", presSubjStem + "eie", presSubjStem + "iemos", presSubjStem + "ieis", presSubjStem + "eiem")
            VerbClass.DAR -> arrayOf(presSubjStem + "ê", presSubjStem + "ês", presSubjStem + "ê", presSubjStem + "emos", presSubjStem + "eis", presSubjStem + "eem")
        }
    }

    private fun conjugateImpSubj(verbClass: VerbClass, impSubjStem: String): Array<String?> {
        println(verbClass)
        return when (verbClass) {
            VerbClass.REG_AR, VerbClass.EAR, VerbClass.SAUDAR, VerbClass.IAR, VerbClass.OIAR -> arrayOf(impSubjStem + "asse", impSubjStem + "asses", impSubjStem + "asse", impSubjStem + "ássemos", impSubjStem + "ásseis", impSubjStem + "assem")
            VerbClass.REG_ER, VerbClass.CRER, VerbClass.VALER, VerbClass.OER, VerbClass.PERDER, VerbClass.PROVER, VerbClass.LER -> arrayOf(impSubjStem + "esse", impSubjStem + "esses", impSubjStem + "esse", impSubjStem + "êssemos", impSubjStem + "êsseis", impSubjStem + "essem")
            VerbClass.REG_IR, VerbClass.ABRIR, VerbClass.SEGUIR, VerbClass.COBRIR, VerbClass.GREDIR, VerbClass.EDIR, VerbClass.ENTIR, VerbClass.DELIR, VerbClass.ELIR, VerbClass.ERIR, VerbClass.OIBIR, VerbClass.VESTIR, VerbClass.ERVIR, VerbClass.OUVIR, VerbClass.TOSSIR, VerbClass.ENGOLIR, VerbClass.FUGIR, VerbClass.DIVERTIR, VerbClass.SERVIR, VerbClass.GANIR, VerbClass.REUNIR, VerbClass.REMIR, VerbClass.DORMIR, VerbClass.OLIR, VerbClass.BARRIR, VerbClass.DEF_ORIR, VerbClass.DESPIR, VerbClass.FALIR, VerbClass.ODIR, VerbClass.ATIR, VerbClass.VER, VerbClass.RIR -> arrayOf(impSubjStem + "isse", impSubjStem + "isses", impSubjStem + "isse", impSubjStem + "íssemos", impSubjStem + "ísseis", impSubjStem + "issem")
            VerbClass.TER, VerbClass.FAZER, VerbClass.POR, VerbClass.DIZER, VerbClass.QUERER, VerbClass.CABER, VerbClass.ESTAR, VerbClass.HAVER, VerbClass.PODER, VerbClass.PÔR, VerbClass.SABER, VerbClass.TRAZER, VerbClass.VIR, VerbClass.DAR -> arrayOf(impSubjStem + "esse", impSubjStem + "esses", impSubjStem + "esse", impSubjStem + "éssemos", impSubjStem + "ésseis", impSubjStem + "essem")
            VerbClass.AIR, VerbClass.TUIR, VerbClass.BUIR, VerbClass.STRUIR -> arrayOf(impSubjStem, impSubjStem + "s", impSubjStem, impSubjStem + "mos", impSubjStem + "is", impSubjStem + "m")
            VerbClass.GEAR, VerbClass.NEVAR -> arrayOf(null, null, impSubjStem, null, null, null)
            VerbClass.IR, VerbClass.SER -> arrayOf(impSubjStem + "osse", impSubjStem + "osses", impSubjStem + "osse", impSubjStem + "ôssemos", impSubjStem + "ôsseis", impSubjStem + "ossem")
            VerbClass.PRAZER -> arrayOf("{d}" + impSubjStem + "esse", "{d}" + impSubjStem + "esses", impSubjStem + "esse", "{d}" + impSubjStem + "éssemos", "{d}" + impSubjStem + "ésseis",
                    "{d}" + impSubjStem + "essem")
            VerbClass.DOER -> arrayOf("{d}" + impSubjStem + "esse", "{d}" + impSubjStem + "esses", impSubjStem + "esse", "{d}" + impSubjStem + "êssemos", "{d}" + impSubjStem + "êsseis",
                    impSubjStem + "essem")
        }
    }

    private fun conjugateFutSubj(verbClass: VerbClass, futSubjStem: String): Array<String?> {
        return when (verbClass) {
            VerbClass.REG_AR, VerbClass.REG_ER, VerbClass.REG_IR, VerbClass.TER, VerbClass.FAZER, VerbClass.ABRIR, VerbClass.POR, VerbClass.DIZER, VerbClass.SEGUIR, VerbClass.CRER, VerbClass.COBRIR, VerbClass.QUERER, VerbClass.VALER, VerbClass.GREDIR, VerbClass.EDIR, VerbClass.ENTIR, VerbClass.DELIR, VerbClass.ELIR, VerbClass.ERIR, VerbClass.EAR, VerbClass.OIBIR, VerbClass.OER, VerbClass.VESTIR, VerbClass.ERVIR, VerbClass.CABER, VerbClass.ESTAR, VerbClass.HAVER, VerbClass.IR, VerbClass.OUVIR, VerbClass.PERDER, VerbClass.PODER, VerbClass.PÔR, VerbClass.PROVER, VerbClass.SABER, VerbClass.SER, VerbClass.TOSSIR, VerbClass.TRAZER, VerbClass.ENGOLIR, VerbClass.FUGIR, VerbClass.DIVERTIR, VerbClass.SERVIR, VerbClass.SAUDAR, VerbClass.GANIR, VerbClass.REUNIR, VerbClass.REMIR, VerbClass.DORMIR, VerbClass.OLIR, VerbClass.BARRIR, VerbClass.DEF_ORIR, VerbClass.DESPIR, VerbClass.FALIR, VerbClass.ODIR, VerbClass.ATIR, VerbClass.VIR, VerbClass.IAR, VerbClass.DAR, VerbClass.LER, VerbClass.VER, VerbClass.RIR, VerbClass.OIAR -> arrayOf(futSubjStem, futSubjStem + "es", futSubjStem, futSubjStem + "mos", futSubjStem + "des", futSubjStem + "em")
            VerbClass.AIR, VerbClass.TUIR, VerbClass.BUIR, VerbClass.STRUIR -> arrayOf(futSubjStem + "ir", futSubjStem + "íres", futSubjStem + "ir", futSubjStem + "irmos", futSubjStem + "irdes", futSubjStem + "írem")
            VerbClass.GEAR, VerbClass.NEVAR -> arrayOf(null, null, futSubjStem, null, null, null)
            VerbClass.PRAZER -> arrayOf("{d}$futSubjStem", "{d}" + futSubjStem + "es", futSubjStem, "{d}" + futSubjStem + "mos", "{d}" + futSubjStem + "des", "{d}" + futSubjStem + "em")
            VerbClass.DOER -> arrayOf("{d}$futSubjStem", "{d}" + futSubjStem + "es", futSubjStem, "{d}" + futSubjStem + "mos", "{d}" + futSubjStem + "des", futSubjStem + "em")
        }
    }

    private fun conjugateImpAff(infinitive: String, verbClass: VerbClass, impAffStem: String): Array<String?> {
        return when (verbClass) {
            VerbClass.REG_AR -> arrayOf(null, impAffStem + "a", impAffStem + "e", impAffStem + "emos", impAffStem + "ai", impAffStem + "em")
            VerbClass.REG_ER -> arrayOf(null, impAffStem + "e", impAffStem + "a", impAffStem + "amos", impAffStem + "ei", impAffStem + "am")
            VerbClass.REG_IR, VerbClass.ABRIR, VerbClass.OIBIR -> arrayOf(null, impAffStem + "e", impAffStem + "a", impAffStem + "amos", impAffStem + "i", impAffStem + "am")
            VerbClass.TER -> arrayOf(null, if (infinitive == "ter") impAffStem + "em" else impAffStem + "ém", impAffStem + "enha", impAffStem + "enhamos", impAffStem + "ende", impAffStem + "enham")
            VerbClass.FAZER -> arrayOf(null, impAffStem + "z/" + impAffStem + "ze", impAffStem + "ça", impAffStem + "çamos", impAffStem + "zei", impAffStem + "çam")
            VerbClass.AIR -> arrayOf(null, impAffStem + "i", impAffStem + "ia", impAffStem + "iamos", impAffStem + "í", impAffStem + "iam")
            VerbClass.POR, VerbClass.PÔR -> arrayOf(null, impAffStem + "õe", impAffStem + "onha", impAffStem + "onhamos", impAffStem + "onde", impAffStem + "onham")
            VerbClass.DIZER -> arrayOf(null, impAffStem + "z/" + impAffStem + "ze", impAffStem + "ga", impAffStem + "gamos", impAffStem + "zei", impAffStem + "gam")
            VerbClass.SEGUIR -> arrayOf(null, impAffStem + "egue", impAffStem + "iga", impAffStem + "igamos", impAffStem + "egui", impAffStem + "igam")
            VerbClass.CRER, VerbClass.LER -> arrayOf(null, impAffStem + "ê", impAffStem + "eia", impAffStem + "eiamos", impAffStem + "ede", impAffStem + "eiam")
            VerbClass.COBRIR -> arrayOf(null, impAffStem + "obre", impAffStem + "ubra", impAffStem + "ubramos", impAffStem + "obri", impAffStem + "ubram")
            VerbClass.QUERER -> arrayOf(null, impAffStem + "r", impAffStem + "ira", impAffStem + "iramos", impAffStem + "rei", impAffStem + "iram")
            VerbClass.VALER -> arrayOf(null, impAffStem + "e", impAffStem + "ha", impAffStem + "hamos", impAffStem + "ei", impAffStem + "ham")
            VerbClass.GREDIR -> arrayOf(null, impAffStem + "ide", impAffStem + "ida", impAffStem + "idamos", impAffStem + "edi", impAffStem + "idam")
            VerbClass.EDIR -> arrayOf(null, impAffStem + "de", impAffStem + "ça", impAffStem + "çamos", impAffStem + "di", impAffStem + "çam")
            VerbClass.ENTIR -> arrayOf(null, impAffStem + "ente", impAffStem + "inta", impAffStem + "intamos", impAffStem + "enti", impAffStem + "intam")
            VerbClass.DELIR, VerbClass.DESPIR -> arrayOf(null, impAffStem + "e", null, null, impAffStem + "i", null)
            VerbClass.ELIR -> arrayOf(null, impAffStem + "ele", impAffStem + "ila", impAffStem + "ilamos", impAffStem + "eli", impAffStem + "ilam")
            VerbClass.ERIR -> arrayOf(null, impAffStem + "ere", impAffStem + "ira", impAffStem + "iramos", impAffStem + "eri", impAffStem + "iram")
            VerbClass.EAR -> arrayOf(null, impAffStem + "ia", impAffStem + "ie", impAffStem + "emos", impAffStem + "ai", impAffStem + "iem")
            VerbClass.OER -> arrayOf(null, impAffStem + "ói", impAffStem + "oa", impAffStem + "oamos", impAffStem + "oei", impAffStem + "oam")
            VerbClass.VESTIR -> arrayOf(null, impAffStem + "este", impAffStem + "ista", impAffStem + "istamos", impAffStem + "esti", impAffStem + "istam")
            VerbClass.TUIR, VerbClass.BUIR, VerbClass.STRUIR -> arrayOf(null, impAffStem + "i", impAffStem + "a", impAffStem + "amos", impAffStem + "í", impAffStem + "am")
            VerbClass.ERVIR, VerbClass.SERVIR -> arrayOf(null, impAffStem + "erve", impAffStem + "irva", impAffStem + "irvamos", impAffStem + "ervi", impAffStem + "irvam")
            VerbClass.CABER, VerbClass.SABER -> arrayOf(null, impAffStem + "be", impAffStem + "iba", impAffStem + "ibamos", impAffStem + "bei", impAffStem + "ibam")
            VerbClass.ESTAR -> arrayOf(null, impAffStem + "á", impAffStem + "eja", impAffStem + "ejamos", impAffStem + "ai", impAffStem + "ejam")
            VerbClass.GEAR, VerbClass.NEVAR -> arrayOf(null, null, impAffStem, null, null, null)
            VerbClass.HAVER -> arrayOf(null, impAffStem + "á", impAffStem + "aja", impAffStem + "ajamos", impAffStem + "avei", impAffStem + "ajam")
            VerbClass.IR -> arrayOf(null, impAffStem + "vai", impAffStem + "vá", impAffStem + "vamos", impAffStem + "ide", impAffStem + "vão")
            VerbClass.OUVIR -> arrayOf(null, impAffStem + "ve", impAffStem + "ça", impAffStem + "çamos", impAffStem + "vi", impAffStem + "çam")
            VerbClass.PERDER -> arrayOf(null, impAffStem + "de", impAffStem + "ca", impAffStem + "camos", impAffStem + "dei", impAffStem + "cam")
            VerbClass.PODER -> arrayOf(null, impAffStem + "de", impAffStem + "ssa", impAffStem + "ssamos", impAffStem + "dei", impAffStem + "ssam")
            VerbClass.PROVER, VerbClass.SER, VerbClass.VER -> arrayOf(null, impAffStem + "ê", impAffStem + "eja", impAffStem + "ejamos", impAffStem + "ede", impAffStem + "ejam")
            VerbClass.TOSSIR -> arrayOf(null, impAffStem + "osse", impAffStem + "ussa", impAffStem + "ussamos", impAffStem + "ossi", impAffStem + "ussam")
            VerbClass.TRAZER -> arrayOf(null, impAffStem + "z", impAffStem + "ga", impAffStem + "gamos", impAffStem + "zei", impAffStem + "gam")
            VerbClass.ENGOLIR -> arrayOf(null, impAffStem + "ole", impAffStem + "ula", impAffStem + "ulamos", impAffStem + "oli", impAffStem + "ulam")
            VerbClass.FUGIR -> arrayOf(null, impAffStem + "oge", impAffStem + "uja", impAffStem + "ujamos", impAffStem + "ugi", impAffStem + "ujam")
            VerbClass.DIVERTIR -> arrayOf(null, impAffStem + "erte", impAffStem + "irta", impAffStem + "irtamos", impAffStem + "erti", impAffStem + "irtam")
            VerbClass.SAUDAR -> arrayOf(null, impAffStem + "úda", impAffStem + "úde", impAffStem + "udemos", impAffStem + "udai", impAffStem + "údem")
            VerbClass.GANIR -> arrayOf("{d}" + impAffStem + "gana", impAffStem + "gane/" + impAffStem + "Normally defective:/" + impAffStem + "ganas", "{d}" + impAffStem + "gana",
                    "{d}" + impAffStem + "ganamos", impAffStem + "gani/" + impAffStem + "Normally defective:/" + impAffStem + "ganais", "{d}" + impAffStem + "ganam")
            VerbClass.REUNIR -> arrayOf(null, impAffStem + "úne", impAffStem + "úna", impAffStem + "únamos", impAffStem + "uni", impAffStem + "únam")
            VerbClass.REMIR -> arrayOf("{d}" + impAffStem + "redima",
                    "{d}" + impAffStem + "redimas/" + impAffStem + "redima/" + impAffStem + "redimamos/" + impAffStem + "remi/" + impAffStem + "Normally defective:/" + impAffStem + "redimais",
                    "{d}" + impAffStem + "redimam")
            VerbClass.DORMIR -> arrayOf(null, impAffStem + "orme", impAffStem + "urma", impAffStem + "urmamos", impAffStem + "ormi", impAffStem + "urmam")
            VerbClass.OLIR -> arrayOf("{d}" + impAffStem + "ula", "{d}" + impAffStem + "ulas", "{d}" + impAffStem + "ula", "{d}" + impAffStem + "ulamos",
                    impAffStem + "oli/" + impAffStem + "Normally defective:/" + impAffStem + "ulais", "{d}" + impAffStem + "ulam")
            VerbClass.PRAZER -> arrayOf("{d}$impAffStem", "{d}" + impAffStem + "s", impAffStem, "{d}" + impAffStem + "mos", "{d}" + impAffStem + "is", "{d}" + impAffStem + "m")
            VerbClass.BARRIR -> arrayOf("{d}" + impAffStem + "barra", impAffStem + "barre/" + impAffStem + "Normally defective:/" + impAffStem + "barras", "{d}" + impAffStem + "barra",
                    "{d}" + impAffStem + "barramos", impAffStem + "barri/" + impAffStem + "Normally defective:/" + impAffStem + "barrais", "{d}" + impAffStem + "barram")
            VerbClass.DEF_ORIR -> arrayOf("{d}" + impAffStem + "colora", "{d}" + impAffStem + "coloras", "{d}" + impAffStem + "colora", "{d}" + impAffStem + "coloramos",
                    impAffStem + "colori/" + impAffStem + "Normally defective:/" + impAffStem + "colorais", "{d}" + impAffStem + "coloram")
            VerbClass.DOER -> arrayOf("{d}$impAffStem", "{d}" + impAffStem + "s", impAffStem, "{d}" + impAffStem + "mos", "{d}" + impAffStem + "is", impAffStem + "m")
            VerbClass.FALIR -> arrayOf(null, null, null, null, impAffStem, null)
            VerbClass.ODIR -> arrayOf("{d}" + impAffStem + "exploda", impAffStem + "explode/" + impAffStem + "Normally defective:/" + impAffStem + "explodas", "{d}" + impAffStem + "exploda",
                    "{d}" + impAffStem + "explodamos", impAffStem + "explodi/" + impAffStem + "Normally defective:/" + impAffStem + "explodais", "{d}" + impAffStem + "explodam")
            VerbClass.ATIR -> arrayOf("{d}" + impAffStem + "lata", impAffStem + "late/" + impAffStem + "Normally defective:/" + impAffStem + "latas", "{d}" + impAffStem + "lata",
                    "{d}" + impAffStem + "latamos", impAffStem + "lati/" + impAffStem + "Normally defective:/" + impAffStem + "latais", "{d}" + impAffStem + "latam")
            VerbClass.VIR -> arrayOf(null, impAffStem + "em", impAffStem + "enha", impAffStem + "enhamos", impAffStem + "inde", impAffStem + "enham")
            VerbClass.IAR -> arrayOf(null, impAffStem + "eia", impAffStem + "eie", impAffStem + "iemos", impAffStem + "iai", impAffStem + "eiem")
            VerbClass.DAR -> arrayOf(null, impAffStem + "á", impAffStem + "ê", impAffStem + "emos", impAffStem + "ai", impAffStem + "eem")
            VerbClass.RIR -> arrayOf(null, impAffStem, impAffStem + "a", impAffStem + "amos", impAffStem + "de", impAffStem + "am")
            VerbClass.OIAR -> arrayOf(null, impAffStem + "óia", impAffStem + "oie", impAffStem + "oiemos", impAffStem + "oiai", impAffStem + "oiem")
        }
    }

    private fun conjugateImpNeg(verbClass: VerbClass, impNegStem: String): Array<String?> {
        return when (verbClass) {
            VerbClass.REG_AR, VerbClass.REG_ER, VerbClass.REG_IR, VerbClass.TER, VerbClass.FAZER, VerbClass.AIR, VerbClass.ABRIR, VerbClass.POR, VerbClass.DIZER, VerbClass.SEGUIR, VerbClass.CRER, VerbClass.COBRIR, VerbClass.QUERER, VerbClass.VALER, VerbClass.GREDIR, VerbClass.EDIR, VerbClass.ENTIR, VerbClass.ELIR, VerbClass.ERIR, VerbClass.OIBIR, VerbClass.OER, VerbClass.VESTIR, VerbClass.TUIR, VerbClass.BUIR, VerbClass.STRUIR, VerbClass.ERVIR, VerbClass.CABER, VerbClass.ESTAR, VerbClass.HAVER, VerbClass.OUVIR, VerbClass.PERDER, VerbClass.PODER, VerbClass.PÔR, VerbClass.PROVER, VerbClass.SABER, VerbClass.SER, VerbClass.TOSSIR, VerbClass.TRAZER, VerbClass.ENGOLIR, VerbClass.FUGIR, VerbClass.DIVERTIR, VerbClass.SERVIR, VerbClass.REUNIR, VerbClass.DORMIR, VerbClass.VIR, VerbClass.LER, VerbClass.VER, VerbClass.RIR, VerbClass.OIAR -> arrayOf(null, impNegStem + "s", impNegStem, impNegStem + "mos", impNegStem + "is", impNegStem + "m")
            VerbClass.DELIR, VerbClass.DESPIR, VerbClass.FALIR -> arrayOf(null, null, null, null, null, null)
            VerbClass.EAR -> arrayOf(null, impNegStem + "ies", impNegStem + "ie", impNegStem + "emos", impNegStem + "eis", impNegStem + "iem")
            VerbClass.GEAR, VerbClass.NEVAR -> arrayOf(null, null, impNegStem, null, null, null)
            VerbClass.IR -> arrayOf(null, impNegStem + "ás", impNegStem + "á", impNegStem + "amos", impNegStem + "ades", impNegStem + "ão")
            VerbClass.SAUDAR -> arrayOf(null, impNegStem + "údes", impNegStem + "úde", impNegStem + "udemos", impNegStem + "udeis", impNegStem + "údem")
            VerbClass.GANIR, VerbClass.OLIR, VerbClass.BARRIR, VerbClass.DEF_ORIR, VerbClass.ODIR, VerbClass.ATIR -> arrayOf("{d}$impNegStem", "{d}" + impNegStem + "s", "{d}$impNegStem", "{d}" + impNegStem + "mos", "{d}" + impNegStem + "is", "{d}" + impNegStem + "m")
            VerbClass.REMIR -> arrayOf("{d}$impNegStem", "{d}" + impNegStem + "s/" + impNegStem + "/" + impNegStem + "mos/" + impNegStem + "is/" + impNegStem + "m")
            VerbClass.PRAZER -> arrayOf("{d}$impNegStem", "{d}" + impNegStem + "s", impNegStem, "{d}" + impNegStem + "mos", "{d}" + impNegStem + "is", "{d}" + impNegStem + "m")
            VerbClass.DOER -> arrayOf("{d}$impNegStem", "{d}" + impNegStem + "s", impNegStem, "{d}" + impNegStem + "mos", "{d}" + impNegStem + "is", impNegStem + "m")
            VerbClass.IAR -> arrayOf(null, impNegStem + "eies", impNegStem + "eie", impNegStem + "iemos", impNegStem + "ieis", impNegStem + "eiem")
            VerbClass.DAR -> arrayOf(null, impNegStem + "ês", impNegStem + "ê", impNegStem + "emos", impNegStem + "eis", impNegStem + "eem")
        }
    }

    private fun conjugatePersInf(verbClass: VerbClass, persInfStem: String): Array<String?> {
        return when (verbClass) {
            VerbClass.REG_AR, VerbClass.REG_ER, VerbClass.REG_IR, VerbClass.TER, VerbClass.FAZER, VerbClass.ABRIR, VerbClass.POR, VerbClass.DIZER, VerbClass.SEGUIR, VerbClass.CRER, VerbClass.COBRIR, VerbClass.QUERER, VerbClass.VALER, VerbClass.GREDIR, VerbClass.EDIR, VerbClass.ENTIR, VerbClass.DELIR, VerbClass.ELIR, VerbClass.ERIR, VerbClass.EAR, VerbClass.OIBIR, VerbClass.OER, VerbClass.VESTIR, VerbClass.ERVIR, VerbClass.CABER, VerbClass.ESTAR, VerbClass.HAVER, VerbClass.IR, VerbClass.OUVIR, VerbClass.PERDER, VerbClass.PODER, VerbClass.PROVER, VerbClass.SABER, VerbClass.SER, VerbClass.TOSSIR, VerbClass.TRAZER, VerbClass.ENGOLIR, VerbClass.FUGIR, VerbClass.DIVERTIR, VerbClass.SERVIR, VerbClass.SAUDAR, VerbClass.GANIR, VerbClass.REUNIR, VerbClass.REMIR, VerbClass.DORMIR, VerbClass.OLIR, VerbClass.BARRIR, VerbClass.DEF_ORIR, VerbClass.DESPIR, VerbClass.FALIR, VerbClass.ODIR, VerbClass.ATIR, VerbClass.VIR, VerbClass.IAR, VerbClass.DAR, VerbClass.LER, VerbClass.VER, VerbClass.RIR, VerbClass.OIAR -> arrayOf(persInfStem, persInfStem + "es", persInfStem, persInfStem + "mos", persInfStem + "des", persInfStem + "em")
            VerbClass.AIR, VerbClass.TUIR, VerbClass.BUIR, VerbClass.STRUIR -> arrayOf(persInfStem + "ir", persInfStem + "íres", persInfStem + "ir", persInfStem + "irmos", persInfStem + "irdes", persInfStem + "írem")
            VerbClass.GEAR, VerbClass.NEVAR -> arrayOf(null, null, persInfStem, null, null, null)
            VerbClass.PÔR -> arrayOf(persInfStem + "ôr", persInfStem + "ores", persInfStem + "ôr", persInfStem + "ormos", persInfStem + "ordes", persInfStem + "orem")
            VerbClass.PRAZER -> arrayOf("{d}$persInfStem", "{d}" + persInfStem + "es", persInfStem, "{d}" + persInfStem + "mos", "{d}" + persInfStem + "des", "{d}" + persInfStem + "em")
            VerbClass.DOER -> arrayOf("{d}$persInfStem", "{d}" + persInfStem + "es", persInfStem, "{d}" + persInfStem + "mos", "{d}" + persInfStem + "des", persInfStem + "em")
        }
    }

    private class VerbData constructor( val verbClass: VerbClass,  val stem: String,  val eAndIStem: String,  val aAndOStem: String,  val presIndStem: String,  val impIndStem: String,  val pretIndStem: String,  val impAffStem: String,  val plupIndStem: String,
                                                val impSubjStem: String,  val futIndStem: String,  val condIndStem: String,  val persInfStem: String,  val presSubjStem: String,  val impNegStem: String,  val futSubjStem: String, val participle: String, val gerund: String?)
}