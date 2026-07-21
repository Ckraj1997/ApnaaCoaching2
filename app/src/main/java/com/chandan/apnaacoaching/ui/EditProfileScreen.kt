package com.chandan.apnaacoaching.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.chandan.apnaacoaching.R
import com.chandan.apnaacoaching.data.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            // Safely check if we have the profile data loaded before uploading
            if (uiState is ProfileState.Success) {
                val currentProfile = (uiState as ProfileState.Success).profile
                viewModel.uploadProfilePicture(context, currentProfile.userId, uri)
            }
        }
    }

    // Observe update state for Toasts and Navigation
    LaunchedEffect(updateState) {
        when (updateState) {
            is UpdateState.Success -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.profile_updated),
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetUpdateState()
                navController.popBackStack() // Go back to profile view
            }
            is UpdateState.Error -> {
                Toast.makeText(
                    context,
                    (updateState as UpdateState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetUpdateState()
            }
            else -> {}
        }
    }

    // Root Surface for the entire screen to handle background color
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- CUSTOM TOP BAR ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Standard top bar height
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp // Adds a subtle shadow below the bar
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = stringResource(R.string.edit_profile),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
            // --- END CUSTOM TOP BAR ---

            if (uiState is ProfileState.Success) {
                val currentProfile = (uiState as ProfileState.Success).profile

                // State variables pre-filled with current data
                var firstName by remember { mutableStateOf(currentProfile.firstName) }
                var middleName by remember { mutableStateOf(currentProfile.middleName ?: "") }
                var lastName by remember { mutableStateOf(currentProfile.lastName) }
                var phone by remember { mutableStateOf(currentProfile.phone ?: "") }
                var gender by remember { mutableStateOf(currentProfile.gender ?: "") }
                var genderDropdownExpanded by remember { mutableStateOf(false) }
                val genderOptions = listOf("Male", "Female", "Other")
                var dob by remember { mutableStateOf(currentProfile.dob ?: "") }
                var location by remember { mutableStateOf(currentProfile.location ?: "") }
                var city by remember { mutableStateOf(currentProfile.city ?: "") }
                var state by remember { mutableStateOf(currentProfile.state ?: "") }
                var pincode by remember { mutableStateOf(currentProfile.pincode ?: "") }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Takes the remaining space below the custom Top Bar
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable {
                                    // Launch the gallery picker
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (!currentProfile.picture.isNullOrEmpty()) {
                                AsyncImage(
                                    model = currentProfile.picture,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Avatar",
                                    modifier = Modifier.size(50.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            // Semi-transparent overlay to indicate it is clickable
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit Picture",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text(stringResource(R.string.first_name)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = middleName,
                        onValueChange = { middleName = it },
                        label = { Text(stringResource(R.string.middle_name)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text(stringResource(R.string.last_name)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(stringResource(R.string.phone_number)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = dob,
                        onValueChange = { dob = it },
                        label = { Text(stringResource(R.string.date_of_birth_yyyy_mm_dd)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = genderDropdownExpanded,
                        onExpandedChange = { genderDropdownExpanded = !genderDropdownExpanded },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        // The visual text field
                        OutlinedTextField(
                            value = gender,
                            onValueChange = {}, // Read-only, so we leave this empty
                            readOnly = true,
                            label = { Text(stringResource(R.string.gender)) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderDropdownExpanded)
                            },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .menuAnchor() // This binds the dropdown menu to this text field
                                .fillMaxWidth()
                        )

                        // The actual dropdown menu options
                        ExposedDropdownMenu(
                            expanded = genderDropdownExpanded,
                            onDismissRequest = { genderDropdownExpanded = false }
                        ) {
                            genderOptions.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        gender = selectionOption
                                        genderDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    Text("Address Details", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text(stringResource(R.string.street_location)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text(stringResource(R.string.city)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = { Text(stringResource(R.string.state)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = pincode,
                        onValueChange = { pincode = it },
                        label = { Text(stringResource(R.string.pincode)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    )

                    Button(
                        onClick = {
                            val updatedProfile = currentProfile.copy(
                                firstName = firstName,
                                middleName = middleName.takeIf { it.isNotBlank() },
                                lastName = lastName,
                                phone = phone.takeIf { it.isNotBlank() },
                                dob = dob.takeIf { it.isNotBlank() },
                                gender = gender.takeIf { it.isNotBlank() },
                                location = location.takeIf { it.isNotBlank() },
                                city = city.takeIf { it.isNotBlank() },
                                state = state.takeIf { it.isNotBlank() },
                                pincode = pincode.takeIf { it.isNotBlank() }
                            )
                            viewModel.updateProfile(updatedProfile)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = updateState !is UpdateState.Loading
                    ) {
                        if (updateState is UpdateState.Loading) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                        } else {
                            Text(stringResource(R.string.save_changes))
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            } else {
                // Failsafe if state is loading/error
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}