package nl.wholesale_iptv.launcher2.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import nl.wholesale_iptv.launcher2.BuildConfig;
import nl.wholesale_iptv.launcher2.R;
import nl.wholesale_iptv.launcher2.adapters.WifiAdapter;
import nl.wholesale_iptv.launcher2.callbacks.FocusCallback;
import nl.wholesale_iptv.launcher2.helpers.ApkHelper;
import nl.wholesale_iptv.launcher2.helpers.NetworkHelper;
import nl.wholesale_iptv.launcher2.helpers.SettingsHelper;
import nl.wholesale_iptv.launcher2.helpers.WorkerHelper;
import nl.wholesale_iptv.launcher2.workers.IPTVBoxWorker;

public class MainActivity extends AppCompatActivity {
    BroadcastReceiver check_update_receiver;
    BroadcastReceiver connectivity_receiver;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        if(IPTVBoxWorker.canRun(this))
            workManager.enqueue(
                new OneTimeWorkRequest
                    .Builder(IPTVBoxWorker.class)
                    .setInitialDelay(1, TimeUnit.SECONDS)
                    .build()
            );
        WorkerHelper.enqueueWorkers(this);

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
        
        connectivity_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkConnection();
            }
        };
    
        IntentFilter connectivity_receiver_intent_filter = new IntentFilter();
        connectivity_receiver_intent_filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        connectivity_receiver_intent_filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
    
        registerReceiver(connectivity_receiver, connectivity_receiver_intent_filter);
        
        check_update_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                runOnUiThread(() -> checkUpdates());
            }
        };
        
        IntentFilter update_receiver_intent_filter = new IntentFilter();
        update_receiver_intent_filter.addAction("nl.wholesale_iptv.launcher2.check_update");
        
        registerReceiver(check_update_receiver, update_receiver_intent_filter);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectivity_receiver);
        unregisterReceiver(check_update_receiver);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        checkUpdates();
    }
    
    private void checkConnection() {
        ImageView connection_icon = findViewById(R.id.connection_icon);
    
        NetworkHelper networkHelper = new NetworkHelper(this);
        if(networkHelper.isEthernetConnected())
            connection_icon.setImageResource(R.drawable.ic_ethernet);
        else if(networkHelper.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
            connection_icon.setImageResource(WifiAdapter.getLevelIconResource(networkHelper.getWifiInfo().getRssi()));
        else
            connection_icon.setImageResource(R.drawable.ic_cloud_off);
    }
    
    private void checkUpdates() {
        LinearLayout settings_update_banner = findViewById(R.id.settings_update_banner);
    
        ApkHelper apkHelper = new ApkHelper(this);
    
        if(apkHelper.hasUpdate())
            settings_update_banner.setVisibility(View.VISIBLE);
        else
            settings_update_banner.setVisibility(View.GONE);
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