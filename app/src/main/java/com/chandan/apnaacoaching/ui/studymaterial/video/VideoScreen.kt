package com.chandan.apnaacoaching.ui.studymaterial.video

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
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
import com.chandan.apnaacoaching.data.VideoItem
import com.chandan.apnaacoaching.utils.extractYoutubeVideoId

@Composable
fun VideoScreen(
    groupId: String,
    levelId: String,
    catId: String,
    viewModel: VideoViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Configure your image path here
    val videoBaseUrl = "https://apnaacoaching.in/config/image/video/"

    LaunchedEffect(groupId, levelId, catId) {
        viewModel.fetchVideos(groupId, levelId, catId)
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
                    text = "Video Lessons",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }

            // --- MAIN LIST AREA ---
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when (val state = uiState) {
                    is VideoUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is VideoUiState.Error -> {
                        Text(state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                    }
                    is VideoUiState.Success -> {
                        if (state.videos.isEmpty()) {
                            Text("No videos found.", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(state.videos) { video ->
                                    VideoCard(
                                        video = video,
                                        videoBaseUrl = videoBaseUrl,
                                        onClick = {
                                            // Handle video click (e.g., open YouTube link or navigate to in-app player)
//                                            if (!video.video_link.isNullOrEmpty()) {
//                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.video_link))
//                                                context.startActivity(intent)
//                                            }
//                                            if (!video.video_link.isNullOrEmpty()) {
//                                                // --- NEW: Route to In-App Player ---
//                                                val encodedUrl = java.net.URLEncoder.encode(video.video_link, java.nio.charset.StandardCharsets.UTF_8.toString())
//                                                navController.navigate("video_player_screen/$encodedUrl")
//                                            }
                                            if (!video.video_link.isNullOrEmpty()) {
                                                // 1. Extract the ID here!
                                                val videoId = extractYoutubeVideoId(video.video_link)

                                                if (videoId != null) {
                                                    // 2. Navigate using ONLY the clean ID
                                                    navController.navigate("video_player_screen/$videoId")
                                                } else {
                                                    // Optional: Show a Toast saying "Invalid Video Link"
                                                }
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
fun VideoCard(
    video: VideoItem,
    videoBaseUrl: String,
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
        Column(modifier = Modifier.fillMaxWidth()) {

            // --- THUMBNAIL AREA ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Black), // Background in case image is loading or missing
                contentAlignment = Alignment.Center
            ) {
                // If the thumbnail is not empty and not the default "video.webp"
                if (!video.video_thumbnail.isNullOrEmpty() && video.video_thumbnail != "video.webp") {
                    AsyncImage(
                        model = videoBaseUrl + video.video_thumbnail,
                        contentDescription = "Video Thumbnail",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Dark overlay to make the play button pop
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    )
                }

                // Play Button Icon Overlay
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Video",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // --- TEXT AREA ---
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = video.title ?: "Untitled Video",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (!video.Description.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = video.Description,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}