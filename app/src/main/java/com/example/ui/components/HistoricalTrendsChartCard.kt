package com.example.ui.components

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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.formatFund
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GovNavyPrimaryLight

data class MonthlyTrendPoint(
    val month: String,
    val fullMonth: String,
    val allotmentLakh: Double,
    val allotmentCr: Double,
    val worksCompleted: Int,
    val completionPercent: Double
)

enum class TrendViewMode(val label: String) {
    COMBINED("Combined Trends"),
    ALLOTMENT("Allotment (₹ Lakh)"),
    COMPLETION("Projects Completed")
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun HistoricalTrendsChartCard(
    modifier: Modifier = Modifier
) {
    // 6 Months Historical Trends (May 2026 - Oct 2026)
    val trendData = remember {
        listOf(
            MonthlyTrendPoint("May", "May 2026", 2100.0, 21.00, 1420, 25.4),
            MonthlyTrendPoint("Jun", "Jun 2026", 2650.0, 26.50, 1680, 30.1),
            MonthlyTrendPoint("Jul", "Jul 2026", 3100.0, 31.00, 1910, 34.2),
            MonthlyTrendPoint("Aug", "Aug 2026", 3450.0, 34.50, 2130, 38.1),
            MonthlyTrendPoint("Sep", "Sep 2026", 3880.0, 38.80, 2290, 41.0),
            MonthlyTrendPoint("Oct", "Oct 2026 (Current)", 4222.0, 42.22, 2393, 42.9)
        )
    }

    var selectedMode by remember { mutableStateOf(TrendViewMode.COMBINED) }
    var selectedPointIndex by remember { mutableStateOf(trendData.size - 1) }

    // Animation progress
    var isStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isStarted = true
    }
    val animProgress by animateFloatAsState(
        targetValue = if (isStarted) 1f else 0f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "chart_anim"
    )

    val selectedPoint = trendData[selectedPointIndex]

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("historical_trends_chart_card"),
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
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Historical 6-Month Trends",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "State Allotment vs Project Sanctions",
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
                        text = "+101.0% Allotment",
                        color = EmeraldSuccess,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mode Selector Tabs (Recharts-style filter)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TrendViewMode.values().forEach { mode ->
                    val isSelected = mode == selectedMode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                            .clickable { selectedMode = mode }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Active Month Detail Banner / Tooltip Display
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = selectedPoint.fullMonth,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "State Allotment: ₹${formatFund(selectedPoint.allotmentLakh)} L (₹${formatFund(selectedPoint.allotmentCr)} Cr)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Projects Sanctioned",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${selectedPoint.worksCompleted} Works (${selectedPoint.completionPercent}%)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Native Jetpack Compose Canvas Chart (Visual Recharts replacement)
            val primaryColor = MaterialTheme.colorScheme.primary
            val successColor = EmeraldSuccess
            val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            val textColor = MaterialTheme.colorScheme.onSurfaceVariant
            val textMeasurer = rememberTextMeasurer()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .pointerInput(trendData) {
                        detectTapGestures { offset ->
                            val pointWidth = size.width / trendData.size
                            val index = (offset.x / pointWidth).toInt().coerceIn(0, trendData.size - 1)
                            selectedPointIndex = index
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height - 40f
                    val topPadding = 20f
                    val bottomPadding = height

                    val maxAllotment = 4500.0
                    val minAllotment = 1500.0
                    val maxWorks = 2600.0
                    val minWorks = 1000.0

                    // 1. Draw horizontal reference grid lines
                    val steps = 3
                    for (i in 0..steps) {
                        val y = topPadding + (height - topPadding) * (i.toFloat() / steps)
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }

                    val pointsCount = trendData.size
                    val spacing = width / (pointsCount - 1)

                    val allotmentCoordinates = mutableListOf<Offset>()
                    val worksCoordinates = mutableListOf<Offset>()

                    for (i in trendData.indices) {
                        val x = i * spacing
                        val allotVal = trendData[i].allotmentLakh
                        val normalizedAllot = ((allotVal - minAllotment) / (maxAllotment - minAllotment)).toFloat().coerceIn(0f, 1f)
                        val yAllot = bottomPadding - (normalizedAllot * (bottomPadding - topPadding) * animProgress)
                        allotmentCoordinates.add(Offset(x, yAllot))

                        val worksVal = trendData[i].worksCompleted.toDouble()
                        val normalizedWorks = ((worksVal - minWorks) / (maxWorks - minWorks)).toFloat().coerceIn(0f, 1f)
                        val yWorks = bottomPadding - (normalizedWorks * (bottomPadding - topPadding) * animProgress)
                        worksCoordinates.add(Offset(x, yWorks))
                    }

                    // 2. Draw Allotment Area & Line
                    if (selectedMode == TrendViewMode.COMBINED || selectedMode == TrendViewMode.ALLOTMENT) {
                        val path = Path().apply {
                            moveTo(allotmentCoordinates[0].x, allotmentCoordinates[0].y)
                            for (i in 1 until allotmentCoordinates.size) {
                                val prev = allotmentCoordinates[i - 1]
                                val curr = allotmentCoordinates[i]
                                val cX1 = prev.x + (curr.x - prev.x) / 2
                                val cY1 = prev.y
                                val cX2 = prev.x + (curr.x - prev.x) / 2
                                val cY2 = curr.y
                                cubicTo(cX1, cY1, cX2, cY2, curr.x, curr.y)
                            }
                        }

                        // Gradient Area Fill under Allotment Curve
                        val fillPath = Path().apply {
                            addPath(path)
                            lineTo(allotmentCoordinates.last().x, bottomPadding)
                            lineTo(allotmentCoordinates.first().x, bottomPadding)
                            close()
                        }

                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.25f),
                                    primaryColor.copy(alpha = 0.02f)
                                ),
                                startY = topPadding,
                                endY = bottomPadding
                            )
                        )

                        // Allotment Line
                        drawPath(
                            path = path,
                            color = primaryColor,
                            style = Stroke(width = 4f, cap = StrokeCap.Round)
                        )
                    }

                    // 3. Draw Project Completion (Works) Curve
                    if (selectedMode == TrendViewMode.COMBINED || selectedMode == TrendViewMode.COMPLETION) {
                        val worksPath = Path().apply {
                            moveTo(worksCoordinates[0].x, worksCoordinates[0].y)
                            for (i in 1 until worksCoordinates.size) {
                                val prev = worksCoordinates[i - 1]
                                val curr = worksCoordinates[i]
                                val cX1 = prev.x + (curr.x - prev.x) / 2
                                val cY1 = prev.y
                                val cX2 = prev.x + (curr.x - prev.x) / 2
                                val cY2 = curr.y
                                cubicTo(cX1, cY1, cX2, cY2, curr.x, curr.y)
                            }
                        }

                        if (selectedMode == TrendViewMode.COMPLETION) {
                            val fillWorksPath = Path().apply {
                                addPath(worksPath)
                                lineTo(worksCoordinates.last().x, bottomPadding)
                                lineTo(worksCoordinates.first().x, bottomPadding)
                                close()
                            }
                            drawPath(
                                path = fillWorksPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        successColor.copy(alpha = 0.25f),
                                        successColor.copy(alpha = 0.02f)
                                    ),
                                    startY = topPadding,
                                    endY = bottomPadding
                                )
                            )
                        }

                        drawPath(
                            path = worksPath,
                            color = successColor,
                            style = Stroke(
                                width = 3.5f,
                                cap = StrokeCap.Round,
                                pathEffect = if (selectedMode == TrendViewMode.COMBINED) PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f) else null
                            )
                        )
                    }

                    // 4. Draw Data Points and Selected Marker
                    for (i in trendData.indices) {
                        val isSelected = i == selectedPointIndex
                        val allotPt = allotmentCoordinates[i]
                        val worksPt = worksCoordinates[i]

                        if (selectedMode == TrendViewMode.COMBINED || selectedMode == TrendViewMode.ALLOTMENT) {
                            drawCircle(
                                color = Color.White,
                                radius = if (isSelected) 8f else 5f,
                                center = allotPt
                            )
                            drawCircle(
                                color = primaryColor,
                                radius = if (isSelected) 6f else 3.5f,
                                center = allotPt
                            )
                        }

                        if (selectedMode == TrendViewMode.COMBINED || selectedMode == TrendViewMode.COMPLETION) {
                            drawCircle(
                                color = Color.White,
                                radius = if (isSelected) 7f else 4.5f,
                                center = worksPt
                            )
                            drawCircle(
                                color = successColor,
                                radius = if (isSelected) 5f else 3f,
                                center = worksPt
                            )
                        }

                        // Vertical highlight guide for selected point
                        if (isSelected) {
                            drawLine(
                                color = primaryColor.copy(alpha = 0.35f),
                                start = Offset(allotPt.x, topPadding),
                                end = Offset(allotPt.x, bottomPadding),
                                strokeWidth = 1.5f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                            )
                        }

                        // Month label on X Axis
                        val labelLayout = textMeasurer.measure(
                            text = trendData[i].month,
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) primaryColor else textColor
                            )
                        )
                        drawText(
                            textLayoutResult = labelLayout,
                            topLeft = Offset(
                                x = allotPt.x - (labelLayout.size.width / 2),
                                y = bottomPadding + 10f
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Chart Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(primaryColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "State Allotment (₹ Lakh)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(successColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Project Completion (Works)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
