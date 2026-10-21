package com.classrecord.app

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import com.classrecord.app.data.demo.DemoDataSeeder
import com.classrecord.app.data.prefs.ThemeMode
import com.classrecord.app.i18n.AppLanguage
import com.classrecord.app.i18n.LocaleApplier
import com.classrecord.app.ui.privacy.PrivacyNoticeDialog
import com.classrecord.app.ui.privacy.PrivacyPolicyScreen
import com.classrecord.app.ui.settings.SettingsScreen
import com.classrecord.app.ui.theme.ClassRecordTheme
import com.github.takahirom.roborazzi.captureRoboImage
import java.io.File
import java.time.Instant
import java.util.Locale
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLegacySystemClock

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    application = ScreenshotTestApp::class,
    sdk = [33],
    qualifiers = "w360dp-h781dp-port-xxhdpi",
)
class DistributionScreenshotTest {
    private val lifecycleOwner = object : LifecycleOwner {
        private val registry = LifecycleRegistry(this).apply {
            currentState = Lifecycle.State.RESUMED
        }
        override val lifecycle: Lifecycle = registry
    }

    private val viewModelStoreOwner = object : ViewModelStoreOwner {
        override val viewModelStore = ViewModelStore()
    }

    @Test
    fun captureDistributionScreens() {
        captureSettingsPrivacyEntry(english = false, dark = false, fileName = "settings-privacy-zh-light.png")
        capturePrivacyNotice(english = false, dark = false, fileName = "privacy-notice-zh-light.png")
        capturePrivacyNotice(english = true, dark = true, fileName = "privacy-notice-en-dark.png")
        capturePrivacyPolicy(english = false, dark = false, fileName = "privacy-policy-zh-light.png")
    }

    private fun captureSettingsPrivacyEntry(english: Boolean, dark: Boolean, fileName: String) {
        val app = org.robolectric.RuntimeEnvironment.getApplication() as ScreenshotTestApp
        prepareLocale(app, english, dark)
        runBlocking {
            DemoDataSeeder(
                app.container.database,
                app.container.classRepository,
                app.container.memberRepository,
                app.container.subGroupRepository,
                app.container.activityRepository,
                app.container.ledgerRepository,
            ).seedIfEmpty(english = english)
            app.container.userPrefs.setThemeMode(if (dark) ThemeMode.DARK else ThemeMode.LIGHT)
        }
        capture(fileName, dark) {
            SettingsScreen(onBack = {}, onOpenPrivacy = {})
        }
    }

    private fun capturePrivacyNotice(english: Boolean, dark: Boolean, fileName: String) {
        val app = org.robolectric.RuntimeEnvironment.getApplication() as ScreenshotTestApp
        prepareLocale(app, english, dark)
        runBlocking {
            app.container.userPrefs.setThemeMode(if (dark) ThemeMode.DARK else ThemeMode.LIGHT)
        }
        capture(fileName, dark) {
            PrivacyNoticeDialog(onDismiss = {}, onReadFullPolicy = {})
        }
    }

    private fun capturePrivacyPolicy(english: Boolean, dark: Boolean, fileName: String) {
        val app = org.robolectric.RuntimeEnvironment.getApplication() as ScreenshotTestApp
        prepareLocale(app, english, dark)
        runBlocking {
            app.container.userPrefs.setThemeMode(if (dark) ThemeMode.DARK else ThemeMode.LIGHT)
        }
        capture(fileName, dark) {
            PrivacyPolicyScreen(onBack = {})
        }
    }

    private fun prepareLocale(app: ScreenshotTestApp, english: Boolean, dark: Boolean) {
        setRobolectricTime(FIXED_MILLIS)
        val localeTag = if (english) "en-US" else "zh-CN"
        val roboLocale = if (english) "en-rUS" else "zh-rCN"
        org.robolectric.RuntimeEnvironment.setQualifiers(
            "$roboLocale-w360dp-h781dp-port-xxhdpi",
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

    private fun capture(fileName: String, dark: Boolean, content: @androidx.compose.runtime.Composable () -> Unit) {
        viewModelStoreOwner.viewModelStore.clear()
        val activity = Robolectric.buildActivity(ComponentActivity::class.java)
            .create()
            .start()
            .resume()
            .visible()
            .get()
        activity.setContent {
            CompositionLocalProvider(
                LocalLifecycleOwner provides lifecycleOwner,
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalContext provides activity,
            ) {
                ClassRecordTheme(darkTheme = dark) {
                    content()
                }
            }
        }
        onView(isRoot()).captureRoboImage(distributionFile(fileName))
    }

    companion object {
        private val FIXED_MILLIS: Long =
            Instant.parse("2026-09-28T04:00:00Z").toEpochMilli()

        private fun setRobolectricTime(millis: Long) {
            val method = ShadowLegacySystemClock::class.java.getDeclaredMethod(
                "setCurrentTimeMillis",
                Long::class.javaPrimitiveType,
            )
            method.isAccessible = true
            method.invoke(null, millis)
        }

        private fun distributionFile(name: String): File {
            val cwd = File(System.getProperty("user.dir"))
            val appModule = if (cwd.name == "app") cwd else File(cwd, "app")
            val dir = File(appModule, "build/distribution-screenshots-raw")
            dir.mkdirs()
            return File(dir, name)
        }
    }
}
