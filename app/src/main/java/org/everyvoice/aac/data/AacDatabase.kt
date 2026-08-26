package org.everyvoice.aac.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CategoryEntity::class, ButtonEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AacDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun buttonDao(): ButtonDao

    companion object {
        @Volatile
        private var instance: AacDatabase? = null

        fun get(context: Context): AacDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AacDatabase::class.java,
                    "everyvoice.db",
                ).build().also { instance = it }
            }
        }
    }
}
