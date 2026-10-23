package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.NmmsBlockPoint
import com.example.data.models.NmmsSummary
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GovNavyPrimaryLight

enum class NmmsChartMetric(val label: String, val unit: String) {
    TODAY_ATTENDANCE("Today's Attendance", "workers"),
    TOTAL_ATTENDANCE("Total Attendance", "workers"),
    TOTAL_MUSTERS("Total Musters", "musters")
}

@Composable
fun NmmsPerformanceChartCard(
    nmms: NmmsSummary,
    modifier: Modifier = Modifier
) {
    var selectedMetric by remember { mutableStateOf(NmmsChartMetric.TODAY_ATTENDANCE) }
    var selectedBlockIndex by remember { mutableIntStateOf(0) }
    var showRecentMusters by remember { mutableStateOf(false) }

    // Chart animation
    var isStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isStarted = true
    }
    val animProgress by animateFloatAsState(
        targetValue = if (isStarted) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "nmms_chart_anim"
    )

    // Sorted blocks for chart display (take top 10 for clean readability)
    val topBlocks = remember(selectedMetric, nmms.blockPerformance) {
        val list = nmms.blockPerformance
        when (selectedMetric) {
            NmmsChartMetric.TODAY_ATTENDANCE -> list.sortedByDescending { it.todayAttendance }
            NmmsChartMetric.TOTAL_ATTENDANCE -> list.sortedByDescending { it.attendance }
            NmmsChartMetric.TOTAL_MUSTERS -> list.sortedByDescending { it.musters }
        }.take(10)
    }

    val selectedBlock = topBlocks.getOrNull(selectedBlockIndex) ?: topBlocks.firstOrNull()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("nmms_performance_card"),
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
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(EmeraldSuccess.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Today's Performance",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EmeraldSuccess.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "LIVE NMMS",
                                    color = EmeraldSuccess,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "NMMS Attendance Sheet • Daily Muster Rolls Verification",
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
                        text = nmms.date,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Metric Banner: Today's Attendance & Today's Musters
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF064E3B), // Deep emerald
                                Color(0xFF047857)  // Vibrant emerald
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TODAY'S FIELD WORKFORCE (NMMS)",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF34D399))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Active Field Sync",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "%,d".format(nmms.todayAttendance),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Workers Present Today on Sites",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "%,d".format(nmms.todayMusters),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Active Muster Rolls",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Secondary Stats Row: Total Cumulative Musters & Total Attendance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = GovNavyPrimaryLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Total Attendance",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "%,d".format(nmms.totalAttendance),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Cumulative across 19 Blocks",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = null,
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Total Musters",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "%,d".format(nmms.totalMusters),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Generated & Uploaded",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // GRAPHICAL CHART: NMMS BLOCK PERFORMANCE
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Block Performance Chart",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Tap bar to inspect",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metric Toggle Segmented Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                NmmsChartMetric.values().forEach { metric ->
                    val isSelected = selectedMetric == metric
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedMetric = metric
                                selectedBlockIndex = 0
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (metric == NmmsChartMetric.TODAY_ATTENDANCE) "Today"
                                else if (metric == NmmsChartMetric.TOTAL_ATTENDANCE) "Total Att."
                                else "Musters",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selected Block Detail Callout
            if (selectedBlock != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EmeraldSuccess.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Selected: ${selectedBlock.block}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            val valText = when (selectedMetric) {
                                NmmsChartMetric.TODAY_ATTENDANCE -> "Today: %,d workers".format(selectedBlock.todayAttendance)
                                NmmsChartMetric.TOTAL_ATTENDANCE -> "Total: %,d workers".format(selectedBlock.attendance)
                                NmmsChartMetric.TOTAL_MUSTERS -> "Musters: %,d rolls".format(selectedBlock.musters)
                            }
                            Text(
                                text = valText,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        val pctOfTotal = when (selectedMetric) {
                            NmmsChartMetric.TODAY_ATTENDANCE -> if (nmms.todayAttendance > 0) (selectedBlock.todayAttendance * 100.0 / nmms.todayAttendance) else 0.0
                            NmmsChartMetric.TOTAL_ATTENDANCE -> if (nmms.totalAttendance > 0) (selectedBlock.attendance * 100.0 / nmms.totalAttendance) else 0.0
                            NmmsChartMetric.TOTAL_MUSTERS -> if (nmms.totalMusters > 0) (selectedBlock.musters * 100.0 / nmms.totalMusters) else 0.0
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldSuccess
                        ) {
                            Text(
                                text = String.format(java.util.Locale.US, "%.2f%% of District", pctOfTotal),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas Bar Chart Component
            val maxVal = remember(topBlocks, selectedMetric) {
                val max = topBlocks.maxOfOrNull {
                    when (selectedMetric) {
                        NmmsChartMetric.TODAY_ATTENDANCE -> it.todayAttendance
                        NmmsChartMetric.TOTAL_ATTENDANCE -> it.attendance
                        NmmsChartMetric.TOTAL_MUSTERS -> it.musters
                    }
                } ?: 1
                if (max == 0) 1 else max
            }

            val primaryColor = MaterialTheme.colorScheme.primary
            val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
            val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(surfaceVariant.copy(alpha = 0.3f))
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(topBlocks, selectedMetric) {
                            detectTapGestures { offset ->
                                val barCount = topBlocks.size
                                if (barCount > 0) {
                                    val stepX = size.width / barCount
                                    val tappedIndex = (offset.x / stepX).toInt().coerceIn(0, barCount - 1)
                                    selectedBlockIndex = tappedIndex
                                }
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height - 24.dp.toPx() // leave space for labels
                    val count = topBlocks.size
                    if (count == 0) return@Canvas

                    val barWidth = (w / count) * 0.65f
                    val slotWidth = w / count

                    // Draw 3 horizontal grid lines
                    val gridLines = 3
                    for (i in 0..gridLines) {
                        val y = h - (h * (i.toFloat() / gridLines))
                        drawLine(
                            color = onSurfaceVariant.copy(alpha = 0.15f),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Draw Bars
                    topBlocks.forEachIndexed { index, item ->
                        val rawValue = when (selectedMetric) {
                            NmmsChartMetric.TODAY_ATTENDANCE -> item.todayAttendance
                            NmmsChartMetric.TOTAL_ATTENDANCE -> item.attendance
                            NmmsChartMetric.TOTAL_MUSTERS -> item.musters
                        }
                        val barHeight = (rawValue.toFloat() / maxVal) * h * animProgress
                        val x = index * slotWidth + (slotWidth - barWidth) / 2f
                        val y = h - barHeight

                        val isSelected = index == selectedBlockIndex

                        val barBrush = if (isSelected) {
                            Brush.verticalGradient(
                                listOf(
                                    AmberGold,
                                    Color(0xFFD97706)
                                )
                            )
                        } else {
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF059669),
                                    Color(0xFF10B981)
                                )
                            )
                        }

                        // Draw Bar
                        drawRoundRect(
                            brush = barBrush,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight.coerceAtLeast(4.dp.toPx())),
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )

                        // Highlight ring around selected bar
                        if (isSelected) {
                            drawRoundRect(
                                color = AmberGold,
                                topLeft = Offset(x - 2.dp.toPx(), y - 2.dp.toPx()),
                                size = Size(barWidth + 4.dp.toPx(), barHeight + 4.dp.toPx()),
                                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                            )
                        }
                    }
                }

                // X-axis block abbreviations
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    topBlocks.forEachIndexed { idx, item ->
                        val shortName = item.block.take(3).uppercase()
                        val isSelected = idx == selectedBlockIndex
                        Text(
                            text = shortName,
                            fontSize = 8.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (isSelected) AmberGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Top Performing Blocks List Preview
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                topBlocks.take(4).forEachIndexed { idx, item ->
                    val isSelected = idx == selectedBlockIndex
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedBlockIndex = idx },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) EmeraldSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (idx == 0) AmberGold else MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "#${idx + 1}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (idx == 0) Color.Black else MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = item.block,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Today: %,d • Total: %,d • Musters: %,d".format(item.todayAttendance, item.attendance, item.musters),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Expandable Recent Musters Sheet
            if (nmms.recentMusters.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showRecentMusters = !showRecentMusters }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = null,
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Live Field Musters Feed (${nmms.recentMusters.size} records)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Icon(
                            imageVector = if (showRecentMusters) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                AnimatedVisibility(visible = showRecentMusters) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        nmms.recentMusters.forEach { rm ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 1.dp
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${rm.block} • ${rm.gp} GP",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = EmeraldSuccess.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "${rm.persondays} Persondays",
                                                color = EmeraldSuccess,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Work: ${rm.workCode}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = "Muster Roll #${rm.mustrollNo} • Status: Synced Today",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp
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
