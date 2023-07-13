package nl.wholesale_iptv.launcher2.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import nl.wholesale_iptv.launcher2.BuildConfig;
import nl.wholesale_iptv.launcher2.R;
import nl.wholesale_iptv.launcher2.callbacks.FocusCallback;
import nl.wholesale_iptv.launcher2.helpers.SettingsHelper;
import nl.wholesale_iptv.launcher2.workers.KeyboardWorker;
import nl.wholesale_iptv.launcher2.workers.ProbeWorker;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        if(KeyboardWorker.canRun(this))
            workManager.enqueue(
                new OneTimeWorkRequest
                    .Builder(KeyboardWorker.class)
                    .setInitialDelay(1, TimeUnit.SECONDS)
                    .build()
            );
        workManager.enqueue(
                new PeriodicWorkRequest.Builder(ProbeWorker.class, 15, TimeUnit.MINUTES)
                    .build()
            );

        SettingsHelper settingsHelper = new SettingsHelper(this);

        TextView app_version = findViewById(R.id.app_version);
        TextView copyright = findViewById(R.id.copyright);

        CardView tv_button = findViewById(R.id.tv_button);
        CardView settings_button = findViewById(R.id.settings_button);

        ImageView tv_button_banner = findViewById(R.id.tv_button_banner);
        ImageView settings_button_banner = findViewById(R.id.settings_button_banner);

        app_version.setText(BuildConfig.VERSION_NAME + "-" + settingsHelper.getTVVersion() + "-" + Build.VERSION.SDK_INT);
        copyright.setText("IPTV Nederland - " + Calendar.getInstance().get(Calendar.YEAR));

        tv_button.setOnFocusChangeListener(handleFocus(hasFocus -> tv_button_banner.setImageResource(hasFocus ? R.drawable.launcher_banner_tv_focus : R.drawable.launcher_banner_tv)));
        settings_button.setOnFocusChangeListener(handleFocus(hasFocus -> {
            settings_button_banner.setImageResource(hasFocus ? R.drawable.launcher_banner_settings_focus : R.drawable.launcher_banner_settings);
        }));

        tv_button.setOnClickListener(v -> launchTV());
        settings_button.setOnClickListener(v -> openSettings());

        tv_button.requestFocus();
    }

    View.OnFocusChangeListener handleFocus(FocusCallback callback) {
        return (v, hasFocus) -> {
            v.setElevation(hasFocus ? 12 : 2);
            v.setAlpha(hasFocus ? 1F : 0.5F);
            callback.onFocusChange(hasFocus);
        };
    }

    void openSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    void launchTV() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("nl.wholesale_iptv.player.tv");
        if(launchIntent != null)
            startActivity(launchIntent);
    }
}