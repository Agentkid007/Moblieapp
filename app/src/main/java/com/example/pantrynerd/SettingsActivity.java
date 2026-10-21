package com.example.pantrynerd;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

//the settings page which switches off the expiry alerts and changes the unit system to
//metric or imperial
//uses SharedPreferences so it persists

// Assignment 2.2 / 3.1 - settings screen the extra screen on top of the four required ones
@SuppressLint("UseSwitchCompatOrMaterialCode")
public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "PantryNerdPreferences";
    public static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";
    public static final String KEY_DARK_MODE = "dark_mode_enabled";
    public static final String KEY_UNIT_SYSTEM = "unit_system"; // metric or imperial

    private Switch alertsSwitch;
    private Switch darkModeSwitch;
    private RadioGroup unitRadioGroup;

    @Override
    protected void onResume() {
        super.onResume();
        NavHelper.select(this, R.id.navigation_settings);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        alertsSwitch = findViewById(R.id.alertsSwitch);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        unitRadioGroup = findViewById(R.id.unitRadioGroup);

        //sets each widget to whatever was last saved before attaching listeners
        loadPreferences();

        //all changes will be saved to SharedPreferences directly
        alertsSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                getPreferences().edit().putBoolean(KEY_EXPIRY_ALERTS, isChecked).apply());

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getPreferences().edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        unitRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String unitSystem = checkedId == R.id.imperialRadioButton ? "imperial" : "metric";
            getPreferences().edit().putString(KEY_UNIT_SYSTEM, unitSystem).apply();
        });

        NavHelper.setup(this, R.id.navigation_settings);
    }

    //loads the last saved preferences
    private void loadPreferences() {
        SharedPreferences sharedPreferences = getPreferences();
        boolean alertsEnabled = sharedPreferences.getBoolean(KEY_EXPIRY_ALERTS, true);
        alertsSwitch.setChecked(alertsEnabled);

        boolean darkModeEnabled = sharedPreferences.getBoolean(KEY_DARK_MODE,
                (getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK)
                        == android.content.res.Configuration.UI_MODE_NIGHT_YES);
        darkModeSwitch.setChecked(darkModeEnabled);

        String unitSystem = sharedPreferences.getString(KEY_UNIT_SYSTEM, "metric");
        if ("imperial".equals(unitSystem)) {
            unitRadioGroup.check(R.id.imperialRadioButton);
        } else {
            unitRadioGroup.check(R.id.metricRadioButton);
        }
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }
}
