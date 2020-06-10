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

import android.content.Context
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.sid.app.verbpractice.MainActivity
import com.sid.app.verbpractice.R
import com.sid.app.verbpractice.enums.Person
import com.sid.app.verbpractice.enums.VerbForm
import com.sid.app.verbpractice.helper.*
import kotlinx.android.synthetic.main.fragment_practice_loading.view.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


/**
 * Shows the main title screen with a button that navigates to About
 */
class PracticeLoadingFragment : Fragment(), CoroutineScope {

    private lateinit var mContext: MainActivity
    var pages: Array<Array<String>> = arrayOf()
    private var enabledVerbs:Array<String> = arrayOf()
    private var enabledEnglishVerbs:Array<String> = arrayOf()
    private var enabledTenses: Array<VerbForm> = arrayOf()
    private var isFullConjugation = true
    private var isPortugal = true

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mContext = context as MainActivity
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(
                (context.toString() +
                        " must implement ChangeIndicativeTensesDialogListener")
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_practice_loading, container, false)
        view.imageView.setImageResource(R.drawable.animated_logo)
        (view.imageView.drawable as Animatable).start()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        launch {
            //get enabled verbs
            getRandomVerbs()

            //get enabled persons
            val personsFrequencies: Array<Int> = mContext.getFrequencies().toTypedArray()
            val allPersons = arrayOf(Person.FIRST_SING, Person.SECOND_SING, Person.THIRD_SING, Person.FIRST_PLUR, Person.SECOND_PLUR, Person.THIRD_PLUR)
            var personFrequencyMap = mapOf(*allPersons.zip(personsFrequencies).toTypedArray()).filterValues { freq -> freq > 0 }
            //get enabled tenses
            enabledTenses = mContext.getAllTenses()
            isFullConjugation = mContext.getIsFullConjugation()
            isPortugal = mContext.getIsPortugal()

            if (personFrequencyMap.isEmpty()) {
                personFrequencyMap = mapOf(Person.FIRST_SING to 5, Person.THIRD_SING to 5, Person.FIRST_PLUR to 5, Person.THIRD_PLUR to 5)
            }

            if (enabledVerbs.isEmpty()) {
                enabledVerbs = arrayOf("ser", "estar", "ter")
                enabledEnglishVerbs = arrayOf(
                    "to be (denotes a permanent quality)~to be|am|are|was|been|being|to be",
                    "to be (denotes a transient quality; a quality expected to change)~to be|am|are|was|been|being|to be",
                    "to have~to have|have|has|had|had|having|to have"
                )
            }

            if (enabledTenses.isEmpty()) {
                enabledTenses = arrayOf(VerbForm.PRES_IND, VerbForm.FUT_IND, VerbForm.PRET_IND, VerbForm.IMP_IND)
            }

            val conjugatedVerbs = enabledVerbs.map { verb ->
                ConjugatorPortuguese.conjugate(verb, enabledTenses, isPortugal)
            }

            val englishVerbMap = mapOf(*enabledVerbs.zip(enabledEnglishVerbs).toTypedArray())

            val conjugatedVerbsMap = hashMapOf(*enabledVerbs.zip(conjugatedVerbs).toTypedArray()).filterValues { verb ->
                val badTenses = mutableListOf<VerbForm>()
                verb.forEach { (tense, conjugation) ->
                    val badPersons = mutableListOf<Person>()
                    conjugation.forEach { (person, conjugatedVerb) ->
                        if (conjugatedVerb == null || person !in personFrequencyMap.keys) {
                            badPersons += person
                        }
                    }
                    for (badPerson in badPersons) {
                        conjugation.remove(badPerson)
                    }
                    if (conjugation.isEmpty()) {
                        badTenses += tense
                    }
                }
                for (badTense in badTenses) {
                    verb.remove(badTense)
                }
                verb.isNotEmpty()
            }

            if (conjugatedVerbsMap.isEmpty()) {
                return@launch
            }

            val possibleConjugations = mutableListOf<PotentialConjugation>()

            for ((verb, tenseMap) in conjugatedVerbsMap) {
                Log.v("mapStuff", verb)
                for ((tense, personMap) in tenseMap) {
                    Log.v("mapStuff", """  $tense""")
                    possibleConjugations += PotentialConjugation(verb, englishVerbMap[verb] ?: verb, tense, personMap)
                }
            }

            for (conjugation in possibleConjugations) {
                Log.v("possibles", conjugation.verb + ":  " + conjugation.tense)
            }

            val selectedConjugations = mutableListOf<Conjugation>()
            if (isFullConjugation) {
                for (i in 0 until 100) {
                    val randomConjugation = possibleConjugations.random()
                    selectedConjugations += Conjugation(randomConjugation)
                    possibleConjugations -= randomConjugation
                    if (possibleConjugations.size == 0) {
                        break
                    }
                }
                for (conjugation in selectedConjugations) {
                    Log.v("conjugations", conjugation.verb + ":  " + conjugation.tense)
                }
            } else {
                for (i in 0 until 100) {
                    val randomConjugation = possibleConjugations.random()
                    val thisConjugation = Conjugation(randomConjugation)
                    val randomPerson = randomConjugation.getRandomPerson(isFullConjugation, personFrequencyMap)
                    if (!randomConjugation.isValidPick) {
                        possibleConjugations -= randomConjugation
                    }
                    thisConjugation.person = when (randomPerson) {
                        Person.FIRST_SING -> 0
                        Person.SECOND_SING -> 1
                        Person.THIRD_SING -> 2
                        Person.FIRST_PLUR -> 3
                        Person.SECOND_PLUR -> 4
                        Person.THIRD_PLUR -> 5
                        null -> -1
                    }
                    selectedConjugations += thisConjugation
                    if (possibleConjugations.size == 0) {
                        break
                    }
                }
            }


            val conjugationArrayParcel = ConjugationArrayParcel(
                selectedConjugations.map { conjugation ->
                    ConjugationParcel(
                        conjugation.verb,
                        englishVerbMap[conjugation.verb] ?: conjugation.verb,
                        conjugation.tense,
                        conjugation.personMap.keys.toTypedArray(),
                        conjugation.personMap.values.toTypedArray(),
                        conjugation.person
                    )
                }.toTypedArray()
            )

            val bundle = bundleOf("conjugations" to conjugationArrayParcel)

            withContext(Dispatchers.Main) {
                Navigation.findNavController(view).navigate(R.id.action_loading_to_practice, bundle)
            }
        }
    }

    private fun getRandomVerbs() = runBlocking {
        val task = async(Dispatchers.IO) {
            mContext.getRandomVerbs()
        }
        val verbs = task.await()
        enabledVerbs = task.await()?.map { it.verb }?.toTypedArray() ?: arrayOf()
        enabledEnglishVerbs = verbs?.map {
            val toFly = if (it.to_fly.startsWith("to ") || it.to_fly.startsWith("To ")) it.to_fly.substring(3).split(" ")[0] else it.to_fly.split(" ")[0]
            it.main_def + "~" + it.main_def.split(";")[0].split(",")[0].replace(Regex("\\(.*\\)"), "").trim() + "|" + it.fly + "|" + it.flies + "|" + it.flew + "|" + it.flown + "|" + it.flying + "|" + toFly }?.toTypedArray() ?: arrayOf()
    }

}
