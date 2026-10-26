package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProfileSettings

@Composable
fun ScheduleAutomationCard(
    settings: ProfileSettings,
    isDarkTheme: Boolean,
    onSettingsChanged: (ProfileSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("schedule_automation_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF1D162E) else Color(0xFFFFFFFF)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isDarkTheme) Color(0xFF3E315C) else Color(0xFFDDD2ED)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Розклад та дні спрацювання",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Автоматичне вмикання профілю за часом",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = settings.isScheduleEnabled,
                    onCheckedChange = { onSettingsChanged(settings.copy(isScheduleEnabled = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF221147),
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = if (isDarkTheme) Color(0xFF2B2144) else Color(0xFFE2D8F0)
                    )
                )
            }

            AnimatedVisibility(
                visible = settings.isScheduleEnabled,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Text(
                        text = "Дні тижня:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val days = listOf(
                        1 to "Пн",
                        2 to "Вт",
                        3 to "Ср",
                        4 to "Чт",
                        5 to "Пт",
                        6 to "Сб",
                        7 to "Нд"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        days.forEach { (dayIndex, dayLabel) ->
                            val isSelected = settings.scheduleDays.contains(dayIndex)
                            val chipColor = MaterialTheme.colorScheme.primary

                            Surface(
                                onClick = {
                                    val newDays = if (isSelected) {
                                        if (settings.scheduleDays.size > 1) settings.scheduleDays - dayIndex else settings.scheduleDays
                                    } else {
                                        settings.scheduleDays + dayIndex
                                    }
                                    onSettingsChanged(settings.copy(scheduleDays = newDays))
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) chipColor else (if (isDarkTheme) Color(0xFF271F3B) else Color(0xFFF3EEFA)),
                                modifier = Modifier
                                    .size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = dayLabel,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Time range
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TimePickerPill(
                            title = "Початок",
                            time = settings.scheduleStartTime,
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier.weight(1f),
                            onTimeSelected = { onSettingsChanged(settings.copy(scheduleStartTime = it)) }
                        )

                        TimePickerPill(
                            title = "Завершення",
                            time = settings.scheduleEndTime,
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier.weight(1f),
                            onTimeSelected = { onSettingsChanged(settings.copy(scheduleEndTime = it)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimePickerPill(
    title: String,
    time: String,
    isDarkTheme: Boolean,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isDarkTheme) Color(0xFF261D3A) else Color(0xFFF5EFFB),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF453666) else Color(0xFFDED3ED)
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = time,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
