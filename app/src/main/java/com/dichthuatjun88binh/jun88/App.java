package com.dichthuatjun88binh.jun88;

import androidx.multidex.MultiDexApplication;

public class App extends MultiDexApplication {
    private static App sInstance;

    public static App self() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }


}
