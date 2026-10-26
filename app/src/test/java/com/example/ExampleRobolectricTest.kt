package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.ProfileSettings
import com.example.model.VolumeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("QuadMode", appName)
    }

    @Test
    fun `verify volume mode 4 indices and order`() {
        assertEquals(0, VolumeMode.OUTDOOR.orderIndex)
        assertEquals(1, VolumeMode.DAY.orderIndex)
        assertEquals(2, VolumeMode.SILENT.orderIndex)
        assertEquals(3, VolumeMode.NIGHT.orderIndex)

        assertEquals(VolumeMode.OUTDOOR, VolumeMode.fromIndex(0))
        assertEquals(VolumeMode.DAY, VolumeMode.fromIndex(1))
        assertEquals(VolumeMode.SILENT, VolumeMode.fromIndex(2))
        assertEquals(VolumeMode.NIGHT, VolumeMode.fromIndex(3))
    }

    @Test
    fun `verify default profile settings`() {
        val nightProfile = ProfileSettings.defaultForMode(VolumeMode.NIGHT)
        assertEquals(0, nightProfile.ringPercent)
        assertTrue(nightProfile.isDnd)
        assertTrue(nightProfile.isScheduleEnabled)

        val outdoorProfile = ProfileSettings.defaultForMode(VolumeMode.OUTDOOR)
        assertEquals(100, outdoorProfile.ringPercent)
        assertTrue(outdoorProfile.boosterPercent >= 100)
    }
}
