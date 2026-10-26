package com.classrecord.app

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import com.classrecord.app.di.AppContainer
import com.classrecord.app.di.AppViewModelFactory
import com.classrecord.app.ui.home.HomeScreen
import com.classrecord.app.ui.home.HomeViewModel
import com.classrecord.app.ui.privacy.PrivacyNoticeDialog
import com.classrecord.app.ui.privacy.PrivacyPolicyScreen
import com.classrecord.app.ui.settings.SettingsScreen
import com.classrecord.app.ui.theme.ClassRecordTheme
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.captureScreenRoboImage
import java.io.File
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    application = ScreenshotTestApp::class,
    sdk = [33],
    qualifiers = ScreenshotTestSupport.HOME_QUALIFIERS,
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
        capturePrivacyNoticeOverHome(english = false, dark = false, fileName = "privacy-notice-zh-light.png")
        capturePrivacyNoticeOverHome(english = true, dark = true, fileName = "privacy-notice-en-dark.png")
        capturePrivacyPolicy(english = false, dark = false, fileName = "privacy-policy-zh-light.png")
    }

    private fun captureSettingsPrivacyEntry(english: Boolean, dark: Boolean, fileName: String) {
        prepare(english, dark)
        val app = org.robolectric.RuntimeEnvironment.getApplication() as ScreenshotTestApp
        ScreenshotTestSupport.seedDemo(app.container, english, dark)
        captureComposable(fileName, dark, screenCapture = false) {
            SettingsScreen(onBack = {}, onOpenPrivacy = {})
        }
    }

    @OptIn(ExperimentalRoborazziApi::class)
    private fun capturePrivacyNoticeOverHome(english: Boolean, dark: Boolean, fileName: String) {
        prepare(english, dark)
        val app = org.robolectric.RuntimeEnvironment.getApplication() as ScreenshotTestApp
        ScreenshotTestSupport.seedDemo(app.container, english, dark)
        val container = app.container
        captureComposable(fileName, dark, screenCapture = false) {
            Box(Modifier.fillMaxSize()) {
                HomeContent(container)
                PrivacyNoticeDialog(onDismiss = {}, onReadFullPolicy = {})
            }
        }
    }

    private fun capturePrivacyPolicy(english: Boolean, dark: Boolean, fileName: String) {
        prepare(english, dark)
        captureComposable(fileName, dark, screenCapture = false) {
            val scrollState = rememberScrollState()
            LaunchedEffect(Unit) {
                while (scrollState.maxValue == 0) {
                    delay(16)
                }
                scrollState.scrollTo(scrollState.maxValue)
                delay(100)
            }
            PrivacyPolicyScreen(onBack = {}, scrollState = scrollState)
        }
    }

    private fun prepare(english: Boolean, dark: Boolean) {
        ScreenshotTestSupport.prepareLocale(
            english = english,
            dark = dark,
            roboQualifiers = ScreenshotTestSupport.HOME_QUALIFIERS,
        )
    }

    @OptIn(ExperimentalRoborazziApi::class)
    private fun captureComposable(
        fileName: String,
        dark: Boolean,
        screenCapture: Boolean,
        content: @Composable () -> Unit,
    ) {
        viewModelStoreOwner.viewModelStore.clear()
        val controller = Robolectric.buildActivity(ComponentActivity::class.java)
            .create()
            .start()
            .resume()
            .visible()
        val activity = controller.get()
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
        ShadowLooper.idleMainLooper()
        repeat(30) {
            ShadowLooper.idleMainLooper()
        }
        val file = distributionFile(fileName)
        if (screenCapture) {
            captureScreenRoboImage(file)
        } else {
            onView(isRoot()).captureRoboImage(file)
        }
        controller.pause().stop().destroy()
    }

    @Composable
    private fun HomeContent(container: AppContainer) {
        val vm: HomeViewModel = viewModel(factory = AppViewModelFactory(container))
        HomeScreen(
            viewModel = vm,
            onEditClass = {},
            onMembers = {},
            onSubGroups = {},
            onNewActivity = {},
            onOpenActivity = {},
            onLedger = {},
            onSettings = {},
        )
    }

    companion object {
        private fun distributionFile(name: String): File {
            val cwd = File(System.getProperty("user.dir"))
            val appModule = if (cwd.name == "app") cwd else File(cwd, "app")
            val dir = File(appModule, "build/distribution-screenshots-raw")
            dir.mkdirs()
            return File(dir, name)
        }
    }
}
