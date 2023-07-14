package nl.wholesale_iptv.launcher2.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import nl.wholesale_iptv.launcher2.reflections.SystemProperties;

public class DeviceHelper {
    private final Context context;

    public DeviceHelper(Context context) {
        this.context = context;
    }

    public boolean isPrixonPrisma() {
        return (Build.MODEL.equals("Prisma") && Build.BRAND.equals("Amlogic") && Build.BOARD.equals("newton"));
    }
    
    public boolean isPrixonPrismaWithRoot() {
        return (isPrixonPrisma() && hasRootAccess());
    }
    
    public void reboot() {
        if(!hasRootAccess())
            return;
        try {
            RootShellHelper.exec("reboot");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getSerial() {
        String prefix = "";
        if(isPrixonPrisma())
            prefix = "2108";
        return (prefix) + getRawSerial();
    }

    public boolean hasRootAccess() {
        if(Build.TAGS.contains("test-keys")) return true;
        String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for(String path: paths)
            if(new File(path).exists()) return true;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if(in.readLine() != null) return true;
            return false;
        } catch(Throwable t) {
            return false;
        } finally {
            if(process != null)
                process.destroy();
        }
    }

    @SuppressLint("HardwareIds")
    private String getRawSerial() {
        String serial;

        serial = SystemProperties.get("ro.serialno");
        if(!serial.equals(""))
            return serial;
        serial = SystemProperties.get("ro.boot.serialno");
        if(!serial.equals(""))
            return serial;
        serial = SystemProperties.get("ro.kernel.androidboot.serialno");
        if(!serial.equals(""))
            return serial;
        serial = Build.SERIAL;
        if(!serial.equals(""))
            return serial;
        return "Onbekend";
    }
}
