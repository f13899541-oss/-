package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hadiths")
data class HadithEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val category: String,
  val bookName: String,
  val hadithNumber: String,
  val reference: String,
  val arabicText: String,
  val banglaText: String,
  val narrator: String,
  val isFavorite: Boolean = false,
  val isDailyPick: Boolean = false
)

@Entity(tableName = "masalas")
data class MasalaEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val category: String,
  val question: String,
  val shortAnswer: String,
  val detailedAnswer: String,
  val dalil: String,
  val isFavorite: Boolean = false
)

@Entity(tableName = "surahs")
data class SurahEntity(
  @PrimaryKey val number: Int,
  val arabicName: String,
  val banglaName: String,
  val meaning: String,
  val totalAyahs: Int,
  val revelationType: String,
  val audioUrl: String,
  val reciter: String
)

@Entity(tableName = "ayahs")
data class AyahEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val surahNumber: Int,
  val ayahNumber: Int,
  val arabicText: String,
  val banglaPronunciation: String,
  val banglaTranslation: String,
  val isFavorite: Boolean = false
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val itemType: String, // "HADITH", "MASALA", "AYAH"
  val itemId: Int,
  val title: String,
  val subtitle: String,
  val arabicSnippet: String,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
  @PrimaryKey val id: Int = 1,
  val isDarkMode: Boolean = false,
  val useSystemTheme: Boolean = true,
  val banglaFontSize: Float = 16f,
  val arabicFontSize: Float = 24f,
  val notificationsEnabled: Boolean = true,
  val dailyHadithId: Int = 1,
  val dailyAyahId: Int = 1,
  val adminPin: String = "1234"
)
