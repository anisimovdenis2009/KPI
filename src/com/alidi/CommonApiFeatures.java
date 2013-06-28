package com.alidi;



import com.alidi.util.IOUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author DA
 *         Date: 31.05.11
 *         Time: 13:20
 *         To change this template use File | Settings | File Templates.
 */
public class CommonApiFeatures {

    public static boolean readJSONtoFile(String relativeUrl, File file) {
        InputStream input = getResponseStream(relativeUrl);
        boolean success = true;
        IOUtil.checkAndCreatePath(Common.CASH_PATH);
        BufferedOutputStream output = null;
        try {
            byte[] buffer = new byte[2048];
            int size;
            if (input != null) {
                output = new BufferedOutputStream(new FileOutputStream(file));
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
        return success;
    }

    public static InputStream getResponseStream(String url) {
        DefaultHttpClient http = new DefaultHttpClient();

        HttpGet request = new HttpGet(url);

/*        request.removeHeaders("Accept-Encoding");
        request.addHeader("Accept-Encoding", "gzip");*/
        try {
            HttpResponse response = http.execute(request);

            int i = response.getStatusLine().getStatusCode();
            if (i == 200) {
                //Log.v(Common.TAG_PHEREO, "i=200");
                HttpEntity ent = response.getEntity();
                if (ent != null) {
                    // Log.v(Common.TAG_PHEREO, "ent not null");
                    /* Header head = ent.getContentEncoding();
                  if (head != null) {
                      Log.v(Common.TAG_PHEREO, "head not null");
                      String sttr = head.getValue();
                      if (sttr.equalsIgnoreCase("gzip")) {
                          Log.v(Common.TAG_PHEREO, "is gzip");
                          InputStream body = ent.getContent();
                          if (body != null)
                              return new GZIPInputStream(body);
                      } else {
                          InputStream is = ent.getContent();
                          if (is != null)
                              return is;
                      }
                  } else {*/
                    InputStream is = ent.getContent();
                    if (is != null) {
                        //Log.v(Common.TAG_PHEREO, "Input Stream is not null");
                        return is;
                        //}
                    }
                }
            }
        } catch (IOException e) {
          e.printStackTrace();
        }
        //Log.v(Common.TAG_PHEREO, "NO resp stream");
        return null;
    }

    public static String makeJSONStringWithoutAssets(InputStream response) {
        String json = "";
        if (response != null) {
            byte[] buffer = new byte[6500];
            try {
                response.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String a = new String(buffer);
            json = a.split("\\}")[0] + "}";
        }
        return json;
    }

    public static String makeJSONStringWithAssetsFromFile(File file) {
        String json = "";
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
           e.printStackTrace();
        }
        if (input != null) {
            byte[] buffer = new byte[65000];
            try {
                input.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String a = new String(buffer);
            json = a.split("\\]\\}")[0] + "]}";
        }
        return json;
    }

    public static String makeProactiveJSONStringWFromFile(File file) {
        String json = "";
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
           e.printStackTrace();
        }
        if (input != null) {
            byte[] buffer = new byte[65000];
            try {
                input.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String a = new String(buffer);
            json = a.split("\\]\\}\\]\\}\\]\\}")[0] + "]}]}]}";
        }
        return json;
    }
}

