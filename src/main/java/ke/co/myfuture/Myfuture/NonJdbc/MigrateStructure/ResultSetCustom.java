package ke.co.myfuture.Myfuture.NonJdbc.MigrateStructure;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ResultSetCustom extends ArrayList<HashMap<String, String>> {
    int currentIndex = -1;
    String output = "";
    HashMap<String, String> currentDataMap;
    private String temp;

    public boolean next() {
        currentIndex++;
        if (currentIndex < size()) {
            currentDataMap = get(currentIndex);
            return true;
        }
        return false;
    }

    public String getString(String key) {
        Set<String> keys = currentDataMap.keySet();
//        System.out.println(Arrays.deepToString(keys.toArray()));
//        System.out.println("key - "+key);
        if (!keys.contains(key)) {
            System.out.println("Does not contain key");
            return null;
        }
        return currentDataMap.get(key);
    }

    public String getString(String key, ColumnMap columnMap) {
        Set<String> keys = currentDataMap.keySet();
//        System.out.println(Arrays.deepToString(keys.toArray()));
//        System.out.println("key - "+key);
        if (!keys.contains(key)) {
            System.out.println("Does not contain key");
            return null;
        }
        if (columnMap.type != null) {
            if (columnMap.type.equalsIgnoreCase("date"))
                return reformatDateString(currentDataMap.get(key),  columnMap.informat);
            else if (columnMap.type.equalsIgnoreCase("double")) {
                temp = currentDataMap.get(key);
                if (temp.isEmpty())
                    return "0";
                return temp;
            }

        }
        return currentDataMap.get(key);
    }

    public String reformatDateString(String date, String format) {
        Date date1 = getDateFromString(date, format);
        return formatDate(date1);
    }

    private String formatDate(Date date) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.getDefault());
        return outputFormat.format(date);
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


    @Override
    public String toString() {
        output = "";
        currentDataMap.entrySet().forEach(entry -> {
//            System.out.println("currentDataMap");
            output += entry.getKey() + ": " + entry.getValue()+", ";
//            System.out.println(entry.getKey() + ": " + entry.getValue()+"\n");
        });
        return output;
    }

    public Double getDouble(String account_balance) {
        if (currentDataMap.get(account_balance).isEmpty()) return 0.0;
        return Double.parseDouble(currentDataMap.get(account_balance));
    }

    public String getString(String key, boolean b) {
        if (b)
            System.out.println("key - "+key);
        return currentDataMap.get(key);
    }
}