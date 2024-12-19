package project.trackingApp.service

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import project.trackingApp.csvParser.TrackingCSVParser
import project.trackingApp.dto.TrackingDTO
import project.trackingApp.error.TrackingError
import project.trackingApp.mapper.dhl.DHLMapper
import project.trackingApp.model.Tracking
import project.trackingApp.repository.TrackingRepository

@Service
class TrackingService(
    private val dhlMapper: DHLMapper,
    private val csvParser: TrackingCSVParser,
    private val trackingRepository: TrackingRepository
) {

    @Transactional
    fun processFile(file: MultipartFile, provider: String): Result<List<TrackingDTO>, TrackingError> = binding {

        val records = csvParser.parseFile(file).bind()


        val dtos = mutableListOf<TrackingDTO>()
        for (record in records) {
            val dto = dhlMapper.map(record, file.originalFilename ?: "unknown").bind()
            dtos.add(dto)
        }


        val entities = dtos.map { dto ->
            Tracking(
                status = dto.status,
                poNumber = dto.poNumber,
                etd = dto.etd,
                eta = dto.eta,
                atd = dto.atd,
                ata = dto.ata,
                packages = dto.packages,
                weight = dto.weight,
                volume = dto.volume,
                shipper = dto.shipper,
                shipperCountry = dto.shipperCountry,
                receiver = dto.receiver,
                receiverCountry = dto.receiverCountry,
                houseAwb = dto.houseAwb,
                shipperRefNo = dto.shipperRefNo,
                carrier = dto.carrier,
                incoTerm = dto.incoTerm,
                flightNo = dto.flightNo,
                pickUpDate = dto.pickUpDate,
                latestCheckpoint = dto.latestCheckpoint
            )
        }


        trackingRepository.saveAll(entities)
        dtos
    }
}
