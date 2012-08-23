package org.oztrack.view;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.types.ProjectType;
import org.springframework.web.servlet.view.document.AbstractExcelView;

public class SearchQueryXLSView extends AbstractExcelView {
    protected final Log logger = LogFactory.getLog(getClass());

    private Project project;
    private List<PositionFix> positionFixes;

    public SearchQueryXLSView(Project project, List<PositionFix> positionFixes) {
        this.project = project;
        this.positionFixes = positionFixes;
    }

    @Override
    protected void buildExcelDocument(
        @SuppressWarnings("rawtypes") Map model,
        HSSFWorkbook workbook,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        if (project.getProjectType() != ProjectType.GPS) {
            throw new IllegalArgumentException("Can only export " + ProjectType.GPS.getDisplayName() + " projects");
        }
        createPositionFixSheet(workbook);
        response.setHeader("Content-Disposition", "attachment; filename=\"export.xls\"");
    }


    protected void createPositionFixSheet(HSSFWorkbook workbook) {
        HSSFSheet sheet = workbook.createSheet("Sheet");

        int rowNum = 0;

        HSSFRow headerRow = sheet.createRow(rowNum++);
        {
            int colNum = 0;
            headerRow.createCell(colNum++).setCellValue("Animal ID");
            headerRow.createCell(colNum++).setCellValue("Detection Time");
            headerRow.createCell(colNum++).setCellValue("Latitude");
            headerRow.createCell(colNum++).setCellValue("Longitude");
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
                animalIdCell.setCellValue(positionFix.getAnimal().getId());

                HSSFCell detectionTimeCell = row.createCell(colNum++);
                detectionTimeCell.setCellValue(positionFix.getDetectionTime());
                detectionTimeCell.setCellStyle(dateTimeCellStyle);

                HSSFCell latCell = row.createCell(colNum++);
                latCell.setCellValue(Double.parseDouble(positionFix.getLatitude()));
                latCell.setCellStyle(latLngCellStyle);

                HSSFCell lngCell = row.createCell(colNum++);
                lngCell.setCellValue(Double.parseDouble(positionFix.getLongitude()));
                lngCell.setCellStyle(latLngCellStyle);
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
