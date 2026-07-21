package com.chandan.apnaacoaching.ui.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chandan.apnaacoaching.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructionsScreen(
    quizId: Int,
    heading: String, // Pass the test title here
    navController: NavController
) {
    var isHindi by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(stringResource(R.string.exit_assessment)) },
            text = { Text(stringResource(R.string.are_you_sure_you_want_to_leave_you_will_need_to_re_read_the_instructions_if_you_return)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        navController.navigateUp() // Actually exit
                    }
                ) {
                    Text(stringResource(R.string.exit), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(stringResource(R.string.stay))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Standard app bar height
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp // Adds a nice subtle shadow below the bar
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp), // Slight padding for the edges
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }

                    Text(
                        text = stringResource(R.string.instructions),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    )

                    TextButton(onClick = { isHindi = !isHindi }) {
                        Icon(Icons.Default.Language, contentDescription = "Language")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isHindi) "ENG" else "हिं", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { isChecked = it }
                        )
                        Text(
                            text = if (isHindi) "मैंने निर्देश पढ़ लिए हैं और सहमत हूँ।" else "I have read and understood the instructions.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = {

                            val langParam = if (isHindi) "hi" else "en"
                            navController.navigate("quiz_screen/$quizId?lang=$langParam") {

                                popUpTo("instructions/$quizId") { inclusive = true }
                            }
                        },
                        enabled = isChecked,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Proceed")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Subject: $heading", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isHindi) "कृपया निम्नलिखित निर्देशों को ध्यान से पढ़ें" else "Please read the instructions carefully",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isHindi) "सामान्य अनुदेश:" else "General Instructions:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            val instructions = if (isHindi) {
                "1. सर्वर पर घड़ी लगाई गई है तथा आपकी स्क्रीन के दाहिने कोने में शीर्ष पर काउंटडाउन टाइमर में आपके लिए परीक्षा समाप्त करने के लिए शेष समय प्रदर्शित होगा।\n\n2. स्क्रीन के दाहिने कोने पर प्रश्न पैलेट, प्रत्येक प्रश्न के लिए निम्न में से कोई एक स्थिति प्रकट करता है:"
            } else {
                "1. The clock will be set at the server. The countdown timer in the top right corner of screen will display the remaining time available for you to complete the examination.\n\n2. The Questions Palette displayed on the right side of screen will show the status of each question using one of the following symbols:"
            }
            Text(
                text = instructions,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            LegendItem(
                ColorNotVisited,
                "1",
                if (isHindi) "आप अभी तक प्रश्न पर नहीं गए हैं।" else "You have not visited the question yet.",
                MaterialTheme.colorScheme.onBackground
            )
            LegendItem(
                ColorNotAnswered,
                "2",
                if (isHindi) "आपने प्रश्न का उत्तर नहीं दिया है।" else "You have not answered the question.",
                MaterialTheme.colorScheme.surface
            )
            LegendItem(
                ColorAnswered,
                "3",
                if (isHindi) "आप प्रश्न का उत्तर दे चुके हैं।" else "You have answered the question.",
                MaterialTheme.colorScheme.surface
            )
            LegendItem(
                ColorMarked,
                "4",
                if (isHindi) "पुनर्विचार के लिए चिन्हित।" else "Marked for review.",
                MaterialTheme.colorScheme.surface
            )
            LegendItem(
                ColorAnsweredMarked,
                "5",
                if (isHindi) "उत्तर दिया गया है और समीक्षा के लिए चिन्हित।" else "Answered and Marked for Review.",
                MaterialTheme.colorScheme.surface
            )

            Spacer(modifier = Modifier.height(24.dp))

            val answeringRules = if (isHindi) {
                "प्रश्नों का उत्तर देना:\n• अपना उत्तर चुनने के लिए, विकल्प के बटनों में से किसी एक पर क्लिक करें।\n• चयनित उत्तर को अचयनित करने के लिए 'Clear Response' बटन पर क्लिक करें।\n• अपना उत्तर सुरक्षित करने के लिए 'Save & Next' पर क्लिक करना जरूरी है।"
            } else {
                "Answering a Question:\n• To select your answer, click on the button of one of the options.\n• To deselect your chosen answer, click on the 'Clear Response' button.\n• To save your answer, you MUST click on the 'Save & Next' button."
            }
            Text(
                text = answeringRules,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LegendItem(color: Color, number: String, text: String, textColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .background(color, RoundedCornerShape(4.dp))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    RoundedCornerShape(4.dp)
                )
        ) {
            Text(text, color = textColor, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
    }
}