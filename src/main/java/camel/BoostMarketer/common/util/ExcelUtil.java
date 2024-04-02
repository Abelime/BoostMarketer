package camel.BoostMarketer.common.util;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExcelUtil {

    public void excelUpload(MultipartFile file, List<HashMap<String, Object>> list) throws Exception {
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
                if (idx == 0) {
                    keyList.add(value);
                } else {
                    data.put(keyList.get(columnIndex), value);
                }
            }
            if (idx != 0) {
                list.add(data);
            }
            idx++;
        }
        System.out.println("list: " + list);
    }


    public static String getStringValue(Cell cell, int columnIndex) {
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
}
