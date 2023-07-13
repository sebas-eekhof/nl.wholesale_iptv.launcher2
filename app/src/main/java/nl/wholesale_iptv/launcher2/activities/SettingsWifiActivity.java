package nl.wholesale_iptv.launcher2.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.wholesale_iptv.launcher2.R;
import nl.wholesale_iptv.launcher2.adapters.InformationAdapter;
import nl.wholesale_iptv.launcher2.adapters.WifiAdapter;
import nl.wholesale_iptv.launcher2.helpers.ColorHelper;
import nl.wholesale_iptv.launcher2.helpers.NetworkHelper;
import nl.wholesale_iptv.launcher2.models.WifiResult;

public class SettingsWifiActivity extends AppCompatActivity {
    private static final int WIFI_REQUEST_CODE = 44213;
    private WifiManager wifiManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_wifi);

        NetworkHelper networkHelper = new NetworkHelper(this);

        this.wifiManager = networkHelper.getWifiManager();

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch wifiToggle = findViewById(R.id.wifi_toggle);

        wifiToggle.setChecked(networkHelper.isWifiEnabled());
        wifiToggle.setOnCheckedChangeListener((v, isChecked) -> networkHelper.setWifiEnabled(isChecked));

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success)
                    scanResult();
                else
                    scanFail();
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanReceiver, intentFilter);

        if(networkHelper.isWifiEnabled())
            startScan();
    }

    private void startScan() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int perm = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if(perm != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    new String[] {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE
                    },
                    SettingsWifiActivity.WIFI_REQUEST_CODE
                );
                return;
            }
        }
        performScan();
    }

    private void performScan() {
        boolean isScanSuccessStart = wifiManager.startScan();
        System.out.println("Scan start success: " + (isScanSuccessStart ? "TRUE" : "FALSE"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == SettingsWifiActivity.WIFI_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                performScan();
            else
                Toast.makeText(this, "Geen rechten om een WiFi scan uit te voeren", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void scanResult() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            scanFail();
            return;
        }
        List<ScanResult> result = wifiManager.getScanResults();
        Collections.sort(result, (d1, d2) -> d2.level - d1.level);
        List<String> ssid_list = new ArrayList<>();
        ArrayList<WifiResult> wifi_list = new ArrayList<>();
        for(ScanResult item: result) {
            if(ssid_list.contains(item.SSID)) continue;
            ssid_list.add(item.SSID);
            wifi_list.add(WifiResult.fromScanResult(item));
        }

        System.out.println("Wifi scan success, count: " + wifi_list.size());

        WifiAdapter adapter = new WifiAdapter(wifi_list, this);
        ListView wifi_listview = findViewById(R.id.wifi_list);
        wifi_listview.setAdapter(adapter);

        wifi_listview.setOnItemClickListener((adapterView, view, position, d) -> {
            WifiResult item = wifi_list.get(position);

            View wifiPrompt = getLayoutInflater().inflate(R.layout.wifi_prompt, null);
            EditText wifiPromptText = wifiPrompt.findViewById(R.id.wifi_password);
            Button wifiPromptButton = wifiPrompt.findViewById(R.id.connect_button);

            AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Verbinden met " + item.SSID)
                    .setMessage("Kaulo")
                    .setView(wifiPrompt)
                    .show();

            wifiPromptText.setOnKeyListener((v, key, event) -> {
                if(key == 66) {
                    String password = wifiPromptText.getText().toString();
                    Toast.makeText(this, password, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return true;
                }
                return false;
            });

            wifiPromptButton.setOnClickListener((v) -> {
                String password = wifiPromptText.getText().toString();
                Toast.makeText(this, password, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });
    }

    private void scanFail() {
        System.out.println("Wifi scan failed");
    }
}
