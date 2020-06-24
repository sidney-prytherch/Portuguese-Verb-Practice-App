package com.sid.app.verbpractice

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sid.app.verbpractice.db.dao.EnglishVerbDao
import com.sid.app.verbpractice.db.dao.PortugueseVerbDao
import com.sid.app.verbpractice.db.entity.EnglishVerb
import com.sid.app.verbpractice.db.entity.PortugueseVerb
import com.sid.app.verbpractice.db.entity.PortugueseVerbDefinition

@Database(entities = [EnglishVerb::class, PortugueseVerb::class, PortugueseVerbDefinition::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun englishVerbDao(): EnglishVerbDao
    abstract fun portugueseVerbDao(): PortugueseVerbDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        @Synchronized
        fun get(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            return if (tempInstance != null) {
                tempInstance
            } else {
                Log.v("confusion", "started fresh")
                val instance = Room.databaseBuilder(context, AppDatabase::class.java, "verbens.db")
                    .createFromAsset("databases/verbens.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}