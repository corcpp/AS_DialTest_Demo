package com.example.wang.as_dialtest_demo;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Random;

/**
 * Created by wang on 2016/2/4.
 */
public class Utils {


    /**
     * 判断应用是否是在前台
     * @param context
     * @return
     */
    public static boolean isBackground(Context context) {

        Log.d("Background.packageName", context.getPackageName());

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {

            if (appProcess.processName.equals(context.getPackageName())) {

                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {

                    Log.i("后台", appProcess.processName);
                    return true;

                }else{
                    Log.i("前台", appProcess.processName);
                    return false;

                }

            }

        }

        return false;

    }


    /**
     * getRunningTasks方法已被弃用，还要另外加权限
     * @param context
     * @return
     */
    public static boolean isApplicationBackground(final Context context) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if(!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if(!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /***
     * 存储文件到 sd卡公共目录下
     * @param context
     * @param fileName
     * @param content
     */
    public  static void saveToFile(Context context, String fileName, String content) {

        FileOutputStream outputStream = null;
        OutputStreamWriter writer = null;

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);
        Log.d("new log.txt file path", file.getPath().toString());
        Log.d(">>>>>>>>>", "begin to save to log.txt...");
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(content.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(outputStream != null)
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 删除日志文件
     * @param context
     * @param fileName
     */
    public  static void deleteFile(Context context, String fileName) {

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);
        if(file.exists()) {
            Log.d("delete log.txt path", file.getPath().toString());
            file.delete();
        }
    }

    public static void notifyAuthnFailed(Context context, int resultCode, String resultString) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setContentTitle("协商失败")
                .setContentText("错误码：" + resultCode + "  " + resultString)
                .setAutoCancel(true)//貌似并没有什么用
                        //加下面这句点击取消通知
//                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT))
                .setTicker("中间件通知")
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
//        notification.flags = Notification.FLAG_INSISTENT;

        notificationManager.notify(new Random().nextInt(1000), notification);
    }

}
