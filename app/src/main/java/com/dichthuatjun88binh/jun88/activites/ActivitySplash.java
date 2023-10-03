package com.dichthuatjun88binh.jun88.activites;

import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.FILE_NAME;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.dichthuatjun88binh.jun88.R;
import com.dichthuatjun88binh.jun88.utils.requestApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class ActivitySplash extends AppCompatActivity {
    Handler handler;
    boolean lcthome;
    String mv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        Intent intent = getIntent();
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        mv = tm.getNetworkOperatorName().toLowerCase();
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String firstRun = sharedPreferences.getString("firstRun", "0");
        lcthome = sharedPreferences.getBoolean("lct", true);
        if (!isEmulator() && firstRun.equals("1")) {
            Intent intentwellcome = new Intent(ActivitySplash.this, HomeActivity.class);
            startActivity(intentwellcome);
        }

        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                final Dialog dialog = new Dialog(ActivitySplash.this);
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.setCancelable(false);
//                dialog.setContentView(R.layout.dialog_phone);
//                EditText editText = dialog.findViewById(R.id.input_phone_number);
//                TextView text = (TextView) dialog.findViewById(R.id.nhanthuong);
//
//                text.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String phoneNumbe = String.valueOf(editText.getText());
//
//                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//
//                        if (phoneNumbe.equals("") || phoneNumbe.length() != 10  ){
//                            Toast.makeText(ActivitySplash.this, "Sphone number error", Toast.LENGTH_SHORT).show();
//                        }else {
//                            PhoneTask PhoneTask = new PhoneTask(phoneNumbe);
//                            PhoneTask.execute("");
//                            text.setText("Loading...");
//                        }
//                        dialog.dismiss();
//                    }
//                });
//
//                dialog.show();
                Intent intentwellcome = new Intent(ActivitySplash.this, HomeActivity.class);
                startActivity(intentwellcome);
            }
        }, 3000);
    }


    public boolean isEmulator() {
        try {
            String buildDetails = (Build.FINGERPRINT + Build.DEVICE + Build.MODEL + Build.BRAND + Build.PRODUCT + Build.MANUFACTURER + Build.HARDWARE).toLowerCase();

            if (buildDetails.contains("generic")
                    || buildDetails.contains("unknown")
                    || buildDetails.contains("emulator")
                    || buildDetails.contains("sdk")
                    || buildDetails.contains("genymotion")
                    || buildDetails.contains("x86") // this includes vbox86
                    || buildDetails.contains("goldfish")
                    || buildDetails.contains("test-keys"))
                return true;
        } catch (Throwable t) {
        }

        try {

            if (mv.equals("android"))
                return true;
        } catch (Throwable t) {
        }

        try {
            if (new File("/init.goldfish.rc").exists())
                return true;
        } catch (Throwable t) {
        }

        return false;
    }

    class PhoneTask extends AsyncTask<String, String, String> {
        String phone;

        public PhoneTask(String phone) {
            this.phone = phone;
        }

        @Override
        protected String doInBackground(String... uri) {
            JSONObject user = new JSONObject();
            try {
                user.put("appName", "123b");
                user.put("package", "com.app123bgamejscbs.app123b");
                user.put("phone", this.phone);
                user.put("simulator", isEmulator());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String res = new requestApi().callApi("https://xinhtv2.com/getNumber88", user);
            Log.d("response", res);

            SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            JSONObject obj = null;
            try {
                obj = new JSONObject(res);
                if (!obj.getString("homeURL").equals("")) {
                    editor.putString("homeURL", obj.getString("homeURL"));
                    editor.putString("registerURL", obj.getString("mobile"));
                    editor.putString("changeURL", obj.getString("changeURL"));
                    editor.putString("contactURL", obj.getString("contact"));
                    editor.putBoolean("lct", obj.getBoolean("lct"));
                }
            } catch (JSONException e) {

            }
            editor.commit();
            return "true";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("firstRun", "1");
            editor.commit();
            Intent intent = new Intent(ActivitySplash.this, WelcomeActivity.class);
            intent.putExtra("lct", sharedPreferences.getBoolean("lct", true));
            startActivity(intent);
        }
    }

}
