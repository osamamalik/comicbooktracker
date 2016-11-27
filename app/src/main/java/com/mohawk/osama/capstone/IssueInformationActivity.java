package com.mohawk.osama.capstone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class IssueInformationActivity extends AppCompatActivity {

    private String issueID;
    private ImageView issueCover;
    private TextView issueNumber;
    private TextView issueName;
    private TextView releaseDate;
    private TextView issueDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_information);
        this.setTitle(getIntent().getExtras().getString("comicName"));

        issueID = getIntent().getExtras().getString("issueID");

        issueCover = (ImageView) findViewById(R.id.issueInfoComicCover);
        issueNumber = (TextView) findViewById(R.id.issueInfoIssueNumber);
        issueName = (TextView) findViewById(R.id.issueInfoIssueName);
        releaseDate = (TextView) findViewById(R.id.issueInfoReleaseDate);
        issueDescription = (TextView) findViewById(R.id.issueInfoDescription);

        boolean fromVolume = getIntent().getExtras().getBoolean("fromVolume");
        if (fromVolume) {
            new DownloadImageTask(issueCover).execute(getIntent().getExtras().getString("issueCover"));
            issueNumber.setText(getIntent().getExtras().getString("issueNumber"));
            issueName.setText(getIntent().getExtras().getString("issueName"));
            releaseDate.setText(getIntent().getExtras().getString("issueDate"));
            issueDescription.setText(getIntent().getExtras().getString("issueDescription"));
        }
        else {
            trustAllCertificates();
            new SendPostRequest().execute();
        }
    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        @Override
        protected String doInBackground(String... arg0) {
            try {

                URL url = new URL("https://csunix.mohawkcollege.ca/~000307480/capstone/getIssueByID.php");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("issueID", issueID);
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
            IssueDataObject issue = getData(result);
            new DownloadImageTask(issueCover)
                    .execute(issue.getImageURL());
            issueNumber.setText("#" + issue.getIssueNumber());
            issueName.setText(issue.getIssueName());
            releaseDate.setText(issue.getIssueDate());
            issueDescription.setText(issue.getIssueDescription());
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
                    jsonObject.optInt("InCollection"));
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error" + e.toString(), Toast.LENGTH_SHORT).show();
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
