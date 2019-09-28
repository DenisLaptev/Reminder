package com.androidschool.denis.myreminder;

import java.text.SimpleDateFormat;

public class Utils {

    public static String getDate(long date) {
        //Определяем формат выводимой даты
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        return dateFormat.format(date);
    }

    public static String getTime(long time) {
        //Определяем формат выводимого времени
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.format(time);
    }

    public static String getFullDate(long date) {
        //Определяем формат выводимой даты
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd.MM.yy HH.mm");
        return fullDateFormat.format(date);
    }
}
