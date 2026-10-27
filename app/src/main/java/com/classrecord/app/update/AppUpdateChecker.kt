package com.classrecord.app.update

import android.content.Context
import com.classrecord.app.BuildConfig
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.requestAppUpdateInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class UpdateCheckResult {
    data object UpToDate : UpdateCheckResult()
    data class SideloadUpdateAvailable(val info: RemoteVersionInfo) : UpdateCheckResult()
    data class PlayUpdateAvailable(val appUpdateInfo: AppUpdateInfo) : UpdateCheckResult()
    data object NetworkError : UpdateCheckResult()
    data object PlayCheckFailed : UpdateCheckResult()
}

class AppUpdateChecker(private val appContext: Context) {

    suspend fun check(): UpdateCheckResult = withContext(Dispatchers.IO) {
        runCatching {
            if (InstallSource.isGooglePlay(appContext)) {
                checkPlay()
            } else {
                checkSideload()
            }
        }.getOrElse { UpdateCheckResult.NetworkError }
    }

    private suspend fun checkSideload(): UpdateCheckResult {
        return try {
            val remote = RemoteVersionManifest.fetch()
            if (remote.versionCode > BuildConfig.VERSION_CODE) {
                UpdateCheckResult.SideloadUpdateAvailable(remote)
            } else {
                UpdateCheckResult.UpToDate
            }
        } catch (_: Exception) {
            UpdateCheckResult.NetworkError
        }
    }

    private suspend fun checkPlay(): UpdateCheckResult {
        return try {
            val manager = AppUpdateManagerFactory.create(appContext)
            val info = manager.requestAppUpdateInfo()
            when (info.updateAvailability()) {
                UpdateAvailability.UPDATE_AVAILABLE -> {
                    val allowed = info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) ||
                        info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                    if (allowed) {
                        UpdateCheckResult.PlayUpdateAvailable(info)
                    } else {
                        UpdateCheckResult.UpToDate
                    }
                }
                UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS ->
                    UpdateCheckResult.PlayUpdateAvailable(info)
                else -> UpdateCheckResult.UpToDate
            }
        } catch (_: Exception) {
            UpdateCheckResult.PlayCheckFailed
        }
    }
}
