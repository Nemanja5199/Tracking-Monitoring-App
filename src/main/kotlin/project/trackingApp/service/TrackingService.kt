package project.trackingApp.service

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.mapResult
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import project.trackingApp.dto.TrackingDTO
import project.trackingApp.dto.toTracking
import project.trackingApp.error.TrackingError
import project.trackingApp.mapper.dhl.DHLMapper
import project.trackingApp.mapper.hellmann.HellmannMapper
import project.trackingApp.mapper.logwin.LogwinMapper
import project.trackingApp.parser.DhlParser
import project.trackingApp.parser.HellmannParser
import project.trackingApp.parser.LogwinParser
import project.trackingApp.repository.TrackingRepository

@Service
class TrackingService(
    private val dhlMapper: DHLMapper,
    private val hellmannMapper: HellmannMapper,
    private val logwinMapper: LogwinMapper,
    private val dhlParser: DhlParser,
    private val logwinParser: LogwinParser,
    private val hellmannParser: HellmannParser,
    private val trackingRepository: TrackingRepository
) {

    @Transactional
    fun processFile(file: MultipartFile, provider: String): Result<List<TrackingDTO>, TrackingError> = binding {

        val records = when (provider.lowercase()) {
            "dhl" -> dhlParser.parseFile(file)
            "hellmann" -> hellmannParser.parseFile(file)
            "logwin" -> logwinParser.parseFile(file)
            else -> Err(TrackingError.UnsupportedProvider(provider))
        }.bind()

        val dtos = when (provider.lowercase()) {
            "dhl" -> records.mapResult { record -> dhlMapper.map(record, file.originalFilename ?: "unknown") }
            "hellmann" -> records.mapResult { record -> hellmannMapper.map(record, file.originalFilename ?: "unknown") }
            "logwin" -> records.mapResult { record -> logwinMapper.map(record, file.originalFilename ?: "unknown") }
            else -> Err(TrackingError.UnsupportedProvider(provider))
        }.bind()

        val entities = dtos.map { it.toTracking() }

        trackingRepository.saveAll(entities)
        dtos
    }
}
