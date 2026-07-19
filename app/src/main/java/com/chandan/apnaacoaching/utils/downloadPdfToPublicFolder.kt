package com.chandan.apnaacoaching.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
// --- HELPER FUNCTION FOR PUBLIC DOWNLOADS ---
fun downloadPdfToPublicFolder(context: Context, url: String, title: String) {
    try {
        val safeUrl = url.replace(" ", "%20")
        val request = DownloadManager.Request(Uri.parse(safeUrl)).apply {
            setTitle(title)
            setDescription("Downloading Study Material...")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "${title.replace(" ", "_")}.pdf"
            )
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to download: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
    }
}
