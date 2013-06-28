package com.alidi.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.File;


public class IOUtil {

    public static boolean getConnectionState(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return (networkInfo != null) && networkInfo.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void checkAndCreatePath(String pathName) {
        File path = new File(pathName);
        checkAndCreatePath(path);
    }

    public static void checkAndCreatePath(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static boolean tryToParse(String title) {
        boolean b = false;
        try {
            Long.parseLong(title);
            b = true;
        } catch (NumberFormatException e) {

        }
        return b;
    }

    public static boolean tryToParseInt(String title) {
        boolean b = false;
        try {
            Integer.parseInt(title);
            b = true;
        } catch (NumberFormatException e) {

        }
        return b;
    }

    public static boolean tryToParseDouble(String title) {
        boolean b = false;
        try {
            Double.parseDouble(title);
            b = true;
        } catch (NumberFormatException e) {

        }
        return b;
    }


    public static String bigFrist(String b) {
        if (b.length() > 0) {
            char a = b.charAt(0);
            String s = String.valueOf(a);
            s = s.toUpperCase();
            b = b.substring(1, b.length());
            String res = s.concat(b);
            return res;
        } else return b;
    }

    public static String getWiFiName(Context context) {
        try {
            // Setup WiFi
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            // Get WiFi status
            WifiInfo info = wifi.getConnectionInfo();
            return info.getSSID();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
