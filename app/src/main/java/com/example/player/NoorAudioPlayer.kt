package com.example.player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import com.example.data.model.SurahEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AudioPlayerState(
  val currentSurah: SurahEntity? = null,
  val isPlaying: Boolean = false,
  val isLoading: Boolean = false,
  val currentPositionMs: Long = 0L,
  val durationMs: Long = 0L,
  val errorMessage: String? = null,
  val volume: Float = 1.0f,
  val isOfflineDownloaded: Boolean = false
)

class NoorAudioPlayer(private val context: Context) {
  private var mediaPlayer: MediaPlayer? = null
  private val scope = CoroutineScope(Dispatchers.Main + Job())
  private var progressJob: Job? = null

  private val _playerState = MutableStateFlow(AudioPlayerState())
  val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

  private var playlist: List<SurahEntity> = emptyList()

  fun setPlaylist(list: List<SurahEntity>) {
    playlist = list
  }

  fun playSurah(surah: SurahEntity, fullList: List<SurahEntity> = playlist) {
    if (fullList.isNotEmpty()) {
      playlist = fullList
    }

    if (_playerState.value.currentSurah?.number == surah.number && mediaPlayer != null) {
      if (!_playerState.value.isPlaying) {
        resume()
      }
      return
    }

    stopAndRelease()

    _playerState.value = _playerState.value.copy(
      currentSurah = surah,
      isLoading = true,
      isPlaying = false,
      currentPositionMs = 0L,
      durationMs = 0L,
      errorMessage = null
    )

    try {
      val player = MediaPlayer().apply {
        setAudioAttributes(
          AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        )
        setDataSource(surah.audioUrl)
        setVolume(_playerState.value.volume, _playerState.value.volume)

        setOnPreparedListener { mp ->
          _playerState.value = _playerState.value.copy(
            isLoading = false,
            isPlaying = true,
            durationMs = mp.duration.toLong(),
            errorMessage = null
          )
          mp.start()
          startProgressTracking()
        }

        setOnCompletionListener {
          _playerState.value = _playerState.value.copy(
            isPlaying = false,
            currentPositionMs = 0L
          )
          playNext()
        }

        setOnErrorListener { _, what, extra ->
          Log.e("NoorAudioPlayer", "MediaPlayer error: what=$what, extra=$extra")
          _playerState.value = _playerState.value.copy(
            isLoading = false,
            isPlaying = false,
            errorMessage = "অডিও লোড করতে সমস্যা হয়েছে। ইন্টারনেট সংযোগ পরীক্ষা করে পুনরায় চেষ্টা করুন।"
          )
          true
        }

        prepareAsync()
      }
      mediaPlayer = player
    } catch (e: Exception) {
      Log.e("NoorAudioPlayer", "Failed to start audio", e)
      _playerState.value = _playerState.value.copy(
        isLoading = false,
        isPlaying = false,
        errorMessage = "অডিও প্লেয়ার প্রস্তুত করা যায়নি: ${e.localizedMessage}"
      )
    }
  }

  fun togglePlayPause() {
    val state = _playerState.value
    if (state.isPlaying) {
      pause()
    } else {
      if (mediaPlayer != null) {
        resume()
      } else if (state.currentSurah != null) {
        playSurah(state.currentSurah)
      }
    }
  }

  fun pause() {
    try {
      mediaPlayer?.pause()
      _playerState.value = _playerState.value.copy(isPlaying = false)
      stopProgressTracking()
    } catch (e: Exception) {
      Log.e("NoorAudioPlayer", "Pause error", e)
    }
  }

  fun resume() {
    try {
      mediaPlayer?.start()
      _playerState.value = _playerState.value.copy(isPlaying = true)
      startProgressTracking()
    } catch (e: Exception) {
      Log.e("NoorAudioPlayer", "Resume error", e)
    }
  }

  fun seekTo(positionMs: Long) {
    try {
      mediaPlayer?.seekTo(positionMs.toInt())
      _playerState.value = _playerState.value.copy(currentPositionMs = positionMs)
    } catch (e: Exception) {
      Log.e("NoorAudioPlayer", "Seek error", e)
    }
  }

  fun skipForward(seconds: Int = 15) {
    mediaPlayer?.let { mp ->
      val newPos = (mp.currentPosition + seconds * 1000).coerceAtMost(mp.duration)
      seekTo(newPos.toLong())
    }
  }

  fun skipBackward(seconds: Int = 15) {
    mediaPlayer?.let { mp ->
      val newPos = (mp.currentPosition - seconds * 1000).coerceAtLeast(0)
      seekTo(newPos.toLong())
    }
  }

  fun playNext() {
    val current = _playerState.value.currentSurah ?: return
    val currentIndex = playlist.indexOfFirst { it.number == current.number }
    if (currentIndex != -1 && currentIndex + 1 < playlist.size) {
      playSurah(playlist[currentIndex + 1])
    }
  }

  fun playPrevious() {
    val current = _playerState.value.currentSurah ?: return
    val currentIndex = playlist.indexOfFirst { it.number == current.number }
    if (currentIndex > 0) {
      playSurah(playlist[currentIndex - 1])
    }
  }

  fun setVolume(volume: Float) {
    val vol = volume.coerceIn(0f, 1f)
    _playerState.value = _playerState.value.copy(volume = vol)
    mediaPlayer?.setVolume(vol, vol)
  }

  fun toggleOfflineSimulation() {
    val current = _playerState.value.isOfflineDownloaded
    _playerState.value = _playerState.value.copy(isOfflineDownloaded = !current)
  }

  fun retry() {
    _playerState.value.currentSurah?.let {
      playSurah(it)
    }
  }

  private fun startProgressTracking() {
    stopProgressTracking()
    progressJob = scope.launch {
      while (isActive) {
        mediaPlayer?.let { mp ->
          if (mp.isPlaying) {
            _playerState.value = _playerState.value.copy(
              currentPositionMs = mp.currentPosition.toLong(),
              durationMs = mp.duration.toLong()
            )
          }
        }
        delay(500)
      }
    }
  }

  private fun stopProgressTracking() {
    progressJob?.cancel()
    progressJob = null
  }

  fun stopAndRelease() {
    stopProgressTracking()
    try {
      mediaPlayer?.stop()
      mediaPlayer?.release()
    } catch (e: Exception) {
      Log.e("NoorAudioPlayer", "Release error", e)
    } finally {
      mediaPlayer = null
    }
  }
}
