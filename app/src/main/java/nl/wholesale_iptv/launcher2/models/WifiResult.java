package nl.wholesale_iptv.launcher2.models;

import android.net.wifi.ScanResult;

import nl.wholesale_iptv.launcher2.enums.WifiSecurity;

public class WifiResult {
    public String SSID;
    public int level;
    public int frequency;
    public WifiSecurity security;

    public WifiResult(String SSID, int level, int frequency, WifiSecurity security) {
        this.SSID = SSID;
        this.level = level;
        this.frequency = frequency;
        this.security = security;
    }

    public static WifiResult fromScanResult(ScanResult item) {
        return new WifiResult(
            item.SSID,
            item.level,
            item.frequency,
            getSecurity(item.capabilities)
        );
    }

    private static WifiSecurity getSecurity(String capabilities) {
        if(capabilities.contains("WEP"))
            return WifiSecurity.WEP;
        if(capabilities.contains("PSK"))
            return WifiSecurity.PSK;
        if(capabilities.contains("EAP"))
            return WifiSecurity.EAP;
        return WifiSecurity.NONE;
    }
}
