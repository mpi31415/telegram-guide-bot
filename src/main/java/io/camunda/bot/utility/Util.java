package io.camunda.bot.utility;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class Util {
    public static boolean validateDate(String date, String format){
        DateFormat sdf = new SimpleDateFormat(format);
        System.out.println("format " + format + "date " + date);
        sdf.setLenient(false);
        try {
            String res = sdf.parse(date).toString();
            System.out.println(res);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
    public static Date stringToDate(String date, String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date result;
        try {
            result = new Date(simpleDateFormat.parse(date).getTime());
        }catch (ParseException e){
            result = new Date(0);
        }
        return result;

    }
}
