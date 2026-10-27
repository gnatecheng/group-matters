package com.classrecord.app.update

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

object PlayUpdateLauncher {

    fun preferredUpdateType(info: AppUpdateInfo): Int? = when {
        info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> AppUpdateType.IMMEDIATE
        info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> AppUpdateType.FLEXIBLE
        else -> null
    }

    fun start(
        activity: Activity,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        appUpdateInfo: AppUpdateInfo,
        manager: AppUpdateManager = AppUpdateManagerFactory.create(activity),
    ): Boolean {
        if (appUpdateInfo.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE &&
            appUpdateInfo.updateAvailability() != UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
        ) {
            return false
        }
        val updateType = preferredUpdateType(appUpdateInfo) ?: return false
        manager.startUpdateFlowForResult(
            appUpdateInfo,
            launcher,
            AppUpdateOptions.newBuilder(updateType).build(),
        )
        return true
    }
}
