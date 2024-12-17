package project.trackingApp.mapper

import project.trackingApp.dto.TrackingDTO
import project.trackingApp.parser.DHLFields

class DHLMapper : BaseMapper {
    override fun map(data: Map<String, String>, filename: String): TrackingDTO {

        return TrackingDTO(

            status = data[DHLFields.LATEST_CHECKPOINT],
            poNumber = null,
            etd = null,
            eta = parseDateTime(data[DHLFields.ESTIMATED_DELIVERY]),
            atd = null,
            ata = null,
            packages = data[DHLFields.PIECES]?.toIntOrNull(),
            weight = parseWeight(data[DHLFields.WEIGHT]),
            volume = null,
            shipper = null,
            shipperCountry = data[DHLFields.ORIGIN_COUNTRY],
            receiver = data[DHLFields.RECEIVER],
            receiverCountry = data[DHLFields.DESTINATION_COUNTRY],
            houseAwb = data[DHLFields.WAYBILL_NUMBER],
            shipperRefNo = data[DHLFields.SHIPPER_REF],
            carrier = "DHL",
            incoTerm = null,
            flightNo = null,
            pickUpDate = parseDateTime(data[DHLFields.PICKUP_DATE]),
            latestCheckpoint = createLatestCheckpoint(
                data[DHLFields.LATEST_CHECKPOINT],
                data[DHLFields.LATEST_CHECKPOINT_DATE]
            ),

            sourceFilename = filename

        )
    }

    private fun parseWeight(weight: String?): Double? {

        if (weight.isNullOrBlank()) return null

        return weight.replace("KG", "").trim().toDoubleOrNull()
    }

    private fun createLatestCheckpoint(checkpoint: String?, date: String?): String? {

        return when {
            checkpoint.isNullOrBlank() -> null
            date.isNullOrBlank() -> checkpoint
            else -> "$checkpoint (at $date)"
        }
    }
}
