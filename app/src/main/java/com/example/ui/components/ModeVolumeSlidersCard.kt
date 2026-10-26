package com.example.ui.components

import android.media.AudioManager
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProfileSettings
import com.example.model.VolumeMode

@Composable
fun ModeVolumeSlidersCard(
    mode: VolumeMode,
    settings: ProfileSettings,
    isDarkTheme: Boolean,
    playingStreamType: Int?, // AudioManager.STREAM_* or null
    onSettingsChanged: (ProfileSettings) -> Unit,
    onTestSoundClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = if (isDarkTheme) mode.darkAccentColor else mode.lightAccentColor

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("mode_volume_sliders_card"),
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = mode.icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Гучність: ${mode.title}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Точне налаштування з кроком 1%",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 1. Ringtone Stream
            PreciseVolumeSliderRow(
                icon = Icons.Default.Phone,
                label = "Дзвінки та виклики",
                percent = settings.ringPercent,
                accentColor = accentColor,
                isDarkTheme = isDarkTheme,
                isPlaying = playingStreamType == AudioManager.STREAM_RING,
                onValueChange = { onSettingsChanged(settings.copy(ringPercent = it)) },
                onTestClick = { onTestSoundClicked(AudioManager.STREAM_RING) },
                testTag = "slider_ring_volume"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Notification Stream
            PreciseVolumeSliderRow(
                icon = Icons.Default.Notifications,
                label = "Сповіщення",
                percent = settings.notificationPercent,
                accentColor = accentColor,
                isDarkTheme = isDarkTheme,
                isPlaying = playingStreamType == AudioManager.STREAM_NOTIFICATION,
                onValueChange = { onSettingsChanged(settings.copy(notificationPercent = it)) },
                onTestClick = { onTestSoundClicked(AudioManager.STREAM_NOTIFICATION) },
                testTag = "slider_notif_volume"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Media Stream
            PreciseVolumeSliderRow(
                icon = Icons.Default.MusicNote,
                label = "Медіа та музика",
                percent = settings.mediaPercent,
                accentColor = accentColor,
                isDarkTheme = isDarkTheme,
                isPlaying = playingStreamType == AudioManager.STREAM_MUSIC,
                onValueChange = { onSettingsChanged(settings.copy(mediaPercent = it)) },
                onTestClick = { onTestSoundClicked(AudioManager.STREAM_MUSIC) },
                testTag = "slider_media_volume"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Subdued auto-save footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = accentColor.copy(alpha = 0.8f),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Зміни зберігаються автоматично",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@Composable
private fun PreciseVolumeSliderRow(
    icon: ImageVector,
    label: String,
    percent: Int,
    accentColor: Color,
    isDarkTheme: Boolean,
    isPlaying: Boolean,
    onValueChange: (Int) -> Unit,
    onTestClick: () -> Unit,
    testTag: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Test Sound Button
                OutlinedButton(
                    onClick = onTestClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isPlaying) Color(0xFFFF5252) else accentColor
                    ),
                    modifier = Modifier
                        .height(30.dp)
                        .padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isPlaying) "Стоп" else "Тест",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "$percent%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }
        }

        // Stepper - / Slider / Stepper +
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onValueChange((percent - 1).coerceAtLeast(0)) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Зменшити на 1%",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Slider(
                value = percent.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = accentColor,
                    activeTrackColor = accentColor,
                    inactiveTrackColor = if (isDarkTheme) Color(0xFF2E2248) else Color(0xFFE4DAF2)
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag(testTag)
            )

            IconButton(
                onClick = { onValueChange((percent + 1).coerceAtMost(100)) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Збільшити на 1%",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
