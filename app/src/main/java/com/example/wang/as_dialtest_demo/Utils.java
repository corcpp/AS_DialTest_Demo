package com.example.wang.as_dialtest_demo;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by wang on 2016/2/4.
 */
public class Utils {


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
}
