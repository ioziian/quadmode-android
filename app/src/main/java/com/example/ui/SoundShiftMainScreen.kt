package com.example.ui

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ProfileSettings
import com.example.model.VolumeMode
import com.example.ui.components.AudioBoosterCard
import com.example.ui.components.ModeVolumeSlidersCard
import com.example.ui.components.ScheduleAutomationCard
import com.example.ui.components.SystemModesCard
import com.example.ui.components.VerticalSteppedSwitch
import com.example.ui.viewmodel.VolumeUiState

@Composable
fun SoundShiftMainScreen(
    uiState: VolumeUiState,
    isDarkTheme: Boolean,
    onModeSelected: (VolumeMode) -> Unit,
    onProfileChanged: (ProfileSettings) -> Unit,
    onToggleTheme: () -> Unit,
    onToggleNotification: (Boolean) -> Unit,
    onTestSoundClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val currentModeColor by animateColorAsState(
        targetValue = if (isDarkTheme) uiState.currentMode.darkAccentColor else uiState.currentMode.lightAccentColor,
        label = "modeColor"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Adaptive Container Width
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header (Notice: Badge on right is REMOVED per user instruction; Theme Toggle is here)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "QuadMode",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "4 режими. 1 жест.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Theme Switch Button (Moon / Sun)
                    Surface(
                        onClick = onToggleTheme,
                        shape = CircleShape,
                        color = if (isDarkTheme) Color(0xFF261D3B) else Color(0xFFEFE8F8),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDarkTheme) Color(0xFF4C3D74) else Color(0xFFD4C8E8)
                        ),
                        modifier = Modifier.testTag("theme_toggle_button")
                    ) {
                        Box(
                            modifier = Modifier.padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Змінити тему оформлення",
                                tint = if (isDarkTheme) Color(0xFFFFD54F) else Color(0xFF5E35B1),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // DND Permission Banner if needed
                AnimatedVisibility(
                    visible = !uiState.hasNotificationPolicyAccess && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .testTag("dnd_permission_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkTheme) Color(0xFF2C1935) else Color(0xFFFDE8F1)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD81B60))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFD81B60),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.dnd_permission_title),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isDarkTheme) Color.White else Color(0xFF880E4F)
                                )
                                Text(
                                    text = stringResource(R.string.dnd_permission_desc),
                                    fontSize = 12.sp,
                                    color = if (isDarkTheme) Color(0xFFF8BBD0) else Color(0xFFAD1457)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD81B60)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("grant_dnd_button")
                            ) {
                                Text("Надати", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // 4-Position Stepped Switch Hero Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("switch_hero_card"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF1B142B) else Color(0xFFFFFFFF)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDarkTheme) Color(0xFF3B2E58) else Color(0xFFDDD3EC)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ПЕРЕМИКАЧ 4 РЕЖИМІВ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // The 4-step vertical toggle
                        VerticalSteppedSwitch(
                            currentMode = uiState.currentMode,
                            isDarkTheme = isDarkTheme,
                            onModeSelected = onModeSelected
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Real-time audio streams monitor
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDarkTheme) Color(0xFF110D1D) else Color(0xFFF4EEFB))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RealtimeVolumeIndicator(
                                title = "Дзвінки",
                                percent = uiState.actualRingPercent,
                                color = currentModeColor
                            )
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(24.dp)
                                    .background(if (isDarkTheme) Color(0xFF332750) else Color(0xFFDDD2ED))
                            )
                            RealtimeVolumeIndicator(
                                title = "Сповіщення",
                                percent = uiState.actualNotificationPercent,
                                color = currentModeColor
                            )
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(24.dp)
                                    .background(if (isDarkTheme) Color(0xFF332750) else Color(0xFFDDD2ED))
                            )
                            RealtimeVolumeIndicator(
                                title = "Медіа",
                                percent = uiState.actualMediaPercent,
                                color = currentModeColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Highlighted Audio Booster Section (Active for all modes, with booster amplification)
                AudioBoosterCard(
                    boosterPercent = uiState.currentProfile.boosterPercent,
                    onBoosterChanged = { newBooster ->
                        onProfileChanged(uiState.currentProfile.copy(boosterPercent = newBooster))
                    },
                    isDarkTheme = isDarkTheme
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Mode Volume Sliders (Ring, Notification, Media with 1% steps + "Тест" sound playback)
                ModeVolumeSlidersCard(
                    mode = uiState.currentMode,
                    settings = uiState.currentProfile,
                    isDarkTheme = isDarkTheme,
                    playingStreamType = uiState.playingTestStream,
                    onSettingsChanged = onProfileChanged,
                    onTestSoundClicked = onTestSoundClicked
                )

                Spacer(modifier = Modifier.height(14.dp))

                // System Modes Toggles (Mute, DND, Airplane, Reading, Auto brightness, Battery saver, Vibrate, Dark mode)
                SystemModesCard(
                    settings = uiState.currentProfile,
                    isDarkTheme = isDarkTheme,
                    onSettingsChanged = onProfileChanged
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Schedule & Days of Week Automation Card
                ScheduleAutomationCard(
                    settings = uiState.currentProfile,
                    isDarkTheme = isDarkTheme,
                    onSettingsChanged = onProfileChanged
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Notification Shade Quick Control Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notification_settings_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF1D162E) else Color(0xFFFFFFFF)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDarkTheme) Color(0xFF3E315C) else Color(0xFFDDD2ED)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
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
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.notification_toggle_title),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = stringResource(R.string.notification_toggle_desc),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = uiState.isNotificationEnabled,
                            onCheckedChange = onToggleNotification,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF221147),
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                uncheckedTrackColor = if (isDarkTheme) Color(0xFF2B2144) else Color(0xFFE2D8F0)
                            ),
                            modifier = Modifier.testTag("switch_notification_shade")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun RealtimeVolumeIndicator(
    title: String,
    percent: Int,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$percent%",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (percent == 0) MaterialTheme.colorScheme.onSurfaceVariant else color
        )
    }
}
