package com.mohawk.osama.capstone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
    private ImageButton star1;
    private ImageButton starEmpty1;
    private ImageButton star2;
    private ImageButton starEmpty2;
    private ImageButton star3;
    private ImageButton starEmpty3;
    private ImageButton star4;
    private ImageButton starEmpty4;
    private ImageButton star5;
    private ImageButton starEmpty5;
    private String userID;
    private int userRating;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_information);
        this.setTitle(getIntent().getExtras().getString("comicName"));

        MyApplication myApp = MyApplication.getInstance();
        userID = myApp.getUserID();

        issueID = getIntent().getExtras().getString("issueID");

        issueCover = (ImageView) findViewById(R.id.issueInfoComicCover);
        issueNumber = (TextView) findViewById(R.id.issueInfoIssueNumber);
        issueName = (TextView) findViewById(R.id.issueInfoIssueName);
        releaseDate = (TextView) findViewById(R.id.issueInfoReleaseDate);
        issueDescription = (TextView) findViewById(R.id.issueInfoDescription);

        star1 = (ImageButton) findViewById(R.id.issueInfoStar1);
        star2 = (ImageButton) findViewById(R.id.issueInfoStar2);
        star3 = (ImageButton) findViewById(R.id.issueInfoStar3);
        star4 = (ImageButton) findViewById(R.id.issueInfoStar4);
        star5 = (ImageButton) findViewById(R.id.issueInfoStar5);
        starEmpty1 = (ImageButton) findViewById(R.id.issueInfoStarEmpty1);
        starEmpty2 = (ImageButton) findViewById(R.id.issueInfoStarEmpty2);
        starEmpty3 = (ImageButton) findViewById(R.id.issueInfoStarEmpty3);
        starEmpty4 = (ImageButton) findViewById(R.id.issueInfoStarEmpty4);
        starEmpty5 = (ImageButton) findViewById(R.id.issueInfoStarEmpty5);

        boolean fromVolume = getIntent().getExtras().getBoolean("fromVolume");
        if (fromVolume) {
            new DownloadImageTask(issueCover).execute(getIntent().getExtras().getString("issueCover"));
            issueNumber.setText(getIntent().getExtras().getString("issueNumber"));
            issueName.setText(getIntent().getExtras().getString("issueName"));
            releaseDate.setText(getIntent().getExtras().getString("issueDate"));
            issueDescription.setText(getIntent().getExtras().getString("issueDescription"));
            new SendPostRequest().execute();
        }
        else {
            new SendPostRequest().execute();
        }

        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRating = 1;
                new SetRatingRequest().execute();
            }
        });

        starEmpty1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRating = 1;
                new SetRatingRequest().execute();
            }
        });

        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRating = 2;
                new SetRatingRequest().execute();
            }
        });

        starEmpty2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRating = 2;
                new SetRatingRequest().execute();
            }
        });

        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRating = 3;
                new SetRatingRequest().execute();
            }
        });

        starEmpty3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRating = 3;
                new SetRatingRequest().execute();
            }
        });

        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRating = 4;
                new SetRatingRequest().execute();
            }
        });

        starEmpty4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRating = 4;
                new SetRatingRequest().execute();
            }
        });

        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRating = 5;
                new SetRatingRequest().execute();
            }
        });

        starEmpty5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRating = 5;
                new SetRatingRequest().execute();
            }
        });

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
				/*
				 * The following method, "handleShakeEvent(count):" is a stub //
				 * method you would use to setup whatever you want done once the
				 * device has been shook.
				 */
                handleShakeEvent();
            }
        });
    }

    private void handleShakeEvent() {
        Intent intent = new Intent(IssueInformationActivity.this, ShakeRecommendationActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        @Override
        protected String doInBackground(String... arg0) {
            try {

                URL url = new URL("https://csunix.mohawkcollege.ca/~000307480/capstone/getIssueByID.php");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("issueID", issueID);
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
            IssueDataObject issue = getData(result);
            new DownloadImageTask(issueCover).execute(issue.getImageURL());
            issueNumber.setText("#" + issue.getIssueNumber());
            issueName.setText(issue.getIssueName());
            releaseDate.setText(issue.getIssueDate());
            issueDescription.setText(issue.getIssueDescription());

            switch (issue.getIssueRating()) {
                case 1:
                    star1.setVisibility(View.VISIBLE);
                    star2.setVisibility(View.GONE);
                    star3.setVisibility(View.GONE);
                    star4.setVisibility(View.GONE);
                    star5.setVisibility(View.GONE);
                    starEmpty1.setVisibility(View.GONE);
                    starEmpty2.setVisibility(View.VISIBLE);
                    starEmpty3.setVisibility(View.VISIBLE);
                    starEmpty4.setVisibility(View.VISIBLE);
                    starEmpty5.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    star1.setVisibility(View.VISIBLE);
                    star2.setVisibility(View.VISIBLE);
                    star3.setVisibility(View.GONE);
                    star4.setVisibility(View.GONE);
                    star5.setVisibility(View.GONE);
                    starEmpty1.setVisibility(View.GONE);
                    starEmpty2.setVisibility(View.GONE);
                    starEmpty3.setVisibility(View.VISIBLE);
                    starEmpty4.setVisibility(View.VISIBLE);
                    starEmpty5.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    star1.setVisibility(View.VISIBLE);
                    star2.setVisibility(View.VISIBLE);
                    star3.setVisibility(View.VISIBLE);
                    star4.setVisibility(View.GONE);
                    star5.setVisibility(View.GONE);
                    starEmpty1.setVisibility(View.GONE);
                    starEmpty2.setVisibility(View.GONE);
                    starEmpty3.setVisibility(View.GONE);
                    starEmpty4.setVisibility(View.VISIBLE);
                    starEmpty5.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    star1.setVisibility(View.VISIBLE);
                    star2.setVisibility(View.VISIBLE);
                    star3.setVisibility(View.VISIBLE);
                    star4.setVisibility(View.VISIBLE);
                    star5.setVisibility(View.GONE);
                    starEmpty1.setVisibility(View.GONE);
                    starEmpty2.setVisibility(View.GONE);
                    starEmpty3.setVisibility(View.GONE);
                    starEmpty4.setVisibility(View.GONE);
                    starEmpty5.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    star1.setVisibility(View.VISIBLE);
                    star2.setVisibility(View.VISIBLE);
                    star3.setVisibility(View.VISIBLE);
                    star4.setVisibility(View.VISIBLE);
                    star5.setVisibility(View.VISIBLE);
                    starEmpty1.setVisibility(View.GONE);
                    starEmpty2.setVisibility(View.GONE);
                    starEmpty3.setVisibility(View.GONE);
                    starEmpty4.setVisibility(View.GONE);
                    starEmpty5.setVisibility(View.GONE);
                    break;
                default:
                    star1.setVisibility(View.GONE);
                    star2.setVisibility(View.GONE);
                    star3.setVisibility(View.GONE);
                    star4.setVisibility(View.GONE);
                    star5.setVisibility(View.GONE);
                    starEmpty1.setVisibility(View.VISIBLE);
                    starEmpty2.setVisibility(View.VISIBLE);
                    starEmpty3.setVisibility(View.VISIBLE);
                    starEmpty4.setVisibility(View.VISIBLE);
                    starEmpty5.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    public class SetRatingRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        @Override
        protected String doInBackground(String... arg0) {
            try {

                URL url = new URL("https://csunix.mohawkcollege.ca/~000307480/capstone/setIssueRating.php");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("issueID", issueID);
                postDataParams.put("userID", userID);
                postDataParams.put("userRating", userRating);
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
            switch (userRating) {
                case 1:
                    star1.setVisibility(View.VISIBLE);
                    star2.setVisibility(View.GONE);
                    star3.setVisibility(View.GONE);
                    star4.setVisibility(View.GONE);
                    star5.setVisibility(View.GONE);
                    starEmpty1.setVisibility(View.GONE);
                    starEmpty2.setVisibility(View.VISIBLE);
                    starEmpty3.setVisibility(View.VISIBLE);
                    starEmpty4.setVisibility(View.VISIBLE);
                    starEmpty5.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    star1.setVisibility(View.VISIBLE);
                    star2.setVisibility(View.VISIBLE);
                    star3.setVisibility(View.GONE);
                    star4.setVisibility(View.GONE);
                    star5.setVisibility(View.GONE);
                    starEmpty1.setVisibility(View.GONE);
                    starEmpty2.setVisibility(View.GONE);
                    starEmpty3.setVisibility(View.VISIBLE);
                    starEmpty4.setVisibility(View.VISIBLE);
                    starEmpty5.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    star1.setVisibility(View.VISIBLE);
                    star2.setVisibility(View.VISIBLE);
                    star3.setVisibility(View.VISIBLE);
                    star4.setVisibility(View.GONE);
                    star5.setVisibility(View.GONE);
                    starEmpty1.setVisibility(View.GONE);
                    starEmpty2.setVisibility(View.GONE);
                    starEmpty3.setVisibility(View.GONE);
                    starEmpty4.setVisibility(View.VISIBLE);
                    starEmpty5.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    star1.setVisibility(View.VISIBLE);
                    star2.setVisibility(View.VISIBLE);
                    star3.setVisibility(View.VISIBLE);
                    star4.setVisibility(View.VISIBLE);
                    star5.setVisibility(View.GONE);
                    starEmpty1.setVisibility(View.GONE);
                    starEmpty2.setVisibility(View.GONE);
                    starEmpty3.setVisibility(View.GONE);
                    starEmpty4.setVisibility(View.GONE);
                    starEmpty5.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    star1.setVisibility(View.VISIBLE);
                    star2.setVisibility(View.VISIBLE);
                    star3.setVisibility(View.VISIBLE);
                    star4.setVisibility(View.VISIBLE);
                    star5.setVisibility(View.VISIBLE);
                    starEmpty1.setVisibility(View.GONE);
                    starEmpty2.setVisibility(View.GONE);
                    starEmpty3.setVisibility(View.GONE);
                    starEmpty4.setVisibility(View.GONE);
                    starEmpty5.setVisibility(View.GONE);
                    break;
                default:
                    star1.setVisibility(View.GONE);
                    star2.setVisibility(View.GONE);
                    star3.setVisibility(View.GONE);
                    star4.setVisibility(View.GONE);
                    star5.setVisibility(View.GONE);
                    starEmpty1.setVisibility(View.VISIBLE);
                    starEmpty2.setVisibility(View.VISIBLE);
                    starEmpty3.setVisibility(View.VISIBLE);
                    starEmpty4.setVisibility(View.VISIBLE);
                    starEmpty5.setVisibility(View.VISIBLE);
                    break;
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
}
