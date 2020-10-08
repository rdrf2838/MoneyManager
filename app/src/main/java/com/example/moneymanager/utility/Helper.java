package com.example.moneymanager.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Helper {
    public Helper() {};
    public static String unixToDMY(int datemodified) {
        Date date = new Date(datemodified*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setTimeZone(TimeZone.getDefault());
        String java_date = sdf.format(date);
        return java_date;
    }
    public static String unixToHMSDMY(int datemodified) {
        Date date = new Date(datemodified*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        sdf.setTimeZone(TimeZone.getDefault());
        String java_date = sdf.format(date);
        return java_date;
    }

    public static int getUnixTime() {
        Date date = Calendar.getInstance().getTime();
        return (int) (date.getTime()/1000);
    }
}
