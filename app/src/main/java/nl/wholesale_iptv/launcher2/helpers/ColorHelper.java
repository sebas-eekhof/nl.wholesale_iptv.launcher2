package nl.wholesale_iptv.launcher2.helpers;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.res.ResourcesCompat;

public class ColorHelper {
    private final Context context;

    public ColorHelper(Context context) {
        this.context = context;
    }

    public int getColor(int resource_id) {
        return ResourcesCompat.getColor(context.getResources(), resource_id, null);
    }

    public static int settingsFocus() {
        return Color.parseColor("#303030");
    }

    public static int transparent() {
        return Color.alpha(0);
    }
}
