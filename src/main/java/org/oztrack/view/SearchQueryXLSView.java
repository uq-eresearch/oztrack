package org.oztrack.view;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.springframework.web.servlet.view.document.AbstractExcelView;

public class SearchQueryXLSView extends AbstractExcelView {
    private final Logger logger = Logger.getLogger(getClass());

    @SuppressWarnings("unused")
    private Project project;
    private List<PositionFix> positionFixes;
    private boolean includeDeleted;

    public SearchQueryXLSView(Project project, List<PositionFix> positionFixes, boolean includeDeleted) {
        this.project = project;
        this.positionFixes = positionFixes;
        this.includeDeleted = includeDeleted;
    }

    @Override
    protected void buildExcelDocument(
        @SuppressWarnings("rawtypes") Map model,
        HSSFWorkbook workbook,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        createPositionFixSheet(workbook);
        response.setHeader("Content-Disposition", "attachment; filename=\"export.xls\"");
    }


    protected void createPositionFixSheet(HSSFWorkbook workbook) {
        HSSFSheet sheet = workbook.createSheet("Sheet");

        int rowNum = 0;

        HSSFRow headerRow = sheet.createRow(rowNum++);
        {
            int colNum = 0;
            headerRow.createCell(colNum++).setCellValue("ANIMALID");
            headerRow.createCell(colNum++).setCellValue("DATE");
            headerRow.createCell(colNum++).setCellValue("LATITUDE");
            headerRow.createCell(colNum++).setCellValue("LONGITUDE");
            if (includeDeleted) {
                headerRow.createCell(colNum++).setCellValue("DELETED");
            }
        }

        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle dateTimeCellStyle = workbook.createCellStyle();
        dateTimeCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
        CellStyle latLngCellStyle = workbook.createCellStyle();
        latLngCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("0.000000"));

        for (PositionFix positionFix : positionFixes) {
            try {
                int colNum = 0;
                HSSFRow row = sheet.createRow(rowNum++);

                HSSFCell animalIdCell = row.createCell(colNum++);
                animalIdCell.setCellType(Cell.CELL_TYPE_STRING);
                animalIdCell.setCellValue(positionFix.getAnimal().getProjectAnimalId());

                HSSFCell detectionTimeCell = row.createCell(colNum++);
                detectionTimeCell.setCellType(Cell.CELL_TYPE_NUMERIC);
                detectionTimeCell.setCellValue(positionFix.getDetectionTime());
                detectionTimeCell.setCellStyle(dateTimeCellStyle);

                HSSFCell latCell = row.createCell(colNum++);
                latCell.setCellType(Cell.CELL_TYPE_NUMERIC);
                latCell.setCellValue(Double.parseDouble(positionFix.getLatitude()));
                latCell.setCellStyle(latLngCellStyle);

                HSSFCell lngCell = row.createCell(colNum++);
                lngCell.setCellType(Cell.CELL_TYPE_NUMERIC);
                lngCell.setCellValue(Double.parseDouble(positionFix.getLongitude()));
                lngCell.setCellStyle(latLngCellStyle);

                if (includeDeleted) {
                    HSSFCell deletedCell = row.createCell(colNum++);
                    deletedCell.setCellType(Cell.CELL_TYPE_BOOLEAN);
                    deletedCell.setCellValue((positionFix.getDeleted() != null) && positionFix.getDeleted());
                }
            }
            catch (Exception e) {
                logger.error("Error writing position fix " + positionFix.getId() + " to XLS", e);
            }
        }

        sheet.createFreezePane(0, 1, 0, 1);
        for (short cellNum = headerRow.getFirstCellNum(); cellNum < headerRow.getLastCellNum(); cellNum++) {
            sheet.autoSizeColumn(cellNum);
        }
    }
}
