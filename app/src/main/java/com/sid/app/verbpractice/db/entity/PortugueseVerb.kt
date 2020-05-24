package com.sid.app.verbpractice.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "PortugueseVerbs")
data class PortugueseVerb(
    @PrimaryKey val verb_id: Int,
    @ColumnInfo(name = "verb") val verb: String,
    @ColumnInfo(name = "added") val added: Int = 0,
    @ColumnInfo(name = "main_def") val main_def: String,
    @ColumnInfo(name = "verb_group") val verb_group: Int = 0)