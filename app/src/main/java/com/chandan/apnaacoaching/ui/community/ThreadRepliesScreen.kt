package com.chandan.apnaacoaching.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import com.chandan.apnaacoaching.data.CommunityReply
import androidx.compose.material.icons.outlined.ChatBubbleOutline
@Composable
fun ThreadRepliesScreen(
    threadId: String,
    userId: String, // Logged-in user ID
    navController: NavController,
    viewModel: CommunityViewModel
) {
    val thread by viewModel.currentThread.collectAsState()
    val replies by viewModel.replies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var replyText by remember { mutableStateOf("") }

    LaunchedEffect(threadId) {
        viewModel.fetchReplies(threadId, userId) // Passed userId here
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // --- TOP BAR ---
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Discussion", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        // --- MAIN CONTENT ---
        Box(modifier = Modifier.weight(1f)) {
            if (isLoading && thread == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 1. The Original Question
                    thread?.let { currentThread ->
                        item {
                            ThreadCard(thread = currentThread, onClick = {}) // Reusing our ThreadCard UI
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Replies", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
                        }
                    }

                    // 2. The Replies List
                    items(replies) { reply ->
                        ReplyItem(reply, threadId, userId,viewModel, navController )
                    }
                }
            }
        }

        // --- STICKY REPLY INPUT BAR ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = { Text("Write a reply...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (replyText.isNotBlank()) {
                            viewModel.postReply(threadId, userId, replyText)
                            replyText = "" // Clear the input after sending
                        }
                    },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(4.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun ReplyItem(
    reply: CommunityReply,
    threadId: String, // Pass this in from the parent screen
    currentUserId: String,
    viewModel: CommunityViewModel,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = "https://apnaacoaching.in/config/image/users/${reply.user_pic_name}",
                    contentDescription = "Profile Pic",
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(reply.first_name ?: "User", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(reply.replied_on ?: "", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Body
            Text(reply.reply, fontSize = 14.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(8.dp))

            // --- LIKE BUTTON SECTION ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { viewModel.toggleLike(reply.reply_id, currentUserId) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = if (reply.is_liked_by_me) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (reply.is_liked_by_me) Color.Red else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = reply.like_count.toString(),
                    fontSize = 14.sp,
                    color = if (reply.is_liked_by_me) Color.Red else Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // NEW: Comment Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { navController.navigate("comments_screen/${threadId}/${reply.reply_id}") }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Comment",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reply", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}