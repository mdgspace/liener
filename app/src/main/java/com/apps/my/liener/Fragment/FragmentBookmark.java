package com.apps.my.liener.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apps.my.liener.DbListener;
import com.apps.my.liener.MainActivity;
import com.apps.my.liener.Page;
import com.apps.my.liener.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by rahul on 25/12/16.
 */

public class FragmentBookmark extends Fragment {
    View recyclerView;
    String TAG = this.getClass().getSimpleName();
    ArrayList<Page> arrayList;
    MyAdapter myAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayList = MainActivity.mydb.getAllData(false);

        Collections.reverse(arrayList);

        myAdapter = new MyAdapter(arrayList);

        final FragmentBookmark fragmentBookmark = new FragmentBookmark();

        MainActivity.mydb.onBookmarkChangedListener(new DbListener() {
            @Override
            public void onDataChanged() {
                arrayList = MainActivity.mydb.getAllData(false);
                Collections.reverse(arrayList);
                Log.d(TAG, "onDataChanged() dblistener called");
                myAdapter.swap(arrayList);
            }

            @Override
            public void onError(Throwable error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recent, container, false);

        recyclerView = rootView.findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(myAdapter);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList mDataset;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mTextView;

            public ViewHolder(View v) {
                super(v);
                mTextView = (TextView) v.findViewById(R.id.content);
            }
        }

        public MyAdapter(ArrayList myDataset) {
            mDataset = myDataset;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return (new ViewHolder(v));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(mDataset.get(position).toString());
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public void swap(ArrayList<Page> datas){
            mDataset.clear();
            mDataset.addAll(datas);
            notifyDataSetChanged();
        }
    }

}



