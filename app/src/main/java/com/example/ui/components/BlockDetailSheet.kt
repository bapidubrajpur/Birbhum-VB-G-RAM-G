package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import com.example.data.models.BlockData
import com.example.data.models.formatFund
import com.example.data.models.getBlockGpPerformance
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockDetailSheet(
    block: BlockData,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val allot = block.allotment
    val works = block.works
    val fto = block.fto
    val ekyc = block.ekyc
    val target = block.target

    val alertInfo = getBlockGpPerformance(block.blockName)
    val persondaysGen = if (block.persondaysGenerated > 0) block.persondaysGenerated else alertInfo.persondaysGenerated
    val nmmsAtt = if (block.nmmsAttendance > 0) block.nmmsAttendance else alertInfo.nmmsAttendance
    val todayAtt = if (block.todayNmmsAttendance > 0) block.todayNmmsAttendance else alertInfo.todayNmmsAttendance
    val sectorWorksSummary = if (block.sectorWorks.isNotEmpty()) block.sectorWorks else alertInfo.sectorWorks
    val bestGpName = if (block.bestGp.isNotEmpty()) block.bestGp else alertInfo.bestGp
    val bestGpStatsStr = if (block.bestGpStats.isNotEmpty()) block.bestGpStats else alertInfo.bestGpStats
    val poorGpName = if (block.poorGp.isNotEmpty()) block.poorGp else alertInfo.poorGp
    val poorGpStatsStr = if (block.poorGpStats.isNotEmpty()) block.poorGpStats else alertInfo.poorGpStats

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.testTag("block_detail_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "Block #${block.slNo}",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Birbhum, West Bengal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = block.blockName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = {
                        val shareText = """
                            Birbhum VB-G RAM G Report
                            Block: ${block.blockName}
                            ------------------------------------
                            • Total Allotment: ₹${allot.totalLakh.formatFund()} Lakh (₹${allot.totalCr.formatFund()} Cr)
                            • Cat I (SC): ₹${allot.cat1Lakh.formatFund()} Lakh
                            • Cat II (ST): ₹${allot.cat2Lakh.formatFund()} Lakh
                            • Cat III (Others): ₹${allot.cat3Lakh.formatFund()} Lakh
                            • Works: ${works.totalWorks} (TS: ${works.tsPct.formatFund()}%, AS: ${works.asPct.formatFund()}%)
                            • Material FTOs: ₹${String.format(java.util.Locale.US, "%.2f", fto.verifiedAmountRs / 10000000.0)} Cr Verified (100%)
                            • e-KYC: ${ekyc.ekycPct}% (${ekyc.ekycDone}/${ekyc.totalWorkers})
                            • Person-days Target: ${"%,d".format(target.totalTarget)}
                            • Person-days Generated: ${"%,d".format(persondaysGen)}
                            • NMMS Attendance: ${"%,d".format(nmmsAtt)} Cumulative (${todayAtt} Today)
                            • Sector-wise Works: $sectorWorksSummary

                            🚨 GRAM PANCHAYAT PERFORMANCE ALERT:
                            🏆 Best Gram Panchayat: $bestGpName ($bestGpStatsStr)
                            ⚠️ Poor Performing Gram Panchayat: $poorGpName ($poorGpStatsStr)
                        """.trimIndent()
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Block Summary"))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Block Report",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 0. GRAM PANCHAYAT PERFORMANCE ALERT BANNER
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gram_panchayat_performance_alert"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFBA1A1A).copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Alert",
                                tint = Color(0xFFBA1A1A),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "GRAM PANCHAYAT PERFORMANCE ALERT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFBA1A1A),
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "${works.gps} Gram Panchayats monitored across ${block.blockName}",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Best Performing GP
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = EmeraldSuccess.copy(alpha = 0.10f),
                        border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🏆",
                                fontSize = 22.sp,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = bestGpName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = EmeraldSuccess
                                    ) {
                                        Text(
                                            text = "BEST GP",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = bestGpStatsStr,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = EmeraldSuccess,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Poor Performing GP
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFBA1A1A).copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, Color(0xFFBA1A1A).copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚠️",
                                fontSize = 22.sp,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = poorGpName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFBA1A1A)
                                    ) {
                                        Text(
                                            text = "POOR PERFORMING",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = poorGpStatsStr,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = Color(0xFFBA1A1A),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Block Operational Metrics inside Alert Banner
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "PERSON-DAYS GENERATED",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${"%,d".format(persondaysGen)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "NMMS ATTENDANCE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${"%,d".format(nmmsAtt)} (${todayAtt} Today)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "SECTOR-WISE WORKS",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = sectorWorksSummary,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Share Alert & Performance Button
                    FilledTonalButton(
                        onClick = {
                            val alertShareText = """
                                🚨 BIRBHUM GRAM PANCHAYAT PERFORMANCE ALERT
                                Block: ${block.blockName}
                                ------------------------------------
                                🏆 BEST GRAM PANCHAYAT:
                                • $bestGpName ($bestGpStatsStr)

                                ⚠️ POOR PERFORMING GRAM PANCHAYAT:
                                • $poorGpName ($poorGpStatsStr)

                                📊 KEY BLOCK INDICATORS:
                                • Person-days Generated: ${"%,d".format(persondaysGen)}
                                • Person-days Target: ${"%,d".format(target.totalTarget)}
                                • NMMS Attendance: ${"%,d".format(nmmsAtt)} Cumulative (${todayAtt} Today)
                                • Sector-wise Works: $sectorWorksSummary
                                • Total Allotment: ₹${allot.totalLakh.formatFund()} L (₹${allot.totalCr.formatFund()} Cr)
                            """.trimIndent()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, alertShareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share GP Alert Report"))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("share_alert_report_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Share GP Alert & Metrics",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 1. ALLOTMENT DOSSIER (GID 263874047)
            SectionHeader(
                title = "State Allotment (GID 263874047)",
                subtitle = "80% Distributable Fund allocation & category heads"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Distributable to Block",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "₹${allot.totalLakh.formatFund()} Lakh (₹${allot.totalCr.formatFund()} Cr)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Cat I (SC)", fontSize = 10.sp, color = CyanMetric, fontWeight = FontWeight.Bold)
                                Text("₹${allot.cat1Lakh.formatFund()} L", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Cat II (ST)", fontSize = 10.sp, color = Color(0xFFD97706), fontWeight = FontWeight.Bold)
                                Text("₹${allot.cat2Lakh.formatFund()} L", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Cat III (Gen)", fontSize = 10.sp, color = PurpleMetric, fontWeight = FontWeight.Bold)
                                Text("₹${allot.cat3Lakh.formatFund()} L", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Beneficiary counts: ${allot.totalCount} Total (${allot.countA} Category A + ${allot.countB} Category B)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1B. SCHEME BANK ACTUAL EXPENDITURE & PERSONDAYS (GID 6823442)
            SectionHeader(
                title = "Scheme Bank Actual Expenditure (GID 6823442)",
                subtitle = "Live Financial Status & Persondays Generated"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CoralWarning.copy(alpha = 0.08f)
                ),
                border = BorderStroke(1.dp, CoralWarning.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Actual Booked Expenditure",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = CoralWarning
                            )
                            Text(
                                text = "₹${block.expenditure.bookedExpenditureLakh.formatFund()} Lakh",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CoralWarning.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${block.expenditure.utilizationPct(block.allotment.totalLakh).formatFund()}% Spent",
                                color = CoralWarning,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Sanction", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("₹${block.expenditure.sanctionAmountLakh.formatFund()} L", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Paid Out", fontSize = 10.sp, color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                                Text("₹${block.expenditure.paidExpenditureLakh.formatFund()} L", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Persondays", fontSize = 10.sp, color = PurpleMetric, fontWeight = FontWeight.Bold)
                                Text("${String.format("%,d", block.expenditure.persondaysGenerated)}", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Wages: ₹${block.expenditure.wagesBookedLakh.formatFund()} L • Material: ₹${block.expenditure.materialBookedLakh.formatFund()} L",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Rem: ₹${block.expenditure.remainingLakh(block.allotment.totalLakh).formatFund()} L",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. WORKS PROGRESS
            SectionHeader(
                title = "Works & Sanctions Progress",
                subtitle = "${works.gps} Gram Panchayats implementation"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Works: ${works.totalWorks}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("Estimates Started: ${works.estimatesStarted}", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Technical Sanctions (TS): ${works.ts} (${works.tsPct.formatFund()}%)", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text("Admin Sanctions (AS): ${works.asSanctions} (${works.asPct.formatFund()}%)", fontSize = 12.sp, color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (works.asPct / 100.0).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = EmeraldSuccess,
                        trackColor = MaterialTheme.colorScheme.surface
                    )
                }
            }

            // 2B. PASCHIM BANGA AWAAS (PBA) - GRGC LIVE SYNC
            if (block.pba != null) {
                val p = block.pba
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = "Paschim Banga Awaas (PBA)",
                    subtitle = "GRGC Live Sync • Beneficiaries & Work Codes"
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Table Top Done: %,d / %,d".format(p.tableTopDone, p.totalBeneficiaries),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Verified OK: %,d (%.1f%%)".format(p.verifiedOk, p.verifiedPct),
                                color = EmeraldSuccess,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Work Codes in Soft: %,d".format(p.availableWorkCodes),
                                color = Color(0xFF0284C7),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "e-MR Issue Register: %,d".format(p.emrIssueRegister),
                                color = Color(0xFF7C3AED),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (p.workCodePct / 100.0).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF0284C7),
                            trackColor = MaterialTheme.colorScheme.surface
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Coverage: %.1f%% of Verified OK • Deleted JC Pending: %,d • Not Avail JC Pending: %,d".format(
                                p.workCodePct,
                                (p.deletedJobcards - p.newJobcardsForDeleted).coerceAtLeast(0),
                                (p.notAvailableJobcards - p.newJobcardsForNotAvailable).coerceAtLeast(0)
                            ),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. MATERIAL FTOs & AUDIT
            SectionHeader(
                title = "Material FTOs Audit by BLT",
                subtitle = "Status: ${fto.status} Verified"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("FTOs Checked: ${fto.ftosChecked} of ${fto.ftosPending}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("Verified: ₹${String.format("%.2f", fto.verifiedAmountRs / 10000000.0)} Cr", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Accepted: ₹${String.format("%.2f", fto.acceptedAmountRs / 10000000.0)} Cr", fontSize = 12.sp, color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                        if (fto.rejectedAmountRs > 0) {
                            Text("Rejected: ₹${String.format("%.2f", fto.rejectedAmountRs / 100000.0)} Lakh", fontSize = 12.sp, color = CrimsonAlert, fontWeight = FontWeight.Bold)
                        } else {
                            Text("No Rejections", fontSize = 12.sp, color = EmeraldSuccess)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. e-KYC & AADHAAR
            SectionHeader(
                title = "Workers Demographic Authentication",
                subtitle = "${ekyc.totalWorkers} Active Registered Workers"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Aadhaar Seeded: ${ekyc.aadhaarSeeded}", fontSize = 12.sp)
                        Text("e-KYC Done: ${ekyc.ekycDone} (${ekyc.ekycPct}%)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("AUA Authenticated: ${ekyc.authenticated}", fontSize = 12.sp)
                        Text("ABPS Eligible: ${ekyc.abpsEligible}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. TARGET SCHEDULE
            SectionHeader(
                title = "Person-days Target (Special Drive)",
                subtitle = "Total Target: ${target.totalTarget} persondays"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Oct 26", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${target.oct2026}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Nov 26", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${target.nov2026}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Dec 26", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${target.dec2026}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Jan 27", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${target.jan2027}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Feb 27", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${target.feb2027}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Close Dossier")
            }
        }
    }
}
