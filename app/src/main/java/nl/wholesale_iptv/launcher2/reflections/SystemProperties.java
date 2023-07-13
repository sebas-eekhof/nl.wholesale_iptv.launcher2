package nl.wholesale_iptv.launcher2.reflections;

import android.annotation.SuppressLint;

import java.lang.reflect.Method;

public class SystemProperties {
    public static String get(String property) {
        try {
            @SuppressLint("PrivateApi") Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            String res = (String) get.invoke(c, property);
            if(res == null)
                return "";
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
