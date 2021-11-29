package com.example.newsaggregator;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class NewsRunnable implements Runnable{
    private static final String TAG = "CountryLoaderRunnable";
    private final MainActivity main;
    private final String newsSource;

    private static final String DATA_URL ="https://newsapi.org/v2/top-headlines?";
    private static final String yourAPIKey = "40158b7d4e3542d68cfba95cb657d4ef";

    NewsRunnable(MainActivity main, String newsSource) {
        this.main = main;
        this.newsSource = newsSource;
    }
    @Override
    public void run(){
        Uri.Builder buildURL = Uri.parse(DATA_URL).buildUpon();
        buildURL.appendQueryParameter("sources", newsSource);
        buildURL.appendQueryParameter("apiKey", yourAPIKey);
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
            final ArrayList<TotalArticles> w = parseArticalsJSON(jsonString);
            main.runOnUiThread(() -> main.updateArticleData(w));
        }


    private ArrayList<TotalArticles> parseArticalsJSON(String s) {
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray articalArray = jObjMain.getJSONArray("articles");
            ArrayList<TotalArticles> totalArticles = new ArrayList<>();
            for (int i = 0; i < articalArray.length(); i++) {
                JSONObject currentArticle = (JSONObject) articalArray.get(i);
                String author = currentArticle.getString("author");
                String title = currentArticle.getString("title");
                String description = currentArticle.getString("description");
                String url = currentArticle.getString("url");
                String urlToImage = currentArticle.getString("urlToImage");
                String publishedAt = currentArticle.getString("publishedAt");
                TotalArticles article = new TotalArticles(author,title,description,url,urlToImage,publishedAt);
                totalArticles.add(article);
           }



            return totalArticles;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
