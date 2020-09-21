package com.sid.app.verbpractice.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sid.app.verbpractice.AppDatabase
import com.sid.app.verbpractice.db.entity.PortugueseVerb


class PortugueseVerbViewModel(app: Application): AndroidViewModel(app) {
    val dao = AppDatabase.get(app).portugueseVerbDao()

    val allPortugueseVerbs = dao.getAll()

//    fun getAllDefinitionsForVerb(verbId: Int) = dao.getDefinitions(verb_id = verbId)

    suspend fun resetAllChecked() = dao.resetAllChecked()

    suspend fun updateChecked(added: Int, verbId: Int) = dao.updateAdded(added = added, verb_id = verbId)

//    suspend fun updateCheckedForDefinition(added: Int, definitionId: Int) = dao.updateAddedForDefinition(added = added, definition_id = definitionId)

    suspend fun getRandomVerb(): PortugueseVerb? = dao.getRandomVerb()

    suspend fun getRandomEnabledVerbs(verbTypes: List<Int>, verbSubtypes: List<Int>): List<PortugueseVerb>? =
        dao.getRandomEnabledVerbs(verb_types = verbTypes, verb_subtypes = verbSubtypes)

    suspend fun getRandomVerbs(verbGroupMin: Int, verbTypes: List<Int>, verbSubtypes: List<Int>): List<PortugueseVerb>? =
        dao.getRandomVerbs(verb_group_min = verbGroupMin, verb_types = verbTypes, verb_subtypes = verbSubtypes)

    suspend fun getSpecificVerb(verb: String): List<PortugueseVerb>? = dao.getSpecificVerb(verb = verb)
}
//class PortugueseVerbViewModel : ViewModel() {
//    private val mRepository: PortugueseVerbRepository = PortugueseVerbRepository(application)
//    val allWords: LiveData<List<PortugueseVerbEntity?>?>
//
//    fun updateChecked(added: Int, definitionId: Int) {
//        mRepository.updateChecked(added, definitionId)
//    }
//
//    init {
//        allWords = mRepository.allVerbs
//    }
//}