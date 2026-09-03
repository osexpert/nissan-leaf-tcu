package com.developerfromjokela.nissanleaftelematics.utils

/**
 * TCU's VIN field checksum. Despite being labeled "CRC" in TCU documentation/UI, it is not a
 * CRC-16 (verified against all 65536 possible CRC-16 polynomials, both bit orders - no match).
 *
 * Reverse engineered from two known-good VIN/checksum pairs read directly from real TCU units:
 * it is a plain 16-bit additive sum of the VIN's ASCII bytes, with the accumulator initialized
 * to 0xFFFF (i.e. sum of bytes, minus 1, wrapped to 16 bits) - stored big-endian (MSB first),
 * matching the byte order TCU returns it in.
 */
class VinChecksum {
    fun calculate(vin: ByteArray): Int {
        var sum = 0xFFFF
        for (b in vin) {
            sum = (sum + (b.toInt() and 0xFF)) and 0xFFFF
        }
        return sum
    }

    fun calculateBytesBigEndian(vin: ByteArray): ByteArray {
        val checksum = calculate(vin)
        return byteArrayOf(((checksum ushr 8) and 0xFF).toByte(), (checksum and 0xFF).toByte())
    }
}
