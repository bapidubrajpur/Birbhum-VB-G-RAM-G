package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.BirbhumData
import com.example.data.models.BlockData
import com.example.data.repository.BirbhumRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class DashboardTab(val title: String, val subtitle: String) {
    OVERVIEW("Overview", "District Executive KPI Summary"),
    CALENDAR("PD Calendar", "Daily Persondays & Attendance Heatmap"),
    ALLOTMENTS("Allotments", "State Allotment (GID 263874047)"),
    WORKS("Works", "Sanctions & Progress Directory"),
    FTOS("Material FTOs", "BLT Verification & Audit"),
    EKYC("e-KYC & Targets", "Worker Auth & Special Drive")
}

enum class BlockSortOption(val label: String) {
    SL_NO("Original Sheet Order"),
    ALLOTMENT_HIGH("Highest Distributable (₹)"),
    EXPENDITURE_HIGH("Highest Expenditure (₹)"),
    UTILIZATION_HIGH("Highest Utilization %"),
    PERSONDAYS_HIGH("Highest Persondays"),
    ALLOTMENT_LOW("Lowest Distributable (₹)"),
    WORKS_TS("Highest Technical Sanction %"),
    EKYC_HIGH("Highest e-KYC %"),
    ALPHABETICAL("Block Name (A-Z)")
}

class BirbhumViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BirbhumRepository(application)

    val birbhumData: StateFlow<BirbhumData> = repository.birbhumData
    val isSyncing: StateFlow<Boolean> = repository.isSyncing
    val syncStatus: StateFlow<String?> = repository.syncStatus
    val lastSyncTime: StateFlow<Long> = repository.lastSyncTime

    private val _selectedTab = MutableStateFlow(DashboardTab.OVERVIEW)
    val selectedTab: StateFlow<DashboardTab> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOption = MutableStateFlow(BlockSortOption.SL_NO)
    val sortOption: StateFlow<BlockSortOption> = _sortOption.asStateFlow()

    private val _selectedBlock = MutableStateFlow<BlockData?>(null)
    val selectedBlock: StateFlow<BlockData?> = _selectedBlock.asStateFlow()

    // Calendar states
    private val _selectedCalendarMonth = MutableStateFlow(10) // 1..12 (defaults to October)
    val selectedCalendarMonth: StateFlow<Int> = _selectedCalendarMonth.asStateFlow()

    private val _selectedCalendarYear = MutableStateFlow(2026)
    val selectedCalendarYear: StateFlow<Int> = _selectedCalendarYear.asStateFlow()

    private val _selectedCalendarDate = MutableStateFlow<com.example.data.models.DailyPersondayRecord?>(null)
    val selectedCalendarDate: StateFlow<com.example.data.models.DailyPersondayRecord?> = _selectedCalendarDate.asStateFlow()

    private val prefs = application.getSharedPreferences("birbhum_prefs", android.content.Context.MODE_PRIVATE)

    private val _customLogoUrl = MutableStateFlow<String?>(prefs.getString("custom_logo_url", null))
    val customLogoUrl: StateFlow<String?> = _customLogoUrl.asStateFlow()

    fun updateCustomLogoUrl(url: String?) {
        val clean = url?.trim()?.takeIf { it.isNotBlank() }
        _customLogoUrl.value = clean
        prefs.edit().apply {
            if (clean != null) putString("custom_logo_url", clean)
            else remove("custom_logo_url")
            apply()
        }
    }

    private val _isLogoVisible = MutableStateFlow(prefs.getBoolean("is_logo_visible", true))
    val isLogoVisible: StateFlow<Boolean> = _isLogoVisible.asStateFlow()

    fun updateLogoVisibility(visible: Boolean) {
        _isLogoVisible.value = visible
        prefs.edit().putBoolean("is_logo_visible", visible).apply()
    }

    private val _selectedDateBlockBreakdown = MutableStateFlow<List<com.example.data.models.DateBlockPersonday>>(emptyList())
    val selectedDateBlockBreakdown: StateFlow<List<com.example.data.models.DateBlockPersonday>> = _selectedDateBlockBreakdown.asStateFlow()

    private val _isLoadingBreakdown = MutableStateFlow(false)
    val isLoadingBreakdown: StateFlow<Boolean> = _isLoadingBreakdown.asStateFlow()

    // Auto-update engine states
    private val _isAutoUpdateEnabled = MutableStateFlow(prefs.getBoolean("auto_update_enabled", true))
    val isAutoUpdateEnabled: StateFlow<Boolean> = _isAutoUpdateEnabled.asStateFlow()

    private val _autoUpdateInterval = MutableStateFlow(prefs.getInt("auto_update_interval", 30))
    val autoUpdateInterval: StateFlow<Int> = _autoUpdateInterval.asStateFlow()

    private val _countdownSeconds = MutableStateFlow(_autoUpdateInterval.value)
    val countdownSeconds: StateFlow<Int> = _countdownSeconds.asStateFlow()

    private val _lastAutoUpdateMessage = MutableStateFlow<String?>("Live auto-update active")
    val lastAutoUpdateMessage: StateFlow<String?> = _lastAutoUpdateMessage.asStateFlow()

    val featureUpdatesDoneList: List<com.example.data.models.FeatureUpdateItem> = listOf(
        com.example.data.models.FeatureUpdateItem(
            id = "ts_as_2_decimals",
            title = "Technical Sanction (TS) & Admin Sanction (AS) Decimal Precision",
            description = "Enforced Technical Sanction (TS) and Administrative Sanction (AS) percentages and figures across Executive Overview, Works Progress Directory, and Block dossiers to be strictly formatted up to 2 decimal places only.",
            category = "Sanctions & Governance",
            dateCompleted = "21 Oct 2026",
            isDone = true
        ),
        com.example.data.models.FeatureUpdateItem(
            id = "block_report_search_bar",
            title = "Block-wise Report Table Search Bar & Gram Panchayat Status Finding",
            description = "Integrated prominent search bar at the top of the block-wise report table to filter by block name or Gram Panchayat name, one-tap GP quick chips, live GP status column (Best/Poor GP), interactive GP status dialog, and shareable reports.",
            category = "Search & Governance",
            dateCompleted = "21 Oct 2026",
            isDone = true
        ),
        com.example.data.models.FeatureUpdateItem(
            id = "sharable_message_gp_alert",
            title = "Comprehensive Sharable Messages & GP Performance Alert",
            description = "Sharable reports include Person-days Generated, NMMS Attendance, and Sector-wise Works, alongside prominent Best and Poor Performing Gram Panchayat alerts with quick-share actions.",
            category = "Reporting & Alerts",
            dateCompleted = "20 Oct 2026",
            isDone = true
        ),
        com.example.data.models.FeatureUpdateItem(
            id = "allocation_fund_2_decimal",
            title = "Allocation of Fund Precision (Up to 2 Decimals Only)",
            description = "Enforced financial accuracy across Executive Overview, Allotment Register, Block dossiers, and Category Heads to be strictly formatted up to 2 decimal places.",
            category = "Financial Governance",
            dateCompleted = "20 Oct 2026",
            isDone = true
        ),
        com.example.data.models.FeatureUpdateItem(
            id = "pba_grgc_live_sync",
            title = "Paschim Banga Awaas (PBA) GRGC Live Sync & Block Report",
            description = "Live sync from GID 2140000000 with Executive Overview KPI and Block-wise Report under Works (Table Top Done, Verified OK, Work Codes in VB-G RAM G Soft, e-MR Register R6.9).",
            category = "PBA & Works Integration",
            dateCompleted = "20 Oct 2026",
            isDone = true
        ),
        com.example.data.models.FeatureUpdateItem(
            id = "landing_15s",
            title = "15-Second Landing Page & Report Transition",
            description = "Faithful high-fidelity official Viksit Bharat 2047 Birbhum landing cover with 15s timer, skip to reports, and top bar re-entry.",
            category = "UI / Landing Experience",
            dateCompleted = "19 Oct 2026",
            isDone = true
        ),
        com.example.data.models.FeatureUpdateItem(
            id = "allotment_vs_expenditure_chart",
            title = "State Allotment vs. Actual Expenditure & Persondays",
            description = "Visual financial progress tracking comparing State Allotment (GID 263874047) with Scheme Bank Actual Expenditure & Persondays (GID 6823442) per block.",
            category = "Financial Analytics",
            dateCompleted = "21 Oct 2026",
            isDone = true
        ),
        com.example.data.models.FeatureUpdateItem(
            id = "auto_update_engine",
            title = "Automated Live Data & Feature Synchronization",
            description = "Auto-updates live data from all 7 Google Sheets in the background with configurable countdown (15s, 30s, 60s, 120s).",
            category = "Core Real-Time Engine",
            dateCompleted = "19 Oct 2026",
            isDone = true
        ),
        com.example.data.models.FeatureUpdateItem(
            id = "full_7_sheets_sync",
            title = "All 7 Google Sheets Live Integration",
            description = "Live sync for Allotment (263874047), Works (1115948623), FTOs (1996508129), NMMS (1613697542), e-KYC (187896883), Targets (232026717), and Categories (6823442).",
            category = "Cloud Data & Sync",
            dateCompleted = "19 Oct 2026",
            isDone = true
        ),
        com.example.data.models.FeatureUpdateItem(
            id = "pd_calendar_heatmap",
            title = "Daily Persondays Calendar & Attendance Heatmap",
            description = "Day-by-day attendance tracking, interactive month navigation, and live block breakdown drilldowns.",
            category = "Reporting & Analytics",
            dateCompleted = "18 Oct 2026",
            isDone = true
        ),
        com.example.data.models.FeatureUpdateItem(
            id = "water_security_kpi",
            title = "Water Security & Category Matrix Works",
            description = "Restoration of water bodies, micro irrigation, and non-plantation works analysis per Gram Panchayat.",
            category = "Domain Analytics",
            dateCompleted = "18 Oct 2026",
            isDone = true
        ),
        com.example.data.models.FeatureUpdateItem(
            id = "material_fto_audit",
            title = "Material FTO BLT Audit Verification",
            description = "100% verified material FTO tracking with approved, accepted, and rejected payment audit figures.",
            category = "Financial Audit",
            dateCompleted = "17 Oct 2026",
            isDone = true
        ),
        com.example.data.models.FeatureUpdateItem(
            id = "ekyc_abps_saturation",
            title = "Worker e-KYC & ABPS Saturation Drive",
            description = "Aadhaar seeding, biometric authentication success rates, and block saturation comparisons.",
            category = "Worker Saturation",
            dateCompleted = "17 Oct 2026",
            isDone = true
        ),
        com.example.data.models.FeatureUpdateItem(
            id = "custom_emblem_logo",
            title = "District Crest & Saffron Brand Customizer",
            description = "Toggle between official Birbhum administration crest, custom crest URL, or bold saffron typography badge.",
            category = "Personalization",
            dateCompleted = "17 Oct 2026",
            isDone = true
        )
    )

    fun toggleAutoUpdate(enabled: Boolean) {
        _isAutoUpdateEnabled.value = enabled
        prefs.edit().putBoolean("auto_update_enabled", enabled).apply()
        if (enabled) {
            _countdownSeconds.value = _autoUpdateInterval.value
        }
    }

    fun setAutoUpdateInterval(seconds: Int) {
        val valid = if (seconds in listOf(15, 30, 60, 120)) seconds else 30
        _autoUpdateInterval.value = valid
        _countdownSeconds.value = valid
        prefs.edit().putInt("auto_update_interval", valid).apply()
    }

    init {
        // 1. Immediately trigger live sync on app launch to load fresh sheet data
        viewModelScope.launch {
            try {
                repository.syncLiveData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 2. Continuous real-time countdown & auto-update loop
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000L) // 1 second ticker
                if (_isAutoUpdateEnabled.value) {
                    val currentCount = _countdownSeconds.value
                    if (currentCount <= 1) {
                        _countdownSeconds.value = _autoUpdateInterval.value
                        try {
                            repository.syncLiveData()
                            _lastAutoUpdateMessage.value = "Updated at ${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US).format(java.util.Date())}"
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        _countdownSeconds.value = currentCount - 1
                    }
                }
            }
        }

        // 3. Automatically set initial selected date to latest available
        viewModelScope.launch {
            birbhumData.collect { data ->
                val latest = data.nmmsSummary.dailyPersondays.firstOrNull()
                    ?: data.nmmsSummary.dailyPersondays.lastOrNull()
                if (latest != null && _selectedCalendarDate.value == null) {
                    _selectedCalendarDate.value = latest
                    _selectedCalendarMonth.value = latest.month
                    _selectedCalendarYear.value = latest.year
                    if (latest.blockBreakdown.isNotEmpty()) {
                        _selectedDateBlockBreakdown.value = latest.blockBreakdown
                    }
                }
            }
        }
    }

    // Filtered & Sorted blocks
    val filteredBlocks: StateFlow<List<BlockData>> = combine(
        birbhumData,
        _searchQuery,
        _sortOption
    ) { data, query, sort ->
        val q = query.trim().lowercase()
        val list = if (q.isEmpty()) {
            data.blocks
        } else {
            data.blocks.filter { it.blockName.lowercase().contains(q) }
        }

        when (sort) {
            BlockSortOption.SL_NO -> list.sortedBy { it.slNo }
            BlockSortOption.ALLOTMENT_HIGH -> list.sortedByDescending { it.allotment.totalLakh }
            BlockSortOption.EXPENDITURE_HIGH -> list.sortedByDescending { it.expenditure.bookedExpenditureLakh }
            BlockSortOption.UTILIZATION_HIGH -> list.sortedByDescending { it.expenditure.utilizationPct(it.allotment.totalLakh) }
            BlockSortOption.PERSONDAYS_HIGH -> list.sortedByDescending { it.expenditure.persondaysGenerated }
            BlockSortOption.ALLOTMENT_LOW -> list.sortedBy { it.allotment.totalLakh }
            BlockSortOption.WORKS_TS -> list.sortedByDescending { it.works.tsPct }
            BlockSortOption.EKYC_HIGH -> list.sortedByDescending { it.ekyc.ekycPct }
            BlockSortOption.ALPHABETICAL -> list.sortedBy { it.blockName }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: DashboardTab) {
        _selectedTab.value = tab
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSortOption(option: BlockSortOption) {
        _sortOption.value = option
    }

    fun selectBlock(block: BlockData?) {
        _selectedBlock.value = block
    }

    fun selectCalendarMonth(month: Int, year: Int) {
        _selectedCalendarMonth.value = month
        _selectedCalendarYear.value = year
        // Pick the latest day or first day with data in this month
        val recordsInMonth = birbhumData.value.nmmsSummary.dailyPersondays.filter { it.month == month && it.year == year }
        val target = recordsInMonth.lastOrNull()
        if (target != null) {
            selectCalendarDate(target)
        }
    }

    fun selectCalendarDate(record: com.example.data.models.DailyPersondayRecord) {
        _selectedCalendarDate.value = record
        _selectedCalendarMonth.value = record.month
        _selectedCalendarYear.value = record.year

        if (record.blockBreakdown.isNotEmpty()) {
            _selectedDateBlockBreakdown.value = record.blockBreakdown
        } else {
            // Fetch live on-demand from repository
            viewModelScope.launch {
                _isLoadingBreakdown.value = true
                val breakdown = repository.fetchDateBlockBreakdown(record.dateIso)
                _selectedDateBlockBreakdown.value = breakdown
                _isLoadingBreakdown.value = false
            }
        }
    }

    fun syncLiveData() {
        viewModelScope.launch {
            repository.syncLiveData()
            // Re-select latest date after sync
            val latest = birbhumData.value.nmmsSummary.dailyPersondays.firstOrNull()
                ?: birbhumData.value.nmmsSummary.dailyPersondays.lastOrNull()
            if (latest != null) {
                _selectedCalendarDate.value = latest
                if (latest.blockBreakdown.isNotEmpty()) {
                    _selectedDateBlockBreakdown.value = latest.blockBreakdown
                }
            }
        }
    }
}
