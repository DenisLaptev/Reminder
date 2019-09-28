package com.androidschool.denis.myreminder.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.androidschool.denis.myreminder.MainActivity;
import com.androidschool.denis.myreminder.MyApplication;
import com.androidschool.denis.myreminder.R;

public class AlarmReceiver extends BroadcastReceiver {

    //BroadcastReceiver - приёмник широковещательных сообщений.
    //получает внешние сообщения (от других приложений, служб)
    //и как-то их обрабатывает (реагирует).
    @Override
    public void onReceive(Context context, Intent intent) {

        //заголовок задачи
        String title = intent.getStringExtra("title");

        //время создания задачи
        long timeStamp = intent.getLongExtra("time_stamp", 0);

        //приоритет задачи (обозначается цветом)
        int color = intent.getIntExtra("color", 0);

        //Интент запускает MainActivity при нажатии на нотификацию
        Intent resultIntent = new Intent(context, MainActivity.class);

        //Если приложение уже запущено, то мы не запускаем MainActivity,
        //иначе запускаем MainActivity.
        if (MyApplication.isActivityVisible()) {
            resultIntent = intent;
        }

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //PendingIntent можно запустить в другом приложении
        //от имени того приложения и с теми полномочиями,
        // в котором он создавался.
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) timeStamp,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("MyReminder");
        builder.setContentText(title);
        builder.setColor(context.getResources().getColor(color));
        builder.setSmallIcon(R.drawable.ic_check_white_48dp);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) timeStamp, notification);


    }
}
