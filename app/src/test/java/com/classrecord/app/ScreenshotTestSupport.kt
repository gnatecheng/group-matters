package com.classrecord.app

import android.content.res.Configuration
import com.classrecord.app.data.demo.DemoDataSeeder
import com.classrecord.app.data.prefs.ThemeMode
import com.classrecord.app.di.AppContainer
import com.classrecord.app.i18n.AppLanguage
import com.classrecord.app.i18n.LocaleApplier
import java.time.Instant
import java.util.Locale
import kotlinx.coroutines.runBlocking
import org.robolectric.shadows.ShadowLegacySystemClock

internal object ScreenshotTestSupport {
    val fixedMillis: Long = Instant.parse("2026-09-28T04:00:00Z").toEpochMilli()

    /** Portrait phone with 2:1 max edge ratio (411×822 dp @ xxhdpi). */
    const val FASTLANE_QUALIFIERS = "w411dp-h822dp-port-xxhdpi"

    const val HOME_QUALIFIERS = "w360dp-h781dp-port-xxhdpi"

    fun setRobolectricTime(millis: Long = fixedMillis) {
        val method = ShadowLegacySystemClock::class.java.getDeclaredMethod(
            "setCurrentTimeMillis",
            Long::class.javaPrimitiveType,
        )
        method.isAccessible = true
        method.invoke(null, millis)
    }

    fun prepareLocale(
        english: Boolean,
        dark: Boolean,
        roboQualifiers: String,
    ) {
        setRobolectricTime()
        val localeTag = if (english) "en-US" else "zh-CN"
        val roboLocale = if (english) "en-rUS" else "zh-rCN"
        org.robolectric.RuntimeEnvironment.setQualifiers(
            "$roboLocale-$roboQualifiers",
        )
        val context = org.robolectric.RuntimeEnvironment.getApplication()
        val config = Configuration(context.resources.configuration)
        config.setLocale(Locale.forLanguageTag(localeTag))
        config.uiMode = (config.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or
            if (dark) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        LocaleApplier.apply(
            context,
            if (english) AppLanguage.EN else AppLanguage.ZH,
            allowClearToSystem = true,
        )
    }

    fun seedDemo(container: AppContainer, english: Boolean, dark: Boolean) {
        runBlocking {
            DemoDataSeeder(
                container.database,
                container.classRepository,
                container.memberRepository,
                container.subGroupRepository,
                container.activityRepository,
                container.ledgerRepository,
            ).forceSeed(english = english)
            val fixed = fixedMillis
            container.database.activityDao().getAll().forEach { activity ->
                container.database.activityDao().update(
                    activity.copy(createdAt = fixed, updatedAt = fixed),
                )
            }
            container.userPrefs.setThemeMode(if (dark) ThemeMode.DARK else ThemeMode.LIGHT)
        }
    }
}
