package ke.co.myfuture.Myfuture.NonJdbc.Migration;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import ke.co.myfuture.Myfuture.NonJdbc.Migration.MigrateStructure.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

@Service
@Slf4j
public class MigratorService {
    @Value("${datasource.oldmyfuture.url}")
    private String db;
    @Value("${datasource.oldmyfuture.username}")
    private String username;
    @Value("${datasource.oldmyfuture.password}")
    private String password;

    public Connection targetConnection = null;

//    MigratorService(String db, String username, String password) {
//        this.db = db;
//        this.username = username;
//        this.password = password;
//        initDatabase();
//    }
    public ResultSet query(Connection targetConnection, String sql) {
        try {
            Statement statement =  targetConnection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet queryStringArray(Connection targetConnection, String sql) {
        try {
            Statement statement =  targetConnection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<String> queryStringArray(String sql) {
        initDatabase();
        ResultSet resultSet = query(targetConnection, sql);
        List<String> strings = new ArrayList<>();
        try {
            while (resultSet.next()) {
                strings.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return  strings;
    }

    public ResultSet query(String sql) {
        try {
            Statement statement =  targetConnection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void execute(Connection targetConnection, String sql) {
        if (targetConnection == null)
            initDatabase();
        try {
            Statement statement =  targetConnection.createStatement();
            statement.executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void execute(String sql) {
        try {
            Statement statement =  targetConnection.createStatement();
            statement.executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void execute(String sql, boolean showSql) {
        execute(targetConnection, sql, showSql);
    }


    public ResultSetCustom toResultsSet(List<String[]> allData) {
        ResultSetCustom resultSetCustom = new ResultSetCustom();

        boolean foundHeader = false;
        String[] keys = new String[0];
        HashMap<String, String> dataMap;
        int i;
        for (String[] tempSplits: allData) {
//            System.out.println("----------------------");
//            System.out.println(foundHeader);
            i = 0;
            for (String split: tempSplits) {
                tempSplits[i] = split.trim();
                i++;
            }
            if (!foundHeader) {
                keys =tempSplits;
                System.out.println("Headers "+Arrays.toString(keys));
                foundHeader = true;
            }else{
                i = 0;
                dataMap = new HashMap<>();

//                System.out.println(Arrays.toString(keys));
//                System.out.println(Arrays.toString(tempSplits));
                if(empty(tempSplits)) {
                    continue;
                }
                for (String key: keys) {
                    if (tempSplits.length > i) {
//                        System.out.println("{"+key+"}-{"+tempSplits[i]+"}");
                        dataMap.put(key, tempSplits[i]);
                        i++;
                    }
                }
                resultSetCustom.add(dataMap);
            }
        }
        System.out.println("Finished creating resultSetCustom");
        return resultSetCustom;
    }

    public ResultSetCustom getData(Table table) {
        List<String[]> allData = readCSV(table);
        return toResultsSet(allData);
    }
    public ResultSetCustom getData(Table table,  File sourceFile) {
        List<String[]> allData;
        if (table.source_type.equalsIgnoreCase("excel")){
            allData = readExcel(sourceFile);
        }else {
            allData = readDataFromTextCSV(sourceFile, table.delimiter);
        }
        System.out.println("Read data from file "+table.source_table);
        return toResultsSet(allData);
    }

    private boolean empty(String[] tempSplits) {
        for (String s: tempSplits){
            if (!s.trim().isEmpty())
                return false;
        }
        return true;
    }

    public void execute(Connection targetConnection, String sql, boolean showSql) {
        if (showSql) {
            System.out.println(sql);
        }
        execute(targetConnection, sql);
    }

    public void executeFromFile(Connection targetConnection, String filePath) {
        execute(targetConnection, readFileToString(filePath), true);
    }




    public static void printQueryResultsWithoutNext(ResultSet resultSet)
    {
        try {
            ResultSetMetaData rsmd = resultSet.getMetaData();

            int columnsNumber = rsmd.getColumnCount();
            System.out.println("Columns: "+columnsNumber);
            for (int i = 1; i <= columnsNumber; i++) {
                System.out.print(String.format(", %"+Math.max(rsmd.getColumnName(i).length(), resultSet.getString(i).length())+"s", rsmd.getColumnName(i)));
            }
            System.out.println();

            for (int i = 1; i <= columnsNumber; i++) {
                System.out.print(String.format(", %"+Math.max(rsmd.getColumnName(i).length(), resultSet.getString(i).length())+"s", resultSet.getString(i)));
            }

            System.out.println();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void appendToExecutionFile(String text){
        appendToExecutionFile(text, "execution.sql");
    }

    public void appendToExecutionFile(String text, String fileName) {
        FileWriter writer = null;
        try {
            String content = readFileToString(fileName, Charset.defaultCharset());
            writer = new FileWriter(fileName, false);
            writer.write(content+"\n"+text);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Date getDateFromString(String dateString, String format) {
//        System.out.println("Date: "+dateString);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        try {
            if (dateString != null && !dateString.trim().isEmpty())
                return dateFormat.parse(dateString);
            else
                return new Date();
        } catch (ParseException e) {
            return new Date();
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.getDefault());
        return outputFormat.format(date);
    }

    public String reformatDateString(String date, String format) {
        Date date1 = getDateFromString(date, format);
        return formatDate(date1);
    }



    public void writeToFile(String text, String fileName) {
        FileWriter writer = null;
        try {
//            String content = readFileToString(fileName, Charset.defaultCharset());
            writer = new FileWriter(fileName, false);
            writer.write(text);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void rewritetheTable(Table table) {
        Gson gson = new Gson();
        table.equalizeMappingColumnNames();
        writeToFile(gson.toJson(table), "conversions/"+table.key+".json");
    }

    public boolean containsCustomKey(Table table, String key) {
        for (String custom: table.custom) {
            if (custom.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsColumnMappingKey(Table table, String key) {
        if (table.column_mapping == null) return false;
        for (ColumnMap columnMap: table.column_mapping) {
            if (columnMap.target_column.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsDefaultKey(Table table, String key) {
        if ( table.defaults == null)
            return false;
        for (DefaultValue defaultValue: table.defaults) {
            if (defaultValue.target_column.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsDefaultHashmap(Table table, String key) {

        if ( table.map_defaults == null) {
//            System.out.println("map_defaults is null-----------");
            return false;
        }
//        System.out.println("Checking ---- '"+key+"'");
        Set<String> keys = table.map_defaults.keySet();
        for (String k: keys) {
//            System.out.println("Compare to "+k);
            if (k.equalsIgnoreCase(key))
                return true;
        }
        return false;
    }

    public boolean containsDefault_IntKey(Table table, String key) {
        if (table.defaults_ints == null)
            return false;
        for (DefaultInt defaultValue: table.defaults_ints) {
            if (defaultValue.target_column.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    public String defaultValue(Table table, String key) {
        for (DefaultValue defaultValue: table.defaults) {
            if (defaultValue.target_column.equalsIgnoreCase(key)) {
                return defaultValue.value;
            }
        }
        return "";
    }
    public String defaultHashmap(Table table, String key) {
        return table.map_defaults.get(key);
    }

    public Long default_IntValue(Table table, String key) {
        for (DefaultInt defaultValue: table.defaults_ints) {
            if (defaultValue.target_column.equalsIgnoreCase(key)) {
                return defaultValue.value;
            }
        }
        return 0L;
    }

    public String mssqlEquivalent(Table table, String key) {
        for (ColumnMap columnMap: table.column_mapping) {
            if (columnMap.target_column.equalsIgnoreCase(key)) {
//                System.out.println("Found "+key+" as "+columnMap.source_column);
                return ""+columnMap.source_column+"";
            }
        }
        return "";
    }
    public String appendColumnString(String start, String add) {
        if (start.isEmpty()) {
            return add;
        }else {
            return start+", "+add;
        }
    }

    public void exportQueryResults( Connection targetConnection, String query, String filePath)
    {
        System.out.println(query);
//        appendToExecutionFile(query);
        List<String[]> data = new ArrayList<String[]>();
        try {

            Statement targetStatement = targetConnection.createStatement();
            ResultSet resultSet = targetStatement.executeQuery(query);
            ResultSetMetaData rsmd = resultSet.getMetaData();

            int columnsNumber = rsmd.getColumnCount();
            System.out.println("Columns: "+columnsNumber);
            boolean first = true;
            while (resultSet.next()) {
                String[] values = new String[columnsNumber];
                if (first){
                    first = false;
                    for (int i = 1; i <= columnsNumber; i++) {
                        values[i-1] = rsmd.getColumnName(i);
                    }
                    data.add(values);

                }
                values = new String[columnsNumber];
                for (int i = 1; i <= columnsNumber; i++){
                    values[i-1] = resultSet.getString(i);
                }
                data.add(values);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // first create file object for file placed at location
        // specified by filepath
        File file = new File(filePath);

        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter with '|' as separator
            CSVWriter writer = new CSVWriter(outputfile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            writer.writeAll(data);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void moveEasy(Connection sourceConnection, Connection targetConnection, Table table) {
        try {
            moveEasy(sourceConnection.createStatement(), targetConnection.createStatement(), table);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void moveEasy(Table table, File sourceFile) {
//        System.out.println(table);
        System.out.println("========================================================");
        if (targetConnection == null) {
            initDatabase();
        }
        try {
            moveEasy(targetConnection.createStatement(), table, sourceFile);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void initDatabase() {
        if (targetConnection != null)
            return;
        try {
            targetConnection = DriverManager.getConnection(this.db, this.username, this.password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void moveEasy(Statement targetStatement, Table table, File sourceFile) {
        try {
//            String readerSQL = getReaderSQLForMS(table);

//            System.out.println("readerSQL: "+readerSQL);

            ResultSetCustom resultSet = getData(table, sourceFile);
            System.out.println("After reading data");
            while (resultSet.next()) {
//                System.out.println("Loop");
                String writerSQL = getOneWriterSQL(table, resultSet, 5000);

//                System.out.println("writerSQL: "+writerSQL);
//
                targetStatement.executeQuery(writerSQL);
            }
            System.out.println("Finished writing to database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void moveEasy(Statement sourceStatement, Statement targetStatement, Table table) {
        try {
            String readerSQL = getReaderSQLForMS(table);

            System.out.println("readerSQL: "+readerSQL);

            ResultSet resultSet = sourceStatement.executeQuery(readerSQL);
            while (resultSet.next()) {

                String writerSQL = getWriterSQL(table, resultSet);

                System.out.println("writerSQL: "+writerSQL);
//
                targetStatement.executeQuery(writerSQL);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String[]> readCSV(Table table) {
        try {
            FileReader filereader = new FileReader(table.source_table);

            // create csvReader object and skip first Line
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .build();
            return csvReader.readAll();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }

    public  List<String[]> readDataFromTextCSV(String sourceFile, String delimiter) {
        return readDataFromTextCSV(new File(sourceFile), delimiter);
    }
    public  List<String[]> readDataFromTextCSV(File sourceFile, String delimiter) {
        try {
            Scanner scanner = new Scanner(sourceFile);
            List<String[]> data = new ArrayList<>();
            String string;
            String[] splits;
//            scanner.
            int i = 0;
            while (scanner.hasNextLine()) {
                string = scanner.nextLine();
                splits = string.split(delimiter);
//                System.out.println("delimiter: "+delimiter);
                data.add(splits);
//                i++;
//                if (i == 10)
//                    break;
//                System.out.println(string);
//                System.out.println(Arrays.deepToString(splits));
            }

            return data;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String[]> readCSV(String file) {
        try {
            FileReader filereader = new FileReader(file);

            // create csvReader object and skip first Line
            try (CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .build()) {
                return csvReader.readAll();
            }
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeFile(String filePath,  List<String[]> data) {
        File file = new File(filePath);

        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter with '|' as separator
            CSVWriter writer = new CSVWriter(outputfile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            writer.writeAll(data);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getReaderSQLForMS(Table table) {
        String columnString = "";
        for (ColumnMap columnMap: table.column_mapping) {
            columnString = appendColumnString(columnString, "["+columnMap.source_column+"]");
        }

        return "SELECT "+columnString+" FROM ["+table.source_table+"]";
    }
    public String readFileToString(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public Table readFileToTable(String fileString) {
        try {
            String content = readFileToString(fileString, Charset.defaultCharset());
//            System.out.println(content);
            Table table = (new Gson()).fromJson(content, Table.class);
            table.updateColumnsToInsert();
            return table;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readFileToString(String path)
    {
        try {
            return readFileToString(path, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getOneWriterSQL(Table table, ResultSetCustom resultSet, int max) throws SQLException {
        String columnString = "";

        for (String column: table.maintable) {
            columnString = appendColumnString(columnString, column);
        }

        String oneRow = "";
        String dataString = "";
        int count = 0;
        while(resultSet.next()) {
            oneRow = getRow(table, resultSet);
            if(oneRow.isEmpty())
                continue;
            if (dataString.isEmpty()) {
                dataString = "("+oneRow+")";
            }else{
                dataString = dataString +", ("+oneRow+")";
            }
            if (++count == max) {
                System.out.println("to inserte: "+count);
                break;
            }
        }
        return "INSERT INTO "+table.target_table+" ("+columnString+") VALUES"+dataString+";";
    }

    private String getRow(Table table, ResultSetCustom resultSet) {

        String oneRow = "";
        try {
            for (String column: table.maintable) {
//                System.out.println(column);
                if (hasFieldValue(table, column)) {
                    oneRow = appendColumnString(oneRow, ""+getFieldValue(table, column, resultSet)+"");
                } else {
                    System.out.println(column+" value not found");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return oneRow;
    }

    public String getWriterSQL(Table table, ResultSet resultSet) throws SQLException {
        String columnString = "";

        for (String column: table.maintable) {
            columnString = appendColumnString(columnString, column);
        }

        String oneRow = "";

        try {
            for (String column: table.maintable) {
//                System.out.println(column);
                if (hasFieldValue(table, column)) {
                    oneRow = appendColumnString(oneRow, ""+getFieldValue(table, column, resultSet)+"");
                } else {
                    System.out.println(column+" value not found");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "INSERT INTO "+table.target_table+" ("+columnString+") VALUES("+oneRow+");";
    }
    public String getWriterSQL(Table table, ResultSetCustom resultSet) throws SQLException {
        String columnString = "";

        for (String column: table.maintable) {
            columnString = appendColumnString(columnString, column);
        }

//        System.out.println(columnString);
        String oneRow = "";

        try {
            for (String column: table.maintable) {
//                System.out.println(column+"-"+getFieldValue(table, column, resultSet));
                if (hasFieldValue(table, column)) {
                    oneRow = appendColumnString(oneRow, ""+getFieldValue(table, column, resultSet)+"");
                } else {
                    System.out.println(column+" value not found");
                }
            }
//            System.out.println(oneRow);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "INSERT INTO "+table.target_table+" ("+columnString+") VALUES("+oneRow+");";
    }

    public boolean hasFieldValue(Table table, String key) {
        if (table == null) return false;
        if (containsDefault_IntKey(table, key)) {
            return true;
        } else if (containsDefaultHashmap(table, key)) {
            return true;
        } else if (containsDefaultKey(table, key)) {
            return true;
        } else if (containsColumnMappingKey(table, key)) {
            return true;
        }
        return hasFieldValue(table.parent, key);
    }

    public String getFieldValue(Table table, String key, ResultSet resultSet) throws SQLException {
        key = key.trim();
        if (table == null) return "";
        if (containsDefault_IntKey(table, key)) {
            return default_IntValue(table, key)+"";
        }else if (containsDefaultHashmap(table, key)) {
            return getStringValue(table, key, defaultHashmap(table, key));
        } else if (containsDefaultKey(table, key)) {
            return getStringValue(table, key ,defaultValue(table, key));
        }else if (containsColumnMappingKey(table, key)) {
            return getStringValue(table, key ,resultSet.getString(mssqlEquivalent(table, key)));
        }else {
            return getFieldValue(table.parent, key, resultSet);
        }
    }
    public String getFieldValue(Table table, String key, ResultSetCustom resultSet) throws SQLException {
        key = key.trim();
        if (table == null) return "";
        if (containsDefault_IntKey(table, key)) {
            return default_IntValue(table, key)+"";
        }else if (containsDefaultHashmap(table, key)) {
            return getStringValue(table, key, defaultHashmap(table, key));
        } else if (containsDefaultKey(table, key)) {
            return getStringValue(table, key ,defaultValue(table, key));
        }else if (containsColumnMappingKey(table, key)) {
//            System.out.println("Contained in column mapping");
            return getStringValue(table, key ,resultSet.getString(mssqlEquivalent(table, key), table.getColumnMap(key)));
        }else {
            return getFieldValue(table.parent, key, resultSet);
        }
    }

    public String getStringValue(Table table, String key, String value) {
//        System.out.println("Key - "+key);
        if (is_int(table, key)) {
            return value;
        }
        return  "\""+value+"\"";
    }

    private boolean is_int(Table table, String key) {
        if (table.is_int == null)
                return false;
        for (String s: table.is_int)
            if (s.equalsIgnoreCase(key))
                return true;
        return false;
    }
    public String readDefaultFromFile(JsonObject jsonObject, String key) {
        return jsonObject.get(key).getAsString();
    }

    public Table getInnerTable(Table table, String name) {
        for (Table innerTable: table.innertables) {
//            System.out.println("Compare "+innerTable);
            if (innerTable.target_table.equalsIgnoreCase(name)) {
                innerTable.parent = table;
                return innerTable;
            }
        }
        return null;
    }

    public ArrayList<String[]> readExcel(File file) {
        return readExcel(file, 0);
    }

    public ArrayList<String[]> readExcel(File file, int sheet_no) {
        int i=0;
        ArrayList<String[]> column_content_array =new ArrayList<>();
        try{
            InputStream fileIn = new FileInputStream(file);
            POIFSFileSystem fs = new POIFSFileSystem(fileIn);
            HSSFWorkbook filename = new HSSFWorkbook(fs);
            HSSFSheet sheet = filename.getSheetAt(sheet_no);                                                // in the row 0 (which is first row of a work sheet)                                                    // search for column index containing string "Inst_Code"
            Integer columnNo = null;

            for (Row row : sheet) {
                ArrayList<String> splits = new ArrayList<>();
                for (Cell c: row) {
                    String cell_value= c.getStringCellValue();
                    if (cell_value == null)
                        cell_value = "";
                    cell_value = cell_value == null? "": cell_value.trim();
                    splits.add(cell_value);
                }

                column_content_array.add(Arrays.copyOf(splits.toArray(), splits.size(), String[].class));
            }
            return column_content_array;
            }
        catch(Exception ex){
            ex.printStackTrace();
            return column_content_array;
        }
    }

    public void exportQueryResultsToExcel(String query, String fileName, HttpServletResponse response,
                                          HashMap<String, String> headerMappings) {
        initDatabase();
        ExcelGenerator excelGenerator = new ExcelGenerator();
        excelGenerator.generateExcelFile(targetConnection, query, fileName, response, headerMappings);
    }
}

