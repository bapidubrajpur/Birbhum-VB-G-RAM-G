package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import com.example.data.models.CategoryMatrixSummary
import com.example.data.models.SchemeCategoryData
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GovNavyPrimaryLight

enum class CategoryFilter(val label: String) {
    ALL("All Categories (11)"),
    HIGH_PROGRESS("High Approval (≥60%)"),
    NEEDS_FOCUS("Pipeline Focus (<60%)"),
    WATER_WORKS("Water & Irrigation")
}

@Composable
fun CategoryMatrixWorkAnalysisCard(
    matrix: CategoryMatrixSummary,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(CategoryFilter.ALL) }
    var expandedCategoryId by remember { mutableStateOf<String?>(null) }

    val filteredCategories = remember(selectedFilter, matrix.categories) {
        when (selectedFilter) {
            CategoryFilter.ALL -> matrix.categories
            CategoryFilter.HIGH_PROGRESS -> matrix.categories.filter { it.approvedPercent >= 60.0 }
            CategoryFilter.NEEDS_FOCUS -> matrix.categories.filter { it.approvedPercent < 60.0 }
            CategoryFilter.WATER_WORKS -> matrix.categories.filter {
                it.id in listOf("DROUGHT", "WATER_BODIES", "WATER_CONSERVATION", "MICRO_IRRIGATION", "FLOOD_CONTROL")
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("category_matrix_work_analysis_card"),
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
                            .background(Color(0xFF7C3AED).copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = Color(0xFF7C3AED),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Work Analysis by Category",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF7C3AED).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "GP MATRIX",
                                    color = Color(0xFF7C3AED),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "GP x Category Matrix Sheet • 11 Distinct Scheme Categories",
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
                        text = "167 GPs",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Overall Headline Callout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF4C1D95), // Deep purple
                                Color(0xFF6D28D9)  // Vibrant violet
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
                            text = "DISTRICT WORKS PIPELINE & PROGRESS",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${matrix.approvalPercent}% Approved",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
                                text = "%,d".format(matrix.totalReceivedWorks),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Total Received Works Proposals",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "%,d".format(matrix.totalApprovedWorks),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Works Approved & Sanctioned",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Animated District Approval Progress
                    AnimatedLinearProgressIndicator(
                        targetProgress = (matrix.approvalPercent / 100.0).toFloat(),
                        color = AmberGold,
                        trackColor = Color.White.copy(alpha = 0.25f),
                        height = 8
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3-Metric Quick Stats Card (Ongoing, Persondays, NMMS Attendance)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Ongoing Works",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "%,d".format(matrix.totalOngoingWorks),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0284C7)
                        )
                        Text(
                            text = "26.9% active in field",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Persondays Gen.",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "%,d".format(matrix.totalPersondays),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = EmeraldSuccess
                        )
                        Text(
                            text = "District cumulative",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "NMMS Attendance",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "%,d".format(matrix.totalNmmsAttendance),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF7C3AED)
                        )
                        Text(
                            text = "Muster verified",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips Scrollable Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryFilter.values().forEach { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                text = filter.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF7C3AED),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Categories List with Progress and Details
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredCategories.forEach { category ->
                    val isExpanded = expandedCategoryId == category.id

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedCategoryId = if (isExpanded) null else category.id
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        tonalElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Category Title Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = category.icon,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = category.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Received: %,d works • Approved: %,d works".format(category.receivedWorks, category.approvedWorks),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                // Status Badge
                                val badgeColor = when {
                                    category.approvedPercent >= 70.0 -> EmeraldSuccess
                                    category.approvedPercent >= 50.0 -> Color(0xFF0284C7)
                                    category.approvedPercent >= 20.0 -> Color(0xFFD97706)
                                    else -> Color(0xFFDC2626)
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = badgeColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = String.format(java.util.Locale.US, "%.2f%% Approved", category.approvedPercent),
                                        color = badgeColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Progress Bar for Category
                            val progressColor = when {
                                category.approvedPercent >= 70.0 -> EmeraldSuccess
                                category.approvedPercent >= 50.0 -> Color(0xFF0284C7)
                                category.approvedPercent >= 20.0 -> Color(0xFFD97706)
                                else -> Color(0xFFDC2626)
                            }

                            AnimatedLinearProgressIndicator(
                                targetProgress = (category.approvedPercent / 100.0).toFloat().coerceIn(0f, 1f),
                                color = progressColor,
                                height = 6
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Key metric pills: Ongoing, Persondays, NMMS
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF0284C7).copy(alpha = 0.1f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Ongoing: %,d".format(category.ongoingWorks),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF0284C7)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldSuccess.copy(alpha = 0.1f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "PD: %,d".format(category.persondaysGenerated),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = EmeraldSuccess
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF7C3AED).copy(alpha = 0.1f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Att: %,d".format(category.nmmsAttendance),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF7C3AED)
                                        )
                                    }
                                }
                            }

                            // Expandable Details
                            AnimatedVisibility(visible = isExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                ) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Field Persondays Generated:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "%,d Persondays".format(category.persondaysGenerated),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "NMMS Attendance Recorded:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "%,d workers".format(category.nmmsAttendance),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Pending Sanction Review:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        val pending = (category.receivedWorks - category.approvedWorks).coerceAtLeast(0)
                                        Text(
                                            text = "%,d works".format(pending),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (pending > 0) Color(0xFFD97706) else EmeraldSuccess
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
}
