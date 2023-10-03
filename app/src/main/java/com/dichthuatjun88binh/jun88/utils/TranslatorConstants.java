package com.dichthuatjun88binh.jun88.utils;

import android.content.SharedPreferences;

import com.dichthuatjun88binh.jun88.R;

public class TranslatorConstants {

    public static int lnaguage1 = 0;
    public static int language2 = 0;

    public static int language_voice_1 = 0;
    public static int language_voice_2 = 0;

    public static int language_conversation_1 = 0;
    public static int language_conversation_2 = 0;

    public static int language_image_1 = 0;
    public static int language_image_2 = 0;

    public static int language_screenshot_1 = 0;
    public static int language_screenshot_2 = 0;
    public static int showornot = 0;
    public static int full_ad = 0;


    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    public static String FILE_NAME = "com.dichthuatjun88binh.jun88.phototranslator.translation";

    public static String KEY_AD = "add";
    public static String KEY_BIG = "BGG";

    public static String KEY_Language_1 = "l11";
    public static String KEY_Language_2 = "l22";
    public static String KEY_Language_Voice_1 = "lv11";
    public static String KEY_Language_Voice_2 = "lv22";
    public static String KEY_Language_Conversation_1 = "lc11";
    public static String KEY_Language_Conversation_2 = "lc22";

    public static String KEY_Language_Image_1 = "li11";
    public static String KEY_Language_Image_2 = "li22";

    public static String KEY_Language_Screenshot_1 = "ls11";
    public static String KEY_Language_Screenshot_2 = "ls22";

    public static String KEY_AD_Source = "ad_source";
    public static int AD_SOURCE = 0;// admob Ad
    public static String KEY_OPEN_COUNT = "count";
    public static int App_Count = 0;
    public static int source_color = 0;
    public static int target_color = 0;

    public static String source_l;
    public static String source_t;
    public static String target_l;
    public static String target_t;

    public static int position;

    public static String KEY_SOURCE_COLOR = "source color";
    public static String KEY_TARGET_COLOR = "target color";
    public static String KEY_SAVE_HISTORY = "save history";
    public static String KEY_SAVE_LANGUAGE_NAME_DEFAULT = "save_language_name_default";
    public static String KEY_SAVE_LANGUAGE_CODE_DEFAULT = "save_language_code_default";
    public static String enable_history = "";

    public static int[] flags = new int[]{
            R.drawable.flag_zh,
            R.drawable.flag_en,
            R.drawable.flag_de,
            R.drawable.flag_hi,
            R.drawable.flag_ja,
            R.drawable.flag_tr,
            R.drawable.flag_vi,
            R.drawable.flag_yi,
            R.drawable.flag_yo,
            R.drawable.flag_zu,};
    public static String[] All_languages = new String[]{
            "Chinese ,Simplified",
            "English",
            "German",
            "Hindi",
            "Japanese",
            "Kurdish ,Kurmanji",
            "Vietnamese",
            "Yiddish",
            "Yoruba",
            "Zulu",
    };

    public static String[] languages_code = new String[]{
            "zh",
            "en",
            "de",
            "hi",
            "ja",
            "ku",
            "vi",
            "yi",
            "yo",
            "zu"};
    public static String[] speach_code = new String[]{
            "zh",
            "en-GB",
            "de-DE",
            "hi-IN",
            "ja-JP",
            "",
            "vi-VN",
            "",
            "",
            "zu-ZA",
    };

    public static String[] text_recognizer_code = new String[]{
            "zh",
            "en",
            "de",
            "hi",
            "ja",
            "ko",
            "",
            "vi",
            "",
            "",
            ""};
}