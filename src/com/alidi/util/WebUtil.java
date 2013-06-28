package com.alidi.util;

import android.util.Log;
import com.alidi.Common;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by IntelliJ IDEA.
 *
 * @author DA
 *         Date: 16.05.11
 *         Time: 15:49
 *         To change this template use File | settings | File Templates.
 */
public class WebUtil {


    private static int getCoverInput(String imUrl, InputStream input) {
        int l = 0;
        try {
            if (imUrl != null) {
                URLConnection covercon = new URL(imUrl).openConnection();
                input = covercon.getInputStream();
                l = covercon.getContentLength();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return l;
    }

    public static boolean downloadImageToFile(String imUrl, String fileName) {
        boolean success = true;
        InputStream input = null;
        BufferedOutputStream output = null;
        int total = 0;
        try {
            byte[] buffer = new byte[2048];
            int size;
            try {
                if (imUrl != null) {
                    URLConnection covercon = new URL(imUrl).openConnection();
                    input = covercon.getInputStream();
                    total = covercon.getContentLength();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                success = false;
            }
            if (input != null) {
                output = new BufferedOutputStream(new FileOutputStream(fileName));
                while ((size = input.read(buffer)) > 0) {
                    output.write(buffer, 0, size);
                }
                output.flush();
            } else
                success = false;

        } catch (IOException ex) {
            ex.printStackTrace();
            success = false;
        } finally {
            try {
                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (total > new File(fileName).length())
            success = false;
        return success;
    }

    public static boolean downloadImageToFile(String imUrl, File fileName) {
        boolean success = true;
        int total = 0;
        InputStream input = null;
        BufferedOutputStream output = null;
        try {
            byte[] buffer = new byte[2048];

            int size;
            total = getCoverInput(imUrl, input);
            if (input != null) {
                output = new BufferedOutputStream(new FileOutputStream(fileName));
                while ((size = input.read(buffer)) > 0) {
                    output.write(buffer, 0, size);
                }
                output.flush();
            } else
                success = false;
        } catch (IOException ex) {
            ex.printStackTrace();
            success = false;
        } finally {
            try {
                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        long length = fileName.length();
        if (total > length)
            success = false;
        return success;
    }

    public static File downloadImageToFile2(String imUrl, File success) {
        //boolean success = true;
        InputStream input = null;
        BufferedOutputStream output = null;
        if (success.exists())
            try {
                byte[] buffer = new byte[2048];
                int size;
                getCoverInput(imUrl,input);
                if (input != null) {
                    output = new BufferedOutputStream(new FileOutputStream(success));
                    while ((size = input.read(buffer)) > 0) {
                        output.write(buffer, 0, size);
                    }
                    output.flush();
                } else
                    Log.v(Common.TAG, "file didn,t downloaded");

            } catch (IOException ex) {
                ex.printStackTrace();

                //success = false;
            } finally {
                try {
                    if (input != null)
                        input.close();
                    if (output != null)
                        output.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        return success;
    }
}
