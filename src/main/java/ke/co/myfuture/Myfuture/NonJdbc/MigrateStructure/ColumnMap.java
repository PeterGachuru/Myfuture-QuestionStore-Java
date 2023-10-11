package ke.co.myfuture.Myfuture.NonJdbc.MigrateStructure;

public class ColumnMap {
    public String source_column;
    public String informat;
    public String type;
    public String target_column;

    @Override
    public String toString() {
        return "ColumnMap{" +
                "source_column='" + source_column + '\'' +
                ", target_column='" + target_column + '\'' +
                '}';
    }
}