package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BlockDetailSheet
import com.example.ui.components.TopAppBarSection
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BirbhumViewModel
import com.example.ui.viewmodel.DashboardTab

import androidx.compose.animation.core.tween
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.ui.screens.LandingPageScreen

class MainActivity : ComponentActivity() {

    private val viewModel: BirbhumViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                var showLandingPage by rememberSaveable { mutableStateOf(true) }

                AnimatedContent(
                    targetState = showLandingPage,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                    },
                    label = "landing_transition"
                ) { isLanding ->
                    if (isLanding) {
                        LandingPageScreen(
                            onDismiss = { showLandingPage = false }
                        )
                    } else {
                        BirbhumDashboardApp(
                            viewModel = viewModel,
                            onShowLandingPage = { showLandingPage = true }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BirbhumDashboardApp(
    viewModel: BirbhumViewModel,
    onShowLandingPage: () -> Unit = {}
) {
    val birbhumData by viewModel.birbhumData.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val filteredBlocks by viewModel.filteredBlocks.collectAsStateWithLifecycle()
    val selectedBlock by viewModel.selectedBlock.collectAsStateWithLifecycle()

    val selectedCalendarMonth by viewModel.selectedCalendarMonth.collectAsStateWithLifecycle()
    val selectedCalendarYear by viewModel.selectedCalendarYear.collectAsStateWithLifecycle()
    val selectedCalendarDate by viewModel.selectedCalendarDate.collectAsStateWithLifecycle()
    val selectedDateBlockBreakdown by viewModel.selectedDateBlockBreakdown.collectAsStateWithLifecycle()
    val isLoadingBreakdown by viewModel.isLoadingBreakdown.collectAsStateWithLifecycle()
    val customLogoUrl by viewModel.customLogoUrl.collectAsStateWithLifecycle()
    val isLogoVisible by viewModel.isLogoVisible.collectAsStateWithLifecycle()
    val isAutoUpdateEnabled by viewModel.isAutoUpdateEnabled.collectAsStateWithLifecycle()
    val autoUpdateInterval by viewModel.autoUpdateInterval.collectAsStateWithLifecycle()
    val countdownSeconds by viewModel.countdownSeconds.collectAsStateWithLifecycle()
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()

    // Handle back button: if on sub-tab or block selected, return to previous state
    BackHandler(enabled = selectedBlock != null || selectedTab != DashboardTab.OVERVIEW) {
        if (selectedBlock != null) {
            viewModel.selectBlock(null)
        } else if (selectedTab != DashboardTab.OVERVIEW) {
            viewModel.selectTab(DashboardTab.OVERVIEW)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("birbhum_dashboard_scaffold"),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBarSection(
                sheetUrl = birbhumData.sheetUrl,
                lastUpdated = birbhumData.lastUpdated,
                isSyncing = isSyncing,
                customLogoUrl = customLogoUrl,
                isLogoVisible = isLogoVisible,
                isAutoUpdateEnabled = isAutoUpdateEnabled,
                autoUpdateInterval = autoUpdateInterval,
                countdownSeconds = countdownSeconds,
                syncStatus = syncStatus,
                featureUpdates = viewModel.featureUpdatesDoneList,
                onToggleLogoVisibility = { viewModel.updateLogoVisibility(it) },
                onUpdateCustomLogoUrl = { viewModel.updateCustomLogoUrl(it) },
                onShowLandingPage = onShowLandingPage,
                onToggleAutoUpdate = { viewModel.toggleAutoUpdate(it) },
                onSetAutoUpdateInterval = { viewModel.setAutoUpdateInterval(it) },
                onSyncClick = { viewModel.syncLiveData() }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar"),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == DashboardTab.OVERVIEW,
                    onClick = { viewModel.selectTab(DashboardTab.OVERVIEW) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == DashboardTab.OVERVIEW) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                            contentDescription = "Overview"
                        )
                    },
                    label = { Text("Overview", fontSize = 11.sp, fontWeight = if (selectedTab == DashboardTab.OVERVIEW) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_overview")
                )

                NavigationBarItem(
                    selected = selectedTab == DashboardTab.CALENDAR,
                    onClick = { viewModel.selectTab(DashboardTab.CALENDAR) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == DashboardTab.CALENDAR) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                            contentDescription = "PD Calendar"
                        )
                    },
                    label = { Text("PD Cal", fontSize = 11.sp, fontWeight = if (selectedTab == DashboardTab.CALENDAR) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_calendar")
                )

                NavigationBarItem(
                    selected = selectedTab == DashboardTab.ALLOTMENTS,
                    onClick = { viewModel.selectTab(DashboardTab.ALLOTMENTS) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == DashboardTab.ALLOTMENTS) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                            contentDescription = "Allotments"
                        )
                    },
                    label = { Text("Allotments", fontSize = 11.sp, fontWeight = if (selectedTab == DashboardTab.ALLOTMENTS) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_allotments")
                )

                NavigationBarItem(
                    selected = selectedTab == DashboardTab.WORKS,
                    onClick = { viewModel.selectTab(DashboardTab.WORKS) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == DashboardTab.WORKS) Icons.Filled.Engineering else Icons.Outlined.Engineering,
                            contentDescription = "Works"
                        )
                    },
                    label = { Text("Works", fontSize = 11.sp, fontWeight = if (selectedTab == DashboardTab.WORKS) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_works")
                )

                NavigationBarItem(
                    selected = selectedTab == DashboardTab.FTOS,
                    onClick = { viewModel.selectTab(DashboardTab.FTOS) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == DashboardTab.FTOS) Icons.Filled.FactCheck else Icons.Outlined.FactCheck,
                            contentDescription = "FTOs"
                        )
                    },
                    label = { Text("FTOs", fontSize = 11.sp, fontWeight = if (selectedTab == DashboardTab.FTOS) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_ftos")
                )

                NavigationBarItem(
                    selected = selectedTab == DashboardTab.EKYC,
                    onClick = { viewModel.selectTab(DashboardTab.EKYC) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == DashboardTab.EKYC) Icons.Filled.Groups else Icons.Outlined.Groups,
                            contentDescription = "e-KYC"
                        )
                    },
                    label = { Text("e-KYC", fontSize = 11.sp, fontWeight = if (selectedTab == DashboardTab.EKYC) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_ekyc")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_content_transition"
            ) { tab ->
                when (tab) {
                    DashboardTab.OVERVIEW -> {
                        OverviewScreen(
                            data = birbhumData,
                            onNavigateToAllotments = { viewModel.selectTab(DashboardTab.ALLOTMENTS) },
                            onNavigateToWorks = { viewModel.selectTab(DashboardTab.WORKS) },
                            onNavigateToFto = { viewModel.selectTab(DashboardTab.FTOS) },
                            onNavigateToEkyc = { viewModel.selectTab(DashboardTab.EKYC) },
                            onNavigateToCalendar = { viewModel.selectTab(DashboardTab.CALENDAR) },
                            onNavigateToPbaWorks = { viewModel.selectTab(DashboardTab.WORKS) },
                            onSelectBlock = { block -> viewModel.selectBlock(block) }
                        )
                    }
                    DashboardTab.CALENDAR -> {
                        PdCalendarScreen(
                            data = birbhumData,
                            selectedMonth = selectedCalendarMonth,
                            selectedYear = selectedCalendarYear,
                            selectedDate = selectedCalendarDate,
                            selectedBlockBreakdown = selectedDateBlockBreakdown,
                            isLoadingBreakdown = isLoadingBreakdown,
                            isSyncing = isSyncing,
                            onSelectMonth = { m, y -> viewModel.selectCalendarMonth(m, y) },
                            onSelectDate = { rec -> viewModel.selectCalendarDate(rec) },
                            onSyncClick = { viewModel.syncLiveData() }
                        )
                    }
                    DashboardTab.ALLOTMENTS -> {
                        AllotmentScreen(
                            data = birbhumData,
                            filteredBlocks = filteredBlocks,
                            searchQuery = searchQuery,
                            sortOption = sortOption,
                            onSearchChange = { viewModel.updateSearchQuery(it) },
                            onSortChange = { viewModel.updateSortOption(it) },
                            onSelectBlock = { block -> viewModel.selectBlock(block) }
                        )
                    }
                    DashboardTab.WORKS -> {
                        WorksProgressScreen(
                            data = birbhumData,
                            onSelectBlock = { block -> viewModel.selectBlock(block) }
                        )
                    }
                    DashboardTab.FTOS -> {
                        FtoScreen(
                            data = birbhumData,
                            onSelectBlock = { block -> viewModel.selectBlock(block) }
                        )
                    }
                    DashboardTab.EKYC -> {
                        EkycTargetScreen(
                            data = birbhumData,
                            onSelectBlock = { block -> viewModel.selectBlock(block) }
                        )
                    }
                }
            }

            // Modal Bottom Sheet when a block is selected
            selectedBlock?.let { block ->
                BlockDetailSheet(
                    block = block,
                    onDismiss = { viewModel.selectBlock(null) }
                )
            }
        }
    }
}
