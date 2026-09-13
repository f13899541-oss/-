package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SurahEntity
import com.example.player.AudioPlayerState
import com.example.ui.components.IslamicTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

fun formatTimeMs(ms: Long): String {
  val totalSec = ms / 1000
  val min = totalSec / 60
  val sec = totalSec % 60
  return String.format("%02d:%02d", min, sec)
}

@Composable
fun AudioPlayerScreen(
  viewModel: NoorViewModel,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val audioState by viewModel.audioState.collectAsState()
  val surahs by viewModel.allSurahs.collectAsState()
  val currentSurah = audioState.currentSurah ?: surahs.find { it.number == 67 } ?: surahs.firstOrNull()

  var sliderPosition by remember { mutableFloatStateOf(0f) }
  var isDraggingSlider by remember { mutableStateOf(false) }

  val progress = if (audioState.durationMs > 0) {
    audioState.currentPositionMs.toFloat() / audioState.durationMs.toFloat()
  } else 0f

  LaunchedEffect(progress) {
    if (!isDraggingSlider) {
      sliderPosition = progress
    }
  }

  Scaffold(
    topBar = {
      IslamicTopAppBar(
        title = "কুরআন অডিও প্লেয়ার",
        subtitle = "অনুমোদিত ও নির্ভরযোগ্য তিলাওয়াত",
        showBack = true,
        onBackClick = onBackClick
      )
    },
    modifier = modifier.fillMaxSize().testTag("audio_player_screen")
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // 1. Surah Hero Disc / Visualizer Card
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
              Brush.verticalGradient(
                colors = listOf(IslamicGreenDark, IslamicGreenPrimary)
              )
            )
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Glowing Quran Disc
            Box(
              modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .background(IslamicGold.copy(alpha = 0.2f))
                .border(3.dp, IslamicGold, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Headphones,
                contentDescription = null,
                tint = IslamicGoldLight,
                modifier = Modifier.size(60.dp)
              )
            }

            Spacer(modifier = Modifier.height(18.dp))

            currentSurah?.let {
              Text(
                text = it.arabicName,
                style = MaterialTheme.typography.headlineMedium.copy(
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.Bold,
                  color = IslamicGoldLight
                )
              )

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = it.banglaName,
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              )

              Text(
                text = "${it.meaning} • ${it.totalAyahs} আয়াত",
                style = MaterialTheme.typography.bodyMedium.copy(
                  color = Color.White.copy(alpha = 0.85f)
                )
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "ক্বারী: ${it.reciter}",
                style = MaterialTheme.typography.labelMedium.copy(
                  color = IslamicGoldLight,
                  fontWeight = FontWeight.Medium
                )
              )
            }
          }
        }
      }

      // 2. Error message banner if any
      if (audioState.errorMessage != null) {
        item {
          Spacer(modifier = Modifier.height(12.dp))
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = audioState.errorMessage ?: "",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onErrorContainer),
                modifier = Modifier.weight(1f)
              )
              TextButton(onClick = { viewModel.retryAudio() }) {
                Text("পুনরায় চেষ্টা")
              }
            }
          }
        }
      }

      // 3. Seek Bar & Timing
      item {
        Spacer(modifier = Modifier.height(18.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
          Slider(
            value = sliderPosition,
            onValueChange = {
              isDraggingSlider = true
              sliderPosition = it
            },
            onValueChangeFinished = {
              isDraggingSlider = false
              val targetMs = (sliderPosition * audioState.durationMs).toLong()
              viewModel.seekAudio(targetMs)
            },
            colors = SliderDefaults.colors(
              thumbColor = IslamicGold,
              activeTrackColor = IslamicGold,
              inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth().testTag("audio_seek_bar")
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = formatTimeMs(audioState.currentPositionMs),
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Text(
              text = if (audioState.durationMs > 0) formatTimeMs(audioState.durationMs) else "--:--",
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          }
        }
      }

      // 4. Playback Controls (15s backward, Prev, Play/Pause, Next, 15s forward)
      item {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // 15s Backward
          IconButton(
            onClick = { viewModel.skipAudioBackward(15) },
            modifier = Modifier.size(48.dp).testTag("skip_backward_15s")
          ) {
            Icon(
              imageVector = Icons.Default.Replay10,
              contentDescription = "১৫ সেকেন্ড পেছনে",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(30.dp)
            )
          }

          // Previous Surah
          IconButton(
            onClick = { viewModel.prevAudio() },
            modifier = Modifier.size(48.dp).testTag("prev_audio_button")
          ) {
            Icon(
              imageVector = Icons.Default.SkipPrevious,
              contentDescription = "পূর্ববর্তী সূরা",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(34.dp)
            )
          }

          // Play / Pause / Loading
          if (audioState.isLoading) {
            Box(
              modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(IslamicGreenPrimary),
              contentAlignment = Alignment.Center
            ) {
              CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(32.dp),
                strokeWidth = 3.dp
              )
            }
          } else {
            IconButton(
              onClick = {
                if (currentSurah != null && audioState.currentSurah == null) {
                  viewModel.playSurah(currentSurah)
                } else {
                  viewModel.togglePlayPauseAudio()
                }
              },
              modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(IslamicGreenPrimary)
                .testTag("audio_player_play_pause")
            ) {
              Icon(
                imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (audioState.isPlaying) "বিরতি" else "চালান",
                tint = Color.White,
                modifier = Modifier.size(38.dp)
              )
            }
          }

          // Next Surah
          IconButton(
            onClick = { viewModel.nextAudio() },
            modifier = Modifier.size(48.dp).testTag("next_audio_button")
          ) {
            Icon(
              imageVector = Icons.Default.SkipNext,
              contentDescription = "পরবর্তী সূরা",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(34.dp)
            )
          }

          // 15s Forward
          IconButton(
            onClick = { viewModel.skipAudioForward(15) },
            modifier = Modifier.size(48.dp).testTag("skip_forward_15s")
          ) {
            Icon(
              imageVector = Icons.Default.Forward10,
              contentDescription = "১৫ সেকেন্ড সামনে",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(30.dp)
            )
          }
        }
      }

      // 5. Volume & Offline Bar
      item {
        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 14.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.VolumeDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
          )

          Slider(
            value = audioState.volume,
            onValueChange = { viewModel.setAudioVolume(it) },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
              thumbColor = IslamicGreenPrimary,
              activeTrackColor = IslamicGreenPrimary
            ),
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp).testTag("volume_slider")
          )

          Icon(
            imageVector = Icons.Default.VolumeUp,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
          )

          Spacer(modifier = Modifier.width(8.dp))

          // Offline button
          IconButton(
            onClick = { viewModel.toggleOfflineSimulation() },
            modifier = Modifier.size(36.dp).testTag("offline_download_button")
          ) {
            Icon(
              imageVector = if (audioState.isOfflineDownloaded) Icons.Filled.DownloadDone else Icons.Outlined.FileDownload,
              contentDescription = "অফলাইন ডাউনলোড",
              tint = if (audioState.isOfflineDownloaded) IslamicGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // 6. Surah Playlist Selector (Quick switch between Surah Al-Mulk, Ar-Rahman, etc.)
      item {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "সূরা তালিকা (প্লেলিস্ট)",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          )
          Text(
            text = "মিশারী রশিদ আল-আফাসী",
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.secondary)
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
      }

      items(surahs, key = { it.number }) { surah ->
        val isThisPlaying = audioState.currentSurah?.number == surah.number

        OutlinedCard(
          onClick = { viewModel.playSurah(surah) },
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.outlinedCardColors(
            containerColor = if (isThisPlaying) IslamicGreenContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
          ),
          border = androidx.compose.foundation.BorderStroke(
            width = if (isThisPlaying) 1.5.dp else 1.dp,
            color = if (isThisPlaying) IslamicGreenPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("playlist_item_${surah.number}")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isThisPlaying) IslamicGreenPrimary else IslamicGold.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "${surah.number}",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (isThisPlaying) Color.White else IslamicGoldDark
                )
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = surah.banglaName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "${surah.meaning} • ${surah.totalAyahs} আয়াত",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )
            }

            Text(
              text = surah.arabicName,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = IslamicGreenPrimary
              )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
              imageVector = if (isThisPlaying && audioState.isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
              contentDescription = null,
              tint = if (isThisPlaying) IslamicGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}
