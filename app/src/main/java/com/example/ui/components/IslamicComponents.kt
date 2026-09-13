package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AyahEntity
import com.example.data.model.HadithEntity
import com.example.data.model.MasalaEntity
import com.example.player.AudioPlayerState
import com.example.ui.theme.*

fun copyToClipboard(context: Context, label: String, text: String) {
  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  val clip = ClipData.newPlainText(label, text)
  clipboard.setPrimaryClip(clip)
  Toast.makeText(context, "কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
}

fun shareText(context: Context, title: String, text: String) {
  val intent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_SUBJECT, title)
    putExtra(Intent.EXTRA_TEXT, text)
  }
  context.startActivity(Intent.createChooser(intent, "শেয়ার করুন"))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslamicTopAppBar(
  title: String,
  subtitle: String? = null,
  showBack: Boolean = false,
  onBackClick: () -> Unit = {},
  actions: @Composable RowScope.() -> Unit = {}
) {
  TopAppBar(
    title = {
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        if (subtitle != null) {
          Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    },
    navigationIcon = {
      if (showBack) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier.testTag("nav_back_button")
        ) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "ফিরে যান"
          )
        }
      } else {
        Box(
          modifier = Modifier
            .padding(horizontal = 12.dp)
            .size(36.dp)
            .clip(CircleShape)
            .background(IslamicGold.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "নূর ইসলামিক",
            tint = IslamicGold,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    },
    actions = actions,
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.surface,
      titleContentColor = MaterialTheme.colorScheme.primary,
      actionIconContentColor = MaterialTheme.colorScheme.primary
    )
  )
}

@Composable
fun HadithCard(
  hadith: HadithEntity,
  banglaFontSize: Float = 16f,
  arabicFontSize: Float = 24f,
  onBookmarkClick: () -> Unit,
  onEditClick: (() -> Unit)? = null,
  onDeleteClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  ElevatedCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("hadith_card_${hadith.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.elevatedCardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Header tag row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        SuggestionChip(
          onClick = {},
          label = {
            Text(
              text = hadith.category,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
          },
          colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = IslamicGreenContainer.copy(alpha = 0.5f),
            labelColor = IslamicGreenPrimary
          ),
          border = null,
          modifier = Modifier.height(28.dp)
        )

        Text(
          text = "${hadith.bookName} • ${hadith.hadithNumber}",
          style = MaterialTheme.typography.labelMedium.copy(
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.SemiBold
          )
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Arabic text
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
          .border(
            width = 1.dp,
            color = IslamicGold.copy(alpha = 0.25f),
            shape = RoundedCornerShape(12.dp)
          )
          .padding(14.dp)
      ) {
        Text(
          text = hadith.arabicText,
          style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = arabicFontSize.sp,
            lineHeight = (arabicFontSize * 1.6f).sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Right
          ),
          modifier = Modifier.fillMaxWidth()
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Narrator
      if (hadith.narrator.isNotBlank()) {
        Text(
          text = hadith.narrator,
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
          )
        )
        Spacer(modifier = Modifier.height(6.dp))
      }

      // Bengali translation
      Text(
        text = hadith.banglaText,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontSize = banglaFontSize.sp,
          lineHeight = (banglaFontSize * 1.5f).sp
        )
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Reference footer
      Text(
        text = "রেফারেন্স: ${hadith.reference}",
        style = MaterialTheme.typography.labelSmall.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
      )

      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
      Spacer(modifier = Modifier.height(6.dp))

      // Action Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row {
          IconButton(
            onClick = onBookmarkClick,
            modifier = Modifier.size(36.dp).testTag("bookmark_hadith_${hadith.id}")
          ) {
            Icon(
              imageVector = if (hadith.isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
              contentDescription = "সংরক্ষণ করুন",
              tint = if (hadith.isFavorite) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(
            onClick = {
              val shareContent = """
                |হাদীস: ${hadith.bookName} (${hadith.hadithNumber})
                |
                |${hadith.arabicText}
                |
                |অর্থ:
                |${hadith.banglaText}
                |
                |বর্ণনাকারী: ${hadith.narrator}
                |রেফারেন্স: ${hadith.reference}
                |
                |— নূর ইসলামিক অ্যাপ
              """.trimMargin()
              copyToClipboard(context, "Hadith", shareContent)
            },
            modifier = Modifier.size(36.dp).testTag("copy_hadith_${hadith.id}")
          ) {
            Icon(
              imageVector = Icons.Outlined.ContentCopy,
              contentDescription = "কপি করুন",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(
            onClick = {
              val shareContent = """
                |হাদীস: ${hadith.bookName} (${hadith.hadithNumber})
                |
                |${hadith.arabicText}
                |
                |অনুবাদ:
                |${hadith.banglaText}
                |
                |রেফারেন্স: ${hadith.reference}
                |— নূর ইসলামিক অ্যাপ থেকে সংগৃহীত
              """.trimMargin()
              shareText(context, "${hadith.bookName} ${hadith.hadithNumber}", shareContent)
            },
            modifier = Modifier.size(36.dp).testTag("share_hadith_${hadith.id}")
          ) {
            Icon(
              imageVector = Icons.Outlined.Share,
              contentDescription = "শেয়ার করুন",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        if (onEditClick != null || onDeleteClick != null) {
          Row {
            if (onEditClick != null) {
              IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp)) {
                Icon(
                  imageVector = Icons.Outlined.Edit,
                  contentDescription = "সম্পাদনা",
                  tint = MaterialTheme.colorScheme.primary
                )
              }
            }
            if (onDeleteClick != null) {
              IconButton(onClick = onDeleteClick, modifier = Modifier.size(36.dp)) {
                Icon(
                  imageVector = Icons.Outlined.Delete,
                  contentDescription = "মুছে ফেলুন",
                  tint = MaterialTheme.colorScheme.error
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun MasalaCard(
  masala: MasalaEntity,
  banglaFontSize: Float = 16f,
  onBookmarkClick: () -> Unit,
  onEditClick: (() -> Unit)? = null,
  onDeleteClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var isExpanded by remember { mutableStateOf(false) }
  val context = LocalContext.current

  ElevatedCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("masala_card_${masala.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.elevatedCardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        SuggestionChip(
          onClick = {},
          label = {
            Text(
              text = masala.category,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
          },
          colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = IslamicGoldLight.copy(alpha = 0.5f),
            labelColor = IslamicGoldDark
          ),
          border = null,
          modifier = Modifier.height(28.dp)
        )

        IconButton(
          onClick = onBookmarkClick,
          modifier = Modifier.size(36.dp).testTag("bookmark_masala_${masala.id}")
        ) {
          Icon(
            imageVector = if (masala.isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = "সংরক্ষণ",
            tint = if (masala.isFavorite) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Question
      Text(
        text = "প্রশ্ন: ${masala.question}",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
          fontSize = (banglaFontSize + 1f).sp,
          lineHeight = ((banglaFontSize + 1f) * 1.4f).sp
        )
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Short Answer
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(IslamicGreenContainer.copy(alpha = 0.35f))
          .padding(10.dp)
      ) {
        Text(
          text = "সংক্ষিপ্ত উত্তর: ${masala.shortAnswer}",
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Medium,
            fontSize = banglaFontSize.sp
          )
        )
      }

      // Detailed answer toggle
      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 10.dp)) {
          Text(
            text = "বিস্তারিত ফিকহি ব্যাখ্যা:",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.secondary
            )
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = masala.detailedAnswer,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontSize = banglaFontSize.sp,
              lineHeight = (banglaFontSize * 1.5f).sp
            )
          )

          Spacer(modifier = Modifier.height(8.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
              .padding(8.dp)
          ) {
            Text(
              text = "দলিল ও সূত্র: ${masala.dalil}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        TextButton(
          onClick = { isExpanded = !isExpanded },
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          modifier = Modifier.testTag("expand_masala_${masala.id}")
        ) {
          Text(
            text = if (isExpanded) "সংক্ষেপ দেখুন ▲" else "বিস্তারিত ব্যাখ্যা ও দলিল দেখুন ▼",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          )
        }

        Row {
          IconButton(
            onClick = {
              val content = """
                |মাসআলা: ${masala.question}
                |উত্তর: ${masala.shortAnswer}
                |বিস্তারিত: ${masala.detailedAnswer}
                |দলিল: ${masala.dalil}
                |— নূর ইসলামিক অ্যাপ
              """.trimMargin()
              copyToClipboard(context, "Masala", content)
            },
            modifier = Modifier.size(36.dp).testTag("copy_masala_${masala.id}")
          ) {
            Icon(
              imageVector = Icons.Outlined.ContentCopy,
              contentDescription = "কপি করুন",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(
            onClick = {
              val content = """
                |মাসআলা: ${masala.question}
                |
                |উত্তর: ${masala.shortAnswer}
                |
                |দলিল: ${masala.dalil}
                |— নূর ইসলামিক অ্যাপ
              """.trimMargin()
              shareText(context, masala.question, content)
            },
            modifier = Modifier.size(36.dp).testTag("share_masala_${masala.id}")
          ) {
            Icon(
              imageVector = Icons.Outlined.Share,
              contentDescription = "শেয়ার করুন",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          if (onEditClick != null) {
            IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp)) {
              Icon(Icons.Outlined.Edit, contentDescription = "সম্পাদনা", tint = MaterialTheme.colorScheme.primary)
            }
          }
          if (onDeleteClick != null) {
            IconButton(onClick = onDeleteClick, modifier = Modifier.size(36.dp)) {
              Icon(Icons.Outlined.Delete, contentDescription = "মুছুন", tint = MaterialTheme.colorScheme.error)
            }
          }
        }
      }
    }
  }
}

@Composable
fun MiniAudioPlayerBar(
  audioState: AudioPlayerState,
  onPlayPauseClick: () -> Unit,
  onOpenPlayerClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val surah = audioState.currentSurah ?: return

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(16.dp))
      .clickable { onOpenPlayerClick() }
      .testTag("mini_audio_player"),
    color = IslamicGreenDark,
    contentColor = Color.White,
    shadowElevation = 8.dp
  ) {
    Column {
      // Progress bar line
      if (audioState.durationMs > 0) {
        val progress = (audioState.currentPositionMs.toFloat() / audioState.durationMs.toFloat()).coerceIn(0f, 1f)
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier.fillMaxWidth().height(3.dp),
          color = IslamicGold,
          trackColor = Color.White.copy(alpha = 0.2f)
        )
      } else if (audioState.isLoading) {
        LinearProgressIndicator(
          modifier = Modifier.fillMaxWidth().height(3.dp),
          color = IslamicGold,
          trackColor = Color.White.copy(alpha = 0.2f)
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(IslamicGold.copy(alpha = 0.25f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Headphones,
            contentDescription = "অডিও প্লেয়ার",
            tint = IslamicGold,
            modifier = Modifier.size(22.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "${surah.banglaName} (${surah.arabicName})",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = if (audioState.isLoading) "লোড হচ্ছে..." else surah.reciter,
            style = MaterialTheme.typography.labelSmall,
            color = IslamicGoldLight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        if (audioState.isLoading) {
          CircularProgressIndicator(
            modifier = Modifier.size(28.dp),
            color = IslamicGold,
            strokeWidth = 2.5.dp
          )
        } else {
          IconButton(
            onClick = onPlayPauseClick,
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(IslamicGold)
              .testTag("mini_player_play_pause")
          ) {
            Icon(
              imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = if (audioState.isPlaying) "Pause" else "Play",
              tint = IslamicGreenDark
            )
          }
        }
      }
    }
  }
}
