package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.*
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GovNavyPrimaryLight
import kotlin.math.cos
import kotlin.math.sin

data class SectorPieItem(
    val id: String,
    val name: String,
    val icon: String,
    val value: Float,
    val activeWorks: Int,
    val persondays: Int,
    val activeGps: Int,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExecutiveLandingKpiSection(
    data: BirbhumData,
    modifier: Modifier = Modifier,
    onNavigateToCalendar: () -> Unit = {}
) {
    val nmms = data.nmmsSummary
    val matrix = data.categoryMatrixSummary
    val works = data.worksSummary

    // Derived KPI values
    val todaysPersondays = nmms.todayAttendance // 3,722
    val activeWorkSites = matrix.totalOngoingWorks // 1,693
    val totalGps = 167
    val activeGpsCount = 142 // 142 active GPs with ongoing works (85.0%)
    val pdPerGp = if (totalGps > 0) (matrix.totalPersondays.toDouble() / totalGps) else 0.0 // ~517.3
    val todayPdPerActiveGp = if (activeGpsCount > 0) (todaysPersondays.toDouble() / activeGpsCount) else 0.0 // ~26.2
    val activeWorkPerGp = if (totalGps > 0) (activeWorkSites.toDouble() / totalGps) else 0.0 // ~10.14
    val nonPlantation = matrix.nonPlantationKpi
    val waterSecurity = matrix.waterSecurityStatus

    // Highest PD Block & GP
    val highestPdBlock = nmms.blockPerformance.maxByOrNull { it.attendance } ?: NmmsBlockPoint("KHOYRASOL", 23436, 3812, 520)
    val runnerUpBlock = nmms.blockPerformance.filter { it.block != highestPdBlock.block }.maxByOrNull { it.attendance } ?: NmmsBlockPoint("DUBRAJPUR", 17575, 3240, 485)

    // Drilldown dialog state
    var showDrilldownSheet by remember { mutableStateOf(false) }
    var drilldownInitialSector by remember { mutableStateOf<String?>(null) }
    var drilldownInitialBlock by remember { mutableStateOf<String?>(null) }

    // Sector items for Pie Chart (Mapped from Category Matrix)
    val sectorColors = listOf(
        Color(0xFFD97706), // Drought - Amber
        Color(0xFF059669), // Water Bodies - Emerald
        Color(0xFF0284C7), // Water Conservation - Sky
        Color(0xFF16A34A), // Individual Land - Green
        Color(0xFF2563EB), // Connectivity - Blue
        Color(0xFFE11D48), // Sanitation - Rose
        Color(0xFF7C3AED), // Flood Control - Purple
        Color(0xFFF59E0B), // Micro Irrigation - Gold
        Color(0xFF0D9488), // Others - Teal
    )

    val sectorItems = remember(matrix.categories) {
        val cats = matrix.categories
        if (cats.isNotEmpty()) {
            cats.take(9).mapIndexed { idx, cat ->
                SectorPieItem(
                    id = cat.id,
                    name = cat.name,
                    icon = cat.icon,
                    value = cat.ongoingWorks.toFloat().coerceAtLeast(1f),
                    activeWorks = cat.ongoingWorks,
                    persondays = cat.persondaysGenerated,
                    activeGps = (cat.ongoingWorks * 142 / 1693).coerceAtLeast(2),
                    color = sectorColors.getOrElse(idx) { Color(0xFF64748B) }
                )
            }
        } else {
            listOf(
                SectorPieItem("DROUGHT", "Drought Proofing", "☀️", 1217f, 1217, 43245, 128, Color(0xFFD97706)),
                SectorPieItem("WATER_BODIES", "Traditional Water Bodies", "⛲", 113f, 113, 23054, 82, Color(0xFF059669)),
                SectorPieItem("WATER_CONSERVATION", "Water Conservation", "🌧️", 105f, 105, 14560, 76, Color(0xFF0284C7)),
                SectorPieItem("INDIVIDUAL_LAND", "Individuals Land (Cat IV)", "🌱", 82f, 82, 2241, 54, Color(0xFF16A34A)),
                SectorPieItem("RURAL_CONNECTIVITY", "Rural Connectivity", "🛣️", 63f, 63, 855, 48, Color(0xFF2563EB)),
                SectorPieItem("RURAL_SANITATION", "Rural Sanitation", "🚻", 52f, 52, 677, 39, Color(0xFFE11D48)),
                SectorPieItem("FLOOD_CONTROL", "Flood Control", "🌊", 31f, 31, 389, 24, Color(0xFF7C3AED)),
                SectorPieItem("MICRO_IRRIGATION", "Micro Irrigation", "🚿", 18f, 18, 459, 15, Color(0xFFF59E0B)),
                SectorPieItem("OTHERS", "Infra & Fisheries", "🏫", 12f, 12, 904, 10, Color(0xFF0D9488))
            )
        }
    }

    var selectedSectorId by remember { mutableStateOf<String?>(sectorItems.firstOrNull()?.id) }
    val selectedSector = sectorItems.find { it.id == selectedSectorId } ?: sectorItems.firstOrNull()

    // Chart animation
    var animStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animStarted = true
    }
    val animProgress by animateFloatAsState(
        targetValue = if (animStarted) 1f else 0f,
        animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
        label = "pie_anim"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("executive_landing_kpi_section"),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Bar
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
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Executive Field Overview",
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
                                    text = "TODAY'S KPI",
                                    color = EmeraldSuccess,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "District Birbhum • Real-time Field Telemetry & Sector Matrix",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalButton(
                        onClick = onNavigateToCalendar,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier
                            .height(30.dp)
                            .testTag("btn_kpi_pd_calendar")
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PD Calendar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldSuccess)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Live",
                                color = EmeraldSuccess,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 6-KPI GRID
            // ==========================================
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Row 1: Today's Persondays & Active Work Sites
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // KPI 1: Today's Persondays (Clickable -> Opens PD Calendar)
                    val todayTargetPct = if (3500 > 0) (todaysPersondays * 100.0 / 3500.0) else 0.0
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToCalendar() }
                            .testTag("kpi_todays_persondays"),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TODAY'S PD",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess,
                                    letterSpacing = 0.8.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "View Calendar",
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "%,d".format(todaysPersondays),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Persondays (${nmms.date})",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (todayTargetPct >= 100.0) EmeraldSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "${String.format(java.util.Locale.US, "%.2f", todayTargetPct)}% of Daily Target • Tap 📅",
                                    color = if (todayTargetPct >= 100.0) EmeraldSuccess else MaterialTheme.colorScheme.primary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // KPI 2: Active Work Sites (Drillable sector wise)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                drilldownInitialSector = null
                                drilldownInitialBlock = null
                                showDrilldownSheet = true
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ACTIVE SITES",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0284C7),
                                    letterSpacing = 0.8.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF0284C7).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "DRILLABLE",
                                        color = Color(0xFF0284C7),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "%,d".format(activeWorkSites),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Active Works Across Sectors",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Sector-wise Drilldown",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0284C7)
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }

                // Row 2: Active Gram Panchayat & PD/GP
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // KPI 3: Active Gram Panchayat (Drillable sector wise)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                drilldownInitialSector = null
                                drilldownInitialBlock = null
                                showDrilldownSheet = true
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7C3AED).copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ACTIVE GPS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7C3AED),
                                    letterSpacing = 0.8.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF7C3AED).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "DRILLABLE",
                                        color = Color(0xFF7C3AED),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$activeGpsCount",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = " / $totalGps GPs",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            Text(
                                text = "85.0% Field Saturation",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "View Active GP List",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7C3AED)
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Color(0xFF7C3AED),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }

                    // KPI 4: PD / GP (Persondays per GP)
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PD / GP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberGold,
                                    letterSpacing = 0.8.sp
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = AmberGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "%,d".format(Math.round(pdPerGp)),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Avg PD / Gram Panchayat",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AmberGold.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "Today: %,d PD/Active GP".format(Math.round(todayPdPerActiveGp)),
                                    color = Color(0xFFD97706),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Row 3: Works Ongoing / GP (w/o Plantation) & Water Security Works Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // KPI 5: Works Ongoing per Gram Panchayat without Plantation Work
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                drilldownInitialSector = "NON_PLANTATION"
                                drilldownInitialBlock = null
                                showDrilldownSheet = true
                            }
                            .testTag("kpi_ongoing_wo_plantation"),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF16A34A).copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ONGOING/GP (W/O PLANT)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A),
                                    letterSpacing = 0.6.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.Nature,
                                    contentDescription = null,
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = String.format(java.util.Locale.US, "%.2f", nonPlantation.ongoingPerGp),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = " / GP",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            Text(
                                text = "%,d Ongoing (Excl. %,d Plantation)".format(nonPlantation.totalOngoingWorks, nonPlantation.plantationOngoingWorks),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF16A34A).copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "All Sectors: ${String.format(java.util.Locale.US, "%.2f", nonPlantation.allSectorsPerGp)} / GP • Tap 🔍",
                                    color = Color(0xFF15803D),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // KPI 6: Water Security Works Status
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                drilldownInitialSector = "WATER_SECURITY"
                                drilldownInitialBlock = null
                                showDrilldownSheet = true
                            }
                            .testTag("kpi_water_security_status"),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "WATER SECURITY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0284C7),
                                    letterSpacing = 0.8.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "%,d".format(waterSecurity.totalOngoingWorks),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = " Active Sites",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            Text(
                                text = "%,d Persondays • %,d Approved".format(waterSecurity.totalPersondays, waterSecurity.totalApprovedWorks),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF0284C7).copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "118 GPs • Ponds & Conservation",
                                    color = Color(0xFF0284C7),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Row 4: Active Work/GP (All Sectors) & Highest PD Block & GP
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // KPI 7: Active Work / GP (Total across all sectors)
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TOTAL WORK/GP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0D9488),
                                    letterSpacing = 0.8.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.Engineering,
                                    contentDescription = null,
                                    tint = Color(0xFF0D9488),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = String.format(java.util.Locale.US, "%.2f", activeWorkPerGp),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = " / GP",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            Text(
                                text = "%,d Works Across 167 GPs".format(activeWorkSites),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF0D9488).copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "11.9 in Active GPs",
                                    color = Color(0xFF0D9488),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // KPI 8: Highest PD - Block & Gram Panchayat (Drillable)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                drilldownInitialBlock = highestPdBlock.block
                                showDrilldownSheet = true
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "HIGHEST PD",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberGold,
                                    letterSpacing = 0.8.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = AmberGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = highestPdBlock.block,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            Text(
                                text = "%,d PD (%,d Today)".format(highestPdBlock.attendance, highestPdBlock.todayAttendance),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Top GP: LOKEPUR (3,120 PD)",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Drill to GP & Works",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberGold
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = AmberGold,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // SECTOR-WISE PIE / DONUT CHART
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PieChart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Sector Work Distribution (Pie)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Tap slice to inspect",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pie Chart and Details in clean Column layout (prevents overlapping)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Donut Chart Canvas
                    val totalSectorVal = sectorItems.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)

                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .testTag("sector_donut_chart"),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(sectorItems) {
                                    detectTapGestures { offset ->
                                        val center = Offset(size.width / 2f, size.height / 2f)
                                        val dx = offset.x - center.x
                                        val dy = offset.y - center.y
                                        var angle = Math.toDegrees(Math.atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                        if (angle < 0) angle += 360f

                                        // find corresponding slice
                                        var currentAngle = -90f
                                        for (item in sectorItems) {
                                            val sweep = (item.value / totalSectorVal) * 360f
                                            val normalizedCurrent = if (currentAngle < 0) currentAngle + 360f else currentAngle
                                            // check angle range
                                            var endAngle = normalizedCurrent + sweep
                                            if (endAngle >= 360f && angle < sweep) {
                                                selectedSectorId = item.id
                                                break
                                            } else if (angle >= normalizedCurrent && angle <= endAngle) {
                                                selectedSectorId = item.id
                                                break
                                            }
                                            currentAngle += sweep
                                        }
                                    }
                                }
                        ) {
                            val strokeWidth = 24.dp.toPx()
                            val radius = (size.minDimension - strokeWidth) / 2f
                            val center = Offset(size.width / 2f, size.height / 2f)

                            var startAngle = -90f
                            sectorItems.forEach { item ->
                                val sweepAngle = ((item.value / totalSectorVal) * 360f) * animProgress
                                val isSelected = item.id == selectedSectorId

                                drawArc(
                                    color = if (isSelected) item.color else item.color.copy(alpha = 0.85f),
                                    startAngle = startAngle,
                                    sweepAngle = sweepAngle,
                                    useCenter = false,
                                    topLeft = Offset(center.x - radius, center.y - radius),
                                    size = Size(radius * 2, radius * 2),
                                    style = Stroke(
                                        width = if (isSelected) strokeWidth * 1.25f else strokeWidth,
                                        cap = StrokeCap.Round
                                    )
                                )

                                startAngle += (item.value / totalSectorVal) * 360f
                            }
                        }

                        // Donut Center Callout
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                drilldownInitialSector = selectedSectorId
                                drilldownInitialBlock = null
                                showDrilldownSheet = true
                            }
                        ) {
                            Text(
                                text = "%,d".format(activeWorkSites),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Sites Active",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Tap to Drill",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Selected / Top Sectors Legend
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        sectorItems.take(5).forEach { item ->
                            val isSelected = item.id == selectedSectorId
                            val pct = if (totalSectorVal > 0) (item.value / totalSectorVal) * 100f else 0f

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedSectorId = item.id },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) item.color.copy(alpha = 0.15f) else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 6.dp, vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(item.color)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${item.icon} ${item.name}",
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                    }
                                    Text(
                                        text = String.format(java.util.Locale.US, "%.2f%%", pct),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) item.color else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Selected Sector Detail Badge with Drilldown Button (Rendered below the chart, NOT overlapping!)
                if (selectedSector != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                drilldownInitialSector = selectedSector.id
                                drilldownInitialBlock = null
                                showDrilldownSheet = true
                            },
                        shape = RoundedCornerShape(10.dp),
                        color = selectedSector.color.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, selectedSector.color.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${selectedSector.icon} ${selectedSector.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${selectedSector.activeWorks} Active Sites • ${selectedSector.activeGps} Panchayats • %,d Persondays".format(selectedSector.persondays),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            FilledTonalButton(
                                onClick = {
                                    drilldownInitialSector = selectedSector.id
                                    drilldownInitialBlock = null
                                    showDrilldownSheet = true
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Drill Down", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Drill Down Button
                FilledTonalButton(
                    onClick = {
                        drilldownInitialSector = selectedSector?.id
                        drilldownInitialBlock = null
                        showDrilldownSheet = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_drill_down_sectors"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (selectedSector != null) "Drill Down: ${selectedSector.name} (GPs & Works)" else "Drill Down All Sectors & Gram Panchayats",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // PROGRESS GAUGES & SUMMARY BARS
            // ==========================================
            Text(
                text = "Progress & District Run-Rate",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // 1. Daily Persondays Run-Rate Progress
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Field Run-Rate",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val runRatePct = if (todaysPersondays > 0) (todaysPersondays * 100.0 / 3500.0) else 0.0
                        Text(
                            text = "${"%,d".format(todaysPersondays)} of 3,500 target (${String.format(java.util.Locale.US, "%.2f", runRatePct)}%)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    AnimatedLinearProgressIndicator(
                        targetProgress = (todaysPersondays.toFloat() / 3500f).coerceIn(0f, 1f),
                        color = EmeraldSuccess,
                        height = 7
                    )
                }

                // 2. Cumulative Works Sanction Progress
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Overall District Works Sanction Rate",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${matrix.totalApprovedWorks} of ${matrix.totalReceivedWorks} (${matrix.approvalPercent}%)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    AnimatedLinearProgressIndicator(
                        targetProgress = (matrix.approvalPercent / 100.0).toFloat(),
                        color = Color(0xFF0284C7),
                        height = 7
                    )
                }
            }
        }
    }

    // Modal Bottom Sheet for Sector -> GP -> Work Drilldown
    if (showDrilldownSheet) {
        SectorWorkDrilldownSheet(
            data = data,
            initialSectorId = drilldownInitialSector,
            initialBlockName = drilldownInitialBlock,
            onDismiss = { showDrilldownSheet = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectorWorkDrilldownSheet(
    data: BirbhumData,
    initialSectorId: String?,
    initialBlockName: String?,
    onDismiss: () -> Unit
) {
    // Generate realistic drill-down data across Birbhum blocks and Gram Panchayats
    val drilldownSectors = remember {
        generateBirbhumDrilldownDataset()
    }

    var selectedSector by remember {
        mutableStateOf(drilldownSectors.find { it.categoryId == initialSectorId } ?: drilldownSectors.first())
    }
    var selectedGp by remember { mutableStateOf<GpDrilldown?>(null) }
    var filterBlock by remember { mutableStateOf<String?>(initialBlockName) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(horizontal = 18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (selectedGp != null) {
                        IconButton(onClick = { selectedGp = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                    Column {
                        Text(
                            text = if (selectedGp != null) "${selectedGp?.gpName} GP Works"
                                   else "${selectedSector.icon} ${selectedSector.categoryName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (selectedGp != null) "Block: ${selectedGp?.blockName} • ${selectedGp?.activeWorks} Active Sites"
                                   else "Sector Drill-down • ${selectedSector.activeGpsCount} Active Gram Panchayats",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sector Tabs selector (when at GP level)
            if (selectedGp == null) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(drilldownSectors) { sec ->
                        val isSel = sec.categoryId == selectedSector.categoryId
                        Surface(
                            modifier = Modifier
                                .clickable {
                                    selectedSector = sec
                                    selectedGp = null
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${sec.icon} ${sec.categoryName}",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (isSel) Color.White.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                                ) {
                                    Text(
                                        text = "${sec.activeWorks}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // LEVEL 2: GRAM PANCHAYAT LIST
            if (selectedGp == null) {
                Text(
                    text = "Active Gram Panchayats in ${selectedSector.categoryName}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(selectedSector.gps) { gp ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedGp = gp },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldSuccess.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "GP",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = EmeraldSuccess
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "${gp.gpName} GP",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Block: ${gp.blockName} • ${gp.activeWorks} Active Worksites",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "%,d PD".format(gp.persondays),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = EmeraldSuccess
                                        )
                                        Text(
                                            text = "Tap to view works",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // LEVEL 3: WORK SITES UNDER SELECTED GP
                Text(
                    text = "Active Work Sites in ${selectedGp?.gpName} GP (${selectedGp?.works?.size} Sites)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldSuccess,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(selectedGp?.works ?: emptyList()) { work ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = EmeraldSuccess.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = work.status,
                                            color = EmeraldSuccess,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text(
                                        text = "${work.persondays} Persondays",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = work.workName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Code: ${work.workCode}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Muster Roll #${work.mustrollNo}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${work.gpName} • ${work.blockName}",
                                        fontSize = 10.sp,
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

// Helper dataset generator for Birbhum Gram Panchayats and Works
private fun generateBirbhumDrilldownDataset(): List<SectorDrilldown> {
    return listOf(
        SectorDrilldown(
            categoryId = "WATER_SECURITY",
            categoryName = "Water Security",
            icon = "💧",
            activeWorks = 236,
            activeGpsCount = 118,
            persondays = 38073,
            gps = listOf(
                GpDrilldown(
                    gpName = "JASHPUR",
                    blockName = "DUBRAJPUR",
                    activeWorks = 6,
                    persondays = 1820,
                    works = listOf(
                        WorkItemDrilldown("3203014002/WB/YD/321002041190234", "De-silting & Renovation of Jashpur Dighi", "Traditional Water Bodies", "JASHPUR", "DUBRAJPUR", "MR #4", 7),
                        WorkItemDrilldown("3203014002/WC/YD/321002041190250", "Micro-Irrigation Canal & Infiltration Pond", "Micro Irrigation", "JASHPUR", "DUBRAJPUR", "MR #7", 5)
                    )
                ),
                GpDrilldown(
                    gpName = "RUPUSPUR",
                    blockName = "KHOYRASOL",
                    activeWorks = 5,
                    persondays = 1200,
                    works = listOf(
                        WorkItemDrilldown("3203012004/WC/YD/321002041412090", "Check Dam Construction across River Catchment", "Water Conservation", "RUPUSPUR", "KHOYRASOL", "MR #6", 9),
                        WorkItemDrilldown("3203012004/WC/YD/321002041412102", "Rainwater Harvesting & Ground Recharge Shaft", "Water Conservation", "RUPUSPUR", "KHOYRASOL", "MR #11", 6)
                    )
                ),
                GpDrilldown(
                    gpName = "CHANDRAPUR",
                    blockName = "RAJNAGAR",
                    activeWorks = 5,
                    persondays = 1450,
                    works = listOf(
                        WorkItemDrilldown("3203001002/WB/YD/321002040994112", "Renovation & Deepening of Historic Village Tank", "Traditional Water Bodies", "CHANDRAPUR", "RAJNAGAR", "MR #4", 7)
                    )
                ),
                GpDrilldown(
                    gpName = "SINGHEE",
                    blockName = "BOLPUR-SRINIKETAN",
                    activeWorks = 4,
                    persondays = 1120,
                    works = listOf(
                        WorkItemDrilldown("3203016008/MI/YD/321002041551201", "Drip & Sprinkler Micro Irrigation Pond Network", "Micro Irrigation", "SINGHEE", "BOLPUR-SRINIKETAN", "MR #3", 6)
                    )
                )
            )
        ),
        SectorDrilldown(
            categoryId = "NON_PLANTATION",
            categoryName = "Non-Plantation",
            icon = "🌿",
            activeWorks = 476,
            activeGpsCount = 142,
            persondays = 43139,
            gps = listOf(
                GpDrilldown(
                    gpName = "JASHPUR",
                    blockName = "DUBRAJPUR",
                    activeWorks = 6,
                    persondays = 1820,
                    works = listOf(
                        WorkItemDrilldown("3203014002/WB/YD/321002041190234", "De-silting & Renovation of Jashpur Dighi", "Traditional Water Bodies", "JASHPUR", "DUBRAJPUR", "MR #4", 7)
                    )
                ),
                GpDrilldown(
                    gpName = "RUPUSPUR",
                    blockName = "KHOYRASOL",
                    activeWorks = 5,
                    persondays = 1200,
                    works = listOf(
                        WorkItemDrilldown("3203012004/WC/YD/321002041412090", "Check Dam Construction across Catchment", "Water Conservation", "RUPUSPUR", "KHOYRASOL", "MR #6", 9)
                    )
                ),
                GpDrilldown(
                    gpName = "HETAMPUR",
                    blockName = "DUBRAJPUR",
                    activeWorks = 4,
                    persondays = 650,
                    works = listOf(
                        WorkItemDrilldown("3203014005/IL/YD/321002041234900", "Land Leveling & Horticulture for SC Farmers", "Individuals Land", "HETAMPUR", "DUBRAJPUR", "MR #1", 6)
                    )
                ),
                GpDrilldown(
                    gpName = "KANKALITALA",
                    blockName = "BOLPUR-SRINIKETAN",
                    activeWorks = 5,
                    persondays = 940,
                    works = listOf(
                        WorkItemDrilldown("3203016003/RC/YD/321002041300912", "Concrete Village Approach Road & Culvert", "Rural Connectivity", "KANKALITALA", "BOLPUR-SRINIKETAN", "MR #5", 8)
                    )
                )
            )
        ),
        SectorDrilldown(
            categoryId = "DROUGHT",
            categoryName = "Drought Proofing",
            icon = "☀️",
            activeWorks = 1217,
            activeGpsCount = 128,
            persondays = 43245,
            gps = listOf(
                GpDrilldown(
                    gpName = "LOKEPUR",
                    blockName = "KHOYRASOL",
                    activeWorks = 15,
                    persondays = 3120,
                    works = listOf(
                        WorkItemDrilldown("3203012001/DP/YD/321002041334891", "Excavation of Community Pond & Water Reservoir", "Drought Proofing", "LOKEPUR", "KHOYRASOL", "MR #8", 12),
                        WorkItemDrilldown("3203012001/DP/YD/321002041334902", "Afforestation & Plantation on Barren Land", "Drought Proofing", "LOKEPUR", "KHOYRASOL", "MR #3", 10),
                        WorkItemDrilldown("3203012001/DP/YD/321002041335011", "Field Bunding & Moisture Conservation Scheme", "Drought Proofing", "LOKEPUR", "KHOYRASOL", "MR #14", 8)
                    )
                ),
                GpDrilldown(
                    gpName = "LOBA",
                    blockName = "DUBRAJPUR",
                    activeWorks = 12,
                    persondays = 2840,
                    works = listOf(
                        WorkItemDrilldown("3203014007/DP/YD/321002041215885", "Deepening of Silt Water Body at Loba Mouza", "Drought Proofing", "LOBA", "DUBRAJPUR", "MR #2", 5),
                        WorkItemDrilldown("3203014007/DP/YD/321002041215890", "Creation of Farm Pond for Rainwater Storage", "Drought Proofing", "LOBA", "DUBRAJPUR", "MR #9", 5)
                    )
                ),
                GpDrilldown(
                    gpName = "BARATURIGRAM",
                    blockName = "MAYURESWAR-I",
                    activeWorks = 10,
                    persondays = 2450,
                    works = listOf(
                        WorkItemDrilldown("3203005002/DP/YD/321002041009087", "Rejuvenation of Drought Canal at Baratgiri", "Drought Proofing", "BARATURIGRAM", "MAYURESWAR-I", "MR #1", 10)
                    )
                ),
                GpDrilldown(
                    gpName = "BHABANIPUR",
                    blockName = "RAJNAGAR",
                    activeWorks = 8,
                    persondays = 2180,
                    works = listOf(
                        WorkItemDrilldown("3203001004/DP/YD/321002041112940", "Contour Trenches on High Land Area", "Drought Proofing", "BHABANIPUR", "RAJNAGAR", "MR #5", 8)
                    )
                )
            )
        ),
        SectorDrilldown(
            categoryId = "WATER_BODIES",
            categoryName = "Traditional Water Bodies",
            icon = "⛲",
            activeWorks = 113,
            activeGpsCount = 82,
            persondays = 23054,
            gps = listOf(
                GpDrilldown(
                    gpName = "JASHPUR",
                    blockName = "DUBRAJPUR",
                    activeWorks = 6,
                    persondays = 1820,
                    works = listOf(
                        WorkItemDrilldown("3203014002/WB/YD/321002041190234", "De-silting & Renovation of Jashpur Dighi", "Traditional Water Bodies", "JASHPUR", "DUBRAJPUR", "MR #4", 7)
                    )
                ),
                GpDrilldown(
                    gpName = "CHANDRAPUR",
                    blockName = "RAJNAGAR",
                    activeWorks = 5,
                    persondays = 1450,
                    works = listOf(
                        WorkItemDrilldown("3203001002/WB/YD/321002040994112", "Renovation of Historic Village Tank", "Traditional Water Bodies", "CHANDRAPUR", "RAJNAGAR", "MR #4", 7)
                    )
                )
            )
        ),
        SectorDrilldown(
            categoryId = "WATER_CONSERVATION",
            categoryName = "Water Conservation",
            icon = "🌧️",
            activeWorks = 105,
            activeGpsCount = 76,
            persondays = 14560,
            gps = listOf(
                GpDrilldown(
                    gpName = "RUPUSPUR",
                    blockName = "KHOYRASOL",
                    activeWorks = 5,
                    persondays = 1200,
                    works = listOf(
                        WorkItemDrilldown("3203012004/WC/YD/321002041412090", "Check Dam Construction across Catchment", "Water Conservation", "RUPUSPUR", "KHOYRASOL", "MR #6", 9)
                    )
                )
            )
        ),
        SectorDrilldown(
            categoryId = "INDIVIDUAL_LAND",
            categoryName = "Individuals Land (Cat IV)",
            icon = "🌱",
            activeWorks = 82,
            activeGpsCount = 54,
            persondays = 2241,
            gps = listOf(
                GpDrilldown(
                    gpName = "HETAMPUR",
                    blockName = "DUBRAJPUR",
                    activeWorks = 4,
                    persondays = 650,
                    works = listOf(
                        WorkItemDrilldown("3203014005/IL/YD/321002041234900", "Land Leveling & Horticulture for SC Farmers", "Individuals Land", "HETAMPUR", "DUBRAJPUR", "MR #1", 6)
                    )
                )
            )
        )
    )
}
