package ke.co.myfuture.Myfuture.NonJdbc.Migration.MigrateStructure;

import java.text.SimpleDateFormat;
import java.util.*;

public class Table {
    public String source_key;
    public String key = "";
    public String target_key;
    public String unique_key;
    public String source_type;
    public String file_date_format;
    public String sqlQuery;
    public Boolean useInQuery = false;
    public Boolean insertAllMapping = false;
    public String source_table;
    public String target_table;

    public String delimiter;

    public List<ColumnMap> column_mapping;
    public List<DefaultValue> defaults;
    public List<DefaultInt> defaults_ints;

    public List<String> custom;
    public List<String> maintable;
    public List<String> innertable1;
    public List<String> innertable2;
    public List<String> is_int;
    public List<Table> innertables;
    public Table parent;

    public String mode;

    public HashMap<String, String> map_defaults;

    @Override
    public String toString() {
        return "Table{" +
                "source_key='" + source_key + '\'' +
                ", key='" + key + '\'' +
                ", target_key='" + target_key + '\'' +
                ", source_table='" + source_table + '\'' +
                ", target_table='" + target_table + '\'' +
                ", column_mapping=" + column_mapping +
                ", defaults=" + defaults +
                ", defaults_ints=" + defaults_ints +
                ", custom=" + custom +
                ", maintable=" + maintable +
                ", innertable1=" + innertable1 +
                ", innertables=" + innertables +
                ", map_defaults=" + map_defaults +
                '}';
    }

    public String[] tableStructure() {
        HashSet<String> values = new HashSet<String>();
        for (ColumnMap columnMap: column_mapping) {
            values.add(columnMap.target_column);
        }

        String[] array =  new String[values.size()];

        int i = 0;
        for (String s: values) {
            array[i++] = s;
        }

        return array;
    }

    public void equalizeMappingColumnNames() {
        for(ColumnMap columnMap: column_mapping) {
            columnMap.source_column = columnMap.target_column;
        }
    }

    public ColumnMap getColumnMap(String column) {
        for(ColumnMap columnMap: column_mapping) {
            if (columnMap.target_column.equalsIgnoreCase(column)){
                return columnMap;
            }
        }
        return null;
    }

    public String getSourceFilePath(Date date){
        SimpleDateFormat format = new SimpleDateFormat(file_date_format);
        String reqDate = format.format(date).toUpperCase();
//        String finacleDate = reqDate;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return source_table.replace("|DATE|", reqDate);
    }

    public void updateColumnsToInsert() {
        if (insertAllMapping) {
            for (ColumnMap columnMap: column_mapping) {
                if (!contains(maintable, columnMap.target_column)){
                    maintable.add(columnMap.target_column);
                }
            }
        }
    }

    public boolean containsCustomKey(String key) {
        for (String custom: custom) {
            if (custom.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsColumnMappingKey( String key) {
        if (column_mapping == null) return false;
        for (ColumnMap columnMap: column_mapping) {
            if (columnMap.target_column.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }


    public boolean contains(List<String> array, String key) {
        if (array == null) return false;
        for (String string: array) {
            if (string.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsDefaultKey(String key) {
        if ( defaults == null)
            return false;
        for (DefaultValue defaultValue: defaults) {
            if (defaultValue.target_column.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsDefaultHashmap(String key) {

        if ( map_defaults == null) {
            System.out.println("map_defaults is null-----------");
            return false;
        }
//        System.out.println("Checking ---- '"+key+"'");
        Set<String> keys = map_defaults.keySet();
        for (String k: keys) {
//            System.out.println("Compare to "+k);
            if (k.equalsIgnoreCase(key))
                return true;
        }
        return false;
    }

    public boolean containsDefault_IntKey( String key) {
        if (defaults_ints == null)
            return false;
        for (DefaultInt defaultValue: defaults_ints) {
            if (defaultValue.target_column.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    public ColumnMap getMapping(String columnKey) {
        for (ColumnMap columnMap: column_mapping) {
            if (columnMap.target_column.equalsIgnoreCase(columnKey)) {
//                System.out.println("Found "+key+" as "+columnMap.source_column);
                return  columnMap;
            }
        }
        return null;
    }
}