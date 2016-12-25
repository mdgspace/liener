package com.apps.my.liener.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apps.my.liener.R;

/**
 * Created by rahul on 25/12/16.
 */

public class FragmentMain extends Fragment{

    //TODO Complete app description here
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }
}
