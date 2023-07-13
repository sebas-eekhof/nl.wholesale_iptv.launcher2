package nl.wholesale_iptv.launcher2.activities;

import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import nl.wholesale_iptv.launcher2.BuildConfig;
import nl.wholesale_iptv.launcher2.R;
import nl.wholesale_iptv.launcher2.adapters.InformationAdapter;
import nl.wholesale_iptv.launcher2.helpers.DeviceHelper;
import nl.wholesale_iptv.launcher2.helpers.NetworkHelper;
import nl.wholesale_iptv.launcher2.helpers.SettingsHelper;
import nl.wholesale_iptv.launcher2.models.InfoItem;

public class InformationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        ListView listView = findViewById(R.id.info_listview);

        SettingsHelper settingsHelper = new SettingsHelper(this);
        NetworkHelper networkHelper = new NetworkHelper(this);
        DeviceHelper deviceHelper = new DeviceHelper(this);

        String ip = networkHelper.getIpAddress();

        ArrayList<InfoItem> list = new ArrayList<>();
        list.add(new InfoItem("Launcher versie", BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")"));
        list.add(new InfoItem("TV versie", settingsHelper.getTVVersionFull()));
        list.add(new InfoItem("SDK versie", "" + Build.VERSION.SDK_INT));
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
