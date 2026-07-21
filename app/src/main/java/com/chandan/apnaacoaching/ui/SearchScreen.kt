package com.chandan.apnaacoaching.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    navController: NavController
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- SEARCH BAR HEADER ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onQueryChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search topics, videos, PDFs...") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.clearSearch() }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }

            // --- SEARCH RESULTS ---
            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is SearchUiState.Idle -> {
                        Text(
                            text = "Type at least 3 characters to search",
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    is SearchUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is SearchUiState.Empty -> {
                        Text(
                            text = "No results found for \"$searchQuery\"",
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    is SearchUiState.Error -> {
                        Text(
                            text = state.message,
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    is SearchUiState.Success -> {
                        val results = state.results
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            // Videos Section
                            if (results.videos.isNotEmpty()) {
                                item { CategoryHeader("Videos") }
                                items(results.videos) { video ->
                                    SearchResultItem(title = video.title ?: "Untitled", desc = video.description) {
                                        // navController.navigate("video_player_screen/${video.id}")
                                    }
                                }
                            }

                            // PDFs Section
                            if (results.pdfs.isNotEmpty()) {
                                item { CategoryHeader("PDF Materials") }
                                items(results.pdfs) { pdf ->
                                    SearchResultItem(title = pdf.title ?: "Untitled", desc = pdf.description) {
                                        // Navigate to PDF
                                    }
                                }
                            }

                            // Updates Section
                            if (results.updates.isNotEmpty()) {
                                item { CategoryHeader("Current Affairs & Updates") }
                                items(results.updates) { update ->
                                    SearchResultItem(title = update.title ?: "Untitled", desc = update.description) {
                                        // Navigate to Update
                                    }
                                }
                            }

                            // Subjective Questions
                            if (results.longQuestions.isNotEmpty()) {
                                item { CategoryHeader("Long Questions") }
                                items(results.longQuestions) { q ->
                                    val title = q.question ?: q.questionHi ?: "Unknown Question"
                                    SearchResultItem(title = title, desc = q.answer ?: q.answerHi) {
                                        // Navigate to subjective view
                                    }
                                }
                            }

                            // One-Liners
                            if (results.oneliners.isNotEmpty()) {
                                item { CategoryHeader("One-Liner Questions") }
                                items(results.oneliners) { q ->
                                    val title = q.question ?: q.questionHi ?: "Unknown Question"
                                    SearchResultItem(title = title, desc = q.answer ?: q.answerHi) {
                                        // Navigate to one-liner view
                                    }
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
fun CategoryHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun SearchResultItem(title: String, desc: String?, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!desc.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2 // Keeps UI clean by truncating long descriptions
                )
            }
        }
    }
}