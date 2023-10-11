package ke.co.myfuture.Myfuture.NonJdbc;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;


public class ExcelGenerator {
    int rowCount = 1;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private String name;
    CellStyle commonStyle;

    public ExcelGenerator() {
        workbook = new XSSFWorkbook();
        commonStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        commonStyle.setFont(font);
    }

    public void exportQueryResults(Connection targetConnection, String query, HashMap<String, String> headerMappings)
    {
        System.out.println(query);
//        appendToExecutionFile(query);
//        List<String[]> data = new ArrayList<String[]>();
        try {
            Statement targetStatement = targetConnection.createStatement();
            ResultSet resultSet = targetStatement.executeQuery(query);
            ResultSetMetaData rsmd = resultSet.getMetaData();

            int columnsNumber = rsmd.getColumnCount();
            System.out.println("Columns: "+columnsNumber);
            boolean first = true;
            while (resultSet.next()) {
                String[] values = new String[columnsNumber];
                if (first) {
                    first = false;
                    for (int i = 1; i <= columnsNumber; i++) {
                        values[i-1] = rsmd.getColumnName(i);
                    }
                    writeHeaders(values, headerMappings);
                }
                values = new String[columnsNumber];
                for (int i = 1; i <= columnsNumber; i++){
                    values[i-1] = formatString(resultSet.getString(i));
                }
                writeValues(values);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    String formatString(String value) {
        if (value == null)
            return "";
        if (value.contains("-") && value.contains(":") ) {
            int index = value.indexOf("-");
            if (value.indexOf("-", index) > 0) {
                index = value.indexOf(":");
                if (value.indexOf(":", index) > 0) {
                    String date = value.substring(0, 10);
//                    System.out.println(date);
                    return date;
                }
            }
        }
        return value;
    }

    private void writeHeaders(String[] values, HashMap<String, String> headerMappings) {
        System.out.println(Arrays.deepToString(values));
        sheet = workbook.createSheet(name);
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        int columnCount = 0;
        for (String string: values) {
//            if (headerMappings.containsKey(string)) {
//                System.out.println("contains key " + string);
//                System.out.println(headerMappings.get(string));
//            }

            createCell(row, columnCount++, headerMappings.get(string), style);
        }
    }
    private void createCell(Row row, int columnCount, Object valueOfCell, CellStyle style) {
//        sheet.autoSizeColumn(columnCount);
//        Cell cell = row.createCell(columnCount);
        if (style != null)
//            System.out.println("valueOfCell: "+valueOfCell);
        if (valueOfCell instanceof Integer) {
            row.createCell(columnCount).setCellValue((Integer) valueOfCell);
        } else if (valueOfCell instanceof Long) {
            row.createCell(columnCount).setCellValue((Long) valueOfCell);
        } else if (valueOfCell instanceof String) {
            row.createCell(columnCount).setCellValue((String) valueOfCell);
        }  else if (valueOfCell instanceof Boolean)  {
            row.createCell(columnCount).setCellValue((Boolean) valueOfCell);
        }else if (valueOfCell == null){
            System.out.println("null cell");
            row.createCell(columnCount).setCellValue("");
        }else {
            System.out.println("undetected cell");
            row.createCell(columnCount).setCellValue((String) valueOfCell);
        }
    }
    private void createCell(Row row, int columnCount, String valueOfCell, CellStyle style) {
           row.createCell(columnCount).setCellValue(valueOfCell);

        if (style != null) {
            Cell cell = row.createCell(columnCount);
            cell.setCellStyle(style);
            cell.setCellValue(valueOfCell);
        }
        else
            row.createCell(columnCount).setCellValue(valueOfCell);
    }
    private void writeValues(String[] values) {
//        System.out.println("Row "+rowCount);
        Row row = sheet.createRow(rowCount++);
        int columnCount = 0;
        for (String string: values) {
            createCell(row, columnCount++, string, null);
        }
    }

    public void generateExcelFile(Connection targetConnection, String query, String name, HttpServletResponse response, HashMap<String, String> headerMappings) {
        this.name = name;
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\""+name+".xlsx" + "\"");
            exportQueryResults(targetConnection, query, headerMappings);
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



