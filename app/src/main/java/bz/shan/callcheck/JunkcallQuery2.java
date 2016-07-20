package bz.shan.callcheck;

import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
    private String keyString2 = "廣告電話清單資料";
    private String keyString3 = "並非廣告電話";
    private String entitySelector = ".x > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(1) > font:nth-child(1) > strong:nth-child(1) > b:nth-child(1) > font:nth-child(1)";
    private String entitySelector2 = "table.post2:nth-child(1) > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(1) > font:nth-child(1) > b:nth-child(1)";
    private String nonAdSelector = ".color2 > strong:nth-child(1) > font:nth-child(1)";
    private String adSelector = ".x > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1) > strong:nth-child(1) > font:nth-child(1)";
    //private Pattern pattern = Pattern.compile("<td><font size=\"2\"><strong><b><font size=\"2\">([^<>\\\\]*)</font></b></strong></font></td>");
    private Pattern pattern = Pattern.compile("名稱資訊\\:(.+)下載");
    private ExecutorService pool = null;

    public JunkcallQuery2() {
        pool = Executors.newFixedThreadPool(1);
    }

    public QueryResult check(final String number, final Context context) throws ConnectionException {

//        ConnectivityManager connMgr = (ConnectivityManager)
//                context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//        if (networkInfo == null || !networkInfo.isConnected()) {
//            throw new ConnectionException("no network connection: " + (networkInfo == null ? "null" : networkInfo));
//        }

        try {
            return pool.submit(new Callable<QueryResult>() {
                @Override
                public QueryResult call() throws Exception {
                    try {
                        URL url = new URL(baseURL + number);

                        Log.i(LOG_TAG, url.toString());
                        Document doc = Jsoup.connect(url.toString()).get();
//                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                        //ByteArrayOutputStream out = new ByteArrayOutputStream();
//                        FileOutputStream out= new FileOutputStream("/home/shan/Downloads/JUNKCALL.org "+number+".html");
//                        InputStream in = connection.getInputStream();
//
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
//                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
//                        //StringBuilder buf = new StringBuilder();
//
//                        String line = reader.readLine();
//
//                        if (line != null) {
//                            writer.write(line);
//                            writer.write("\n");
//                        }
//                        while ((line = reader.readLine()) != null) {
//                            writer.write(line);
//                            writer.write("\n");
//                        }


                        Elements entityEle = doc.select(entitySelector);
                        Elements entityEle2 = doc.select(entitySelector2);
                        Elements adEle = doc.select(adSelector);
                        Elements nonAdEle = doc.select(nonAdSelector);

                        Log.i(LOG_TAG, entityEle.text());
                        Log.i(LOG_TAG, entityEle2.text());
                        Log.i(LOG_TAG, adEle.text());
                        Log.i(LOG_TAG, nonAdEle.text());

                        String entity = "";
                        if (entityEle.size() == 0) {
                            if (entityEle2.size() > 0) {
                                entity = entityEle2.get(0).text().trim();
//                                Matcher matcher = pattern.matcher(txt);
//                                if(matcher.find()) {
//                                    entity = matcher.group(1).trim();
//                                }
                            }
                        } else {
                            entity = entityEle.get(0).text().trim();
                        }


                        boolean junk = false;
                        if (adEle.size() > 0) {
                            junk = keyString2.equals(adEle.get(0).text().trim());
                        }
                        if (nonAdEle.size() > 0 && nonAdEle.get(0).text().trim().contains(keyString3)) {
                            junk = false;
                        }

                        if("".equals(entity)){
                            entity = "unknown";
                        }
                        return new QueryResult(entity, junk);
                    } catch (IOException e) {
                        throw new ConnectionException(e);
                    }
//                    } finally {
//                        if (connection != null) {
//                            connection.disconnect();
//                        }
//                    }
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
