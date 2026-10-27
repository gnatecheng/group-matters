package com.classrecord.app.update

import android.content.Context
import android.os.Build

object InstallSource {
    /** True when the app was installed from Google Play (not sideloaded). */
    fun isGooglePlay(context: Context): Boolean {
        val installer = installingPackageName(context) ?: return false
        return installer == "com.android.vending"
    }

    private fun installingPackageName(context: Context): String? {
        val pm = context.packageManager
        val packageName = context.packageName
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            runCatching { pm.getInstallSourceInfo(packageName).installingPackageName }.getOrNull()
        } else {
            @Suppress("DEPRECATION")
            pm.getInstallerPackageName(packageName)
        }
    }
}
