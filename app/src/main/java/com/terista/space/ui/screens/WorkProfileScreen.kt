package com.terista.space.ui.screens

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.terista.space.viewmodel.WorkProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: WorkProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var hasWorkProfile by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        hasWorkProfile = viewModel.hasActiveWorkProfile()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Work Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Android Work Profile",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Create a secure, isolated work profile on your device. " +
                               "This uses Android's official enterprise containerization APIs.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (hasWorkProfile) {
                        StatusIndicator(
                            text = "Work Profile Active",
                            isActive = true
                        )
                        Button(
                            onClick = { viewModel.openWorkProfileSettings() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Manage Work Profile")
                        }
                        OutlinedButton(
                            onClick = { showDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Remove Work Profile")
                        }
                    } else {
                        StatusIndicator(
                            text = "No Work Profile",
                            isActive = false
                        )
                        Button(
                            onClick = { viewModel.provisionWorkProfile() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Create Work Profile")
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Features",
                        style = MaterialTheme.typography.titleMedium
                    )
                    FeatureItem("Isolated app sandbox")
                    FeatureItem("Separate notifications")
                    FeatureItem("Independent file storage")
                    FeatureItem("Managed by DevicePolicyManager")
                    FeatureItem("Quick toggle in settings")
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Remove Work Profile?") },
            text = { Text("This will delete all apps and data in the work profile.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.removeWorkProfile()
                    showDialog = false
                    hasWorkProfile = false
                }) {
                    Text("Remove")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun StatusIndicator(text: String, isActive: Boolean) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isActive) 
                MaterialTheme.colorScheme.onPrimaryContainer 
            else 
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FeatureItem(text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("•", style = MaterialTheme.typography.bodyMedium)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}
