package com.sid.app.verbpractice.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "PortugueseVerbs")
data class PortugueseVerb(
    @PrimaryKey val verb_id: Int,
    @ColumnInfo(name = "verb") val verb: String,
    @ColumnInfo(name = "added") val added: Int = 0) {

//    @Ignore private var isExpanded: Boolean = false
//        get() = field
//        set(value) {
//            field = value
//        }
}

//
//@Entity(tableName = "PortugueseVerbs")
//class PortugueseVerbEntity : PortugueseVerb {
//    @PrimaryKey
//    private var verb_id = 0
//    @ColumnInfo(name = "verb")
//    private var verb: String? = null
//    @ColumnInfo(name = "added")
//    private var added = 0
//    @Ignore
//    private var expanded = false
//    @Ignore
//    private var definitions: List<PortugueseVerbDefinitionEntity>? = null
//
//    override fun getDefinitions(): List<PortugueseVerbDefinitionEntity> {
//        return definitions!!
//    }
//
//    override fun setDefinitions(definitions: List<PortugueseVerbDefinitionEntity>) {
//        this.definitions = definitions
//    }
//
//    override fun getVerb_id(): Int {
//        return verb_id
//    }
//
//    fun setVerb_id(verb_id: Int) {
//        this.verb_id = verb_id
//    }
//
//    override fun getVerb(): String {
//        return verb!!
//    }
//
//    fun setVerb(verb: String?) {
//        this.verb = verb
//    }
//
//    override fun getAdded(): Int {
//        return added
//    }
//
//    fun setAdded(added: Int) {
//        this.added = added
//    }
//
//    override fun getExpanded(): Boolean {
//        return expanded
//    }
//
//    override fun setExpanded(expanded: Boolean) {
//        this.expanded = expanded
//    }
//
//    constructor() {}
//    @Ignore
//    constructor(verb_id: Int, verb: String?, added: Int, expanded: Boolean, definitions: List<PortugueseVerbDefinitionEntity>?) {
//        this.verb_id = verb_id
//        this.verb = verb
//        this.added = added
//        this.expanded = expanded
//        this.definitions = definitions
//    }
//
//    constructor(portugueseVerb: PortugueseVerb) {
//        added = portugueseVerb.added
//        definitions = portugueseVerb.definitions
//        expanded = portugueseVerb.expanded
//        verb = portugueseVerb.verb
//        verb_id = portugueseVerb.verb_id
//    }
//}