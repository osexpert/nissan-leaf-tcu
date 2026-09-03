package com.developerfromjokela.nissanleaftelematics.utils



data class DtcRecord(
    val byteHigh: Int,
    val byteMid: Int,
    val byteLow: Int,
    val status: DtcStatus
) {
    val rawCode: Int = (byteHigh shl 16) or (byteMid shl 8) or byteLow

    val code: String by lazy { decodeSaeCode() }

    private fun decodeSaeCode(): String {
        val category = when ((byteHigh shr 6) and 0x03) {
            0 -> 'P'
            1 -> 'C'
            2 -> 'B'
            else -> 'U'
        }
        val digit1 = (byteHigh shr 4) and 0x03
        val digit2 = byteHigh and 0x0F
        val digit3 = (byteMid shr 4) and 0x0F
        val digit4 = byteMid and 0x0F
        return "%c%d%X%X%X".format(category, digit1, digit2, digit3, digit4)
    }

    override fun toString(): String {
        val flags = status.activeFlags()
        val flagsStr = if (flags.isEmpty()) "none" else flags.joinToString(", ")
        return "%s (raw 0x%06X, status 0x%02X) -> %s".format(code, rawCode, status.raw, flagsStr)
    }
}

