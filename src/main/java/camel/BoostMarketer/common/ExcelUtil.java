package camel.BoostMarketer.common;

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

    public void excelUpload(MultipartFile file) throws Exception {

        List<HashMap<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> data;

        Workbook workbook;
        Row row;
        Cell cell;

        OPCPackage opcPackage = OPCPackage.open(file.getInputStream());
        workbook = new XSSFWorkbook(opcPackage);


        Sheet sheet = workbook.getSheetAt(0);
        List<String> keyList = new ArrayList<>();
        // 행 정보 얻기
        int idx = 0;
        for (Row item : sheet) {
            data = new HashMap<>();
            row = item;

            // 셀의 수
            int cells = row.getPhysicalNumberOfCells();
            for (int columnIndex = 0; columnIndex < cells; columnIndex++) {

                cell = row.getCell(columnIndex);
                if (cell == null) {
                    // 셀이 존재하지 않는 경우
                    continue;
                } else {
                    String value = getStringValue(cell,columnIndex);
                    if (idx == 0) {
                        keyList.add(value);
                    } else {
                        data.put(keyList.get(columnIndex), value);
                    }
                }
            }

            if (idx != 0) {
                // 한 행의 값을 list에 추가
                list.add(data);
            }
            idx++;
        }
        System.out.println("list"+list);

    }

    public static String getStringValue(Cell cell, int columnIndex) {
        String rtnValue;
        try {
            rtnValue = cell.getStringCellValue();
        } catch (IllegalStateException e) {
            if(columnIndex==0){
                double value = cell.getNumericCellValue();
                rtnValue = String.valueOf((int)value);
            }else{
                rtnValue = Double.toString(cell.getNumericCellValue());
            }
        } return rtnValue;
    }
}
