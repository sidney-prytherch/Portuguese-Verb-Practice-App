package com.sid.app.verbpractice.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "EnglishVerbs")
data class EnglishVerb(
    @PrimaryKey val verb: String,
    @ColumnInfo(name = "second_form") val second_form: String?,
    @ColumnInfo(name = "past") val past: String?,
    @ColumnInfo(name = "past_part") val past_part: String?,
    @ColumnInfo(name = "added") val added: Int = 0
)

//    @PrimaryKey
//    var verb = ""
//    @ColumnInfo(name = "second_form")
//    var second_form: String? = null
//    @ColumnInfo(name = "past")
//    private var past: String? = null
//    @ColumnInfo(name = "past_part")
//    private var past_part: String? = null
//    @ColumnInfo(name = "added")
//    private var added = 0
//
//    constructor() {}
//    @Ignore
//    constructor(verb: String, second_form: String?, past: String?, past_part: String?, added: Int) {
//        this.verb = verb
//        this.second_form = second_form
//        this.past = past
//        this.past_part = past_part
//        this.added = added
//    }
//
//    constructor(englishVerb: EnglishVerb) {
//        verb = englishVerb.verb
//        second_form = englishVerb.second_form
//        past = englishVerb.past
//        past_part = englishVerb.past_part
//        added = englishVerb.added
//    }
//
//    override fun getVerb(): String {
//        return verb
//    }
//
//    fun setVerb(verb: String) {
//        this.verb = verb
//    }
//
//    override fun getSecond_form(): String {
//        return second_form!!
//    }
//
//    fun setSecond_form(second_form: String?) {
//        this.second_form = second_form
//    }
//
//    override fun getPast(): String {
//        return past!!
//    }
//
//    fun setPast(past: String?) {
//        this.past = past
//    }
//
//    override fun getPast_part(): String {
//        return past_part!!
//    }
//
//    fun setPast_part(past_part: String?) {
//        this.past_part = past_part
//    }
//
//    override fun getAdded(): Int {
//        return added
//    }
//
//    fun setAdded(added: Int) {
//        this.added = added
//    }
//}