package project.trackingApp.mapper.hellmann

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import org.springframework.context.annotation.Configuration
import project.trackingApp.dto.TrackingDTO
import project.trackingApp.error.TrackingError
import project.trackingApp.mapper.BaseMapper
@Configuration
class HellmannMapper : BaseMapper {
    override fun map(data: Map<String, String>, filename: String): Result<TrackingDTO, TrackingError> {
        val houseAwb = data[HellmannFields.HOUSE_AWB]?.takeIf { it.isNotBlank() }
            ?: return Err(TrackingError.MissingRequiredField("Missing required field: House AWB"))

        return binding {
            val etd = parseDateTime(data[HellmannFields.FLIGHT_ETD], "Flight ETD").bind()
            val eta = parseDateTime(data[HellmannFields.FLIGHT_ETA], "Flight ETA").bind()
            val atd = parseDateTime(data[HellmannFields.FLIGHT_ATD], "Flight ATD").bind()
            val ata = parseDateTime(data[HellmannFields.FLIGHT_ATA], "Flight ATA").bind()

            val weight = data[HellmannFields.GROSS_WEIGHT]?.toDoubleOrNull()

            TrackingDTO(
                status = data[HellmannFields.STATUS],
                poNumber = null,
                etd = etd,
                eta = eta,
                atd = atd,
                ata = ata,
                packages = data[HellmannFields.PACKAGES]?.toIntOrNull(),
                weight = weight,
                volume = null,
                shipper = data[HellmannFields.SHIPPER_NAME],
                shipperCountry = data[HellmannFields.SHIPPER_COUNTRY],
                receiver = data[HellmannFields.CONSIGNEE_NAME],
                receiverCountry = data[HellmannFields.CONSIGNEE_COUNTRY],
                houseAwb = houseAwb,
                shipperRefNo = null,
                carrier = "Hellmann",
                incoTerm = data[HellmannFields.INCOTERM],
                flightNo = data[HellmannFields.FLIGHT_NO],
                pickUpDate = data[HellmannFields.PICKUP_DATE],
                latestCheckpoint = data[HellmannFields.STATUS],
                sourceFilename = filename
            )
        }
    }
}
