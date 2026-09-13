package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SurahEntity
import com.example.ui.components.IslamicTopAppBar
import com.example.ui.components.copyToClipboard
import com.example.ui.components.shareText
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

@Composable
fun SurahDetailScreen(
  surah: SurahEntity,
  viewModel: NoorViewModel,
  onBackClick: () -> Unit,
  onPlayAudio: (SurahEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val ayahs by viewModel.currentSurahAyahs.collectAsState()
  val settings by viewModel.settings.collectAsState()
  val audioState by viewModel.audioState.collectAsState()
  val isAudioPlaying = audioState.isPlaying && audioState.currentSurah?.number == surah.number

  Scaffold(
    topBar = {
      IslamicTopAppBar(
        title = surah.banglaName,
        subtitle = "${surah.arabicName} • ${surah.totalAyahs} আয়াত",
        showBack = true,
        onBackClick = onBackClick,
        actions = {
          IconButton(
            onClick = { onPlayAudio(surah) },
            modifier = Modifier.testTag("surah_detail_play_button")
          ) {
            Icon(
              imageVector = if (isAudioPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
              contentDescription = "অডিও শুনুন",
              tint = IslamicGold,
              modifier = Modifier.size(28.dp)
            )
          }
        }
      )
    },
    modifier = modifier.fillMaxSize().testTag("surah_detail_screen")
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Header Card with Bismillah (except Surah 9, but our surahs all start with Bismillah)
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = IslamicGreenDark),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = surah.arabicName,
              style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
              )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "${surah.banglaName} - ${surah.meaning}",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            )

            Text(
              text = "${surah.revelationType} • মোট ${surah.totalAyahs} টি আয়াত",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color.White.copy(alpha = 0.8f)
              )
            )

            if (surah.number != 9 && surah.number != 1) {
              Spacer(modifier = Modifier.height(14.dp))
              Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.SemiBold,
                  color = IslamicGoldLight,
                  fontSize = (settings.arabicFontSize * 0.95f).sp
                )
              )
            }
          }
        }
      }

      // Ayahs list
      items(ayahs, key = { it.id }) { ayah ->
        ElevatedCard(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("ayah_card_${ayah.ayahNumber}"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          ) {
            // Ayah number badge & action row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(IslamicGreenContainer),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "${ayah.ayahNumber}",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = IslamicGreenPrimary
                  )
                )
              }

              Row {
                IconButton(
                  onClick = {
                    viewModel.toggleFavoriteAyah(ayah, surah.banglaName)
                  },
                  modifier = Modifier.size(34.dp).testTag("bookmark_ayah_${ayah.ayahNumber}")
                ) {
                  Icon(
                    imageVector = if (ayah.isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "সংরক্ষণ",
                    tint = if (ayah.isFavorite) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                IconButton(
                  onClick = {
                    val content = """
                      |${surah.banglaName} - আয়াত ${ayah.ayahNumber}
                      |${ayah.arabicText}
                      |উচ্চারণ: ${ayah.banglaPronunciation}
                      |অনুবাদ: ${ayah.banglaTranslation}
                      |— নূর ইসলামিক অ্যাপ
                    """.trimMargin()
                    copyToClipboard(context, "Ayah", content)
                  },
                  modifier = Modifier.size(34.dp).testTag("copy_ayah_${ayah.ayahNumber}")
                ) {
                  Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = "কপি",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                IconButton(
                  onClick = {
                    val content = """
                      |${surah.banglaName} - আয়াত ${ayah.ayahNumber}
                      |${ayah.arabicText}
                      |অনুবাদ: ${ayah.banglaTranslation}
                      |— নূর ইসলামিক অ্যাপ
                    """.trimMargin()
                    shareText(context, "${surah.banglaName} আয়াত ${ayah.ayahNumber}", content)
                  },
                  modifier = Modifier.size(34.dp).testTag("share_ayah_${ayah.ayahNumber}")
                ) {
                  Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "শেয়ার",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Arabic Text
            Text(
              text = ayah.arabicText,
              style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = settings.arabicFontSize.sp,
                lineHeight = (settings.arabicFontSize * 1.65f).sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Right
              ),
              modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Pronunciation
            if (ayah.banglaPronunciation.isNotBlank()) {
              Text(
                text = "উচ্চারণ: ${ayah.banglaPronunciation}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.SemiBold
                )
              )
              Spacer(modifier = Modifier.height(4.dp))
            }

            // Translation
            Text(
              text = ayah.banglaTranslation,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = settings.banglaFontSize.sp,
                lineHeight = (settings.banglaFontSize * 1.5f).sp
              )
            )
          }
        }
      }
    }
  }
}
