package com.chandan.apnaacoaching.ui.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun InstructionsScreen(
    quizId: Int,
    navController: NavController
) {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()) {
        Text("Test Instructions", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Text("1. Total time for this test is fixed.")
        Text("2. Negative marking will be applied for wrong answers.")
        Text("3. Do not close or refresh the app during the test.")

        // Note: You can later replace this hardcoded text by fetching the
        // real HTML from your get_instructions.php backend file!

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Move from Instructions -> Actual Quiz Screen
                navController.navigate("quiz_screen/$quizId")
            }
        ) {
            Text("I Agree, Start Quiz Now")
        }
    }
}