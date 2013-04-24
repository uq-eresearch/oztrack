package org.oztrack.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import au.com.bytecode.opencsv.CSVWriter;

public class ExcelToCsvConverter {

    public static void convertExcelToCsv(InputStream in, OutputStream out)
    throws IOException, InvalidFormatException, FileNotFoundException, UnsupportedEncodingException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Workbook workbook = WorkbookFactory.create(in);
        Sheet sheet = workbook.getSheetAt(0);
        CSVWriter csvWriter = null;
        try {
            csvWriter = new CSVWriter(new OutputStreamWriter(out, "UTF-8"));
            for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                String[] csvValues = new String[row.getLastCellNum()];
                for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    if (cell == null) {
                        csvValues[cellNum] = "";
                    }
                    else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            csvValues[cellNum] = dateFormat.format(cell.getDateCellValue());
                        } else {
                            csvValues[cellNum] = String.valueOf(cell.getNumericCellValue());
                        }
                    }
                    else {
                        csvValues[cellNum] = cell.getStringCellValue();
                    }
                }
                csvWriter.writeNext(csvValues);
            }
        }
        finally {
            try {csvWriter.close();} catch (Exception e) {}
        }
    }

}
