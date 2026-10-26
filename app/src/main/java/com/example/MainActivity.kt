package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import com.example.ui.SoundShiftMainScreen
import com.example.ui.theme.SoundShiftTheme
import com.example.ui.viewmodel.VolumeViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: VolumeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val isDarkTheme = when (uiState.themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> systemDark
            }

            // Request POST_NOTIFICATIONS on Android 13+
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    viewModel.toggleNotification(true)
                }
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val hasNotifPermission = ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (!hasNotifPermission) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            SoundShiftTheme(darkTheme = isDarkTheme) {
                SoundShiftMainScreen(
                    uiState = uiState,
                    isDarkTheme = isDarkTheme,
                    onModeSelected = { mode -> viewModel.selectMode(mode) },
                    onProfileChanged = { profile -> viewModel.updateCurrentProfile(profile) },
                    onToggleTheme = { viewModel.toggleTheme() },
                    onToggleNotification = { enabled -> viewModel.toggleNotification(enabled) },
                    onTestSoundClicked = { streamType -> viewModel.playTestSound(streamType) }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshActualVolumes()
    }
}
