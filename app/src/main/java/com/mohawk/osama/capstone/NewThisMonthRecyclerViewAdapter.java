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

    /**
     * Sets on item click listener.
     *
     * @param myClickListener the my click listener
     */
    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    /**
     * Instantiates a new New this month recycler view adapter.
     *
     * @param myDataset the my dataset
     */
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

    /**
     * Add item.
     *
     * @param dataObj the data obj
     * @param index   the index
     */
    public void addItem(NewThisMonthDataObject dataObj, int index) {
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
}
