package project.trackingApp.controller

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.mapBoth
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import project.trackingApp.dto.TrackingDTO
import project.trackingApp.error.TrackingError
import project.trackingApp.error.toErrorMessage
import project.trackingApp.service.TrackingService

@RestController
@RequestMapping("/api/tracking")
class TrackingController(private val trackingService: TrackingService) {

    @PostMapping("/upload")
    fun uploadFile(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("provider") provider: String
    ): ResponseEntity<out Any> = binding{
        val results = trackingService.processFile(file, provider).bind()
         ResponseEntity.ok(results)
    }.mapBoth(
        success= {response -> response},
        failure= { error ->
            when (error) {
                is TrackingError.FileParseError -> ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("message" to error.toErrorMessage()))

                is TrackingError.InvalidFileFormat -> ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("message" to error.toErrorMessage()))

                is TrackingError.NoDataToImport -> ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("message" to error.toErrorMessage()))

                is TrackingError.InvalidTrackingNumber -> ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("message" to error.toErrorMessage()))

                is TrackingError.DuplicateShipment -> ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(mapOf("message" to error.toErrorMessage()))

                is TrackingError.InvalidDateFormat -> ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("message" to error.toErrorMessage()))

                is TrackingError.UnsupportedProvider -> ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("message" to error.toErrorMessage()))

                is TrackingError.ProviderMappingError -> ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("message" to error.toErrorMessage()))

                is TrackingError.DatabaseError -> ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(mapOf("message" to error.toErrorMessage()))

                is TrackingError.MissingRequiredField -> ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("message" to error.toErrorMessage()))

                else -> ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(mapOf("message" to error.toErrorMessage()))
            }
        }
    )
}
