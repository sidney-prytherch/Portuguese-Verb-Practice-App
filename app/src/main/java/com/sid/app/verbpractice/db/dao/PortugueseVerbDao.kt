package com.sid.app.verbpractice.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.sid.app.verbpractice.db.entity.PortugueseVerb

@Dao
interface PortugueseVerbDao {
    @Query("SELECT * FROM PortugueseVerbs")
    fun getAll(): LiveData<List<PortugueseVerb>>
//    fun getAll(): List<PortugueseVerb>

//    @Query("SELECT * FROM PortugueseVerbDefinitions WHERE verb_id = :verb_id")
//    fun getDefinitions(verb_id: Int): LiveData<List<PortugueseVerbDefinition>>

//    @Query("update PortugueseVerbDefinitions set added = :added where definition_id = :definition_id")
//    suspend fun updateAdded(added: Int, definition_id: Int)
    @Query("update PortugueseVerbs set added = :added where verb_id = :verb_id")
    suspend fun updateAdded(added: Int, verb_id: Int)

//    @Query("update PortugueseVerbDefinitions set added = :added where definition_id = :definition_id")
//    suspend fun updateAddedForDefinition(added: Int, definition_id: Int)

    @Query("update PortugueseVerbs set added = 0")
    suspend fun resetAllChecked()

    @Query("SELECT * FROM PortugueseVerbs WHERE added = 1 ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomVerb(): PortugueseVerb?

    @Query("SELECT * FROM PortugueseVerbs WHERE added = 1 AND verb_type IN (:verb_types) AND verb_subtype IN (:verb_subtypes) ORDER BY RANDOM() LIMIT 50")
    suspend fun getRandomEnabledVerbs(verb_types: List<Int>, verb_subtypes: List<Int>): List<PortugueseVerb>?

    @Query("SELECT * FROM PortugueseVerbs WHERE verb = :verb")
    suspend fun getSpecificVerb(verb: String): List<PortugueseVerb>?

    @Query("SELECT * FROM PortugueseVerbs WHERE verb_group > :verb_group_min AND verb_type IN (:verb_types) AND verb_subtype IN (:verb_subtypes) ORDER BY RANDOM() LIMIT 50")
    suspend fun getRandomVerbs(verb_group_min: Int, verb_types: List<Int>, verb_subtypes: List<Int>): List<PortugueseVerb>?
}

//
//@Dao
//interface PortugueseVerbDao {
//    @get:Query("SELECT * FROM PortugueseVerbs")
//    val all: LiveData<List<PortugueseVerbEntity?>?>?
//
//    @Query("SELECT * FROM PortugueseVerbDefinitions WHERE verb_id = :verb_id")
//    fun getDefinitions(verb_id: Int): LiveData<List<PortugueseVerbDefinitionEntity?>?>?
//
//    @Query("SELECT * FROM PortugueseVerbs WHERE verb LIKE :verb LIMIT 1")
//    fun findByName(verb: String?): PortugueseVerbEntity?
//
//    @Query("update PortugueseVerbDefinitions set added = :added where definition_id = :definition_id")
//    suspend fun updateAdded(added: Int, definition_id: Int)
//}