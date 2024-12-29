package project.trackingApp.parser

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.apache.poi.EncryptedDocumentException
import org.apache.poi.ss.formula.eval.NotImplementedException
import org.apache.poi.ss.usermodel.*
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import project.trackingApp.error.TrackingError
import java.io.IOException
import java.text.SimpleDateFormat

@Component
class HellmannParser : ProviderFileParser {
    override fun parseFile(file: MultipartFile): Result<List<Map<String, String>>, TrackingError> {
        return runCatching {
            if (file.isEmpty) return@runCatching Err(TrackingError.FileParseError("File is empty"))

            val workbook = WorkbookFactory.create(file.inputStream)
            val sheet = workbook.getSheetAt(1)


            val headerRow = findHeaderRow(sheet)
            val headers = extractHeaders(sheet.getRow(headerRow))
            val results = parseDataRows(sheet, headerRow, headers)

            if (results.isNotEmpty()) {
                println("Sample row: ${results.first()}")
            }

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

    private fun findHeaderRow(sheet: Sheet): Int {
        val potentialHeaders = listOf(
            "Status",
            "House AWB",
            "Shipper Name",
            "Shipper Country",
            "Consignee Name"
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

        return (1..row.lastCellNum).mapNotNull { cellIndex ->
            val cell = row.getCell(cellIndex)
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

        // Start from index 1 (column B) to skip the empty first column
        headers.forEachIndexed { headerIndex, header ->
            // Add 1 to headerIndex to account for the empty first column
            val cell = dataRow.getCell(headerIndex + 1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)

            val value = when (cell?.cellType) {
                CellType.STRING -> cell.stringCellValue.trim()
                CellType.NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        SimpleDateFormat("dd.MM.yyyy").format(cell.dateCellValue)
                    } else {
                        when {
                            header.contains("Weight") -> String.format("%.2f", cell.numericCellValue)
                            header == "No of Packages" -> cell.numericCellValue.toInt().toString()
                            else -> cell.numericCellValue.toString().replace(".0", "").trim()
                        }
                    }
                }
                CellType.BLANK -> ""
                null -> ""
                else -> cell.toString().trim()
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
