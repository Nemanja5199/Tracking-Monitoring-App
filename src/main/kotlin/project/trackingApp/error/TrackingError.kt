// TrackingError.kt
package project.trackingApp.error

import java.time.LocalDateTime

sealed class TrackingError {
    data class FileParseError(val message: String) : TrackingError()
    data class InvalidFileFormat(val message: String) : TrackingError()
    object NoDataToImport : TrackingError()
    data class InvalidTrackingNumber(val trackingNumber: String) : TrackingError()
    data class DuplicateShipment(val trackingNumber: String) : TrackingError()
    data class InvalidDateFormat(
        val field: String,
        val value: String,
        val expectedFormat: String
    ) : TrackingError()
    data class UnsupportedProvider(val provider: String) : TrackingError()
    data class ProviderMappingError(
        val provider: String,
        val field: String
    ) : TrackingError()
    data class DatabaseError(val operation: String, val message: String) : TrackingError()
    data class ShipmentNotFound(val trackingNumber: String) : TrackingError()
    data class InvalidShipmentData(
        val trackingNumber: String,
        val field: String,
        val message: String
    ) : TrackingError()
    data class InvalidTimelineSequence(
        val trackingNumber: String,
        val date: LocalDateTime,
        val event: String
    ) : TrackingError()
    data class UnexpectedError(val message: String) : TrackingError()
    data class ImportFailed(val message: String) : TrackingError()
    data class MissingRequiredField(val message: String) : TrackingError()
}

fun TrackingError.toErrorMessage(): String = when (this) {
    is TrackingError.FileParseError -> "Failed to parse file: $message"
    is TrackingError.InvalidFileFormat -> "Invalid file format: $message"
    is TrackingError.NoDataToImport -> "No data found to import"
    is TrackingError.InvalidTrackingNumber -> "Invalid tracking number: $trackingNumber"
    is TrackingError.DuplicateShipment -> "Duplicate shipment found: $trackingNumber"
    is TrackingError.InvalidDateFormat -> "Invalid date in $field: '$value' (Expected format: $expectedFormat)"
    is TrackingError.UnsupportedProvider -> "Unsupported provider: $provider"
    is TrackingError.ProviderMappingError -> "Failed to map $field for provider $provider"
    is TrackingError.DatabaseError -> "Database error during $operation: $message"
    is TrackingError.ShipmentNotFound -> "Shipment not found: $trackingNumber"
    is TrackingError.InvalidShipmentData -> "Invalid data for shipment $trackingNumber in field $field: $message"
    is TrackingError.InvalidTimelineSequence -> "Invalid timeline sequence for $trackingNumber at $date: $event"
    is TrackingError.UnexpectedError -> "Unexpected error: $message"
    is TrackingError.ImportFailed -> "Import failed: $message"
}
