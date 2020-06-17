package com.example.newsgateway;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static java.net.HttpURLConnection.HTTP_OK;


public class AsyncSourceLoader extends AsyncTask<String, Void, String> {
    private static final String TAG = "AsyncSourceLoader";
    private MainActivity mainActivity;
    private static final String dataURL = "https://newsapi.org/v2/sources";
    private static final String yourAPIKey = "7636d8c746a44712916116f6fe0bedc0";

    private HashSet<String> topics = new HashSet<String>();
    private HashSet<String> languages = new HashSet<String>();
    private HashSet<String> countries = new HashSet<String>();

    private static HashMap<String, String> countryCodeList_ma = new HashMap<>();
    private static HashMap<String, String> languageCodeList_ma = new HashMap<>();
    private static HashMap<String, String> sourceDetails = new HashMap<>();


    public AsyncSourceLoader(MainActivity mainActivity, HashMap<String, String> countryCodeList_ma, HashMap<String, String> languageCodeList_ma) {
        this.mainActivity = mainActivity;
        this.countryCodeList_ma = countryCodeList_ma;
        this.languageCodeList_ma = languageCodeList_ma;

        Log.d(TAG, "AsyncSourceLoader: "+countryCodeList_ma.size() + languageCodeList_ma.size());

    }



    @Override
    protected void onPostExecute(String s) {
        if (s == null) {
            mainActivity.dataDownloadFailed();
        }
        else
        {
            try {
                HashMap<String,Source>  sourceListResult= parseJSON(s);
                mainActivity.setupSource(sourceListResult,topics,languages,countries,sourceDetails);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        /*HashMap<String, HashSet<String>> regionMap = parseJSON(s);
        if (regionMap != null) {
            mainActivity.setupRegions(regionMap);
        }*/
    }



    @Override
    protected String doInBackground(String... strings) {
        Uri.Builder buildURL = Uri.parse(dataURL).buildUpon();
        buildURL.appendQueryParameter("apiKey", yourAPIKey);
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: " + urlToUse);
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Log.d(TAG, "doInBackground: " + conn.getResponseCode());

            if (conn.getResponseCode() == HTTP_OK) {
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } else {
                return null;
            }
            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return sb.toString();
    }


    private HashMap<String, Source> parseJSON(String s) throws JSONException {
       // Log.d(TAG, "parseJSON: "+s);
        HashMap<String, Source> SourceList = new HashMap<>();
        JSONObject sourceDetailsJSON = new JSONObject(s);
        JSONArray sources = sourceDetailsJSON.getJSONArray("sources");

        String language_fullName = null;
        String country_fullName = null;
        for(int i =0 ;i<sources.length();i++)
        {
           // Log.d(TAG, "parseJSON: Source " +sources.get(i).toString());
            JSONObject jsonObject = sources.getJSONObject(i);

            String id = jsonObject.getString("id");

            String name = jsonObject.getString("name");
            String category = jsonObject.getString("category");
            String language = jsonObject.getString("language");
            String country = jsonObject.getString("country");
            Log.d(TAG, "parseJSON: "+id + name + category + language + country);

            Set set = countryCodeList_ma.entrySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                Map.Entry me2 = (Map.Entry) iterator.next();
                if (me2.getKey().toString().contains(country.toUpperCase()))
                {
                    Log.d(TAG, "parseJSON1: " +me2.getValue().toString());
                    country_fullName = me2.getValue().toString();

                }
            }

            Set set_lang = languageCodeList_ma.entrySet();
            Iterator iterator_lang = set_lang.iterator();
            while (iterator_lang.hasNext())
            {
                Map.Entry me2 = (Map.Entry) iterator_lang.next();
                if (me2.getKey().toString().contains(language.toUpperCase()))
                {
                    Log.d(TAG, "parseJSON1: " +me2.getValue().toString());

                    language_fullName = me2.getValue().toString();
                }
            }
            
           // Log.d(TAG, "parseJSON1: "+id + name + category + language_fullName + country_fullName);

            //Load the hash sets for the menu
            topics.add(category);
            languages.add(language_fullName);
            countries.add(country_fullName);

            SourceList.put(name,new Source(id,name,category,language_fullName,country_fullName));
            String sourceCheck = category + "|" + language_fullName +"|"+country_fullName +"-" + name;
            sourceDetails.put(id,sourceCheck);
        }


        return SourceList;
    }




}
