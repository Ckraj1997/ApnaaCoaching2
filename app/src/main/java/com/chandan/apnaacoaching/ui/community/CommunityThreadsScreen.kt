package com.chandan.apnaacoaching.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.chandan.apnaacoaching.data.CommunityThread

@Composable
fun CommunityThreadsScreen(
    groupId: String,
    navController: NavController,
    viewModel: CommunityViewModel
) {
    val threads by viewModel.threads.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isPaginating by viewModel.isPaginating.collectAsState()

    // Fetch data when screen loads
    LaunchedEffect(groupId) {
        viewModel.fetchThreads(groupId)
    }

    // Root Box allows us to overlay the FAB exactly where we want it without Scaffold
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Main Content Column
        Column(modifier = Modifier.fillMaxSize()) {

            // --- CUSTOM TOP BAR (No extra padding) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 4.dp)
                    .background(Color.White)
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Community Discussion",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }

            // --- MAIN LIST AREA ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Takes up remaining space below the top bar
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (threads.isEmpty()) {
                    Text(
                        text = "No discussions yet. Be the first to ask!",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Use itemsIndexed to track scroll position
                        itemsIndexed(threads) { index, thread ->

                            // --- PAGINATION TRIGGER ---
                            // If we have rendered the last item in the current list, fetch more!
                            if (index == threads.lastIndex && !isLoading && !isPaginating) {
                                LaunchedEffect(index) {
                                    viewModel.fetchThreads(groupId, isRefresh = false)
                                }
                            }

                            ThreadCard(
                                thread = thread,
                                onClick = {
                                    navController.navigate("replies_screen/${thread.thread_id}")
                                }
                            )
                        }

                        // --- LOADING FOOTER ---
                        // Show a small loading spinner at the bottom while fetching the next page
                        if (isPaginating) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- FLOATING ACTION BUTTON OVERLAY ---
        ExtendedFloatingActionButton(
            onClick = { navController.navigate("create_thread_screen/$groupId") },
            icon = { Icon(Icons.Default.Add, contentDescription = "Ask Question") },
            text = { Text("Ask Question") },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp) // Exact control over FAB placement
        )
    }
}

@Composable
fun ThreadCard(thread: CommunityThread, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // User Info Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = "https://apnaacoaching.in/config/image/users/${thread.user_pic_name}",
                    contentDescription = "Profile Pic",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = thread.first_name ?: "User",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = thread.posted_on ?: "",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Thread Title & Description
            Text(
                text = thread.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = thread.description,
                fontSize = 14.sp,
                color = Color.DarkGray,
                maxLines = 3 // Truncates long descriptions gracefully
            )
        }
    }
}