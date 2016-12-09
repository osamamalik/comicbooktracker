package com.mohawk.osama.capstone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * The type Search recycler view adapter.
 */
public class SearchRecyclerViewAdapter extends RecyclerView
        .Adapter<SearchRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "SearchRecyclerViewAdapter";
    private ArrayList<SearchDataObject> mDataset;
    private static MyClickListener myClickListener;
    /**
     * The Added comic id.
     */
    String addedComicID;
    private String userID;

    /**
     * The type Data object holder.
     */
    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        /**
         * The Comic name.
         */
        TextView comicName;
        /**
         * The Year.
         */
        TextView year;
        /**
         * The Publisher.
         */
        TextView publisher;
        /**
         * The Cover image.
         */
        ImageView coverImage;
        /**
         * The Comic id.
         */
        TextView comicID;
        /**
         * The Add button.
         */
        ImageButton addButton;

        /**
         * Instantiates a new Data object holder.
         *
         * @param itemView the item view
         */
        public DataObjectHolder(View itemView) {
            super(itemView);
            comicName = (TextView) itemView.findViewById(R.id.searchComicNameTextView);
            year = (TextView) itemView.findViewById(R.id.searchYearTextView);
            publisher = (TextView) itemView.findViewById(R.id.searchPublisherTextView);
            coverImage = (ImageView) itemView.findViewById(R.id.searchComicCover);
            comicID = (TextView) itemView.findViewById(R.id.searchComicID);
            this.addButton = (ImageButton) itemView.findViewById(R.id.searchAddButton);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
            Intent intent = new Intent(v.getContext(), VolumeActivity.class);
            intent.putExtra("comicID", comicID.getText().toString());
            intent.putExtra("comicName", comicName.getText().toString());
            v.getContext().startActivity(intent);
        }

    }

    /**
     * Sets on item click listener.
     *
     * @param myClickListener the my click listener
     */
    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    /**
     * Instantiates a new Search recycler view adapter.
     *
     * @param myDataset the my dataset
     */
    public SearchRecyclerViewAdapter(ArrayList<SearchDataObject> myDataset) {
        mDataset = myDataset;

        MyApplication myApp = MyApplication.getInstance();
        userID = myApp.getUserID();
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_card_view_row, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        holder.comicName.setText(mDataset.get(position).getComicName());
        holder.year.setText(mDataset.get(position).getStartYear());
        holder.publisher.setText(mDataset.get(position).getPublisher());
        new DownloadImageTask(holder.coverImage).execute(mDataset.get(position).getImageURL());
        holder.comicID.setText(mDataset.get(position).getComicID());

        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDataObject sDO = mDataset.get(position);
                addedComicID = sDO.getComicID();
                new SendPostRequest().execute();
                holder.addButton.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Add item.
     *
     * @param dataObj the data obj
     * @param index   the index
     */
    public void addItem(SearchDataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    /**
     * Delete item.
     *
     * @param index the index
     */
    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /**
     * The interface My click listener.
     */
    public interface MyClickListener {
        /**
         * On item click.
         *
         * @param position the position
         * @param v        the v
         */
        public void onItemClick(int position, View v);
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

    /**
     * The type Send post request.
     */
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        @Override
        protected String doInBackground(String... arg0) {
            try {

                URL url = new URL("https://csunix.mohawkcollege.ca/~000307480/capstone/addToCollection.php");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("comicID", addedComicID);
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

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    /**
     * Gets data.
     *
     * @param jsonResult the json result
     * @return the data
     */
    public int getData(String jsonResult) {
        int inCollection = 0;
        try {
            JSONArray jsonMainNode = new JSONArray(jsonResult);
            JSONObject jsonObject = jsonMainNode.getJSONObject(0);
            inCollection = jsonObject.optInt("InCollection");
        } catch (JSONException e) {
            //Toast.makeText(getApplicationContext(), "Error" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return inCollection;
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
}
