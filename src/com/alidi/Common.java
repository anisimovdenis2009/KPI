package com.alidi;

import android.os.Environment;

/**
 * Created with IntelliJ IDEA.
 * User: anisimov.da
 * Date: 16.05.13
 * Time: 17:05
 * To change this template use File | Settings | File Templates.
 */
public class Common {
    public static final String TAG = "KPI";
    public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String CASH_PATH = SDCARD_PATH + "/KPI";
    public static final String JSON_KPI = CASH_PATH + "/JSON_KPI.txt";
    public static final String KPI_DB = CASH_PATH + "/kpi.db";
}
