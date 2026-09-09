package com.stalkerone.depthkeyboard

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

/** Checks GitHub Releases on app launch and installs a newer APK through Android's installer. */
class AppUpdateManager(private val activity: android.app.Activity) {
    companion object {
        private const val RELEASES_URL = "https://api.github.com/repos/stalker-one/depth-swiftkeyboard/releases?per_page=20"
        private const val APK_NAME = "depth-keyboard.apk"
        private const val CURRENT_VERSION_CODE_FALLBACK = 1
        private const val PREFS = "depth_keyboard_update"
        private const val KEY_LAST_CHECK = "last_check"
        private const val CHECK_INTERVAL_MS = 6L * 60L * 60L * 1000L
    }

    private val main = Handler(Looper.getMainLooper())
    private var downloadId = -1L
    private var downloadDialog: AlertDialog? = null
    private var pendingInstallUri: Uri? = null

    fun checkOnLaunch(force: Boolean = false) {
        val prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        if (!force && now - prefs.getLong(KEY_LAST_CHECK, 0L) < CHECK_INTERVAL_MS) return
        prefs.edit().putLong(KEY_LAST_CHECK, now).apply()

        Thread {
            val release = fetchNewestRelease() ?: return@Thread
            val localCode = localVersionCode()
            if (release.versionCode <= localCode) return@Thread
            main.post { showRequiredUpdate(release, localCode) }
        }.start()
    }

    fun onResume() {
        val uri = pendingInstallUri ?: return
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O ||
            activity.packageManager.canRequestPackageInstalls()) {
            pendingInstallUri = null
            launchInstaller(uri)
        }
    }

    private fun localVersionCode(): Int = try {
        @Suppress("DEPRECATION")
        activity.packageManager.getPackageInfo(activity.packageName, 0).versionCode
    } catch (_: Throwable) {
        CURRENT_VERSION_CODE_FALLBACK
    }

    private data class Release(
        val versionCode: Int,
        val versionName: String,
        val notes: String,
        val apkUrl: String,
        val sha256: String?
    )

    private fun fetchNewestRelease(): Release? {
        var connection: HttpURLConnection? = null
        return try {
            connection = (URL(RELEASES_URL).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 15_000
                setRequestProperty("Accept", "application/vnd.github+json")
                setRequestProperty("User-Agent", "Depth-Keyboard-Updater")
            }
            if (connection.responseCode !in 200..299) return null
            val json = connection.inputStream.bufferedReader().use { it.readText() }
            val releases = JSONArray(json)
            var best: Release? = null
            for (i in 0 until releases.length()) {
                val item = releases.optJSONObject(i) ?: continue
                if (item.optBoolean("draft", false)) continue
                val assets = item.optJSONArray("assets") ?: continue
                var apkUrl: String? = null
                var digest: String? = null
                for (j in 0 until assets.length()) {
                    val asset = assets.optJSONObject(j) ?: continue
                    if (asset.optString("name") == APK_NAME) {
                        apkUrl = asset.optString("browser_download_url").takeIf { it.startsWith("https://github.com/") }
                        digest = asset.optString("digest").removePrefix("sha256:").takeIf { it.matches(Regex("[0-9a-fA-F]{64}")) }
                        break
                    }
                }
                if (apkUrl == null) continue

                val body = item.optString("body")
                val versionCode = parseVersionCode(body) ?: parseTagVersionCode(item.optString("tag_name")) ?: continue
                val versionName = parseVersionName(body) ?: item.optString("tag_name").removePrefix("v").ifBlank { "Update" }
                val candidate = Release(versionCode, versionName, body, apkUrl, digest)
                if (best == null || candidate.versionCode > best!!.versionCode) best = candidate
            }
            best
        } catch (_: Throwable) {
            null
        } finally {
            connection?.disconnect()
        }
    }

    private fun parseVersionCode(body: String): Int? =
        Regex("(?im)^\\s*versionCode\\s*[:=]\\s*(\\d+)\\s*$").find(body)?.groupValues?.getOrNull(1)?.toIntOrNull()

    private fun parseVersionName(body: String): String? =
        Regex("(?im)^\\s*versionName\\s*[:=]\\s*([^\\s]+)\\s*$").find(body)?.groupValues?.getOrNull(1)

    private fun parseTagVersionCode(tag: String): Int? {
        val match = Regex("^v?(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?").find(tag) ?: return null
        val major = match.groupValues[1].toIntOrNull() ?: return null
        val minor = match.groupValues[2].toIntOrNull() ?: 0
        val patch = match.groupValues[3].toIntOrNull() ?: 0
        return major * 10000 + minor * 100 + patch
    }

    private fun showRequiredUpdate(release: Release, localCode: Int) {
        if (activity.isFinishing || activity.isDestroyed) return

        val root = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 12, 36, 4)
        }
        val title = TextView(activity).apply {
            text = "A required update is available"
            textSize = 22f
            setTextColor(0xff18243a.toInt())
            setPadding(0, 0, 0, 8)
        }
        val version = TextView(activity).apply {
            text = "Depth Keyboard ${release.versionName}  •  build ${release.versionCode}\nInstalled build: $localCode"
            textSize = 14f
            setTextColor(0xff56617a.toInt())
            setPadding(0, 0, 0, 12)
        }
        val notes = TextView(activity).apply {
            text = cleanNotes(release.notes)
            textSize = 13f
            setTextColor(0xff46536b.toInt())
            maxLines = 7
        }
        root.addView(title)
        root.addView(version)
        root.addView(notes)

        AlertDialog.Builder(activity)
            .setView(root)
            .setCancelable(false)
            .setPositiveButton("Update now") { _, _ -> startDownload(release) }
            .setNegativeButton("Exit") { _, _ -> activity.finish() }
            .show()
    }

    private fun cleanNotes(body: String): String {
        val cleaned = body.lines()
            .filterNot { it.trim().startsWith("versionCode", ignoreCase = true) }
            .filterNot { it.trim().startsWith("versionName", ignoreCase = true) }
            .joinToString("\n")
            .trim()
        return if (cleaned.isBlank()) "A newer version of Depth Keyboard is ready. Update now to continue." else cleaned.take(700)
    }

    private fun startDownload(release: Release) {
        val request = DownloadManager.Request(Uri.parse(release.apkUrl)).apply {
            setTitle("Depth Keyboard update")
            setDescription("Downloading ${release.versionName}…")
            setMimeType("application/vnd.android.package-archive")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(false)
            setDestinationInExternalFilesDir(activity, android.os.Environment.DIRECTORY_DOWNLOADS, APK_NAME)
        }
        val manager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        try {
            downloadId = manager.enqueue(request)
            showDownloadDialog(manager, release)
        } catch (_: Throwable) {
            showDownloadError("Could not start the update download.")
        }
    }

    private fun showDownloadDialog(manager: DownloadManager, release: Release) {
        val box = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(40, 20, 40, 10)
        }
        val label = TextView(activity).apply { text = "Downloading ${release.versionName}…"; textSize = 16f }
        val progress = ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal).apply { max = 100 }
        box.addView(label, LinearLayout.LayoutParams(-1, -2))
        box.addView(progress, LinearLayout.LayoutParams(-1, -2).apply { topMargin = 18 })
        downloadDialog = AlertDialog.Builder(activity).setTitle("Depth Keyboard update").setView(box).setCancelable(false).show()

        val poll = object : Runnable {
            override fun run() {
                if (activity.isFinishing || downloadId < 0) return
                val query = DownloadManager.Query().setFilterById(downloadId)
                manager.query(query)?.use { cursor ->
                    if (!cursor.moveToFirst()) return@use
                    val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                    val downloaded = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val total = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    if (total > 0) progress.progress = ((downloaded * 100L) / total).toInt().coerceIn(0, 100)
                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            downloadDialog?.dismiss()
                            val uri = manager.getUriForDownloadedFile(downloadId)
                            if (uri == null) showDownloadError("The update file could not be opened.") else {
                                pendingInstallUri = uri
                                installOrOpenSettings(uri)
                            }
                            return
                        }
                        DownloadManager.STATUS_FAILED -> {
                            downloadDialog?.dismiss()
                            showDownloadError("The update download failed. Please try again.")
                            return
                        }
                    }
                }
                main.postDelayed(this, 400)
            }
        }
        main.post(poll)
    }

    private fun installOrOpenSettings(uri: Uri) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O &&
            !activity.packageManager.canRequestPackageInstalls()) {
            AlertDialog.Builder(activity)
                .setTitle("Allow APK installation")
                .setMessage("Android needs permission for Depth Keyboard to install its downloaded update. Enable it, then return here.")
                .setCancelable(false)
                .setPositiveButton("Open settings") {
                    _, _ ->
                    try {
                        activity.startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${activity.packageName}")))
                    } catch (_: Throwable) {
                        activity.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                    }
                }
                .setNegativeButton("Exit") { _, _ -> activity.finish() }
                .show()
        } else {
            pendingInstallUri = null
            launchInstaller(uri)
        }
    }

    private fun launchInstaller(uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            activity.startActivity(intent)
        } catch (_: Throwable) {
            showDownloadError("Android could not open the installer. Please install the APK from Downloads.")
        }
    }

    private fun showDownloadError(message: String) {
        if (activity.isFinishing || activity.isDestroyed) return
        AlertDialog.Builder(activity)
            .setTitle("Update unavailable")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
