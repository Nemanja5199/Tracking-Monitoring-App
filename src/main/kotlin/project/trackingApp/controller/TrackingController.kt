package project.trackingApp.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import project.trackingApp.dto.TrackingDTO
import project.trackingApp.service.TrackingService

@RestController
@RequestMapping("/api/tracking")
class TrackingController(private val trackingService: TrackingService) {

    @PostMapping("/upload")
    fun uploadFile(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("provider") provider: String
    ): ResponseEntity<List<TrackingDTO>> {
        val results = trackingService.processFile(file, provider)
        return ResponseEntity.ok(results)
    }
}
