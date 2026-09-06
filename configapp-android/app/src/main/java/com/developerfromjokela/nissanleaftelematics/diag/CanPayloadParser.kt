package com.developerfromjokela.nissanleaftelematics.diag

data class CanPacket(
    val dataLength: Int,
    val data: ByteArray? = null
)

class CanPayloadParser {
    @OptIn(ExperimentalStdlibApi::class)
    fun parse(hexInput: String, skipIntegrityCheck: Boolean = false): CanPacket {
        val cleanHex = hexInput.replace(Regex("\\s+"), "")
        if (cleanHex.length < 8) throw IllegalArgumentException("Input too short: $cleanHex")

        var dataLength = 0
        var data: ByteArray? = null
        for (chunk in cleanHex.chunked(16)) {
            val pciByte = chunk.substring(0, 2).toInt(radix = 16)
            val pciType = pciByte and 0xF0
            println(pciByte)
            if (pciType == 0x30) {
                // Flow Control frame leaked into the stream (e.g. echoed request) - not payload, skip it
                println("Skipping Flow Control frame $chunk")
                continue
            } else if (pciType == 0x10) {
                if (chunk.length < 8) {
                    println("Header malformed $chunk")
                    continue
                }
                println(chunk)
                // First Frame: length is 12 bits - low nibble of PCI byte + full second byte
                dataLength = ((pciByte and 0x0F) shl 8) or chunk.substring(2, 4).toInt(16)
                val firstDataChunk = chunk.substring(4).hexToByteArray()
                data = ByteArray(0)
                data += firstDataChunk
            } else if (pciType == 0x20 && data != null && dataLength > data.size) {
                // Consecutive Frame
                val newChunk = chunk.substring(2).hexToByteArray()
                println("datasize: ${data.size}, newchunksize: ${newChunk.size}, maxlen: $dataLength")
                data += if (data.size+newChunk.size > dataLength) {
                    println("overflow, cutting down to 0 -> ${dataLength-data.size}")
                    val newSplittedArr = ByteArray(dataLength-data.size)
                    newChunk.copyInto(newSplittedArr, 0, 0, dataLength-data.size)
                    newSplittedArr
                } else {
                    newChunk
                }
            } else {
                // Single Frame: length is the low nibble of the PCI byte
                dataLength = pciByte and 0x0F
                val firstDataChunk = chunk.substring(2).hexToByteArray()
                data = ByteArray(0)
                data += firstDataChunk.copyOf(dataLength)
            }
        }
        println("DATALEN $dataLength, READDATA:${data?.size}")
        println(data)

        if (dataLength != data?.size && !skipIntegrityCheck) {
            throw Exception("Data length mismatch! Missing ${dataLength-(data?.size ?: 0)} bytes! Please try reading again.")
        }

        return CanPacket(
            dataLength = dataLength,
            data = data
        )
    }

    fun toReadableString(packet: CanPacket): String? {
        return packet.data?.let { String(it).trim('\u0000') }
    }
}
