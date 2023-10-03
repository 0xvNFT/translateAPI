package com.dichthuatjun88binh.jun88.activites;


import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.FILE_NAME;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.dichthuatjun88binh.jun88.R;
import com.dichthuatjun88binh.jun88.utils.AppConfig;


public class WelcomeActivity extends AppCompatActivity {

    ImageView trangchu, gioithieu, csbm, hotro;
    Boolean lct;
    String code_country, PACKAGE_NAME,
            Home_link,
            register_link,
            contact_link,
            change_link, non;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Intent intent = getIntent();
        trangchu = findViewById(R.id.trangchu);
        gioithieu = findViewById(R.id.gioithieu);
        csbm = findViewById(R.id.csbm);
        hotro = findViewById(R.id.hotro);

        lct = intent.getBooleanExtra("lct", true);
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        code_country = tm.getNetworkCountryIso();
        non = tm.getNetworkOperatorName().toLowerCase();
        PACKAGE_NAME = getApplicationContext().getPackageName();

        sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);

        Home_link = sharedPreferences.getString("homeURL", "");
        register_link = sharedPreferences.getString("registerURL", "");
        contact_link = sharedPreferences.getString("contactURL", "");


        trangchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lct) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Home_link));
                    startActivity(intent);
                } else {
                    startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                }
            }
        });

        gioithieu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lct) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.INTRO));
                    startActivity(intent);
                } else {

                }
            }
        });
        csbm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lct) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.POLICY));
                    startActivity(intent);
                } else {

                }
            }
        });
        hotro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lct) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contact_link));
                    startActivity(intent);
                } else {
                }
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI(getWindow());
        }
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

}