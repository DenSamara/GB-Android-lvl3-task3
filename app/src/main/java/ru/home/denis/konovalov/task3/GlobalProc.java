package ru.home.denis.konovalov.task3;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class GlobalProc {
    private static String TAG = GlobalProc.class.getSimpleName();
    private static boolean showDebugInfo = true;

    public static void logE(String tag, String message){
        if (showDebugInfo) Log.e(tag, message);
    }

    public static void toast(Context ctx, String txt){
        Toast.makeText(ctx, txt, Toast.LENGTH_LONG).show();
    }
}
