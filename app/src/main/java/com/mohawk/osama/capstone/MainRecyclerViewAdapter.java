package com.mohawk.osama.capstone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * The type Main recycler view adapter.
 */
public class MainRecyclerViewAdapter extends RecyclerView
        .Adapter<MainRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MainRecyclerViewAdapter";
    private ArrayList<MainDataObject> mDataset;
    private static MyClickListener myClickListener;
    /**
     * The Current comic id.
     */
    String currentComicID;
    /**
     * The Current issue id.
     */
    String currentIssueID;
    /**
     * The Database action.
     */
    int databaseAction = 0;
    /**
     * The Current view.
     */
    View currentView;
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
         * The Issue name.
         */
        TextView issueName;
        /**
         * The Release date.
         */
        TextView releaseDate;
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
         * The M image button.
         */
        ImageButton mImageButton;

        /**
         * Instantiates a new Data object holder.
         *
         * @param itemView the item view
         */
        public DataObjectHolder(View itemView) {
            super(itemView);
            comicName = (TextView) itemView.findViewById(R.id.mainComicNameTextView);
            issueName = (TextView) itemView.findViewById(R.id.mainIssueNameTextView);
            releaseDate = (TextView) itemView.findViewById(R.id.mainReleaseDateTextView);
            coverImage = (ImageView) itemView.findViewById(R.id.mainComicCover);
            issueID = (TextView) itemView.findViewById(R.id.mainIssueID);
            readButton = (ImageButton) itemView.findViewById(R.id.mainReadButton);
            mImageButton = (ImageButton) itemView.findViewById(R.id.mainMore);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
            Intent intent = new Intent(v.getContext(), IssueInformationActivity.class);
            intent.putExtra("issueID", issueID.getText().toString());
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
     * Instantiates a new Main recycler view adapter.
     *
     * @param myDataset the my dataset
     */
    public MainRecyclerViewAdapter(ArrayList<MainDataObject> myDataset) {
        mDataset = myDataset;

        MyApplication myApp = MyApplication.getInstance();
        userID = myApp.getUserID();
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_card_view_row, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        currentView = view;
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        holder.comicName.setText(mDataset.get(position).getComicName());
        holder.issueName.setText(mDataset.get(position).getIssueName());
        holder.releaseDate.setText(mDataset.get(position).getReleaseDate());
        new DownloadImageTask(holder.coverImage).execute(mDataset.get(position).getImageURL());
        holder.issueID.setText(mDataset.get(position).getIssueID());

        holder.readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIssueID = mDataset.get(position).getIssueID();
                currentComicID = mDataset.get(position).getComicID();
                new SendPostRequest().execute();
            }
        });

        holder.mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentComicID = mDataset.get(position).getComicID();
                showPopupMenu(holder.mImageButton,position);
            }
        });
    }

    private void showPopupMenu(View view,int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(),view );
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.main_popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MainMenuItemClickListener(position, view));
        popup.show();
    }

    /**
     * Add item.
     *
     * @param dataObj the data obj
     * @param index   the index
     */
    public void addItem(MainDataObject dataObj, int index) {
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
                postDataParams.put("comicID", currentComicID);
                postDataParams.put("issueID", currentIssueID);
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
            Intent intent = new Intent(currentView.getContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            currentView.getContext().startActivity(intent);
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

    private class MainMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int position;
        /**
         * The V.
         */
        View v;

        /**
         * Instantiates a new Main menu item click listener.
         *
         * @param position the position
         * @param v        the v
         */
        public MainMenuItemClickListener(int position, View v) {
            this.position = position;
            this.v = v;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {

                case R.id.main_view_comic:
                    Intent intent = new Intent(v.getContext(), VolumeActivity.class);
                    intent.putExtra("comicID", mDataset.get(position).getComicID());
                    v.getContext().startActivity(intent);
                    return true;
                case R.id.main_mark_all_read:
                    databaseAction = 0;
                    new SendPostRequest().execute();
                    deleteItem(position);
                    return true;
                case R.id.main_remove_from_collection:
                    databaseAction = 1;
                    new SendPostRequest().execute();
                    deleteItem(position);
                    return true;
                default:
            }
            return false;
        }

        /**
         * The type Send post request.
         */
        public class SendPostRequest extends AsyncTask<String, Void, String> {

            protected void onPreExecute(){}

            @Override
            protected String doInBackground(String... arg0) {
                try {
                    URL url = new URL("https://csunix.mohawkcollege.ca/~000307480/checkIfInCollection.php");

                    JSONObject postDataParams = new JSONObject();
                    switch (databaseAction) {
                        case 0:
                            url = new URL("https://csunix.mohawkcollege.ca/~000307480/capstone/markAllRead.php");
                            break;
                        case 1:
                            url = new URL("https://csunix.mohawkcollege.ca/~000307480/capstone/removeFromCollection.php");
                        default:
                            break;
                    }

                    postDataParams.put("comicID", currentComicID);
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
                //Intent intent = new Intent(v.getContext(), MainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                //v.getContext().startActivity(intent);

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
}
