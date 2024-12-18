package project.trackingApp.csvParser


import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.apache.poi.EncryptedDocumentException
import org.apache.poi.ss.formula.eval.NotImplementedException
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import project.trackingApp.error.TrackingError
import java.io.IOException

@Component
class TrackingCSVParser {

    fun parseFile(file: MultipartFile): Result<List<Map<String, String>>, TrackingError> = runCatching {

        if (file.isEmpty) return Err(TrackingError.FileParseError("File is empty"))

        val workbook = WorkbookFactory.create(file.inputStream)
        val sheet = workbook.getSheetAt(0)

        var headerRow = 0
        for (rowNum in 0..sheet.lastRowNum) {
            val row = sheet.getRow(rowNum)
            if (row != null) {
                for (cell in row) {
                    if (cell?.stringCellValue == "Payer Account Number") {
                        headerRow = rowNum
                        break
                    }
                }
            }
            if (headerRow != 0) break
        }

        val row = sheet.getRow(headerRow)
        val headers = row.map { cell ->
            when (cell?.cellType) {
                CellType.STRING -> cell.stringCellValue.trim()
                CellType.NUMERIC -> cell.numericCellValue.toString().trim()
                else -> ""
            }
        }.filter { it.isNotBlank() }

        val results = mutableListOf<Map<String, String>>()

        for (rowIndex in (headerRow + 1)..sheet.lastRowNum) {
            val dataRow = sheet.getRow(rowIndex) ?: continue
            if (isRowEmpty(dataRow)) continue

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

            if (rowData.isNotEmpty()) {
                results.add(rowData)
            }
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

    private fun isRowEmpty(row: org.apache.poi.ss.usermodel.Row): Boolean {
        return row.cellIterator().asSequence().all { cell ->
            cell.cellType == CellType.BLANK || cell.toString().trim().isEmpty()
        }
    }
}
