package com.dichthuatjun88binh.jun88.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.All_languages;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.FILE_NAME;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.KEY_AD;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.KEY_BIG;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.KEY_Language_Voice_1;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.KEY_Language_Voice_2;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.KEY_SAVE_HISTORY;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.KEY_SOURCE_COLOR;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.KEY_TARGET_COLOR;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.editor;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.enable_history;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.flags;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.full_ad;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.language_voice_1;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.language_voice_2;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.languages_code;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.sharedPreferences;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.showornot;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.source_color;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.speach_code;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.target_color;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.dichthuatjun88binh.jun88.R;
import com.dichthuatjun88binh.jun88.activites.SelectLanguageActivity;
import com.dichthuatjun88binh.jun88.db.HelperDB;
import com.dichthuatjun88binh.jun88.model.LanguageModel;
import com.dichthuatjun88binh.jun88.utils.AppConfig;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class VoiceFragment extends Fragment {

    public static VoiceFragment fragment;
    ImageView drawer_icon, source_flag, target_flag;
    TextView source_name, destination_name, translated_text;
    RelativeLayout source_picker, target_picker;
    ImageView changer;
    EditText source_input;
    Activity activity;
    Context context;
    String selection, mText;
    LanguageModel source_data, target_data;
    int mpostion;
    Editable etext;
    List<LanguageModel> languages_data = new ArrayList<>();
    List<LanguageModel> search_list1 = new ArrayList<>();
    ProgressBar progressBar;
    TextToSpeech textToSpeech;
    String speakMode;
    DrawerLayout drawer;
    HelperDB helperDB;
    ScrollView scrollView;
    FloatingActionButton fabMicro;
    String languageSelected;
    String translatedText = "";
    ActivityResultLauncher<Intent> selectLanguageActivityResultLauncher;

    public static VoiceFragment newInstance() {
        if (fragment == null) {
            fragment = new VoiceFragment();
        }
        return fragment;
    }

    public static void removeInstance() {
        fragment = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectLanguageActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String newLanguage = data.getStringExtra(AppConfig.EXTRA_NEW_LANGUAGE_SELECTED);
                        if (selection.equals("source")) {
                            try {
                                //Fix swap language (when target language same as source language)
                                if (newLanguage.equals(target_data.getLanguage_name())) {
                                    target_data = source_data;
                                    selection = "target";
                                    setData();
                                    for (int i = 0; i < search_list1.size(); i++) {
                                        LanguageModel language_model = search_list1.get(i);
                                        if (language_model.getLanguage_name().equals(target_data.getLanguage_name())) {
                                            editor.putInt(KEY_Language_Voice_2, i);
                                            editor.commit();
                                            break;
                                        }
                                    }
                                    selection = "source";
                                }
                                //End swap language

                                for (int i = 0; i < search_list1.size(); i++) {
                                    LanguageModel language_model = search_list1.get(i);
                                    if (newLanguage.equals(language_model.getLanguage_name())) {
                                        source_data = language_model;
                                        editor.putInt(KEY_Language_Voice_1, i);
                                        editor.commit();
                                        break;
                                    }
                                }
                                setData();
                            } catch (Exception e) {
                                Toast.makeText(context, String.valueOf(e), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            try {
                                //Fix swap language (when target language same as source language)
                                if (newLanguage.equals(source_data.getLanguage_name())) {
                                    source_data = target_data;
                                    selection = "source";
                                    setData();
                                    for (int i = 0; i < search_list1.size(); i++) {
                                        LanguageModel language_model = search_list1.get(i);
                                        if (language_model.getLanguage_name().equals(source_data.getLanguage_name())) {
                                            editor.putInt(KEY_Language_Voice_1, i);
                                            editor.commit();
                                            break;
                                        }
                                    }
                                    selection = "target";
                                }
                                //End swap language

                                for (int i = 0; i < search_list1.size(); i++) {
                                    LanguageModel language_model = search_list1.get(i);
                                    if (newLanguage.equals(language_model.getLanguage_name())) {
                                        target_data = language_model;
                                        editor.putInt(KEY_Language_Voice_2, i);
                                        editor.commit();
                                        break;
                                    }
                                }
                                setData();
                            } catch (Exception e) {
                                Toast.makeText(context, String.valueOf(e), Toast.LENGTH_SHORT).show();
                            }
                        }

                        try {
                            progressBar.setVisibility(View.VISIBLE);
                            DoTranslation();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_voice, container, false);

        helperDB = new HelperDB(context);
        source_flag = view.findViewById(R.id.flag_source);
        target_flag = view.findViewById(R.id.flag_final);

        source_name = view.findViewById(R.id.source_name);
        destination_name = view.findViewById(R.id.target_name);

        source_picker = view.findViewById(R.id.source_picker);
        target_picker = view.findViewById(R.id.target_picker);

        scrollView = view.findViewById(R.id.scrollView);

        source_input = view.findViewById(R.id.source_text);
        translated_text = view.findViewById(R.id.translated_text);
        changer = view.findViewById(R.id.interchange);
        progressBar = view.findViewById(R.id.progress_circular);

        for (int i = 0; i < All_languages.length; i++) {
            LanguageModel language_model = new LanguageModel();
            language_model.setLanguage_name(All_languages[i]);
            language_model.setLanguage_flag(flags[i]);
            language_model.setLanguage_code(languages_code[i]);
            language_model.setLanguage_speech_code(speach_code[i]);
            languages_data.add(language_model);
        }
        sharedPreferences = context.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        language_voice_1 = sharedPreferences.getInt(KEY_Language_Voice_1, language_voice_1);
        language_voice_2 = sharedPreferences.getInt(KEY_Language_Voice_2, language_voice_2);
        if (language_voice_1 != 0) {
            source_data = languages_data.get(language_voice_1);
            if (language_voice_2 != 0) {
                target_data = languages_data.get(language_voice_2);
            } else {
                target_data = languages_data.get(AppConfig.TARGET_INIT);
            }
        } else {
            source_data = languages_data.get(AppConfig.SOURCE_INIT);
            target_data = languages_data.get(AppConfig.TARGET_INIT);
        }
        source_name.setText(source_data.getLanguage_name());
        source_flag.setImageResource(source_data.getLanguage_flag());
        destination_name.setText(target_data.getLanguage_name());
        target_flag.setImageResource(target_data.getLanguage_flag());

        source_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selection = "source";
                languageSelected = source_data.getLanguage_name();
                PickLanguage(languageSelected);
            }
        });

        target_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selection = "target";
                languageSelected = target_data.getLanguage_name();
                PickLanguage(languageSelected);

            }
        });

        changer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LanguageModel temp = source_data;
                source_data = target_data;
                target_data = temp;
                source_name.setText(source_data.getLanguage_name());
                source_flag.setImageResource(source_data.getLanguage_flag());
                destination_name.setText(target_data.getLanguage_name());
                target_flag.setImageResource(target_data.getLanguage_flag());
                full_ad = sharedPreferences.getInt(KEY_BIG, full_ad);
                if (full_ad == 3) {
                    full_ad = 0;
                    editor.putInt(KEY_BIG, 0);
                    editor.commit();
                } else {
                    full_ad = full_ad + 1;
                    editor.putInt(KEY_BIG, full_ad);
                    editor.commit();
                }
            }
        });


        LinearLayout copy = view.findViewById(R.id.copy);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                full_ad = sharedPreferences.getInt(KEY_BIG, full_ad);
                if (full_ad == 3) {

                    full_ad = 0;
                    editor.putInt(KEY_BIG, 0);
                    editor.commit();
                } else {
                    full_ad = full_ad + 1;
                    editor.putInt(KEY_BIG, full_ad);
                    editor.commit();
                }
                if (translated_text.getText().toString().length() != 0) {
                    if (Build.VERSION.SDK_INT < 11) {
                        ((ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE)).setText(translated_text.getText().toString());
                    } else {
                        ((android.content.ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Copy", translated_text.getText().toString()));
                    }
                    Toast.makeText(context, context.getResources().getString(R.string.text_copied_to_clipboard), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(context, context.getResources().getString(R.string.nothing_to_copy), Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayout clear = view.findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textToSpeech != null && textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                }
                if (source_input.getText().toString().length() == 0) {
                    Toast.makeText(context, context.getResources().getString(R.string.nothing_to_delete), Toast.LENGTH_SHORT).show();
                    return;
                }
                source_input.setText("");
                translated_text.setText("");
                translatedText = "";
                Toast.makeText(context, context.getResources().getString(R.string.text_deleted), Toast.LENGTH_SHORT).show();
                full_ad = sharedPreferences.getInt(KEY_BIG, full_ad);
                if (full_ad == 3) {
                    full_ad = 0;
                    editor.putInt(KEY_BIG, 0);
                    editor.commit();
                } else {
                    full_ad = full_ad + 1;
                    editor.putInt(KEY_BIG, full_ad);
                    editor.commit();
                }
            }
        });
        /*Button speechToText = view.findViewById(R.id.SpechToText);
        speechToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (source_data.getLanguage_speech_code() == "") {
                    Toast.makeText(activity, activity.getResources().getString(R.string.speech_to_text_feature_is_not_available_in_selected_language), Toast.LENGTH_SHORT).show();
                } else {
                    ChangeSpeechToText();
                }
            }
        });*/
        fabMicro = view.findViewById(R.id.fabMicro);
        fabMicro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (source_data.getLanguage_speech_code().length() == 0) {
                    Toast.makeText(context, context.getResources().getString(R.string.speech_to_text_feature_is_not_available_in_selected_language), Toast.LENGTH_SHORT).show();
                } else {
                    ChangeSpeechToText();
                }
            }
        });

        ImageView play_img = view.findViewById(R.id.play_source);
        play_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textToSpeech != null && textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                }
                speakMode = "source";
                ChangeTextToSpeech(speakMode);
            }
        });

        ImageView play_target = view.findViewById(R.id.play_target);
        play_target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textToSpeech != null && textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                }
                speakMode = "target";
                ChangeTextToSpeech(speakMode);
            }
        });

        LinearLayout share = view.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String share_text = translated_text.getText().toString();
                if (share_text.isEmpty()) {
                    Toast.makeText(context, context.getResources().getString(R.string.nothing_to_share), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("text/plain");
                    intent.putExtra("android.intent.extra.SUBJECT", "Translated Text");
                    intent.putExtra("android.intent.extra.TEXT", share_text);
                    startActivity(Intent.createChooser(intent, "Share Via"));
                }
            }
        });
        return view;
    }

    private void ChangeSpeechToText() {
        Intent speech_intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        speech_intent.putExtra("android.speech.extra.LANGUAGE_MODEL", source_data.getLanguage_name());
        speech_intent.putExtra("android.speech.extra.LANGUAGE", source_data.getLanguage_speech_code());
        speech_intent.putExtra("android.speech.extra.LANGUAGE_PREFERENCE", source_data.getLanguage_name());
        try {
            startActivityForResult(speech_intent, 1);
            source_input.setText("");
        } catch (ActivityNotFoundException ea) {
            Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    private void ChangeTextToSpeech(String origin) {
        showornot = sharedPreferences.getInt(KEY_AD, showornot);
        if (showornot == 4) {
            showornot = 1;
            editor.putInt(KEY_AD, showornot);
            editor.commit();
        } else {
            showornot = showornot + 1;
            editor.putInt(KEY_AD, showornot);
            editor.commit();
        }
        if (origin.equals("source")) {
            if (!source_input.getText().toString().isEmpty()) {
                textToSpeech = new TextToSpeech(context, this::onInit, "com.google.android.tts");
                onInit(0);

            } else {
                Toast.makeText(context, context.getResources().getString(R.string.nothing_to_speak), Toast.LENGTH_SHORT).show();
            }
        } else if (origin.equals("target")) {
            if (!translated_text.getText().toString().isEmpty()) {
                textToSpeech = new TextToSpeech(context, this::onInit, "com.google.android.tts");
                onInit(0);

            } else {
                Toast.makeText(context, context.getResources().getString(R.string.nothing_to_speak), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean isOnline(Context context) {
        if (context == null) return false;

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                }
            } else {
                try {
                    NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        Log.i("update_statut", "Network is available : true");
                        return true;
                    }
                } catch (Exception e) {
                    Log.i("update_statut", e.getMessage());
                }
            }
        }
        return false;
    }

    public void onInit(int i) {
        if (i == 0) {
            if (speakMode.equals("source")) {
                try {
                    if ("zh".equalsIgnoreCase(source_data.getLanguage_speech_code())) {
                        textToSpeech.setLanguage(Locale.SIMPLIFIED_CHINESE);
                    } else if ("zh-TW".equalsIgnoreCase(source_data.getLanguage_speech_code())) {
                        textToSpeech.setLanguage(Locale.TRADITIONAL_CHINESE);
                    } else {
                        textToSpeech.setLanguage(new Locale(source_data.getLanguage_speech_code()));
                    }
                    textToSpeech.speak(source_input.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
                } catch (Exception e) {
                    Toast.makeText(context, context.getResources().getString(R.string.text_to_speech_feature_is_not_available_in_target_language), Toast.LENGTH_SHORT).show();
                }
            } else {
                try {
                    if ("zh".equalsIgnoreCase(target_data.getLanguage_speech_code())) {
                        textToSpeech.setLanguage(Locale.SIMPLIFIED_CHINESE);
                    } else if ("zh-TW".equalsIgnoreCase(target_data.getLanguage_speech_code())) {
                        textToSpeech.setLanguage(Locale.TRADITIONAL_CHINESE);
                    } else {
                        textToSpeech.setLanguage(new Locale(target_data.getLanguage_speech_code()));
                    }
                    textToSpeech.speak(translated_text.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
                } catch (Exception e) {
                    Toast.makeText(context, context.getResources().getString(R.string.text_to_speech_feature_is_not_available_in_target_language), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    public String readJSON(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            HttpResponse mResponse = new DefaultHttpClient().execute(new HttpGet(URL));
            if (mResponse != null) {
                if (mResponse.getStatusLine().getStatusCode() == 200) {
                    InputStream inputStream = mResponse.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    while (true) {
                        String line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        stringBuilder.append(line);
                    }
                    inputStream.close();
                } else {
                    Toast.makeText(context, "Some thing went wrong.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("JSON", "Failed to download file");
            }
        } catch (Exception e) {
            Log.d("readJSON", e.getLocalizedMessage());
            stringBuilder.append("[\"ERROR\"]");
        }
        return stringBuilder.toString();
    }

    public void DoTranslation() throws UnsupportedEncodingException {
        showornot = sharedPreferences.getInt(KEY_AD, showornot);
        if (showornot == 4) {
            showornot = 1;
            editor.putInt(KEY_AD, showornot);
            editor.commit();
            //ShowLargAdd();
        } else {
            showornot = showornot + 1;
            editor.putInt(KEY_AD, showornot);
            editor.commit();
        }
        mText = URLEncoder.encode(source_input.getText().toString(), StandardCharsets.UTF_8);
        new ReadLanguageTask().execute("https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + source_data.getLanguage_code() + "&tl=" + target_data.getLanguage_code() + "&dt=t&ie=UTF-8&oe=UTF-8&q=" + mText);
    }

    private void CloseKeyboard() {
        ((InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(source_input.getWindowToken(), 0);
    }

    private void OpenKeyBoard() {
        InputMethodManager keyboard = (InputMethodManager)
                context.getSystemService(INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(source_input, 0);
    }

    private void PickLanguage(String languageSelected) {
        languages_data.clear();
        search_list1.clear();
        for (int i = 0; i < All_languages.length; i++) {
            if (speach_code[i].length() > 0) {
                LanguageModel language_model = new LanguageModel();
                language_model.setLanguage_name(All_languages[i]);
                language_model.setLanguage_flag(flags[i]);
                language_model.setLanguage_code(languages_code[i]);
                language_model.setLanguage_speech_code(speach_code[i]);
                languages_data.add(language_model);
            }

        }
        search_list1.addAll(languages_data);
        Intent intent = new Intent(activity, SelectLanguageActivity.class);
        intent.putExtra(AppConfig.EXTRA_LANGUAGE_SELECTED, languageSelected);
        intent.putExtra(AppConfig.EXTRA_SPEAK_LANGUAGE, true);
        if (selectLanguageActivityResultLauncher != null) {
            selectLanguageActivityResultLauncher.launch(intent);
        }
    }

    @Override
    public void onPause() {
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPREFRENCE();
        if (translatedText.length() > 0) {
            translated_text.setText(translatedText);
        }

    }

    public void getPREFRENCE() {
        sharedPreferences = context.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        source_color = sharedPreferences.getInt(KEY_SOURCE_COLOR, source_color);
        target_color = sharedPreferences.getInt(KEY_TARGET_COLOR, target_color);
        enable_history = sharedPreferences.getString(KEY_SAVE_HISTORY, enable_history);

        if (source_color != 0) {
            source_input.setTextColor(source_color);
        } else {
            source_color = context.getResources().getColor(R.color.colorAccent, null);
            source_input.setTextColor(context.getResources().getColor(R.color.colorAccent, null));
        }
        if (target_color != 0) {
            translated_text.setTextColor(target_color);
        } else {
            target_color = Color.WHITE;
            translated_text.setTextColor(Color.WHITE);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 101: {

            }
            case 1: {
                if (resultCode == RESULT_OK && data != null) {
                    source_input.setText((CharSequence) data.getStringArrayListExtra("android.speech.extra.RESULTS").get(0));
                    mpostion = source_input.length();
                    etext = source_input.getText();
                    Selection.setSelection(etext, mpostion);
                    try {
                        progressBar.setVisibility(View.VISIBLE);
                        DoTranslation();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }

    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    public void setData() {
        if (selection.equals("source")) {
            source_name.setText(source_data.getLanguage_name());
            source_flag.setImageResource(source_data.getLanguage_flag());
        } else {
            destination_name.setText(target_data.getLanguage_name());
            target_flag.setImageResource(target_data.getLanguage_flag());
        }
    }

    private class ReadLanguageTask extends AsyncTask<String, Void, String> {
        private ReadLanguageTask() {
        }

        protected String doInBackground(String... urls) {
            return readJSON(urls[0]);
        }

        protected void onPostExecute(String result) {
            if (result.equals("[\"ERROR\"]")) {
                //Toast.makeText(activity, activity.getResources().getString(C0761R.string.connectionfail), 1).show();
                return;
            }
            try {
                JSONArray jsonarray = new JSONArray(result);
                String str = "";
                for (int i = 0; i < jsonarray.getJSONArray(0).length(); i++) {
                    str = str + jsonarray.getJSONArray(0).getJSONArray(i).getString(0);
                }
                translated_text.setText(str);
                translatedText = str;
                progressBar.setVisibility(View.GONE);
                enable_history = sharedPreferences.getString(KEY_SAVE_HISTORY, enable_history);
                if (enable_history.equals("") || enable_history == null || enable_history.equals("1")) {
                    helperDB.InsertRecord("Translation_Data", source_data.getLanguage_name(), source_input.getText().toString(), target_data.getLanguage_name(), str);
                }
            } catch (Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

}