package nl.wholesale_iptv.launcher2.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;

import nl.wholesale_iptv.launcher2.R;
import nl.wholesale_iptv.launcher2.helpers.SettingsHelper;

public class SettingsActivity extends AppCompatActivity {
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SettingsHelper settingsHelper = new SettingsHelper(this);
        NavigationView navigationView = findViewById(R.id.settings_navigation_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch(item.getItemId()) {
                case R.id.option_wifi:
                    settingsHelper.openWifiSettings();
                    break;
                case R.id.option_android_view:
                    settingsHelper.openAndroidDisplaySettings();
                    break;
                case R.id.option_display:
                    settingsHelper.openDisplaySettings();
                    break;
                case R.id.option_sounds:
                    settingsHelper.openAndroidSoundSettings();
                    break;
                case R.id.option_speakers:
                    settingsHelper.openSoundSettings();
                    break;
                case R.id.option_connect_remote:
                    settingsHelper.openRemotePairing();
                    break;
                case R.id.option_updates:
                    startActivity(new Intent(this, UpdatesActivity.class));
                    break;
                case R.id.option_info:
                    startActivity(new Intent(this, InformationActivity.class));
                    break;
            }
            return true;
        });
    }
}
