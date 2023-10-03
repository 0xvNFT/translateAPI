package com.dichthuatjun88binh.jun88.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dichthuatjun88binh.jun88.R;
import com.dichthuatjun88binh.jun88.db.HelperDB;
import com.dichthuatjun88binh.jun88.model.DataBaseModel;
import com.dichthuatjun88binh.jun88.utils.ItemListener;
import com.dichthuatjun88binh.jun88.utils.TranslatorAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryFragment extends Fragment {

    public static HistoryFragment fragment;
    Activity activity;
    Context context;
    ImageButton btnMenu;
    TextView empty;
    RecyclerView histortrev;
    HelperDB helperDB;
    TranslatorAdapter translatorAdapter;
    ArrayList<DataBaseModel> data_models = new ArrayList<>();
    int count = 0;
    DataBaseModel data_model;
    ProgressBar progressLoading;
    DeleteHistoryDialog deleteHistoryDialog;
    View lineDivider;

    public static HistoryFragment newInstance() {
        if (fragment == null) {
            fragment = new HistoryFragment();
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);
        empty = view.findViewById(R.id.emptytext);
        progressLoading = view.findViewById(R.id.progressLoading);

        histortrev = view.findViewById(R.id.datarev);
        histortrev.setLayoutManager(new LinearLayoutManager(getActivity()));
        helperDB = new HelperDB(context);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupPopupMenu(view);
    }

    private void setupPopupMenu(View view) {
        btnMenu = view.findViewById(R.id.menu_button);
        PopupMenu popupMenu = new PopupMenu(context, btnMenu);
//        popupMenu.getMenuInflater().inflate(R.menu.menu_history, popupMenu.getMenu());
        popupMenu.inflate(R.menu.menu_history);
        popupMenu.setForceShowIcon(true);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        if (data_models.size() > 0) {
                            deleteHistoryDialog = new DeleteHistoryDialog();
                            deleteHistoryDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "");
                            deleteHistoryDialog.setOnClickListenerDeleteModeDialog(new DeleteHistoryDialog.OnClickListenerDeleteModeDialog() {
                                @Override
                                public void OnOk() {
                                    helperDB.delAll();
                                    histortrev.setVisibility(View.GONE);
                                    empty.setVisibility(View.VISIBLE);
//                                    updateBannerAd();
                                }
                            });
                        } else {
                            Toast.makeText(context, context.getString(R.string.nothing_to_delete), Toast.LENGTH_SHORT).show();
                        }

                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();

            }
        });
    }

    @Override
    public void onPause() {
        if (deleteHistoryDialog != null && deleteHistoryDialog.isResumed()) {
            deleteHistoryDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }

    private void reloadData() {
        progressLoading.setVisibility(View.VISIBLE);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                data_models.clear();
                count = checkData();
                if (count == 0) {
                    empty.setVisibility(View.VISIBLE);
                    histortrev.setVisibility(View.GONE);
                } else {
                    empty.setVisibility(View.GONE);
                    Cursor cr = helperDB.getTransData();
                    while (cr.moveToNext()) {
                        data_model = new DataBaseModel();
                        data_model.setId(cr.getInt(0));
                        data_model.setSource_language_jun(cr.getString(1));
                        data_model.setSource_language_txt(cr.getString(2));
                        data_model.setTarget_language(cr.getString(3));
                        data_model.setTarget_language_txt(cr.getString(4));
                        data_models.add(data_model);
                    }
                    cr.close();
                    helperDB.close();
                    Collections.reverse(data_models);
                    translatorAdapter = new TranslatorAdapter(context, data_models);
                    histortrev.setAdapter(translatorAdapter);
                    histortrev.setVisibility(View.VISIBLE);
                    histortrev.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (translatorAdapter.positionSwipe >= 0) {
                                translatorAdapter.viewBinderHelper.closeLayout(String.valueOf(translatorAdapter.positionSwipe));
                                translatorAdapter.positionSwipe = -1;
                            }
                        }
                    });
                    translatorAdapter.setItemListener(new ItemListener() {
                        @Override
                        public void onDelete(int position) {
                            helperDB = new HelperDB(context);
                            int a = helperDB.delTrans(String.valueOf(data_models.get(position).getId()));
                            if (a == 1) {
                                Toast.makeText(context, context.getString(R.string.translation_item_successfully_deleted), Toast.LENGTH_SHORT).show();
                                reloadData();
                            }
                        }
                    });
                }
                progressLoading.setVisibility(View.GONE);

            }
        });
    }


    public int checkData() {
        HelperDB dbHelper = new HelperDB(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT * FROM Translation_Data";
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }
}