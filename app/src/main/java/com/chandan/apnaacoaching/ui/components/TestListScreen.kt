package com.chandan.apnaacoaching.ui.components

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chandan.apnaacoaching.data.CbtTest
import com.chandan.apnaacoaching.ui.PracticeState
import com.chandan.apnaacoaching.ui.PracticeViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestListScreen(
    testType: String, // "live" or "mock"
    userId: String,
    viewModel: PracticeViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()

    val tests by if (testType == "live") {
        viewModel.liveTests.collectAsState()
    } else {
        viewModel.mockTests.collectAsState()
    }

    Scaffold { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is PracticeState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is PracticeState.Error -> {
                    Text(
                        text = state.message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is PracticeState.Success -> {
                    if (tests.isEmpty()) {
                        Text(
                            text = "No $testType tests available right now.",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Gray
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(tests) { test ->
                                CbtTestCard(
                                    test = test,
                                    userId = userId,
                                    viewModel = viewModel,
                                    navController = navController
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CbtTestCard(
    test: CbtTest,
    userId: String,
    viewModel: PracticeViewModel,
    navController: NavController
) {
    val context = LocalContext.current

    // 1. Calculate the exact start time in milliseconds
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val startTimeMillis = remember(test.startTime) {
        try {
            format.parse(test.startTime)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    // 2. Create a state that knows if the test has started
    var isTestStarted by remember { mutableStateOf(System.currentTimeMillis() >= startTimeMillis) }

    // 3. Auto-unlock the button exactly when the time arrives
    LaunchedEffect(startTimeMillis) {
        while (!isTestStarted) {
            delay(1000L) // Check every second
            if (System.currentTimeMillis() >= startTimeMillis) {
                isTestStarted = true
            }
        }
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = test.heading,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = test.sysName,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                LiveTimeIndicator(
                    startTimeStr = test.startTime,
                    endTimeStr = test.endTime
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Timer,
                    contentDescription = "Time",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${test.timeDuration} mins", style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Marks: +${test.plusPoint} | -${test.minusPoint}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            if (test.isSubmitted) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate("instructions/${test.id}") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reattempt")
                    }
                    Button(
                        onClick = {
                            navController.navigate("detailed_result_screen/${test.id}/$userId")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("View Result")
                    }
                }
            } else {
                // --- NEW BUTTON LOGIC HERE ---
                if (!isTestStarted && test.isEnrolled) {
                    // Show disabled Red Button if enrolled but not live
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = Color(0xFFFFEBEE), // Light red background
                            disabledContentColor = Color.Red // Red text
                        )
                    ) {
                        Text("Test is not live Yet", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Normal active button for Enrollment or Starting the test
                    Button(
                        onClick = {
                            if (test.isEnrolled) {
                                navController.navigate("instructions/${test.id}")
                            } else {
                                viewModel.enrollInTest(userId, test.id) { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (test.isEnrolled) "Start Test" else "Enroll for ${test.entryFee} Coins")
                    }
                }
            }
        }
    }
}

fun getTestTimeStatus(startTimeStr: String, endTimeStr: String): String {
    try {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val startTime = format.parse(startTimeStr)?.time ?: return ""
        val endTime = format.parse(endTimeStr)?.time ?: return ""
        val currentTime = System.currentTimeMillis()

        return when {
            currentTime in startTime..endTime -> "🔴 Live Now"
            currentTime < startTime -> {
                val diff = startTime - currentTime
                val hours = diff / (1000 * 60 * 60)
                val minutes = (diff / (1000 * 60)) % 60

                when {
                    hours > 24 -> "Starts in ${hours / 24} days"
                    hours > 0 -> "Starts in ${hours}h ${minutes}m"
                    else -> "Starts in ${minutes}m"
                }
            }

            else -> "Ended"
        }
    } catch (e: Exception) {
        return ""
    }
}

@Composable
fun LiveTimeIndicator(startTimeStr: String, endTimeStr: String) {
    // Hold the current status string
    var statusText by remember { mutableStateOf(getTestTimeStatus(startTimeStr, endTimeStr)) }

    // Update the text automatically every 60 seconds (1 minute)
    LaunchedEffect(startTimeStr, endTimeStr) {
        while (true) {
            statusText = getTestTimeStatus(startTimeStr, endTimeStr)
            delay(60000L.milliseconds) // Wait 1 minute before recalculating
        }
    }

    // Don't draw anything if the status couldn't be calculated or is empty
    if (statusText.isEmpty()) return

    // Dynamic colors based on the status
    val isLive = statusText.contains("Live")
    val bgColor = if (isLive) Color(0xFFFFEBEE) else Color(0xFFE3F2FD) // Light Red vs Light Blue
    val textColor = if (isLive) Color.Red else Color(0xFF1976D2) // Solid Red vs Solid Blue

    Text(
        text = statusText,
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}


//@Composable
//fun CbtTestCard(
//    test: CbtTest,
//    userId: String,
//    viewModel: PracticeViewModel,
//    navController: NavController
//) {
//    val context = LocalContext.current
////    var isLoadingResult by remember { mutableStateOf(false) }
//
//    ElevatedCard(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(
//                text = test.heading,
//                fontWeight = FontWeight.Bold,
//                style = MaterialTheme.typography.titleMedium
//            )
//            Text(
//                text = test.sysName,
//                color = Color.Gray,
//                style = MaterialTheme.typography.bodySmall
//            )
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Icon(
//                    Icons.Default.Timer,
//                    contentDescription = "Time",
//                    modifier = Modifier.size(16.dp),
//                    tint = MaterialTheme.colorScheme.primary
//                )
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(text = "${test.timeDuration} mins", style = MaterialTheme.typography.bodySmall)
//
//                Spacer(modifier = Modifier.width(16.dp))
//
//                Text(
//                    text = "Marks: +${test.plusPoint} | -${test.minusPoint}",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = Color.DarkGray
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//            HorizontalDivider()
//            Spacer(modifier = Modifier.height(8.dp))
//
//            if (test.isSubmitted) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    OutlinedButton(
//                        onClick = { navController.navigate("instructions/${test.id}") },
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("Reattempt")
//                    }
//                    Button(
//                        onClick = {
//
//                            navController.navigate("detailed_result_screen/${test.id}/$userId")
//                        },
//                        modifier = Modifier.weight(1f),
//                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
//                    ) {
//                        Text("View Result")
//                    }
//                }
//            } else {
//                Button(
//                    onClick = {
//                        if (test.isEnrolled) {
//                            navController.navigate("instructions/${test.id}")
//                        } else {
//                            viewModel.enrollInTest(userId, test.id) { message ->
//                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text(if (test.isEnrolled) "Start Test" else "Enroll for ${test.entryFee} Coins")
//                }
//            }
//        }
//    }
//}