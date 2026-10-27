package com.classrecord.app.update

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

const val REMOTE_VERSION_URL = "https://dl.etais.dev/group-matters-version.json"

data class RemoteVersionInfo(
    val versionCode: Int,
    val versionName: String,
    val apkUrl: String,
    val releaseNotesZh: String?,
    val releaseNotesEn: String?,
) {
    fun effectiveApkUrl(): String =
        apkUrl.trim().ifEmpty { "https://dl.etais.dev/group-matters-$versionName.apk" }
}

object RemoteVersionManifest {
    fun parse(json: String): RemoteVersionInfo {
        val root = JSONObject(json)
        val notes = root.optJSONObject("releaseNotes")
        return RemoteVersionInfo(
            versionCode = root.getInt("versionCode"),
            versionName = root.getString("versionName"),
            apkUrl = root.optString("apkUrl", ""),
            releaseNotesZh = notes?.optString("zh")?.takeIf { it.isNotBlank() },
            releaseNotesEn = notes?.optString("en")?.takeIf { it.isNotBlank() },
        )
    }

    fun fetch(url: String = REMOTE_VERSION_URL, connect: (URL) -> HttpURLConnection = { it.openConnection() as HttpURLConnection }): RemoteVersionInfo {
        val connection = connect(URL(url)).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 15_000
            setRequestProperty("Accept", "application/json")
        }
        try {
            val code = connection.responseCode
            if (code !in 200..299) {
                error("HTTP $code")
            }
            val body = connection.inputStream.bufferedReader().readText()
            return parse(body)
        } finally {
            connection.disconnect()
        }
    }
}
