package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BirbhumData
import com.example.data.models.DailyPersondayRecord
import com.example.data.models.DateBlockPersonday
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GovNavyPrimaryLight
import java.util.Calendar
import java.util.Locale

@Composable
fun PdCalendarScreen(
    data: BirbhumData,
    selectedMonth: Int,
    selectedYear: Int,
    selectedDate: DailyPersondayRecord?,
    selectedBlockBreakdown: List<DateBlockPersonday>,
    isLoadingBreakdown: Boolean,
    isSyncing: Boolean,
    onSelectMonth: (month: Int, year: Int) -> Unit,
    onSelectDate: (DailyPersondayRecord) -> Unit,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dailyRecords = data.nmmsSummary.dailyPersondays
    val availableMonths = remember(dailyRecords) {
        val set = mutableSetOf<Pair<Int, Int>>()
        dailyRecords.forEach { set.add(Pair(it.year, it.month)) }
        if (set.isEmpty()) {
            listOf(Pair(2026, 10), Pair(2026, 9), Pair(2026, 8), Pair(2026, 7))
        } else {
            set.sortedWith(compareByDescending<Pair<Int, Int>> { it.first }.thenByDescending { it.second })
        }
    }

    val monthNames = arrayOf("", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    val shortMonthNames = arrayOf("", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    // Filter records for the selected month
    val recordsInMonth = remember(dailyRecords, selectedMonth, selectedYear) {
        dailyRecords.filter { it.year == selectedYear && it.month == selectedMonth }
            .associateBy { it.day }
    }

    // Month metrics
    val totalMonthPd = remember(recordsInMonth) { recordsInMonth.values.sumOf { it.persondays } }
    val totalMonthMusters = remember(recordsInMonth) { recordsInMonth.values.sumOf { it.musters } }
    val activeDaysCount = remember(recordsInMonth) { recordsInMonth.values.count { it.persondays > 0 } }
    val maxDayRecord = remember(recordsInMonth) { recordsInMonth.values.maxByOrNull { it.persondays } }
    val avgPdPerDay = if (activeDaysCount > 0) totalMonthPd / activeDaysCount else 0

    // Calendar grid calculations
    val calendarDays = remember(selectedMonth, selectedYear) {
        buildCalendarMonth(selectedYear, selectedMonth)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("pd_calendar_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Card with Live Attendance & Sheet Status
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pd_calendar_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldSuccess.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "LIVE SYNCED",
                                        color = EmeraldSuccess,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "GID 1613697542 • VB-G RAM G",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Persondays (PD) Calendar",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Daily Attendance & Muster Roll Generation Heatmap",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = onSyncClick,
                            enabled = !isSyncing,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .testTag("btn_calendar_sync")
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Sync Live Sheet",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Month Selector Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val currentIdx = availableMonths.indexOfFirst { it.first == selectedYear && it.second == selectedMonth }
                                if (currentIdx < availableMonths.lastIndex && currentIdx >= 0) {
                                    val prev = availableMonths[currentIdx + 1]
                                    onSelectMonth(prev.second, prev.first)
                                }
                            },
                            enabled = availableMonths.indexOfFirst { it.first == selectedYear && it.second == selectedMonth } < availableMonths.lastIndex
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${monthNames.getOrElse(selectedMonth) { "" }} $selectedYear",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = {
                                val currentIdx = availableMonths.indexOfFirst { it.first == selectedYear && it.second == selectedMonth }
                                if (currentIdx > 0) {
                                    val next = availableMonths[currentIdx - 1]
                                    onSelectMonth(next.second, next.first)
                                }
                            },
                            enabled = availableMonths.indexOfFirst { it.first == selectedYear && it.second == selectedMonth } > 0
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                        }
                    }

                    // Quick Month Pills
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableMonths.forEach { (yr, mo) ->
                            val isSelected = yr == selectedYear && mo == selectedMonth
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectMonth(mo, yr) },
                                label = {
                                    Text(
                                        text = "${shortMonthNames.getOrElse(mo) { "" }} $yr",
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                } else null
                            )
                        }

                        // Jump to Latest Date Button
                        val latestRec = dailyRecords.lastOrNull()
                        if (latestRec != null) {
                            FilledTonalButton(
                                onClick = {
                                    onSelectMonth(latestRec.month, latestRec.year)
                                    onSelectDate(latestRec)
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.Today, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Jump to Latest (${latestRec.displayDate})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 2. Month Overview Metrics Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricSummaryCard(
                    title = "Month Persondays",
                    value = "%,d".format(totalMonthPd),
                    subtitle = "$activeDaysCount active days",
                    icon = Icons.Default.TrendingUp,
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                MetricSummaryCard(
                    title = "Total Musters",
                    value = "%,d".format(totalMonthMusters),
                    subtitle = "Issued in month",
                    icon = Icons.Default.FactCheck,
                    accentColor = Color(0xFF0284C7),
                    modifier = Modifier.weight(1f)
                )

                MetricSummaryCard(
                    title = "Daily Average",
                    value = "%,d".format(avgPdPerDay),
                    subtitle = "PD / active day",
                    icon = Icons.Default.Speed,
                    accentColor = EmeraldSuccess,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. Calendar Grid Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pd_calendar_grid_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Persondays Heatmap",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tap a date to inspect",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Day of Week Header: Mon .. Sun
                    val weekdays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    Row(modifier = Modifier.fillMaxWidth()) {
                        weekdays.forEach { dayName ->
                            Text(
                                text = dayName,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Calendar Weeks
                    calendarDays.chunked(7).forEach { week ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            week.forEach { calDay ->
                                val dayNum = calDay.dayOfMonth
                                val isCurrentMonth = calDay.isCurrentMonth
                                val record = if (isCurrentMonth) recordsInMonth[dayNum] else null
                                val isSelected = isCurrentMonth && selectedDate != null &&
                                        selectedDate.year == selectedYear &&
                                        selectedDate.month == selectedMonth &&
                                        selectedDate.day == dayNum

                                val isTodayOrLatest = isCurrentMonth && (
                                        (record != null && record.dateIso == data.nmmsSummary.latestDateIso) ||
                                                (selectedMonth == 10 && dayNum == 17 && selectedYear == 2026)
                                        )

                                CalendarDayCell(
                                    dayNumber = dayNum,
                                    isCurrentMonth = isCurrentMonth,
                                    record = record,
                                    isSelected = isSelected,
                                    isTodayOrLatest = isTodayOrLatest,
                                    onClick = {
                                        if (isCurrentMonth) {
                                            val targetRecord = record ?: DailyPersondayRecord(
                                                dateIso = String.format(Locale.US, "%04d-%02d-%02d", selectedYear, selectedMonth, dayNum),
                                                displayDate = "$dayNum ${shortMonthNames.getOrElse(selectedMonth) { "" }} $selectedYear",
                                                year = selectedYear,
                                                month = selectedMonth,
                                                day = dayNum,
                                                persondays = 0,
                                                musters = 0
                                            )
                                            onSelectDate(targetRecord)
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Legend
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendItem(color = EmeraldSuccess, label = "≥3,000 PD (Target Met)")
                        LegendItem(color = Color(0xFF0284C7), label = "2,000–2,999 PD")
                        LegendItem(color = Color(0xFFD97706), label = "1,000–1,999 PD")
                        LegendItem(color = Color(0xFF64748B), label = "<1,000 PD")
                    }
                }
            }
        }

        // 4. Selected Date Detail & Block Breakdown Panel
        if (selectedDate != null) {
            item {
                SelectedDateDetailCard(
                    dateRecord = selectedDate,
                    blockBreakdown = selectedBlockBreakdown,
                    isLoadingBreakdown = isLoadingBreakdown,
                    isLatest = selectedDate.dateIso == data.nmmsSummary.latestDateIso
                )
            }
        }

        // 5. Daily Trend Bar Chart across the selected month
        if (recordsInMonth.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pd_calendar_trend_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Daily Trajectory (${shortMonthNames.getOrElse(selectedMonth) { "" }} $selectedYear)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (maxDayRecord != null) {
                                Text(
                                    text = "Peak: ${maxDayRecord.day} ${shortMonthNames.getOrElse(selectedMonth) { "" }} (${maxDayRecord.persondays} PD)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Horizontal scrollable bar chart
                        val maxPd = remember(recordsInMonth) { (recordsInMonth.values.maxOfOrNull { it.persondays } ?: 3500).coerceAtLeast(100) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val daysInMonth = calendarDays.count { it.isCurrentMonth }
                            for (d in 1..daysInMonth) {
                                val rec = recordsInMonth[d]
                                val pd = rec?.persondays ?: 0
                                val barHeightRatio = (pd.toFloat() / maxPd.toFloat()).coerceIn(0.04f, 1f)
                                val isSelected = selectedDate != null && selectedDate.day == d && selectedDate.month == selectedMonth

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clickable {
                                            val target = rec ?: DailyPersondayRecord(
                                                dateIso = String.format(Locale.US, "%04d-%02d-%02d", selectedYear, selectedMonth, d),
                                                displayDate = "$d ${shortMonthNames.getOrElse(selectedMonth) { "" }} $selectedYear",
                                                year = selectedYear,
                                                month = selectedMonth,
                                                day = d,
                                                persondays = 0,
                                                musters = 0
                                            )
                                            onSelectDate(target)
                                        }
                                        .padding(horizontal = 2.dp)
                                ) {
                                    if (pd > 0) {
                                        Text(
                                            text = if (pd >= 1000) "${pd / 1000}k" else "$pd",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                    }

                                    Box(
                                        modifier = Modifier
                                            .width(18.dp)
                                            .height((barHeightRatio * 80).dp)
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(
                                                when {
                                                    isSelected -> MaterialTheme.colorScheme.primary
                                                    pd >= 3000 -> EmeraldSuccess
                                                    pd >= 2000 -> Color(0xFF0284C7)
                                                    pd >= 1000 -> Color(0xFFD97706)
                                                    pd > 0 -> Color(0xFF64748B)
                                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                                }
                                            )
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$d",
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
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

@Composable
fun CalendarDayCell(
    dayNumber: Int,
    isCurrentMonth: Boolean,
    record: DailyPersondayRecord?,
    isSelected: Boolean,
    isTodayOrLatest: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pd = record?.persondays ?: 0
    val hasData = pd > 0

    val bgColor = when {
        !isCurrentMonth -> MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        pd >= 3000 -> EmeraldSuccess.copy(alpha = 0.15f)
        pd >= 2000 -> Color(0xFF0284C7).copy(alpha = 0.15f)
        pd >= 1000 -> Color(0xFFD97706).copy(alpha = 0.15f)
        pd > 0 -> Color(0xFF64748B).copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    }

    val badgeColor = when {
        pd >= 3000 -> EmeraldSuccess
        pd >= 2000 -> Color(0xFF0284C7)
        pd >= 1000 -> Color(0xFFD97706)
        pd > 0 -> Color(0xFF64748B)
        else -> Color.Transparent
    }

    Box(
        modifier = modifier
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                } else if (isTodayOrLatest) {
                    Modifier.border(1.5.dp, EmeraldSuccess, RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            )
            .clickable(enabled = isCurrentMonth, onClick = onClick)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Day Number
            Text(
                text = if (isCurrentMonth) "$dayNumber" else "",
                fontSize = 12.sp,
                fontWeight = if (isSelected || isTodayOrLatest) FontWeight.ExtraBold else FontWeight.Bold,
                color = when {
                    !isCurrentMonth -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                    isTodayOrLatest -> EmeraldSuccess
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )

            // Persondays Badge / text
            if (isCurrentMonth && hasData) {
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    shape = RoundedCornerShape(3.dp),
                    color = badgeColor
                ) {
                    Text(
                        text = if (pd >= 1000) "${pd / 1000}k" else "$pd",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                    )
                }
            } else if (isCurrentMonth && isTodayOrLatest && !hasData) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(EmeraldSuccess)
                )
            }
        }
    }
}

@Composable
fun SelectedDateDetailCard(
    dateRecord: DailyPersondayRecord,
    blockBreakdown: List<DateBlockPersonday>,
    isLoadingBreakdown: Boolean,
    isLatest: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("selected_date_detail_card"),
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
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = dateRecord.displayDate,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isLatest) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EmeraldSuccess.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "TODAY / LATEST",
                                    color = EmeraldSuccess,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "Daily Persondays & Block Deployment Breakdown",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EventAvailable,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Key Metrics of the Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DateStatBox(
                    label = "Persondays",
                    value = "%,d".format(dateRecord.persondays),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                DateStatBox(
                    label = "Musters Issued",
                    value = "%,d".format(dateRecord.musters),
                    color = Color(0xFF0284C7),
                    modifier = Modifier.weight(1f)
                )

                val avgTurnout = if (dateRecord.musters > 0) dateRecord.persondays.toDouble() / dateRecord.musters else 0.0
                DateStatBox(
                    label = "Avg Workers/Muster",
                    value = String.format(Locale.US, "%.2f", avgTurnout),
                    color = EmeraldSuccess,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Target Run-Rate (Daily Target: 3,500 PD)
            val dailyTarget = 3500
            val targetPct = if (dailyTarget > 0) (dateRecord.persondays * 100f / dailyTarget) else 0f
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily District Target Progress",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${"%,d".format(dateRecord.persondays)} / 3,500 target (${String.format(Locale.US, "%.2f", targetPct)}%)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (targetPct >= 100f) EmeraldSuccess else MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { (targetPct / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (targetPct >= 100f) EmeraldSuccess else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Block Breakdown Table / List
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Block-wise Contribution (${blockBreakdown.size} Blocks Reporting)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (isLoadingBreakdown) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (blockBreakdown.isNotEmpty()) {
                val maxBlockPd = (blockBreakdown.maxOfOrNull { it.persondays } ?: 1).coerceAtLeast(1)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    blockBreakdown.forEachIndexed { index, block ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (index < 3) EmeraldSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${index + 1}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (index < 3) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = block.blockName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${block.musters} musters",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${"%,d".format(block.persondays)} PD",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                // Mini contribution bar
                                val share = block.persondays.toFloat() / maxBlockPd.toFloat()
                                Box(
                                    modifier = Modifier
                                        .width(60.dp)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(fraction = share.coerceIn(0.05f, 1f))
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (!isLoadingBreakdown) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (dateRecord.persondays == 0) "No person-days recorded on this date (Holiday/Rest Day)" else "Block-wise attendance data sync in progress...",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(14.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun MetricSummaryCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = subtitle, fontSize = 9.sp, color = accentColor, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DateStatBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.08f)
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

data class CalendarDay(
    val dayOfMonth: Int,
    val isCurrentMonth: Boolean
)

fun buildCalendarMonth(year: Int, month: Int): List<CalendarDay> {
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.MONTH, month - 1)
    cal.set(Calendar.DAY_OF_MONTH, 1)

    // Calendar day of week: Sunday=1, Monday=2 ... Saturday=7
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    // Convert to Monday=0 .. Sunday=6
    val leadDays = (firstDayOfWeek - Calendar.MONDAY + 7) % 7
    val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val list = mutableListOf<CalendarDay>()
    // Leading days from previous month
    for (i in 0 until leadDays) {
        list.add(CalendarDay(dayOfMonth = 0, isCurrentMonth = false))
    }
    // Days in current month
    for (d in 1..maxDays) {
        list.add(CalendarDay(dayOfMonth = d, isCurrentMonth = true))
    }
    // Trailing days to fill last week
    val remainder = list.size % 7
    if (remainder != 0) {
        for (i in 0 until (7 - remainder)) {
            list.add(CalendarDay(dayOfMonth = 0, isCurrentMonth = false))
        }
    }
    return list
}
