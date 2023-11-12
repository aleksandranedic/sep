package com.example.authservice.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    public static Date convertToDate(String dateString){
        String dateFormat = "EEE MMM dd yyyy HH:mm:ss 'GMT'Z (zzz)";

        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        try {
            Date date = formatter.parse(dateString);
            System.out.println(date);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
