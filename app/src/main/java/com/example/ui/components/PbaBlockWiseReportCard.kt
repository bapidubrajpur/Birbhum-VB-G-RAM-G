package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.models.BlockData
import com.example.data.models.PbaBlockReport
import com.example.data.models.PbaSummary
import com.example.data.models.getBlockGpPerformance
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary

enum class PbaSortField {
    NAME,
    TABLE_TOP,
    VERIFIED_OK,
    WORK_CODES,
    EMR_REGISTER,
    GP_COUNT
}

enum class PbaViewMode {
    TABLE,
    CARDS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PbaBlockWiseReportCard(
    pbaSummary: PbaSummary,
    blocks: List<BlockData>,
    onSelectBlock: (BlockData) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var sortField by remember { mutableStateOf(PbaSortField.VERIFIED_OK) }
    var sortAscending by remember { mutableStateOf(false) }
    var viewMode by remember { mutableStateOf(PbaViewMode.TABLE) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedReportForGpStatus by remember { mutableStateOf<PbaBlockReport?>(null) }

    // Combine pba reports from summary or blocks
    val rawList = if (pbaSummary.blocks.isNotEmpty()) {
        pbaSummary.blocks
    } else {
        blocks.mapNotNull { it.pba }
    }

    // Filter by search: matches block name, GP name (best/poor), or status
    val filteredList = rawList.filter { item ->
        if (searchQuery.isBlank()) return@filter true
        val q = searchQuery.trim()
        if (item.blockName.contains(q, ignoreCase = true)) return@filter true
        val alert = getBlockGpPerformance(item.blockName)
        if (alert.bestGp.contains(q, ignoreCase = true)) return@filter true
        if (alert.poorGp.contains(q, ignoreCase = true)) return@filter true
        if (alert.bestGpStats.contains(q, ignoreCase = true)) return@filter true
        if (alert.poorGpStats.contains(q, ignoreCase = true)) return@filter true
        if (alert.sectorWorks.contains(q, ignoreCase = true)) return@filter true
        val matched = blocks.find { it.blockName.equals(item.blockName, ignoreCase = true) }
        if (matched != null && "${matched.works.gps} GP".contains(q, ignoreCase = true)) return@filter true
        false
    }

    // Sort list
    val sortedList = remember(filteredList, sortField, sortAscending, blocks) {
        val sorted = when (sortField) {
            PbaSortField.NAME -> filteredList.sortedBy { it.blockName }
            PbaSortField.TABLE_TOP -> filteredList.sortedBy { it.tableTopDone }
            PbaSortField.VERIFIED_OK -> filteredList.sortedBy { it.verifiedOk }
            PbaSortField.WORK_CODES -> filteredList.sortedBy { it.availableWorkCodes }
            PbaSortField.EMR_REGISTER -> filteredList.sortedBy { it.emrIssueRegister }
            PbaSortField.GP_COUNT -> filteredList.sortedBy { report ->
                blocks.find { it.blockName.equals(report.blockName, ignoreCase = true) }?.works?.gps ?: 10
            }
        }
        if (sortAscending) sorted else sorted.reversed()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pba_block_wise_report_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFF6F00).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "GRGC LIVE SYNC • GID 2140000000",
                                color = Color(0xFFE65100),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "19 Blocks",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Paschim Banga Awaas (PBA) Block Report",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Table Top Beneficiary Verification, Work Codes & e-MR Register",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                // View Mode Toggle (Table vs Cards)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(2.dp)
                ) {
                    IconButton(
                        onClick = { viewMode = PbaViewMode.TABLE },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (viewMode == PbaViewMode.TABLE) MaterialTheme.colorScheme.primary else Color.Transparent)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = "Table View",
                            tint = if (viewMode == PbaViewMode.TABLE) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewMode = PbaViewMode.CARDS },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (viewMode == PbaViewMode.CARDS) MaterialTheme.colorScheme.primary else Color.Transparent)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewAgenda,
                            contentDescription = "Cards View",
                            tint = if (viewMode == PbaViewMode.CARDS) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar at Top of Block-wise Report Table with Block and Gram Panchayat search support
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("block_report_search_bar"),
                placeholder = { Text("Filter table by block name or Gram Panchayat (e.g. Dubrajpur, Bolpur, Kasba, Chinpai)...", fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search blocks and Gram Panchayats",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search", modifier = Modifier.size(18.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6F00),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Filter Chips to Find Block-wise Gram Panchayat Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Find GP Status:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                SuggestionChip(
                    onClick = { searchQuery = "" },
                    label = { Text("All 19 Blocks", fontSize = 11.sp, fontWeight = if (searchQuery.isBlank()) FontWeight.Bold else FontWeight.Normal) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (searchQuery.isBlank()) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                    )
                )

                SuggestionChip(
                    onClick = { searchQuery = "DUBRAJPUR" },
                    label = { Text("Dubrajpur (10 GPs)", fontSize = 11.sp) }
                )

                SuggestionChip(
                    onClick = { searchQuery = "BOLPUR" },
                    label = { Text("Bolpur (9 GPs)", fontSize = 11.sp) }
                )

                SuggestionChip(
                    onClick = { searchQuery = "SURI" },
                    label = { Text("Suri (15 GPs)", fontSize = 11.sp) }
                )

                SuggestionChip(
                    onClick = { searchQuery = "KASBA" },
                    label = { Text("🏆 Kasba GP", fontSize = 11.sp, color = EmeraldSuccess, fontWeight = FontWeight.Bold) }
                )

                SuggestionChip(
                    onClick = { searchQuery = "LAXMI-NARAYANPUR" },
                    label = { Text("🏆 Laxmi-Narayanpur GP", fontSize = 11.sp, color = EmeraldSuccess, fontWeight = FontWeight.Bold) }
                )

                SuggestionChip(
                    onClick = { searchQuery = "CHINPAI" },
                    label = { Text("⚠️ Chinpai GP (Alert)", fontSize = 11.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold) }
                )

                SuggestionChip(
                    onClick = { searchQuery = "RAIPUR-SUPUR" },
                    label = { Text("⚠️ Raipur-Supur GP", fontSize = 11.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filter indicator status bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (searchQuery.isNotBlank()) {
                    val matchingGpsCount = sortedList.count { item ->
                        val alert = getBlockGpPerformance(item.blockName)
                        alert.bestGp.contains(searchQuery.trim(), ignoreCase = true) ||
                        alert.poorGp.contains(searchQuery.trim(), ignoreCase = true)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFF6F00).copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "Filtered: ${sortedList.size} of ${rawList.size} blocks",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        if (matchingGpsCount > 0) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldSuccess.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "GP Match Found",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = "for \"${searchQuery.trim()}\"",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(
                        onClick = { searchQuery = "" },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Clear", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        text = "Showing all ${rawList.size} blocks (167 Gram Panchayats in Birbhum)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sort Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sort:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FilterChip(
                    selected = sortField == PbaSortField.VERIFIED_OK,
                    onClick = {
                        if (sortField == PbaSortField.VERIFIED_OK) sortAscending = !sortAscending
                        else { sortField = PbaSortField.VERIFIED_OK; sortAscending = false }
                    },
                    label = { Text("Verified OK", fontSize = 11.sp) },
                    trailingIcon = {
                        if (sortField == PbaSortField.VERIFIED_OK) {
                            Icon(
                                imageVector = if (sortAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                )

                FilterChip(
                    selected = sortField == PbaSortField.GP_COUNT,
                    onClick = {
                        if (sortField == PbaSortField.GP_COUNT) sortAscending = !sortAscending
                        else { sortField = PbaSortField.GP_COUNT; sortAscending = false }
                    },
                    label = { Text("GP Count", fontSize = 11.sp) },
                    trailingIcon = {
                        if (sortField == PbaSortField.GP_COUNT) {
                            Icon(
                                imageVector = if (sortAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                )

                FilterChip(
                    selected = sortField == PbaSortField.WORK_CODES,
                    onClick = {
                        if (sortField == PbaSortField.WORK_CODES) sortAscending = !sortAscending
                        else { sortField = PbaSortField.WORK_CODES; sortAscending = false }
                    },
                    label = { Text("Work Codes Soft", fontSize = 11.sp) },
                    trailingIcon = {
                        if (sortField == PbaSortField.WORK_CODES) {
                            Icon(
                                imageVector = if (sortAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                )

                FilterChip(
                    selected = sortField == PbaSortField.EMR_REGISTER,
                    onClick = {
                        if (sortField == PbaSortField.EMR_REGISTER) sortAscending = !sortAscending
                        else { sortField = PbaSortField.EMR_REGISTER; sortAscending = false }
                    },
                    label = { Text("e-MR Issue Register", fontSize = 11.sp) },
                    trailingIcon = {
                        if (sortField == PbaSortField.EMR_REGISTER) {
                            Icon(
                                imageVector = if (sortAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                )

                FilterChip(
                    selected = sortField == PbaSortField.TABLE_TOP,
                    onClick = {
                        if (sortField == PbaSortField.TABLE_TOP) sortAscending = !sortAscending
                        else { sortField = PbaSortField.TABLE_TOP; sortAscending = false }
                    },
                    label = { Text("Table Top", fontSize = 11.sp) }
                )

                FilterChip(
                    selected = sortField == PbaSortField.NAME,
                    onClick = {
                        if (sortField == PbaSortField.NAME) sortAscending = !sortAscending
                        else { sortField = PbaSortField.NAME; sortAscending = true }
                    },
                    label = { Text("Block Name", fontSize = 11.sp) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Content: Table View vs Cards View
            if (viewMode == PbaViewMode.TABLE) {
                PbaTableView(
                    items = sortedList,
                    pbaSummary = pbaSummary,
                    blocks = blocks,
                    searchQuery = searchQuery,
                    onClearSearch = { searchQuery = "" },
                    onSelectBlockName = { name ->
                        val matched = blocks.find { it.blockName.equals(name, ignoreCase = true) }
                        if (matched != null) onSelectBlock(matched)
                    },
                    onOpenGpStatus = { report ->
                        selectedReportForGpStatus = report
                    }
                )
            } else {
                PbaCardsListView(
                    items = sortedList,
                    blocks = blocks,
                    searchQuery = searchQuery,
                    onClearSearch = { searchQuery = "" },
                    onSelectBlock = onSelectBlock,
                    onOpenGpStatus = { report ->
                        selectedReportForGpStatus = report
                    }
                )
            }
        }
    }

    // Modal Dialog to Find & View Block-wise Gram Panchayat Status
    if (selectedReportForGpStatus != null) {
        val rep = selectedReportForGpStatus!!
        val matched = blocks.find { it.blockName.equals(rep.blockName, ignoreCase = true) }
        BlockGpStatusDialog(
            blockReport = rep,
            blockData = matched,
            onDismiss = { selectedReportForGpStatus = null },
            onOpenFullDossier = { b ->
                selectedReportForGpStatus = null
                onSelectBlock(b)
            }
        )
    }
}

@Composable
private fun PbaTableView(
    items: List<PbaBlockReport>,
    pbaSummary: PbaSummary,
    blocks: List<BlockData>,
    searchQuery: String = "",
    onClearSearch: () -> Unit = {},
    onSelectBlockName: (String) -> Unit,
    onOpenGpStatus: (PbaBlockReport) -> Unit
) {
    val horizontalScroll = rememberScrollState()

    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(horizontalScroll)
        ) {
            // Table Header Row with exact requested columns + Gram Panchayat Status
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sl",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(36.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Block",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.width(130.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Gram Panchayat\nStatus & Alert",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.width(155.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Total PBA\nBeneficiaries",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(95.dp),
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Table Top\nExercise Done",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.width(105.dp),
                    textAlign = TextAlign.End,
                    color = Color(0xFFD97706)
                )
                Text(
                    text = "Verified &\nFound OK",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.width(100.dp),
                    textAlign = TextAlign.End,
                    color = EmeraldSuccess
                )
                Text(
                    text = "Work Code in\nVB-G RAM G Soft",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.width(115.dp),
                    textAlign = TextAlign.End,
                    color = Color(0xFF0284C7)
                )
                Text(
                    text = "As per e-MR\nRegister (R6.9)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.width(110.dp),
                    textAlign = TextAlign.End,
                    color = Color(0xFF7C3AED)
                )
                Text(
                    text = "Deleted JC\n(Pending)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(90.dp),
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "JC Not Avail\n(Pending)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(90.dp),
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            if (items.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 36.dp, horizontal = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No blocks found matching \"$searchQuery\"" else "No block records available",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Check block/GP spelling or clear search filter to view all 19 blocks",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (searchQuery.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            FilledTonalButton(onClick = onClearSearch) {
                                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Clear search filter")
                            }
                        }
                    }
                }
            } else {
                // Data Rows for 19 Blocks
                items.forEachIndexed { index, item ->
                    val isEven = index % 2 == 0
                    val rowBg = if (isEven) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                    val isDubrajpur = item.blockName.contains("DUBRAJPUR", ignoreCase = true)
                    val blockData = blocks.find { it.blockName.equals(item.blockName, ignoreCase = true) }
                    val alert = getBlockGpPerformance(item.blockName)
                    val gpCount = blockData?.works?.gps ?: 10
                    val isGpMatch = searchQuery.isNotBlank() && (
                        alert.bestGp.contains(searchQuery.trim(), ignoreCase = true) ||
                        alert.poorGp.contains(searchQuery.trim(), ignoreCase = true)
                    )

                    Row(
                        modifier = Modifier
                            .background(if (isDubrajpur) Color(0xFFFF6F00).copy(alpha = 0.08f) else if (isGpMatch) EmeraldSuccess.copy(alpha = 0.08f) else rowBg)
                            .clickable { onSelectBlockName(item.blockName) }
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${item.slNo}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.width(36.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.width(130.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.blockName,
                                fontSize = 11.sp,
                                fontWeight = if (isDubrajpur) FontWeight.Black else FontWeight.Bold,
                                color = if (isDubrajpur) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurface
                            )
                            if (isDubrajpur) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFF6F00),
                                    modifier = Modifier.size(6.dp)
                                ) {}
                            }
                        }

                        // Gram Panchayat Status & Alert Column
                        Column(
                            modifier = Modifier
                                .width(155.dp)
                                .clickable { onOpenGpStatus(item) }
                                .padding(end = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "$gpCount GPs",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFFF6F00).copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "Status",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE65100)
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            tint = Color(0xFFE65100),
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "🏆 ${alert.bestGp}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "⚠️ ${alert.poorGp}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFDC2626),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Text(
                            text = "%,d".format(item.totalBeneficiaries),
                            fontSize = 11.sp,
                            modifier = Modifier.width(95.dp),
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "%,d".format(item.tableTopDone),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(105.dp),
                            textAlign = TextAlign.End,
                            color = Color(0xFFD97706)
                        )

                        Text(
                            text = "%,d".format(item.verifiedOk),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.width(100.dp),
                            textAlign = TextAlign.End,
                            color = EmeraldSuccess
                        )

                        Text(
                            text = "%,d".format(item.availableWorkCodes),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(115.dp),
                            textAlign = TextAlign.End,
                            color = Color(0xFF0284C7)
                        )

                        Text(
                            text = "%,d".format(item.emrIssueRegister),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(110.dp),
                            textAlign = TextAlign.End,
                            color = Color(0xFF7C3AED)
                        )

                        Text(
                            text = "%,d".format(item.deletedJobcards - item.newJobcardsForDeleted),
                            fontSize = 10.sp,
                            modifier = Modifier.width(90.dp),
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "%,d".format(item.notAvailableJobcards - item.newJobcardsForNotAvailable),
                            fontSize = 10.sp,
                            modifier = Modifier.width(90.dp),
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                }
            }

            // Total Row Pinned At Bottom (if items not empty)
            if (items.isNotEmpty()) {
                val isFiltered = items.size < 19
                Row(
                    modifier = Modifier
                        .background(Color(0xFFFF6F00).copy(alpha = 0.12f))
                        .padding(vertical = 12.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Σ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.width(36.dp),
                        color = Color(0xFFE65100)
                    )
                    Text(
                        text = if (isFiltered) "SUBTOTAL (${items.size} BLOCKS)" else "DISTRICT TOTAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.width(130.dp),
                        color = Color(0xFFE65100)
                    )
                    Column(modifier = Modifier.width(155.dp)) {
                        Text(
                            text = if (isFiltered) "${items.size * 9} GPs (Subtotal)" else "167 Gram Panchayats",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = "District-wide Status",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "%,d".format(if (isFiltered) items.sumOf { it.totalBeneficiaries } else pbaSummary.totalBeneficiaries),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.width(95.dp),
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "%,d".format(if (isFiltered) items.sumOf { it.tableTopDone } else pbaSummary.tableTopDone),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.width(105.dp),
                        textAlign = TextAlign.End,
                        color = Color(0xFFD97706)
                    )
                    Text(
                        text = "%,d".format(if (isFiltered) items.sumOf { it.verifiedOk } else pbaSummary.verifiedOk),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.width(100.dp),
                        textAlign = TextAlign.End,
                        color = EmeraldSuccess
                    )
                    Text(
                        text = "%,d".format(if (isFiltered) items.sumOf { it.availableWorkCodes } else pbaSummary.availableWorkCodes),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.width(115.dp),
                        textAlign = TextAlign.End,
                        color = Color(0xFF0284C7)
                    )
                    Text(
                        text = "%,d".format(if (isFiltered) items.sumOf { it.emrIssueRegister } else pbaSummary.emrIssueRegister),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.width(110.dp),
                        textAlign = TextAlign.End,
                        color = Color(0xFF7C3AED)
                    )
                    Text(
                        text = "%,d".format(if (isFiltered) items.sumOf { it.deletedJobcards - it.newJobcardsForDeleted } else pbaSummary.deletedJobcards - pbaSummary.newJobcardsForDeleted),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(90.dp),
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "%,d".format(if (isFiltered) items.sumOf { it.notAvailableJobcards - it.newJobcardsForNotAvailable } else pbaSummary.notAvailableJobcards - pbaSummary.newJobcardsForNotAvailable),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(90.dp),
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PbaCardsListView(
    items: List<PbaBlockReport>,
    blocks: List<BlockData>,
    searchQuery: String = "",
    onClearSearch: () -> Unit = {},
    onSelectBlock: (BlockData) -> Unit,
    onOpenGpStatus: (PbaBlockReport) -> Unit
) {
    if (items.isEmpty()) {
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
                    .padding(28.dp),
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
                        text = if (searchQuery.isNotBlank()) "No blocks found matching \"$searchQuery\"" else "No block data available",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (searchQuery.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        TextButton(onClick = onClearSearch) {
                            Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset search filter")
                        }
                    }
                }
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEach { item ->
                val matchedBlock = blocks.find { it.blockName.equals(item.blockName, ignoreCase = true) }
                val isDubrajpur = item.blockName.contains("DUBRAJPUR", ignoreCase = true)
                val alert = getBlockGpPerformance(item.blockName)
                val gpCount = matchedBlock?.works?.gps ?: 10

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (matchedBlock != null) onSelectBlock(matchedBlock)
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDubrajpur) Color(0xFFFF6F00).copy(alpha = 0.05f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ),
                    border = if (isDubrajpur) BorderStroke(1.dp, Color(0xFFFF6F00).copy(alpha = 0.4f)) else null
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Block Title & Verified OK badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#${item.slNo}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.blockName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldSuccess.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${String.format(java.util.Locale.US, "%.1f%%", item.verifiedPct)} OK",
                                    color = EmeraldSuccess,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Gram Panchayat Status Card
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenGpStatus(item) }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AccountBalance,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Gram Panchayat Status ($gpCount GPs)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = "Inspect Status ›",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Best GP
                                    Surface(
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(6.dp),
                                        color = EmeraldSuccess.copy(alpha = 0.08f)
                                    ) {
                                        Column(modifier = Modifier.padding(6.dp)) {
                                            Text(text = "🏆 Best GP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                                            Text(text = alert.bestGp, fontSize = 11.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                                            Text(text = alert.bestGpStats, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    // Poor GP
                                    Surface(
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFDC2626).copy(alpha = 0.08f)
                                    ) {
                                        Column(modifier = Modifier.padding(6.dp)) {
                                            Text(text = "⚠️ Alert GP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                                            Text(text = alert.poorGp, fontSize = 11.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                                            Text(text = alert.poorGpStats, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 4 Core Columns Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Table Top Done
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFF6F00).copy(alpha = 0.08f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "Table Top",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFD97706)
                                    )
                                    Text(
                                        text = "%,d".format(item.tableTopDone),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "of %,d".format(item.totalBeneficiaries),
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Verified & Found OK
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldSuccess.copy(alpha = 0.08f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "Verified OK",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = EmeraldSuccess
                                    )
                                    Text(
                                        text = "%,d".format(item.verifiedOk),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${String.format(java.util.Locale.US, "%.1f%%", item.verifiedPct)}",
                                        fontSize = 9.sp,
                                        color = EmeraldSuccess,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Work Codes in Soft
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0284C7).copy(alpha = 0.08f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "Work Codes",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF0284C7)
                                    )
                                    Text(
                                        text = "%,d".format(item.availableWorkCodes),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "in Soft",
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // e-MR Register (R6.9)
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF7C3AED).copy(alpha = 0.08f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "e-MR (R6.9)",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF7C3AED)
                                    )
                                    Text(
                                        text = "%,d".format(item.emrIssueRegister),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${item.mustersGenerated} Musters",
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress indicators
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Work Code Coverage: ${String.format(java.util.Locale.US, "%.1f%%", item.workCodePct)} of Verified OK",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Pending JC: ${item.pendingDeleted + item.pendingNotAvailable}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (item.pendingDeleted + item.pendingNotAvailable > 0) Color(0xFFDC2626) else EmeraldSuccess
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        AnimatedLinearProgressIndicator(
                            targetProgress = (item.workCodePct / 100.0).toFloat().coerceIn(0f, 1f),
                            color = Color(0xFF0284C7),
                            height = 4
                        )
                    }
                }
            }
        }
    }
}

/**
 * Modal Dialog to view and inspect detailed Gram Panchayat Status and Performance for a block
 */
@Composable
fun BlockGpStatusDialog(
    blockReport: PbaBlockReport,
    blockData: BlockData?,
    onDismiss: () -> Unit,
    onOpenFullDossier: (BlockData) -> Unit
) {
    val context = LocalContext.current
    val alert = getBlockGpPerformance(blockReport.blockName)
    val gpCount = blockData?.works?.gps ?: 10

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .clip(RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
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
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${blockReport.blockName} Block",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Gram Panchayat Performance & Status ($gpCount GPs)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // GP Performance Alert Section
                    Text(
                        text = "GRAM PANCHAYAT BENCHMARK ALERT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Best GP Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldSuccess.copy(alpha = 0.08f)),
                        border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.3f))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Top Performing GP: ",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldSuccess
                                    )
                                    Text(
                                        text = alert.bestGp,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = alert.bestGpStats,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Poor GP Alert Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDC2626).copy(alpha = 0.08f)),
                        border = BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.3f))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Underperforming / Alert GP: ",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFDC2626)
                                    )
                                    Text(
                                        text = alert.poorGp,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = alert.poorGpStats,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Operational Telemetry
                    Text(
                        text = "BLOCK & GP OPERATIONAL METRICS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Person-days", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("%,d".format(alert.persondaysGenerated), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("Generated", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("NMMS Attendance", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("%,d".format(alert.nmmsAttendance), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("Today: %,d".format(alert.todayNmmsAttendance), fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = EmeraldSuccess)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Verified OK", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("%,d".format(blockReport.verifiedOk), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                                Text("${String.format(java.util.Locale.US, "%.1f%%", blockReport.verifiedPct)}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                            }
                        }
                    }

                    // Sector works summary
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Sector-wise Works Summary", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(alert.sectorWorks, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                // Actions: Share & Open Dossier
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val shareText = """
                                🏛️ BIRBHUM DISTRICT - GRAM PANCHAYAT STATUS REPORT
                                Block: ${blockReport.blockName}
                                Gram Panchayats Monitored: $gpCount GPs
                                
                                🏆 Best Performing GP: ${alert.bestGp} (${alert.bestGpStats})
                                ⚠️ Underperforming GP: ${alert.poorGp} (${alert.poorGpStats})
                                
                                📊 Operational Metrics:
                                • Person-days Generated: %,d
                                • NMMS Attendance: %,d (Today: %,d)
                                • PBA Beneficiaries: %,d (Verified OK: %,d / %.1f%%)
                                • Work Codes in Soft: %,d
                                • Sector Works: ${alert.sectorWorks}
                                
                                [District Rural Development Cell - Birbhum]
                            """.trimIndent().format(
                                alert.persondaysGenerated,
                                alert.nmmsAttendance,
                                alert.todayNmmsAttendance,
                                blockReport.totalBeneficiaries,
                                blockReport.verifiedOk,
                                blockReport.verifiedPct,
                                blockReport.availableWorkCodes
                            )
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share GP Status Report"))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share GP Report", fontSize = 11.sp)
                    }

                    if (blockData != null) {
                        Button(
                            onClick = { onOpenFullDossier(blockData) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Full Dossier", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}
