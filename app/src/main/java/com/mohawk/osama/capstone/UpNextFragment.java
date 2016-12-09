package com.mohawk.osama.capstone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Osama on 11/15/2016.
 */
public class UpNextFragment extends Fragment {
    private static String comicID;
    private static String iID;
    private ImageView issueCover;
    private TextView issueNumber;
    private TextView issueName;
    private TextView issueDate;
    private TextView issueDescription;
    private TextView issueID;
    private ImageButton readButton;
    private TextView readLabel;
    private String userID;
    private boolean markRead = false;

    /**
     * Instantiates a new Up next fragment.
     */
    public UpNextFragment() {
    }

    /**
     * New instance up next fragment.
     *
     * @param cID the comic id
     * @return the up next fragment
     */
    public static UpNextFragment newInstance(String cID) {
        comicID = cID;
        UpNextFragment fragment = new UpNextFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        markRead = false;
        View rootView = inflater.inflate(R.layout.fragment_up_next, container, false);

        issueCover = (ImageView) rootView.findViewById(R.id.upNextComicCover);
        issueNumber = (TextView) rootView.findViewById(R.id.upNextIssueNumber);
        issueName = (TextView) rootView.findViewById(R.id.upNextIssueName);
        issueDate = (TextView) rootView.findViewById(R.id.upNextIssueDate);
        issueDescription = (TextView) rootView.findViewById(R.id.upNextDescription);
        issueID = (TextView) rootView.findViewById(R.id.upNextIssueID);
        readLabel = (TextView) rootView.findViewById(R.id.upNextReadLabel);

        readButton = (ImageButton) rootView.findViewById(R.id.upNextReadButton);

        MyApplication myApp = MyApplication.getInstance();
        userID = myApp.getUserID();

        new SendPostRequest().execute();

        return rootView;
    }

    /**
     * The type Send post request.
     */
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        @Override
        protected String doInBackground(String... arg0) {
            try {

                URL url = new URL("https://csunix.mohawkcollege.ca/~000307480/capstone/getIssueInformation.php");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("comicID", comicID);
                postDataParams.put("userID", userID);

                if (markRead) {
                    url = new URL("https://csunix.mohawkcollege.ca/~000307480/capstone/setIssueRead.php");
                    postDataParams.put("issueID", iID);
                }
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
            if (!result.equals("null")) {
                if (!markRead) {
                    IssueDataObject upNext = getData(result);
                    new DownloadImageTask(issueCover).execute(upNext.getImageURL());
                    issueNumber.setText("#" + upNext.getIssueNumber());
                    issueName.setText(upNext.getIssueName());
                    issueDate.setText(upNext.getIssueDate());
                    issueDescription.setText(upNext.getIssueDescription());
                    issueID.setText(upNext.getIssueID());

                    if (upNext.getInCollection() == 0) {
                        readButton.setVisibility(View.GONE);
                        readLabel.setVisibility(View.GONE);
                    }


                    readButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            markRead = true;
                            iID = issueID.getText().toString();
                            new SendPostRequest().execute();
                            readButton.setVisibility(View.GONE);
                            readLabel.setVisibility(View.GONE);
                        }
                    });
                }
            }
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
    public IssueDataObject getData(String jsonResult) {
        IssueDataObject obj = new IssueDataObject();
        try {
            JSONArray jsonMainNode = new JSONArray(jsonResult);
            JSONObject jsonObject = jsonMainNode.getJSONObject(0);
            obj = new IssueDataObject(jsonObject.optString("IssueCover"),
                    jsonObject.optString("IssueNumber"),
                    jsonObject.optString("IssueName"),
                    jsonObject.optString("ReleaseDate"),
                    jsonObject.optString("IssueDescription"),
                    jsonObject.optString("IssueID"),
                    jsonObject.optInt("InCollection"),
                    jsonObject.optInt("IssueRating"));
        } catch (JSONException e) {
            //Toast.makeText(getApplicationContext(), "Error" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return obj;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        /**
         * The Bm image.
         */
        ImageView bmImage;

        /**
         * Instantiates a new Download image task.
         *
         * @param bmImage the bm image
         */
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
