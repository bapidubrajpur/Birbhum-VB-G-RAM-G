package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent

@Composable
fun LogoCustomizerDialog(
    currentLogoUrl: String?,
    isLogoVisible: Boolean = true,
    onToggleLogoVisibility: (Boolean) -> Unit = {},
    onSaveLogoUrl: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    var inputUrl by remember { mutableStateOf(currentLogoUrl ?: "") }
    var previewUrl by remember { mutableStateOf(currentLogoUrl) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("logo_customizer_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
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
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Logo & Branding",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Customize District/Office Logo",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Toggle visibility card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Show Logo on Header",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isLogoVisible) "Emblem is displayed" else "Hidden (clean text badge shown)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isLogoVisible,
                            onCheckedChange = { onToggleLogoVisibility(it) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Live Preview Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CURRENT DISPLAYED LOGO",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(2.5.dp, Color(0xFFFF6D00), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!previewUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = previewUrl,
                                    contentDescription = "Custom Logo Preview",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_launcher_emblem),
                                    contentDescription = "Default Emblem",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (!previewUrl.isNullOrBlank()) "Custom Web Logo Active" else "Bundled Official Emblem Active",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (!previewUrl.isNullOrBlank()) EmeraldSuccess else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Option 1: Enter Custom Logo URL
                Text(
                    text = "Option 1: Paste Logo Image URL",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Enter any direct link (PNG, JPG, SVG, WebP) to your department or administration logo:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = inputUrl,
                    onValueChange = {
                        inputUrl = it
                        previewUrl = it.ifBlank { null }
                    },
                    label = { Text("https://example.com/logo.png") },
                    placeholder = { Text("Paste direct image link...") },
                    leadingIcon = {
                        Icon(Icons.Default.Link, contentDescription = null)
                    },
                    trailingIcon = {
                        if (inputUrl.isNotBlank()) {
                            IconButton(onClick = {
                                inputUrl = ""
                                previewUrl = null
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("logo_url_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Preset options
                Text(
                    text = "Quick Presets:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionChip(
                        onClick = {
                            inputUrl = ""
                            previewUrl = null
                        },
                        label = { Text("Default Crest", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = if (previewUrl.isNullOrBlank()) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        )
                    )

                    SuggestionChip(
                        onClick = {
                            // VB-G RAM G / West Bengal Gov sample badge
                            val wbEmblemUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d5/Emblem_of_West_Bengal.svg/240px-Emblem_of_West_Bengal.svg.png"
                            inputUrl = wbEmblemUrl
                            previewUrl = wbEmblemUrl
                        },
                        label = { Text("WB State Emblem", fontSize = 12.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Option 2: Guide for uploading your file
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Option 2: Replace App Icon & File in Code",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "To permanently bake your logo into the Android APK and app launcher icon:",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "app/src/main/res/drawable/ic_launcher_emblem.png",
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Simply upload or send your logo image in this chat, and we will automatically place it into the project as your app launcher icon and header emblem!",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onSaveLogoUrl(previewUrl)
                            onDismiss()
                        },
                        modifier = Modifier.testTag("apply_logo_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Apply Logo")
                    }
                }
            }
        }
    }
}
