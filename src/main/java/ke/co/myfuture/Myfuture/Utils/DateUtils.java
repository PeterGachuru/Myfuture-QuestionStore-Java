package ke.co.myfuture.Myfuture.Utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {
    public static Date convertLocalDateToDate(LocalDate dueDate) {
        return Date.from(dueDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
