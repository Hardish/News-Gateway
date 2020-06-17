package com.example.newsgateway;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class DataContainer {
  /*
        load data and keep data available in Hashmap
        reading the json file from the row folder
    */
    private static final String TAG = "DataContainer";

    private static HashMap<String, String> countryCodeList = new HashMap<>();
    private static HashMap<String, String> revCountryCodeList = new HashMap<>();
    private static HashMap<String, String> languageCodeList = new HashMap<>();
    private static HashMap<String, String> revLanguageCodeList = new HashMap<>();


    public static void loadData(MainActivity mainActivity) {
        try
        {
            JSONObject countryJSON = loadJsonCounntryData(mainActivity);
            parseCountryJSON(countryJSON);

            JSONObject languageJSON = loadJsonLanguageData(mainActivity);
            paeseLanguageJSON(languageJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainActivity.dataReady(countryCodeList,revCountryCodeList,languageCodeList,revLanguageCodeList);
    }

    private static void paeseLanguageJSON(JSONObject languageJSON) throws JSONException {

        JSONArray jsonArray = languageJSON.getJSONArray("languages");

        for(int i =0 ;i<jsonArray.length();i++)
        {
            // Log.d(TAG, "parseCountryJSON: "+jsonArray.get(i).toString());
            JSONObject country_JSON = jsonArray.getJSONObject(i);
            String language_code = country_JSON.getString("code");
            String language_name = country_JSON.getString("name");
            Log.d(TAG, "parseCountryJSON: "+language_code + language_name);

            languageCodeList.put(language_code,language_name);
            revLanguageCodeList.put(language_name,language_code);

            

        }
    }

    private static JSONObject loadJsonLanguageData(MainActivity mainActivity) throws IOException, JSONException {
//context refer to main activity as getResouce is availble using mainActivity only
        InputStream is = mainActivity.getResources().openRawResource(R.raw.language_codes);

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        String line = reader.readLine();
        while (line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        reader.close();

        return new JSONObject(sb.toString());

    }

    private static void parseCountryJSON(JSONObject countryJSON) throws JSONException {

       JSONArray jsonArray = countryJSON.getJSONArray("countries");

        for(int i =0 ;i<jsonArray.length();i++)
        {
           // Log.d(TAG, "parseCountryJSON: "+jsonArray.get(i).toString());
            JSONObject country_JSON = jsonArray.getJSONObject(i);
            String country_code = country_JSON.getString("code");
            String country_name = country_JSON.getString("name");
            Log.d(TAG, "parseCountryJSON: "+country_code + country_name);

            countryCodeList.put(country_code,country_name);
            revCountryCodeList.put(country_name,country_code);

        }


    }


    private static JSONObject loadJsonCounntryData(MainActivity mainActivity)
            throws IOException, JSONException {
        //context refer to main activity as getResouce is availble using mainActivity only
        InputStream is = mainActivity.getResources().openRawResource(R.raw.country_codes);

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        String line = reader.readLine();
        while (line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        reader.close();

        return new JSONObject(sb.toString());

    }

}
