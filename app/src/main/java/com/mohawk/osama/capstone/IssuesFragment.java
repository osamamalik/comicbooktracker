package com.mohawk.osama.capstone;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

/**
 * Created by Osama on 11/15/2016.
 * This fragment is a tab in the Volume activity, showing a list in the volume
 */
public class IssuesFragment extends Fragment {
    private static String comicID;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    /**
     * The Database results.
     */
    public ArrayList databaseResults = new ArrayList<IssuesDataObject>();
    private String userID;

    /**
     * Instantiates a new Issues fragment.
     */
    public IssuesFragment() {
    }

    /**
     * New instance issues fragment.
     *
     * @param cID the c id
     * @return the issues fragment
     */
    public static IssuesFragment newInstance(String cID) {
        comicID = cID;
        IssuesFragment fragment = new IssuesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_issues, container, false);

        MyApplication myApp = MyApplication.getInstance();
        userID = myApp.getUserID();

        new SendPostRequest().execute();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.issues_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new IssuesRecyclerViewAdapter(getDataSet(), comicID);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((IssuesRecyclerViewAdapter) mAdapter).setOnItemClickListener(new IssuesRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                //Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });
    }

    private ArrayList<IssuesDataObject> getDataSet() {
        ArrayList results = new ArrayList<IssuesDataObject>();
        if (databaseResults.size() == 0) {
            for (int index = 0; index < 1; index++) {
                IssuesDataObject obj = new IssuesDataObject("Loading Issues...", "", "", "", "", "", "", "", 0);
                results.add(index, obj);
            }
        }
        else {
            results.clear();
            for (int i = 0; i < databaseResults.size(); i++) {
                IssuesDataObject r = (IssuesDataObject) databaseResults.get(i);
                IssuesDataObject obj = new IssuesDataObject(r.getImageURL(),
                        r.getIssueNumber(), r.getIssueName(), r.getIssueDate(), r.getIssueDescription(), r.getIssueID(), r.getComicName(), r.getComicID(), r.getIsRead());
                results.add(i, obj);
            }
        }
        return results;
    }

    /**
     * The type Send post request.
     */
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        @Override
        protected String doInBackground(String... arg0) {
            try {

                URL url = new URL("https://csunix.mohawkcollege.ca/~000307480/capstone/getIssues.php");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("comicID", comicID);
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
            mAdapter = new IssuesRecyclerViewAdapter(getDataSet(), comicID);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    /**
     * Gets post data string.
     *
     * @param params the params
     * @return the post data string
     * @throws Exception the exception
     */
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

    /**
     * Gets data.
     *
     * @param jsonResult the json result
     * @return the data
     */
    public ArrayList<IssuesDataObject> getData(String jsonResult) {
        try {
            JSONArray jsonMainNode = new JSONArray(jsonResult);

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                IssuesDataObject obj = new IssuesDataObject(jsonChildNode.optString("IssueCover"),
                        jsonChildNode.optString("IssueNumber"),
                        jsonChildNode.optString("IssueName"),
                        jsonChildNode.optString("ReleaseDate"),
                        jsonChildNode.optString("IssueDescription"),
                        jsonChildNode.optString("IssueID"),
                        jsonChildNode.optString("ComicBookName"),
                        jsonChildNode.optString("ComicBookID"),
                        jsonChildNode.optInt("IsRead"));
                databaseResults.add(i, obj);
            }
        } catch (JSONException e) {
            //Toast.makeText(getApplicationContext(), "Error" + e.toString(), Toast.LENGTH_SHORT).show();
        }

        return databaseResults;
    }
}
