package pers.perry.xu.crawler.framework.webcrawler.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>();

    public static String formatDate(Date date, String formatStringPattern) {
        return getFormat(formatStringPattern).format(date);
    }

    private static DateFormat getFormat(String formatStringPattern) {
        DateFormat dateFormat = threadLocal.get();
        if(dateFormat==null){
            dateFormat = new SimpleDateFormat(formatStringPattern);
            threadLocal.set(dateFormat);
        }
        return dateFormat;
    }
}
