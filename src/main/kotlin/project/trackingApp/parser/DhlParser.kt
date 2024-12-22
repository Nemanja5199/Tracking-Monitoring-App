package project.trackingApp.parser

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.apache.poi.EncryptedDocumentException
import org.apache.poi.ss.formula.eval.NotImplementedException
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import project.trackingApp.error.TrackingError
import java.io.IOException

@Component
class DhlParser : ProviderFileParser {
    override fun parseFile(file: MultipartFile): Result<List<Map<String, String>>, TrackingError> {
        return runCatching {
            if (file.isEmpty) return@runCatching Err(TrackingError.FileParseError("File is empty"))

            val workbook = WorkbookFactory.create(file.inputStream)
            val sheet = workbook.getSheetAt(0)

            val headerRow = findDhlHeaderRow(sheet)
            val headers = extractHeaders(sheet.getRow(headerRow))
            val results = parseDataRows(sheet, headerRow, headers)

            Ok(results)
        }.getOrElse { e ->
            when (e) {
                is IOException -> Err(
                    TrackingError.FileParseError(
                        "Failed to read file: ${e.message}"
                    )
                )
                is IllegalArgumentException -> Err(
                    TrackingError.InvalidFileFormat(
                        "Invalid file format: ${e.message}"
                    )
                )
                is EncryptedDocumentException -> Err(
                    TrackingError.FileParseError(
                        "File is password protected or encrypted"
                    )
                )
                is NotImplementedException -> Err(
                    TrackingError.InvalidFileFormat(
                        "Unsupported Excel feature or formula"
                    )
                )
                is ArrayIndexOutOfBoundsException -> Err(
                    TrackingError.InvalidFileFormat(
                        "Invalid sheet or cell reference"
                    )
                )
                is NullPointerException -> Err(
                    TrackingError.InvalidFileFormat(
                        "Missing required data in Excel file"
                    )
                )
                else -> Err(
                    TrackingError.UnexpectedError(
                        "An unexpected error occurred: ${e.message}"
                    )
                )
            }
        }
    }

    private fun findDhlHeaderRow(sheet: Sheet): Int {
        val potentialHeaders = listOf(
            "Payer Account Number",
            "Pickup Date",
            "Origin Country/Territory",
            "IATA code",
            "Origin City",
            "IATA Code",
            "Destination Country/Territory",
            "Destination City",
            "Waybill Number",
            "Shipper Reference Number",
            "Receiver",
            "Receiver Postal Code",
            "Product Code",
            "Pieces",
            "Piece ID",
            "Manifested Weight",
            "Estimated Delivery Date",
            "Last Checkpoint Code",
            "Latest Checkpoint Date/Time",
            "Latest Checkpoint",
            "Latest Checkpoint's Remarks",
            "Location of Scan",
            "Customer Uploaded Comments",
            "Comments"
        )

        for (rowNum in 0..sheet.lastRowNum) {
            val row = sheet.getRow(rowNum)
            if (row != null) {

                val rowValues = row.map { cell ->
                    when (cell?.cellType) {
                        CellType.STRING -> cell.stringCellValue?.trim() ?: ""
                        CellType.NUMERIC -> cell.numericCellValue.toString().trim()
                        CellType.BLANK -> ""
                        else -> cell?.toString()?.trim() ?: ""
                    }
                }

                val matchedHeaderCount = potentialHeaders.count { headerKeyword ->
                    rowValues.any { it.contains(headerKeyword, ignoreCase = true) }
                }

                if (matchedHeaderCount >= potentialHeaders.size / 2) {
                    return rowNum
                }
            }
        }

        throw IllegalArgumentException("Could not find header row")
    }

    private fun extractHeaders(row: Row): List<String> {
        return row.map { cell ->
            when (cell?.cellType) {
                CellType.STRING -> cell.stringCellValue.trim()
                CellType.NUMERIC -> cell.numericCellValue.toString().trim()
                else -> ""
            }
        }.filter { it.isNotBlank() }
    }

    private fun parseDataRows(
        sheet: Sheet,
        headerRow: Int,
        headers: List<String>
    ): MutableList<Map<String, String>> {
        val results = mutableListOf<Map<String, String>>()

        for (rowIndex in (headerRow + 1)..sheet.lastRowNum) {
            val dataRow = sheet.getRow(rowIndex) ?: continue

            // Skip empty rows
            if (isRowEmpty(dataRow)) continue

            val rowData = parseRowData(dataRow, headers)

            if (rowData.isNotEmpty()) {
                results.add(rowData)
            }
        }

        return results
    }

    private fun parseRowData(
        dataRow: Row,
        headers: List<String>
    ): MutableMap<String, String> {
        val rowData = mutableMapOf<String, String>()

        headers.forEachIndexed { index, header ->
            val cell = dataRow.getCell(index)
            val value = when (cell?.cellType) {
                CellType.STRING -> cell.stringCellValue.trim()
                CellType.NUMERIC -> cell.numericCellValue.toString().trim()
                else -> ""
            }

            if (value.isNotBlank()) {
                rowData[header] = value
            }
        }

        return rowData
    }

    private fun isRowEmpty(row: Row): Boolean {
        return row.cellIterator().asSequence().all { cell ->
            cell.cellType == CellType.BLANK || cell.toString().trim().isEmpty()
        }
    }
}
