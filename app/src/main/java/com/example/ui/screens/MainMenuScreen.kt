package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.viewmodel.GameMode
import com.example.viewmodel.GameUiState

@Composable
fun MainMenuScreen(
    state: GameUiState,
    onSelectMode: (GameMode) -> Unit,
    onOpenGuide: () -> Unit,
    onOpen3DViewer: () -> Unit = {},
    onFastBattle: () -> Unit
) {
    var showGodotDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Scaffold(
        containerColor = ArenaDarkBg,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onFastBattle,
                containerColor = GoldAccent,
                contentColor = Color.Black,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .testTag("main_fast_battle_fab")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hemen Savaş!", fontWeight = FontWeight.ExtraBold)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Hero Banner Art with Gradient
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_arena_hero),
                        contentDescription = "Hero Arena Colosseum",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        ArenaDarkBg.copy(alpha = 0.5f),
                                        ArenaDarkBg
                                    )
                                )
                            )
                    )

                    // Title & Trophy Overlay
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GoldAccent)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "2 KİŞİLİK OTO-SAVAŞ",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ArenaSurfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "🏆 ${state.playerTrophies} Kupa",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "HERO ARENA",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "10 Efsanevi Kahraman, Taktiksel Dizilimler ve Otomatik Düellolar",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Mode Selection Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Oyun Modunu Seç",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mode 1: 2-Player Pass and Play
                    GameModeCard(
                        title = GameMode.PASS_AND_PLAY.title,
                        subtitle = GameMode.PASS_AND_PLAY.subtitle,
                        emoji = GameMode.PASS_AND_PLAY.iconEmoji,
                        badge = "ÖNERİLEN",
                        borderColor = Team1Blue,
                        tag = "mode_pass_and_play",
                        onClick = { onSelectMode(GameMode.PASS_AND_PLAY) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mode 2: Online Room
                    GameModeCard(
                        title = GameMode.ONLINE_ROOM.title,
                        subtitle = GameMode.ONLINE_ROOM.subtitle,
                        emoji = GameMode.ONLINE_ROOM.iconEmoji,
                        badge = "ONLINE 2 KİŞİ",
                        borderColor = Team2Red,
                        tag = "mode_online_room",
                        onClick = { onSelectMode(GameMode.ONLINE_ROOM) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mode 3: Online Matchmaking
                    GameModeCard(
                        title = GameMode.MATCHMAKING.title,
                        subtitle = GameMode.MATCHMAKING.subtitle,
                        emoji = GameMode.MATCHMAKING.iconEmoji,
                        badge = "SIRALAMALI",
                        borderColor = GoldAccent,
                        tag = "mode_matchmaking",
                        onClick = { onSelectMode(GameMode.MATCHMAKING) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mode 4: Practice
                    GameModeCard(
                        title = GameMode.PRACTICE.title,
                        subtitle = GameMode.PRACTICE.subtitle,
                        emoji = GameMode.PRACTICE.iconEmoji,
                        badge = "DENEME",
                        borderColor = ArenaBorder,
                        tag = "mode_practice",
                        onClick = { onSelectMode(GameMode.PRACTICE) }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3D Hero Showcase Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, GoldAccent.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                            .clickable { onOpen3DViewer() }
                            .testTag("open_3d_viewer"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ArenaSurface)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(125.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_3d_showcase),
                                    contentDescription = "3D Hero Showcase",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color.Transparent, ArenaDarkBg.copy(alpha = 0.85f))
                                            )
                                        )
                                    )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(GoldAccent)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("360° 3D MODELLER", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "💎 3D Kahraman Laboratuvarı",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                    Text(
                                        text = "10 kahramanı 3D incele, 360° döndür, tel kafes & kristal modları dene!",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Hero Guide Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, ArenaBorder, RoundedCornerShape(14.dp))
                            .clickable { onOpenGuide() }
                            .testTag("open_hero_guide"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = ArenaSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(ArenaSurfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Kahraman Rehberi (10 Kahraman)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Okçu Elf, Siyah Ork, Taş Golemi, Robot Tank vb. detayları",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Godot Project Info Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, Color(0xFF478CBF).copy(alpha = 0.8f), RoundedCornerShape(14.dp))
                            .clickable { showGodotDialog = true }
                            .testTag("open_godot_info"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = ArenaSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF478CBF).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🤖", fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "Godot Engine 4 Projesi",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF478CBF).copy(alpha = 0.3f))
                                                .padding(horizontal = 6.dp, vertical = 1.dp)
                                        ) {
                                            Text("HAZIR", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF478CBF))
                                        }
                                    }
                                    Text(
                                        text = "godot_project/ klasöründe tüm 3D sahneler & GDScript hazır",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF478CBF),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showGodotDialog) {
        AlertDialog(
            onDismissRequest = { showGodotDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🤖", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Godot Engine 4 Projesi Hazır", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Oyunun tüm kaynak kodları, 3D sahneleri ve GDScript motoru Godot Engine 4 için hazırlandı:",
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "📁 godot_project/\n" +
                                "  ├── project.godot (Ana Godot 4.x ayarları)\n" +
                                "  ├── scenes/ (main_menu, battle_arena, hero_viewer_3d)\n" +
                                "  └── scripts/ (hero_registry, battle_engine, hero_3d_mesh_builder)\n\n" +
                                "🎮 Godot Engine 4.x ile 'Import' diyerek project.godot dosyasını açıp doğrudan çalıştırabilir veya Android/PC/Web çıktısı alabilirsiniz!",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showGodotDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF478CBF))
                ) {
                    Text("Harika!")
                }
            }
        )
    }
}

@Composable
private fun GameModeCard(
    title: String,
    subtitle: String,
    emoji: String,
    badge: String,
    borderColor: Color,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.5.dp, borderColor.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(tag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(ArenaSurfaceVariant)
                    .border(1.dp, borderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(borderColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = borderColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }

            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = borderColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
