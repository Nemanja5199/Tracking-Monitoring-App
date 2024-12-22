package project.trackingApp.mapper.dhl

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import project.trackingApp.dto.TrackingDTO
import project.trackingApp.error.TrackingError
import project.trackingApp.mapper.BaseMapper

class DHLMapper : BaseMapper {
    override fun map(data: Map<String, String>, filename: String): Result<TrackingDTO, TrackingError> {

        val latestCheckpoint = data[DHLFields.LATEST_CHECKPOINT]?.takeIf { it.isNotBlank() }
            ?: return Err(TrackingError.MissingRequiredField("Missing required field: latestCheckpoint"))

        val waybillNumber = data[DHLFields.WAYBILL_NUMBER]?.takeIf { it.isNotBlank() }
            ?: return Err(TrackingError.MissingRequiredField("Missing required field: waybillNumber"))

        return binding {

            val estimatedDelivery = parseDateTime(data[DHLFields.ESTIMATED_DELIVERY], "Estimated Delivery Date").bind()
            val pickupDate = parseDateTime(data[DHLFields.PICKUP_DATE], "Pickup Date").bind()
            val weight = parseWeight(data[DHLFields.WEIGHT])

            TrackingDTO(
                status = data[DHLFields.LATEST_CHECKPOINT],
                poNumber = null,
                etd = null,
                eta = estimatedDelivery,
                atd = null,
                ata = null,
                packages = data[DHLFields.PIECES]?.toIntOrNull(),
                weight = weight,
                volume = null,
                shipper = null,
                shipperCountry = data[DHLFields.ORIGIN_COUNTRY],
                receiver = data[DHLFields.RECEIVER],
                receiverCountry = data[DHLFields.DESTINATION_COUNTRY],
                houseAwb = waybillNumber,
                shipperRefNo = data[DHLFields.SHIPPER_REF],
                carrier = "DHL",
                incoTerm = null,
                flightNo = null,
                pickUpDate = pickupDate,
                latestCheckpoint = latestCheckpoint,
                sourceFilename = filename
            )
        }
    }

    private fun parseWeight(weight: String?): Double? {

        if (weight.isNullOrBlank()) return null

        return weight.replace("KG", "").trim().toDoubleOrNull()
    }
}
