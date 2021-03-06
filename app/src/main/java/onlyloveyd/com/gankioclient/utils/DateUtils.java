package onlyloveyd.com.gankioclient.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private final static ThreadLocal<SimpleDateFormat> dateFormater =
            new ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                }
            };

    private static SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    /**
     * 格式化日期(精确到天)
     */
    public static String formatDateDetailDay(Date date) {
        return day.format(date);
    }

    /**
     * 将字符日期转换成Date
     */
    public static Date parseStringToDate(String date) {
        try {
            return dateFormater.get().parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
}
