package com.example.newsaggregator;

import android.net.Uri;
import android.os.Looper;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class OptionsRunnable implements Runnable {
    private static final String TAG = "CountryLoaderRunnable";
    private final MainActivity main;
    /*private final String topic;
    private final String language;
    private final String country;*/
    private static final String DATA_URL ="https://newsapi.org/v2/sources?";
    private static final String yourAPIKey = "40158b7d4e3542d68cfba95cb657d4ef";

   OptionsRunnable(MainActivity main) {
        this.main = main;

    }
    @Override
    public void run(){
        Uri.Builder buildURL = Uri.parse(DATA_URL).buildUpon();
        /*if (topic != "") {
            buildURL.appendQueryParameter("category", topic);
        }
        if (language != "") {
            buildURL.appendQueryParameter("language", language);
        }
        if (country != "") {
            buildURL.appendQueryParameter("country", country);
        }*/
        buildURL.appendQueryParameter("apiKey", yourAPIKey);
        Looper.prepare();
       //Toast.makeText(main, buildURL.toString(), Toast.LENGTH_SHORT).show();
        String urlToUse = buildURL.build().toString();

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent","");
            connection.connect();

            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                handleResults(null);
                return;
            }

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (Exception e) {
            handleResults(null);
            return;
        }
        handleResults(sb.toString());
    }

    public void handleResults(final String jsonString) {
            final ArrayList<TotalSources> w = parseSourcesJSON(jsonString);
            main.runOnUiThread(() -> main.updateSourcesData(w));
    }

    private ArrayList<TotalSources> parseSourcesJSON(String s) {
        ArrayList<TotalSources> totalSources= new ArrayList<>();
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray sourcesArray = jObjMain.getJSONArray("sources");

            for (int i = 0; i < sourcesArray.length(); i++) {
                JSONObject currentSources = (JSONObject) sourcesArray.get(i);
                String id = currentSources.getString("id");
                String name = currentSources.getString("name");
                String category = currentSources.getString("category");
                String language = currentSources.getString("language");
                String country = currentSources.getString("country");
                TotalSources source = new TotalSources(id,name,category,language,country);
                totalSources.add(source);
            }



            return totalSources;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalSources;
    }
}
