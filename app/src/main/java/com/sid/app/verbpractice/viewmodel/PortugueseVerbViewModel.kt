package com.sid.app.verbpractice.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.Config
import androidx.paging.toLiveData
import com.sid.app.verbpractice.AppDatabase
import com.sid.app.verbpractice.db.entity.PortugueseVerb


class PortugueseVerbViewModel(app: Application): AndroidViewModel(app) {
    val dao = AppDatabase.get(app).portugueseVerbDao()

    val allPortugueseVerbs = dao.getAll()

    fun getAllDefinitionsForVerb(verbId: Int) = dao.getDefinitions(verb_id = verbId)

    suspend fun resetAllChecked() = dao.resetAllChecked()

    suspend fun updateChecked(added: Int, verbId: Int) = dao.updateAdded(added = added, verb_id = verbId)

    suspend fun updateCheckedForDefinition(added: Int, definitionId: Int) = dao.updateAddedForDefinition(added = added, definition_id = definitionId)

    suspend fun getRandomVerb(): PortugueseVerb? = dao.getRandomVerb()

    suspend fun getRandomVerbs(): List<PortugueseVerb>? = dao.getRandomVerbs()
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