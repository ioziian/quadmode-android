package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VolumeMode
import kotlinx.coroutines.launch

@Composable
fun VerticalSteppedSwitch(
    currentMode: VolumeMode,
    isDarkTheme: Boolean,
    onModeSelected: (VolumeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val scope = rememberCoroutineScope()

    // Precise geometry math so the handle is NEVER clipped in any mode!
    val trackTotalHeight = 356.dp
    val trackOuterPadding = 8.dp
    val handleHeight = 70.dp
    // Inner groove height = 356 - (8 * 2) = 340.dp
    // Maximum travel for top of handle: 340 - 70 = 270.dp
    val maxTravel = (356 - 16 - 70).dp // 270.dp

    val targetFraction = when (currentMode) {
        VolumeMode.OUTDOOR -> 0f
        VolumeMode.DAY -> 0.3333f
        VolumeMode.SILENT -> 0.6666f
        VolumeMode.NIGHT -> 1f
    }

    val animatedFraction = remember { Animatable(targetFraction) }

    LaunchedEffect(currentMode) {
        animatedFraction.animateTo(
            targetValue = targetFraction,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val activeColor by animateColorAsState(
        targetValue = if (isDarkTheme) currentMode.darkAccentColor else currentMode.lightAccentColor,
        label = "activeModeColor"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vertical_stepped_switch"),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Physical Audio Hardware Track Chassis
        Box(
            modifier = Modifier
                .width(108.dp)
                .height(trackTotalHeight)
                .shadow(
                    elevation = 14.dp,
                    shape = RoundedCornerShape(36.dp),
                    spotColor = activeColor.copy(alpha = 0.35f)
                )
                .clip(RoundedCornerShape(36.dp))
                .background(
                    Brush.verticalGradient(
                        colors = if (isDarkTheme) {
                            listOf(Color(0xFF2E2248), Color(0xFF1B142B), Color(0xFF281E3F))
                        } else {
                            listOf(Color(0xFFEDE5F7), Color(0xFFF7F3FC), Color(0xFFE5DBF2))
                        }
                    )
                )
                .border(
                    2.dp,
                    if (isDarkTheme) Color(0xFF4C3B72) else Color(0xFFC7B6DF),
                    RoundedCornerShape(36.dp)
                )
                .padding(trackOuterPadding)
        ) {
            // Deep Inner Carved Slotted Channel
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = if (isDarkTheme) {
                                listOf(Color(0xFF0B0814), Color(0xFF130E22), Color(0xFF0B0814))
                            } else {
                                listOf(Color(0xFFDED2ED), Color(0xFFEFE7F8), Color(0xFFDED2ED))
                            }
                        )
                    )
                    .border(
                        1.5.dp,
                        if (isDarkTheme) Color(0xFF261B3D) else Color(0xFFCEBEE3),
                        RoundedCornerShape(26.dp)
                    )
            ) {
                // Background Track Ruler Notches and Clickable Trigger Zones
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    VolumeMode.entries.forEach { mode ->
                        val isModeActive = currentMode == mode
                        val stepColor = if (isDarkTheme) mode.darkAccentColor else mode.lightAccentColor

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    onModeSelected(mode)
                                }
                                .testTag("switch_step_${mode.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            // Illuminated Step Notch
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left tick mark
                                Box(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(2.5.dp)
                                        .clip(RoundedCornerShape(1.dp))
                                        .background(if (isModeActive) stepColor else Color(0xFF3B2D54))
                                )

                                // Center subtle notch marker
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (isModeActive) stepColor.copy(alpha = 0.5f) else Color.Transparent)
                                )

                                // Right tick mark
                                Box(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(2.5.dp)
                                        .clip(RoundedCornerShape(1.dp))
                                        .background(if (isModeActive) stepColor else Color(0xFF3B2D54))
                                )
                            }
                        }
                    }
                }

                // Cyber-Tactile Studio Fader Handle (Тумблер)
                val currentOffsetDp = maxTravel * animatedFraction.value

                Box(
                    modifier = Modifier
                        .offset(x = 0.dp, y = currentOffsetDp)
                        .fillMaxWidth()
                        .height(handleHeight)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = activeColor
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = if (isDarkTheme) {
                                    listOf(
                                        Color(0xFF3F305E),
                                        Color(0xFF291E42),
                                        Color(0xFF1B132C)
                                    )
                                } else {
                                    listOf(
                                        Color(0xFFFFFFFF),
                                        Color(0xFFF6F0FD),
                                        Color(0xFFE4D6F5)
                                    )
                                }
                            )
                        )
                        .border(
                            2.dp,
                            Brush.verticalGradient(
                                colors = listOf(
                                    activeColor,
                                    activeColor.copy(alpha = 0.6f)
                                )
                            ),
                            RoundedCornerShape(20.dp)
                        )
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                scope.launch {
                                    val deltaFraction = delta / (maxTravel.value * 2.2f)
                                    val newFraction = (animatedFraction.value + deltaFraction).coerceIn(0f, 1f)
                                    animatedFraction.snapTo(newFraction)
                                }
                            },
                            onDragStopped = {
                                val snapMode = when {
                                    animatedFraction.value < 0.166f -> VolumeMode.OUTDOOR
                                    animatedFraction.value < 0.500f -> VolumeMode.DAY
                                    animatedFraction.value < 0.833f -> VolumeMode.SILENT
                                    else -> VolumeMode.NIGHT
                                }
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                onModeSelected(snapMode)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Glowing Active Icon and Jewel Light
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = currentMode.icon,
                                contentDescription = currentMode.title,
                                tint = activeColor,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(activeColor)
                                    .border(1.dp, Color.White, CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Metallic Grip Ribs
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .width(16.dp)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(1.5.dp))
                                        .background(
                                            if (isDarkTheme) Color(0xFF554178) else Color(0xFFC5B4DC)
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(18.dp))

        // Mode Status Cards alongside Switch (Perfect vertical alignment)
        Column(
            modifier = Modifier.height(trackTotalHeight),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            VolumeMode.entries.forEach { mode ->
                val isSelected = currentMode == mode
                val modeColor = if (isDarkTheme) mode.darkAccentColor else mode.lightAccentColor

                Surface(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onModeSelected(mode)
                    },
                    shape = RoundedCornerShape(18.dp),
                    color = if (isSelected) {
                        modeColor.copy(alpha = if (isDarkTheme) 0.18f else 0.14f)
                    } else {
                        Color.Transparent
                    },
                    border = if (isSelected) {
                        androidx.compose.foundation.BorderStroke(1.5.dp, modeColor)
                    } else {
                        androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDarkTheme) Color(0xFF281E3D) else Color(0xFFECE4F7)
                        )
                    },
                    modifier = Modifier.padding(vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) modeColor else (if (isDarkTheme) Color(0xFF251C3A) else Color(0xFFEFE8FA))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = mode.icon,
                                contentDescription = mode.title,
                                tint = if (isSelected) {
                                    if (mode == VolumeMode.DAY && isDarkTheme) Color(0xFF20133A) else Color.Black
                                } else {
                                    modeColor.copy(alpha = 0.8f)
                                },
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = mode.title,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onSurface
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    fontSize = 15.sp
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(modeColor)
                                    )
                                }
                            }
                            Text(
                                text = mode.subtitle,
                                color = if (isSelected) modeColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
