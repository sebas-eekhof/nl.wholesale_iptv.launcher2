package nl.wholesale_iptv.launcher2.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import nl.wholesale_iptv.launcher2.R;
import nl.wholesale_iptv.launcher2.enums.WifiSecurity;
import nl.wholesale_iptv.launcher2.models.WifiResult;

public class WifiAdapter extends ArrayAdapter<WifiResult> {
    private Context context;
    private ArrayList<WifiResult> data;

    public WifiAdapter(ArrayList<WifiResult> data, Context context) {
        super(context, R.layout.wifi_item, data);
        this.context = context;
        this.data = data;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        WifiResult item = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.wifi_item, parent, false);

        ((TextView) convertView.findViewById(R.id.wifi_ssid)).setText(item.SSID);
        ((ImageView) convertView.findViewById(R.id.wifi_signal_icon)).setImageResource(getLevelIconResource(item.level));

        if(item.security == WifiSecurity.NONE)
            convertView.findViewById(R.id.wifi_lock_icon).setVisibility(View.INVISIBLE);


        return convertView;
    }

    public static int getLevelIconResource(int level) {
        switch(WifiManager.calculateSignalLevel(level, 5)) {
            case 4:
                return R.drawable.ic_wifi_4;
            case 3:
                return R.drawable.ic_wifi_3;
            case 2:
                return R.drawable.ic_wifi_2;
            case 1:
                return R.drawable.ic_wifi_1;
            default:
                return R.drawable.ic_wifi_0;
        }
    }
}
