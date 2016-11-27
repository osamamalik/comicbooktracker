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
public class VolumeInformationFragment extends Fragment {
    private static String comicID;
    private ImageView comicCover;
    private TextView comicYear;
    private TextView comicPublisher;
    private TextView comicCount;
    private TextView comicDescription;
    private ImageButton addButton;
    private TextView addLabel;
    private ImageButton removeButton;
    private TextView removeLabel;
    private String userID;
    private boolean addToCollection = false;
    private int isRemove = 0;

    public VolumeInformationFragment() {
    }

    public static VolumeInformationFragment newInstance(String cID) {
        comicID = cID;
        VolumeInformationFragment fragment = new VolumeInformationFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        addToCollection = false;
        View rootView = inflater.inflate(R.layout.fragment_volume_information, container, false);

        comicCover = (ImageView) rootView.findViewById(R.id.volumeInfoComicCover);
        comicYear = (TextView) rootView.findViewById(R.id.volumeInfoYear);
        comicPublisher = (TextView) rootView.findViewById(R.id.volumeInfoPublisher);
        comicCount = (TextView) rootView.findViewById(R.id.volumeInfoIssueCount);
        comicDescription = (TextView) rootView.findViewById(R.id.volumeInfoDescription);

        addButton = (ImageButton) rootView.findViewById(R.id.volumeInfoAddButton);
        addLabel = (TextView) rootView.findViewById(R.id.volumeInfoAddLabel);
        removeButton = (ImageButton) rootView.findViewById(R.id.volumeInfoRemoveButton);
        removeLabel = (TextView) rootView.findViewById(R.id.volumeInfoRemoveLabel);

        MyApplication myApp = MyApplication.getInstance();
        userID = myApp.getUserID();

        trustAllCertificates();
        new SendPostRequest().execute();

        return rootView;
    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        @Override
        protected String doInBackground(String... arg0) {
            try {

                URL url = new URL("https://csunix.mohawkcollege.ca/~000307480/capstone/getVolumeInformation.php");
                JSONObject postDataParams = new JSONObject();
                if (addToCollection) {
                    url = new URL("https://csunix.mohawkcollege.ca/~000307480/capstone/addToCollection.php");
                    postDataParams.put("userID", userID);
                    postDataParams.put("isRemove", isRemove);
                }
                postDataParams.put("comicID", comicID);
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
            if (!addToCollection) {
                VolumeDataObject volume = getData(result);
                new DownloadImageTask(comicCover).execute(volume.getImageURL());
                comicYear.setText(volume.getComicYear());
                comicPublisher.setText(volume.getComicPublisher());
                comicCount.setText(volume.getComicCount() + " Issues");
                comicDescription.setText(volume.getComicDescription());

                if (volume.getInCollection() == 1) {
                    addButton.setVisibility(View.GONE);
                    addLabel.setVisibility(View.GONE);
                    removeButton.setVisibility(View.VISIBLE);
                    removeLabel.setVisibility(View.VISIBLE);
                }
                else {
                    addButton.setVisibility(View.VISIBLE);
                    addLabel.setVisibility(View.VISIBLE);
                    removeButton.setVisibility(View.GONE);
                    removeLabel.setVisibility(View.GONE);
                }

                addButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        addToCollection = true;
                        isRemove = 0;
                        new SendPostRequest().execute();
                        addButton.setVisibility(View.GONE);
                        addLabel.setVisibility(View.GONE);
                        removeButton.setVisibility(View.VISIBLE);
                        removeLabel.setVisibility(View.VISIBLE);
                    }
                });

                removeButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        addToCollection = true;
                        isRemove = 1;
                        new SendPostRequest().execute();
                        addButton.setVisibility(View.VISIBLE);
                        addLabel.setVisibility(View.VISIBLE);
                        removeButton.setVisibility(View.GONE);
                        removeLabel.setVisibility(View.GONE);
                    }
                });
            }
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

    public VolumeDataObject getData(String jsonResult) {
        VolumeDataObject obj = new VolumeDataObject();
        try {
            JSONArray jsonMainNode = new JSONArray(jsonResult);
            JSONObject jsonObject = jsonMainNode.getJSONObject(0);
            obj = new VolumeDataObject(jsonObject.optString("IssueCover"),
                    jsonObject.optString("StartYear"),
                    jsonObject.optString("Publisher"),
                    jsonObject.optString("IssueCount"),
                    jsonObject.optString("ComicBookDescription"),
                    jsonObject.optInt("InCollection"));
        } catch (JSONException e) {
            //Toast.makeText(getApplicationContext(), "Error" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return obj;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
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

    public static void trustAllCertificates() {
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
