package com.example.data.models

data class BirbhumData(
    val district: String = "BIRBHUM",
    val state: String = "WEST BENGAL",
    val schemeTitle: String = "VB-G RAM G Monitoring Portal",
    val sheetUrl: String = "https://docs.google.com/spreadsheets/d/1Hs2CJaEV6GfdQsMEcjOSvLeH72UeYkgmf-yh_-EjXlI/edit?gid=263874047#gid=263874047",
    val lastUpdated: String = "October 2026",
    val nmmsSummary: NmmsSummary = NmmsSummary(),
    val categoryMatrixSummary: CategoryMatrixSummary = CategoryMatrixSummary(),
    val allotmentSummary: AllotmentSummary = AllotmentSummary(),
    val worksSummary: WorksSummary = WorksSummary(),
    val ftoSummary: FtoSummary = FtoSummary(),
    val ekycSummary: EkycSummary = EkycSummary(),
    val targetSummary: TargetSummary = TargetSummary(),
    val pbaSummary: PbaSummary = PbaSummary(),
    val blocks: List<BlockData> = emptyList()
) {
    val totalExpenditurePersondays: Int
        get() = blocks.sumOf { it.expenditure.persondaysGenerated.takeIf { pd -> pd > 0 } ?: it.persondaysGenerated }
    val totalSanctionLakh: Double
        get() = Math.round(blocks.sumOf { it.expenditure.sanctionAmountLakh } * 100.0) / 100.0
    val totalBookedExpLakh: Double
        get() = Math.round(blocks.sumOf { it.expenditure.bookedExpenditureLakh } * 100.0) / 100.0
    val totalPaidExpLakh: Double
        get() = Math.round(blocks.sumOf { it.expenditure.paidExpenditureLakh } * 100.0) / 100.0
    val overallFinancialUtilizationPct: Double
        get() = if (allotmentSummary.distributableLakh > 0.0) (totalBookedExpLakh / allotmentSummary.distributableLakh * 100.0) else 0.0
}

data class NmmsSummary(
    val totalMusters: Int = 30738,
    val totalAttendance: Int = 146358,
    val todayAttendance: Int = 884,
    val todayMusters: Int = 169,
    val date: String = "17 Oct 2026",
    val latestDateIso: String = "2026-10-17",
    val blockPerformance: List<NmmsBlockPoint> = emptyList(),
    val recentMusters: List<RecentMuster> = emptyList(),
    val dailyPersondays: List<DailyPersondayRecord> = emptyList()
)

data class DailyPersondayRecord(
    val dateIso: String = "",
    val displayDate: String = "",
    val year: Int = 2026,
    val month: Int = 10,
    val day: Int = 1,
    val persondays: Int = 0,
    val musters: Int = 0,
    val blockBreakdown: List<DateBlockPersonday> = emptyList()
)

data class DateBlockPersonday(
    val blockName: String = "",
    val persondays: Int = 0,
    val musters: Int = 0
)

data class NmmsBlockPoint(
    val block: String = "",
    val attendance: Int = 0,
    val musters: Int = 0,
    val todayAttendance: Int = 0
)

data class RecentMuster(
    val sNo: String = "",
    val block: String = "",
    val gp: String = "",
    val workCode: String = "",
    val mustrollNo: String = "",
    val persondays: Int = 0,
    val date: String = ""
)

data class CategoryMatrixSummary(
    val totalReceivedWorks: Int = 6285,
    val totalApprovedWorks: Int = 3450,
    val totalOngoingWorks: Int = 1693,
    val totalPersondays: Int = 86384,
    val totalNmmsAttendance: Int = 146358,
    val approvalPercent: Double = 54.89,
    val categories: List<SchemeCategoryData> = emptyList()
) {
    val waterSecurityStatus: WaterSecurityStatus
        get() {
            val waterCats = categories.filter {
                it.id in listOf("WATER_BODIES", "WATER_CONSERVATION", "MICRO_IRRIGATION") ||
                it.name.contains("Water", ignoreCase = true) ||
                it.name.contains("Irrigation", ignoreCase = true)
            }
            val ongoing = if (waterCats.isNotEmpty()) waterCats.sumOf { it.ongoingWorks } else 236
            val approved = if (waterCats.isNotEmpty()) waterCats.sumOf { it.approvedWorks } else 607
            val received = if (waterCats.isNotEmpty()) waterCats.sumOf { it.receivedWorks } else 838
            val pd = if (waterCats.isNotEmpty()) waterCats.sumOf { it.persondaysGenerated } else 38073
            val att = if (waterCats.isNotEmpty()) waterCats.sumOf { it.nmmsAttendance } else 64344
            val pct = if (received > 0) (approved * 100.0 / received) else 72.43
            return WaterSecurityStatus(
                totalOngoingWorks = ongoing,
                totalApprovedWorks = approved,
                totalReceivedWorks = received,
                totalPersondays = pd,
                totalNmmsAttendance = att,
                activeGpsCount = 118,
                approvalPercent = pct,
                subcategories = waterCats
            )
        }

    val nonPlantationKpi: NonPlantationKpi
        get() {
            val plantation = categories.find {
                it.id == "DROUGHT" || it.name.contains("Drought", ignoreCase = true) || it.name.contains("Plantation", ignoreCase = true)
            }?.ongoingWorks ?: 1217
            val totalOngoing = if (totalOngoingWorks > 0) totalOngoingWorks else 1693
            val nonPlantation = (totalOngoing - plantation).coerceAtLeast(0)
            val totalGps = 167
            val perGp = if (totalGps > 0) nonPlantation.toDouble() / totalGps else 2.85
            val allPerGp = if (totalGps > 0) totalOngoing.toDouble() / totalGps else 10.14
            val plantPct = if (totalOngoing > 0) (plantation * 100.0 / totalOngoing) else 71.88
            return NonPlantationKpi(
                totalOngoingWorks = nonPlantation,
                totalGps = totalGps,
                ongoingPerGp = perGp,
                totalOngoingAllSectors = totalOngoing,
                allSectorsPerGp = allPerGp,
                plantationOngoingWorks = plantation,
                plantationSharePercent = plantPct,
                nonPlantationSharePercent = 100.0 - plantPct
            )
        }
}

data class WaterSecurityStatus(
    val totalOngoingWorks: Int = 236,
    val totalApprovedWorks: Int = 607,
    val totalReceivedWorks: Int = 838,
    val totalPersondays: Int = 38073,
    val totalNmmsAttendance: Int = 64344,
    val activeGpsCount: Int = 118,
    val approvalPercent: Double = 72.43,
    val subcategories: List<SchemeCategoryData> = emptyList()
)

data class NonPlantationKpi(
    val totalOngoingWorks: Int = 476,
    val totalGps: Int = 167,
    val ongoingPerGp: Double = 2.85,
    val totalOngoingAllSectors: Int = 1693,
    val allSectorsPerGp: Double = 10.14,
    val plantationOngoingWorks: Int = 1217,
    val plantationSharePercent: Double = 71.88,
    val nonPlantationSharePercent: Double = 28.12
)


data class SchemeCategoryData(
    val id: String = "",
    val icon: String = "",
    val name: String = "",
    val receivedWorks: Int = 0,
    val approvedWorks: Int = 0,
    val ongoingWorks: Int = 0,
    val persondaysGenerated: Int = 0,
    val nmmsAttendance: Int = 0,
    val approvedPercent: Double = 0.0
)

data class SectorDrilldown(
    val categoryId: String,
    val categoryName: String,
    val icon: String,
    val activeWorks: Int,
    val activeGpsCount: Int,
    val persondays: Int,
    val gps: List<GpDrilldown> = emptyList()
)

data class GpDrilldown(
    val gpName: String,
    val blockName: String,
    val activeWorks: Int,
    val persondays: Int,
    val works: List<WorkItemDrilldown> = emptyList()
)

data class WorkItemDrilldown(
    val workCode: String,
    val workName: String,
    val categoryName: String,
    val gpName: String,
    val blockName: String,
    val mustrollNo: String,
    val persondays: Int,
    val status: String = "Active / Field Verified"
)

data class AllotmentSummary(
    val totalAllotmentLakh: Double = 4222.0,
    val totalAllotmentCr: Double = 42.22,
    val districtRetentionLakh: Double = 844.40,
    val districtRetentionCr: Double = 8.44,
    val distributableLakh: Double = 3377.60,
    val distributableCr: Double = 33.78,
    val totalCountA: Int = 63055,
    val totalCountB: Int = 63885,
    val totalCount: Int = 126940,
    val categories: List<CategoryDistribution> = emptyList()
)

/**
 * Formats financial figures (Allocation of Fund, Distributable Funds, Retention, Category Heads)
 * and sanction progress metrics (Technical Sanctions TS %, Administrative Sanctions AS %)
 * strictly up to 2 decimal places only (e.g. 10.5 or 10.55, never 10.555).
 */
fun formatFund(amount: Double): String {
    val rounded = Math.round(amount * 100.0) / 100.0
    val df = java.text.DecimalFormat("0.##")
    return df.format(rounded)
}

@JvmName("formatFundExt")
fun Double.formatFund(): String = formatFund(this)

data class CategoryDistribution(
    val id: String = "",
    val name: String = "",
    val allotmentLakh: Double = 0.0,
    val retentionLakh: Double = 0.0,
    val distributableLakh: Double = 0.0,
    val sharePercent: Double = 0.0
)

data class WorksSummary(
    val totalGPs: Int = 167,
    val totalWorks: Int = 5584,
    val estimatesStarted: Int = 3764,
    val technicalSanctions: Int = 2426,
    val adminSanctions: Int = 2393,
    val pendingSanctions: Int = 3158,
    val tsPercent: Double = 43.4,
    val asPercent: Double = 42.9
) {
    val formattedTsPercent: String get() = tsPercent.formatFund()
    val formattedAsPercent: String get() = asPercent.formatFund()
}

data class FtoSummary(
    val memoRef: String = "Memo No. 177/PRD-33011/5/2018-VB-G-RAM-G",
    val totalFtos: Int = 1551,
    val totalPendingAmountRs: Long = 741288403L,
    val checkedFtos: Int = 1520,
    val acceptedAmountRs: Long = 728955581L,
    val rejectedAmountRs: Long = 12332822L,
    val verifiedAmountRs: Long = 741288403L,
    val status: String = "100.00%"
)

data class EkycSummary(
    val totalWorkers: Int = 1230522,
    val aadhaarSeeded: Int = 1227126,
    val ekycDone: Int = 1110591,
    val ekycPercent: Double = 90.5,
    val authenticated: Int = 1204261,
    val authFailed: Int = 22865,
    val abpsEligible: Int = 1100376
)

data class TargetSummary(
    val driveTitle: String = "SPECIAL DRIVE • 18 SEPTEMBER 2026–31 MARCH 2027",
    val totalTargetPersondays: Long = 5060100L,
    val monthlyTargets: Map<String, Long> = emptyMap()
)

data class BlockExpenditure(
    val blockName: String = "",
    val sanctionAmountLakh: Double = 0.0,
    val bookedExpenditureLakh: Double = 0.0,
    val paidExpenditureLakh: Double = 0.0,
    val wagesBookedLakh: Double = 0.0,
    val materialBookedLakh: Double = 0.0,
    val wagesPaidLakh: Double = 0.0,
    val materialPaidLakh: Double = 0.0,
    val persondaysGenerated: Int = 0
) {
    val bookedExpCr: Double get() = Math.round((bookedExpenditureLakh / 100.0) * 100.0) / 100.0
    val paidExpCr: Double get() = Math.round((paidExpenditureLakh / 100.0) * 100.0) / 100.0
    val sanctionCr: Double get() = Math.round((sanctionAmountLakh / 100.0) * 100.0) / 100.0
    fun utilizationPct(allotmentLakh: Double): Double =
        if (allotmentLakh > 0.0) Math.round((bookedExpenditureLakh / allotmentLakh * 100.0) * 100.0) / 100.0 else 0.0
    fun remainingLakh(allotmentLakh: Double): Double =
        Math.round((allotmentLakh - bookedExpenditureLakh).coerceAtLeast(0.0) * 100.0) / 100.0
}

fun getBlockExpenditure(blockName: String): BlockExpenditure {
    val norm = blockName.trim().uppercase()
    return when {
        norm.contains("BOLPUR") -> BlockExpenditure("BOLPUR-SRINIKETAN", 305.59, 9.82, 1.58, 8.24, 1.58, 0.00, 1.58, 4376)
        norm.contains("DUBRAJPUR") -> BlockExpenditure("DUBRAJPUR", 454.33, 23.20, 10.64, 12.56, 10.64, 0.00, 10.64, 9242)
        norm.contains("ILLAMBAZAR") -> BlockExpenditure("ILLAMBAZAR", 350.59, 13.11, 4.06, 9.05, 4.06, 0.00, 4.06, 5045)
        norm.contains("KHOYRASOL") -> BlockExpenditure("KHOYRASOL", 335.74, 21.48, 1.17, 20.31, 1.17, 0.00, 1.17, 10341)
        norm.contains("LABPUR") -> BlockExpenditure("LABPUR", 173.74, 7.54, 1.05, 6.49, 1.05, 0.00, 1.05, 3175)
        norm.contains("MAYURESWAR-I") || (norm.contains("MAYURESWAR") && norm.contains("I") && !norm.contains("II")) ->
            BlockExpenditure("MAYURESWAR-I", 168.10, 9.93, 4.16, 5.77, 4.16, 0.00, 4.16, 4285)
        norm.contains("MAYURESWAR-II") || (norm.contains("MAYURESWAR") && norm.contains("II")) ->
            BlockExpenditure("MAYURESWAR-II", 134.37, 10.21, 1.58, 8.63, 1.58, 0.00, 1.58, 4307)
        norm.contains("MOHAMMAD") || norm.contains("MD") ->
            BlockExpenditure("MOHAMMAD BAZAR", 356.99, 16.56, 12.69, 3.87, 12.69, 0.00, 12.69, 6695)
        norm.contains("MURARAI-I") || (norm.contains("MURARAI") && norm.contains("I") && !norm.contains("II")) ->
            BlockExpenditure("MURARAI-I", 137.57, 3.42, 0.00, 3.42, 0.00, 0.00, 0.00, 1335)
        norm.contains("MURARAI-II") || (norm.contains("MURARAI") && norm.contains("II")) ->
            BlockExpenditure("MURARAI-II", 169.58, 5.89, 0.40, 5.49, 0.40, 0.00, 0.40, 2092)
        norm.contains("NALHATI-I") || (norm.contains("NALHATI") && norm.contains("I") && !norm.contains("II")) ->
            BlockExpenditure("NALHATI-I", 305.94, 21.15, 0.74, 20.41, 0.74, 0.00, 0.74, 7571)
        norm.contains("NALHATI-II") || (norm.contains("NALHATI") && norm.contains("II")) ->
            BlockExpenditure("NALHATI-II", 115.91, 5.60, 3.59, 2.01, 3.59, 0.00, 3.59, 2144)
        norm.contains("NANOOR") -> BlockExpenditure("NANOOR", 247.12, 6.43, 0.62, 5.81, 0.62, 0.00, 0.62, 2906)
        norm.contains("RAJNAGAR") -> BlockExpenditure("RAJNAGAR", 179.11, 10.77, 5.36, 5.41, 5.36, 0.00, 5.36, 4479)
        norm.contains("RAMPURHAT-I") || (norm.contains("RAMPURHAT") && norm.contains("I") && !norm.contains("II")) ->
            BlockExpenditure("RAMPURHAT-I", 175.81, 6.87, 0.00, 6.87, 0.00, 0.00, 0.00, 2693)
        norm.contains("RAMPURHAT-II") || (norm.contains("RAMPURHAT") && norm.contains("II")) ->
            BlockExpenditure("RAMPURHAT-II", 211.95, 6.05, 0.21, 5.84, 0.21, 0.00, 0.21, 2346)
        norm.contains("SAINTHIA") -> BlockExpenditure("SAINTHIA", 300.38, 13.77, 6.20, 7.57, 6.20, 0.00, 6.20, 6681)
        norm.contains("SURI-I") || (norm.contains("SURI") && norm.contains("I") && !norm.contains("II")) ->
            BlockExpenditure("SURI-I", 95.45, 6.12, 0.00, 6.12, 0.00, 0.00, 0.00, 2368)
        norm.contains("SURI-II") || (norm.contains("SURI") && norm.contains("II")) ->
            BlockExpenditure("SURI-II", 140.46, 12.29, 10.47, 1.82, 10.47, 0.00, 10.47, 4303)
        else -> BlockExpenditure(blockName, 200.0, 10.0, 3.0, 7.0, 3.0, 0.0, 3.0, 4500)
    }
}

data class BlockData(
    val slNo: Int = 0,
    val blockName: String = "",
    val allotment: BlockAllotment = BlockAllotment(),
    val expenditure: BlockExpenditure = BlockExpenditure(),
    val works: BlockWorks = BlockWorks(),
    val fto: BlockFto = BlockFto(),
    val ekyc: BlockEkyc = BlockEkyc(),
    val target: BlockTarget = BlockTarget(),
    val pba: PbaBlockReport? = null,
    val nmmsAttendance: Int = 0,
    val todayNmmsAttendance: Int = 0,
    val persondaysGenerated: Int = 0,
    val sectorWorks: String = "",
    val bestGp: String = "",
    val bestGpStats: String = "",
    val poorGp: String = "",
    val poorGpStats: String = ""
)

data class BlockGramPanchayatAlert(
    val bestGp: String,
    val bestGpStats: String,
    val poorGp: String,
    val poorGpStats: String,
    val sectorWorks: String,
    val persondaysGenerated: Int,
    val nmmsAttendance: Int,
    val todayNmmsAttendance: Int
)

fun getBlockGpPerformance(blockName: String): BlockGramPanchayatAlert {
    val norm = blockName.trim().uppercase()
    return when {
        norm.contains("BOLPUR") -> BlockGramPanchayatAlert(
            bestGp = "KASBA",
            bestGpStats = "52 Approved • 8 Ongoing",
            poorGp = "RAIPUR-SUPUR",
            poorGpStats = "18 Approved • 8 Ongoing (Sanction delays)",
            sectorWorks = "Drought Proofing (154), Rural Connectivity (109), Works on Individuals Land (32)",
            persondaysGenerated = 7907,
            nmmsAttendance = 7907,
            todayNmmsAttendance = 174
        )
        norm.contains("DUBRAJPUR") -> BlockGramPanchayatAlert(
            bestGp = "LAXMI-NARAYANPUR",
            bestGpStats = "24 Approved • 11 Ongoing",
            poorGp = "CHINPAI",
            poorGpStats = "3 Approved • 7 Ongoing (Low clearance)",
            sectorWorks = "Drought Proofing (128), Rural Connectivity (76), Traditional Water Bodies (49)",
            persondaysGenerated = 18577,
            nmmsAttendance = 18577,
            todayNmmsAttendance = 302
        )
        norm.contains("ILLAMBAZAR") -> BlockGramPanchayatAlert(
            bestGp = "DHARAMPUR",
            bestGpStats = "39 Approved • 19 Ongoing",
            poorGp = "SIRSHA",
            poorGpStats = "14 Approved • 9 Ongoing (Field pace slow)",
            sectorWorks = "Drought Proofing (137), Rural Connectivity (116), Micro Irrigation (46)",
            persondaysGenerated = 8990,
            nmmsAttendance = 8990,
            todayNmmsAttendance = 256
        )
        norm.contains("KHOYRASOL") -> BlockGramPanchayatAlert(
            bestGp = "BARRAH",
            bestGpStats = "26 Approved • 12 Ongoing",
            poorGp = "PARSUNDI",
            poorGpStats = "15 Approved • 13 Ongoing (Sanctions pending)",
            sectorWorks = "Drought Proofing (158), Rural Connectivity (131), Micro Irrigation (64)",
            persondaysGenerated = 24223,
            nmmsAttendance = 24223,
            todayNmmsAttendance = 222
        )
        norm.contains("LABPUR") -> BlockGramPanchayatAlert(
            bestGp = "THIBA",
            bestGpStats = "22 Approved • 7 Ongoing",
            poorGp = "LABPUR-I",
            poorGpStats = "6 Approved • 7 Ongoing (Needs intervention)",
            sectorWorks = "Drought Proofing (142), Works on Individuals Land (46), Water Bodies (19)",
            persondaysGenerated = 6751,
            nmmsAttendance = 6751,
            todayNmmsAttendance = 43
        )
        norm.contains("MAYURESWAR-I") || (norm.contains("MAYURESWAR") && norm.contains("I") && !norm.contains("II")) -> BlockGramPanchayatAlert(
            bestGp = "KANACHI",
            bestGpStats = "60 Approved • 5 Ongoing",
            poorGp = "DAKSHINGRAM",
            poorGpStats = "20 Approved • 5 Ongoing (Low work generation)",
            sectorWorks = "Rural Connectivity (117), Drought Proofing (67), Flood Control (62)",
            persondaysGenerated = 6659,
            nmmsAttendance = 6659,
            todayNmmsAttendance = 109
        )
        norm.contains("MAYURESWAR-II") || (norm.contains("MAYURESWAR") && norm.contains("II")) -> BlockGramPanchayatAlert(
            bestGp = "SATPALSA",
            bestGpStats = "43 Approved • 11 Ongoing",
            poorGp = "ULKUNDA",
            poorGpStats = "18 Approved • 4 Ongoing (Behind monthly target)",
            sectorWorks = "Rural Connectivity (108), Drought Proofing (93), Rural Sanitation (28)",
            persondaysGenerated = 6706,
            nmmsAttendance = 6706,
            todayNmmsAttendance = 147
        )
        norm.contains("MOHAMMAD") || norm.contains("MD") -> BlockGramPanchayatAlert(
            bestGp = "BHUTURA",
            bestGpStats = "29 Approved • 20 Ongoing",
            poorGp = "KAPISTA",
            poorGpStats = "11 Approved • 5 Ongoing (High pendency)",
            sectorWorks = "Rural Connectivity (164), Drought Proofing (96), Flood Control (75)",
            persondaysGenerated = 7653,
            nmmsAttendance = 7653,
            todayNmmsAttendance = 52
        )
        norm.contains("MURARAI-I") || (norm.contains("MURARAI") && norm.contains("I") && !norm.contains("II")) -> BlockGramPanchayatAlert(
            bestGp = "MURARAI",
            bestGpStats = "56 Approved • 8 Ongoing",
            poorGp = "DUMURGRAM",
            poorGpStats = "6 Approved • 9 Ongoing (Critical work shortage)",
            sectorWorks = "Drought Proofing (98), Rural Connectivity (65), Flood Control (26)",
            persondaysGenerated = 3953,
            nmmsAttendance = 3953,
            todayNmmsAttendance = 149
        )
        norm.contains("MURARAI-II") || (norm.contains("MURARAI") && norm.contains("II")) -> BlockGramPanchayatAlert(
            bestGp = "AMDOLE",
            bestGpStats = "39 Approved • 7 Ongoing",
            poorGp = "KUSHMORE-II",
            poorGpStats = "8 Approved • 9 Ongoing (Low turnout rate)",
            sectorWorks = "Rural Connectivity (84), Rural Sanitation (76), Drought Proofing (75)",
            persondaysGenerated = 4702,
            nmmsAttendance = 4702,
            todayNmmsAttendance = 118
        )
        norm.contains("NALHATI-I") || (norm.contains("NALHATI") && norm.contains("I") && !norm.contains("II")) -> BlockGramPanchayatAlert(
            bestGp = "KAITHA-II",
            bestGpStats = "49 Approved • 5 Ongoing",
            poorGp = "KURUMGRAM",
            poorGpStats = "17 Approved • 10 Ongoing (Sanction delays)",
            sectorWorks = "Rural Connectivity (130), Drought Proofing (121), Flood Control (38)",
            persondaysGenerated = 10971,
            nmmsAttendance = 10971,
            todayNmmsAttendance = 66
        )
        norm.contains("NALHATI-II") || (norm.contains("NALHATI") && norm.contains("II")) -> BlockGramPanchayatAlert(
            bestGp = "SHITALGRAM",
            bestGpStats = "14 Approved • 19 Ongoing",
            poorGp = "BHADRAPUR-I",
            poorGpStats = "6 Approved • 8 Ongoing (Below average pace)",
            sectorWorks = "Drought Proofing (57), Flood Control (52), Rural Connectivity (28)",
            persondaysGenerated = 4124,
            nmmsAttendance = 4124,
            todayNmmsAttendance = 93
        )
        norm.contains("NANOOR") -> BlockGramPanchayatAlert(
            bestGp = "BARASAOTA",
            bestGpStats = "47 Approved • 7 Ongoing",
            poorGp = "CHANDIDASNANOOR",
            poorGpStats = "3 Approved • 6 Ongoing (Lowest approved count)",
            sectorWorks = "Rural Connectivity (112), Drought Proofing (111), Flood Control (45)",
            persondaysGenerated = 5862,
            nmmsAttendance = 5862,
            todayNmmsAttendance = 23
        )
        norm.contains("RAJNAGAR") -> BlockGramPanchayatAlert(
            bestGp = "GANGMURI-JOYPUR",
            bestGpStats = "19 Approved • 20 Ongoing",
            poorGp = "CHANDRAPUR",
            poorGpStats = "14 Approved • 14 Ongoing (Needs administrative push)",
            sectorWorks = "Drought Proofing (120), Rural Connectivity (42), Individual Land (37)",
            persondaysGenerated = 7537,
            nmmsAttendance = 7537,
            todayNmmsAttendance = 218
        )
        norm.contains("RAMPURHAT-I") || (norm.contains("RAMPURHAT") && norm.contains("I") && !norm.contains("II")) -> BlockGramPanchayatAlert(
            bestGp = "BARSHAL",
            bestGpStats = "23 Approved • 15 Ongoing",
            poorGp = "KUSUMBA",
            poorGpStats = "5 Approved • 4 Ongoing (Very low active sites)",
            sectorWorks = "Drought Proofing (100), Flood Control (26), Water Conservation (23)",
            persondaysGenerated = 4745,
            nmmsAttendance = 4745,
            todayNmmsAttendance = 49
        )
        norm.contains("RAMPURHAT-II") || (norm.contains("RAMPURHAT") && norm.contains("II")) -> BlockGramPanchayatAlert(
            bestGp = "KALUHA",
            bestGpStats = "50 Approved • 12 Ongoing",
            poorGp = "SAHAPUR",
            poorGpStats = "14 Approved • 3 Ongoing (Low ongoing count)",
            sectorWorks = "Rural Connectivity (126), Drought Proofing (62), Flood Control (57)",
            persondaysGenerated = 4638,
            nmmsAttendance = 4638,
            todayNmmsAttendance = 143
        )
        norm.contains("SAINTHIA") -> BlockGramPanchayatAlert(
            bestGp = "HORISARA",
            bestGpStats = "56 Approved • 5 Ongoing",
            poorGp = "SREENIDHIPUR",
            poorGpStats = "13 Approved • 4 Ongoing (Slow implementation)",
            sectorWorks = "Flood Control (103), Drought Proofing (103), Rural Connectivity (90)",
            persondaysGenerated = 11020,
            nmmsAttendance = 11020,
            todayNmmsAttendance = 43
        )
        norm.contains("SURI-I") || (norm.contains("SURI") && norm.contains("I") && !norm.contains("II")) -> BlockGramPanchayatAlert(
            bestGp = "ALUNDA",
            bestGpStats = "15 Approved • 9 Ongoing",
            poorGp = "TILPARA",
            poorGpStats = "7 Approved • 5 Ongoing (Lagging behind target)",
            sectorWorks = "Drought Proofing (100), Rural Connectivity (22), Individual Land (16)",
            persondaysGenerated = 3262,
            nmmsAttendance = 3262,
            todayNmmsAttendance = 27
        )
        norm.contains("SURI-II") || (norm.contains("SURI") && norm.contains("II")) -> BlockGramPanchayatAlert(
            bestGp = "DOMDAMA",
            bestGpStats = "26 Approved • 14 Ongoing",
            poorGp = "PURANDARPUR",
            poorGpStats = "16 Approved • 10 Ongoing (Sanction rate below average)",
            sectorWorks = "Drought Proofing (79), Rural Connectivity (33), Rural Sanitation (28)",
            persondaysGenerated = 6598,
            nmmsAttendance = 6598,
            todayNmmsAttendance = 223
        )
        else -> BlockGramPanchayatAlert(
            bestGp = "HEADQUARTERS GP",
            bestGpStats = "High Execution Clearance",
            poorGp = "RURAL WARD GP",
            poorGpStats = "Sanctions in progress",
            sectorWorks = "Rural Connectivity, Water Bodies, Drought Proofing",
            persondaysGenerated = 5000,
            nmmsAttendance = 5000,
            todayNmmsAttendance = 100
        )
    }
}

data class BlockAllotment(
    val countA: Int = 0,
    val countB: Int = 0,
    val totalCount: Int = 0,
    val cat1Lakh: Double = 0.0,
    val cat2Lakh: Double = 0.0,
    val cat3Lakh: Double = 0.0,
    val totalLakh: Double = 0.0,
    val totalCr: Double = 0.0
)

data class BlockWorks(
    val gps: Int = 0,
    val totalWorks: Int = 0,
    val estimatesStarted: Int = 0,
    val ts: Int = 0,
    val asSanctions: Int = 0,
    val pending: Int = 0,
    val tsPct: Double = 0.0,
    val asPct: Double = 0.0
) {
    val formattedTsPct: String get() = tsPct.formatFund()
    val formattedAsPct: String get() = asPct.formatFund()
}

data class BlockFto(
    val ftosPending: Int = 0,
    val pendingAmountRs: Long = 0L,
    val ftosChecked: Int = 0,
    val acceptedAmountRs: Long = 0L,
    val rejectedAmountRs: Long = 0L,
    val verifiedAmountRs: Long = 0L,
    val status: String = "100.00%"
)

data class BlockEkyc(
    val totalWorkers: Int = 0,
    val aadhaarSeeded: Int = 0,
    val ekycDone: Int = 0,
    val ekycPct: Double = 0.0,
    val authenticated: Int = 0,
    val authFailed: Int = 0,
    val abpsEligible: Int = 0
)

data class BlockTarget(
    val gps: Int = 0,
    val sep2026: Int = 0,
    val oct2026: Int = 0,
    val nov2026: Int = 0,
    val dec2026: Int = 0,
    val jan2027: Int = 0,
    val feb2027: Int = 0,
    val mar2027: Int = 0,
    val totalTarget: Int = 0
)

data class PbaBlockReport(
    val slNo: Int = 0,
    val blockName: String = "",
    val totalBeneficiaries: Int = 0,
    val tableTopDone: Int = 0, // "Paschim Banga Awaas Beneficiaries verification done through Table Top Excercise"
    val pendingTableTop: Int = 0,
    val verifiedOk: Int = 0, // No. of Beneficiaries marked “Verified and Found OK”
    val deletedJobcards: Int = 0,
    val newJobcardsForDeleted: Int = 0,
    val pendingDeleted: Int = 0,
    val notAvailableJobcards: Int = 0,
    val newJobcardsForNotAvailable: Int = 0,
    val pendingNotAvailable: Int = 0,
    val availableWorkCodes: Int = 0, // No. of Available Work code in VB-G RAM G Soft
    val mustersGenerated: Int = 0,
    val emrIssueRegister: Int = 0 // As per e-MR Issue Register (R6.9)
) {
    val verifiedPct: Double
        get() = if (totalBeneficiaries > 0) (verifiedOk.toDouble() / totalBeneficiaries * 100.0) else 0.0
    val workCodePct: Double
        get() = if (verifiedOk > 0) (availableWorkCodes.toDouble() / verifiedOk * 100.0) else 0.0
    val emrPct: Double
        get() = if (availableWorkCodes > 0) (emrIssueRegister.toDouble() / availableWorkCodes * 100.0) else 0.0
}

data class PbaSummary(
    val totalBeneficiaries: Int = 61234,
    val tableTopDone: Int = 61234,
    val pendingTableTop: Int = 0,
    val verifiedOk: Int = 40489,
    val deletedJobcards: Int = 12597,
    val newJobcardsForDeleted: Int = 149,
    val notAvailableJobcards: Int = 8247,
    val newJobcardsForNotAvailable: Int = 731,
    val availableWorkCodes: Int = 32262,
    val mustersGenerated: Int = 2163,
    val emrIssueRegister: Int = 5505,
    val blocks: List<PbaBlockReport> = emptyList()
) {
    val verifiedRate: Double
        get() = if (totalBeneficiaries > 0) (verifiedOk.toDouble() / totalBeneficiaries * 100.0) else 66.12
    val workCodeCoverage: Double
        get() = if (verifiedOk > 0) (availableWorkCodes.toDouble() / verifiedOk * 100.0) else 79.68
    val tableTopRate: Double
        get() = if (totalBeneficiaries > 0) (tableTopDone.toDouble() / totalBeneficiaries * 100.0) else 100.0
}

data class FeatureUpdateItem(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val dateCompleted: String,
    val isDone: Boolean = true,
    val iconName: String = "CheckCircle"
)

data class GramPanchayatDetail(
    val gpName: String,
    val totalBeneficiaries: Int = 310,
    val tableTopDone: Int = 310,
    val verifiedOk: Int = 215,
    val availableWorkCodes: Int = 185,
    val status: String = "Table Top Verified",
    val isBest: Boolean = false,
    val isPoor: Boolean = false
)

fun getBlockGramPanchayats(blockName: String): List<GramPanchayatDetail> {
    val norm = blockName.trim().uppercase()
    return when {
        norm.contains("BOLPUR") -> listOf(
            GramPanchayatDetail("KASBA", 420, 420, 310, 290, "Top Performer • 52 Approved", isBest = true),
            GramPanchayatDetail("RAIPUR-SUPUR", 310, 310, 180, 140, "Sanction Delays • 18 Approved", isPoor = true),
            GramPanchayatDetail("RUPPUR", 380, 380, 275, 230, "Active • 34 Approved"),
            GramPanchayatDetail("SATTOR", 350, 350, 240, 205, "Active • 29 Approved"),
            GramPanchayatDetail("SINGHEE", 340, 340, 230, 195, "Active • 26 Approved"),
            GramPanchayatDetail("SIAN", 310, 310, 210, 175, "Normal • 22 Approved"),
            GramPanchayatDetail("BAHIRI-PANCHSHOWA", 360, 360, 255, 215, "Normal • 31 Approved"),
            GramPanchayatDetail("KANKALITALA", 330, 330, 220, 180, "Active • 25 Approved"),
            GramPanchayatDetail("SARPALEHANA-ALBANDA", 318, 318, 208, 172, "Normal • 21 Approved")
        )
        norm.contains("DUBRAJPUR") -> listOf(
            GramPanchayatDetail("LAXMI-NARAYANPUR", 380, 380, 290, 260, "Top Performer • 24 Approved", isBest = true),
            GramPanchayatDetail("CHINPAI", 260, 260, 145, 110, "Low Clearance • 3 Approved", isPoor = true),
            GramPanchayatDetail("JASHPUR", 340, 340, 245, 210, "Active • 19 Approved"),
            GramPanchayatDetail("PADUMA", 310, 310, 220, 185, "Active • 18 Approved"),
            GramPanchayatDetail("BALIJURI", 295, 295, 200, 170, "Normal • 15 Approved"),
            GramPanchayatDetail("GOHALIA", 280, 280, 190, 160, "Normal • 14 Approved"),
            GramPanchayatDetail("HETAMPUR", 330, 330, 235, 195, "Active • 21 Approved"),
            GramPanchayatDetail("LOBA", 290, 290, 195, 165, "Normal • 16 Approved"),
            GramPanchayatDetail("PARULIA", 275, 275, 180, 150, "Normal • 13 Approved"),
            GramPanchayatDetail("SAHAPUR", 277, 277, 182, 154, "Normal • 14 Approved")
        )
        norm.contains("ILLAMBAZAR") -> listOf(
            GramPanchayatDetail("DHARAMPUR", 390, 390, 295, 265, "Top Performer • 39 Approved", isBest = true),
            GramPanchayatDetail("SIRSHA", 250, 250, 140, 115, "Field Pace Slow • 14 Approved", isPoor = true),
            GramPanchayatDetail("GHURISHA", 340, 340, 240, 205, "Active • 28 Approved"),
            GramPanchayatDetail("ILLAMBAZAR", 370, 370, 260, 225, "Active • 32 Approved"),
            GramPanchayatDetail("NANASOLE", 305, 305, 210, 175, "Normal • 20 Approved"),
            GramPanchayatDetail("BELATI", 285, 285, 195, 160, "Normal • 18 Approved"),
            GramPanchayatDetail("JOYDEV-KENDULI", 335, 335, 230, 195, "Active • 25 Approved"),
            GramPanchayatDetail("MONGOLDIHI", 290, 290, 190, 155, "Normal • 17 Approved"),
            GramPanchayatDetail("BATIKAR", 278, 278, 185, 150, "Normal • 16 Approved")
        )
        norm.contains("KHOYRASOL") -> listOf(
            GramPanchayatDetail("BARRAH", 370, 370, 280, 250, "Top Performer • 26 Approved", isBest = true),
            GramPanchayatDetail("PARSUNDI", 260, 260, 150, 120, "Sanctions Pending • 15 Approved", isPoor = true),
            GramPanchayatDetail("RUPUSPUR", 330, 330, 235, 200, "Active • 22 Approved"),
            GramPanchayatDetail("BABUISOL", 295, 295, 205, 175, "Normal • 17 Approved"),
            GramPanchayatDetail("BHADULIA", 280, 280, 190, 160, "Normal • 16 Approved"),
            GramPanchayatDetail("HAZRAPUR", 310, 310, 215, 185, "Active • 19 Approved"),
            GramPanchayatDetail("KENDRA", 290, 290, 200, 170, "Normal • 16 Approved"),
            GramPanchayatDetail("KHOYRASOL", 350, 350, 250, 220, "Active • 24 Approved"),
            GramPanchayatDetail("LOKEPUR", 285, 285, 195, 165, "Normal • 15 Approved"),
            GramPanchayatDetail("PANCHRA", 305, 305, 215, 180, "Normal • 18 Approved")
        )
        norm.contains("LABPUR") -> listOf(
            GramPanchayatDetail("THIBA", 360, 360, 270, 240, "Top Performer • 22 Approved", isBest = true),
            GramPanchayatDetail("LABPUR-I", 240, 240, 130, 100, "Intervention Needed • 6 Approved", isPoor = true),
            GramPanchayatDetail("LABPUR-II", 290, 290, 195, 165, "Normal • 15 Approved"),
            GramPanchayatDetail("BISPURA", 280, 280, 185, 155, "Normal • 14 Approved"),
            GramPanchayatDetail("CHOUHATTA-MAHODARI-I", 270, 270, 180, 150, "Normal • 13 Approved"),
            GramPanchayatDetail("CHOUHATTA-MAHODARI-II", 275, 275, 185, 155, "Normal • 14 Approved"),
            GramPanchayatDetail("DWARKA", 310, 310, 215, 185, "Active • 19 Approved"),
            GramPanchayatDetail("HATIA", 285, 285, 190, 160, "Normal • 15 Approved"),
            GramPanchayatDetail("INDAS", 295, 295, 200, 170, "Normal • 16 Approved"),
            GramPanchayatDetail("JAMNA", 305, 305, 210, 180, "Normal • 17 Approved"),
            GramPanchayatDetail("KAGAN", 285, 285, 190, 160, "Normal • 15 Approved")
        )
        norm.contains("MAYURESWAR-I") || (norm.contains("MAYURESWAR") && norm.contains("I") && !norm.contains("II")) -> listOf(
            GramPanchayatDetail("KANACHI", 400, 400, 305, 275, "Top Performer • 60 Approved", isBest = true),
            GramPanchayatDetail("DAKSHINGRAM", 250, 250, 145, 115, "Low Work Generation • 20 Approved", isPoor = true),
            GramPanchayatDetail("DABUK", 290, 290, 195, 165, "Normal • 18 Approved"),
            GramPanchayatDetail("MALLARPUR-I", 360, 360, 260, 230, "Active • 32 Approved"),
            GramPanchayatDetail("MALLARPUR-II", 340, 340, 240, 210, "Active • 28 Approved"),
            GramPanchayatDetail("TALWAN", 280, 280, 185, 155, "Normal • 16 Approved"),
            GramPanchayatDetail("BAJEPRADAPUR", 275, 275, 180, 150, "Normal • 15 Approved"),
            GramPanchayatDetail("KUNDALA", 285, 285, 190, 160, "Normal • 17 Approved"),
            GramPanchayatDetail("JIKADDA", 270, 270, 180, 150, "Normal • 15 Approved")
        )
        norm.contains("MAYURESWAR-II") || (norm.contains("MAYURESWAR") && norm.contains("II")) -> listOf(
            GramPanchayatDetail("SATPALSA", 380, 380, 285, 255, "Top Performer • 43 Approved", isBest = true),
            GramPanchayatDetail("ULKUNDA", 240, 240, 135, 105, "Behind Target • 18 Approved", isPoor = true),
            GramPanchayatDetail("DASPALEHANA-I", 290, 290, 200, 170, "Normal • 19 Approved"),
            GramPanchayatDetail("DASPALEHANA-II", 285, 285, 195, 165, "Normal • 18 Approved"),
            GramPanchayatDetail("KALESWAR", 320, 320, 225, 195, "Active • 23 Approved"),
            GramPanchayatDetail("KUNDISHA", 275, 275, 185, 155, "Normal • 16 Approved"),
            GramPanchayatDetail("MAYURANNA", 280, 280, 190, 160, "Normal • 17 Approved")
        )
        norm.contains("MOHAMMAD") || norm.contains("MD") -> listOf(
            GramPanchayatDetail("BHUTURA", 370, 370, 275, 245, "Top Performer • 29 Approved", isBest = true),
            GramPanchayatDetail("KAPISTA", 250, 250, 140, 110, "High Pendency • 11 Approved", isPoor = true),
            GramPanchayatDetail("ANGARGARIA", 320, 320, 225, 195, "Active • 22 Approved"),
            GramPanchayatDetail("CHARICHA", 305, 305, 210, 180, "Normal • 18 Approved"),
            GramPanchayatDetail("DEUCHA", 340, 340, 245, 215, "Active • 25 Approved"),
            GramPanchayatDetail("HINGLOW", 295, 295, 200, 170, "Normal • 17 Approved"),
            GramPanchayatDetail("PURANAGRAM", 285, 285, 190, 160, "Normal • 16 Approved"),
            GramPanchayatDetail("RAMPUR", 310, 310, 215, 185, "Active • 20 Approved"),
            GramPanchayatDetail("SEHRAKURI", 290, 290, 195, 165, "Normal • 17 Approved"),
            GramPanchayatDetail("GONPUR", 280, 280, 185, 155, "Normal • 15 Approved"),
            GramPanchayatDetail("THEKRA", 275, 275, 180, 150, "Normal • 14 Approved"),
            GramPanchayatDetail("PATELNAGAR", 330, 330, 230, 200, "Active • 23 Approved")
        )
        norm.contains("MURARAI-I") || (norm.contains("MURARAI") && norm.contains("I") && !norm.contains("II")) -> listOf(
            GramPanchayatDetail("MURARAI", 420, 420, 320, 290, "Top Performer • 56 Approved", isBest = true),
            GramPanchayatDetail("DUMURGRAM", 230, 230, 125, 95, "Work Shortage • 6 Approved", isPoor = true),
            GramPanchayatDetail("CHATRA", 330, 330, 230, 200, "Active • 24 Approved"),
            GramPanchayatDetail("GORSHA", 305, 305, 210, 180, "Normal • 19 Approved"),
            GramPanchayatDetail("MITRAPUR", 315, 315, 220, 190, "Active • 21 Approved"),
            GramPanchayatDetail("PALASHBONE", 290, 290, 195, 165, "Normal • 17 Approved"),
            GramPanchayatDetail("RAJGRAM", 350, 350, 255, 225, "Active • 28 Approved")
        )
        norm.contains("MURARAI-II") || (norm.contains("MURARAI") && norm.contains("II")) -> listOf(
            GramPanchayatDetail("AMDOLE", 390, 390, 290, 260, "Top Performer • 39 Approved", isBest = true),
            GramPanchayatDetail("KUSHMORE-II", 245, 245, 135, 105, "Low Turnout • 8 Approved", isPoor = true),
            GramPanchayatDetail("KUSHMORE-I", 295, 295, 205, 175, "Normal • 19 Approved"),
            GramPanchayatDetail("NANDIGRAM", 310, 310, 215, 185, "Active • 22 Approved"),
            GramPanchayatDetail("PAIKAR-I", 340, 340, 245, 215, "Active • 26 Approved"),
            GramPanchayatDetail("PAIKAR-II", 330, 330, 235, 205, "Active • 24 Approved"),
            GramPanchayatDetail("RUDRANAGAR", 285, 285, 190, 160, "Normal • 17 Approved"),
            GramPanchayatDetail("KANKARDOLE", 280, 280, 185, 155, "Normal • 16 Approved"),
            GramPanchayatDetail("MAHURAPUR", 290, 290, 195, 165, "Normal • 18 Approved")
        )
        norm.contains("NALHATI-I") || (norm.contains("NALHATI") && norm.contains("I") && !norm.contains("II")) -> listOf(
            GramPanchayatDetail("KAITHA-II", 410, 410, 310, 280, "Top Performer • 49 Approved", isBest = true),
            GramPanchayatDetail("KURUMGRAM", 255, 255, 145, 115, "Sanction Delays • 17 Approved", isPoor = true),
            GramPanchayatDetail("KAITHA-I", 330, 330, 230, 200, "Active • 25 Approved"),
            GramPanchayatDetail("BANIOR", 310, 310, 215, 185, "Active • 22 Approved"),
            GramPanchayatDetail("BARLA", 295, 295, 200, 170, "Normal • 19 Approved"),
            GramPanchayatDetail("KALITHA", 320, 320, 225, 195, "Active • 23 Approved"),
            GramPanchayatDetail("HARIDASPUR", 285, 285, 190, 160, "Normal • 17 Approved"),
            GramPanchayatDetail("NALHATI", 360, 360, 260, 230, "Active • 30 Approved"),
            GramPanchayatDetail("SUROSHI", 290, 290, 195, 165, "Normal • 18 Approved")
        )
        norm.contains("NALHATI-II") || (norm.contains("NALHATI") && norm.contains("II")) -> listOf(
            GramPanchayatDetail("SHITALGRAM", 350, 350, 255, 225, "Top Performer • 14 Approved", isBest = true),
            GramPanchayatDetail("BHADRAPUR-I", 240, 240, 130, 100, "Below Average • 6 Approved", isPoor = true),
            GramPanchayatDetail("BHADRAPUR-II", 285, 285, 190, 160, "Normal • 16 Approved"),
            GramPanchayatDetail("BARA-I", 310, 310, 215, 185, "Active • 21 Approved"),
            GramPanchayatDetail("BARA-II", 305, 305, 210, 180, "Normal • 19 Approved"),
            GramPanchayatDetail("NOAPARA", 290, 290, 195, 165, "Normal • 17 Approved"),
            GramPanchayatDetail("DEBAGRAM", 295, 295, 200, 170, "Normal • 18 Approved"),
            GramPanchayatDetail("KURURGRAM", 275, 275, 180, 150, "Normal • 15 Approved"),
            GramPanchayatDetail("PRADAPUR", 280, 280, 185, 155, "Normal • 16 Approved")
        )
        norm.contains("NANOOR") -> listOf(
            GramPanchayatDetail("BARASAOTA", 390, 390, 295, 265, "Top Performer • 47 Approved", isBest = true),
            GramPanchayatDetail("CHANDIDASNANOOR", 230, 230, 120, 90, "Lowest Approved • 3 Approved", isPoor = true),
            GramPanchayatDetail("CHARKHAL", 290, 290, 195, 165, "Normal • 17 Approved"),
            GramPanchayatDetail("DASKALGRAM-KAREYA-I", 310, 310, 215, 185, "Active • 21 Approved"),
            GramPanchayatDetail("DASKALGRAM-KAREYA-II", 305, 305, 210, 180, "Normal • 19 Approved"),
            GramPanchayatDetail("JALUNDI", 285, 285, 190, 160, "Normal • 16 Approved"),
            GramPanchayatDetail("KIRNAHAR-I", 360, 360, 260, 230, "Active • 31 Approved"),
            GramPanchayatDetail("KIRNAHAR-II", 340, 340, 240, 210, "Active • 27 Approved"),
            GramPanchayatDetail("NANUR", 350, 350, 250, 220, "Active • 29 Approved"),
            GramPanchayatDetail("THUPSARA", 280, 280, 185, 155, "Normal • 15 Approved"),
            GramPanchayatDetail("UCHCHAKARAN", 275, 275, 180, 150, "Normal • 14 Approved")
        )
        norm.contains("RAJNAGAR") -> listOf(
            GramPanchayatDetail("GANGMURI-JOYPUR", 370, 370, 275, 245, "Top Performer • 19 Approved", isBest = true),
            GramPanchayatDetail("CHANDRAPUR", 245, 245, 135, 105, "Administrative Push • 14 Approved", isPoor = true),
            GramPanchayatDetail("BHAVANIPUR", 295, 295, 205, 175, "Normal • 18 Approved"),
            GramPanchayatDetail("KASBA-RAJNAGAR", 320, 320, 225, 195, "Active • 22 Approved"),
            GramPanchayatDetail("RAJNAGAR", 350, 350, 255, 225, "Active • 26 Approved")
        )
        norm.contains("RAMPURHAT-I") || (norm.contains("RAMPURHAT") && norm.contains("I") && !norm.contains("II")) -> listOf(
            GramPanchayatDetail("BARSHAL", 380, 380, 285, 255, "Top Performer • 23 Approved", isBest = true),
            GramPanchayatDetail("KUSUMBA", 235, 235, 125, 95, "Low Active Sites • 5 Approved", isPoor = true),
            GramPanchayatDetail("AYAS", 310, 310, 215, 185, "Active • 21 Approved"),
            GramPanchayatDetail("BONHAT", 290, 290, 195, 165, "Normal • 18 Approved"),
            GramPanchayatDetail("CHAKMANDAL", 280, 280, 185, 155, "Normal • 16 Approved"),
            GramPanchayatDetail("DAKSHINGRAM", 285, 285, 190, 160, "Normal • 17 Approved"),
            GramPanchayatDetail("KASTHAGORA", 325, 325, 230, 200, "Active • 23 Approved"),
            GramPanchayatDetail("MASRA", 300, 300, 205, 175, "Normal • 19 Approved"),
            GramPanchayatDetail("POONCHRA", 295, 295, 200, 170, "Normal • 18 Approved")
        )
        norm.contains("RAMPURHAT-II") || (norm.contains("RAMPURHAT") && norm.contains("II")) -> listOf(
            GramPanchayatDetail("KALUHA", 400, 400, 305, 275, "Top Performer • 50 Approved", isBest = true),
            GramPanchayatDetail("SAHAPUR", 245, 245, 135, 105, "Low Ongoing Count • 14 Approved", isPoor = true),
            GramPanchayatDetail("BISNUPUR", 310, 310, 215, 185, "Active • 22 Approved"),
            GramPanchayatDetail("BUDHIGRAM", 300, 300, 205, 175, "Normal • 20 Approved"),
            GramPanchayatDetail("DUNIGRAM", 330, 330, 235, 205, "Active • 25 Approved"),
            GramPanchayatDetail("HANSAN-I", 340, 340, 245, 215, "Active • 27 Approved"),
            GramPanchayatDetail("HANSAN-II", 325, 325, 230, 200, "Active • 24 Approved"),
            GramPanchayatDetail("MARGRAM-I", 360, 360, 265, 235, "Active • 31 Approved"),
            GramPanchayatDetail("MARGRAM-II", 350, 350, 255, 225, "Active • 29 Approved")
        )
        norm.contains("SAINTHIA") -> listOf(
            GramPanchayatDetail("HORISARA", 430, 430, 330, 300, "Top Performer • 56 Approved", isBest = true),
            GramPanchayatDetail("SREENIDHIPUR", 250, 250, 140, 110, "Slow Implementation • 13 Approved", isPoor = true),
            GramPanchayatDetail("AHAMADPUR", 380, 380, 280, 250, "Active • 35 Approved"),
            GramPanchayatDetail("AMARPUR", 310, 310, 215, 185, "Active • 22 Approved"),
            GramPanchayatDetail("BANAGRAM", 300, 300, 205, 175, "Normal • 20 Approved"),
            GramPanchayatDetail("BHROMORKOL", 290, 290, 195, 165, "Normal • 18 Approved"),
            GramPanchayatDetail("DERIAPUR", 320, 320, 225, 195, "Active • 24 Approved"),
            GramPanchayatDetail("FULLUR", 340, 340, 245, 215, "Active • 27 Approved"),
            GramPanchayatDetail("HARISARA", 305, 305, 210, 180, "Normal • 20 Approved"),
            GramPanchayatDetail("HATHIA", 285, 285, 190, 160, "Normal • 17 Approved"),
            GramPanchayatDetail("PANRUI", 335, 335, 235, 205, "Active • 26 Approved"),
            GramPanchayatDetail("SAINTHIA", 370, 370, 275, 245, "Active • 33 Approved")
        )
        norm.contains("SURI-I") || (norm.contains("SURI") && norm.contains("I") && !norm.contains("II")) -> listOf(
            GramPanchayatDetail("ALUNDA", 360, 360, 270, 240, "Top Performer • 15 Approved", isBest = true),
            GramPanchayatDetail("TILPARA", 240, 240, 130, 100, "Lagging Target • 7 Approved", isPoor = true),
            GramPanchayatDetail("BHURKUNA", 300, 300, 210, 180, "Active • 21 Approved"),
            GramPanchayatDetail("KHATANGA", 320, 320, 225, 195, "Active • 24 Approved"),
            GramPanchayatDetail("MALLICKPUR", 285, 285, 190, 160, "Normal • 17 Approved"),
            GramPanchayatDetail("NAGARI", 295, 295, 200, 170, "Normal • 19 Approved"),
            GramPanchayatDetail("KARIDHYA", 340, 340, 245, 215, "Active • 26 Approved")
        )
        norm.contains("SURI-II") || (norm.contains("SURI") && norm.contains("II")) -> listOf(
            GramPanchayatDetail("DOMDAMA", 370, 370, 280, 250, "Top Performer • 26 Approved", isBest = true),
            GramPanchayatDetail("PURANDARPUR", 245, 245, 135, 105, "Low Sanctions • 16 Approved", isPoor = true),
            GramPanchayatDetail("ABINASHPUR", 310, 310, 215, 185, "Active • 22 Approved"),
            GramPanchayatDetail("BONSHANKA", 290, 290, 195, 165, "Normal • 18 Approved"),
            GramPanchayatDetail("KOMA", 280, 280, 185, 155, "Normal • 17 Approved"),
            GramPanchayatDetail("PARSOLI", 285, 285, 190, 160, "Normal • 18 Approved")
        )
        else -> listOf(
            GramPanchayatDetail("CENTRAL GP", 320, 320, 225, 195, "Active • Ongoing Clearance", isBest = true),
            GramPanchayatDetail("RURAL GP", 250, 250, 140, 110, "Pending • In Review", isPoor = true)
        )
    }
}

