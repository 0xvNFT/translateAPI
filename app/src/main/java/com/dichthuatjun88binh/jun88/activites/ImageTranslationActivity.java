package com.dichthuatjun88binh.jun88.activites;

import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.All_languages;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.FILE_NAME;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.KEY_BIG;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.KEY_Language_Image_1;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.KEY_Language_Image_2;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.editor;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.flags;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.full_ad;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.language_image_1;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.language_image_2;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.languages_code;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.sharedPreferences;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.speach_code;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dichthuatjun88binh.jun88.R;
import com.dichthuatjun88binh.jun88.db.HelperDB;
import com.dichthuatjun88binh.jun88.model.LanguageModel;
import com.dichthuatjun88binh.jun88.orc_reader_main.CameraSourcePreview;
import com.dichthuatjun88binh.jun88.orc_reader_main.GraphicOverlay;
import com.dichthuatjun88binh.jun88.orc_reader_main.TextGraphicBlock;
import com.dichthuatjun88binh.jun88.utils.AppConfig;
import com.dichthuatjun88binh.jun88.utils.Exif;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageTranslationActivity extends AppCompatActivity {
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private final String TAG = "ImageTranslationActivity";
    public ImageEnum imageScreen = ImageEnum.CAMERA;
    //Loading Ads
    public LinearLayout layoutLoadingAds;
    RelativeLayout source_picker, target_picker;
    TextView source_name, destination_name;
    ImageView source_flag, target_flag;
    ImageView changer;
    String selection;
    String languageSelected;
    LanguageModel source_data, target_data;
    List<LanguageModel> languages_data = new ArrayList<>();
    List<LanguageModel> search_list1 = new ArrayList<>();
    HelperDB helperDB;
    ProgressBar progressBar;
    CameraSourcePreview mPreview;
    String result;
    FloatingActionButton fabHome;
    FloatingActionButton fabCamera;
    FloatingActionButton fabGallery;
    String stringText;
    //Image layout
    RelativeLayout layoutBgBlack;
    RelativeLayout layoutButtons;
    ConstraintLayout layoutImage;
    AppCompatImageView imgPhoto;
    Button btnNewScan;
    FloatingActionButton fabHome2;
    FloatingActionButton fabGallery2;
    int translateMax = 0;
    int translateIndex = 0;
    float paddingDrawTextWidth = 0;
    float paddingDrawTextHeight = 0;
    //Layout Landscape screen
    OrientationEventListener orientationEventListener;
    boolean firstLandscape = false;
    LinearLayout layoutLandscapeNotify;
    Button btnOk;
    //Firebase Analytics
    Bundle bundle = new Bundle();
    int loadInterAdsCount = 0;
    com.google.mlkit.vision.text.TextRecognizer recognizer;
    View lineDivider;
    ActivityResultLauncher<Intent> galleryActivityResultLauncher;
    ActivityResultLauncher<Intent> selectLanguageActivityResultLauncher;
    CameraSource mCameraSource;
    private TextRecognizer textRecognizer;
    private GraphicOverlay mGraphicOverlay;
    private Translator translator;
    private ActivityResultLauncher<String> getPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_translation);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);

        }
        //Set Full screen (hide Android Navigation)
        hideSystemUI(getWindow());

        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.VIETNAMESE)
                        .build();
        this.translator = Translation.getClient(options);

        initViews();
        initData();
        addListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initViews() {
        source_flag = findViewById(R.id.flag_source);
        target_flag = findViewById(R.id.flag_final);
        source_name = findViewById(R.id.source_name);
        destination_name = findViewById(R.id.target_name);
        source_picker = findViewById(R.id.source_picker);
        target_picker = findViewById(R.id.target_picker);
        changer = findViewById(R.id.interchange);
        progressBar = findViewById(R.id.progress_circular);
        mPreview = findViewById(R.id.preview);
        fabHome = findViewById(R.id.fabHome);
        fabGallery = findViewById(R.id.fabGallery);
        fabCamera = findViewById(R.id.fabCamera);
        mGraphicOverlay = findViewById(R.id.graphicOverlay);

        //Landscape Notify
        layoutLandscapeNotify = findViewById(R.id.layoutLandscapeNotify);
        btnOk = findViewById(R.id.btnOk);

        //Image layout
        layoutBgBlack = findViewById(R.id.layoutBgBlack);
        layoutButtons = findViewById(R.id.layoutButtons);
        layoutImage = findViewById(R.id.layoutImage);
        imgPhoto = findViewById(R.id.imgPhoto);
        btnNewScan = findViewById(R.id.btnNewScan);
        fabHome2 = findViewById(R.id.fabHome2);
        fabGallery2 = findViewById(R.id.fabGallery2);
    }

    private void addListener() {
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
                updateTranslatorLanguages();
            }
        });

        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(ImageTranslationActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ImageTranslationActivity.this, ImageTranslationActivity.this.getResources().getString(R.string.no_camera_permission), Toast.LENGTH_SHORT).show();
                    return;
                }
//                if (stringText == null) {
//                    Toast.makeText(context, context.getResources().getString(R.string.could_not_catch_text), Toast.LENGTH_SHORT).show();
//                } else {
                progressBar.setVisibility(View.VISIBLE);
                switchCameraOrPhotoImage(ImageEnum.DETAILS);
                imgPhoto.setImageDrawable(null);
                layoutButtons.setVisibility(View.GONE);
                mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                    @Override
                    public void onPictureTaken(@NonNull byte[] bytes) {
                        mPreview.stop();
                        layoutBgBlack.setVisibility(View.VISIBLE);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Bitmap bitmapPicture;
                        int orientation = Exif.getOrientation(bytes);
                        switch (orientation) {
                            case 90:
                                bitmapPicture = rotateImage(bitmap, 90);

                                break;
                            case 180:
                                bitmapPicture = rotateImage(bitmap, 180);

                                break;
                            case 270:
                                bitmapPicture = rotateImage(bitmap, 270);

                                break;
                            case 0:
                                // if orientation is zero we don't need to rotate this
                            default:
                                bitmapPicture = bitmap;
                                break;
                        }
                        imgPhoto.setImageBitmap(bitmapPicture);
                        new CountDownTimer(5000, 1000) {
                            @Override
                            public void onTick(long l) {
                                if (bitmapPicture != null) {
                                    runTextRecognition(getScaledBitmap(bitmapPicture));
                                    cancel();
                                }
                            }

                            @Override
                            public void onFinish() {

                            }
                        }.start();
                    }
                });
//                }
            }
        });

        TextRecognizer();

        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageChooser();
            }
        });


        switchCameraOrPhotoImage(ImageEnum.CAMERA);

        btnNewScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGraphicOverlay.clear();
                switchCameraOrPhotoImage(ImageEnum.CAMERA);
                try {
                    if (ActivityCompat.checkSelfPermission(ImageTranslationActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mPreview.start(mCameraSource, mGraphicOverlay);
                    layoutBgBlack.setVisibility(View.GONE);
                    layoutButtons.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        fabHome2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImageTranslationActivity.this, HomeActivity.class);
                intent.putExtra(AppConfig.EXTRA_TEXT, stringText);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        fabGallery2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        orientationEventListener = new OrientationEventListener(ImageTranslationActivity.this) {
            @Override
            public void onOrientationChanged(int i) {
                if (i < 45 || i > 315) {
                    layoutLandscapeNotify.setVisibility(View.GONE);
                } else if (!firstLandscape && imageScreen == ImageEnum.CAMERA) {
                    layoutLandscapeNotify.setVisibility(View.VISIBLE);
                }
            }
        };
        orientationEventListener.enable();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstLandscape = true;
                layoutLandscapeNotify.setVisibility(View.GONE);
            }
        });

        /*fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackHome(stringText);
                dismiss();
            }
        });*/

        galleryActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    if (data != null) {
                        final Uri selectedImageUri = data.getData();
                        if (selectedImageUri != null) {
                            try {
                                InputStream stream = ImageTranslationActivity.this.getContentResolver().openInputStream(selectedImageUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                                if (bitmap != null) {
                                    mGraphicOverlay.clear();
                                    mPreview.stop();
                                    layoutButtons.setVisibility(View.GONE);
                                    layoutBgBlack.setVisibility(View.VISIBLE);
                                    switchCameraOrPhotoImage(ImageEnum.DETAILS);
                                    imgPhoto.setImageBitmap(bitmap);

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            runTextRecognition(getScaledBitmap(bitmap));
                                        }
                                    }, 1000);

                                    /*Frame mframe = new Frame.Builder().setBitmap(bitmap).build();
                                    SparseArray<TextBlock> sparseArray = mTextRecognizer.detect(mframe);
                                    StringBuilder stringBuilder = new StringBuilder();

                                    for (int i = 0; i < sparseArray.size(); ++i) {
                                        TextBlock textBlock = sparseArray.valueAt(i);
                                        if (textBlock != null && textBlock.getValue() != null) {
                                            stringBuilder.append(textBlock.getValue() + " ");
                                        }
                                    }

                                    stringText = stringBuilder.toString();
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tvOriginalText.setText(stringText);
                                        }
                                    });*/
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            }
        });

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
                                            editor.putInt(KEY_Language_Image_2, i);
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
                                        editor.putInt(KEY_Language_Image_1, i);
                                        editor.commit();
                                        break;
                                    }
                                }
                                setData();
                                updateTranslatorLanguages();
                            } catch (Exception e) {
                                Toast.makeText(ImageTranslationActivity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
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
                                            editor.putInt(KEY_Language_Image_1, i);
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
                                        editor.putInt(KEY_Language_Image_2, i);
                                        editor.commit();
                                        break;
                                    }
                                }
                                setData();
                                updateTranslatorLanguages();
                            } catch (Exception e) {
                                Toast.makeText(ImageTranslationActivity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }
            }
        });
    }

    private void initData() {
        helperDB = new HelperDB(ImageTranslationActivity.this);
        for (int i = 0; i < All_languages.length; i++) {
            LanguageModel language_model = new LanguageModel();
            language_model.setLanguage_name(All_languages[i]);
            language_model.setLanguage_flag(flags[i]);
            language_model.setLanguage_code(languages_code[i]);
            language_model.setLanguage_speech_code(speach_code[i]);
            languages_data.add(language_model);
        }
        sharedPreferences = ImageTranslationActivity.this.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        language_image_1 = sharedPreferences.getInt(KEY_Language_Image_1, language_image_1);
        language_image_2 = sharedPreferences.getInt(KEY_Language_Image_2, language_image_2);
        if (language_image_1 != 0) {
            source_data = languages_data.get(language_image_1);
            if (language_image_2 != 0) {
                target_data = languages_data.get(language_image_2);
            } else {
                target_data = languages_data.get(AppConfig.TARGET_INIT);
            }
        } else {
            source_data = languages_data.get(AppConfig.SOURCE_INIT);
            target_data = languages_data.get(AppConfig.TARGET_INIT);
        }
        updateTranslatorLanguages();
        source_name.setText(source_data.getLanguage_name());
        source_flag.setImageResource(source_data.getLanguage_flag());
        destination_name.setText(target_data.getLanguage_name());
        target_flag.setImageResource(target_data.getLanguage_flag());

        getPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean isGranted) {
                if (isGranted) {
                    if (ActivityCompat.checkSelfPermission(ImageTranslationActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startCameraSource();
                }
            }
        });
    }

    private void updateTranslatorLanguages() {
        if (TranslateLanguage.fromLanguageTag(source_data.getLanguage_code()) != null && TranslateLanguage.fromLanguageTag(target_data.getLanguage_code()) != null) {
            TranslatorOptions options =
                    new TranslatorOptions.Builder()
                            .setSourceLanguage(TranslateLanguage.fromLanguageTag(source_data.getLanguage_code()))
                            .setTargetLanguage(TranslateLanguage.fromLanguageTag(target_data.getLanguage_code()))
                            .build();
            this.translator = Translation.getClient(options);

            if (imageScreen == ImageEnum.DETAILS) {
                Bitmap bm = ((BitmapDrawable) imgPhoto.getDrawable()).getBitmap();
                runTextRecognition(getScaledBitmap(bm));
            }
        } else {
            if (mGraphicOverlay != null) {
                mGraphicOverlay.clear();
            }
            if (imageScreen == ImageEnum.DETAILS) {
                Toast.makeText(ImageTranslationActivity.this, ImageTranslationActivity.this.getResources().getString(R.string.this_feature_is_not_available_in_selected_language), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void switchCameraOrPhotoImage(ImageEnum imageEnum) {
        imageScreen = imageEnum;
        if (imageScreen == ImageEnum.CAMERA) {
            layoutImage.setVisibility(View.GONE);
        } else {
            layoutImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
        if (orientationEventListener != null) {
            orientationEventListener.disable();
        }
        if (recognizer != null) {
            recognizer.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (imageScreen == ImageEnum.CAMERA) {
            startCameraSource();
        }
        if (orientationEventListener != null) {
            orientationEventListener.enable();
        }

    }

    @Override
    protected void onDestroy() {
        mPreview.release();
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

    private void TextRecognizer() {
        textRecognizer = new com.google.android.gms.vision.text.TextRecognizer.Builder(ImageTranslationActivity.this).build();
        mCameraSource = new CameraSource.Builder(ImageTranslationActivity.this, textRecognizer)
                .setAutoFocusEnabled(true)
                .build();
//        startCameraSource();
        /*surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mCameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                mCameraSource.stop();
            }
        });*/

        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                /*if (imageScreen == ImageEnum.CAMERA) {
                    SparseArray<TextBlock> sparseArray = detections.getDetectedItems();
                    StringBuilder stringBuilder = new StringBuilder();

                    for (int i = 0; i < sparseArray.size(); ++i) {
                        TextBlock textBlock = sparseArray.valueAt(i);
                        if (textBlock != null && textBlock.getValue().length() > 0) {
                            stringBuilder.append(textBlock.getValue());
                            stringBuilder.append("\n");
                        }
                    }

                    stringText = stringBuilder.toString();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            result = stringText;
                            textView.setText(stringText);
                        }
                    });
                }*/
            }
        });
    }

    private void startCameraSource() {
        if (mCameraSource != null) {
            try {
                if (ActivityCompat.checkSelfPermission(ImageTranslationActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (galleryActivityResultLauncher != null) {
            galleryActivityResultLauncher.launch(intent);
        }
    }

    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    private void runTextRecognition(Bitmap mSelectedImage) {
        //Fill a color
        mGraphicOverlay.clear();

        if (mSelectedImage != null) {
            InputImage image = InputImage.fromBitmap(mSelectedImage, 0);
            recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
            recognizer.process(image)
                    .addOnSuccessListener(
                            new OnSuccessListener<Text>() {
                                @Override
                                public void onSuccess(Text texts) {
                                    processTextRecognitionResult(texts);
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    progressBar.setVisibility(View.GONE);
                                    e.printStackTrace();
                                }
                            });
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void processTextRecognitionResult(Text texts) {
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ImageTranslationActivity.this, ImageTranslationActivity.this.getResources().getString(R.string.could_not_catch_text), Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        translateMax = 0;
        translateIndex = 0;

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < blocks.size(); i++) {
            List<Text.Line> lines = blocks.get(i).getLines();
            translateMax += lines.size();
            for (int j = 0; j < lines.size(); j++) {
                startDrawText(lines.get(j));
                if (lines.get(j) != null && lines.get(j).getText().length() > 0) {
                    stringBuilder.append(lines.get(j).getText());
                    stringBuilder.append("\n");
                }
            }

            /*List<Text.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
                    mGraphicOverlay.add(textGraphic);

                }
            }*/
        }
        stringText = stringBuilder.toString();
    }

    public void startDrawText(Text.Line line) {
        translator.downloadModelIfNeeded()
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                translate(line);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
    }

    private void translate(Text.Line line) {
        translator.translate(line.getText())
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@NonNull String translatedText) {
                                GraphicOverlay.Graphic textGraphic = new TextGraphicBlock(mGraphicOverlay, line, translatedText, paddingDrawTextWidth, paddingDrawTextHeight);
                                mGraphicOverlay.add(textGraphic);
                                translateIndex++;
                                if (translateIndex >= translateMax) {
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                translateIndex++;
                                if (translateIndex >= translateMax) {
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
    }

    private Bitmap getScaledBitmap(Bitmap bitmapImage) {

        //width and height of original image
        final int imageWidth = bitmapImage.getWidth();
        final int imageHeight = bitmapImage.getHeight();

        //width and height of the imageView
        final int imageViewWidth = imgPhoto.getMeasuredWidth();
        final int imageViewHeight = imgPhoto.getMeasuredHeight();

        final int scaledWidth, scaledHeight;

        if (imageWidth * imageViewHeight <= imageHeight * imageViewWidth) {
            //rescaled width and height of image within ImageView
            scaledWidth = (imageWidth * imageViewHeight) / imageHeight;
            scaledHeight = imageViewHeight;
        } else {
            //rescaled width and height of image within ImageView
            scaledWidth = imageViewWidth;
            scaledHeight = (imageHeight * imageViewWidth) / imageWidth;
        }

        if (scaledWidth > 0 && scaledHeight > 0) {
            Bitmap bitmapScale = Bitmap.createScaledBitmap(bitmapImage, scaledWidth, scaledHeight, true);
            paddingDrawTextWidth = (layoutImage.getMeasuredWidth() - bitmapScale.getWidth()) / 2.0f;
            paddingDrawTextHeight = (layoutImage.getMeasuredHeight() - bitmapScale.getHeight()) / 2.0f;

            return bitmapScale;
        } else {
            return null;
        }
    }

    private void PickLanguage(String languageSelected) {
        languages_data.clear();
        search_list1.clear();
        for (int i = 0; i < All_languages.length; i++) {
            LanguageModel language_model = new LanguageModel();
            language_model.setLanguage_name(All_languages[i]);
            language_model.setLanguage_flag(flags[i]);
            language_model.setLanguage_code(languages_code[i]);
            language_model.setLanguage_speech_code(speach_code[i]);
            languages_data.add(language_model);
        }
        search_list1.addAll(languages_data);

        Intent intent = new Intent(ImageTranslationActivity.this, SelectLanguageActivity.class);
        intent.putExtra(AppConfig.EXTRA_LANGUAGE_SELECTED, languageSelected);
        intent.putExtra(AppConfig.EXTRA_TEXT_RECOGNIZER_LANGUAGE, true);
        if (selectLanguageActivityResultLauncher != null) {
            selectLanguageActivityResultLauncher.launch(intent);
        }
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

    public enum ImageEnum {
        CAMERA,
        DETAILS
    }
}