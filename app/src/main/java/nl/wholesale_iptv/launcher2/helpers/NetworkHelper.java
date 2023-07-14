package nl.wholesale_iptv.launcher2.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.annotation.Nullable;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetworkHelper {
    private final Context context;

    public NetworkHelper(Context context) {
        this.context = context;
    }

    public boolean isWifiEnabled() {
        return getWifiManager().isWifiEnabled();
    }

    public void setWifiEnabled(boolean enabled) {
        getWifiManager().setWifiEnabled(enabled);
    }

    public WifiManager getWifiManager() {
        return (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Nullable
    private String getMACInterface(String iface_name) {
        try {
            NetworkInterface iface = NetworkInterface.getByName(iface_name);
            StringBuilder mac = new StringBuilder();
            byte[] buf = iface.getHardwareAddress();
            for(int i = 0; i < buf.length; i++) {
                if(i != 0 && mac.length() % 2 == (int) mac.length() % 2)
                    mac.append(":");
                String hex = Integer.toHexString(buf[i] & 0xFF);
                if(hex.length() == 1)
                    mac.append("0").append(hex);
                else
                    mac.append(hex);
            }
            return mac.toString().toUpperCase();
        } catch(Exception e) {
            e.printStackTrace();
        }
        try {
            return FileHelper.readFile("/sys/class/net/" + iface_name + "/address").toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMAC(@Nullable String default_value) {
        String mac = getMACInterface("eth0");
        if(mac == null)
            return default_value;
        return mac;
    }

    @Nullable
    public String getMACSecondary(@Nullable String default_value) {
        String mac = getMACInterface("wlan0");
        if(mac == null)
            return default_value;
        return mac;
    }

    @Nullable
    public String getIpAddress() {
        if(isEthernetConnected())
            try {
                NetworkInterface iface = NetworkInterface.getByName("eth0");
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if(address instanceof Inet6Address) continue;
                    return address.getHostAddress();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        return null;
    }

    public boolean isEthernetConnected() {
        try {
            NetworkInterface iface = NetworkInterface.getByName("eth0");
            return iface.isUp();
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isWifiConnected() {
        WifiManager wifiManager = getWifiManager();
        return (wifiManager.isWifiEnabled() && wifiManager.getConnectionInfo().getNetworkId() != -1);
    }

    @Nullable
    public String getWifiSSID() {
        WifiManager wifiManager = getWifiManager();
        if(!wifiManager.isWifiEnabled())
            return null;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    public int getWifiState() {
        try {
            WifiManager wifiManager = getWifiManager();
            return wifiManager.getWifiState();
        } catch(Exception e) {
            e.printStackTrace();
            return WifiManager.WIFI_STATE_UNKNOWN;
        }
    }
    
    public WifiInfo getWifiInfo() {
        WifiManager wifiManager = getWifiManager();
        return wifiManager.getConnectionInfo();
    }

    public String wifiStateToText(int state) {
        switch(state) {
            case WifiManager.WIFI_STATE_ENABLED:
                return "Verbonden";
            case WifiManager.WIFI_STATE_ENABLING:
                return "Bezig met verbinden";
            case WifiManager.WIFI_STATE_DISABLED:
                return "Niet verbonden";
            case WifiManager.WIFI_STATE_DISABLING:
                return "Verbinding aan het verbreken";
        }
        return "Onbekende status";
    }
}
