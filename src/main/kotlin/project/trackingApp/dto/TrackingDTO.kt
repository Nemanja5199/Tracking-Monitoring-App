package project.trackingApp.dto

import project.trackingApp.model.Tracking
import java.time.LocalDateTime
import java.util.UUID

data class TrackingDTO(
    val id: UUID? = null,
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
    val pickUpDate: String?,
    val latestCheckpoint: String? = null,
    val sourceFilename: String? = null,
)

fun TrackingDTO.toTracking(): Tracking = Tracking(
    id = id,
    status = status,
    poNumber = poNumber,
    etd = etd,
    eta = eta,
    atd = atd,
    ata = ata,
    packages = packages,
    weight = weight,
    volume = volume,
    shipper = shipper,
    shipperCountry = shipperCountry,
    receiver = receiver,
    receiverCountry = receiverCountry,
    houseAwb = houseAwb,
    shipperRefNo = shipperRefNo,
    carrier = carrier,
    incoTerm = incoTerm,
    flightNo = flightNo,
    pickUpDate = pickUpDate,
    latestCheckpoint = latestCheckpoint,
    sourceFilename = sourceFilename
)

fun Tracking.toDTO(): TrackingDTO = TrackingDTO(
    id = id,
    status = status,
    poNumber = poNumber,
    etd = etd,
    eta = eta,
    atd = atd,
    ata = ata,
    packages = packages,
    weight = weight,
    volume = volume,
    shipper = shipper,
    shipperCountry = shipperCountry,
    receiver = receiver,
    receiverCountry = receiverCountry,
    houseAwb = houseAwb,
    shipperRefNo = shipperRefNo,
    carrier = carrier,
    incoTerm = incoTerm,
    flightNo = flightNo,
    pickUpDate = pickUpDate,
    latestCheckpoint = latestCheckpoint,
    sourceFilename = sourceFilename
)
