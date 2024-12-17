package project.trackingApp.parser

import org.springframework.web.multipart.MultipartFile
import project.trackingApp.csvParser.TrackingCSVParser
import project.trackingApp.dto.TrackingDTO
import project.trackingApp.mapper.DHLMapper

class DHLParser(
    private val csvParser: TrackingCSVParser,
    private val mapper: DHLMapper
) {

    fun parse(file: MultipartFile, filename: String): List<TrackingDTO> {
        val records = csvParser.parseFile(file)
        return records.map { record ->
            mapper.map(record, filename)
        }
    }
}
