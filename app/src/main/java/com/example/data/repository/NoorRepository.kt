package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoorRepository(private val db: AppDatabase) {

  // Hadiths
  val allHadiths: Flow<List<HadithEntity>> = db.hadithDao().getAllHadiths()
  fun getHadithsByCategory(category: String): Flow<List<HadithEntity>> =
    db.hadithDao().getHadithsByCategory(category)

  fun searchHadiths(query: String): Flow<List<HadithEntity>> =
    db.hadithDao().searchHadiths(query)

  suspend fun insertHadith(hadith: HadithEntity): Long =
    db.hadithDao().insertHadith(hadith)

  suspend fun updateHadith(hadith: HadithEntity) =
    db.hadithDao().updateHadith(hadith)

  suspend fun deleteHadith(hadith: HadithEntity) {
    db.hadithDao().deleteHadith(hadith)
    db.bookmarkDao().deleteBookmark("HADITH", hadith.id)
  }

  suspend fun toggleFavoriteHadith(hadith: HadithEntity) {
    val newFav = !hadith.isFavorite
    db.hadithDao().updateFavorite(hadith.id, newFav)
    if (newFav) {
      db.bookmarkDao().insertBookmark(
        BookmarkEntity(
          itemType = "HADITH",
          itemId = hadith.id,
          title = "${hadith.bookName} (${hadith.hadithNumber})",
          subtitle = hadith.banglaText.take(100) + if (hadith.banglaText.length > 100) "..." else "",
          arabicSnippet = hadith.arabicText.take(80) + "..."
        )
      )
    } else {
      db.bookmarkDao().deleteBookmark("HADITH", hadith.id)
    }
  }

  // Masalas
  val allMasalas: Flow<List<MasalaEntity>> = db.masalaDao().getAllMasalas()
  fun getMasalasByCategory(category: String): Flow<List<MasalaEntity>> =
    db.masalaDao().getMasalasByCategory(category)

  fun searchMasalas(query: String): Flow<List<MasalaEntity>> =
    db.masalaDao().searchMasalas(query)

  suspend fun insertMasala(masala: MasalaEntity): Long =
    db.masalaDao().insertMasala(masala)

  suspend fun updateMasala(masala: MasalaEntity) =
    db.masalaDao().updateMasala(masala)

  suspend fun deleteMasala(masala: MasalaEntity) {
    db.masalaDao().deleteMasala(masala)
    db.bookmarkDao().deleteBookmark("MASALA", masala.id)
  }

  suspend fun toggleFavoriteMasala(masala: MasalaEntity) {
    val newFav = !masala.isFavorite
    db.masalaDao().updateFavorite(masala.id, newFav)
    if (newFav) {
      db.bookmarkDao().insertBookmark(
        BookmarkEntity(
          itemType = "MASALA",
          itemId = masala.id,
          title = masala.question,
          subtitle = masala.shortAnswer,
          arabicSnippet = masala.dalil
        )
      )
    } else {
      db.bookmarkDao().deleteBookmark("MASALA", masala.id)
    }
  }

  // Quran Surahs & Ayahs
  val allSurahs: Flow<List<SurahEntity>> = db.quranDao().getAllSurahs()

  fun getSurahByNumber(number: Int): Flow<SurahEntity?> =
    db.quranDao().getSurahByNumber(number)

  fun getAyahsForSurah(surahNumber: Int): Flow<List<AyahEntity>> =
    db.quranDao().getAyahsForSurah(surahNumber)

  fun searchAyahs(query: String): Flow<List<AyahEntity>> =
    db.quranDao().searchAyahs(query)

  suspend fun updateSurah(surah: SurahEntity) =
    db.quranDao().updateSurah(surah)

  suspend fun updateAyah(ayah: AyahEntity) =
    db.quranDao().updateAyah(ayah)

  suspend fun toggleFavoriteAyah(ayah: AyahEntity, surahName: String = "আল-কুরআন") {
    val newFav = !ayah.isFavorite
    db.quranDao().updateAyahFavorite(ayah.id, newFav)
    if (newFav) {
      db.bookmarkDao().insertBookmark(
        BookmarkEntity(
          itemType = "AYAH",
          itemId = ayah.id,
          title = "$surahName - আয়াত ${ayah.ayahNumber}",
          subtitle = ayah.banglaTranslation,
          arabicSnippet = ayah.arabicText
        )
      )
    } else {
      db.bookmarkDao().deleteBookmark("AYAH", ayah.id)
    }
  }

  // Bookmarks
  val allBookmarks: Flow<List<BookmarkEntity>> = db.bookmarkDao().getAllBookmarks()
  fun getBookmarksByType(type: String): Flow<List<BookmarkEntity>> =
    db.bookmarkDao().getBookmarksByType(type)

  suspend fun removeBookmark(type: String, itemId: Int) {
    db.bookmarkDao().deleteBookmark(type, itemId)
    when (type) {
      "HADITH" -> db.hadithDao().updateFavorite(itemId, false)
      "MASALA" -> db.masalaDao().updateFavorite(itemId, false)
      "AYAH" -> db.quranDao().updateAyahFavorite(itemId, false)
    }
  }

  // Settings
  val settings: Flow<AppSettingsEntity?> = db.settingsDao().getSettings()

  suspend fun saveSettings(settings: AppSettingsEntity) =
    db.settingsDao().saveSettings(settings)
}
