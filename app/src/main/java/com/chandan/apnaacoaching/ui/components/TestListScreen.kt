package com.chandan.apnaacoaching.ui.components

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chandan.apnaacoaching.data.CbtTest
import com.chandan.apnaacoaching.ui.PracticeState
import com.chandan.apnaacoaching.ui.PracticeViewModel

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

    // Choose which list to observe based on the argument passed in!
    val tests by if (testType == "live") {
        viewModel.liveTests.collectAsState()
    } else {
        viewModel.mockTests.collectAsState()
    }

    Scaffold { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
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
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {

                        if (test.isEnrolled) {
                            // Already enrolled -> navigate to test instructions/start screen
                            // Note: We will need to create this route in your NavGraph later!
                            navController.navigate("instructions/${test.id}")
                        } else {
                            // Not enrolled -> trigger the enrollment API
                            viewModel.enrollInTest(userId, test.id) { message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }

                    },
                    enabled = !test.isSubmitted
                ) {
                    Text(
                        when {
                            test.isSubmitted -> "Completed"
                            test.isEnrolled -> "Start Test"
                            else -> "Enroll for ${test.entryFee} Coins"
                        }
                    )
                }
            }
        }
    }
}