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
    @ColumnInfo(name = "to_fly") val to_fly: String,
    @ColumnInfo(name = "fly") val fly: String,
    @ColumnInfo(name = "flies") val flies: String,
    @ColumnInfo(name = "flew") val flew: String,
    @ColumnInfo(name = "flown") val flown: String,
    @ColumnInfo(name = "flying") val flying: String,
    @ColumnInfo(name = "verb_group") val verb_group: Int = 0)