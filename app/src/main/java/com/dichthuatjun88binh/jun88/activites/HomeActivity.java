package com.dichthuatjun88binh.jun88.activites;

import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.FILE_NAME;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.editor;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.sharedPreferences;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dichthuatjun88binh.jun88.R;
import com.dichthuatjun88binh.jun88.fragments.HistoryFragment;
import com.dichthuatjun88binh.jun88.fragments.HomeFragment;
import com.dichthuatjun88binh.jun88.fragments.InfoFragment;
import com.dichthuatjun88binh.jun88.fragments.MainFragment;
import com.dichthuatjun88binh.jun88.fragments.VoiceFragment;
import com.dichthuatjun88binh.jun88.service.FloatingWidgetService;
import com.dichthuatjun88binh.jun88.utils.AppConfig;
import com.dichthuatjun88binh.jun88.utils.FragmentChangeListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;


public class HomeActivity extends AppCompatActivity implements FragmentChangeListener {
    public ChipNavigationBar bottomBar;
    //Ads
    public LinearLayout layoutLoadingAds;
    public MainFragment mainFragment;
    public HomeFragment homeFragment;
    public HistoryFragment historyFragment;
    public InfoFragment infoFragment;
    public VoiceFragment voiceFragment;
    RelativeLayout relativeLayout;
    boolean isHome;
    //Show button Translate
    Button btnTranslation;
    int mExtraScreenHeight = -1, mKeyboardHeight = 0;
    boolean mKeyboardOpen;
    Bundle bundle = new Bundle();
    BottomSheetDialog bottomSheetDialog;


    ActivityResultLauncher<Intent> drawOverOtherAppActivityResultLauncher, accessibilityServiceActivityResultLauncher;
    ActivityResultLauncher<Intent> openImageTranslationActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        initView();
        addListener();

        drawOverOtherAppActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                //Check if the permission is granted or not.
                if (!Settings.canDrawOverlays(HomeActivity.this)) {
                    //Permission is not available then display toast
                    Toast.makeText(HomeActivity.this,
                            getResources().getString(R.string.draw_other_app_permission_denied),
                            Toast.LENGTH_SHORT).show();
                } else {
                    //If permission granted start floating widget service
                    startFloatingWidgetService();
                }
            }
        });
        accessibilityServiceActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                //Check if the permission is granted or not.
                if (!Settings.canDrawOverlays(HomeActivity.this)) {
//                    Permission is not available then display toast
                    Toast.makeText(HomeActivity.this,
                            getResources().getString(R.string.draw_other_app_permission_denied),
                            Toast.LENGTH_SHORT).show();
                } else {
                    //If permission granted start floating widget service
                    startFloatingWidgetService();
                }
            }
        });

        openImageTranslationActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    bottomBar.setItemSelected(R.id.text_home, true);
                    if (data != null) {
                        String sourceText = data.getStringExtra(AppConfig.EXTRA_TEXT);
                        if (sourceText != null && sourceText.trim().length() > 0) {
                            mainFragment.receiveTextFromCamera(sourceText);
                        }
                    }
                }
            }
        });
    }

    private void initView() {
        btnTranslation = findViewById(R.id.btnTranslation);
        //Initializing Fragments
        homeFragment = HomeFragment.newInstance();
        mainFragment = MainFragment.newInstance();
        infoFragment = InfoFragment.newInstance();
        voiceFragment = VoiceFragment.newInstance();
        historyFragment = HistoryFragment.newInstance();

        mainFragment.setBtnTranslationActivity(btnTranslation);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, homeFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        isHome = true;

        bottomBar = findViewById(R.id.bottomNav);
        bottomBar.setItemSelected(R.id.home_main, true);
        relativeLayout = findViewById(R.id.backPressedLayout);
    }

    private void removeMenuTitle(int id) {
        View item = bottomBar.findViewById(id);
        if (item != null) {
            View itemTitle = item.findViewById(R.id.cbn_item_title);
            if (itemTitle != null) {
                itemTitle.setVisibility(View.GONE);
            }
        }
    }

    private void addListener() {
        bottomBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                //Fix remove title and align center icon
                removeMenuTitle(id);
                switch (id) {
                    case R.id.text_home:
                        FragmentTransaction transactionhome = getSupportFragmentManager().beginTransaction();
                        transactionhome.replace(R.id.frameLayout, mainFragment);
                        transactionhome.addToBackStack(null);
                        transactionhome.commit();
                        isHome = false;
                        break;
                    case R.id.home_main:
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frameLayout, homeFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        isHome = true;
                        break;

                    case R.id.history:
                        FragmentTransaction translationHistory = getSupportFragmentManager().beginTransaction();
                        translationHistory.replace(R.id.frameLayout, historyFragment);
                        translationHistory.addToBackStack(null);
                        translationHistory.commit();
                        isHome = false;
                        break;

                    case R.id.voice:
                        FragmentTransaction transactionVoice = getSupportFragmentManager().beginTransaction();
                        transactionVoice.replace(R.id.frameLayout, voiceFragment);
                        transactionVoice.addToBackStack(null);
                        transactionVoice.commit();
                        isHome = false;
                        break;

                    case R.id.camera:
                        isHome = false;
                        Intent intent = new Intent(HomeActivity.this, ImageTranslationActivity.class);
                        openImageTranslationActivityResultLauncher.launch(intent);
                        break;


                    case R.id.info:
                        FragmentTransaction transactionInfo = getSupportFragmentManager().beginTransaction();
                        transactionInfo.replace(R.id.frameLayout, infoFragment);
                        transactionInfo.addToBackStack(null);
                        transactionInfo.commit();
                        isHome = false;
                        break;
                }
            }
        });
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.GONE);
            }
        });

        View rootView = getWindow().getDecorView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int rootViewHeight, visibleDisplayFrameHeight, fakeHeight;
                Rect rect = new Rect();
                rootView.getWindowVisibleDisplayFrame(rect);

                rootViewHeight = rootView.getRootView().getHeight();
                visibleDisplayFrameHeight = rect.height();

                fakeHeight = rootViewHeight - visibleDisplayFrameHeight;

                if (mExtraScreenHeight == -1) {
                    mExtraScreenHeight = fakeHeight;
                    btnTranslation.setVisibility(View.GONE);
                } else if (fakeHeight <= 400) {
                    mExtraScreenHeight = fakeHeight;
                    btnTranslation.setVisibility(View.GONE);
                    mKeyboardOpen = false;
                } else if (bottomBar.getSelectedItemId() == R.id.text_home) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        WindowInsets insets = HomeActivity.this.getWindowManager().getCurrentWindowMetrics().getWindowInsets();
                        int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom; //in pixels
                        mKeyboardHeight = fakeHeight - navigationBarHeight;
                    } else {
                        mKeyboardHeight = fakeHeight - mExtraScreenHeight;
                    }
                    mKeyboardOpen = true;
                    //Set margin for show Translation button above virtual keyboard
                    btnTranslation.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnTranslation.getLayoutParams();
                    params.setMargins(0, 0, 0, mKeyboardHeight);
                    btnTranslation.setLayoutParams(params);
                    btnTranslation.setEnabled(mainFragment != null && mainFragment.source_input.getText() != null && mainFragment.source_input.getText().length() > 0);
                }
            }
        });
    }

    private void startFloatingWidgetService() {
        startService(new Intent(HomeActivity.this, FloatingWidgetService.class).setAction(FloatingWidgetService.ACTION_START));
    }


    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transactionTheme = getSupportFragmentManager().beginTransaction();
        transactionTheme.replace(R.id.frameLayout, fragment);
        bottomBar.setItemSelected(R.id.home_main, true);
        transactionTheme.addToBackStack(null);
        transactionTheme.commit();
        isHome = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(HomeActivity.this, WelcomeActivity.class));
    }
}