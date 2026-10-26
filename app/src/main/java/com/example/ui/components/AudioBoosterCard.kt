package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AudioBoosterCard(
    boosterPercent: Int, // 100% .. 200%
    onBoosterChanged: (Int) -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val boosterColor by animateColorAsState(
        targetValue = when {
            boosterPercent > 160 -> Color(0xFFFF5722) // Extreme Orange-Red
            boosterPercent > 120 -> Color(0xFFFF9800) // High Orange
            else -> Color(0xFFFFC107)                 // Amber
        },
        label = "boosterColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("audio_booster_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF201633) else Color(0xFFF9F4FF)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = boosterColor.copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(boosterColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = boosterColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AUDIO BOOSTER",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = boosterColor.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (boosterPercent > 100) "+${boosterPercent - 100}%" else "NORMAL",
                                    color = boosterColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Програмне підсилення звуку динаміка",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "$boosterPercent%",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = boosterColor
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Slider with - / + 1% steppers
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onBoosterChanged((boosterPercent - 1).coerceAtLeast(100)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Зменшити на 1%",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Slider(
                    value = boosterPercent.toFloat(),
                    onValueChange = { onBoosterChanged(it.toInt()) },
                    valueRange = 100f..200f,
                    colors = SliderDefaults.colors(
                        thumbColor = boosterColor,
                        activeTrackColor = boosterColor,
                        inactiveTrackColor = if (isDarkTheme) Color(0xFF352650) else Color(0xFFDCCFEF)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("slider_audio_booster")
                )

                IconButton(
                    onClick = { onBoosterChanged((boosterPercent + 1).coerceAtMost(200)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Збільшити на 1%",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Preset Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(100, 125, 150, 200).forEach { preset ->
                    val isSelected = boosterPercent == preset
                    Surface(
                        onClick = { onBoosterChanged(preset) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) boosterColor else (if (isDarkTheme) Color(0xFF2E2248) else Color(0xFFEFE8F8)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$preset%",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
