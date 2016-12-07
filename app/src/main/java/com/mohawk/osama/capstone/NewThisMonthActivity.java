package com.mohawk.osama.capstone;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class NewThisMonthActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "NewThisMonthActivity";
    private String jsonResult;
    private String userID;

    public ArrayList databaseResults = new ArrayList<NewThisMonthDataObject>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_this_month);

        MyApplication myApp = MyApplication.getInstance();
        userID = myApp.getUserID();

        new SendPostRequest().execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New This Month");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.new_this_month_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new NewThisMonthRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);



    }

    @Override
    protected void onResume() {
        super.onResume();
        ((NewThisMonthRecyclerViewAdapter) mAdapter).setOnItemClickListener(new NewThisMonthRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_collection) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_new_this_month) {
            // do nothing
        } else if (id == R.id.nav_recommendations) {
            Intent intent = new Intent(this, RecommendedActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private ArrayList<NewThisMonthDataObject> getDataSet() {
        ArrayList results = new ArrayList<NewThisMonthDataObject>();
        if (databaseResults.size() == 0) {
            for (int index = 0; index < 0; index++) {
                NewThisMonthDataObject obj = new NewThisMonthDataObject("Loading Issues...", "", "", "", "", "", "");
                results.add(index, obj);
            }
        }
        else {
            results.clear();
            for (int i = 0; i < databaseResults.size(); i++) {
                NewThisMonthDataObject r = (NewThisMonthDataObject) databaseResults.get(i);
                NewThisMonthDataObject obj = new NewThisMonthDataObject(r.getImageURL(),
                        r.getIssueNumber(), r.getIssueName(), r.getIssueDate(), r.getIssueDescription(), r.getIssueID(), r.getComicName());
                results.add(i, obj);
            }
        }
        return results;
    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        @Override
        protected String doInBackground(String... arg0) {
            try {

                URL url = new URL("https://csunix.mohawkcollege.ca/~000307480/capstone/getNewThisMonth.php");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("userID", userID);
                Log.e("params", postDataParams.toString());

                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            getData(result);
            mAdapter = new NewThisMonthRecyclerViewAdapter(getDataSet());
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

    public ArrayList<NewThisMonthDataObject> getData(String jsonResult) {
        try {
            JSONArray jsonMainNode = new JSONArray(jsonResult);

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                NewThisMonthDataObject obj = new NewThisMonthDataObject(jsonChildNode.optString("IssueCover"),
                        jsonChildNode.optString("IssueNumber"),
                        jsonChildNode.optString("IssueName"),
                        jsonChildNode.optString("ReleaseDate"),
                        jsonChildNode.optString("IssueDescription"),
                        jsonChildNode.optString("IssueID"),
                        jsonChildNode.optString("ComicBookName"));
                databaseResults.add(i, obj);
            }
        } catch (JSONException e) {
            //Toast.makeText(getApplicationContext(), "Error" + e.toString(), Toast.LENGTH_SHORT).show();
        }

        return databaseResults;
    }
}
