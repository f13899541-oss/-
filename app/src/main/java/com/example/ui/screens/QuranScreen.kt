package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SurahEntity
import com.example.ui.components.IslamicTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

@Composable
fun QuranScreen(
  viewModel: NoorViewModel,
  onOpenSurah: (SurahEntity) -> Unit,
  onPlayAudio: (SurahEntity) -> Unit,
  onOpenSearch: () -> Unit,
  onOpenBookmarks: () -> Unit,
  modifier: Modifier = Modifier
) {
  val surahs by viewModel.allSurahs.collectAsState()
  val audioState by viewModel.audioState.collectAsState()

  Scaffold(
    topBar = {
      IslamicTopAppBar(
        title = "পবিত্র কুরআন",
        subtitle = "সূরা পাঠ, বাংলা অর্থ ও সুললিত তেলাওয়াত",
        actions = {
          IconButton(onClick = onOpenSearch) {
            Icon(Icons.Default.Search, contentDescription = "সার্চ", tint = MaterialTheme.colorScheme.primary)
          }
          IconButton(onClick = onOpenBookmarks) {
            Icon(Icons.Default.Bookmark, contentDescription = "সংরক্ষিত", tint = IslamicGold)
          }
        }
      )
    },
    modifier = modifier.fillMaxSize().testTag("quran_screen")
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = IslamicGreenContainer.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = IslamicGreenPrimary)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "সূরা আল-মুলক ও সূরা আর-রহমান সহ গুরুত্বপূর্ণ সূরাগুলোর পূর্ণাঙ্গ আরবি, বাংলা উচ্চারণ, অর্থ ও অডিও সংযুক্ত রয়েছে।",
              style = MaterialTheme.typography.bodySmall.copy(color = IslamicGreenDark)
            )
          }
        }
      }

      items(surahs, key = { it.number }) { surah ->
        val isCurrentlyPlaying = audioState.isPlaying && audioState.currentSurah?.number == surah.number

        ElevatedCard(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenSurah(surah) }
            .testTag("surah_card_${surah.number}"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.elevatedCardColors(
            containerColor = if (isCurrentlyPlaying) IslamicGreenContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
          ),
          elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Surah Number Badge
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                  if (isCurrentlyPlaying) IslamicGreenPrimary else IslamicGold.copy(alpha = 0.18f)
                ),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "${surah.number}",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (isCurrentlyPlaying) Color.White else IslamicGoldDark
                )
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = surah.banglaName,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                if (surah.number == 67 || surah.number == 55) {
                  Spacer(modifier = Modifier.width(6.dp))
                  SuggestionChip(
                    onClick = {},
                    label = { Text("বিশেষ", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                      containerColor = IslamicGold.copy(alpha = 0.2f),
                      labelColor = IslamicGoldDark
                    ),
                    border = null,
                    modifier = Modifier.height(22.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(2.dp))

              Text(
                text = "${surah.meaning} • ${surah.revelationType} • ${surah.totalAyahs} আয়াত",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              )
            }

            // Arabic Name & Audio Button
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = surah.arabicName,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.Bold,
                  color = IslamicGreenPrimary
                )
              )

              Spacer(modifier = Modifier.height(6.dp))

              IconButton(
                onClick = { onPlayAudio(surah) },
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(if (isCurrentlyPlaying) IslamicGreenPrimary else IslamicGold.copy(alpha = 0.2f))
                  .testTag("play_surah_${surah.number}")
              ) {
                Icon(
                  imageVector = if (isCurrentlyPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                  contentDescription = "তেলাওয়াত শুনুন",
                  tint = if (isCurrentlyPlaying) Color.White else IslamicGoldDark,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}
