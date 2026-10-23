package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.runtime.*
import coil.compose.AsyncImage
import com.example.R
import com.example.data.models.FeatureUpdateItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarSection(
    sheetUrl: String,
    lastUpdated: String,
    isSyncing: Boolean,
    customLogoUrl: String? = null,
    isLogoVisible: Boolean = true,
    isAutoUpdateEnabled: Boolean = true,
    autoUpdateInterval: Int = 30,
    countdownSeconds: Int = 30,
    syncStatus: String? = null,
    featureUpdates: List<FeatureUpdateItem> = emptyList(),
    onToggleLogoVisibility: (Boolean) -> Unit = {},
    onUpdateCustomLogoUrl: (String?) -> Unit = {},
    onShowLandingPage: () -> Unit = {},
    onToggleAutoUpdate: (Boolean) -> Unit = {},
    onSetAutoUpdateInterval: (Int) -> Unit = {},
    onSyncClick: () -> Unit
) {
    val context = LocalContext.current
    var showLogoDialog by remember { mutableStateOf(false) }
    var showAutoUpdateDialog by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "sync_spin")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    if (showLogoDialog) {
        LogoCustomizerDialog(
            currentLogoUrl = customLogoUrl,
            isLogoVisible = isLogoVisible,
            onToggleLogoVisibility = onToggleLogoVisibility,
            onSaveLogoUrl = {
                onUpdateCustomLogoUrl(it)
                showLogoDialog = false
            },
            onDismiss = { showLogoDialog = false }
        )
    }

    if (showAutoUpdateDialog) {
        AutoUpdateFeatureDialog(
            isAutoUpdateEnabled = isAutoUpdateEnabled,
            autoUpdateInterval = autoUpdateInterval,
            countdownSeconds = countdownSeconds,
            isSyncing = isSyncing,
            syncStatus = syncStatus,
            lastUpdated = lastUpdated,
            featureUpdates = featureUpdates,
            onToggleAutoUpdate = onToggleAutoUpdate,
            onSetInterval = onSetAutoUpdateInterval,
            onSyncNow = onSyncClick,
            onDismiss = { showAutoUpdateDialog = false }
        )
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLogoVisible) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(2.dp, Color(0xFFFF6F00), CircleShape)
                                .clickable { showLogoDialog = true }
                                .testTag("app_logo_clickable"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!customLogoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = customLogoUrl,
                                    contentDescription = "Custom Organization Logo",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_launcher_emblem),
                                    contentDescription = "Birbhum Administration Crest",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(3.dp)
                                )
                            }
                        }
                    } else {
                        // Clean, high-contrast prominent text badge in Saffron when logo is removed
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFF6F00),
                            modifier = Modifier
                                .clickable { showLogoDialog = true }
                                .testTag("app_logo_clickable")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "BIRBHUM",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = "VB-G RAM G",
                                    color = Color.White.copy(alpha = 0.95f),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "BIRBHUM",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFF6F00) // Prominent Saffron
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "VB-G RAM G",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldSuccess.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "WB P&RD",
                                    color = EmeraldSuccess,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Executive Monitoring & Allotment Portal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Clickable Auto-Update Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isAutoUpdateEnabled) EmeraldSuccess.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(
                            1.dp,
                            if (isAutoUpdateEnabled) EmeraldSuccess.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showAutoUpdateDialog = true }
                            .testTag("auto_update_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isSyncing) GoldAccent else if (isAutoUpdateEnabled) EmeraldSuccess else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isSyncing) "Syncing..." else if (isAutoUpdateEnabled) "Auto ${countdownSeconds}s" else "Paused",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSyncing) GoldAccent else if (isAutoUpdateEnabled) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(3.dp))

                    // Feature Updates Done & Settings Modal Button
                    IconButton(
                        onClick = { showAutoUpdateDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("features_update_button")
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = EmeraldSuccess,
                                    contentColor = Color.White
                                ) {
                                    Text("DONE", fontSize = 7.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Feature Updates Done & Auto-Update",
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    // View Cover / Landing Page button
                    IconButton(
                        onClick = onShowLandingPage,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("landing_page_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MenuBook,
                            contentDescription = "View Cover / Landing Page",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Open Google Sheets button
                    IconButton(
                        onClick = {
                            if (sheetUrl.isNotEmpty()) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sheetUrl))
                                context.startActivity(intent)
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("open_sheets_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Open Google Sheets",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Sync live button
                    IconButton(
                        onClick = onSyncClick,
                        enabled = !isSyncing,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("sync_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync Live Sheet Data",
                            tint = if (isSyncing) GoldAccent else MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(20.dp)
                                .then(if (isSyncing) Modifier.rotate(angle) else Modifier)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Status bar (clickable to view auto-update and features done)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .clickable { showAutoUpdateDialog = true }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isSyncing) GoldAccent else EmeraldSuccess)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSyncing) "Auto-updating 7 Google Sheets..." else "7 Sheets Live • Auto-Update ${if (isAutoUpdateEnabled) "${countdownSeconds}s" else "OFF"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = lastUpdated,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Auto-update settings",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}
