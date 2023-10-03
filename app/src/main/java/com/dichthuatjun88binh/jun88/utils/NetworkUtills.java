package com.dichthuatjun88binh.jun88.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;

public class NetworkUtills {
    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return cm.getActiveNetwork() != null && cm.getNetworkCapabilities(cm.getActiveNetwork()) != null;
        }
        return false;
    }
}
