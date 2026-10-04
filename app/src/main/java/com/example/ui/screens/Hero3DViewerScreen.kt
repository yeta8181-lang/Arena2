package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.graphics3d.Hero3DCanvas
import com.example.graphics3d.RenderStyle
import com.example.model.Hero
import com.example.model.HeroRegistry
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Hero3DViewerScreen(
    initialHeroId: String = "okcu_elf",
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val scope = rememberCoroutineScope()

    var selectedHeroId by remember { mutableStateOf(initialHeroId) }
    val hero = HeroRegistry.getHeroById(selectedHeroId)

    var autoSpin by remember { mutableStateOf(true) }
    var renderStyle by remember { mutableStateOf(RenderStyle.SHADED) }
    var isAttacking by remember { mutableStateOf(false) }
    var showSkillBlast by remember { mutableStateOf(false) }
    var zoomScale by remember { mutableFloatStateOf(1.05f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "💎 3D Kahraman İnceleme",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("viewer_3d_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    // Auto Spin Toggle
                    IconButton(
                        onClick = { autoSpin = !autoSpin },
                        modifier = Modifier.testTag("toggle_auto_spin")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Döndür",
                            tint = if (autoSpin) GoldAccent else TextSecondary
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
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Switcher Carousel
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(HeroRegistry.allHeroes, key = { it.id }) { h ->
                    val isSelected = h.id == selectedHeroId
                    val borderColor = if (isSelected) Color(h.colorHex) else ArenaBorder

                    Card(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable {
                                selectedHeroId = h.id
                                showSkillBlast = false
                            }
                            .testTag("carousel_3d_${h.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) ArenaSurfaceVariant else ArenaSurface
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = h.emoji, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = h.name,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) TextPrimary else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main 3D Canvas Stage
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("canvas_3d_stage"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ArenaSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(hero.colorHex).copy(alpha = 0.5f))
                )
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Render 3D Model
                    Hero3DCanvas(
                        heroId = hero.id,
                        modifier = Modifier.fillMaxSize(),
                        autoSpin = autoSpin,
                        renderStyle = renderStyle,
                        zoomScale = zoomScale,
                        isAttacking = isAttacking,
                        initialRotationY = 0.4f,
                        initialRotationX = 0.2f
                    )

                    // Touch Hint Overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ArenaDarkBg.copy(alpha = 0.75f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "👆 Parmağınla 360° Çevir",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    // Skill Blast Popup FX
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showSkillBlast,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = ArenaDarkBg.copy(alpha = 0.9f)),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(Color(hero.colorHex))
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "💥 3D SALDIRI!", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = GoldAccent)
                                Text(text = "${hero.skillName} Patlaması!", fontSize = 13.sp, color = Color(hero.colorHex), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Action buttons overlay at bottom of 3D canvas
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    isAttacking = true
                                    showSkillBlast = true
                                    delay(800)
                                    isAttacking = false
                                    delay(1000)
                                    showSkillBlast = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(hero.colorHex)),
                            modifier = Modifier.testTag("trigger_3d_skill_button")
                        ) {
                            Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("3D Yeteneği Ateşle!", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3D Shading Mode Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "3D Görünüm:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = renderStyle == RenderStyle.SHADED,
                        onClick = { renderStyle = RenderStyle.SHADED },
                        label = { Text("🎨 Gölgeli 3D", fontSize = 10.sp) },
                        modifier = Modifier.testTag("style_shaded")
                    )
                    FilterChip(
                        selected = renderStyle == RenderStyle.WIREFRAME,
                        onClick = { renderStyle = RenderStyle.WIREFRAME },
                        label = { Text("📐 Tel Kafes", fontSize = 10.sp) },
                        modifier = Modifier.testTag("style_wireframe")
                    )
                    FilterChip(
                        selected = renderStyle == RenderStyle.GLOW_CRYSTAL,
                        onClick = { renderStyle = RenderStyle.GLOW_CRYSTAL },
                        label = { Text("💎 Kristal", fontSize = 10.sp) },
                        modifier = Modifier.testTag("style_crystal")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Hero Details Panel
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ArenaSurface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = hero.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${hero.role.iconEmoji} ${hero.role.displayName}  |  ${hero.title}",
                                fontSize = 11.sp,
                                color = Color(hero.colorHex)
                            )
                        }

                        // Stats summary
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "❤️ ${hero.maxHp}", fontSize = 11.sp, color = HpGreen, fontWeight = FontWeight.Bold)
                            Text(text = "⚔️ ${hero.attack}", fontSize = 11.sp, color = CritRed, fontWeight = FontWeight.Bold)
                            Text(text = "🛡️ ${hero.defense}", fontSize = 11.sp, color = ShieldIce, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "✨ ${hero.skillName}: ${hero.skillDesc}",
                        fontSize = 11.sp,
                        color = GoldAccent,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
