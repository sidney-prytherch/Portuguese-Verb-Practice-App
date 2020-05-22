package com.sid.app.verbpractice.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "PortugueseVerbDefinitions")
data class PortugueseVerbDefinition(
    @PrimaryKey val definition_id: Int,
    @ColumnInfo(name = "verb_id") val verb_id: Int,
    @ColumnInfo(name = "verb_type") val verbType: Int,
    @ColumnInfo(name = "definition") val definition: String?,
    @ColumnInfo(name = "extra_info") val extra_info: String?,
    @ColumnInfo(name = "added") val added: Int = 0
)

//@Entity(tableName = "PortugueseVerbDefinitions")
//class PortugueseVerbDefinition : PortugueseVerbDefinitionOLD {
//    @PrimaryKey
//    var definition_id = 0
//    @ColumnInfo(name = "verb_id")
//    @ForeignKey(entity = PortugueseVerb::class, parentColumns = ["verb_id"], childColumns = ["verb_id"])
//    var verb_id = 0
//    @ColumnInfo(name = "verb_type")
//    private var verb_type = 0
//    @ColumnInfo(name = "definition")
//    private var definition: String? = null
//    @ColumnInfo(name = "extra_info")
//    private var extra_info: String? = null
//    @ColumnInfo(name = "added")
//    private var added = 0
//
//    override fun getDefinition_id(): Int {
//        return definition_id
//    }
//
//    fun setDefinition_id(definition_id: Int) {
//        this.definition_id = definition_id
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
//    override fun getDefinition(): String {
//        return definition!!
//    }
//
//    fun setDefinition(definition: String?) {
//        this.definition = definition
//    }
//
//    override fun getExtra_info(): String {
//        return extra_info!!
//    }
//
//    fun setExtra_info(extra_info: String?) {
//        this.extra_info = extra_info
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
//    override fun getVerb_type(): Int {
//        return verb_type
//    }
//
//    fun setVerb_type(verb_type: Int) {
//        this.verb_type = verb_type
//    }
//
//    override fun toString(): String {
//        return "Definition{" +
//                "definition_id=" + definition_id +
//                ", verb=" + verb_id +
//                ", definition='" + definition + '\'' +
//                ", added='" + (added == 1) + '\'' +
//                '}'
//    }
//
//    constructor() {}
//    @Ignore
//    constructor(definition_id: Int, verb_id: Int, verb_type: Int, definition: String?, extra_info: String?, added: Int) {
//        this.definition_id = definition_id
//        this.verb_id = verb_id
//        this.verb_type = verb_type
//        this.definition = definition
//        this.extra_info = extra_info
//        this.added = added
//    }
//
//    constructor(portugueseVerbDefinition: PortugueseVerbDefinitionOLD) {
//        definition_id = portugueseVerbDefinition.definition_id
//        verb_id = portugueseVerbDefinition.verb_id
//        verb_type = portugueseVerbDefinition.verb_type
//        definition = portugueseVerbDefinition.definition
//        extra_info = portugueseVerbDefinition.extra_info
//        added = portugueseVerbDefinition.added
//    }
//}