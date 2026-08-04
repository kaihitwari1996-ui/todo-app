package com.example.todoapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todoapp.ui.theme.*
import com.example.todoapp.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm: AppViewModel) {
    val currentTheme by vm.theme.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose Theme", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { inner ->
        LazyColumn(
            Modifier.fillMaxSize().padding(inner),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ThemeMode.values()) { mode ->
                ThemeCard(
                    mode      = mode,
                    selected  = currentTheme == mode,
                    onClick   = { vm.setTheme(mode) }
                )
            }
        }
    }
}

@Composable
fun ThemeCard(mode: ThemeMode, selected: Boolean, onClick: () -> Unit) {
    val (bg, surface, primary, text) = when (mode) {
        ThemeMode.PENCIL   -> listOf(PencilBackground, PencilSurface, PencilAccent, PencilPrimary)
        ThemeMode.CLASSIC  -> listOf(ClassicBackground, ClassicSurface, ClassicPrimary, ClassicPrimary)
        ThemeMode.TICKTICK -> listOf(FlowBackground, FlowSurface, FlowPrimary, Color(0xFF1A1A1A))
        ThemeMode.AGRO     -> listOf(AgroBackground, AgroSurface, AgroPrimary, Color(0xFF1C1C0E))
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        border   = BorderStroke(if (selected) 2.dp else 1.dp,
            if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline.copy(0.3f)),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Preview swatch
            Box(
                Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)).background(bg),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    Modifier.fillMaxSize().padding(6.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(2.dp)).background(primary))
                    Box(Modifier.fillMaxWidth(0.7f).height(5.dp).clip(RoundedCornerShape(2.dp)).background(text.copy(0.3f)))
                    Box(Modifier.fillMaxWidth(0.5f).height(5.dp).clip(RoundedCornerShape(2.dp)).background(text.copy(0.2f)))
                    Box(Modifier.size(20.dp, 8.dp).clip(RoundedCornerShape(2.dp)).background(primary.copy(0.6f)))
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text("${mode.emoji} ${mode.displayName}", style = MaterialTheme.typography.titleLarge)
                Text(mode.description, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 3.dp))
            }

            if (selected) {
                Icon(Icons.Default.Check, "Selected",
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            }
        }
    }
}
