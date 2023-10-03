package com.dichthuatjun88binh.jun88.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.dichthuatjun88binh.jun88.R;

public class HomeFragment extends Fragment {

    public static HomeFragment fragment;
    ImageView vanban, ghiam, hinhanh, camera;
    Button btnTranslationActivity;

    public static HomeFragment newInstance() {
        if (fragment == null) {
            fragment = new HomeFragment();
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        vanban = view.findViewById(R.id.vanban);
        ghiam = view.findViewById(R.id.ghiam);
        hinhanh = view.findViewById(R.id.anh);
        camera = view.findViewById(R.id.camera);

        return view;
    }

    public void setBtnTranslationActivity(Button btnTranslationActivity) {
        this.btnTranslationActivity = btnTranslationActivity;
    }
}