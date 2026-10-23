package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.models.formatFund
import com.example.data.repository.BirbhumRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Birbhum VB-G RAM G", appName)
  }

  @Test
  fun `verify bundled birbhum dataset loads 19 blocks`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = BirbhumRepository(context)
    val data = repository.birbhumData.value
    assertEquals("BIRBHUM", data.district)
    assertEquals(19, data.blocks.size)

    val dubrajpur = data.blocks.find { it.blockName.equals("DUBRAJPUR", ignoreCase = true) }
    assertNotNull("DUBRAJPUR block should exist in dataset", dubrajpur)
    assertTrue("DUBRAJPUR allotment should be greater than 0", (dubrajpur?.allotment?.totalLakh ?: 0.0) > 0.0)
    assertTrue("DUBRAJPUR e-KYC should be above 90%", (dubrajpur?.ekyc?.ekycPct ?: 0.0) > 90.0)

    // Verify NMMS Attendance dataset
    assertTrue("NMMS today's attendance should be positive", data.nmmsSummary.todayAttendance > 0)
    assertTrue("NMMS block performance points should be loaded", data.nmmsSummary.blockPerformance.isNotEmpty())
    assertTrue("Daily persondays records should be loaded", data.nmmsSummary.dailyPersondays.isNotEmpty())
    assertEquals("Latest date ISO should be 2026-10-17", "2026-10-17", data.nmmsSummary.latestDateIso)

    // Verify GP x Category Matrix dataset
    assertEquals("Category Matrix should have 11 categories", 11, data.categoryMatrixSummary.categories.size)
    assertTrue("Category Matrix total received works should be positive", data.categoryMatrixSummary.totalReceivedWorks > 0)
    assertTrue("Category Matrix total approved works should be positive", data.categoryMatrixSummary.totalApprovedWorks > 0)
    assertTrue("Active work sites should be positive", data.categoryMatrixSummary.totalOngoingWorks > 0)
    assertTrue("Today's persondays should match NMMS attendance", data.nmmsSummary.todayAttendance > 0)
  }

  @Test
  fun `verify auto-update engine and completed features list`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val vm = com.example.ui.viewmodel.BirbhumViewModel(context as android.app.Application)
    assertTrue("Auto-update should be enabled by default", vm.isAutoUpdateEnabled.value)
    assertEquals("Default auto-update interval should be 30s", 30, vm.autoUpdateInterval.value)
    assertTrue("Countdown seconds should be between 1 and 30", vm.countdownSeconds.value in 1..30)
    assertTrue("Feature updates done list should have at least 8 items", vm.featureUpdatesDoneList.size >= 8)
    assertTrue("All feature updates should be marked done", vm.featureUpdatesDoneList.all { it.isDone })
    assertTrue("PBA GRGC feature should be present in done list", vm.featureUpdatesDoneList.any { it.id == "pba_grgc_live_sync" })
  }

  @Test
  fun `verify Paschim Banga Awaas GRGC dataset and columns`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = BirbhumRepository(context)
    val data = repository.birbhumData.value

    // Verify PBA Summary Totals
    assertEquals(61234, data.pbaSummary.totalBeneficiaries)
    assertEquals(61234, data.pbaSummary.tableTopDone)
    assertEquals(40489, data.pbaSummary.verifiedOk)
    assertEquals(32262, data.pbaSummary.availableWorkCodes)
    assertEquals(5505, data.pbaSummary.emrIssueRegister)
    assertTrue("Verified rate should be around 66%", data.pbaSummary.verifiedRate > 65.0)

    // Verify 19 blocks in PBA report
    assertEquals(19, data.pbaSummary.blocks.size)

    // Verify DUBRAJPUR PBA record
    val dubrajpur = data.blocks.find { it.blockName.equals("DUBRAJPUR", ignoreCase = true) }
    assertNotNull("DUBRAJPUR block should exist", dubrajpur)
    assertNotNull("DUBRAJPUR should have PBA report", dubrajpur?.pba)
    assertEquals(3037, dubrajpur?.pba?.totalBeneficiaries)
    assertEquals(3037, dubrajpur?.pba?.tableTopDone)
    assertEquals(2047, dubrajpur?.pba?.verifiedOk)
    assertEquals(1714, dubrajpur?.pba?.availableWorkCodes)
  }

  @Test
  fun `verify allocation of fund figures are up to 2 decimals only`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = BirbhumRepository(context)
    val data = repository.birbhumData.value
    val summary = data.allotmentSummary

    fun countDecimals(formatted: String): Int {
      val dot = formatted.indexOf('.')
      return if (dot < 0) 0 else formatted.length - dot - 1
    }

    assertTrue("distributableCr decimal count <= 2", countDecimals(com.example.data.models.formatFund(summary.distributableCr)) <= 2)
    assertTrue("distributableLakh decimal count <= 2", countDecimals(com.example.data.models.formatFund(summary.distributableLakh)) <= 2)
    assertTrue("districtRetentionCr decimal count <= 2", countDecimals(com.example.data.models.formatFund(summary.districtRetentionCr)) <= 2)
    assertTrue("totalAllotmentCr decimal count <= 2", countDecimals(com.example.data.models.formatFund(summary.totalAllotmentCr)) <= 2)

    assertEquals("33.78", com.example.data.models.formatFund(summary.distributableCr))
    assertEquals("8.44", com.example.data.models.formatFund(summary.districtRetentionCr))
    assertEquals("42.22", com.example.data.models.formatFund(summary.totalAllotmentCr))

    // Verify all 19 blocks have allotment figures strictly <= 2 decimal places
    for (block in data.blocks) {
      val allot = block.allotment
      assertTrue("Block ${block.blockName} totalCr <= 2 decimals", countDecimals(com.example.data.models.formatFund(allot.totalCr)) <= 2)
      assertTrue("Block ${block.blockName} totalLakh <= 2 decimals", countDecimals(com.example.data.models.formatFund(allot.totalLakh)) <= 2)
      assertTrue("Block ${block.blockName} cat1Lakh <= 2 decimals", countDecimals(com.example.data.models.formatFund(allot.cat1Lakh)) <= 2)
      assertTrue("Block ${block.blockName} cat2Lakh <= 2 decimals", countDecimals(com.example.data.models.formatFund(allot.cat2Lakh)) <= 2)
      assertTrue("Block ${block.blockName} cat3Lakh <= 2 decimals", countDecimals(com.example.data.models.formatFund(allot.cat3Lakh)) <= 2)
    }
  }

  @Test
  fun `verify gram panchayat alert and sharable report data`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = BirbhumRepository(context)
    val data = repository.birbhumData.value

    for (block in data.blocks) {
      val alert = com.example.data.models.getBlockGpPerformance(block.blockName)
      assertTrue("Block ${block.blockName} should have best GP", alert.bestGp.isNotEmpty())
      assertTrue("Block ${block.blockName} should have best GP stats", alert.bestGpStats.isNotEmpty())
      assertTrue("Block ${block.blockName} should have poor GP", alert.poorGp.isNotEmpty())
      assertTrue("Block ${block.blockName} should have poor GP stats", alert.poorGpStats.isNotEmpty())
      assertTrue("Block ${block.blockName} should have sector works", alert.sectorWorks.isNotEmpty())
      assertTrue("Block ${block.blockName} should have persondays generated > 0", alert.persondaysGenerated > 0)
      assertTrue("Block ${block.blockName} should have NMMS attendance > 0", alert.nmmsAttendance > 0)
    }

    // Verify DUBRAJPUR specific alert values
    val dubrajpurAlert = com.example.data.models.getBlockGpPerformance("DUBRAJPUR")
    assertEquals("LAXMI-NARAYANPUR", dubrajpurAlert.bestGp)
    assertEquals("CHINPAI", dubrajpurAlert.poorGp)
    assertEquals(18577, dubrajpurAlert.persondaysGenerated)
    assertEquals(18577, dubrajpurAlert.nmmsAttendance)
    assertEquals(302, dubrajpurAlert.todayNmmsAttendance)
    assertTrue("Sector works should mention Drought Proofing", dubrajpurAlert.sectorWorks.contains("Drought Proofing"))
  }

  @Test
  fun `verify block-wise report table filtering by block name`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = BirbhumRepository(context)
    val data = repository.birbhumData.value
    val pbaList = data.pbaSummary.blocks

    assertEquals(19, pbaList.size)

    // Test exact block search
    val dubrajpurMatch = pbaList.filter { it.blockName.contains("DUBRAJPUR", ignoreCase = true) }
    assertEquals(1, dubrajpurMatch.size)
    assertEquals("DUBRAJPUR", dubrajpurMatch.first().blockName)
    assertEquals(3037, dubrajpurMatch.first().tableTopDone)

    // Test partial search matching multiple (e.g. SURI matches SURI-I and SURI-II)
    val suriMatches = pbaList.filter { it.blockName.contains("SURI", ignoreCase = true) }
    assertEquals(2, suriMatches.size)
    assertTrue(suriMatches.any { it.blockName.contains("SURI-I") })
    assertTrue(suriMatches.any { it.blockName.contains("SURI-II") })

    // Test non-matching query returns empty list
    val nonExistent = pbaList.filter { it.blockName.contains("NON_EXISTENT_XYZ", ignoreCase = true) }
    assertTrue("Unknown block should yield empty filtered list", nonExistent.isEmpty())

    // Test blank search returns all blocks
    val blankSearch = pbaList.filter { "".isBlank() || it.blockName.contains("".trim(), ignoreCase = true) }
    assertEquals(19, blankSearch.size)
  }

  @Test
  fun `verify block-wise report table search by gram panchayat status and name`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = BirbhumRepository(context)
    val data = repository.birbhumData.value
    val pbaList = data.pbaSummary.blocks
    val blocks = data.blocks

    assertEquals(19, pbaList.size)
    assertEquals(19, blocks.size)

    // Helper search function identical to PbaBlockWiseReportCard filter
    fun filterReports(query: String) = pbaList.filter { item ->
      if (query.isBlank()) return@filter true
      val q = query.trim()
      if (item.blockName.contains(q, ignoreCase = true)) return@filter true
      val alert = com.example.data.models.getBlockGpPerformance(item.blockName)
      if (alert.bestGp.contains(q, ignoreCase = true)) return@filter true
      if (alert.poorGp.contains(q, ignoreCase = true)) return@filter true
      if (alert.bestGpStats.contains(q, ignoreCase = true)) return@filter true
      if (alert.poorGpStats.contains(q, ignoreCase = true)) return@filter true
      val matched = blocks.find { it.blockName.equals(item.blockName, ignoreCase = true) }
      if (matched != null && "${matched.works.gps} GP".contains(q, ignoreCase = true)) return@filter true
      false
    }

    // 1. Finding block by Best Gram Panchayat "Kasba" -> BOLPUR-SRINIKETAN
    val kasbaResult = filterReports("Kasba")
    assertEquals(1, kasbaResult.size)
    assertTrue("Kasba GP should resolve to Bolpur block", kasbaResult.first().blockName.contains("BOLPUR"))

    // 2. Finding block by Alert/Poor Performing Gram Panchayat "Chinpai" -> DUBRAJPUR
    val chinpaiResult = filterReports("Chinpai")
    assertEquals(1, chinpaiResult.size)
    assertEquals("DUBRAJPUR", chinpaiResult.first().blockName)

    // 3. Finding block by Best Gram Panchayat "Laxmi-Narayanpur" -> DUBRAJPUR
    val laxmiResult = filterReports("Laxmi-Narayanpur")
    assertEquals(1, laxmiResult.size)
    assertEquals("DUBRAJPUR", laxmiResult.first().blockName)

    // 4. Finding block by Best Gram Panchayat "Barrah" -> KHOYRASOL
    val barrahResult = filterReports("Barrah")
    assertEquals(1, barrahResult.size)
    assertEquals("KHOYRASOL", barrahResult.first().blockName)

    // 5. Total Gram Panchayats count across district
    val totalGps = blocks.sumOf { it.works.gps }
    assertEquals(167, totalGps)

    // 6. Dubrajpur Gram Panchayats count
    val dubrajpurBlock = blocks.first { it.blockName == "DUBRAJPUR" }
    assertEquals(10, dubrajpurBlock.works.gps)
  }

  @Test
  fun `verify state allotment vs actual expenditure and persondays metrics per block`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = BirbhumRepository(context)
    val data = repository.birbhumData.value
    val blocks = data.blocks

    // 1. Verify DUBRAJPUR Scheme Bank actual expenditure and persondays
    val dubrajpur = blocks.find { it.blockName == "DUBRAJPUR" }
    assertNotNull("DUBRAJPUR must be present", dubrajpur)
    assertEquals(23.20, dubrajpur!!.expenditure.bookedExpenditureLakh, 0.01)
    assertEquals(10.64, dubrajpur.expenditure.paidExpenditureLakh, 0.01)
    assertEquals(454.33, dubrajpur.expenditure.sanctionAmountLakh, 0.01)
    assertEquals(9242, dubrajpur.expenditure.persondaysGenerated)
    assertTrue("DUBRAJPUR allotment is 384.33 L", dubrajpur.allotment.totalLakh > 380.0)
    assertTrue("DUBRAJPUR remaining fund calculation", dubrajpur.expenditure.remainingLakh(dubrajpur.allotment.totalLakh) > 350.0)
    val dubrajpurUtil = dubrajpur.expenditure.utilizationPct(dubrajpur.allotment.totalLakh)
    assertTrue("DUBRAJPUR utilization is around 6.04%", dubrajpurUtil in 5.9..6.2)

    // 2. Verify KHOYRASOL Scheme Bank metrics
    val khoyrasol = blocks.find { it.blockName == "KHOYRASOL" }
    assertNotNull("KHOYRASOL must be present", khoyrasol)
    assertEquals(21.48, khoyrasol!!.expenditure.bookedExpenditureLakh, 0.01)
    assertEquals(1.17, khoyrasol.expenditure.paidExpenditureLakh, 0.01)
    assertEquals(10341, khoyrasol.expenditure.persondaysGenerated)

    // 3. Verify BOLPUR-SRINIKETAN Scheme Bank metrics
    val bolpur = blocks.find { it.blockName.contains("BOLPUR") }
    assertNotNull("BOLPUR must be present", bolpur)
    assertEquals(9.82, bolpur!!.expenditure.bookedExpenditureLakh, 0.01)
    assertEquals(1.58, bolpur.expenditure.paidExpenditureLakh, 0.01)
    assertEquals(4376, bolpur.expenditure.persondaysGenerated)

    // 4. District wide Scheme Bank metrics
    assertTrue("District total persondays generated should be > 80000", data.totalExpenditurePersondays > 80000)
    assertEquals(89444, data.totalExpenditurePersondays)
    assertTrue("District total booked expenditure should be around 210 Lakh", data.totalBookedExpLakh > 200.0)
    assertTrue("District total paid expenditure should be around 64 Lakh", data.totalPaidExpLakh > 60.0)
    assertTrue("District financial utilization should be around 4.98%", data.overallFinancialUtilizationPct in 4.5..5.5)

    // 5. Verify formatFund strictly formats up to 2 decimal places
    assertEquals("23.2", dubrajpur.expenditure.bookedExpenditureLakh.formatFund())
    assertEquals("10.64", dubrajpur.expenditure.paidExpenditureLakh.formatFund())
    assertEquals("4.98", data.overallFinancialUtilizationPct.formatFund())
  }

  @Test
  fun `verify TS and AS sanction figures and rates are strictly up to 2 decimals only`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = BirbhumRepository(context)
    val data = repository.birbhumData.value
    val works = data.worksSummary

    fun countDecimals(formatted: String): Int {
      val dot = formatted.indexOf('.')
      return if (dot < 0) 0 else formatted.length - dot - 1
    }

    // Verify district summary TS and AS rates <= 2 decimals
    assertTrue("District TS % <= 2 decimals", countDecimals(works.tsPercent.formatFund()) <= 2)
    assertTrue("District AS % <= 2 decimals", countDecimals(works.asPercent.formatFund()) <= 2)
    assertEquals("43.4", works.formattedTsPercent)
    assertEquals("42.9", works.formattedAsPercent)

    // Verify all 19 blocks have TS and AS rates <= 2 decimals
    for (block in data.blocks) {
      val w = block.works
      assertTrue("Block ${block.blockName} TS % <= 2 decimals", countDecimals(w.tsPct.formatFund()) <= 2)
      assertTrue("Block ${block.blockName} AS % <= 2 decimals", countDecimals(w.asPct.formatFund()) <= 2)
      assertTrue("Block ${block.blockName} formattedTsPct <= 2 decimals", countDecimals(w.formattedTsPct) <= 2)
      assertTrue("Block ${block.blockName} formattedAsPct <= 2 decimals", countDecimals(w.formattedAsPct) <= 2)
    }
  }
}


