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

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Osama on 11/16/2016.
 */
public class NewThisMonthRecyclerViewAdapter extends RecyclerView.Adapter<NewThisMonthRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "NewThisMonthRecyclerViewAdapter";
    private ArrayList<NewThisMonthDataObject> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView issueNumber;
        TextView issueName;
        TextView issueDate;
        ImageView coverImage;
        TextView issueID;
        TextView issueDescription;
        TextView issueImageURL;

        public DataObjectHolder(View itemView) {
            super(itemView);
            issueNumber = (TextView) itemView.findViewById(R.id.newIssueNumberTextView);
            issueName = (TextView) itemView.findViewById(R.id.newIssueNameTextView);
            issueDate = (TextView) itemView.findViewById(R.id.newIssueDateTextView);
            coverImage = (ImageView) itemView.findViewById(R.id.newComicCover);
            issueID = (TextView) itemView.findViewById(R.id.newIssueID);
            issueDescription = (TextView) itemView.findViewById(R.id.newIssueDescription);
            issueImageURL = (TextView) itemView.findViewById(R.id.newIssueImageURL);
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

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public NewThisMonthRecyclerViewAdapter(ArrayList<NewThisMonthDataObject> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_this_month_card_view_row, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.issueNumber.setText(mDataset.get(position).getComicName() + " #" + mDataset.get(position).getIssueNumber());
        holder.issueName.setText(mDataset.get(position).getIssueName());
        holder.issueDate.setText(mDataset.get(position).getIssueDate());
        new DownloadImageTask(holder.coverImage).execute(mDataset.get(position).getImageURL());
        holder.issueID.setText(mDataset.get(position).getIssueID());
        holder.issueDescription.setText(mDataset.get(position).getIssueDescription());
        holder.issueImageURL.setText(mDataset.get(position).getImageURL());
    }

    public void addItem(NewThisMonthDataObject dataObj, int index) {
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
}
