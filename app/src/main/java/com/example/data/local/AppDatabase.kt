package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    HadithEntity::class,
    MasalaEntity::class,
    SurahEntity::class,
    AyahEntity::class,
    BookmarkEntity::class,
    AppSettingsEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun hadithDao(): HadithDao
  abstract fun masalaDao(): MasalaDao
  abstract fun quranDao(): QuranDao
  abstract fun bookmarkDao(): BookmarkDao
  abstract fun settingsDao(): SettingsDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context, scope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "noor_islamic.db"
        ).addCallback(object : Callback() {
          override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            scope.launch(Dispatchers.IO) {
              val database = getInstance(context, scope)
              IslamicSeedData.seedDatabase(database)
            }
          }
        }).build()
        INSTANCE = instance
        instance
      }
    }
  }
}
