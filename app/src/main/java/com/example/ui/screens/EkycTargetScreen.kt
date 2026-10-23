package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BirbhumData
import com.example.data.models.BlockData
import com.example.ui.components.AnimatedLinearProgressIndicator
import com.example.ui.components.MetricKpiCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CyanMetric
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent

@Composable
fun EkycTargetScreen(
    data: BirbhumData,
    onSelectBlock: (BlockData) -> Unit
) {
    val ekyc = data.ekycSummary
    val target = data.targetSummary

    var showTargetHeatmap by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ekyc_target_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // e-KYC Executive Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WORKER e-KYC & AADHAAR AUTHENTICATION",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = CyanMetric,
                            letterSpacing = 1.1.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyanMetric.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "99.7% Aadhaar Seeded",
                                color = CyanMetric,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Total Active Workers: 1,230,522 (12.3 Lakh)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Aadhaar Based Payment System (ABPS) readiness and demographic auth",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // KPIs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricKpiCard(
                    title = "e-KYC Completed",
                    value = "${ekyc.ekycPercent}%",
                    subtitle = "11,10,591 Workers done",
                    icon = Icons.Default.HowToReg,
                    iconTint = EmeraldSuccess,
                    badgeText = "Leader",
                    modifier = Modifier.weight(1f)
                )

                MetricKpiCard(
                    title = "ABPS Eligible",
                    value = "11.00 L",
                    subtitle = "11,00,376 workers ready",
                    icon = Icons.Default.AccountBalance,
                    iconTint = MaterialTheme.colorScheme.primary,
                    badgeText = "Payment Ready",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Special Drive Target Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = target.driveTitle,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "5,060,100 Person-days",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Planning standard: 30,300 person-days × GP count",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Monthly Schedule Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Oct 26", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("7.59L", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Nov 26", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("10.12L", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Dec 26", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("10.12L", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Jan 27", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("9.10L", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Feb 27", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("7.08L", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Section Header
        item {
            SectionHeader(
                title = "e-KYC Ranking & Block Targets",
                subtitle = "Ranked by e-KYC completion % (19 Blocks)",
                badgeText = "Leaderboard"
            )
        }

        // Blocks sorted by e-KYC %
        val sortedByEkyc = data.blocks.sortedByDescending { it.ekyc.ekycPct }
        items(sortedByEkyc, key = { it.slNo }) { block ->
            val ek = block.ekyc
            val tgt = block.target

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectBlock(block) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = block.blockName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (block.blockName.equals("DUBRAJPUR", ignoreCase = true)) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = EmeraldSuccess.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "#1 Rank",
                                            color = EmeraldSuccess,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${ek.ekycDone} of ${ek.totalWorkers} Workers Done",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (ek.ekycPct >= 92.0) EmeraldSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "${ek.ekycPct}%",
                                color = if (ek.ekycPct >= 92.0) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    AnimatedLinearProgressIndicator(
                        targetProgress = (ek.ekycPct / 100.0).toFloat(),
                        color = if (ek.ekycPct >= 92.0) EmeraldSuccess else MaterialTheme.colorScheme.primary,
                        height = 6
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ABPS Ready: ${ek.abpsEligible}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Target: ${tgt.totalTarget} persondays",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
