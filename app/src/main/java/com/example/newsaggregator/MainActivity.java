package com.example.newsaggregator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

import javax.xml.transform.Source;

public class MainActivity extends AppCompatActivity {
    private final HashMap<String, String> countyCodes = new HashMap<>();
    private final HashMap<String, String> languageCode = new HashMap<>();
    ArrayList<String> countriesMenu = new ArrayList<String>();
    ArrayList<String> languageMenu = new ArrayList<String>();


    private final HashMap<String, HashSet<TotalSources>> topicsHash = new HashMap<>();
    private final HashMap<String, HashSet<TotalSources>> languageHash = new HashMap<>();
    private final HashMap<String, HashSet<TotalSources>> contryHash = new HashMap<>();

    private final ArrayList<TotalArticles> currentArticle = new ArrayList<>();
    private final ArrayList<TotalSources> sourceDisplayed = new ArrayList<>();


    private Menu menu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private NewsAdapter newsAdapter;
    private ArrayAdapter<TotalSources> arrayAdapter;
    private ViewPager2 viewPager;
    private String topic = "";
    private String language = "";
    private String country = "";
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            InputStream input = getResources().openRawResource(R.raw.country_codes);
            InputStream input2 = getResources().openRawResource(R.raw.language_codes);
            BufferedReader reader = new BufferedReader((new InputStreamReader(input)));
            BufferedReader reader2 = new BufferedReader((new InputStreamReader(input2)));
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            String line;
            String line2;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            while ((line2 = reader2.readLine()) != null) {
                sb2.append(line2).append('\n');
            }
            countryJsonObject(sb.toString());
            languageJsonObject(sb2.toString());
        }
        catch (Exception e){
            ;
        }

        setContentView(R.layout.activity_main);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);
        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    selectItem(position);
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
        );
        mDrawerToggle = new ActionBarDrawerToggle(
                this,            /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        newsAdapter = new NewsAdapter(this,currentArticle);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(newsAdapter);
        download();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    public void download() {
        if (!this.netCheck()) {
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
        } else {
            //Runnable info = new NewsRunnable(this, "cnn");
            Runnable info2 = new OptionsRunnable(this);
            //new Thread(info).start();
            new Thread(info2).start();
            findViewById(R.id.progressBar).setVisibility(View.GONE);

        }

    }

    private boolean netCheck() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    public void updateArticleData(ArrayList<TotalArticles> w) {
        currentArticle.clear();

        currentArticle.addAll(w);
        newsAdapter.notifyDataSetChanged();
        setTitle(title);
        viewPager.setCurrentItem(0);
    }

    public void updateSourcesData(ArrayList<TotalSources> sList) {
        for (TotalSources sources : sList) {
            //String id = sources.getId();
            //String name = sources.getName();

            if (!topicsHash.containsKey(sources.getCategory()))
                topicsHash.put(sources.getCategory(), new HashSet<>());
            Objects.requireNonNull(topicsHash.get(sources.getCategory())).add(sources);

            if (!languageHash.containsKey(sources.getLanguage()))
                languageHash.put(languageCode.get(sources.getLanguage().toUpperCase(Locale.ROOT)), new HashSet<>());
            Objects.requireNonNull(languageHash.get(languageCode.get(sources.getLanguage().toUpperCase(Locale.ROOT)))).add(sources);
            Log.d("UPDATE", "updateSourcesData: " + sources.getLanguage()+languageCode.keySet() + languageCode.get(sources.getLanguage().toUpperCase(Locale.ROOT)));

            if (!contryHash.containsKey(sources.getCountry()))
                contryHash.put(countyCodes.get(sources.getCountry().toUpperCase(Locale.ROOT)), new HashSet<>());
            Objects.requireNonNull(contryHash.get(countyCodes.get(sources.getCountry().toUpperCase(Locale.ROOT)))).add(sources);

           // if (!sourceToArticle.containsKey(id))
           //    sourceToArticle.put(id, new ArrayList<>());
           //Objects.requireNonNull(sourceToArticle.get(id)).add(source);
        }



        ////

        sourceDisplayed.addAll(sList);
        setTitle("News Aggregator" + "(" + sourceDisplayed.size() + ")");
        contryHash.put("All",new HashSet<>(sList));
        topicsHash.put("All",new HashSet<>(sList));
        languageHash.put("All",new HashSet<>(sList));


        arrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_item, sourceDisplayed);
        mDrawerList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        createMenu();
        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        this.menu = m;
        return super.onCreateOptionsMenu(menu);
    }

    public void createMenu(){
        try {

            SubMenu TopicSub;
            SubMenu countrySub;
            SubMenu LanguageSub;
            TopicSub = menu.addSubMenu("Topics");
            countrySub = menu.addSubMenu("Countries");
            LanguageSub = menu.addSubMenu("Languages");
            ArrayList<String> topicsList = new ArrayList<>();
            topicsList.addAll(topicsHash.keySet());
            ArrayList<String> countryList = new ArrayList<>(contryHash.keySet());
            ArrayList<String> languageList = new ArrayList<>(languageHash.keySet());
            Collections.sort(topicsList);
            Collections.sort(countryList);
            Collections.sort(languageList);
            Log.d("Topic menu", "onCreateOptionsMenu: " + topicsList.size() + topicsHash.size());
            Log.d("country menu", "onCreateOptionsMenu: " + countryList.size());
            Log.d("language menu", "onCreateOptionsMenu: " + languageList.size());
            for(int i = 0; i < topicsList.size(); i++){
                TopicSub.add(0,i+1,i+1,topicsList.get(i));
            }

            for(int i = 0; i < countryList.size(); i++){
                countrySub.add(1,i+1,i+1, countryList.get(i));
                Log.d("MENU", "createMenu: " + countyCodes.keySet());
            }

            for(int i = 0; i < languageList.size(); i++){
                LanguageSub.add(2,i+1,i+1,languageList.get(i));
            }



        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void countryJsonObject(String s){
        //parse json
        //create hashmap off country code and lang code
        //hashmap used to update news object
        //hashmap passed to onCreateOptionsMenu
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray countryArray = jObjMain.getJSONArray("countries");

            for (int i = 0; i < countryArray.length(); i++) {
                JSONObject currentCountry = (JSONObject) countryArray.get(i);
                String code = currentCountry.getString("code");
                String name = currentCountry.getString("name");
                countyCodes.put(code,name);
                countriesMenu.add(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void languageJsonObject(String s){
        //parse json
        //create hashmap off country code and lang code
        //hashmap used to update news object
        //hashmap passed to onCreateOptionsMenu
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray countryArray = jObjMain.getJSONArray("languages");

            for (int i = 0; i < countryArray.length(); i++) {
                JSONObject currentCountry = (JSONObject) countryArray.get(i);
                String code = currentCountry.getString("code");
                String name = currentCountry.getString("name");
                languageCode.put(code,name);
                languageMenu.add(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private void selectItem(int position) {
        viewPager.setBackground(null);
        NewsRunnable news2 = new NewsRunnable(this, sourceDisplayed.get(position).getId());
        new Thread(news2).start();
        title = sourceDisplayed.get(position).getName();

        mDrawerLayout.closeDrawer(mDrawerList);



    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.hasSubMenu()){
            return true;}
        int parentId = item.getGroupId();

        if(parentId == 0) {
            topic = item.toString();
            if(topic.equals("All")){
                sourceDisplayed.addAll(topicsHash.get("All"));
            }
            sourceDisplayed.retainAll(topicsHash.get(topic));
            arrayAdapter.notifyDataSetChanged();
            setTitle("News Aggregator" + "(" + sourceDisplayed.size() + ")");
        }
        if(parentId == 1){
            country = item.toString();
            if(country.equals("All")){
                sourceDisplayed.addAll(contryHash.get("All"));
            }
            Log.d("Vedant is AWESOME", "onOptionsItemSelected: " + country + contryHash.keySet());
            sourceDisplayed.retainAll(contryHash.get(country));
            arrayAdapter.notifyDataSetChanged();
            setTitle("News Aggregator" + "(" + sourceDisplayed.size() + ")");
        }
        if(parentId == 2){
            language = item.toString();
            if(language.equals("All")){
                sourceDisplayed.addAll(languageHash.get("All"));
            }
            sourceDisplayed.retainAll(languageHash.get(language));
            arrayAdapter.notifyDataSetChanged();
            setTitle("News Aggregator" + "(" + sourceDisplayed.size() + ")");

        }
        return super.onOptionsItemSelected(item);
    }

}

