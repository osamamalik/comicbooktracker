package com.mohawk.osama.capstone;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * The type My recycler view adapter.
 */
public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<DataObject> mDataset;
    private static MyClickListener myClickListener;

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
         * Instantiates a new Data object holder.
         *
         * @param itemView the item view
         */
        public DataObjectHolder(View itemView) {
            super(itemView);
            comicName = (TextView) itemView.findViewById(R.id.textView);
            issueName = (TextView) itemView.findViewById(R.id.textView2);
            releaseDate = (TextView) itemView.findViewById(R.id.textView3);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
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
     * Instantiates a new My recycler view adapter.
     *
     * @param myDataset the my dataset
     */
    public MyRecyclerViewAdapter(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.comicName.setText(mDataset.get(position).getmText1());
        holder.issueName.setText(mDataset.get(position).getmText2());
        holder.releaseDate.setText(mDataset.get(position).getmText3());
    }

    /**
     * Add item.
     *
     * @param dataObj the data obj
     * @param index   the index
     */
    public void addItem(DataObject dataObj, int index) {
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
}
