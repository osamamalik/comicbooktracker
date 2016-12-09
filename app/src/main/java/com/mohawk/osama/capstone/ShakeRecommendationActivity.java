package com.mohawk.osama.capstone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * The type Shake recommendation activity.
 */
public class ShakeRecommendationActivity extends AppCompatActivity {
    private String userID;
    private int comicID;
    private ImageView comicCover;
    private TextView comicYear;
    private TextView comicPublisher;
    private TextView comicCount;
    private TextView comicDescription;
    private ImageButton addButton;
    private TextView addLabel;
    private ImageButton removeButton;
    private TextView removeLabel;
    private boolean addToCollection = false;
    private int isRemove = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_recommendation);

        MyApplication myApp = MyApplication.getInstance();
        userID = myApp.getUserID();

        addToCollection = false;

        comicCover = (ImageView) findViewById(R.id.shakeComicCover);
        comicYear = (TextView) findViewById(R.id.shakeYear);
        comicPublisher = (TextView) findViewById(R.id.shakePublisher);
        comicCount = (TextView) findViewById(R.id.shakeIssueCount);
        comicDescription = (TextView) findViewById(R.id.shakeDescription);

        addButton = (ImageButton) findViewById(R.id.shakeAddButton);
        addLabel = (TextView) findViewById(R.id.shakeAddLabel);
        removeButton = (ImageButton) findViewById(R.id.shakeRemoveButton);
        removeLabel = (TextView) findViewById(R.id.shakeRemoveLabel);

        new SendPostRequest().execute();


    }

    /**
     * The type Send post request.
     */
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        @Override
        protected String doInBackground(String... arg0) {
            try {

                URL url = new URL("https://csunix.mohawkcollege.ca/~000307480/capstone/getRandomRecommendation.php");
                JSONObject postDataParams = new JSONObject();
                if (addToCollection) {
                    url = new URL("https://csunix.mohawkcollege.ca/~000307480/capstone/addToCollection.php");
                    postDataParams.put("isRemove", isRemove);
                }
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
            if (!addToCollection) {
                VolumeDataObject volume = getData(result);
                comicID = volume.getComicID();

                getSupportActionBar().setTitle(volume.getComicName());

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

    /**
     * Gets data.
     *
     * @param jsonResult the json result
     * @return the data
     */
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
                    jsonObject.optInt("InCollection"),
                    jsonObject.optInt("ComicBookRating"),
                    jsonObject.optInt("ComicBookID"),
                    jsonObject.optString("ComicBookName"));
        } catch (JSONException e) {
            //Toast.makeText(getApplicationContext(), "Error" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return obj;
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
