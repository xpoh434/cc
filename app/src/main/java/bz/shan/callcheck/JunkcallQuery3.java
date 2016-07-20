package bz.shan.callcheck;

import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
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
public class JunkcallQuery3 {

    private static final String LOG_TAG = "JunkcallQuery3";
    private String baseURL = "https://number.whoscall.com/en-US/hk/%s/";
    private String entitySelector = ".number-info__name";
    private ExecutorService pool = null;

    public JunkcallQuery3() {
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
                        URL url = new URL(String.format(baseURL, number));

                        Log.i(LOG_TAG, url.toString());
                        Document doc = Jsoup.connect(url.toString()).get();

                        Elements entityEle = doc.select(entitySelector);

                        Log.i(LOG_TAG, entityEle.text());

                        String entity = "";
                        boolean junk = false;
                        if (entityEle.size() > 0) {
                            entity = entityEle.text();
                            junk = entityEle.hasClass("number-info__name--spam");
                        }

                        return new QueryResult(entity, junk);
                    } catch (IOException e) {
                        throw new ConnectionException(e);
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
