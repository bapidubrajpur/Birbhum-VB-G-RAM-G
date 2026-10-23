package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.EmeraldSuccess
import kotlinx.coroutines.delay

/**
 * Official Cover / Landing Page for VB-GRAM G Birbhum
 * Displays for 15 seconds upon app launch, then automatically transitions to reports.
 * User can also skip immediately or pause the timer.
 */
@Composable
fun LandingPageScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var secondsRemaining by remember { mutableIntStateOf(15) }
    var isPaused by remember { mutableStateOf(false) }

    // 15-second countdown timer
    LaunchedEffect(isPaused) {
        while (secondsRemaining > 0) {
            delay(1000L)
            if (!isPaused) {
                secondsRemaining--
            }
        }
        if (secondsRemaining <= 0) {
            onDismiss()
        }
    }

    val progressFraction by animateFloatAsState(
        targetValue = secondsRemaining / 15f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "countdown_progress"
    )

    // Palette inspired by the official cover
    val saffronPrimary = Color(0xFFE65100)
    val saffronWarm = Color(0xFFF57C00)
    val saffronLight = Color(0xFFFF9800)
    val creamBg = Color(0xFFFFFDF8)
    val cardBg = Color(0xFFFFF8EC)
    val darkCharcoal = Color(0xFF231F20)
    val leafGreen = Color(0xFF2E7D32)
    val navyBlue = Color(0xFF0D47A1)

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(creamBg)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("landing_page_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Floating Countdown & Skip Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .testTag("landing_countdown_bar"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1E16)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = saffronPrimary,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "$secondsRemaining",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = if (isPaused) "Timer Paused" else "Continuing to Reports in ${secondsRemaining}s",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "VB-GRAM G Birbhum Annual Review",
                                    color = Color(0xFFFFCC80),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            IconButton(
                                onClick = { isPaused = !isPaused },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                    contentDescription = if (isPaused) "Resume" else "Pause",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(containerColor = saffronPrimary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("skip_to_reports_button")
                            ) {
                                Text(
                                    text = "Reports",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Progress line
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = saffronWarm,
                        trackColor = Color(0xFF4E342E)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldSuccess)
                            )
                            Text(
                                text = "AUTO-UPDATED LIVE",
                                color = EmeraldSuccess,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFF6F00).copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "FEATURE UPDATED DONE",
                                color = Color(0xFFFFCC80),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Main Document / Poster Sheet
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(2.dp, Brush.linearGradient(listOf(saffronPrimary, saffronLight, saffronPrimary))),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    // Inner Decorative Double Border
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFFFCC80), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Top Header Bar: National Emblem + Viksit Bharat 2047
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // National Emblem
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width(72.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_state_emblem),
                                        contentDescription = "State Emblem of India",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(42.dp)
                                    )
                                    Text(
                                        text = "सत्यमेव जयते",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF424242)
                                    )
                                }

                                // Ornamental Center Filigree
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = "✦", color = saffronWarm, fontSize = 10.sp)
                                    Box(
                                        modifier = Modifier
                                            .width(28.dp)
                                            .height(1.dp)
                                            .background(saffronLight)
                                    )
                                    Text(text = "❖", color = saffronPrimary, fontSize = 12.sp)
                                    Box(
                                        modifier = Modifier
                                            .width(28.dp)
                                            .height(1.dp)
                                            .background(saffronLight)
                                    )
                                    Text(text = "✦", color = saffronWarm, fontSize = 10.sp)
                                }

                                // Viksit Bharat 2047
                                Column(
                                    horizontalAlignment = Alignment.End,
                                    modifier = Modifier.padding(end = 4.dp)
                                ) {
                                    Text(
                                        text = "Viksit",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = saffronPrimary
                                    )
                                    Text(
                                        text = "Bharat",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = navyBlue
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(10.dp)
                                                .height(2.5.dp)
                                                .background(saffronPrimary)
                                        )
                                        Text(
                                            text = "2047",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = darkCharcoal
                                        )
                                        Box(
                                            modifier = Modifier
                                                .width(10.dp)
                                                .height(2.5.dp)
                                                .background(leafGreen)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Main Title: VB-GRAM G
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "VB-",
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.SansSerif,
                                    color = darkCharcoal,
                                    letterSpacing = 1.sp
                                )

                                // Stylized G with Leaf
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(46.dp)
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawStylizedG(
                                            saffron = saffronPrimary,
                                            leafColor = leafGreen
                                        )
                                    }
                                }

                                Text(
                                    text = "RAM G",
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.SansSerif,
                                    color = darkCharcoal,
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // BIRBHUM Title
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "❧", color = saffronWarm, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "BIRBHUM",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Serif,
                                    color = saffronPrimary,
                                    letterSpacing = 4.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "☙", color = saffronWarm, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Motto: Transparency | Accountability | Participation | Progress
                            Text(
                                text = "TRANSPARENCY   |   ACCOUNTABILITY   |   PARTICIPATION   |   PROGRESS",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6D4C41),
                                letterSpacing = 0.5.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // 125 DAYS WORK Ribbon Banner
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFFBF360C),
                                                saffronPrimary,
                                                Color(0xFFE65100),
                                                Color(0xFFBF360C)
                                            )
                                        )
                                    )
                                    .padding(vertical = 8.dp, horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "❯❯",
                                        color = Color(0xFFFFE082),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "125 DAYS WORK",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 2.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "❮❮",
                                        color = Color(0xFFFFE082),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "— Towards Sustainable Rural Transformation —",
                                fontSize = 10.sp,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF795548)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Central Art & Ground Showcase
                            RuralShowcaseSection(
                                saffronColor = saffronPrimary,
                                leafGreen = leafGreen,
                                navyBlue = navyBlue
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Five Governance Pillars Row / Grid
                            Text(
                                text = "CORE PILLARS OF EXCELLENCE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = saffronPrimary,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            GovernancePillarsRow()

                            Spacer(modifier = Modifier.height(14.dp))

                            // Footer Report Attribution
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(cardBg, RoundedCornerShape(10.dp))
                                    .border(1.dp, Color(0xFFFFE0B2), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "A Report on",
                                        fontSize = 12.sp,
                                        fontStyle = FontStyle.Italic,
                                        fontFamily = FontFamily.Serif,
                                        color = Color(0xFF8D6E63)
                                    )
                                    Text(
                                        text = "RURAL DEVELOPMENT & GOOD GOVERNANCE",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF4E342E),
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                Text(
                                    text = "DISTRICT BIRBHUM\nWEST BENGAL",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = saffronPrimary,
                                    textAlign = TextAlign.End,
                                    lineHeight = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Enter Dashboard Now CTA Button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(4.dp, RoundedCornerShape(14.dp))
                    .testTag("enter_dashboard_button"),
                colors = ButtonDefaults.buttonColors(containerColor = saffronPrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Dashboard,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "ENTER DASHBOARD REPORTS",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Auto-dismissing after 15 seconds • Tap button to enter immediately",
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Draws the rural landscape illustration with workers, sunset, and 3 ground photo cards
 */
@Composable
private fun RuralShowcaseSection(
    saffronColor: Color,
    leafGreen: Color,
    navyBlue: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Landscape & Silhouette Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.5.dp, Color(0xFFFFCC80))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Background Sunset Sky & Horizon Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawSunsetLandscape(saffronColor)
                }

                // Foreground Chakra & Sprout Seal
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(2.dp, saffronColor),
                        shadowElevation = 4.dp,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_ashoka_chakra),
                                contentDescription = "Ashoka Chakra",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "BIRBHUM VB-G RAM G",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF3E2723)
                        )
                        Text(
                            text = "Sustainable Water & Land Works",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = saffronColor
                        )
                    }
                }
            }
        }

        // 3 Showcase Cards for Real Ground Works
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GroundWorkCard(
                title = "Water Security",
                subtitle = "Ponds & Harvesting",
                tag = "1,693 Works",
                icon = Icons.Default.WaterDrop,
                accentColor = Color(0xFF0288D1),
                modifier = Modifier.weight(1f)
            )

            GroundWorkCard(
                title = "Connectivity",
                subtitle = "Rural Pathways",
                tag = "Quality Infra",
                icon = Icons.Default.DirectionsWalk,
                accentColor = Color(0xFF388E3C),
                modifier = Modifier.weight(1f)
            )

            GroundWorkCard(
                title = "Afforestation",
                subtitle = "Plantation Works",
                tag = "1,217 Plantations",
                icon = Icons.Default.Forest,
                accentColor = Color(0xFF689F38),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun GroundWorkCard(
    title: String,
    subtitle: String,
    tag: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBF5)),
        border = BorderStroke(1.dp, Color(0xFFFFE0B2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.12f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E2723),
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                fontSize = 7.5.sp,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = accentColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = tag,
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Five Governance Pillars
 */
@Composable
private fun GovernancePillarsRow() {
    val pillars = listOf(
        PillarItem(
            icon = Icons.Default.Groups,
            title = "Empowering People",
            desc = "Strengthening Rural Livelihoods"
        ),
        PillarItem(
            icon = Icons.Default.FactCheck,
            title = "Timely Works",
            desc = "Quality Execution On Ground"
        ),
        PillarItem(
            icon = Icons.Default.Eco,
            title = "Sustainable Dev",
            desc = "Building Resilient Communities"
        ),
        PillarItem(
            icon = Icons.Default.TrendingUp,
            title = "Data Governance",
            desc = "Real Time Monitoring Real Impact"
        ),
        PillarItem(
            icon = Icons.Default.Handshake,
            title = "Jan Bhagidari",
            desc = "People's Participation & Progress"
        )
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // First row of 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            pillars.take(3).forEach { pillar ->
                PillarBadge(pillar = pillar, modifier = Modifier.weight(1f))
            }
        }
        // Second row of 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            pillars.drop(3).forEach { pillar ->
                PillarBadge(pillar = pillar, modifier = Modifier.weight(1f))
            }
        }
    }
}

private data class PillarItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val desc: String
)

@Composable
private fun PillarBadge(
    pillar: PillarItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBF6)),
        border = BorderStroke(0.8.dp, Color(0xFFFFE0B2))
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = pillar.icon,
                contentDescription = null,
                tint = Color(0xFFE65100),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = pillar.title,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E1C14),
                textAlign = TextAlign.Center
            )
            Text(
                text = pillar.desc,
                fontSize = 7.sp,
                color = Color(0xFF6D4C41),
                textAlign = TextAlign.Center,
                lineHeight = 9.sp,
                maxLines = 2
            )
        }
    }
}

/**
 * Custom Canvas drawing for the stylized 'G' enclosing a green leaf
 */
private fun DrawScope.drawStylizedG(
    saffron: Color,
    leafColor: Color
) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val radius = size.minDimension * 0.42f
    val strokeW = size.minDimension * 0.16f

    // Draw circular G arc in saffron (approx 300 degrees from bottom up around)
    drawArc(
        color = saffron,
        startAngle = 40f,
        sweepAngle = 290f,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2),
        style = Stroke(width = strokeW)
    )

    // Inward horizontal bar of G
    drawLine(
        color = saffron,
        start = Offset(center.x + radius, center.y),
        end = Offset(center.x, center.y),
        strokeWidth = strokeW
    )

    // Center Leaf shape
    val leafPath = Path().apply {
        moveTo(center.x - 2f, center.y + 4f)
        cubicTo(
            center.x - 8f, center.y - 4f,
            center.x - 2f, center.y - 12f,
            center.x + 2f, center.y - 14f
        )
        cubicTo(
            center.x + 8f, center.y - 8f,
            center.x + 6f, center.y + 2f,
            center.x - 2f, center.y + 4f
        )
        close()
    }
    drawPath(path = leafPath, color = leafColor)
}

/**
 * Draws sunset silhouette of workers and landscape
 */
private fun DrawScope.drawSunsetLandscape(saffronColor: Color) {
    // Sky gradient
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFCC80),
                Color(0xFFFFE0B2),
                Color(0xFFFFF3E0)
            )
        )
    )

    // Golden Sun
    drawCircle(
        color = Color(0xFFFFB74D),
        radius = size.height * 0.28f,
        center = Offset(size.width * 0.25f, size.height * 0.35f)
    )

    // Rolling Hills
    val hillPath = Path().apply {
        moveTo(0f, size.height * 0.70f)
        quadraticTo(
            size.width * 0.25f, size.height * 0.55f,
            size.width * 0.55f, size.height * 0.68f
        )
        quadraticTo(
            size.width * 0.8f, size.height * 0.78f,
            size.width, size.height * 0.65f
        )
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
    }
    drawPath(path = hillPath, color = Color(0xFFD84315).copy(alpha = 0.55f))

    // Foreground Dark Soil Line
    val groundPath = Path().apply {
        moveTo(0f, size.height * 0.82f)
        quadraticTo(
            size.width * 0.5f, size.height * 0.75f,
            size.width, size.height * 0.84f
        )
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
    }
    drawPath(path = groundPath, color = Color(0xFF4E342E))

    // Worker Silhouettes (Stylized sticks & tools)
    val silhouetteColor = Color(0xFF2E1C14)

    // Worker 1 (walking with spade)
    drawCircle(silhouetteColor, 3.5f, Offset(size.width * 0.52f, size.height * 0.68f))
    drawLine(silhouetteColor, Offset(size.width * 0.52f, size.height * 0.71f), Offset(size.width * 0.52f, size.height * 0.80f), 2.5f)
    drawLine(silhouetteColor, Offset(size.width * 0.52f, size.height * 0.80f), Offset(size.width * 0.50f, size.height * 0.86f), 2f)
    drawLine(silhouetteColor, Offset(size.width * 0.52f, size.height * 0.80f), Offset(size.width * 0.54f, size.height * 0.86f), 2f)
    // spade handle over shoulder
    drawLine(silhouetteColor, Offset(size.width * 0.48f, size.height * 0.67f), Offset(size.width * 0.56f, size.height * 0.75f), 1.5f)

    // Worker 2 (carrying basket on head)
    drawCircle(silhouetteColor, 3.5f, Offset(size.width * 0.62f, size.height * 0.67f))
    // basket
    drawArc(silhouetteColor, 0f, 180f, true, Offset(size.width * 0.59f, size.height * 0.61f), Size(18f, 10f))
    drawLine(silhouetteColor, Offset(size.width * 0.62f, size.height * 0.70f), Offset(size.width * 0.62f, size.height * 0.80f), 2.5f)
    drawLine(silhouetteColor, Offset(size.width * 0.62f, size.height * 0.80f), Offset(size.width * 0.60f, size.height * 0.86f), 2f)
    drawLine(silhouetteColor, Offset(size.width * 0.62f, size.height * 0.80f), Offset(size.width * 0.64f, size.height * 0.86f), 2f)

    // Birds in sky
    drawBird(Offset(size.width * 0.35f, size.height * 0.25f), silhouetteColor)
    drawBird(Offset(size.width * 0.40f, size.height * 0.20f), silhouetteColor)
    drawBird(Offset(size.width * 0.44f, size.height * 0.27f), silhouetteColor)
}

private fun DrawScope.drawBird(pos: Offset, color: Color) {
    val wingPath = Path().apply {
        moveTo(pos.x - 6f, pos.y + 2f)
        quadraticTo(pos.x - 3f, pos.y - 4f, pos.x, pos.y)
        quadraticTo(pos.x + 3f, pos.y - 4f, pos.x + 6f, pos.y + 2f)
    }
    drawPath(path = wingPath, color = color, style = Stroke(width = 1.2f))
}
