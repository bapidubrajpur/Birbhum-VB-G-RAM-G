package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BirbhumData
import com.example.data.models.BlockData
import com.example.ui.components.MetricKpiCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyPrimary

@Composable
fun FtoScreen(
    data: BirbhumData,
    onSelectBlock: (BlockData) -> Unit
) {
    val fto = data.ftoSummary

    fun formatCrores(rupees: Long): String {
        val cr = rupees / 10000000.0
        return String.format("₹%.2f Cr", cr)
    }

    fun formatLakhs(rupees: Long): String {
        val lakh = rupees / 100000.0
        return String.format("₹%.2f L", lakh)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("fto_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Administrative Memo Reference Card
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
                            text = "MATERIAL FTO VERIFICATION REPORT",
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
                                text = "100.00% Done",
                                color = EmeraldSuccess,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ref: ${fto.memoRef}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Comprehensive findings of Block Level Teams (BLTs) across Birbhum district",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // FTO KPIs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricKpiCard(
                    title = "Verified Amount",
                    value = formatCrores(fto.verifiedAmountRs),
                    subtitle = "1,551 FTOs Total",
                    icon = Icons.Default.ReceiptLong,
                    iconTint = NavyPrimary,
                    modifier = Modifier.weight(1f)
                )

                MetricKpiCard(
                    title = "Accepted by BLT",
                    value = formatCrores(fto.acceptedAmountRs),
                    subtitle = "98.3% Accepted",
                    icon = Icons.Default.CheckCircle,
                    iconTint = EmeraldSuccess,
                    badgeText = "Clean",
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
                    title = "Rejected Amount",
                    value = formatCrores(fto.rejectedAmountRs),
                    subtitle = "${formatLakhs(fto.rejectedAmountRs)} disallowed",
                    icon = Icons.Default.Cancel,
                    iconTint = CrimsonAlert,
                    badgeText = "Audited",
                    modifier = Modifier.weight(1f)
                )

                MetricKpiCard(
                    title = "FTOs Checked",
                    value = "${fto.checkedFtos}",
                    subtitle = "of ${fto.totalFtos} FTOs",
                    icon = Icons.Default.FactCheck,
                    iconTint = MaterialTheme.colorScheme.primary,
                    badgeText = "100%",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section Header
        item {
            SectionHeader(
                title = "Block-wise Material FTO Findings",
                subtitle = "Accepted vs Rejected audit amounts by BLT",
                badgeText = "19 Blocks"
            )
        }

        // Block cards
        val sortedByAmount = data.blocks.sortedByDescending { it.fto.verifiedAmountRs }
        items(sortedByAmount, key = { it.slNo }) { block ->
            val bFto = block.fto
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
                                text = "${bFto.ftosPending} FTOs • ${bFto.ftosChecked} Checked by BLT",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldSuccess.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "100%",
                                color = EmeraldSuccess,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Total Verified",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = formatCrores(bFto.verifiedAmountRs),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldSuccess.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Accepted",
                                    fontSize = 10.sp,
                                    color = EmeraldSuccess
                                )
                                Text(
                                    text = formatCrores(bFto.acceptedAmountRs),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess
                                )
                            }
                        }

                        if (bFto.rejectedAmountRs > 0) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = CrimsonAlert.copy(alpha = 0.12f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Rejected",
                                        fontSize = 10.sp,
                                        color = CrimsonAlert
                                    )
                                    Text(
                                        text = formatLakhs(bFto.rejectedAmountRs),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CrimsonAlert
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
