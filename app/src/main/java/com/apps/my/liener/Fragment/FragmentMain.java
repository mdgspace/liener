package com.apps.my.liener.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.apps.my.liener.BubbleService;
import com.apps.my.liener.MainActivity;
import com.apps.my.liener.R;

/**
 * Created by rahul on 25/12/16.
 */

public class FragmentMain extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        rootView.findViewById(R.id.button_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent googleIntent = new Intent(getActivity(), BubbleService.class);
                googleIntent.putExtra("url", "http://google.com/");
                getActivity().startService(googleIntent);
            }
        });
        rootView.findViewById(R.id.button_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bubbleServiceIntent = new Intent(getActivity(),BubbleService.class);
                getActivity().stopService(bubbleServiceIntent);
            }
        });
        return rootView;
    }
}
