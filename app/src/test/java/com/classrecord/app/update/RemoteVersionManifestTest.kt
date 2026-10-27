package com.classrecord.app.update

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class RemoteVersionManifestTest {

    @Test
    fun parseFullManifest() {
        val json = """
            {
              "versionCode": 11,
              "versionName": "1.6.0",
              "apkUrl": "https://dl.etais.dev/group-matters-1.6.0.apk",
              "releaseNotes": {
                "zh": "修复问题",
                "en": "Bug fixes"
              }
            }
        """.trimIndent()
        val info = RemoteVersionManifest.parse(json)
        assertEquals(11, info.versionCode)
        assertEquals("1.6.0", info.versionName)
        assertEquals("https://dl.etais.dev/group-matters-1.6.0.apk", info.effectiveApkUrl())
        assertEquals("修复问题", info.releaseNotesZh)
        assertEquals("Bug fixes", info.releaseNotesEn)
    }

    @Test
    fun fallbackApkUrlWhenBlank() {
        val json = """
            {"versionCode": 12, "versionName": "2.0.0", "apkUrl": ""}
        """.trimIndent()
        val info = RemoteVersionManifest.parse(json)
        assertTrue(info.effectiveApkUrl().endsWith("group-matters-2.0.0.apk"))
    }
}
