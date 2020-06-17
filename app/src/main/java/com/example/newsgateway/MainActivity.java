package com.example.newsgateway;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ArrayList<String> sourceDisplayed = new ArrayList<>();
    private HashMap<String, Source> SourceList = new HashMap<>();
    private ArrayList<String> ListMenu = new ArrayList<>();
    private static ArrayList<String> selectedListMenu = new ArrayList<>();   // push menu selection for updating drawer
    private static HashMap<String, String> selectedListMenu_check = new HashMap<>();// push menu selection for updating drawer

    ArrayList<String> sourceName = new ArrayList<>();
    ArrayList<String> sourceid = new ArrayList<>();
    ArrayList<String> copy_sourceName = new ArrayList<>();
    ArrayList<String> copy_sourceid = new ArrayList<>();

    private String selectedMenu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private Menu opt_menu;
    private static HashMap<String, String> sourceDetails_ma = new HashMap<>();  //to iterate for selected country,language and category

    private static HashMap<String, String> countryCodeList_ma = new HashMap<>();
    private static HashMap<String, String> revCountryCodeList_ma = new HashMap<>();
    private static HashMap<String, String> languageCodeList_ma = new HashMap<>();
    private static HashMap<String, String> revLanguageCodeList_ma = new HashMap<>();

    private static boolean topic_flag = false;
    private static boolean language_flag = false;
    private static boolean country_flag = false;

    private static String topicSelection;
    private static String languageSelection;
    private static String countrySelection;

    private List<Fragment> fragments = new ArrayList<>();
    private NewsPagerAdapter newsPagerAdapter;
    private ViewPager pager;
    private String currentSelectedSource; //current selection in drawer

    SubMenu topic_menu;
    SubMenu language_menu;
    SubMenu country_menu;

    private String topicsSelected;
    private String lannguageSelected;
    private String countrySelected;

    ArrayList<String> copy_topics_local = new ArrayList<>();
    ArrayList<String> copy_language_local = new ArrayList<>();
    ArrayList<String> copy_country_local = new ArrayList<>();

    List<String> topic_list = new ArrayList<String>();

    List<String> lang_list = new ArrayList<String>();

    List<String> country_list = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //step1: load the json files and create Hashmap
        DataContainer.loadData(this);
        //step2:load all sources in list and put into drawer

        mDrawerLayout = findViewById(R.id.drawer_layout); // <== Important!
        mDrawerList = findViewById(R.id.drawer_list); // <== Important!


        mDrawerList.setOnItemClickListener(   // <== Important!
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectItem(position);
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                }
        );

        mDrawerToggle = new ActionBarDrawerToggle(   // <== Important!
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );


        newsPagerAdapter = new NewsPagerAdapter(getSupportFragmentManager(), fragments);
        pager = findViewById(R.id.viewpager);
        pager.setAdapter(newsPagerAdapter);

        if (sourceDisplayed.isEmpty()) {
            //call Async task to load source
            new AsyncSourceLoader(this, countryCodeList_ma, languageCodeList_ma).execute();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        opt_menu = menu;
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState(); // <== IMPORTANT
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig); // <== IMPORTANT
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {  // <== Important!
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        } else {
         /*   if(topic_flag == true && country_flag == true && language_flag == true)
            {

            }
            else
            {*/
            String selection = item.getTitle().toString();

            if (item.getSubMenu() == topic_menu) {
                if (selection.equalsIgnoreCase("Topics")) {
                    selectedMenu = selection + "-";
                    topic_flag = true;
                }

            } else if (item.getSubMenu() == language_menu) {
                if (selection.equalsIgnoreCase("Languages")) {
                    selectedMenu = selection + "-";
                    language_flag = true;
                }

            } else if (item.getSubMenu() == country_menu) {
                if (selection.equalsIgnoreCase("Country")) {
                    selectedMenu = selection + "-";
                    country_flag = true;
                }


            } else if (selection.isEmpty()) {

            } else {
                sourceDisplayed.clear();
                setTitle("News Gateway " + "( " + 0 + " )");
                Log.d(TAG, "onOptionsItemSelected: " + selection);
                selectedMenu = selectedMenu + selection;
                String[] keyValue = selectedMenu.trim().split("-", 2);
                selectedListMenu_check.put(keyValue[0], keyValue[1]);

                updateSelectionData(keyValue[0], keyValue[1]);
            }
        }


        //}


        // selectedListMenu.clear();
        return super.onOptionsItemSelected(item);

    }

    private void updateSelectionData(String value, String s) {
        {
            sourceName.clear();
            ArrayList<String> topics_local = new ArrayList<>();
            ArrayList<String> language_local = new ArrayList<>();
            ArrayList<String> country_local = new ArrayList<>();

            if ((value.equalsIgnoreCase("topics") && topicsSelected == null)) {
                topicSelection = s;
                //setValueInTopic(s);
                copy_topics_local.clear();
                Set check_value = sourceDetails_ma.entrySet();
                Iterator iterator_sourceDetails = check_value.iterator();

                if (s.equalsIgnoreCase("all")) {

                    while (iterator_sourceDetails.hasNext()) {
                        Map.Entry map_sourceDetail = (Map.Entry) iterator_sourceDetails.next();
                        {
                            topics_local.add(map_sourceDetail.getKey().toString());

                        }
                    }
                } else {
                    while (iterator_sourceDetails.hasNext()) {
                        Map.Entry map_sourceDetail = (Map.Entry) iterator_sourceDetails.next();
                        if (map_sourceDetail.getValue().toString().toUpperCase().contains(topicSelection.toUpperCase())) {
                            topics_local.add(map_sourceDetail.getKey().toString());

                        }
                    }
                }
                copy_topics_local.addAll(topics_local);
            }
            if (topicsSelected != null && topicSelection != null) {
                if (value.equalsIgnoreCase("topics")) {
                    if (s.equalsIgnoreCase(topicsSelected) && s.equalsIgnoreCase(topicSelection)) {
                        topics_local.addAll(copy_topics_local);
                    } else {
                        topicSelection = s;
                        // setValueInTopic(s);
                        copy_topics_local.clear();
                        Set check_value = sourceDetails_ma.entrySet();
                        Iterator iterator_sourceDetails = check_value.iterator();

                        if (s.equalsIgnoreCase("all")) {

                            while (iterator_sourceDetails.hasNext()) {
                                Map.Entry map_sourceDetail = (Map.Entry) iterator_sourceDetails.next();
                                {
                                    topics_local.add(map_sourceDetail.getKey().toString());

                                }
                            }
                        } else {
                            while (iterator_sourceDetails.hasNext()) {
                                Map.Entry map_sourceDetail = (Map.Entry) iterator_sourceDetails.next();
                                if (map_sourceDetail.getValue().toString().toUpperCase().contains(topicSelection.toUpperCase())) {
                                    topics_local.add(map_sourceDetail.getKey().toString());

                                }
                            }
                        }
                        copy_topics_local.addAll(topics_local);
                    }


                } else if (topicSelection.equalsIgnoreCase(topicsSelected) && !value.equalsIgnoreCase("topics")) {
                    topics_local.addAll(copy_topics_local);
                } else {
                    topicSelection = s;
                    // setValueInTopic(s);
                    copy_topics_local.clear();
                    Set check_value = sourceDetails_ma.entrySet();
                    Iterator iterator_sourceDetails = check_value.iterator();

                    if (s.equalsIgnoreCase("all")) {

                        while (iterator_sourceDetails.hasNext()) {
                            Map.Entry map_sourceDetail = (Map.Entry) iterator_sourceDetails.next();
                            {
                                topics_local.add(map_sourceDetail.getKey().toString());

                            }
                        }
                    } else {
                        while (iterator_sourceDetails.hasNext()) {
                            Map.Entry map_sourceDetail = (Map.Entry) iterator_sourceDetails.next();
                            if (map_sourceDetail.getValue().toString().toUpperCase().contains(topicSelection.toUpperCase())) {
                                topics_local.add(map_sourceDetail.getKey().toString());

                            }
                        }
                    }
                    copy_topics_local.addAll(topics_local);
                }

            }
////*

            if ((value.equalsIgnoreCase("Languages") && lannguageSelected == null)) {
                copy_language_local.clear();
                languageSelection = s;

                Set check_value = sourceDetails_ma.entrySet();
                Iterator iterator_sourceDetails = check_value.iterator();

                if (s.equalsIgnoreCase("all")) {

                    while (iterator_sourceDetails.hasNext()) {
                        Map.Entry map_sourceDetail = (Map.Entry) iterator_sourceDetails.next();
                        {
                            language_local.add(map_sourceDetail.getKey().toString());

                        }
                    }
                } else {
                    while (iterator_sourceDetails.hasNext()) {
                        Map.Entry map_sourceDetail = (Map.Entry) iterator_sourceDetails.next();
                        if (map_sourceDetail.getValue().toString().toUpperCase().contains(languageSelection.toUpperCase())) {
                            language_local.add(map_sourceDetail.getKey().toString());

                        }
                    }
                }
                copy_language_local.addAll(language_local);
            }
            if (lannguageSelected != null && languageSelection != null) {
                if (value.equalsIgnoreCase("Languages")) {
                    if (lannguageSelected.equalsIgnoreCase(s) && languageSelection.equalsIgnoreCase(s)) {
                        language_local.addAll(copy_language_local);
                    } else {
                        copy_language_local.clear();
                        languageSelection = s;

                        Set check_value = sourceDetails_ma.entrySet();
                        Iterator iterator_sourceDetails = check_value.iterator();

                        if (s.equalsIgnoreCase("all")) {

                            while (iterator_sourceDetails.hasNext()) {
                                Map.Entry map_sourceDetail = (Map.Entry) iterator_sourceDetails.next();
                                {
                                    language_local.add(map_sourceDetail.getKey().toString());

                                }
                            }
                        } else {
                            while (iterator_sourceDetails.hasNext()) {
                                Map.Entry map_sourceDetail = (Map.Entry) iterator_sourceDetails.next();
                                if (map_sourceDetail.getValue().toString().toUpperCase().contains(languageSelection.toUpperCase())) {
                                    language_local.add(map_sourceDetail.getKey().toString());

                                }
                            }
                        }
                        copy_language_local.addAll(language_local);
                    }
                } else if (lannguageSelected.equalsIgnoreCase(languageSelection)) {
                    language_local.addAll(copy_language_local);
                } else {
                    copy_language_local.clear();
                    languageSelection = s;

                    Set check_value = sourceDetails_ma.entrySet();
                    Iterator iterator_sourceDetails = check_value.iterator();

                    if (s.equalsIgnoreCase("all")) {

                        while (iterator_sourceDetails.hasNext()) {
                            Map.Entry map_sourceDetail = (Map.Entry) iterator_sourceDetails.next();
                            {
                                language_local.add(map_sourceDetail.getKey().toString());

                            }
                        }
                    } else {
                        while (iterator_sourceDetails.hasNext()) {
                            Map.Entry map_sourceDetail = (Map.Entry) iterator_sourceDetails.next();
                            if (map_sourceDetail.getValue().toString().toUpperCase().contains(languageSelection.toUpperCase())) {
                                language_local.add(map_sourceDetail.getKey().toString());

                            }
                        }
                    }
                    copy_language_local.addAll(language_local);
                }
            }


            if ((value.equalsIgnoreCase("Country") && countrySelected == null)) {
                copy_country_local.clear();
                countrySelection = s;
                Set check_value = sourceDetails_ma.entrySet();
                Iterator iterator_sourceDetails = check_value.iterator();

                if (s.equalsIgnoreCase("all")) {

                    while (iterator_sourceDetails.hasNext()) {
                        Map.Entry map_sourceDetail = (Map.Entry) iterator_sourceDetails.next();
                        {
                            country_local.add(map_sourceDetail.getKey().toString());

                        }
                    }
                } else {
                    while (iterator_sourceDetails.hasNext()) {
                        Map.Entry map_sourceDetail = (Map.Entry) iterator_sourceDetails.next();
                        if (map_sourceDetail.getValue().toString().toUpperCase().contains(countrySelection.toUpperCase())) {
                            country_local.add(map_sourceDetail.getKey().toString());

                        }
                    }
                }
                copy_country_local.addAll(country_local);
            }

            if (countrySelected != null && countrySelection != null) {
                if (value.equalsIgnoreCase("Country")) {
                    if (countrySelected.equalsIgnoreCase(s) && countrySelection.equalsIgnoreCase(s)) {
                        country_local.addAll(copy_country_local);
                    } else {
                        copy_country_local.clear();
                        countrySelection = s;
                        Set check_value = sourceDetails_ma.entrySet();
                        Iterator iterator_sourceDetails = check_value.iterator();

                        if (s.equalsIgnoreCase("all")) {

                            while (iterator_sourceDetails.hasNext()) {
                                Map.Entry map_sourceDetail = (Map.Entry) iterator_sourceDetails.next();
                                {
                                    country_local.add(map_sourceDetail.getKey().toString());

                                }
                            }
                        } else {
                            while (iterator_sourceDetails.hasNext()) {
                                Map.Entry map_sourceDetail = (Map.Entry) iterator_sourceDetails.next();
                                if (map_sourceDetail.getValue().toString().toUpperCase().contains(countrySelection.toUpperCase())) {
                                    country_local.add(map_sourceDetail.getKey().toString());

                                }
                            }
                        }
                        copy_country_local.addAll(country_local);
                    }
                } else if (countrySelected.equalsIgnoreCase(countrySelected)) {
                    country_local.addAll(copy_country_local);
                } else {
                    copy_country_local.clear();
                    countrySelection = s;
                    Set check_value = sourceDetails_ma.entrySet();
                    Iterator iterator_sourceDetails = check_value.iterator();

                    if (s.equalsIgnoreCase("all")) {

                        while (iterator_sourceDetails.hasNext()) {
                            Map.Entry map_sourceDetail = (Map.Entry) iterator_sourceDetails.next();
                            {
                                country_local.add(map_sourceDetail.getKey().toString());

                            }
                        }
                    } else {
                        while (iterator_sourceDetails.hasNext()) {
                            Map.Entry map_sourceDetail = (Map.Entry) iterator_sourceDetails.next();
                            if (map_sourceDetail.getValue().toString().toUpperCase().contains(countrySelection.toUpperCase())) {
                                country_local.add(map_sourceDetail.getKey().toString());

                            }
                        }
                    }
                    copy_country_local.addAll(country_local);
                }
            }

            //compare all of three

            compareCommonInThree(topics_local, language_local, country_local);


        }
    }

    private void compareCommonInThree(ArrayList<String> topics_local, ArrayList<String> language_local, ArrayList<String> country_local) {

        if (country_flag == true && topic_flag == true && language_flag == true) {
         topics_local.retainAll(language_local);
         language_local.retainAll(country_local);
         topics_local.retainAll(country_local);
         country_local.retainAll(topics_local);
         country_local.retainAll(language_local);
         language_local.retainAll(topics_local);
         updateSourceName(topics_local);

        } else if (country_flag == true && topic_flag == true) {
            topics_local.retainAll(country_local);
            updateSourceName(topics_local);
        } else if (country_flag == true && language_flag == true) {
            language_local.retainAll(country_local);
            updateSourceName(language_local);
        } else if (topic_flag == true && language_flag == true) {
            topics_local.retainAll(language_local);
            updateSourceName(topics_local);
        } else if (topic_flag == true) {
            updateSourceName(topics_local);
        } else if (language_flag == true) {
            updateSourceName(language_local);
        } else if (country_flag == true) {
            updateSourceName(country_local);
        }


    }

    private void updateSourceName(ArrayList<String> arrayList) {

        sourceName.clear();

        Set check_value = sourceDetails_ma.entrySet();
        Iterator iterator_sourceDetails = check_value.iterator();


        while (iterator_sourceDetails.hasNext()) {

            Map.Entry map_sourceDetail = (Map.Entry) iterator_sourceDetails.next();
            {
                for (String s : arrayList) {
                    if (map_sourceDetail.getKey().toString().toUpperCase().equalsIgnoreCase(s)) {
                        String[] name = map_sourceDetail.getValue().toString().trim().split("-", 2);
                        sourceName.add(name[1]);
                    }
                }
            }
        }


        updateDrawerList(sourceName);
    }

    private void updateDrawerList(ArrayList<String> sourceName) {


        updateStoringVariable();
        if (sourceName != null && sourceName.size()>=1) {

            Collections.sort(sourceName);
            sourceDisplayed.addAll(sourceName);
            setTitle("News Gateway " + "( " + sourceName.size() + " )");
            // Poke the adapter to redraw the pager

            ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();

        } else {
            sourceDisplayed.clear();
            ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
            makeAlertDialog();
        }
    }

    private void makeAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("No Data available for provided selection");
        builder.setTitle("Please change selection");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateStoringVariable() {

        Set updateValue = selectedListMenu_check.entrySet();
        Iterator iterator_value = updateValue.iterator();


        while (iterator_value.hasNext()) {
            Map.Entry map_update_value = (Map.Entry) iterator_value.next();
            {
                if (map_update_value.getKey().toString().equalsIgnoreCase("Topics")) {
                    if (topic_flag == true) {
                        topicsSelected = map_update_value.getValue().toString();
                    }
                }
                if (map_update_value.getKey().toString().equalsIgnoreCase("Languages")) {
                    if (language_flag == true) {
                        lannguageSelected = map_update_value.getValue().toString();
                    }
                }
                if (map_update_value.getKey().toString().equalsIgnoreCase("Country")) {
                    if (country_flag == true) {
                        countrySelected = map_update_value.getValue().toString();
                    }
                }


            }
        }
    }

    private void selectItem(int position) {


        // Clear the BG image
        pager.setBackground(null);

        // Get the selected subregion
        currentSelectedSource = sourceDisplayed.get(position);
        setTitle(currentSelectedSource);
        Set set_source = SourceList.entrySet();
        Iterator iterator_source = set_source.iterator();
        while (iterator_source.hasNext()) {
            Map.Entry me2 = (Map.Entry) iterator_source.next();
            if (me2.getKey().toString().toUpperCase().contains(currentSelectedSource.toUpperCase())) {
                Source source = (Source) me2.getValue();
                // Load the countries from that subregion
                new AsyncArticleDetailLoader(this).execute(source.getId());

            }
        }

        // Close the drawer

        mDrawerLayout.closeDrawer(mDrawerList);
    }



    public void setArticles(ArrayList<Article> articles) {
        // setTitle(currentSelectedSource);

        // Mark each current fragment in the pager as old (dirty)
        for (int i = 0; i < newsPagerAdapter.getCount(); i++)
            newsPagerAdapter.notifyChangeInPosition(i);

        // Clear the lis of existing fragments
        fragments.clear();

        // Creae new fragments, one per country in the selected subregion
        for (int i = 0; i < articles.size(); i++)
        {
            if(i<=9)
            {
                fragments.add(NewsDetailFragment.newInstance(articles.get(i), i + 1, articles.size()));
            }

        }



        // Poke the adapter to refresh the pager
        newsPagerAdapter.notifyDataSetChanged();

        // Set the pager to display the first country/fragment
        pager.setCurrentItem(0);

    }


    public void dataReady
            (HashMap<String, String> countryCodeList, HashMap<String, String> revCountryCodeList,
             HashMap<String, String> languageCodeList, HashMap<String, String> revLanguageCodeList) {

        countryCodeList_ma.putAll(countryCodeList);
        revCountryCodeList_ma.putAll(revCountryCodeList);
        languageCodeList_ma.putAll(languageCodeList);
        revLanguageCodeList_ma.putAll(revLanguageCodeList);

    }

    public void dataDownloadFailed() {
        Toast.makeText(this, "Failed to download source data", Toast.LENGTH_LONG).show();
    }

    public void setupSource
            (HashMap<String, Source> sourceListResult, HashSet<String> topics, HashSet<String> languages, HashSet<String> countries,
             HashMap<String, String> sourceDetails) {
        SourceList.clear();
        sourceDisplayed.clear();
        sourceDetails_ma.putAll(sourceDetails);
        //display menu
        topic_menu = opt_menu.addSubMenu("Topics");
        language_menu = opt_menu.addSubMenu("Languages");
        country_menu = opt_menu.addSubMenu("Country");

        topic_menu.add("all");
        language_menu.add("all");
        country_menu.add("all");
       /* List<String> topic_list = new ArrayList<String>(topics);
        Collections.sort(topic_list);
        List<String> lang_list = new ArrayList<String>(languages);
        Collections.sort(lang_list);
        List<String> country_list = new ArrayList<String>(countries);
        Collections.sort(country_list);*/

         topic_list = new ArrayList<String>(topics);
         Collections.sort(topic_list);
         lang_list = new ArrayList<String>(languages);
         Collections.sort(lang_list);
         country_list = new ArrayList<String>(countries);
         Collections.sort(country_list);

        for (String s : topic_list) {

            topic_menu.add(s);
            //   ListMenu.add(s);
        }


        for (String l : lang_list) {

            language_menu.add(l);
            //  ListMenu.add(l);
        }


        for (String c : country_list) {

            country_menu.add(c);
            // ListMenu.add(c);
        }


        if (sourceListResult.isEmpty()) {
            Toast.makeText(this, "No Data Selected", Toast.LENGTH_LONG).show();
            return;
        } else {
            SourceList.putAll(sourceListResult);
            for (String s : sourceListResult.keySet()) {
                sourceDisplayed.add(s);

            }
            Collections.sort(sourceDisplayed);
            setTitle("News Gateway " + "( " + sourceDetails_ma.size() + " )");
            mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item, sourceDisplayed));

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        /*TextView history = findViewById(R.id.txthistory);
        TextView output = findViewById(R.id.txtoutput);
        outState.putString("HISTORY", history.getText().toString());
        outState.putString("OUTPUT",output.getText().toString());
        outState.putString("TempInput",temp.getText().toString());*/

        outState.putStringArrayList("sourceDisplayed",sourceDisplayed);
        outState.putStringArrayList("topic_list", (ArrayList<String>) topic_list);
        outState.putStringArrayList("lang_list", (ArrayList<String>) lang_list);
        outState.putStringArrayList("country_list", (ArrayList<String>) country_list);
        outState.putBoolean("topic_flag",topic_flag);
        outState.putBoolean("language_flag",language_flag);
        outState.putBoolean("country_flag",country_flag);
        outState.putString("topicSelection",topicSelection);
        outState.putString("languageSelection",languageSelection);
        outState.putString("countrySelection",countrySelection);
        outState.putString("topicsSelected",topicsSelected);
        outState.putString("lannguageSelected",lannguageSelected);
        outState.putString("countrySelected",countrySelected);

        outState.putStringArrayList("copy_topics_local",copy_topics_local);
        outState.putStringArrayList("copy_language_local",copy_language_local);
        outState.putStringArrayList("copy_country_local",copy_country_local);
        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey("sourceDisplayed"))
        {
            sourceDisplayed = savedInstanceState.getStringArrayList("sourceDisplayed");
        }
        if(savedInstanceState.containsKey("topic_list"))
        {
            topic_list = savedInstanceState.getStringArrayList("topic_list");
        }
        if(savedInstanceState.containsKey("lang_list"))
        {
            lang_list = savedInstanceState.getStringArrayList("lang_list");
        }
        if(savedInstanceState.containsKey("country_list"))
        {
            country_list = savedInstanceState.getStringArrayList("country_list");
        }
        if(savedInstanceState.containsKey("copy_topics_local"))
        {
            copy_topics_local = savedInstanceState.getStringArrayList("copy_topics_local");
        }
        if(savedInstanceState.containsKey("copy_language_local"))
        {
            copy_language_local = savedInstanceState.getStringArrayList("copy_language_local");
        }
        if(savedInstanceState.containsKey("copy_country_local"))
        {
            copy_country_local = savedInstanceState.getStringArrayList("copy_country_local");
        }
        if(savedInstanceState.containsKey(countrySelected))
        {
            countrySelected = savedInstanceState.getString("countrySelected");
        }
        if(savedInstanceState.containsKey(lannguageSelected))
        {
            lannguageSelected = savedInstanceState.getString("lannguageSelected");
        }
        if(savedInstanceState.containsKey(topicsSelected))
        {
            topicsSelected = savedInstanceState.getString("topicsSelected");
        }
        if(savedInstanceState.containsKey(countrySelection))
        {
            countrySelection = savedInstanceState.getString("countrySelection");
        }
        if(savedInstanceState.containsKey(languageSelection))
        {
            languageSelection = savedInstanceState.getString("languageSelection");
        }
        if(savedInstanceState.containsKey(topicSelection))
        {
            topicSelection = savedInstanceState.getString("topicSelection");
        }
        if(savedInstanceState.containsKey("topic_flag"))
        {
            topic_flag = savedInstanceState.getBoolean("topic_flag");
        }
        if(savedInstanceState.containsKey("language_flag"))
        {
            language_flag = savedInstanceState.getBoolean("language_flag");
        }
        if(savedInstanceState.containsKey("country_flag"))
        {
            country_flag = savedInstanceState.getBoolean("country_flag");
        }
    }


}
