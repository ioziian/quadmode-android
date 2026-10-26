package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class VolumeMode(
    val title: String,
    val subtitle: String,
    val orderIndex: Int,
    val icon: ImageVector,
    val darkAccentColor: Color,
    val lightAccentColor: Color
) {
    OUTDOOR(
        title = "Вуличний",
        subtitle = "Максимальна чутність та супер-підсилення",
        orderIndex = 0,
        icon = Icons.Default.VolumeUp,
        darkAccentColor = Color(0xFFFF9800), // Помаранчевий
        lightAccentColor = Color(0xFFE65100)
    ),
    DAY(
        title = "Денний",
        subtitle = "Зручний збалансований рівень",
        orderIndex = 1,
        icon = Icons.Default.WbSunny,
        darkAccentColor = Color(0xFFFFFFFF), // Білий
        lightAccentColor = Color(0xFF4A3780)  // Контрастний бузковий для світлої теми
    ),
    SILENT(
        title = "Беззвучний",
        subtitle = "Вимкнено звук викликів та медіа",
        orderIndex = 2,
        icon = Icons.Default.VolumeOff, // Перекреслений динамік
        darkAccentColor = Color(0xFF4CAF50), // Зелений
        lightAccentColor = Color(0xFF2E7D32)
    ),
    NIGHT(
        title = "Нічний",
        subtitle = "Повний спокій та тиша для сну",
        orderIndex = 3,
        icon = Icons.Default.Nightlight, // Місяць
        darkAccentColor = Color(0xFF00B0FF), // Блакитний
        lightAccentColor = Color(0xFF0288D1)
    );

    companion object {
        fun fromIndex(index: Int): VolumeMode {
            return entries.firstOrNull { it.orderIndex == index } ?: DAY
        }
    }
}
