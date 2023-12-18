package com.kerwin.common;

import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class PtDateTime {
    public static final TimeZone CurrTimeZone = getCurrTimeZone();

    private static TimeZone getCurrTimeZone() {
        String timeZoneId = System.getProperty("user.timezone");
        if (StringUtils.isNotEmpty(timeZoneId)) {
            return TimeZone.getTimeZone(timeZoneId);
        }
        return null;
    }

    public static String getDateTimeString() {
        return getDateTimeString(null, (TimeZone) null, null);
    }

    public static String getDateTimeString(TimeZone timeZone) {
        return getDateTimeString(null, timeZone, null);
    }

    public static String getDateTimeString(String format) {
        return getDateTimeString(null, (TimeZone) null, format);
    }

    public static String getDateTimeString(Calendar datetime) {
        return getDateTimeString(datetime, (TimeZone) null, null);
    }

    public static String getDateTimeString(Calendar datetime, String format) {
        return getDateTimeString(datetime, (TimeZone) null, format);
    }

    public static String getDateTimeString(Calendar datetime, String timeZoneId, String format) {
        TimeZone timeZone = null;
        if (PtCommon.isNotEmpty(timeZoneId)) {
            timeZone = TimeZone.getTimeZone(timeZoneId);
        }

        return getDateTimeString(datetime, timeZone, format);
    }

    public static String getDateTimeString(Calendar datetime, TimeZone timeZone, String format) {
        Calendar datetimeNew = datetime;
        if (datetimeNew == null) {
            datetimeNew = Calendar.getInstance();
        }

        if (format == null) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat tempDateTimeFormat = new SimpleDateFormat(format);
        if (timeZone != null) {
            tempDateTimeFormat.setTimeZone(timeZone);
        }
        return tempDateTimeFormat.format(datetimeNew.getTime());
    }

    public static String getDateTimeIsoString() {
        return getDateTimeIsoString(null, (TimeZone) null);
    }

    public static String getDateTimeIsoString(Calendar datetime, String timeZoneId) {
        TimeZone timeZone = null;
        if (PtCommon.isNotEmpty(timeZoneId)) {
            timeZone = TimeZone.getTimeZone(timeZoneId);
        }

        return getDateTimeIsoString(datetime, timeZone);
    }

    public static String getDateTimeIsoString(Calendar datetime, TimeZone timeZone) {
        Calendar datetimeNew = datetime;
        if (datetimeNew == null) {
            datetimeNew = Calendar.getInstance();
        }
        SimpleDateFormat tempDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
        if (timeZone != null) {
            tempDateTimeFormat.setTimeZone(timeZone);
        }
        return tempDateTimeFormat.format(datetimeNew.getTime());
    }

    public static Calendar getDateTime() {
        return Calendar.getInstance();
    }

    public static Calendar getDateTimeByZoneId(String timeZoneId) {
        if (timeZoneId != null && !timeZoneId.isEmpty()) {
            return Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
        }
        return Calendar.getInstance();
    }

    public static Calendar getDateTime(TimeZone timeZone) {
        if (timeZone != null) {
            return Calendar.getInstance(timeZone);
        }
        return Calendar.getInstance();
    }

    public static Calendar getDateTime(String datetime) throws ParseException {
        return getDateTime(datetime, null, null);
    }

    public static Calendar getDateTime(String datetime, String format) throws ParseException {
        return getDateTime(datetime, null, format);
    }

    public static Calendar getDateTime(String datetime, String timeZoneId, String format) throws ParseException {
        Calendar result = Calendar.getInstance();
        if (datetime != null) {
            if (format == null) {
                format = "yyyy-MM-dd HH:mm:ss";
            }
            SimpleDateFormat df = new SimpleDateFormat(format);
            if (timeZoneId != null) {
                df.setTimeZone(TimeZone.getTimeZone(timeZoneId));
            }
            Date tempDate = df.parse(datetime);
            result.setTime(tempDate);
        }
        else if (timeZoneId != null) {
            result = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
        }
        return result;
    }

    public static Calendar getDateTime(Long date) {
        Calendar ret = Calendar.getInstance();
        ret.setTime(new Date(date));
        return ret;
    }

    public static Calendar getDateTime(Date date) {
        Calendar ret = Calendar.getInstance();
        ret.setTime(date);
        return ret;
    }

    //date 0表示月份第一天 -1表示月份最后一天 >=31也为最后一天 其他数字为日期值
    public static Calendar getDateTime(int year, int month, int date, int hrs, int min, int sec) throws Exception {
        return getDateTime(year, month, date, hrs, min, sec, 0);
    }

    public static Calendar getDateTime(int year, int month, int date, int hrs, int min, int sec, int millsec) throws Exception {
        Calendar ret = Calendar.getInstance();
        if (month < 1 || month > 12) {
            throw new Exception("month is not right.");
        }
        if (date < 1 || date >= 31) {
            ret.set(year, month - 1, 1, hrs, min, sec);
            ret.set(Calendar.DAY_OF_MONTH, ret.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        else if (date == 0) {
            ret.set(year, month - 1, 1, hrs, min, sec);
        }
        else {
            ret.set(year, month - 1, date, hrs, min, sec);
        }
        ret.set(Calendar.MILLISECOND, millsec);

        return ret;
    }

    public static Calendar getDateTime(Timestamp timestamp) {
        Calendar ret = Calendar.getInstance();
        ret.setTime(timestamp);
        return ret;
    }
}
