package com.sid.app.verbpractice.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.Config
import androidx.paging.toLiveData
import com.sid.app.verbpractice.AppDatabase

class EnglishVerbViewModel(app: Application): AndroidViewModel(app) {
    val dao = AppDatabase.get(app).englishVerbDao()

    val allEnglishVerbs = dao.getAll().toLiveData(Config(
        pageSize = 60,
        enablePlaceholders = true,
        maxSize = 200
    ))
}

//
//class EnglishVerbViewModel(application: Application?) : AndroidViewModel(application!!) {
//    val allWords: LiveData<List<EnglishVerbEntity>>
//
//    init {
//        val mRepository = EnglishVerbRepository(application)
//        allWords = mRepository.allVerbs
//    }
//}