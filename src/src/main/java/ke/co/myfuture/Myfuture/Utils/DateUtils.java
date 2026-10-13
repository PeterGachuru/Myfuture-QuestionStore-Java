package ke.co.myfuture.Myfuture.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static Date convertLocalDateToDate(LocalDate dueDate) {
        return Date.from(dueDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    public static LocalDate convertDateToLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static Date getDateFromString(String dateString, String format) {
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

    public static String formatDate(Date date, String format) {
        SimpleDateFormat outputFormat = new SimpleDateFormat(format, Locale.getDefault());
        return outputFormat.format(date);
    }
}
