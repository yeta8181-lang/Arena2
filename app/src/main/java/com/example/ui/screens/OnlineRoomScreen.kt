package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HeroRegistry
import com.example.ui.theme.*
import com.example.viewmodel.GameUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineRoomScreen(
    state: GameUiState,
    onGenerateCode: () -> Unit,
    onCodeChange: (String) -> Unit,
    onJoinRoom: () -> Unit,
    onStartBattle: () -> Unit,
    onBackToMenu: () -> Unit
) {
    BackHandler { onBackToMenu() }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🌐 Online 2 Kişilik Oda",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackToMenu,
                        modifier = Modifier.testTag("room_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ArenaSurface)
            )
        },
        containerColor = ArenaDarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Create Room Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_room_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ArenaSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Team1Blue)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "👑 Oda Oluştur (1. Oyuncu - Kurucu)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Team1Blue
                    )
                    Text(
                        text = "Bu kodu arkadaşına ver, odaya katılıp senin takımınla savaşsın!",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ArenaDarkBg)
                            .border(1.dp, ArenaBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.createdRoomCode,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldAccent,
                            letterSpacing = 2.sp
                        )

                        Row {
                            IconButton(onClick = onGenerateCode) {
                                Icon(Icons.Default.Refresh, contentDescription = "Yenile", tint = TextSecondary)
                            }
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Hero Arena Oda Kodu", state.createdRoomCode)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Oda kodu kopyalandı!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.testTag("copy_room_code_button")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Kopyala", tint = GoldAccent)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Senin Takımın: ${state.team1Setup.teamName} (4 Kahraman Hazır)",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    // Display Current Team Icons
                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.team1Setup.selectedHeroIds.forEach { heroId ->
                            val hero = HeroRegistry.getHeroById(heroId)
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ArenaDarkBg)
                                    .border(1.dp, Team1Blue, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = hero.emoji, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }

            // Section 2: Join Room Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("join_room_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ArenaSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Team2Red)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚔️ Arkadaşının Odasına Katıl (2. Oyuncu)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Team2Red
                    )
                    Text(
                        text = "Arkadaşının paylaştığı oda kodunu gir ve hemen savaşa başla:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = state.enteredRoomCode,
                        onValueChange = onCodeChange,
                        placeholder = { Text("Örn: ARENA-5821", color = TextMuted) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("room_code_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Team2Red,
                            unfocusedBorderColor = ArenaBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onJoinRoom,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("join_room_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Team2Red)
                    ) {
                        Text("Odaya Katıl & Savaşa Gir", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Status message
            if (state.roomStatusMessage.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = ArenaSurfaceVariant)
                ) {
                    Text(
                        text = state.roomStatusMessage,
                        fontSize = 12.sp,
                        color = GoldAccent,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Fast launch button
            Button(
                onClick = onStartBattle,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("host_start_button"),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hazır Takımlarla Savaşa Gir", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
