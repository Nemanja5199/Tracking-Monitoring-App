package project.trackingApp.dto

import com.github.michaelbull.result.Result
import project.trackingApp.error.TrackingError
import java.time.LocalDateTime

data class TrackingDTO(
    val status: String? = null,
    val poNumber: String? = null,
    val etd: LocalDateTime? = null,
    val eta: LocalDateTime?,
    val atd: LocalDateTime? = null,
    val ata: LocalDateTime? = null,
    val packages: Int? = null,
    val weight: Double? = null,
    val volume: Double? = null,
    val shipper: String? = null,
    val shipperCountry: String? = null,
    val receiver: String? = null,
    val receiverCountry: String? = null,
    val houseAwb: String? = null,
    val shipperRefNo: String? = null,
    val carrier: String? = null,
    val incoTerm: String? = null,
    val flightNo: String? = null,
    val pickUpDate: LocalDateTime?,
    val latestCheckpoint: String? = null,
    val sourceFilename: String
)