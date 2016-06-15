package bz.shan.callcheck;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shan on 5/20/16.
 */
public class JunkcallQuery2 {

    private static final String LOG_TAG = "JunkcallQuery";
    private String baseURL = "http://junkcall.org/hk/?q=";
    private String keyString = "沒有資料";
    private Pattern pattern = Pattern.compile("<td><font size=\"2\"><strong><b><font size=\"2\">([^<>\\\\]*)</font></b></strong></font></td>");
    private ExecutorService pool = null;

    public JunkcallQuery2() {
        pool = Executors.newFixedThreadPool(1);
    }

    public static class ConnectionException extends RuntimeException {
        public ConnectionException(Exception e) {
            super(e);
        }
        public ConnectionException(String msg) {
            super(msg);
        }
    }

    public String check(final String number, final Context context) throws ConnectionException {

        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//        if (networkInfo == null || !networkInfo.isConnected()) {
//            throw new ConnectionException("no network connection: " + (networkInfo == null ? "null" : networkInfo));
//        }

        try {
            return pool.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    HttpURLConnection connection = null;
                    try {
                        URL url = new URL(baseURL + number);

                        Log.i(LOG_TAG, url.toString());
                        connection = (HttpURLConnection) url.openConnection();

                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        InputStream in = connection.getInputStream();

                        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            Log.e(LOG_TAG, "error response code=" + connection.getResponseCode());
                            throw new ConnectionException("invalid response");
                        }

                        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                        StringBuilder buf = new StringBuilder();

                        String line = reader.readLine();

                        if (line != null) {
                            buf.append(line);
//                            Log.i(LOG_TAG, line);
//                            if (line.indexOf(keyString) > 0) {
//                                return true;
//                            }
                        }
                        while ((line = reader.readLine()) != null) {
//                            buf.append('\n');
                            buf.append(line);
//                            Log.i(LOG_TAG, line);
//                            if (line.indexOf(keyString) > 0) {
//                                return true;
//                            }
                        }

//                        Log.i(LOG_TAG, buf.toString());
                        String src = buf.toString();
                        boolean notJunk = src.indexOf(keyString) >= 0;
                        if (notJunk) {
                            return "";
                        } else{
                            Matcher matcher = pattern.matcher(src);
                            if(matcher.find()) {
                                String entity = matcher.group(1).trim();
                                if("".equals(entity)) {
                                    return "unknown";
                                }
                                else {
                                    return entity;
                                }
                            } else {
                                return "unknown";
                            }
                        }
                    } catch (IOException e){
                        throw new ConnectionException(e);
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                }
            }).get();
        } catch (ExecutionException e) {
            if(e.getCause() instanceof ConnectionException) {
                throw (ConnectionException)e.getCause();
            } else{
                throw new RuntimeException(e);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
