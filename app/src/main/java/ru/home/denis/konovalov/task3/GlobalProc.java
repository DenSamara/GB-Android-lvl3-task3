package ru.home.denis.konovalov.task3;

import android.util.Log;

public class GlobalProc {
    private static String TAG = GlobalProc.class.getSimpleName();
    private static boolean showDebugInfo = true;

    public static void logE(String tag, String message){
        if (showDebugInfo) Log.e(tag, message);
    }
}
