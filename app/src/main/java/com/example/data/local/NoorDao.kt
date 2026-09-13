package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HadithDao {
  @Query("SELECT * FROM hadiths ORDER BY id ASC")
  fun getAllHadiths(): Flow<List<HadithEntity>>

  @Query("SELECT * FROM hadiths WHERE category = :category ORDER BY id ASC")
  fun getHadithsByCategory(category: String): Flow<List<HadithEntity>>

  @Query("SELECT * FROM hadiths WHERE id = :id LIMIT 1")
  fun getHadithById(id: Int): Flow<HadithEntity?>

  @Query("SELECT * FROM hadiths WHERE isFavorite = 1 ORDER BY id DESC")
  fun getFavoriteHadiths(): Flow<List<HadithEntity>>

  @Query("SELECT * FROM hadiths WHERE banglaText LIKE '%' || :query || '%' OR arabicText LIKE '%' || :query || '%' OR narrator LIKE '%' || :query || '%' OR reference LIKE '%' || :query || '%'")
  fun searchHadiths(query: String): Flow<List<HadithEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHadith(hadith: HadithEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(hadiths: List<HadithEntity>)

  @Update
  suspend fun updateHadith(hadith: HadithEntity)

  @Delete
  suspend fun deleteHadith(hadith: HadithEntity)

  @Query("UPDATE hadiths SET isFavorite = :isFav WHERE id = :id")
  suspend fun updateFavorite(id: Int, isFav: Boolean)

  @Query("SELECT COUNT(*) FROM hadiths")
  suspend fun getCount(): Int
}

@Dao
interface MasalaDao {
  @Query("SELECT * FROM masalas ORDER BY id ASC")
  fun getAllMasalas(): Flow<List<MasalaEntity>>

  @Query("SELECT * FROM masalas WHERE category = :category ORDER BY id ASC")
  fun getMasalasByCategory(category: String): Flow<List<MasalaEntity>>

  @Query("SELECT * FROM masalas WHERE id = :id LIMIT 1")
  fun getMasalaById(id: Int): Flow<MasalaEntity?>

  @Query("SELECT * FROM masalas WHERE isFavorite = 1 ORDER BY id DESC")
  fun getFavoriteMasalas(): Flow<List<MasalaEntity>>

  @Query("SELECT * FROM masalas WHERE question LIKE '%' || :query || '%' OR shortAnswer LIKE '%' || :query || '%' OR detailedAnswer LIKE '%' || :query || '%' OR dalil LIKE '%' || :query || '%'")
  fun searchMasalas(query: String): Flow<List<MasalaEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMasala(masala: MasalaEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(masalas: List<MasalaEntity>)

  @Update
  suspend fun updateMasala(masala: MasalaEntity)

  @Delete
  suspend fun deleteMasala(masala: MasalaEntity)

  @Query("UPDATE masalas SET isFavorite = :isFav WHERE id = :id")
  suspend fun updateFavorite(id: Int, isFav: Boolean)

  @Query("SELECT COUNT(*) FROM masalas")
  suspend fun getCount(): Int
}

@Dao
interface QuranDao {
  @Query("SELECT * FROM surahs ORDER BY number ASC")
  fun getAllSurahs(): Flow<List<SurahEntity>>

  @Query("SELECT * FROM surahs WHERE number = :number LIMIT 1")
  fun getSurahByNumber(number: Int): Flow<SurahEntity?>

  @Query("SELECT * FROM ayahs WHERE surahNumber = :surahNumber ORDER BY ayahNumber ASC")
  fun getAyahsForSurah(surahNumber: Int): Flow<List<AyahEntity>>

  @Query("SELECT * FROM ayahs WHERE isFavorite = 1 ORDER BY surahNumber, ayahNumber ASC")
  fun getFavoriteAyahs(): Flow<List<AyahEntity>>

  @Query("SELECT * FROM ayahs WHERE id = :id LIMIT 1")
  fun getAyahById(id: Int): Flow<AyahEntity?>

  @Query("SELECT * FROM ayahs WHERE banglaTranslation LIKE '%' || :query || '%' OR arabicText LIKE '%' || :query || '%' OR banglaPronunciation LIKE '%' || :query || '%'")
  fun searchAyahs(query: String): Flow<List<AyahEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSurahs(surahs: List<SurahEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAyahs(ayahs: List<AyahEntity>)

  @Update
  suspend fun updateSurah(surah: SurahEntity)

  @Update
  suspend fun updateAyah(ayah: AyahEntity)

  @Query("UPDATE ayahs SET isFavorite = :isFav WHERE id = :id")
  suspend fun updateAyahFavorite(id: Int, isFav: Boolean)

  @Query("SELECT COUNT(*) FROM surahs")
  suspend fun getSurahCount(): Int
}

@Dao
interface BookmarkDao {
  @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
  fun getAllBookmarks(): Flow<List<BookmarkEntity>>

  @Query("SELECT * FROM bookmarks WHERE itemType = :type ORDER BY createdAt DESC")
  fun getBookmarksByType(type: String): Flow<List<BookmarkEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBookmark(bookmark: BookmarkEntity): Long

  @Query("DELETE FROM bookmarks WHERE itemType = :type AND itemId = :itemId")
  suspend fun deleteBookmark(type: String, itemId: Int)

  @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE itemType = :type AND itemId = :itemId)")
  fun isBookmarked(type: String, itemId: Int): Flow<Boolean>
}

@Dao
interface SettingsDao {
  @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
  fun getSettings(): Flow<AppSettingsEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveSettings(settings: AppSettingsEntity)
}
