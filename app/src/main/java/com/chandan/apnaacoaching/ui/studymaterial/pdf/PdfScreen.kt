package com.chandan.apnaacoaching.ui.studymaterial.pdf

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.chandan.apnaacoaching.data.PdfItem

@Composable
fun PdfScreen(
    groupId: String,
    levelId: String,
    catId: String,
    viewModel: PdfViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Configure your image path here
    val pdfBaseUrl = "https://apnaacoaching.in/config/image/pdf/"

    LaunchedEffect(groupId, levelId, catId) {
        viewModel.fetchPdfs(groupId, levelId, catId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FA)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- CUSTOM TOP BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 4.dp)
                    .background(Color.White)
                    .height(56.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PDF Materials",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }

            // --- MAIN LIST AREA ---
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when (val state = uiState) {
                    is PdfUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is PdfUiState.Error -> {
                        Text(state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                    }
                    is PdfUiState.Success -> {
                        if (state.pdfs.isEmpty()) {
                            Text("No PDFs found.", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(state.pdfs) { pdf ->
                                    PdfCard(
                                        pdf = pdf,
                                        pdfBaseUrl = pdfBaseUrl,
                                        onClick = {
                                            if (!pdf.pdf_link.isNullOrEmpty()) {
                                                var finalUrl = pdf.pdf_link.trim()

                                                if (!finalUrl.startsWith("http://") && !finalUrl.startsWith("https://")) {
                                                    finalUrl = if (finalUrl.contains("www.") || finalUrl.contains(".com") || finalUrl.contains("drive.google")) {
                                                        "https://$finalUrl"
                                                    } else {
                                                        // FIX: Adjusted this path!
                                                        // If your link is "pdf_files/...", this will output "https://apnaacoaching.in/config/image/pdf_files/..."
                                                        // Ensure this matches exactly where your PDFs are stored on your Hostinger server!
                                                        "https://apnaacoaching.in/config/image/$finalUrl"
                                                    }
                                                }

                                                val cleanTitle = pdf.title ?: "Study_Material"
                                                // URL Encode the final link
                                                val encodedUrl = java.net.URLEncoder.encode(finalUrl, java.nio.charset.StandardCharsets.UTF_8.toString())

                                                // Navigate
                                                navController.navigate("pdf_player_screen/$encodedUrl/$cleanTitle")
                                            } else {
                                                android.widget.Toast.makeText(context, "PDF link is empty.", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PdfCard(
    pdf: PdfItem,
    pdfBaseUrl: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // --- THUMBNAIL AREA ---
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8EAF6)), // Light blue background
                contentAlignment = Alignment.Center
            ) {
                // If the thumbnail is missing or is the default "pdf.webp", show a nice Icon
                if (pdf.pdf_thumbnail.isNullOrEmpty() || pdf.pdf_thumbnail == "pdf.webp") {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "PDF Icon",
                        tint = Color(0xFF3F51B5),
                        modifier = Modifier.size(40.dp)
                    )
                } else {
                    AsyncImage(
                        model = pdfBaseUrl + pdf.pdf_thumbnail,
                        contentDescription = "PDF Thumbnail",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // --- TEXT AREA ---
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = pdf.title ?: "Untitled PDF",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (!pdf.Description.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = pdf.Description,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "View PDF",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}