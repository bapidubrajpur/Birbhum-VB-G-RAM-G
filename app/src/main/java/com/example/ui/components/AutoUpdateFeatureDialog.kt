package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.FeatureUpdateItem
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent

@Composable
fun AutoUpdateFeatureDialog(
    isAutoUpdateEnabled: Boolean,
    autoUpdateInterval: Int,
    countdownSeconds: Int,
    isSyncing: Boolean,
    syncStatus: String?,
    lastUpdated: String,
    featureUpdates: List<FeatureUpdateItem>,
    onToggleAutoUpdate: (Boolean) -> Unit,
    onSetInterval: (Int) -> Unit,
    onSyncNow: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Auto-Update, 1: Features Done

    val infiniteTransition = rememberInfiniteTransition(label = "dialog_spin")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .testTag("auto_update_feature_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFF6F00).copy(alpha = 0.15f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = Color(0xFFFF6F00),
                                    modifier = Modifier
                                        .size(24.dp)
                                        .then(if (isSyncing) Modifier.rotate(spinAngle) else Modifier)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Auto-Update & Features",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Birbhum Live Administration Hub",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("dialog_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Switcher
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Autorenew,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Auto-Update", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        },
                        modifier = Modifier.testTag("tab_auto_update")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Features Done", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        },
                        modifier = Modifier.testTag("tab_features_done")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Content
                if (selectedTab == 0) {
                    AutoUpdateTabContent(
                        isAutoUpdateEnabled = isAutoUpdateEnabled,
                        autoUpdateInterval = autoUpdateInterval,
                        countdownSeconds = countdownSeconds,
                        isSyncing = isSyncing,
                        syncStatus = syncStatus,
                        lastUpdated = lastUpdated,
                        spinAngle = spinAngle,
                        onToggleAutoUpdate = onToggleAutoUpdate,
                        onSetInterval = onSetInterval,
                        onSyncNow = onSyncNow
                    )
                } else {
                    FeaturesDoneTabContent(
                        featureUpdates = featureUpdates
                    )
                }
            }
        }
    }
}

@Composable
private fun AutoUpdateTabContent(
    isAutoUpdateEnabled: Boolean,
    autoUpdateInterval: Int,
    countdownSeconds: Int,
    isSyncing: Boolean,
    syncStatus: String?,
    lastUpdated: String,
    spinAngle: Float,
    onToggleAutoUpdate: (Boolean) -> Unit,
    onSetInterval: (Int) -> Unit,
    onSyncNow: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Master Live Status Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAutoUpdateEnabled) EmeraldSuccess.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = BorderStroke(
                    1.dp,
                    if (isAutoUpdateEnabled) EmeraldSuccess.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isAutoUpdateEnabled) EmeraldSuccess else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAutoUpdateEnabled) "LIVE AUTO-UPDATE ACTIVE" else "AUTO-UPDATE PAUSED",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isAutoUpdateEnabled) EmeraldSuccess else Color.Gray,
                                letterSpacing = 0.8.sp
                            )
                        }

                        Switch(
                            checked = isAutoUpdateEnabled,
                            onCheckedChange = onToggleAutoUpdate,
                            modifier = Modifier.testTag("auto_update_toggle_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isAutoUpdateEnabled) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Next Google Sheets Refresh in:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFF6F00).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "$countdownSeconds sec",
                                    color = Color(0xFFFF6F00),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress bar of countdown
                        LinearProgressIndicator(
                            progress = { (countdownSeconds.toFloat() / autoUpdateInterval.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFFFF6F00),
                            trackColor = Color(0xFFFF6F00).copy(alpha = 0.2f),
                        )
                    } else {
                        Text(
                            text = "Auto-update is currently paused. Data will only refresh when you tap 'Sync All Sheets Now'.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Interval Frequency Selection
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Refresh Cadence Frequency",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(15, 30, 60, 120).forEach { seconds ->
                            val isSelected = autoUpdateInterval == seconds
                            val label = when (seconds) {
                                15 -> "15 sec"
                                30 -> "30 sec"
                                60 -> "1 min"
                                else -> "2 min"
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSetInterval(seconds) }
                                    .testTag("interval_${seconds}s")
                            ) {
                                Text(
                                    text = label,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Sync Now Button
        item {
            Button(
                onClick = onSyncNow,
                enabled = !isSyncing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("sync_now_dialog_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F00)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .then(if (isSyncing) Modifier.rotate(spinAngle) else Modifier)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSyncing) "Syncing with Google Sheets..." else "Sync All Sheets Now",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Live Sheets Connection Matrix
        item {
            Text(
                text = "Connected Google Sheet Endpoints (Live)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            val endpoints = listOf(
                Pair("State Allotment", "GID 263874047"),
                Pair("Works Progress & Sanctions", "GID 1115948623"),
                Pair("Material FTO Verification", "GID 1996508129"),
                Pair("NMMS Daily Attendance & Musters", "GID 1613697542"),
                Pair("e-KYC & Aadhaar Seeding", "GID 187896883"),
                Pair("Annual & Monthly Targets", "GID 232026717"),
                Pair("Category Matrix & Water Security", "GID 6823442")
            )

            endpoints.forEach { (title, gid) ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = gid, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldSuccess.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldSuccess)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("LIVE", color = EmeraldSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Last Status Note
        item {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Status: ${syncStatus ?: "Connected & Ready"}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Last synced: $lastUpdated",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FeaturesDoneTabContent(
    featureUpdates: List<FeatureUpdateItem>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = EmeraldSuccess.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ALL FEATURES UPDATED & DONE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldSuccess,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Every requested milestone is verified, completed, and synced.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(featureUpdates, key = { it.id }) { item ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFF6F00).copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = item.category.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF6F00),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldSuccess.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "DONE",
                                    color = EmeraldSuccess,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Completed: ${item.dateCompleted}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
