package com.chandan.apnaacoaching.ui.studymaterial.pdf

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chandan.apnaacoaching.utils.downloadPdfToPublicFolder
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


@Composable
fun PdfPlayerScreen(
    encodedUrl: String,
    pdfTitle: String,
    navController: NavController
) {
    val context = LocalContext.current

    val rawPdfUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())
    val safePdfUrl = rawPdfUrl.replace(" ", "%20")

    var localFile by remember { mutableStateOf<File?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    // Download remote file to cache folder silently
    LaunchedEffect(safePdfUrl) {
        withContext(Dispatchers.IO) {
            try {
                isLoading = true
                val url = java.net.URL(safePdfUrl)
                val connection = url.openConnection()
                connection.connect()

                val tempFile = File(context.cacheDir, "temp_viewing_material.pdf")
                url.openStream().use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                localFile = tempFile
                isLoading = false
            } catch (e: Exception) {
                e.printStackTrace()
                hasError = true
                isLoading = false
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FA)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- TOP BAR WITH DOWNLOAD BUTTON ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 4.dp)
                    .background(Color.White)
                    .height(56.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = pdfTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black,
                        maxLines = 1
                    )
                }

                IconButton(onClick = { downloadPdfToPublicFolder(context, safePdfUrl, pdfTitle) }) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download to Device",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // --- BOUQUET 16KB-SAFE PDF VIEWER ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                    hasError || localFile == null -> {
                        Text("Failed to load PDF preview.", color = Color.Red)
                    }
                    else -> {
                        // Initialize Bouquet State
                        val pdfState = rememberVerticalPdfReaderState(
                            resource = ResourceType.Local(Uri.fromFile(localFile!!)),
                            isZoomEnable = true // Enables pinch-to-zoom effortlessly
                        )

                        // Render the PDF
                        VerticalPDFReader(
                            state = pdfState,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}