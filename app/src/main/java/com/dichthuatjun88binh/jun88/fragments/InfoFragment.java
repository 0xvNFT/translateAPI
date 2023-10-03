package com.dichthuatjun88binh.jun88.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.dichthuatjun88binh.jun88.R;

public class InfoFragment extends Fragment {

    public static InfoFragment fragment;
    ImageView gioithieu, csbm, hotro;

    public static InfoFragment newInstance() {
        if (fragment == null) {
            fragment = new InfoFragment();
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        gioithieu = view.findViewById(R.id.gioithieu);
        csbm = view.findViewById(R.id.csbm);
        hotro = view.findViewById(R.id.hotro);

        return view;
    }
}