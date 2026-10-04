package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CombatLogEntry
import com.example.ui.theme.*

@Composable
fun CombatLogView(
    logs: List<CombatLogEntry>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("combat_log_view"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurface.copy(alpha = 0.95f)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📜 Savaş Günlüğü",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )
                Text(
                    text = "${logs.size} Olay",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (logs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Savaş başlıyor... Olaylar burada listelenecek.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(logs, key = { it.id }) { log ->
                        val seconds = log.timestampSeconds.toInt()
                        val timeStr = String.format("%02d:%02d", seconds / 60, seconds % 60)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "[$timeStr]",
                                fontSize = 9.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(log.colorHex).copy(alpha = 0.2f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = log.tag,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(log.colorHex)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = log.message,
                                fontSize = 10.sp,
                                color = TextPrimary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
