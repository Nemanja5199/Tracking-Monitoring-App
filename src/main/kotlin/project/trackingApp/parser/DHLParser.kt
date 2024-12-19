package project.trackingApp.parser

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import org.springframework.web.multipart.MultipartFile
import project.trackingApp.csvParser.TrackingCSVParser
import project.trackingApp.dto.TrackingDTO
import project.trackingApp.error.TrackingError
import project.trackingApp.mapper.dhl.DHLMapper

class DHLParser(
    private val csvParser: TrackingCSVParser,
    private val mapper: DHLMapper
) {

    fun parse(file: MultipartFile, filename: String): Result<List<TrackingDTO>, TrackingError> = binding {
        val records = csvParser.parseFile(file).bind()
        records.map { record ->
            mapper.map(record, filename).bind()
        }
    }
}
