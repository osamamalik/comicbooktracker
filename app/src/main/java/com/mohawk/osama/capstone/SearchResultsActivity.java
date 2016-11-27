package com.mohawk.osama.capstone;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

public class SearchResultsActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public ArrayList databaseResults = new ArrayList<SearchDataObject>();
    private String title;
    private String year;
    private String publisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        this.setTitle(getString(R.string.searchResults));

        title = getIntent().getExtras().getString("title");
        year = getIntent().getExtras().getString("year");
        publisher = getIntent().getExtras().getString("publisher");

        trustAllCertificates();
        new SendPostRequest().execute();

        mRecyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SearchRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((SearchRecyclerViewAdapter) mAdapter).setOnItemClickListener(new SearchRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                //Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });
    }

    private ArrayList<SearchDataObject> getDataSet() {
        ArrayList results = new ArrayList<SearchDataObject>();
        if (databaseResults.size() == 0) {
            for (int index = 0; index < 20; index++) {
                SearchDataObject obj = new SearchDataObject("Comic Name " + index,
                        "Year " + index, "Publisher " + index, "Issue Cover " + index,
                        "ComicID " + index);
                results.add(index, obj);
            }
        }
        else {
            results.clear();
            for (int i = 0; i < databaseResults.size(); i++) {
                SearchDataObject r = (SearchDataObject) databaseResults.get(i);
                SearchDataObject obj = new SearchDataObject(r.getComicName(),
                        r.getStartYear(), r.getPublisher(), r.getImageURL(), r.getComicID());
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

                URL url = new URL("https://csunix.mohawkcollege.ca/~000307480/capstone/searchComics.php");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("title", title);
                postDataParams.put("year", year);
                postDataParams.put("publisher", publisher);
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
            mAdapter = new SearchRecyclerViewAdapter(getDataSet());
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

    public ArrayList<SearchDataObject> getData(String jsonResult) {
        //ArrayList databaseResults = new ArrayList<SearchDataObject>();

        try {
            JSONArray jsonMainNode = new JSONArray(jsonResult);

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                SearchDataObject obj = new SearchDataObject(jsonChildNode.optString("ComicBookName"),
                        jsonChildNode.optString("StartYear"),
                        jsonChildNode.optString("Publisher"),
                        jsonChildNode.optString("IssueCover"),
                        jsonChildNode.optString("ComicBookID"));
                databaseResults.add(i, obj);
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error" + e.toString(), Toast.LENGTH_SHORT).show();
        }

        return databaseResults;
    }

    public void trustAllCertificates() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            java.security.cert.X509Certificate[] myTrustedAnchors = new java.security.cert.X509Certificate[0];
                            return myTrustedAnchors;
                        }

                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception e) {
        }
    }
}
