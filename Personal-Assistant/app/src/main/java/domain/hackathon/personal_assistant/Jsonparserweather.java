package domain.hackathon.personal_assistant;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by darkness7245 on 3/29/2018.
 */

public class Jsonparserweather {
    private static final String TAG = Jsonparserweather.class.getSimpleName();
    public String response;
    private String test = "https://personalassistant-ec554.appspot.com/recognize/weather/FL/Orlando";


    public Jsonparserweather() {
    }

    public String makeServiceCall(final String reqUrl) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //connect to the api url
                    URL url = new URL(reqUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();
                    //read the response
                    InputStream in = conn.getInputStream();
                    response = convertStreamToString(in);
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                }
            }
        });
        thread.start();
        try{
            thread.join();
            return response;
        } catch (Exception e){
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return null;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
