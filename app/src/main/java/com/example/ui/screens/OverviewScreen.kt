package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BirbhumData
import com.example.data.models.BlockData
import com.example.data.models.formatFund
import com.example.ui.components.AllotmentExpenditureChartCard
import com.example.ui.components.AnimatedLinearProgressIndicator
import com.example.ui.components.CategoryMatrixWorkAnalysisCard
import com.example.ui.components.ExecutiveLandingKpiSection
import com.example.ui.components.HistoricalTrendsChartCard
import com.example.ui.components.NmmsPerformanceChartCard
import com.example.ui.components.PbaOverviewKpiCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun OverviewScreen(
    data: BirbhumData,
    onNavigateToAllotments: () -> Unit,
    onNavigateToWorks: () -> Unit,
    onNavigateToFto: () -> Unit,
    onNavigateToEkyc: () -> Unit,
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToPbaWorks: () -> Unit = onNavigateToWorks,
    onSelectBlock: (BlockData) -> Unit
) {
    val allot = data.allotmentSummary
    val works = data.worksSummary
    val fto = data.ftoSummary
    val ekyc = data.ekycSummary

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("overview_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // District Context Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("district_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "DISTRICT BIRBHUM",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EmeraldSuccess.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "WEST BENGAL",
                                    color = EmeraldSuccess,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Executive Monitoring Dashboard",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "VB-G RAM G Implementation • 19 Blocks • 167 GPs",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldSuccess)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Auto-Updated • ${data.lastUpdated}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldSuccess
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // ==========================================
        // 1. EXECUTIVE KPI, PIE & PROGRESS OVERVIEW (Upper Landing Section)
        // ==========================================
        item {
            ExecutiveLandingKpiSection(
                data = data,
                onNavigateToCalendar = onNavigateToCalendar
            )
        }

        // ==========================================
        // 1B. PASCHIM BANGA AWAAS (PBA) GRGC LIVE SYNC KPI
        // ==========================================
        item {
            PbaOverviewKpiCard(
                pba = data.pbaSummary,
                onNavigateToPbaWorks = onNavigateToPbaWorks
            )
        }

        // ==========================================
        // QUICK LAUNCH: PERSOMDAYS (PD) CALENDAR
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToCalendar() }
                    .testTag("banner_open_pd_calendar"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Persondays (PD) Calendar",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = EmeraldSuccess.copy(alpha = 0.18f)
                                ) {
                                    Text(
                                        text = data.nmmsSummary.date,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = EmeraldSuccess,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "View daily attendance heatmap, monthly targets & block breakdowns",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Calendar",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // ==========================================
        // 2. TODAY'S PERFORMANCE (NMMS ATTENDANCE SHEET) WITH GRAPHICAL CHART
        // ==========================================
        item {
            NmmsPerformanceChartCard(nmms = data.nmmsSummary)
        }

        // ==========================================
        // 2. WORK ANALYSIS BY CATEGORY & PROGRESS (GP x CATEGORY MATRIX)
        // ==========================================
        item {
            CategoryMatrixWorkAnalysisCard(matrix = data.categoryMatrixSummary)
        }

        // ==========================================
        // 3. STATE ALLOTMENT SUMMARY CARD (Material3)
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("state_allotment_summary_card"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GovNavyPrimaryLight.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = GovNavyPrimaryLight,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "State Allotment Summary",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Google Sheet GID 263874047",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "80% / 20% Rule",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Headline Amount Callout
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        GovNavyPrimaryLight,
                                        Color(0xFF144A82)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "TOTAL STATE ALLOTMENT RECEIVED",
                                style = MaterialTheme.typography.labelSmall,
                                color = AmberGold,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "₹${allot.totalAllotmentLakh.formatFund()} L",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "₹${allot.totalAllotmentCr.formatFund()} Crore",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Split Details: Distributable (80%) vs Retention (20%)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Distributable to Blocks
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldSuccess.copy(alpha = 0.1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Distributable to Blocks (80%)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "₹${allot.distributableLakh.formatFund()} L",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "₹${allot.distributableCr.formatFund()} Cr to 19 Blocks",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // District Retention
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = GoldAccent.copy(alpha = 0.15f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "District Retention (20%)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "₹${allot.districtRetentionLakh.formatFund()} L",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "₹${allot.districtRetentionCr.formatFund()} Cr Retained",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Animated Progress Bar for Distributable Share
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Distributable Ratio to Blocks",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "80.0% Distributable",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        AnimatedLinearProgressIndicator(
                            targetProgress = 0.80f,
                            color = EmeraldSuccess,
                            height = 8
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3 Category Heads Breakdown
                    Text(
                        text = "Fund Allocation by Head:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CyanMetric.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Cat I (SC)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyanMetric)
                                Text("₹1,584 L", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                Text("46.9% share", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GoldAccent.copy(alpha = 0.15f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Cat II (ST)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                                Text("₹230.40 L", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                Text("6.8% share", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PurpleMetric.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Cat III (Gen)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PurpleMetric)
                                Text("₹1,563.20 L", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                Text("46.3% share", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "126,940 Total Units (${allot.totalCountA} Cat A + ${allot.totalCountB} Cat B)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )

                        TextButton(
                            onClick = onNavigateToAllotments,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("View 19 Blocks", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3B. STATE ALLOTMENT VS. ACTUAL EXPENDITURE & PERSONDAYS CHART
        // ==========================================
        item {
            AllotmentExpenditureChartCard(
                data = data,
                onSelectBlock = onSelectBlock
            )
        }

        // ==========================================
        // 2. WORKS PROGRESS SUMMARY CARD (Material3)
        // WITH ANIMATED LINEAR PROGRESS BARS
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("works_progress_summary_card"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0284C7).copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Engineering,
                                    contentDescription = null,
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Works Progress & Sanctions",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Technical & Admin Sanctions Directory",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0284C7).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "167 GPs",
                                color = Color(0xFF0284C7),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Works KPI Grid (4 Metrics)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Total Works", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${works.totalWorks}", fontSize = 16.sp, fontWeight = FontWeight.Black)
                                Text("19 Blocks", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Estimates", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${works.estimatesStarted}", fontSize = 16.sp, fontWeight = FontWeight.Black)
                                Text("67.4% pipeline", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0284C7).copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("TS Sanction", fontSize = 11.sp, color = Color(0xFF0284C7), fontWeight = FontWeight.Bold)
                                Text("${works.technicalSanctions}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF0284C7))
                                Text("${works.tsPercent.formatFund()}% approved", fontSize = 10.sp, color = Color(0xFF0284C7))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldSuccess.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("AS Sanction", fontSize = 11.sp, color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                                Text("${works.adminSanctions}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = EmeraldSuccess)
                                Text("${works.asPercent.formatFund()}% sanctioned", fontSize = 10.sp, color = EmeraldSuccess)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ANIMATED PROGRESS BARS FOR WORKS
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // 1. Technical Sanctions (TS) Animated Bar
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF0284C7))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Technical Sanctions (TS)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "${works.technicalSanctions} of ${works.totalWorks} (${works.tsPercent.formatFund()}%)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0284C7)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            AnimatedLinearProgressIndicator(
                                targetProgress = (works.tsPercent / 100.0).toFloat(),
                                color = Color(0xFF0284C7),
                                height = 8
                            )
                        }

                        // 2. Admin Sanctions (AS) Animated Bar
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldSuccess)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Admin Sanctions (AS)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "${works.adminSanctions} of ${works.totalWorks} (${works.asPercent.formatFund()}%)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            AnimatedLinearProgressIndicator(
                                targetProgress = (works.asPercent / 100.0).toFloat(),
                                color = EmeraldSuccess,
                                height = 8
                            )
                        }

                        // 3. Estimates Started (Preparation Pipeline) Animated Bar
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF59E0B))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Estimates Prepared Pipeline",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "${works.estimatesStarted} of ${works.totalWorks} (67.4%)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            AnimatedLinearProgressIndicator(
                                targetProgress = (works.estimatesStarted.toFloat() / works.totalWorks.toFloat()),
                                color = Color(0xFFF59E0B),
                                height = 8
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "3,158 works pending final administrative clearance",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )

                        TextButton(
                            onClick = onNavigateToWorks,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Explore Works", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. e-KYC & AUTH STATUS CARD (Material3)
        // WITH ANIMATED LINEAR PROGRESS BARS
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ekyc_status_summary_card"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(EmeraldSuccess.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Worker e-KYC & Authentication",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Aadhaar Seeding & ABPS Readiness",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldSuccess.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${ekyc.ekycPercent}% Completed",
                                color = EmeraldSuccess,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // PRIMARY ANIMATED PROGRESS BAR: e-KYC Completion
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "11,10,591 OF 12,30,522 WORKERS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${ekyc.ekycPercent}% e-KYC Done",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldSuccess
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "99.7% Aadhaar Seeded",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Large animated progress bar
                        AnimatedLinearProgressIndicator(
                            targetProgress = (ekyc.ekycPercent / 100.0).toFloat(),
                            color = EmeraldSuccess,
                            height = 10,
                            animationDurationMs = 1300
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // SECONDARY ANIMATED PROGRESS BARS FOR AUTH & ABPS
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Aadhaar Seeding Bar (99.72%)
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Aadhaar Seeding Coverage",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "12,27,126 Workers (99.7%)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanMetric
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            AnimatedLinearProgressIndicator(
                                targetProgress = (ekyc.aadhaarSeeded.toFloat() / ekyc.totalWorkers.toFloat()),
                                color = CyanMetric,
                                height = 6
                            )
                        }

                        // ABPS Direct Payment Eligibility Bar (89.4%)
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "ABPS Direct Payment Ready",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "11,00,376 Workers (89.4%)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            AnimatedLinearProgressIndicator(
                                targetProgress = (ekyc.abpsEligible.toFloat() / ekyc.totalWorkers.toFloat()),
                                color = MaterialTheme.colorScheme.primary,
                                height = 6
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Metric Pills: Seeded, Authenticated, ABPS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Aadhaar Seeded", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("12.27 L", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("99.72%", fontSize = 10.sp, color = EmeraldSuccess)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Authenticated", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("12.04 L", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("AUA Success", fontSize = 10.sp, color = Color(0xFF0284C7))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("ABPS Eligible", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                Text("11.00 L", fontSize = 14.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                Text("Payment Ready", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Spotlight Callout: Dubrajpur #1
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = EmeraldSuccess.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Dubrajpur Block leads District Birbhum at 94.07% e-KYC compliance (72,067 workers)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "22,865 auth failures under active field remediation",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )

                        TextButton(
                            onClick = onNavigateToEkyc,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("View Leaderboard", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 4. HISTORICAL 6-MONTH TRENDS CHART
        // ==========================================
        item {
            HistoricalTrendsChartCard()
        }

        // ==========================================
        // 5. MATERIAL FTOs & AUDIT MINI-CARD
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToFto() },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NavyPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = NavyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Material FTOs Verification",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = EmeraldSuccess.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "100% BLT Verified",
                                        color = EmeraldSuccess,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "₹72.90 Cr Accepted • ₹1.23 Cr Rejected audit findings",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View FTOs",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // ==========================================
        // 5. BLOCKS SPOTLIGHT DIRECTORY
        // ==========================================
        item {
            SectionHeader(
                title = "Notable Blocks Spotlight",
                subtitle = "Dubrajpur, Bolpur-Sriniketan, Khoyrasol, Sainthia",
                badgeText = "Tap for Dossier"
            )
        }

        // Spotlight Blocks: Dubrajpur, Bolpur, Khoyrasol, Sainthia
        val spotlightNames = listOf("DUBRAJPUR", "BOLPUR-SRINIKETAN", "KHOYRASOL", "SAINTHIA")
        items(spotlightNames) { targetName ->
            val block = data.blocks.firstOrNull { it.blockName.equals(targetName, ignoreCase = true) }
            if (block != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectBlock(block) }
                        .testTag("spotlight_card_${block.blockName}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = block.blockName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (block.blockName.equals("DUBRAJPUR", ignoreCase = true)) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = EmeraldSuccess.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "#1 e-KYC (94.07%)",
                                            color = EmeraldSuccess,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Distributable Allotment: ₹${block.allotment.totalLakh.formatFund()} Lakh (₹${block.allotment.totalCr.formatFund()} Cr)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "Works: ${block.works.totalWorks} (${block.works.asSanctions} AS)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "•",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "e-KYC: ${block.ekyc.ekycPct}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldSuccess
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "View Details",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
