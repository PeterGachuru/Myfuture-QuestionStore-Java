package ke.co.myfuture.Myfuture.Utils.Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StaticFunctionUtils {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public static String simpleDateFormat(Date date) {
        if (date == null)
            return null;
        return simpleDateFormat.format(date);
    }
}
