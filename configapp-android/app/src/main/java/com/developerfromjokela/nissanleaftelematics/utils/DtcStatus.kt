package com.developerfromjokela.nissanleaftelematics.utils

data class DtcStatus(val raw: Int) {
    val testFailed = raw and 0x01 != 0
    val testFailedThisOperationCycle = raw and 0x02 != 0
    val pendingDTC = raw and 0x04 != 0
    val confirmedDTC = raw and 0x08 != 0
    val testNotCompletedSinceLastClear = raw and 0x10 != 0
    val testFailedSinceLastClear = raw and 0x20 != 0
    val testNotCompletedThisOperationCycle = raw and 0x40 != 0
    val warningIndicatorRequested = raw and 0x80 != 0

    fun activeFlags(): List<String> = buildList {
        if (testFailed) add("Test Failed")
        if (testFailedThisOperationCycle) add("Test Failed This Operation Cycle")
        if (pendingDTC) add("Pending DTC")
        if (confirmedDTC) add("Confirmed DTC")
        if (testNotCompletedSinceLastClear) add("Test Not Completed Since Last Clear")
        if (testFailedSinceLastClear) add("Test Failed Since Last Clear")
        if (testNotCompletedThisOperationCycle) add("Test Not Completed This Operation Cycle")
        if (warningIndicatorRequested) add("Warning Indicator Requested")
    }
}
