// BaseMapper.kt
package project.trackingApp.mapper

import project.trackingApp.dto.TrackingDTO
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

interface BaseMapper {
    fun map(data: Map<String, String>, filename: String): TrackingDTO

    fun parseDateTime(value: String?): LocalDateTime? {
        if (value.isNullOrBlank()) return null

        if (value.contains(".")) {
            val days = value.substringBefore(".").toDouble()
            val date = Date(((days - 25569) * 86400000).toLong())
            return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        }

        val formatter = DateTimeFormatter.ofPattern("M/d/yy")
        val parsedDate = runCatching { LocalDate.parse(value, formatter) }.getOrNull()
        return parsedDate?.atStartOfDay()
    }
}
