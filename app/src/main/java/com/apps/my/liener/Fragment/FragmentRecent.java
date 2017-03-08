package com.apps.my.liener.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.my.liener.BubbleService;
import com.apps.my.liener.DbListener;
import com.apps.my.liener.MainActivity;
import com.apps.my.liener.Page;
import com.apps.my.liener.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by rahul on 25/12/16.
 */

public class FragmentRecent extends Fragment {
    View recyclerView;
    String TAG = this.getClass().getSimpleName();
    ArrayList<Page> arrayList;
    MyAdapter myAdapter;
    boolean isHistory;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isHistory = getArguments().getBoolean("isHistory");
        arrayList = MainActivity.mydb.getAllData(isHistory);
        Collections.reverse(arrayList);

        myAdapter = new MyAdapter(arrayList);

        DbListener dbListener = new DbListener() {
            @Override
            public void onDataChanged() {
                sqlDataChanged();
                Log.d(TAG, "onDataChanged() "+ isHistory +"called");
            }

            @Override
            public void onError(Throwable error) {

            }
        };
        if(isHistory){
            MainActivity.mydb.onHistoryChangedListener(dbListener);
        }
        else {
            MainActivity.mydb.onBookmarkChangedListener(dbListener);
        }

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
            public TextView mTitle;
            public TextView mUrl;
            public TextView mTime;


            public ViewHolder(View v) {
                super(v);
                mTitle = (TextView) v.findViewById(R.id.title);
                mUrl = (TextView) v.findViewById(R.id.url);
                mTime = (TextView) v.findViewById(R.id.time);
            }
        }

        public MyAdapter(ArrayList myDataset) {
            mDataset = myDataset;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.link_view, parent, false);
            return (new ViewHolder(v));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Page mPage = new Page(mDataset.get(position).toString());
            holder.mTitle.setText(mPage.getTitle());
            holder.mUrl.setText(mPage.getUrl());
            holder.mTime.setText(getDate(mPage.getTs()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pageIntent = new Intent(getActivity(), BubbleService.class);
                    pageIntent.putExtra("url", mPage.getUrl());
                    pageIntent.putExtra("isRecent",true);
                    getActivity().startService(pageIntent);
                }
            });
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

    private String getDate(String time) {
        if(time==null)
            return "null";
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Log.d(TAG, "getDate() called with: time = [" + time + "]");
        java.util.Date currenTimeZone=new java.util.Date(Long.parseLong(time));
        return  sdf.format(currenTimeZone);
    }

    private void sqlDataChanged(){
        arrayList = MainActivity.mydb.getAllData(isHistory);
        Collections.reverse(arrayList);
        Log.d(TAG, "onDataChanged() dblistener called");
        myAdapter.swap(arrayList);
    }
    public void refreshList(){
        sqlDataChanged();
    }

}


