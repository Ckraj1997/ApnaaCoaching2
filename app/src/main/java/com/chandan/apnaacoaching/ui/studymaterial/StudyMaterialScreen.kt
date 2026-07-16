package com.chandan.apnaacoaching.ui.studymaterial

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chandan.apnaacoaching.data.MaterialType

// Data class to hold the static material options

@Composable
fun StudyMaterialScreen(
    groupId: String,
    levelId: String,
    categoryId: String,
    navController: NavController
) {
    // Replicating the options from your PHP file
    val materialOptions = listOf(
        MaterialType(
            "one_linear",
            "One Linear",
            "Easy to remember and learn.",
            Icons.AutoMirrored.Filled.Article,
            Color(0xFF4CAF50)
        ),
        MaterialType(
            "subjective",
            "Long Question",
            "Deep Learning.",
            Icons.Default.Description,
            Color(0xFF2196F3)
        ),
        MaterialType("quiz", "Quiz", "Empower yourself.", Icons.Default.Quiz, Color(0xFFFF9800)),
        MaterialType(
            "video",
            "Video",
            "Learn From video.",
            Icons.Default.OndemandVideo,
            Color(0xFFF44336)
        ),
        MaterialType(
            "pdf",
            "PDF",
            "Take notes as a PDF.",
            Icons.Default.PictureAsPdf,
            Color(0xFF9C27B0)
        ),
        MaterialType(
            "update",
            "Current Affairs",
            "Daily updates.",
            Icons.Default.Public,
            Color(0xFF00BCD4)
        )
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FA) // Clean background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- CUSTOM TOP BAR ---
            // Avoids Scaffold padding completely
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 4.dp)
                    .background(Color.White)
//                    .statusBarsPadding() // Ensures it doesn't overlap the device battery/time bar
                    .height(56.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Study Material",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }

            // --- TWO COLUMN GRID ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // Forces exactly 2 columns
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(materialOptions) { option ->
                    MaterialTile(option = option) {
                        when (option.id) {
                            "one_linear" -> {
                                navController.navigate("one_liner_screen/$groupId/$levelId/$categoryId")
                            }

                            "subjective" -> {
                                navController.navigate("subjective_screen/$groupId/$levelId/$categoryId")
                            }

                            "pdf" -> {
//                                navController.navigate("pdf_screen/$groupId/$levelId/$categoryId")
                            }

                            "video" -> {
//                                navController.navigate("video_screen/$groupId/$levelId/$categoryId")
                            }

                            "quiz" -> {
                                navController.navigate("quiz_list_screen/$groupId/$levelId/$categoryId")
                            }

                            "update" -> {
//                                navController.navigate("current_affairs_screen/$groupId/$levelId/$categoryId")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MaterialTile(option: MaterialType, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp) // Fixed height for uniform square-ish tiles
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.title,
                tint = option.iconTint,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = option.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = option.description,
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}