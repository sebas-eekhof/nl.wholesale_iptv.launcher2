package nl.wholesale_iptv.launcher2.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;

public class SettingsHelper {
    private final Context context;

    public SettingsHelper(Context context) {
        this.context = context;
    }

    public void openWifiSettings() {
        context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    public void openAndroidDisplaySettings() {
        context.startActivity(new Intent(Settings.ACTION_DISPLAY_SETTINGS));
    }

    public void openDisplaySettings() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("com.droidlogic.tv.settings","com.droidlogic.tv.settings.display.DisplayActivity"));
        context.startActivity(intent);
    }

    public void openAndroidSoundSettings() {
        context.startActivity(new Intent(Settings.ACTION_SOUND_SETTINGS));
    }

    public void openSoundSettings() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("com.droidlogic.tv.settings","com.droidlogic.tv.settings.SoundActivity"));
        context.startActivity(intent);
    }

    public void openRemotePairing() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("com.droidlogic.tv.settings","com.droidlogic.tv.settings.BtSetupActivity"));
        context.startActivity(intent);
    }

    public String getTVVersionFull() {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo("nl.wholesale_iptv.player.tv", PackageManager.GET_ACTIVITIES);
            return info.versionName + " (" + info.versionCode + ")";
        } catch(Exception e) {
            e.printStackTrace();
            return "NF";
        }
    }

    public String getTVVersion() {
        try {
            return context.getPackageManager().getPackageInfo("nl.wholesale_iptv.player.tv", PackageManager.GET_ACTIVITIES).versionName;
        } catch(Exception e) {
            e.printStackTrace();
            return "NF";
        }
    }
}
