package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Hero
import com.example.ui.theme.*

@Composable
fun HeroDetailDialog(
    hero: Hero,
    onDismiss: () -> Unit,
    onOpen3DViewer: (() -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("hero_detail_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ArenaSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color(hero.colorHex))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color(hero.colorHex).copy(alpha = 0.2f))
                                .border(2.dp, Color(hero.colorHex), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = hero.emoji, fontSize = 28.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = hero.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = hero.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(hero.colorHex)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_detail_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3D Interactive Model Stage
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ArenaDarkBg)
                        .border(1.dp, Color(hero.colorHex).copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                ) {
                    com.example.graphics3d.Hero3DCanvas(
                        heroId = hero.id,
                        modifier = Modifier.fillMaxSize(),
                        autoSpin = true,
                        zoomScale = 1.0f
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(ArenaSurfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "💎 3D Model", fontSize = 9.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Role & Range Badge
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("${hero.role.iconEmoji} ${hero.role.displayName}") },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = ArenaSurfaceVariant,
                            labelColor = TextPrimary
                        )
                    )
                    SuggestionChip(
                        onClick = {},
                        label = { Text(if (hero.attackRange == com.example.model.AttackRange.MELEE) "⚔️ Yakın Dövüş" else "🏹 Menzilli") },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = ArenaSurfaceVariant,
                            labelColor = TextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Grid
                Text(
                    text = "Savaş İstatistikleri",
                    style = MaterialTheme.typography.labelLarge,
                    color = GoldAccent,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatBox(label = "Can (HP)", value = "${hero.maxHp}", color = HpGreen, icon = "❤️")
                    StatBox(label = "Saldırı", value = "${hero.attack}", color = CritRed, icon = "⚔️")
                    StatBox(label = "Zırh", value = "${hero.defense}", color = ShieldIce, icon = "🛡️")
                    StatBox(label = "Hız", value = "${hero.attackSpeedSeconds}s", color = GoldAccent, icon = "⚡")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Signature Skill Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ArenaSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✨", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Özel Yetenek: ${hero.skillName}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = hero.skillDesc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Lore
                Text(
                    text = hero.lore,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dismiss_hero_detail"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(hero.colorHex))
                ) {
                    Text("Tamam", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, color: Color, icon: String) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaDarkBg),
        modifier = Modifier.width(72.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 14.sp)
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}
