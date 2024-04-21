package camel.BoostMarketer.common.util;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExcelUtil {

    public static void excelUpload(MultipartFile file, List<HashMap<String, Object>> list) throws Exception {
        HashMap<String, Object> data;
        Workbook workbook;
        Sheet sheet;
        Row row;
        Cell cell;

        OPCPackage opcPackage = OPCPackage.open(file.getInputStream());
        workbook = new XSSFWorkbook(opcPackage);
        sheet = workbook.getSheetAt(0);
        List<String> keyList = new ArrayList<>();

        int idx = 0;
        for (Row item : sheet) {
            data = new HashMap<>();
            row = item;
            int cells = row.getLastCellNum(); // Use getLastCellNum to consider all cells

            for (int columnIndex = 0; columnIndex < cells; columnIndex++) {
                cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                String value = getStringValue(cell, columnIndex);
//                if (idx == 0) {
//                    keyList.add(value);
//                } else {
//                    data.put(String.valueOf(++columnIndex), value);
//                }

                if (idx != 0) {
                    int num = columnIndex+1;
                    data.put(String.valueOf(num), value);
                }
            }
            if (idx != 0) {
                list.add(data);
            }
            idx++;
        }
    }


    private static String getStringValue(Cell cell, int columnIndex) {
        String rtnValue = "";

        switch (cell.getCellType()) {
            case STRING:
                rtnValue = cell.getStringCellValue();
                break;
            case NUMERIC:
                if(columnIndex == 0){
                    rtnValue = String.valueOf((int)cell.getNumericCellValue());
                }else{
                    rtnValue = Double.toString(cell.getNumericCellValue());
                }
                break;
            case BLANK:
                rtnValue = ""; // Handle blank cells
                break;
            // Add cases for other types as needed
            default:
                rtnValue = "Unsupported Cell Type";
                break;
        }
        return rtnValue;
    }

    public static ByteArrayInputStream createExcelFile(List<String> categories) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet("keyword_excel");

            // Define header style with borders
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Set font
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Set borders
            headerStyle.setBorderTop(BorderStyle.MEDIUM);
            headerStyle.setBorderBottom(BorderStyle.MEDIUM);
            headerStyle.setBorderLeft(BorderStyle.MEDIUM);
            headerStyle.setBorderRight(BorderStyle.MEDIUM);

            // Set alignment
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Creating the header row with styles
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < categories.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(categories.get(i));
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 20 * 256); // Width set to about 20 characters wide
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to import data to Excel file: " + e.getMessage());
        }
    }
}
