package com.sid.app.verbpractice.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sid.app.verbpractice.db.entity.EnglishVerb

@Dao
interface EnglishVerbDao {
    @Query("SELECT * FROM EnglishVerbs")
    fun getAll(): DataSource.Factory<Int, EnglishVerb>

    @Query("SELECT * FROM EnglishVerbs WHERE verb = :verb")
    fun findByName(verb: String): EnglishVerb

    @Insert
    fun insertAll(englishVerbs: List<EnglishVerb>)
}

//@Dao
//interface EnglishVerbDao {
//    @get:Query("SELECT * FROM EnglishVerbs")
//    val all: LiveData<List<EnglishVerbEntity?>?>?
//
//    @Query("SELECT * FROM EnglishVerbs WHERE verb = :verb")
//    fun findByName(verb: String?): EnglishVerbEntity?
//
//    @Insert
//    fun insertAll(englishVerbs: List<EnglishVerbEntity?>?)
//}