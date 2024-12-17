package project.trackingApp.csvParser

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class TrackingCSVParser {

    fun parseFile(file: MultipartFile): List<Map<String, String>> {
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

        return results
    }

    private fun isRowEmpty(row: org.apache.poi.ss.usermodel.Row): Boolean {
        return row.cellIterator().asSequence().all { cell ->
            cell.cellType == CellType.BLANK || cell.toString().trim().isEmpty()
        }
    }
}
