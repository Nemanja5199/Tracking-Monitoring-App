// BaseMapper.kt
package project.trackingApp.mapper

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import project.trackingApp.dto.TrackingDTO
import project.trackingApp.error.TrackingError
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

interface BaseMapper {
    fun map(data: Map<String, String>, filename: String): Result<TrackingDTO, TrackingError>

    fun parseDateTime(value: String?): Result<LocalDateTime?, String> {
        if (value.isNullOrBlank()) return Ok(null)

        // Handle Excel date format
        if (value.contains(".")) {
            val days = value.substringBefore(".").toDoubleOrNull()
                ?: return Err("Invalid Excel date format: $value")
            val date = Date(((days - 25569) * 86400000).toLong())
            return Ok(
                date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            )
        }

        // Handle standard date format (M/d/yy)
        val formatter = DateTimeFormatter.ofPattern("M/d/yy")
        return runCatching {
            val parsedDate = LocalDate.parse(value, formatter)
            Ok(parsedDate.atStartOfDay())
        }.getOrElse {
            Err("Invalid standard date format: $value")
        }
    }
}
