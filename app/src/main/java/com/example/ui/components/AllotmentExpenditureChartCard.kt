package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BirbhumData
import com.example.data.models.BlockData
import com.example.data.models.formatFund
import com.example.ui.theme.*

enum class ChartMetricMode(val label: String, val shortLabel: String) {
    BOOKED_EXP("Allotment vs. Booked Exp", "Booked"),
    PAID_EXP("Allotment vs. Paid Disbursement", "Paid"),
    PERSONDAYS("Persondays Generated", "Persondays")
}

enum class ChartUnitMode(val label: String) {
    LAKH("₹ Lakh"),
    CRORE("₹ Crore")
}

enum class ChartSortMode(val label: String) {
    ALLOTMENT_DESC("Allotment (High to Low)"),
    EXPENDITURE_DESC("Expenditure (High to Low)"),
    UTILIZATION_DESC("Utilization % (High to Low)"),
    PERSONDAYS_DESC("Persondays (High to Low)"),
    ALPHABETICAL("Block Name (A-Z)")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllotmentExpenditureChartCard(
    data: BirbhumData,
    modifier: Modifier = Modifier,
    initialMetric: ChartMetricMode = ChartMetricMode.BOOKED_EXP,
    onSelectBlock: (BlockData) -> Unit = {}
) {
    var metricMode by remember { mutableStateOf(initialMetric) }
    var unitMode by remember { mutableStateOf(ChartUnitMode.LAKH) }
    var sortMode by remember { mutableStateOf(ChartSortMode.ALLOTMENT_DESC) }
    var localSearchQuery by remember { mutableStateOf("") }
    var expandedBlockName by remember { mutableStateOf<String?>(null) }

    val allBlocks = data.blocks
    val allotmentSummary = data.allotmentSummary

    // District wide metrics
    val totalAllotmentLakh = allotmentSummary.distributableLakh
    val totalSanctionLakh = data.totalSanctionLakh
    val totalBookedExpLakh = data.totalBookedExpLakh
    val totalPaidExpLakh = data.totalPaidExpLakh
    val totalPersondays = data.totalExpenditurePersondays
    val overallUtilizationPct = data.overallFinancialUtilizationPct

    // Filtering and sorting
    val filteredBlocks = remember(allBlocks, localSearchQuery, sortMode) {
        val q = localSearchQuery.trim().lowercase()
        val base = if (q.isEmpty()) allBlocks else allBlocks.filter { it.blockName.lowercase().contains(q) }
        when (sortMode) {
            ChartSortMode.ALLOTMENT_DESC -> base.sortedByDescending { it.allotment.totalLakh }
            ChartSortMode.EXPENDITURE_DESC -> base.sortedByDescending { it.expenditure.bookedExpenditureLakh }
            ChartSortMode.UTILIZATION_DESC -> base.sortedByDescending { it.expenditure.utilizationPct(it.allotment.totalLakh) }
            ChartSortMode.PERSONDAYS_DESC -> base.sortedByDescending { it.expenditure.persondaysGenerated }
            ChartSortMode.ALPHABETICAL -> base.sortedBy { it.blockName }
        }
    }

    // Maximum values for relative bar widths
    val maxAllotmentLakh = remember(allBlocks) { allBlocks.maxOfOrNull { it.allotment.totalLakh } ?: 1.0 }
    val maxPersondays = remember(allBlocks) { allBlocks.maxOfOrNull { it.expenditure.persondaysGenerated } ?: 1 }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("allotment_expenditure_chart_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Visual Chart",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Allotment vs. Actual Expenditure",
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
                                    text = "LIVE SCHEME BANK",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldSuccess,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "State Allotment (GID 263874047) vs. Actual Expenditure & Persondays (GID 6823442)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                // Unit Toggle Chip
                FilledTonalButton(
                    onClick = {
                        unitMode = if (unitMode == ChartUnitMode.LAKH) ChartUnitMode.CRORE else ChartUnitMode.LAKH
                    },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("chart_unit_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CurrencyRupee,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unitMode.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // District Financial Status Summary Ribbon
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "DISTRICT FINANCIAL STATUS (BIRBHUM)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FinancialSummaryMetric(
                            label = "State Allotment",
                            value = formatAmountByUnit(totalAllotmentLakh, unitMode),
                            color = MaterialTheme.colorScheme.primary
                        )
                        FinancialSummaryMetric(
                            label = "Booked Exp",
                            value = formatAmountByUnit(totalBookedExpLakh, unitMode),
                            color = CoralWarning
                        )
                        FinancialSummaryMetric(
                            label = "Paid Out",
                            value = formatAmountByUnit(totalPaidExpLakh, unitMode),
                            color = EmeraldSuccess
                        )
                        FinancialSummaryMetric(
                            label = "Utilization",
                            value = "${overallUtilizationPct.formatFund()}%",
                            color = if (overallUtilizationPct >= 5.0) EmeraldSuccess else CoralWarning
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Total Persondays Generated: ",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = String.format("%,d PD", totalPersondays),
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "Sanction: ${formatAmountByUnit(totalSanctionLakh, unitMode)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metric Mode Selector Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ChartMetricMode.values().forEach { mode ->
                    val isSelected = mode == metricMode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                            .clickable { metricMode = mode }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.shortLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar & Sort Row
            OutlinedTextField(
                value = localSearchQuery,
                onValueChange = { localSearchQuery = it },
                placeholder = { Text("Filter block in chart (e.g. Dubrajpur, Bolpur)...", fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (localSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { localSearchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chart_block_search_input"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Sort Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ChartSortMode.values()) { sortOpt ->
                    val isSelected = sortOpt == sortMode
                    FilterChip(
                        selected = isSelected,
                        onClick = { sortMode = sortOpt },
                        label = {
                            Text(
                                text = sortOpt.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chart Legend
            ChartLegendRow(metricMode = metricMode)

            Spacer(modifier = Modifier.height(12.dp))

            // Blocks Progress Chart List
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                filteredBlocks.forEachIndexed { index, block ->
                    BlockProgressChartRow(
                        rank = index + 1,
                        block = block,
                        metricMode = metricMode,
                        unitMode = unitMode,
                        maxAllotmentLakh = maxAllotmentLakh,
                        maxPersondays = maxPersondays,
                        isExpanded = expandedBlockName == block.blockName,
                        onToggleExpand = {
                            expandedBlockName = if (expandedBlockName == block.blockName) null else block.blockName
                        },
                        onSelectBlock = { onSelectBlock(block) }
                    )
                }
            }
        }
    }
}

@Composable
fun BlockProgressChartRow(
    rank: Int,
    block: BlockData,
    metricMode: ChartMetricMode,
    unitMode: ChartUnitMode,
    maxAllotmentLakh: Double,
    maxPersondays: Int,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onSelectBlock: () -> Unit
) {
    val allotLakh = block.allotment.totalLakh
    val exp = block.expenditure
    val bookedLakh = exp.bookedExpenditureLakh
    val paidLakh = exp.paidExpenditureLakh
    val persondays = exp.persondaysGenerated
    val utilizationPct = exp.utilizationPct(allotLakh)
    val remainingLakh = exp.remainingLakh(allotLakh)

    // Animated bar fraction calculations
    val allotmentFraction = remember(allotLakh, maxAllotmentLakh) {
        if (maxAllotmentLakh > 0) (allotLakh / maxAllotmentLakh).toFloat().coerceIn(0.05f, 1f) else 0.1f
    }
    val animatedAllotmentFraction by animateFloatAsState(
        targetValue = allotmentFraction,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "allotment_bar"
    )

    val expFraction = remember(bookedLakh, allotLakh) {
        if (allotLakh > 0) (bookedLakh / allotLakh).toFloat().coerceIn(0.02f, 1f) else 0.05f
    }
    val animatedExpFraction by animateFloatAsState(
        targetValue = expFraction,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "exp_bar"
    )

    val paidFraction = remember(paidLakh, allotLakh) {
        if (allotLakh > 0) (paidLakh / allotLakh).toFloat().coerceIn(0.01f, 1f) else 0.03f
    }
    val animatedPaidFraction by animateFloatAsState(
        targetValue = paidFraction,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "paid_bar"
    )

    val pdFraction = remember(persondays, maxPersondays) {
        if (maxPersondays > 0) (persondays.toFloat() / maxPersondays).coerceIn(0.05f, 1f) else 0.1f
    }
    val animatedPdFraction by animateFloatAsState(
        targetValue = pdFraction,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "pd_bar"
    )

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = if (isExpanded) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() }
            .testTag("block_chart_item_${block.blockName}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Rank + Block Name + Utilization Badge + Persondays Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$rank",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = block.blockName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Persondays pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PurpleMetric.copy(alpha = 0.12f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Engineering,
                                contentDescription = null,
                                tint = PurpleMetric,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${String.format("%,d", persondays)} PD",
                                color = PurpleMetric,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Utilization % badge
                    val badgeColor = when {
                        utilizationPct >= 7.0 -> EmeraldSuccess
                        utilizationPct >= 4.0 -> CoralWarning
                        else -> MaterialTheme.colorScheme.error
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = badgeColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${utilizationPct.formatFund()}% Spent",
                            color = badgeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Visual Progress Bars depending on metric mode
            when (metricMode) {
                ChartMetricMode.BOOKED_EXP -> {
                    // Comparative dual bar: Allotment (Navy track) vs Booked Exp (Warm Amber fill)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Allotment Track Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Allotment: ${formatAmountByUnit(allotLakh, unitMode)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Rem: ${formatAmountByUnit(remainingLakh, unitMode)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedAllotmentFraction)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                            )
                                        )
                                    )
                            )
                        }

                        // Actual Booked Expenditure Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Booked Exp: ${formatAmountByUnit(bookedLakh, unitMode)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CoralWarning
                            )
                            Text(
                                text = "Sanction: ${formatAmountByUnit(exp.sanctionAmountLakh, unitMode)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedAllotmentFraction * animatedExpFraction)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(CoralWarning, CoralWarning.copy(alpha = 0.75f))
                                        )
                                    )
                            )
                        }
                    }
                }

                ChartMetricMode.PAID_EXP -> {
                    // Comparative bar for Paid Out Disbursement
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Allotment: ${formatAmountByUnit(allotLakh, unitMode)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Paid: ${formatAmountByUnit(paidLakh, unitMode)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        ) {
                            // Allotment backdrop
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedAllotmentFraction)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                            )
                            // Paid overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedAllotmentFraction * animatedPaidFraction)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(EmeraldSuccess, EmeraldSuccess.copy(alpha = 0.8f))
                                        )
                                    )
                            )
                        }
                    }
                }

                ChartMetricMode.PERSONDAYS -> {
                    // Persondays Bar Chart
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Generated: ${String.format("%,d", persondays)} Mandays",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PurpleMetric
                            )
                            Text(
                                text = "Wages: ${formatAmountByUnit(exp.wagesBookedLakh, unitMode)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedPdFraction)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(PurpleMetric, CyanMetric)
                                        )
                                    )
                            )
                        }
                    }
                }
            }

            // Expandable Detailed Breakdown Section
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "SCHEME BANK FINANCIAL AUDIT BREAKDOWN",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailMetricColumn(
                            title = "Wages Booked",
                            value = formatAmountByUnit(exp.wagesBookedLakh, unitMode),
                            color = CoralWarning
                        )
                        DetailMetricColumn(
                            title = "Material Booked",
                            value = formatAmountByUnit(exp.materialBookedLakh, unitMode),
                            color = CoralWarning
                        )
                        DetailMetricColumn(
                            title = "Wages Paid",
                            value = formatAmountByUnit(exp.wagesPaidLakh, unitMode),
                            color = EmeraldSuccess
                        )
                        DetailMetricColumn(
                            title = "Material Paid",
                            value = formatAmountByUnit(exp.materialPaidLakh, unitMode),
                            color = EmeraldSuccess
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = onSelectBlock,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Launch,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Block Dossier", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChartLegendRow(metricMode: ChartMetricMode) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (metricMode) {
            ChartMetricMode.BOOKED_EXP -> {
                LegendItem(color = MaterialTheme.colorScheme.primary, label = "State Allotment")
                LegendItem(color = CoralWarning, label = "Booked Expenditure")
                LegendItem(color = PurpleMetric, label = "Persondays (PD)")
            }
            ChartMetricMode.PAID_EXP -> {
                LegendItem(color = MaterialTheme.colorScheme.primary, label = "State Allotment")
                LegendItem(color = EmeraldSuccess, label = "Actual Paid Disbursement")
                LegendItem(color = PurpleMetric, label = "Persondays")
            }
            ChartMetricMode.PERSONDAYS -> {
                LegendItem(color = PurpleMetric, label = "Mandays Generated")
                LegendItem(color = CyanMetric, label = "Turnout Velocity")
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun FinancialSummaryMetric(label: String, value: String, color: Color) {
    Column {
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}

@Composable
fun DetailMetricColumn(title: String, value: String, color: Color) {
    Column {
        Text(
            text = title,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

private fun formatAmountByUnit(lakh: Double, unitMode: ChartUnitMode): String {
    return when (unitMode) {
        ChartUnitMode.LAKH -> "₹${lakh.formatFund()} L"
        ChartUnitMode.CRORE -> "₹${(lakh / 100.0).formatFund()} Cr"
    }
}
