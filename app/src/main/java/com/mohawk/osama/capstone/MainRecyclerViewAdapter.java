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

public class MainRecyclerViewAdapter extends RecyclerView
        .Adapter<MainRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MainRecyclerViewAdapter";
    private ArrayList<MainDataObject> mDataset;
    private static MyClickListener myClickListener;
    String currentComicID;
    String currentIssueID;
    int databaseAction = 0;
    View currentView;
    private String userID;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView comicName;
        TextView issueName;
        TextView releaseDate;
        ImageView coverImage;
        TextView issueID;
        ImageButton readButton;
        ImageButton mImageButton;

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

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

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

    public void addItem(MainDataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
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

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        @Override
        protected String doInBackground(String... arg0) {
            try {
                trustAllCertificates();
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

    private class MainMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int position;
        View v;

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

        public class SendPostRequest extends AsyncTask<String, Void, String> {

            protected void onPreExecute(){}

            @Override
            protected String doInBackground(String... arg0) {
                try {
                    trustAllCertificates();
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
}
