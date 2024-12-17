package project.trackingApp.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import project.trackingApp.csvParser.TrackingCSVParser
import project.trackingApp.dto.TrackingDTO
import project.trackingApp.mapper.DHLMapper
import project.trackingApp.model.Tracking
import project.trackingApp.repository.TrackingRepository

@Service
class TrackingService(
    private val dhlMapper: DHLMapper,
    private val csvParser: TrackingCSVParser,
    private val trackingRepository: TrackingRepository
) {

    @Transactional
    fun processFile(file: MultipartFile, provider: String): List<TrackingDTO> {

        val records = csvParser.parseFile(file)
        val dtos = records.map { record ->
            dhlMapper.map(record, file.originalFilename ?: "unknown")
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
        return dtos
    }
}
