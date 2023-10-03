package com.dichthuatjun88binh.jun88.activites;

import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.All_languages;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.flags;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.languages_code;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.speach_code;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.text_recognizer_code;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dichthuatjun88binh.jun88.R;
import com.dichthuatjun88binh.jun88.db.HelperDB;
import com.dichthuatjun88binh.jun88.model.DataBaseModel;
import com.dichthuatjun88binh.jun88.model.LanguageModel;
import com.dichthuatjun88binh.jun88.utils.AppConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectLanguageActivity extends AppCompatActivity {
    public LinearLayout layoutLoadingAds;
    DialogView dialogView;
    Button btnCancel;
    FrameLayout layoutBannerAds;
    EditText search_lang;
    RelativeLayout layoutRecent, layoutAllLanguages;
    RecyclerView lang_rev, lang_recent;
    HelperDB helperDB;
    List<LanguageModel> languages_data = new ArrayList<>();
    List<LanguageModel> search_list1 = new ArrayList<>();
    List<LanguageModel> languages_recent = new ArrayList<>();
    ArrayList<DataBaseModel> dataModels = new ArrayList<>();
    LanguageModel source_data, target_data;
    String languageSelected;
    boolean speakLanguage = false;
    boolean textRecognizerLanguage = false;
    View lineDivider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);

        //Set Full screen (hide Android Navigation)
        hideSystemUI(getWindow());

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(AppConfig.EXTRA_LANGUAGE_SELECTED)) {
                languageSelected = intent.getStringExtra(AppConfig.EXTRA_LANGUAGE_SELECTED);
            }
            if (intent.hasExtra(AppConfig.EXTRA_SPEAK_LANGUAGE)) {
                speakLanguage = intent.getBooleanExtra(AppConfig.EXTRA_SPEAK_LANGUAGE, false);
            }
            if (intent.hasExtra(AppConfig.EXTRA_TEXT_RECOGNIZER_LANGUAGE)) {
                textRecognizerLanguage = intent.getBooleanExtra(AppConfig.EXTRA_TEXT_RECOGNIZER_LANGUAGE, false);
            }
        }

        initView();
        addListener();
        initData();
    }

    private void initView() {
        btnCancel = findViewById(R.id.btnCancel);

        search_lang = findViewById(R.id.search_lang);
        layoutRecent = findViewById(R.id.layoutRecent);
        layoutAllLanguages = findViewById(R.id.layoutAllLanguages);
        lang_rev = findViewById(R.id.lang_rev);

        //Show Recent languages
        lang_recent = findViewById(R.id.lang_recent);
    }

    private void addListener() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        search_lang.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initData() {
        languages_data.clear();
        search_list1.clear();
        for (int i = 0; i < All_languages.length; i++) {
            if (!speakLanguage || speach_code[i].length() > 0) {
                if (!textRecognizerLanguage || text_recognizer_code[i].length() > 0) {
                    LanguageModel language_model = new LanguageModel();
                    language_model.setLanguage_name(All_languages[i]);
                    language_model.setLanguage_flag(flags[i]);
                    language_model.setLanguage_code(languages_code[i]);
                    language_model.setLanguage_speech_code(speach_code[i]);
                    languages_data.add(language_model);
                }
            }
        }
        search_list1.addAll(languages_data);

        lang_rev.setLayoutManager(new LinearLayoutManager(this));
        final LanguageAdapter language_adapter = new LanguageAdapter(this, languages_data);
        language_adapter.setLanguageSelected(languageSelected);
        lang_rev.setAdapter(language_adapter);

        lang_recent.setLayoutManager(new LinearLayoutManager(this));
        final LanguageAdapter recentLanguageAdapter = new LanguageAdapter(this, languages_recent);
        recentLanguageAdapter.setLanguageSelected(languageSelected);
        lang_recent.setAdapter(recentLanguageAdapter);

        languages_recent.clear();
        dataModels.clear();
        helperDB = new HelperDB(this);
        Cursor cr = helperDB.getTransData();
        if (cr.getCount() > 0) {
            while (cr.moveToNext()) {
                DataBaseModel dataModel = new DataBaseModel();
                dataModel.setId(cr.getInt(0));
                dataModel.setSource_language_jun(cr.getString(1));
                dataModel.setSource_language_txt(cr.getString(2));
                dataModel.setTarget_language(cr.getString(3));
                dataModel.setTarget_language_txt(cr.getString(4));
                dataModels.add(dataModel);
            }
            Collections.reverse(dataModels);
            for (int j = 0; j < dataModels.size() && languages_recent.size() < 5; j++) {
                for (int i = 0; i < All_languages.length && languages_recent.size() < 5; i++) {
                    if (All_languages[i].equalsIgnoreCase(dataModels.get(j).getSource_language_jun()) || All_languages[i].equalsIgnoreCase(dataModels.get(j).getTarget_language())) {
                        boolean isExist = false;
                        for (LanguageModel recentLang : languages_recent) {
                            if (All_languages[i].equalsIgnoreCase(recentLang.getLanguage_name())) {
                                isExist = true;
                                break;
                            }
                        }
                        if (!isExist) {
                            if (!speakLanguage || speach_code[i].length() > 0) {
                                if (!textRecognizerLanguage || text_recognizer_code[i].length() > 0) {
                                    LanguageModel languageModel = new LanguageModel();
                                    languageModel.setLanguage_name(All_languages[i]);
                                    languageModel.setLanguage_flag(flags[i]);
                                    languageModel.setLanguage_code(languages_code[i]);
                                    languageModel.setLanguage_speech_code(speach_code[i]);
                                    languages_recent.add(languageModel);
                                }
                            }
                        }
                    }
                }
            }
            cr.close();
        }
        showHideRecentLanguage(search_lang.getText().toString());
    }

    private void showHideRecentLanguage(String str) {
        if (languages_recent.size() == 0 || str.length() > 0) {
            layoutRecent.setVisibility(View.GONE);
            layoutAllLanguages.setVisibility(View.GONE);
            lang_recent.setVisibility(View.GONE);
        } else {
            layoutRecent.setVisibility(View.VISIBLE);
            layoutAllLanguages.setVisibility(View.VISIBLE);
            lang_recent.setVisibility(View.VISIBLE);
        }
    }

    private void filter(String str) {
        showHideRecentLanguage(str);
        languages_data.clear();
        if (str.length() == 0) {
            languages_data.addAll(search_list1);
        } else {
            for (int i = 0; i < search_list1.size(); i++) {
                LanguageModel language_model = search_list1.get(i);
                if (language_model.getLanguage_name().toLowerCase().contains(str.toLowerCase())) {
                    languages_data.add(language_model);
                }
            }
        }
        final LanguageAdapter language_adapter = new LanguageAdapter(this, languages_data);
        language_adapter.setLanguageSelected(languageSelected);
        lang_rev.setAdapter(language_adapter);
    }

    @Override
    protected void onDestroy() {
        CloseKeyboard();
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideSystemUI(getWindow());
    }

    public void hideSystemUI(Window window) { //pass getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController windowsInsets = window.getDecorView().getWindowInsetsController();
            if (windowsInsets != null) {
                windowsInsets.hide(WindowInsets.Type.navigationBars());
            }
        } else {
            View decorView = window.getDecorView();

            int uiVisibility = decorView.getSystemUiVisibility();

            uiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            uiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            uiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            uiVisibility |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
            uiVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            uiVisibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            uiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE;
            uiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            decorView.setSystemUiVisibility(uiVisibility);
        }
    }

    public void CloseKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            View view = this.getCurrentFocus();
            if (view != null) {
                inputMethodManager.hideSoftInputFromWindow(
                        view.getWindowToken(), 0);
            }

        }
    }

    public interface DialogView {
        void getView(View view);
    }

    class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {

        Context context;
        LanguageAdapter.ViewHolder holder;
        LanguageModel language_model;
        String languageSelected = "";
        List<LanguageModel> languagesData;

        LanguageAdapter(Context context, List<LanguageModel> languagesData) {
            this.context = context;
            this.languagesData = languagesData;
        }

        public void setLanguageSelected(String languageSelected) {
            this.languageSelected = languageSelected != null ? languageSelected : "";
        }

        @NonNull
        @Override
        public LanguageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new LanguageAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_item_lang, parent, false));
        }

        @Override
        public void onBindViewHolder(final LanguageAdapter.ViewHolder holder, final int position) {
            this.holder = holder;
            try {
                language_model = languagesData.get(position);
                holder.flag.setImageResource(language_model.getLanguage_flag());
                holder.source_langg.setText(language_model.getLanguage_name());
                holder.source_langg.setTextColor(
                        languageSelected.equalsIgnoreCase(language_model.getLanguage_name()) ?
                                context.getResources().getColor(R.color.colorAccent, null) : context.getResources().getColor(R.color.white, null));
                holder.imgSelected.setVisibility(languageSelected.equalsIgnoreCase(language_model.getLanguage_name()) ? View.VISIBLE : View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, String.valueOf(e), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public int getItemCount() {
            return languagesData.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView source_langg;
            ImageView flag;
            ImageView imgSelected;

            ViewHolder(View v) {
                super(v);
                source_langg = v.findViewById(R.id.name_txt);
                flag = v.findViewById(R.id.img);
                imgSelected = v.findViewById(R.id.imgSelected);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LanguageModel newLanguage = languagesData.get(LanguageAdapter.ViewHolder.this.getAdapterPosition());
                        Intent intent = new Intent(SelectLanguageActivity.this, HomeActivity.class);
                        intent.putExtra(AppConfig.EXTRA_NEW_LANGUAGE_SELECTED, newLanguage.getLanguage_name());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });

            }

        }
    }
}