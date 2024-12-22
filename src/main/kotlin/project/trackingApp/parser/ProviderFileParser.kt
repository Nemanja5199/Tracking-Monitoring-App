package project.trackingApp.parser

import com.github.michaelbull.result.Result
import org.springframework.web.multipart.MultipartFile
import project.trackingApp.error.TrackingError

interface ProviderFileParser {

    fun parseFile(file: MultipartFile): Result<List<Map<String, String>>, TrackingError>
}
