package com.example.ui.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.IslamicTopAppBar
import com.example.ui.components.copyToClipboard
import com.example.ui.components.shareText
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

@Composable
fun BookmarkScreen(
  viewModel: NoorViewModel,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val bookmarks by viewModel.filteredBookmarks.collectAsState()
  val selectedType by viewModel.selectedBookmarkType.collectAsState()

  val types = listOf(
    "ALL" to "সব প্রিয়",
    "HADITH" to "হাদীস",
    "MASALA" to "মাসআলা",
    "AYAH" to "আয়াত"
  )

  Scaffold(
    topBar = {
      IslamicTopAppBar(
        title = "সংরক্ষিত তালিকা",
        subtitle = "আপনার প্রিয় হাদীস, মাসআলা ও আয়াত",
        showBack = true,
        onBackClick = onBackClick
      )
    },
    modifier = modifier.fillMaxSize().testTag("bookmark_screen")
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        types.forEach { (typeKey, title) ->
          val isSelected = selectedType == typeKey
          FilterChip(
            selected = isSelected,
            onClick = { viewModel.setBookmarkType(typeKey) },
            label = { Text(title) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = IslamicGold,
              selectedLabelColor = IslamicGreenDark
            )
          )
        }
      }

      if (bookmarks.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.BookmarkBorder,
              contentDescription = null,
              tint = IslamicGold,
              modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "এখনো কোনো হাদীস, মাসআলা বা আয়াত সংরক্ষণ করেননি।",
              style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
              )
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(bookmarks, key = { "${it.itemType}_${it.itemId}" }) { item ->
            ElevatedCard(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
              modifier = Modifier.fillMaxWidth().testTag("bookmark_item_${item.itemId}")
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  val badgeColor = when (item.itemType) {
                    "HADITH" -> IslamicGreenContainer
                    "MASALA" -> IslamicGoldLight
                    else -> MaterialTheme.colorScheme.surfaceVariant
                  }
                  val badgeTextColor = when (item.itemType) {
                    "HADITH" -> IslamicGreenPrimary
                    "MASALA" -> IslamicGoldDark
                    else -> MaterialTheme.colorScheme.primary
                  }
                  val typeLabel = when (item.itemType) {
                    "HADITH" -> "হাদীস"
                    "MASALA" -> "মাসআলা"
                    else -> "আয়াত"
                  }

                  SuggestionChip(
                    onClick = {},
                    label = { Text(typeLabel, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                      containerColor = badgeColor,
                      labelColor = badgeTextColor
                    ),
                    border = null,
                    modifier = Modifier.height(26.dp)
                  )

                  IconButton(
                    onClick = { viewModel.removeBookmark(item.itemType, item.itemId) },
                    modifier = Modifier.size(32.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.BookmarkRemove,
                      contentDescription = "মুছুন",
                      tint = MaterialTheme.colorScheme.error
                    )
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = item.title,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )
                )

                if (item.arabicSnippet.isNotBlank()) {
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = item.arabicSnippet,
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontFamily = FontFamily.Serif,
                      textAlign = TextAlign.Right
                    ),
                    modifier = Modifier.fillMaxWidth()
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = item.subtitle,
                  style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.End
                ) {
                  IconButton(onClick = {
                    copyToClipboard(context, item.title, "${item.title}\n\n${item.arabicSnippet}\n\n${item.subtitle}\n— নূর ইসলামিক অ্যাপ")
                  }) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = "কপি", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                  }

                  IconButton(onClick = {
                    shareText(context, item.title, "${item.title}\n\n${item.arabicSnippet}\n\n${item.subtitle}\n— নূর ইসলামিক অ্যাপ")
                  }) {
                    Icon(Icons.Outlined.Share, contentDescription = "শেয়ার", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
