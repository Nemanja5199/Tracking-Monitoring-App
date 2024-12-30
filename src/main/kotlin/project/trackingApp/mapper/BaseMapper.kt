
package project.trackingApp.mapper

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import project.trackingApp.dto.TrackingDTO
import project.trackingApp.error.TrackingError
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface BaseMapper {
    fun map(data: Map<String, String>, filename: String): Result<TrackingDTO, TrackingError>

    fun parseDateTime(value: String?, fieldName: String): Result<LocalDateTime?, TrackingError> {
        if (value.isNullOrBlank()) return Ok(null)

        val formatters = listOf(
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("M/d/yy"),
            DateTimeFormatter.ofPattern("dd-MMM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        )

        if (value.matches(Regex("^\\d+\\.?\\d*$"))) {
            val days = value.substringBefore(".").toDoubleOrNull() ?: return Err(
                TrackingError.InvalidDateFormat(
                    field = fieldName,
                    value = value,
                    expectedFormat = "Excel date format"
                )
            )
            val epoch = LocalDateTime.of(1899, 12, 30, 0, 0)
            return Ok(epoch.plusDays(days.toLong()))
        }

        return formatters.firstNotNullOfOrNull { formatter ->
            runCatching {
                val parsedDateTime = LocalDateTime.parse(value, formatter)
                Ok(parsedDateTime)
            }.getOrNull() ?: runCatching {
                val parsedDate = LocalDate.parse(value, formatter)
                Ok(parsedDate.atStartOfDay())
            }.getOrNull()
        } ?: Err(
            TrackingError.InvalidDateFormat(
                field = fieldName,
                value = value,
                expectedFormat = "Supported formats: dd.MM.yyyy HH:mm:ss, yyyy-MM-dd HH:mm:ss, dd.MM.yyyy, M/d/yy, dd-MMM-yyyy, yyyy-MM-dd"
            )
        )
    }
}
