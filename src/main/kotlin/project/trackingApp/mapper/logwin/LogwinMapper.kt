package project.trackingApp.mapper.logwin

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import org.springframework.context.annotation.Configuration
import project.trackingApp.dto.TrackingDTO
import project.trackingApp.error.TrackingError
import project.trackingApp.mapper.BaseMapper

@Configuration
class LogwinMapper : BaseMapper {
    override fun map(data: Map<String, String>, filename: String): Result<TrackingDTO, TrackingError> {
        val houseNumber = data[LogwinFields.HOUSE]?.takeIf { it.isNotBlank() }
            ?: return Err(TrackingError.MissingRequiredField("Missing required field: House"))

        val status = data[LogwinFields.STATUS]?.takeIf { it.isNotBlank() }
            ?: return Err(TrackingError.MissingRequiredField("Missing required field: Status"))

        return binding {
            val etd = parseDateTime(data[LogwinFields.ETD], "Flight ETD").bind()
            val eta = parseDateTime(data[LogwinFields.ETA], "Flight ETA").bind()
            val atd = parseDateTime(data[LogwinFields.ATD], "Flight ATD").bind()
            val ata = parseDateTime(data[LogwinFields.ATA], "Flight ATA").bind()

            TrackingDTO(
                status = status,
                poNumber = data[LogwinFields.PO_NUMBER],
                etd = etd,
                eta = eta,
                atd = atd,
                ata = ata,
                packages = data[LogwinFields.PACKAGES]?.toIntOrNull(),
                weight = data[LogwinFields.WEIGHT]?.toDoubleOrNull(),
                volume = data[LogwinFields.VOLUME]?.toDoubleOrNull(),
                shipper = data[LogwinFields.SHIPPER],
                shipperCountry = null,
                receiver = data[LogwinFields.CONSIGNEE],
                receiverCountry = null,
                houseAwb = houseNumber,
                shipperRefNo = data[LogwinFields.SHIPPER_REF],
                carrier = "Logwin",
                incoTerm = null,
                flightNo = data[LogwinFields.VOYAGE_FLIGHT],
                pickUpDate = null,
                latestCheckpoint = data[LogwinFields.STATUS],
                sourceFilename = filename
            )
        }
    }
}
