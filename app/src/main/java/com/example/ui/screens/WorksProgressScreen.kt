package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.models.formatFund
import com.example.ui.components.AnimatedLinearProgressIndicator
import com.example.ui.components.CategoryMatrixWorkAnalysisCard
import com.example.ui.components.MetricKpiCard
import com.example.ui.components.PbaBlockWiseReportCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary

enum class WorksTabSelection {
    PBA_REPORT,
    SANCTIONS_DIRECTORY
}

@Composable
fun WorksProgressScreen(
    data: BirbhumData,
    initialTab: WorksTabSelection = WorksTabSelection.PBA_REPORT,
    onSelectBlock: (BlockData) -> Unit
) {
    val works = data.worksSummary
    var selectedTab by remember { mutableStateOf(initialTab) }
    var worksSearchQuery by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("works_progress_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Executive Summary Header
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
                            text = "WORKS & SCHEMES IMPLEMENTATION",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.1.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldSuccess.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "19 Blocks • 167 GPs",
                                color = EmeraldSuccess,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "Birbhum Works & Paschim Banga Awaas",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Paschim Banga Awaas (PBA) beneficiary verification, work codes, sanctions & GP directory",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Segmented Tabs Switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedTab == WorksTabSelection.PBA_REPORT) Color(0xFFFF6F00) else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = WorksTabSelection.PBA_REPORT }
                                .testTag("tab_pba_report")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HomeWork,
                                    contentDescription = null,
                                    tint = if (selectedTab == WorksTabSelection.PBA_REPORT) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PBA Report (GRGC)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == WorksTabSelection.PBA_REPORT) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedTab == WorksTabSelection.SANCTIONS_DIRECTORY) NavyPrimary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = WorksTabSelection.SANCTIONS_DIRECTORY }
                                .testTag("tab_general_works")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Engineering,
                                    contentDescription = null,
                                    tint = if (selectedTab == WorksTabSelection.SANCTIONS_DIRECTORY) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Sanctions & Progress",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == WorksTabSelection.SANCTIONS_DIRECTORY) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // TAB 1: PASCHIM BANGA AWAAS (PBA) BLOCK REPORT
        // ==========================================
        if (selectedTab == WorksTabSelection.PBA_REPORT) {
            item {
                PbaBlockWiseReportCard(
                    pbaSummary = data.pbaSummary,
                    blocks = data.blocks,
                    onSelectBlock = onSelectBlock
                )
            }
        } else {
            // ==========================================
            // TAB 2: GENERAL SANCTIONS & WORKS DIRECTORY
            // ==========================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricKpiCard(
                        title = "Total Works",
                        value = "%,d".format(works.totalWorks),
                        subtitle = "167 Panchayats",
                        icon = Icons.Default.Assignment,
                        iconTint = NavyPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    MetricKpiCard(
                        title = "Estimates Started",
                        value = "%,d".format(works.estimatesStarted),
                        subtitle = "${String.format(java.util.Locale.US, "%.2f%%", (works.estimatesStarted.toDouble() / works.totalWorks) * 100)} prepared",
                        icon = Icons.Default.HourglassEmpty,
                        iconTint = Color(0xFFD97706),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricKpiCard(
                        title = "Tech Sanctions (TS)",
                        value = "%,d".format(works.technicalSanctions),
                        subtitle = "${works.tsPercent.formatFund()}% approved",
                        icon = Icons.Default.Engineering,
                        iconTint = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f)
                    )

                    MetricKpiCard(
                        title = "Admin Sanctions (AS)",
                        value = "%,d".format(works.adminSanctions),
                        subtitle = "${works.asPercent.formatFund()}% sanctioned",
                        icon = Icons.Default.CheckCircle,
                        iconTint = EmeraldSuccess,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Works Ongoing w/o Plantation & Water Security Status KPIs
            item {
                val nonPlantation = data.categoryMatrixSummary.nonPlantationKpi
                val waterSecurity = data.categoryMatrixSummary.waterSecurityStatus
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricKpiCard(
                        title = "Ongoing / GP (w/o Plant)",
                        value = String.format(java.util.Locale.US, "%.2f", nonPlantation.ongoingPerGp),
                        subtitle = "%,d ongoing (Excl. %,d Plant)".format(nonPlantation.totalOngoingWorks, nonPlantation.plantationOngoingWorks),
                        icon = Icons.Default.Nature,
                        iconTint = Color(0xFF16A34A),
                        modifier = Modifier.weight(1f)
                    )

                    MetricKpiCard(
                        title = "Water Security Status",
                        value = "%,d Sites".format(waterSecurity.totalOngoingWorks),
                        subtitle = "%,d PD • %,d Approved".format(waterSecurity.totalPersondays, waterSecurity.totalApprovedWorks),
                        icon = Icons.Default.WaterDrop,
                        iconTint = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Category Matrix Work Analysis Card
            item {
                CategoryMatrixWorkAnalysisCard(matrix = data.categoryMatrixSummary)
            }

            // Section header
            item {
                SectionHeader(
                    title = "Block-wise Progress Directory",
                    subtitle = "Ranked by Technical & Admin Sanctions rate (19 Blocks)",
                    badgeText = "19 Blocks"
                )
            }

            // Search Bar at Top of Block-wise Directory Table/List
            item {
                OutlinedTextField(
                    value = worksSearchQuery,
                    onValueChange = { worksSearchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("works_block_search_bar"),
                    placeholder = { Text("Filter by block name or Gram Panchayat (e.g. Dubrajpur, Kasba, Chinpai)...", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search blocks and Gram Panchayats",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (worksSearchQuery.isNotBlank()) {
                            IconButton(onClick = { worksSearchQuery = "" }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search", modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }

            // Blocks list filtered by block or GP name and sorted by TS %
            val sortedByProgress = data.blocks
                .filter {
                    if (worksSearchQuery.isBlank()) true
                    else {
                        val q = worksSearchQuery.trim()
                        val alert = com.example.data.models.getBlockGpPerformance(it.blockName)
                        it.blockName.contains(q, ignoreCase = true) ||
                        alert.bestGp.contains(q, ignoreCase = true) ||
                        alert.poorGp.contains(q, ignoreCase = true) ||
                        "${it.works.gps} GP".contains(q, ignoreCase = true)
                    }
                }
                .sortedByDescending { it.works.tsPct }

            if (sortedByProgress.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No blocks found matching \"$worksSearchQuery\"",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                TextButton(onClick = { worksSearchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reset search filter")
                                }
                            }
                        }
                    }
                }
            } else {
                items(sortedByProgress, key = { it.slNo }) { block ->
                val w = block.works
                val alert = com.example.data.models.getBlockGpPerformance(block.blockName)
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
                                Text(
                                    text = block.blockName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${w.gps} Gram Panchayats • ${w.totalWorks} Total Works",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (w.tsPct >= 50.0) EmeraldSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "${w.tsPct}% TS",
                                    color = if (w.tsPct >= 50.0) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Gram Panchayat Status Badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldSuccess.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "🏆 Best: ${alert.bestGp}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFDC2626).copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "⚠️ Alert: ${alert.poorGp}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress Bar for TS
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Technical Sanction (TS): ${w.ts} of ${w.totalWorks}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${w.tsPct.formatFund()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        AnimatedLinearProgressIndicator(
                            targetProgress = (w.tsPct / 100.0).toFloat(),
                            color = MaterialTheme.colorScheme.primary,
                            height = 6
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress Bar for AS
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Admin Sanction (AS): ${w.asSanctions} of ${w.totalWorks}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${w.asPct.formatFund()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        AnimatedLinearProgressIndicator(
                            targetProgress = (w.asPct / 100.0).toFloat(),
                            color = EmeraldSuccess,
                            height = 6
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Metrics pills: Estimates Started vs Pending
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Estimates: ${w.estimatesStarted}",
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Pending: ${w.pending}",
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
}
