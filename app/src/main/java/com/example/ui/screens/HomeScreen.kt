package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.HadithEntity
import com.example.data.model.SurahEntity
import com.example.ui.components.HadithCard
import com.example.ui.components.copyToClipboard
import com.example.ui.components.shareText
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

data class HomeNavCard(
  val title: String,
  val subtitle: String,
  val icon: ImageVector,
  val route: String,
  val color: Color,
  val iconTint: Color
)

@Composable
fun HomeScreen(
  viewModel: NoorViewModel,
  onNavigateToTab: (String) -> Unit,
  onOpenSurah: (SurahEntity) -> Unit,
  onOpenAudioPlayer: (SurahEntity?) -> Unit,
  onOpenSearch: () -> Unit,
  onOpenBookmarks: () -> Unit,
  onOpenSettings: () -> Unit,
  onOpenAdmin: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val settings by viewModel.settings.collectAsState()
  val dailyHadith by viewModel.dailyHadith.collectAsState()
  val dailyAyahPair by viewModel.dailyAyah.collectAsState()
  val surahs by viewModel.allSurahs.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("home_screen"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // 1. Top Islamic Hero Header
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
          .background(
            Brush.verticalGradient(
              colors = listOf(IslamicGreenDark, IslamicGreenPrimary)
            )
          )
      ) {
        // Banner overlay if drawable exists
        Image(
          painter = painterResource(id = R.drawable.banner_islamic),
          contentDescription = "নূর ইসলামিক ব্যানার",
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)),
          contentScale = ContentScale.Crop,
          alpha = 0.35f
        )

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(IslamicGold.copy(alpha = 0.25f))
                  .border(1.5.dp, IslamicGold, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.MenuBook,
                  contentDescription = "Noor Logo",
                  tint = IslamicGold,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "নূর ইসলামিক",
                  style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                  )
                )
                Text(
                  text = "হাদীস • মাসআলা • কুরআন",
                  style = MaterialTheme.typography.labelMedium.copy(
                    color = IslamicGoldLight,
                    fontWeight = FontWeight.Medium
                  )
                )
              }
            }

            Row {
              IconButton(
                onClick = onOpenSearch,
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(Color.White.copy(alpha = 0.15f))
                  .testTag("home_search_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = "সার্চ করুন",
                  tint = Color.White
                )
              }

              Spacer(modifier = Modifier.width(8.dp))

              IconButton(
                onClick = onOpenBookmarks,
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(Color.White.copy(alpha = 0.15f))
                  .testTag("home_bookmarks_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Bookmark,
                  contentDescription = "সংরক্ষিত",
                  tint = IslamicGold
                )
              }

              Spacer(modifier = Modifier.width(8.dp))

              IconButton(
                onClick = onOpenAdmin,
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(Color.White.copy(alpha = 0.15f))
                  .testTag("home_admin_button")
              ) {
                Icon(
                  imageVector = Icons.Default.AdminPanelSettings,
                  contentDescription = "এডমিন প্যানেল",
                  tint = IslamicGoldLight
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Daily greeting & Bismillah
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = Color.Black.copy(alpha = 0.25f)
            ),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.SemiBold,
                  color = IslamicGoldLight,
                  fontSize = (settings.arabicFontSize * 0.9f).sp
                )
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "পরম করুণাময় ও অসীম দয়ালু আল্লাহর নামে শুরু করছি",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color.White.copy(alpha = 0.9f),
                  textAlign = TextAlign.Center
                )
              )
            }
          }
        }
      }
    }

    // 2. Navigation Cards (Requirement 1)
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 16.dp)
      ) {
        Text(
          text = "ইসলামিক বিভাগসমূহ",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        val navCards = listOf(
          HomeNavCard("📖 হাদীস", "সহীহ হাদীসের সংকলন", Icons.Default.MenuBook, "HADITH", IslamicGreenContainer, IslamicGreenPrimary),
          HomeNavCard("🕌 মাসআলা", "দৈনন্দিন শরয়ী বিধান", Icons.Default.AccountBalance, "MASALA", IslamicGoldLight, IslamicGoldDark),
          HomeNavCard("📜 সূরা", "কুরআন তেলাওয়াত ও অর্থ", Icons.Default.AutoStories, "QURAN", IslamicGreenContainer, IslamicGreenPrimary),
          HomeNavCard("🎧 অডিও কুরআন", "মনমুগ্ধকর সুললিত তেলাওয়াত", Icons.Default.Headphones, "AUDIO", IslamicGoldLight, IslamicGoldDark),
          HomeNavCard("🔍 সার্চ", "হাদীস, মাসআলা ও আয়াত", Icons.Default.Search, "SEARCH", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.primary),
          HomeNavCard("⭐ প্রিয় তালিকা", "আমার সংরক্ষিত কালেকশন", Icons.Default.Star, "BOOKMARK", IslamicGoldLight.copy(alpha = 0.7f), IslamicGoldDark),
          HomeNavCard("⚙️ সেটিংস", "ফন্ট, থিম ও পরিচিতি", Icons.Default.Settings, "SETTINGS", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant),
          HomeNavCard("👑 এডমিন প্যানেল", "হাদীস, মাসআলা ও অডিও যোগ", Icons.Default.AdminPanelSettings, "ADMIN", IslamicGoldLight, IslamicGoldDark)
        )

        // Display in clean responsive grid
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          // Row 1: Hadith & Masala
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            HomeGridCard(
              card = navCards[0],
              modifier = Modifier.weight(1f),
              onClick = { onNavigateToTab("HADITH") }
            )
            HomeGridCard(
              card = navCards[1],
              modifier = Modifier.weight(1f),
              onClick = { onNavigateToTab("MASALA") }
            )
          }

          // Row 2: Surah & Audio
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            HomeGridCard(
              card = navCards[2],
              modifier = Modifier.weight(1f),
              onClick = { onNavigateToTab("QURAN") }
            )
            HomeGridCard(
              card = navCards[3],
              modifier = Modifier.weight(1f),
              onClick = { onOpenAudioPlayer(null) }
            )
          }

          // Row 3: Search & Bookmark
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            HomeGridCard(
              card = navCards[4],
              modifier = Modifier.weight(1f),
              onClick = onOpenSearch
            )
            HomeGridCard(
              card = navCards[5],
              modifier = Modifier.weight(1f),
              onClick = onOpenBookmarks
            )
          }

          // Row 4: Settings & Admin Panel
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            HomeGridCard(
              card = navCards[6],
              modifier = Modifier.weight(1f),
              onClick = onOpenSettings
            )
            HomeGridCard(
              card = navCards[7],
              modifier = Modifier.weight(1f),
              onClick = onOpenAdmin
            )
          }
        }
      }
    }

    // 3. Featured Surahs (সূরা আল-মুলক ও সূরা আর-রহমান)
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "বিশেষ সূরা ও অডিও",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          )
          TextButton(onClick = { onNavigateToTab("QURAN") }) {
            Text("সবগুলো দেখুন", style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.secondary))
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val mulkSurah = surahs.find { it.number == 67 }
        val rahmanSurah = surahs.find { it.number == 55 }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          if (mulkSurah != null) {
            FeaturedSurahCard(
              surah = mulkSurah,
              modifier = Modifier.weight(1f),
              onReadClick = { onOpenSurah(mulkSurah) },
              onPlayClick = {
                viewModel.playSurah(mulkSurah)
                onOpenAudioPlayer(mulkSurah)
              }
            )
          }
          if (rahmanSurah != null) {
            FeaturedSurahCard(
              surah = rahmanSurah,
              modifier = Modifier.weight(1f),
              onReadClick = { onOpenSurah(rahmanSurah) },
              onPlayClick = {
                viewModel.playSurah(rahmanSurah)
                onOpenAudioPlayer(rahmanSurah)
              }
            )
          }
        }
      }
    }

    // 4. Daily Ayah (আজকের আয়াত - Requirement 9)
    item {
      dailyAyahPair?.let { (surah, ayah) ->
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "আজকের আয়াত",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              )
            }

            Text(
              text = "${surah.banglaName} (আয়াত ${ayah.ayahNumber})",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
              )
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surface
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.3f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = ayah.arabicText,
                style = MaterialTheme.typography.bodyLarge.copy(
                  fontSize = settings.arabicFontSize.sp,
                  lineHeight = (settings.arabicFontSize * 1.6f).sp,
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.Medium,
                  textAlign = TextAlign.Right
                ),
                modifier = Modifier.fillMaxWidth()
              )

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = "উচ্চারণ: ${ayah.banglaPronunciation}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontWeight = FontWeight.Medium
                )
              )

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = ayah.banglaTranslation,
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontSize = settings.banglaFontSize.sp,
                  fontWeight = FontWeight.Normal
                )
              )

              Spacer(modifier = Modifier.height(10.dp))
              HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
              Spacer(modifier = Modifier.height(6.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row {
                  IconButton(
                    onClick = {
                      viewModel.toggleFavoriteAyah(ayah, surah.banglaName)
                    },
                    modifier = Modifier.size(36.dp)
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
                        |আয়াত: ${surah.banglaName} (${ayah.ayahNumber})
                        |
                        |${ayah.arabicText}
                        |
                        |উচ্চারণ: ${ayah.banglaPronunciation}
                        |অনুবাদ: ${ayah.banglaTranslation}
                        |— নূর ইসলামিক অ্যাপ
                      """.trimMargin()
                      copyToClipboard(context, "Daily Ayah", content)
                    },
                    modifier = Modifier.size(36.dp)
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
                        |কুরআনের বাণী: ${surah.banglaName} (আয়াত ${ayah.ayahNumber})
                        |
                        |${ayah.arabicText}
                        |
                        |অনুবাদ: ${ayah.banglaTranslation}
                        |— নূর ইসলামিক অ্যাপ
                      """.trimMargin()
                      shareText(context, "${surah.banglaName} আয়াত ${ayah.ayahNumber}", content)
                    },
                    modifier = Modifier.size(36.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Outlined.Share,
                      contentDescription = "শেয়ার",
                      tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                FilledTonalButton(
                  onClick = { onOpenSurah(surah) },
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                  Text("সম্পূর্ণ সূরা পড়ুন", style = MaterialTheme.typography.labelSmall)
                }
              }
            }
          }
        }
      }
    }

    // 5. Daily Hadith (আজকের হাদীস - Requirement 8)
    item {
      dailyHadith?.let { hadith ->
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                tint = IslamicGreenPrimary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "আজকের হাদীস",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              )
            }

            Text(
              text = "${hadith.bookName} • ${hadith.hadithNumber}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
              )
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          HadithCard(
            hadith = hadith,
            banglaFontSize = settings.banglaFontSize,
            arabicFontSize = settings.arabicFontSize,
            onBookmarkClick = { viewModel.toggleFavoriteHadith(hadith) }
          )
        }
      }
    }
  }
}

@Composable
fun HomeGridCard(
  card: HomeNavCard,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  ElevatedCard(
    onClick = onClick,
    modifier = modifier.height(100.dp),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(card.color),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = card.icon,
          contentDescription = card.title,
          tint = card.iconTint,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = card.title,
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = card.subtitle,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

@Composable
fun HomeSmallCard(
  card: HomeNavCard,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  OutlinedCard(
    onClick = onClick,
    modifier = modifier.height(84.dp),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = card.icon,
        contentDescription = card.title,
        tint = card.iconTint,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = card.title,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
fun FeaturedSurahCard(
  surah: SurahEntity,
  onReadClick: () -> Unit,
  onPlayClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  ElevatedCard(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.elevatedCardColors(
      containerColor = if (surah.number == 67) IslamicGreenContainer.copy(alpha = 0.4f) else IslamicGoldLight.copy(alpha = 0.4f)
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(IslamicGreenPrimary),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "${surah.number}",
            style = MaterialTheme.typography.labelMedium.copy(
              color = Color.White,
              fontWeight = FontWeight.Bold
            )
          )
        }

        Text(
          text = surah.arabicName,
          style = MaterialTheme.typography.titleMedium.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            color = IslamicGreenDark
          )
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = surah.banglaName,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
      )

      Text(
        text = "${surah.meaning} • ${surah.totalAyahs} আয়াত",
        style = MaterialTheme.typography.bodySmall.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        )
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = onReadClick,
          modifier = Modifier.weight(1f).height(36.dp),
          contentPadding = PaddingValues(horizontal = 4.dp),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("পড়ুন", style = MaterialTheme.typography.labelSmall)
        }

        Button(
          onClick = onPlayClick,
          modifier = Modifier.weight(1f).height(36.dp),
          contentPadding = PaddingValues(horizontal = 4.dp),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary)
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(2.dp))
          Text("শুনুন", style = MaterialTheme.typography.labelSmall)
        }
      }
    }
  }
}
