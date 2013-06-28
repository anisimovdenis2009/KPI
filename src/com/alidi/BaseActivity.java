package com.alidi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.alidi.util.GUIUtil;


import java.lang.Exception;import java.lang.Object;import java.lang.Runnable;import java.lang.String;import java.util.ArrayList;


/**
 * Created by IntelliJ IDEA.
 * User: anisimov.da
 * Date: 16.12.11
 * Time: 18:28
 * To change this template use File | Settings | File Templates.
 */
public class BaseActivity extends Activity {

    protected static final Object WAIT_SYNCHRONIZER = new Object();

    protected void checkAndResumeWaiting() {
        synchronized (WAIT_SYNCHRONIZER) {
            if (isWaitStillNeeded) {
                showWaitDialog();
            }
        }
    }

    protected Dialog waitDialog;
    public static boolean isWaitStillNeeded = false;

    protected void startWaiting() {
        runOnUiThread(
                new Runnable() {
                    public void run() {
                        synchronized (WAIT_SYNCHRONIZER) {
                            Log.v(Common.TAG, "WAIT_DIALOG starting  ");
                            isWaitStillNeeded = true;
                            showWaitDialog();
                            Log.v(Common.TAG, "WAIT_DIALOG started ");
                        }
                    }
                }
        );
    }

    protected void showWaitDialog() {
        if (waitDialog != null) {
            if (waitDialog.isShowing()) return;
            dissmissWaitDialog();
        }
        waitDialog = GUIUtil.getWaitDialog(this);
        waitDialog.show();
        Log.v(Common.TAG, "WAIT_DIALOG shown");
        if (!isWaitStillNeeded)
            dissmissWaitDialog();
    }

    protected void cancelWaiting() {
        runOnUiThread(
                new Runnable() {
                    public void run() {
                        synchronized (WAIT_SYNCHRONIZER) {
                            isWaitStillNeeded = false;
                            dissmissWaitDialog();

                        }
                    }
                }
        );
    }

    protected void hideWaitDialogOnPause() {
        synchronized (WAIT_SYNCHRONIZER) {
            dissmissWaitDialog();
        }
    }

    protected void dissmissWaitDialog() {
        if (waitDialog != null) {
            Dialog killer = waitDialog;
            waitDialog = null;
            if (killer.isShowing())
                killer.dismiss();

        }
    }

}
