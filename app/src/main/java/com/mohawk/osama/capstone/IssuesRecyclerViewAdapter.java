package com.mohawk.osama.capstone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
 * Created by Osama on 11/15/2016.
 */
public class IssuesRecyclerViewAdapter extends RecyclerView
        .Adapter<IssuesRecyclerViewAdapter
        .DataObjectHolder>  {
    private static String LOG_TAG = "IssuesRecyclerViewAdapter";
    private ArrayList<IssuesDataObject> mDataset;
    private static MyClickListener myClickListener;
    /**
     * The Read issue id.
     */
    String readIssueID;
    /**
     * The Read comic id.
     */
    String readComicID;
    private String userID;

    /**
     * The type Data object holder.
     */
    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        /**
         * The Issue number.
         */
        TextView issueNumber;
        /**
         * The Issue name.
         */
        TextView issueName;
        /**
         * The Issue date.
         */
        TextView issueDate;
        /**
         * The Cover image.
         */
        ImageView coverImage;
        /**
         * The Issue id.
         */
        TextView issueID;
        /**
         * The Read button.
         */
        ImageButton readButton;
        /**
         * The Issue description.
         */
        TextView issueDescription;
        /**
         * The Issue image url.
         */
        TextView issueImageURL;

        /**
         * Instantiates a new Data object holder.
         *
         * @param itemView the item view
         */
        public DataObjectHolder(View itemView) {
            super(itemView);
            issueNumber = (TextView) itemView.findViewById(R.id.issuesIssueNumberTextView);
            issueName = (TextView) itemView.findViewById(R.id.issuesIssueNameTextView);
            issueDate = (TextView) itemView.findViewById(R.id.issuesIssueDateTextView);
            coverImage = (ImageView) itemView.findViewById(R.id.issuesComicCover);
            issueID = (TextView) itemView.findViewById(R.id.issuesIssueID);
            issueDescription = (TextView) itemView.findViewById(R.id.issuesIssueDescription);
            issueImageURL = (TextView) itemView.findViewById(R.id.issuesIssueImageURL);
            this.readButton = (ImageButton) itemView.findViewById(R.id.issuesReadButton);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
            Intent intent = new Intent(v.getContext(), IssueInformationActivity.class);
            intent.putExtra("fromVolume", true);
            intent.putExtra("issueNumber", issueNumber.getText().toString());
            intent.putExtra("issueName", issueName.getText().toString());
            intent.putExtra("issueDate", issueDate.getText().toString());
            intent.putExtra("issueCover", issueImageURL.getText().toString());
            intent.putExtra("issueDescription", issueDescription.getText().toString());
            intent.putExtra("issueID", issueID.getText().toString());
            intent.putExtra("comicName", issueName.getText().toString());
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
     * Instantiates a new Issues recycler view adapter.
     *
     * @param myDataset the my dataset
     * @param cID       the c id
     */
    public IssuesRecyclerViewAdapter(ArrayList<IssuesDataObject> myDataset, String cID) {
        readComicID = cID;
        mDataset = myDataset;

        MyApplication myApp = MyApplication.getInstance();
        userID = myApp.getUserID();
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.issues_card_view_row, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        holder.issueNumber.setText("#" + mDataset.get(position).getIssueNumber());
        holder.issueName.setText(mDataset.get(position).getIssueName());
        holder.issueDate.setText(mDataset.get(position).getIssueDate());
        new DownloadImageTask(holder.coverImage).execute(mDataset.get(position).getImageURL());
        holder.issueID.setText(mDataset.get(position).getIssueID());
        holder.issueDescription.setText(mDataset.get(position).getIssueDescription());
        holder.issueImageURL.setText(mDataset.get(position).getImageURL());

        if (mDataset.get(position).getIsRead() == 1) {
            holder.readButton.setVisibility(View.GONE);
        }

        holder.readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IssuesDataObject sDO = mDataset.get(position);
                readIssueID = sDO.getIssueID();
                new SendPostRequest().execute();
                holder.readButton.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Add item.
     *
     * @param dataObj the data obj
     * @param index   the index
     */
    public void addItem(IssuesDataObject dataObj, int index) {
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

                URL url = new URL("https://csunix.mohawkcollege.ca/~000307480/capstone/setIssueRead.php");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("issueID", readIssueID);
                postDataParams.put("comicID", readComicID);
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

}
