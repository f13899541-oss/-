package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.NoorRepository
import com.example.player.AudioPlayerState
import com.example.player.NoorAudioPlayer
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class SearchResultState(
  val hadiths: List<HadithEntity> = emptyList(),
  val masalas: List<MasalaEntity> = emptyList(),
  val ayahs: List<Pair<SurahEntity?, AyahEntity>> = emptyList()
)

class NoorViewModel(application: Application) : AndroidViewModel(application) {

  private val database = AppDatabase.getInstance(application, viewModelScope)
  val repository = NoorRepository(database)
  val audioPlayer = NoorAudioPlayer(application)

  val audioState: StateFlow<AudioPlayerState> = audioPlayer.playerState

  // All Hadiths & Filter
  val allHadiths: StateFlow<List<HadithEntity>> = repository.allHadiths
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _selectedHadithCategory = MutableStateFlow("সব")
  val selectedHadithCategory: StateFlow<String> = _selectedHadithCategory.asStateFlow()

  val filteredHadiths: StateFlow<List<HadithEntity>> = combine(allHadiths, _selectedHadithCategory) { list, cat ->
    if (cat == "সব") list else list.filter { it.category == cat }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // All Masalas & Filter
  val allMasalas: StateFlow<List<MasalaEntity>> = repository.allMasalas
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _selectedMasalaCategory = MutableStateFlow("সব")
  val selectedMasalaCategory: StateFlow<String> = _selectedMasalaCategory.asStateFlow()

  val filteredMasalas: StateFlow<List<MasalaEntity>> = combine(allMasalas, _selectedMasalaCategory) { list, cat ->
    if (cat == "সব") list else list.filter { it.category == cat }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Surahs & Ayahs
  val allSurahs: StateFlow<List<SurahEntity>> = repository.allSurahs
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _selectedSurah = MutableStateFlow<SurahEntity?>(null)
  val selectedSurah: StateFlow<SurahEntity?> = _selectedSurah.asStateFlow()

  val currentSurahAyahs: StateFlow<List<AyahEntity>> = _selectedSurah.flatMapLatest { surah ->
    if (surah != null) repository.getAyahsForSurah(surah.number)
    else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Bookmarks
  val allBookmarks: StateFlow<List<BookmarkEntity>> = repository.allBookmarks
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _selectedBookmarkType = MutableStateFlow("ALL")
  val selectedBookmarkType: StateFlow<String> = _selectedBookmarkType.asStateFlow()

  val filteredBookmarks: StateFlow<List<BookmarkEntity>> = combine(allBookmarks, _selectedBookmarkType) { list, type ->
    if (type == "ALL") list else list.filter { it.itemType == type }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Settings
  val settings: StateFlow<AppSettingsEntity> = repository.settings.map {
    it ?: AppSettingsEntity()
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettingsEntity())

  // Search
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  val searchResults: StateFlow<SearchResultState> = _searchQuery
    .debounce(300)
    .flatMapLatest { query ->
      if (query.isBlank()) {
        flowOf(SearchResultState())
      } else {
        combine(
          repository.searchHadiths(query),
          repository.searchMasalas(query),
          repository.searchAyahs(query),
          allSurahs
        ) { hList, mList, aList, sList ->
          val surahMap = sList.associateBy { it.number }
          SearchResultState(
            hadiths = hList,
            masalas = mList,
            ayahs = aList.map { Pair(surahMap[it.surahNumber], it) }
          )
        }
      }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchResultState())

  // Daily Hadith & Daily Ayah
  val dailyHadith: StateFlow<HadithEntity?> = combine(allHadiths, settings) { hadiths, set ->
    if (hadiths.isEmpty()) null
    else {
      // Pick based on day of year
      val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
      val idx = (dayOfYear + set.dailyHadithId - 1) % hadiths.size
      hadiths.getOrNull(idx) ?: hadiths.first()
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val dailyAyah: StateFlow<Pair<SurahEntity, AyahEntity>?> = combine(allSurahs, settings) { surahs, set ->
    val mulk = surahs.find { it.number == 67 } ?: surahs.firstOrNull()
    if (mulk != null) {
      Pair(
        mulk,
        AyahEntity(
          id = 999,
          surahNumber = 67,
          ayahNumber = 1,
          arabicText = "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
          banglaPronunciation = "তাবারাকাল্লাযী বিয়াদিহিল মুলকু ওয়াহুওয়া আলা কুল্লি শাইয়িন ক্বাদীর",
          banglaTranslation = "বরকতময় তিনি, যাঁর হাতে সমগ্র রাজত্ব এবং তিনি সর্ববিষয়ে সর্বশক্তিমান।"
        )
      )
    } else null
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // Actions
  fun setHadithCategory(category: String) {
    _selectedHadithCategory.value = category
  }

  fun setMasalaCategory(category: String) {
    _selectedMasalaCategory.value = category
  }

  fun selectSurah(surah: SurahEntity) {
    _selectedSurah.value = surah
  }

  fun setBookmarkType(type: String) {
    _selectedBookmarkType.value = type
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun toggleFavoriteHadith(hadith: HadithEntity) {
    viewModelScope.launch {
      repository.toggleFavoriteHadith(hadith)
    }
  }

  fun toggleFavoriteMasala(masala: MasalaEntity) {
    viewModelScope.launch {
      repository.toggleFavoriteMasala(masala)
    }
  }

  fun toggleFavoriteAyah(ayah: AyahEntity, surahName: String = "কুরআন") {
    viewModelScope.launch {
      repository.toggleFavoriteAyah(ayah, surahName)
    }
  }

  fun removeBookmark(type: String, itemId: Int) {
    viewModelScope.launch {
      repository.removeBookmark(type, itemId)
    }
  }

  // Audio actions
  fun playSurah(surah: SurahEntity) {
    audioPlayer.playSurah(surah, allSurahs.value)
  }

  fun togglePlayPauseAudio() {
    audioPlayer.togglePlayPause()
  }

  fun seekAudio(positionMs: Long) {
    audioPlayer.seekTo(positionMs)
  }

  fun skipAudioForward(seconds: Int = 15) {
    audioPlayer.skipForward(seconds)
  }

  fun skipAudioBackward(seconds: Int = 15) {
    audioPlayer.skipBackward(seconds)
  }

  fun nextAudio() {
    audioPlayer.playNext()
  }

  fun prevAudio() {
    audioPlayer.playPrevious()
  }

  fun setAudioVolume(volume: Float) {
    audioPlayer.setVolume(volume)
  }

  fun toggleOfflineSimulation() {
    audioPlayer.toggleOfflineSimulation()
  }

  fun retryAudio() {
    audioPlayer.retry()
  }

  // Settings & Theme
  fun updateDarkMode(darkMode: Boolean, useSystem: Boolean = false) {
    viewModelScope.launch {
      val current = settings.value
      repository.saveSettings(current.copy(isDarkMode = darkMode, useSystemTheme = useSystem))
    }
  }

  fun updateFontSize(banglaSp: Float, arabicSp: Float) {
    viewModelScope.launch {
      val current = settings.value
      repository.saveSettings(current.copy(banglaFontSize = banglaSp, arabicFontSize = arabicSp))
    }
  }

  fun toggleNotifications(enabled: Boolean) {
    viewModelScope.launch {
      val current = settings.value
      repository.saveSettings(current.copy(notificationsEnabled = enabled))
    }
  }

  // Admin Actions
  fun addHadith(hadith: HadithEntity) {
    viewModelScope.launch {
      repository.insertHadith(hadith)
    }
  }

  fun updateHadith(hadith: HadithEntity) {
    viewModelScope.launch {
      repository.updateHadith(hadith)
    }
  }

  fun deleteHadith(hadith: HadithEntity) {
    viewModelScope.launch {
      repository.deleteHadith(hadith)
    }
  }

  fun addMasala(masala: MasalaEntity) {
    viewModelScope.launch {
      repository.insertMasala(masala)
    }
  }

  fun updateMasala(masala: MasalaEntity) {
    viewModelScope.launch {
      repository.updateMasala(masala)
    }
  }

  fun deleteMasala(masala: MasalaEntity) {
    viewModelScope.launch {
      repository.deleteMasala(masala)
    }
  }

  fun updateSurahAudio(surah: SurahEntity) {
    viewModelScope.launch {
      repository.updateSurah(surah)
    }
  }

  fun setDailyHadithManual(hadithId: Int) {
    viewModelScope.launch {
      val current = settings.value
      repository.saveSettings(current.copy(dailyHadithId = hadithId))
    }
  }

  override fun onCleared() {
    super.onCleared()
    audioPlayer.stopAndRelease()
  }
}
