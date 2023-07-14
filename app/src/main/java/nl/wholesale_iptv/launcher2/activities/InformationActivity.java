package nl.wholesale_iptv.launcher2.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;

import nl.wholesale_iptv.launcher2.BuildConfig;
import nl.wholesale_iptv.launcher2.R;
import nl.wholesale_iptv.launcher2.adapters.InformationAdapter;
import nl.wholesale_iptv.launcher2.helpers.ApkHelper;
import nl.wholesale_iptv.launcher2.helpers.DeviceHelper;
import nl.wholesale_iptv.launcher2.helpers.NetworkHelper;
import nl.wholesale_iptv.launcher2.helpers.SettingsHelper;
import nl.wholesale_iptv.launcher2.models.InfoItem;

public class InformationActivity extends AppCompatActivity {
    BroadcastReceiver check_update_receiver;
    BroadcastReceiver connectivity_receiver;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        buildList();
    
        connectivity_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                buildList();
            }
        };
    
        IntentFilter connectivity_receiver_intent_filter = new IntentFilter();
        connectivity_receiver_intent_filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        connectivity_receiver_intent_filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
    
        registerReceiver(connectivity_receiver, connectivity_receiver_intent_filter);
    
        check_update_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                runOnUiThread(() -> buildList());
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
    
    private void buildList() {
        ListView listView = findViewById(R.id.info_listview);
    
        SettingsHelper settingsHelper = new SettingsHelper(this);
        NetworkHelper networkHelper = new NetworkHelper(this);
        DeviceHelper deviceHelper = new DeviceHelper(this);
        ApkHelper apkHelper = new ApkHelper(this);
    
        Date last_update_check = apkHelper.getLastUpdateCheckDate();
        String ip = networkHelper.getIpAddress();
    
        DateFormat dateFormat = new DateFormat();
    
        ArrayList<InfoItem> list = new ArrayList<>();
        list.add(new InfoItem("Launcher versie", BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")"));
        list.add(new InfoItem("TV versie", settingsHelper.getTVVersionFull()));
        list.add(new InfoItem("SDK versie", "" + Build.VERSION.SDK_INT));
        list.add(new InfoItem("Laatste update check", (last_update_check == null) ? "Nooit" : (String) DateFormat.format("dd-MM-yyyy hh:mm:ss", last_update_check)));
        list.add(new InfoItem("MAC", networkHelper.getMAC("Onbekend")));
        list.add(new InfoItem("Serienummer", deviceHelper.getSerial()));
        list.add(new InfoItem("Ethernet", networkHelper.isEthernetConnected() ? "Verbonden" : "Losgekoppeld"));
        list.add(new InfoItem("WiFi", networkHelper.wifiStateToText(networkHelper.getWifiState())));
        if(ip != null)
            list.add(new InfoItem("IP", ip));
    
        InformationAdapter adapter = new InformationAdapter(list, this);
        listView.setAdapter(adapter);
    }
}
