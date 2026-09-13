package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HadithEntity
import com.example.data.model.MasalaEntity
import com.example.data.model.SurahEntity
import com.example.ui.components.IslamicTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

@Composable
fun AdminScreen(
  viewModel: NoorViewModel,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var isAuthenticated by remember { mutableStateOf(false) }
  var pinInput by remember { mutableStateOf("") }
  var pinError by remember { mutableStateOf(false) }

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Hadith, 1: Masala, 2: Audio/Quran

  // Hadith Form State
  var showHadithDialog by remember { mutableStateOf(false) }
  var editingHadith by remember { mutableStateOf<HadithEntity?>(null) }
  var hBookName by remember { mutableStateOf("সহীহ বুখারী") }
  var hNumber by remember { mutableStateOf("") }
  var hCategory by remember { mutableStateOf("নামাজ") }
  var hNarrator by remember { mutableStateOf("") }
  var hArabic by remember { mutableStateOf("") }
  var hBangla by remember { mutableStateOf("") }
  var hReference by remember { mutableStateOf("") }

  // Masala Form State
  var showMasalaDialog by remember { mutableStateOf(false) }
  var editingMasala by remember { mutableStateOf<MasalaEntity?>(null) }
  var mQuestion by remember { mutableStateOf("") }
  var mCategory by remember { mutableStateOf("নামাজের মাসআলা") }
  var mShortAns by remember { mutableStateOf("") }
  var mDetailAns by remember { mutableStateOf("") }
  var mDalil by remember { mutableStateOf("") }

  // Audio Update State
  var showAudioDialog by remember { mutableStateOf(false) }
  var editingSurah by remember { mutableStateOf<SurahEntity?>(null) }
  var surahAudioUrl by remember { mutableStateOf("") }
  var surahReciter by remember { mutableStateOf("") }

  val hadiths by viewModel.allHadiths.collectAsState()
  val masalas by viewModel.allMasalas.collectAsState()
  val surahs by viewModel.allSurahs.collectAsState()

  Scaffold(
    topBar = {
      IslamicTopAppBar(
        title = "এডমিন প্যানেল",
        subtitle = "কন্টেন্ট ও অডিও পরিচালনা",
        showBack = true,
        onBackClick = onBackClick
      )
    },
    modifier = modifier.fillMaxSize().testTag("admin_screen")
  ) { padding ->
    if (!isAuthenticated) {
      // PIN Authentication Screen
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = IslamicGoldDark,
              modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = "এডমিন লগইন",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Text(
              text = "এডমিন পিন প্রবেশ করান (ডিফল্ট: 7860)",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
              value = pinInput,
              onValueChange = {
                pinInput = it
                pinError = false
              },
              label = { Text("পিন (PIN)") },
              singleLine = true,
              isError = pinError,
              supportingText = { if (pinError) Text("ভুল পিন! পুনরায় চেষ্টা করুন (পিন: 7860)") },
              visualTransformation = PasswordVisualTransformation(),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
              modifier = Modifier.fillMaxWidth().testTag("admin_pin_field")
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = {
                if (pinInput.trim() == "7860" || pinInput.trim() == "1234") {
                  isAuthenticated = true
                  pinError = false
                } else {
                  pinError = true
                }
              },
              modifier = Modifier.fillMaxWidth().testTag("admin_login_submit"),
              colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary)
            ) {
              Text("প্রবেশ করুন")
            }
          }
        }
      }
    } else {
      // Authenticated Content Dashboard
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
      ) {
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = MaterialTheme.colorScheme.surface,
          contentColor = IslamicGreenPrimary
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = { Text("হাদীস (${hadiths.size})") }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = { Text("মাসআলা (${masalas.size})") }
          )
          Tab(
            selected = selectedTab == 2,
            onClick = { selectedTab = 2 },
            text = { Text("অডিও সূরা (${surahs.size})") }
          )
        }

        when (selectedTab) {
          0 -> {
            // Hadith Management
            Box(modifier = Modifier.fillMaxSize()) {
              LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                item {
                  Button(
                    onClick = {
                      editingHadith = null
                      hBookName = "সহীহ বুখারী"
                      hNumber = ""
                      hCategory = "নামাজ"
                      hNarrator = ""
                      hArabic = ""
                      hBangla = ""
                      hReference = ""
                      showHadithDialog = true
                    },
                    modifier = Modifier.fillMaxWidth().testTag("admin_add_hadith_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary)
                  ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("নতুন হাদীস যোগ করুন")
                  }
                }

                items(hadiths, key = { it.id }) { hadith ->
                  Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Text(
                          text = "${hadith.bookName} - ${hadith.hadithNumber} (${hadith.category})",
                          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = IslamicGreenPrimary)
                        )
                        Row {
                          IconButton(
                            onClick = {
                              editingHadith = hadith
                              hBookName = hadith.bookName
                              hNumber = hadith.hadithNumber
                              hCategory = hadith.category
                              hNarrator = hadith.narrator
                              hArabic = hadith.arabicText
                              hBangla = hadith.banglaText
                              hReference = hadith.reference
                              showHadithDialog = true
                            },
                            modifier = Modifier.size(32.dp)
                          ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = IslamicGreenPrimary)
                          }
                          IconButton(
                            onClick = {
                              viewModel.deleteHadith(hadith)
                              Toast.makeText(context, "হাদীসটি মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                          ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                          }
                        }
                      }
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = hadith.banglaText,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                      )
                    }
                  }
                }
              }
            }
          }
          1 -> {
            // Masala Management
            Box(modifier = Modifier.fillMaxSize()) {
              LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                item {
                  Button(
                    onClick = {
                      editingMasala = null
                      mQuestion = ""
                      mCategory = "নামাজের মাসআলা"
                      mShortAns = ""
                      mDetailAns = ""
                      mDalil = ""
                      showMasalaDialog = true
                    },
                    modifier = Modifier.fillMaxWidth().testTag("admin_add_masala_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGoldDark)
                  ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("নতুন মাসআলা যোগ করুন")
                  }
                }

                items(masalas, key = { it.id }) { masala ->
                  Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Text(
                          text = masala.question,
                          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                          modifier = Modifier.weight(1f)
                        )
                        Row {
                          IconButton(
                            onClick = {
                              editingMasala = masala
                              mQuestion = masala.question
                              mCategory = masala.category
                              mShortAns = masala.shortAnswer
                              mDetailAns = masala.detailedAnswer
                              mDalil = masala.dalil
                              showMasalaDialog = true
                            },
                            modifier = Modifier.size(32.dp)
                          ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = IslamicGreenPrimary)
                          }
                          IconButton(
                            onClick = {
                              viewModel.deleteMasala(masala)
                              Toast.makeText(context, "মাসআলা মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                          ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                          }
                        }
                      }
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = "উত্তর: ${masala.shortAnswer}",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                      )
                    }
                  }
                }
              }
            }
          }
          2 -> {
            // Audio Management
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(16.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              item {
                Text(
                  text = "সূরার অডিও লিংক ও ক্বারীর নাম পরিবর্তন করুন",
                  style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
              }

              items(surahs, key = { it.number }) { surah ->
                Card(
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = "${surah.number}. ${surah.banglaName} (${surah.arabicName})",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                      )
                      Text(
                        text = "ক্বারী: ${surah.reciter}",
                        style = MaterialTheme.typography.labelSmall.copy(color = IslamicGoldDark)
                      )
                      Text(
                        text = surah.audioUrl,
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        maxLines = 1
                      )
                    }

                    IconButton(
                      onClick = {
                        editingSurah = surah
                        surahAudioUrl = surah.audioUrl
                        surahReciter = surah.reciter
                        showAudioDialog = true
                      }
                    ) {
                      Icon(Icons.Default.Edit, contentDescription = "Edit Audio", tint = IslamicGreenPrimary)
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

  // Hadith Form Dialog
  if (showHadithDialog) {
    AlertDialog(
      onDismissRequest = { showHadithDialog = false },
      title = { Text(if (editingHadith == null) "নতুন হাদীস যোগ" else "হাদীস সম্পাদনা") },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = hBookName,
            onValueChange = { hBookName = it },
            label = { Text("গ্রন্থের নাম (যেমন: সহীহ বুখারী)") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = hNumber,
            onValueChange = { hNumber = it },
            label = { Text("হাদীস নম্বর (যেমন: ১)") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = hCategory,
            onValueChange = { hCategory = it },
            label = { Text("ক্যাটাগরি (নামাজ/রোজা/আখলাক ইত্যাদি)") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = hNarrator,
            onValueChange = { hNarrator = it },
            label = { Text("বর্ণনাকারী") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = hArabic,
            onValueChange = { hArabic = it },
            label = { Text("আরবি পাঠ (তশকীলসহ)") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = hBangla,
            onValueChange = { hBangla = it },
            label = { Text("বাংলা অনুবাদ") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = hReference,
            onValueChange = { hReference = it },
            label = { Text("রেফারেন্স ও সনদ") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (hBangla.isNotBlank() && hArabic.isNotBlank()) {
              if (editingHadith == null) {
                viewModel.addHadith(
                  HadithEntity(
                    bookName = hBookName.ifBlank { "সহীহ বুখারী" },
                    hadithNumber = hNumber.ifBlank { "০১" },
                    category = hCategory.ifBlank { "সাধারণ" },
                    narrator = hNarrator,
                    arabicText = hArabic,
                    banglaText = hBangla,
                    reference = hReference
                  )
                )
                Toast.makeText(context, "হাদীস সফলভাবে যোগ করা হয়েছে", Toast.LENGTH_SHORT).show()
              } else {
                viewModel.updateHadith(
                  editingHadith!!.copy(
                    bookName = hBookName,
                    hadithNumber = hNumber,
                    category = hCategory,
                    narrator = hNarrator,
                    arabicText = hArabic,
                    banglaText = hBangla,
                    reference = hReference
                  )
                )
                Toast.makeText(context, "হাদীস আপডেট করা হয়েছে", Toast.LENGTH_SHORT).show()
              }
              showHadithDialog = false
            } else {
              Toast.makeText(context, "অনুগ্রহ করে আরবি এবং বাংলা অনুবাদ লিখুন", Toast.LENGTH_SHORT).show()
            }
          }
        ) {
          Text("সংরক্ষণ")
        }
      },
      dismissButton = {
        TextButton(onClick = { showHadithDialog = false }) {
          Text("বাতিল")
        }
      }
    )
  }

  // Masala Form Dialog
  if (showMasalaDialog) {
    AlertDialog(
      onDismissRequest = { showMasalaDialog = false },
      title = { Text(if (editingMasala == null) "নতুন মাসআলা যোগ" else "মাসআলা সম্পাদনা") },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = mQuestion,
            onValueChange = { mQuestion = it },
            label = { Text("প্রশ্ন") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = mCategory,
            onValueChange = { mCategory = it },
            label = { Text("ক্যাটাগরি (নামাজের মাসআলা / রোজার মাসআলা ইত্যাদি)") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = mShortAns,
            onValueChange = { mShortAns = it },
            label = { Text("সংক্ষিপ্ত উত্তর") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = mDetailAns,
            onValueChange = { mDetailAns = it },
            label = { Text("বিস্তারিত ফিকহি ব্যাখ্যা") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = mDalil,
            onValueChange = { mDalil = it },
            label = { Text("দলিল ও নির্ভরযোগ্য সূত্র") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (mQuestion.isNotBlank() && mShortAns.isNotBlank()) {
              if (editingMasala == null) {
                viewModel.addMasala(
                  MasalaEntity(
                    question = mQuestion,
                    category = mCategory.ifBlank { "সাধারণ মাসআলা" },
                    shortAnswer = mShortAns,
                    detailedAnswer = mDetailAns,
                    dalil = mDalil
                  )
                )
                Toast.makeText(context, "মাসআলা যোগ করা হয়েছে", Toast.LENGTH_SHORT).show()
              } else {
                viewModel.updateMasala(
                  editingMasala!!.copy(
                    question = mQuestion,
                    category = mCategory,
                    shortAnswer = mShortAns,
                    detailedAnswer = mDetailAns,
                    dalil = mDalil
                  )
                )
                Toast.makeText(context, "মাসআলা আপডেট হয়েছে", Toast.LENGTH_SHORT).show()
              }
              showMasalaDialog = false
            } else {
              Toast.makeText(context, "প্রশ্ন ও উত্তর উভয়ই পূরণ করুন", Toast.LENGTH_SHORT).show()
            }
          }
        ) {
          Text("সংরক্ষণ")
        }
      },
      dismissButton = {
        TextButton(onClick = { showMasalaDialog = false }) {
          Text("বাতিল")
        }
      }
    )
  }

  // Audio Edit Dialog
  if (showAudioDialog && editingSurah != null) {
    AlertDialog(
      onDismissRequest = { showAudioDialog = false },
      title = { Text("অডিও ও ক্বারী পরিবর্তন: ${editingSurah!!.banglaName}") },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = surahReciter,
            onValueChange = { surahReciter = it },
            label = { Text("ক্বারীর নাম") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = surahAudioUrl,
            onValueChange = { surahAudioUrl = it },
            label = { Text("অনুমোদিত অডিও লিংক (URL)") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.updateSurahAudio(
              editingSurah!!.copy(
                reciter = surahReciter,
                audioUrl = surahAudioUrl
              )
            )
            Toast.makeText(context, "অডিও তথ্য আপডেট হয়েছে", Toast.LENGTH_SHORT).show()
            showAudioDialog = false
          }
        ) {
          Text("সংরক্ষণ")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAudioDialog = false }) {
          Text("বাতিল")
        }
      }
    )
  }
}
