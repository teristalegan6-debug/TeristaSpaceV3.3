package com.terista.space.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.terista.space.data.model.VirtualApp
import com.terista.space.viewmodel.VirtualAppsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirtualAppsScreen(
    onNavigateBack: () -> Unit,
    viewModel: VirtualAppsViewModel = hiltViewModel()
) {
    val apps by viewModel.virtualApps.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Virtual Apps") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showInstallDialog() }) {
                Icon(Icons.Default.Add, contentDescription = "Install App")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(apps) { app ->
                VirtualAppCard(
                    app = app,
                    onLaunch = { viewModel.launchApp(app) },
                    onUninstall = { viewModel.uninstallApp(app) }
                )
            }
        }
    }
}

@Composable
private fun VirtualAppCard(
    app: VirtualApp,
    onLaunch: () -> Unit,
    onUninstall: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Package: ${app.packageName}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Status: ${if (app.isRunning) "Running" else "Stopped"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (app.isRunning) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onLaunch) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Launch")
                }
                IconButton(onClick = onUninstall) {
                    Icon(Icons.Default.Delete, contentDescription = "Uninstall")
                }
            }
        }
    }
}
