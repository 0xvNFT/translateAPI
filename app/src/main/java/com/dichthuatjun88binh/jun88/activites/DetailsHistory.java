package com.dichthuatjun88binh.jun88.activites;

import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.position;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.source_l;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.source_t;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.target_l;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.target_t;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dichthuatjun88binh.jun88.R;
import com.dichthuatjun88binh.jun88.db.HelperDB;

public class DetailsHistory extends AppCompatActivity {

    public LinearLayout layoutLoadingAds;
    Context context;
    TextView source_label, target_label, source_txt, target_txt;
    LinearLayout del, share, copy;
    HelperDB mydb;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_details);
        mydb = new HelperDB(getApplicationContext());
        source_label = findViewById(R.id.source_detale_label);
        target_label = findViewById(R.id.target_detale_label);
        source_txt = findViewById(R.id.source_detaile_text);
        target_txt = findViewById(R.id.target_dtaile_text);
        copy = findViewById(R.id.copy);
        source_label.setText(source_l);
        target_label.setText(target_l);
        source_txt.setText(source_t);
        target_txt.setText(target_t);
        back = findViewById(R.id.goback);
        del = findViewById(R.id.del);
        share = findViewById(R.id.share);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a = mydb.delTrans(String.valueOf(position));
                if (a == 1) {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.translation_item_successfully_deleted), Toast.LENGTH_SHORT).show();
                    finish();
                    //ActivityMain.ShowLargAdd();
                }
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
//                    String sAux = "Translation result" + "\n";
                    String sAux = source_t + "\n\n" + target_t;
//                    sAux = sAux + target_t + "\n\n" + "-----------\n";
//                    sAux = sAux + "For more transltions please download the App\n" + "https://play.google.com/store/apps/details?id=" + getPackageName();
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, getString(R.string.choose_one)));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Sorry, Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sAux = source_t + "\n\n" + target_t;
                if (target_t.length() != 0) {
                    if (Build.VERSION.SDK_INT < 11) {
                        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setText(sAux);
                    } else {
                        ((android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Copy", sAux));
                    }
                    Toast.makeText(getApplicationContext(), getString(R.string.text_copied_to_clipboard), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getBaseContext(), getString(R.string.nothing_to_copy), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
