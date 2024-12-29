
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

    fun parseDateTime(value: String?, fieldName: String): Result<LocalDateTime?, TrackingError> {
        if (value.isNullOrBlank()) return Ok(null)

        val formatters = listOf(
            DateTimeFormatter.ofPattern("M/d/yy"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("dd-MMM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        )

        if (value.contains(".")) {
            val days = value.substringBefore(".").toDoubleOrNull()
                ?: return Err(
                    TrackingError.InvalidDateFormat(
                        field = fieldName,
                        value = value,
                        expectedFormat = "Excel date format"
                    )
                )

            val date = Date(((days - 25569) * 86400000).toLong())
            return Ok(
                date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            )
        }

        return formatters.firstNotNullOfOrNull { formatter ->
            try {
                val parsedDate = LocalDate.parse(value, formatter)
                Ok(parsedDate.atStartOfDay())
            } catch (e: Exception) {
                null
            }
        } ?: Err(
            TrackingError.InvalidDateFormat(
                field = fieldName,
                value = value,
                expectedFormat = "Supported formats: M/d/yy, dd.MM.yyyy, dd-MMM-yyyy, yyyy-MM-dd"
            )
        )
    }
}

