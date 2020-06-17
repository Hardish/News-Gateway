package com.example.newsgateway;

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
import java.util.ArrayList;
import java.util.HashMap;

import static java.net.HttpURLConnection.HTTP_OK;

public class AsyncArticleDetailLoader extends AsyncTask<String, Void, ArrayList<Article>> {

    //https://newsapi.org/v2/top-headlines?sources=cnn&apiKey=7636d8c746a44712916116f6fe0bedc0

    private static final String TAG = "AsyncArticleDetailLoade";
    private String sourceId;
    private  String DATA_URL = "https://newsapi.org/v2";
    private static final String yourAPIKey = "7636d8c746a44712916116f6fe0bedc0";
    private MainActivity mainActivity;
    private static HashMap<String, ArrayList<Article>> cachedArticle = new HashMap<>();


    public AsyncArticleDetailLoader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPostExecute(ArrayList<Article> articles) {
        if(articles.size()== 0)
        {

        }
        else
        {
            mainActivity.setArticles(articles);
        }


    }

    @Override
    protected ArrayList<Article> doInBackground(String... strings) {


        //source name/id of the source will pass from the main method.
        sourceId = strings[0];

        if (cachedArticle.containsKey(sourceId))
            return cachedArticle.get(sourceId);

        Uri.Builder buildURL = Uri.parse(DATA_URL).buildUpon();
        buildURL.appendPath("top-headlines");
        buildURL.appendQueryParameter("sources", sourceId);
        buildURL.appendQueryParameter("apiKey", yourAPIKey);


        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: "+urlToUse);
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

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        ArrayList<Article> articlesList = parseJSON(sb.toString());
        cachedArticle.put(sourceId,articlesList);
        return articlesList;
    }

    private ArrayList<Article> parseJSON(String s) {

        ArrayList<Article> articlesList = new ArrayList<>();

        try {
            JSONObject articleJSON = new JSONObject(s);
            JSONArray articleDetailJSON = articleJSON.getJSONArray("articles");

            for(int i = 0; i< articleDetailJSON.length();i++)
            {
                String articleAuthorname = null;
                String articleTitle = null;
                String articleDesc = null;
                String articleURL = null;
                String articleImageURL = null;
                String articlePublishedDate = null;

                JSONObject rowArticle = articleDetailJSON.getJSONObject(i);

                if (rowArticle.has("author")) {
                    articleAuthorname = rowArticle.getString("author");
                }

                if(rowArticle.has("title"))
                {
                    articleTitle = rowArticle.getString("title");
                }

                if(rowArticle.has("description"))
                {
                    articleDesc  = rowArticle.getString("description");
                }

                if(rowArticle.has("url"))
                {
                    articleURL = rowArticle.getString("url");
                }

                if(rowArticle.has("urlToImage"))
                {
                    articleImageURL = rowArticle.getString("urlToImage");
                }
                if(rowArticle.has("publishedAt"))
                {
                    articlePublishedDate  = rowArticle.getString("publishedAt");
                }

                articlesList.add(new Article(articleAuthorname,articleTitle,articleDesc,articleURL,articleImageURL,articlePublishedDate));

            }

            return articlesList;


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }
}
