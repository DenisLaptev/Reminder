package com.androidschool.denis.myreminder;

import android.app.Application;

public class MyApplication extends Application {

    //Если активити уже запущена (видна),
    //то по нажатию на нотификацию уже не запускаем опять.

    private static boolean activityVisible;

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed(){
        activityVisible = true;
    }

    public static void activityPaused(){
        activityVisible = false;
    }
}
