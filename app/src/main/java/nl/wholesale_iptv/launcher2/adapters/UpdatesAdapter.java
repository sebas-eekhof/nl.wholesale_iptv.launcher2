package nl.wholesale_iptv.launcher2.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import nl.wholesale_iptv.launcher2.R;
import nl.wholesale_iptv.launcher2.models.UpdateItem;

public class UpdatesAdapter extends ArrayAdapter<UpdateItem> {
    private Context context;
    private ArrayList<UpdateItem> data;

    public UpdatesAdapter(ArrayList<UpdateItem> data, Context context) {
        super(context, R.layout.update_item, data);
        this.context = context;
        this.data = data;
    }

    @SuppressLint({"SetTextI18n", "ViewHolder"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        UpdateItem item = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.update_item, parent, false);
        ((TextView) convertView.findViewById(R.id.package_title)).setText(item.name);
        ((TextView) convertView.findViewById(R.id.package_version)).setText(item.current_version);

        if(item.hasUpdate()) {
            ((TextView) convertView.findViewById(R.id.available_version)).setText(item.available_version + " beschikbaar");
            ((TextView) convertView.findViewById(R.id.up_to_date)).setVisibility(View.GONE);
        } else
            ((LinearLayout) convertView.findViewById(R.id.update_available_badge)).setVisibility(View.GONE);

        return convertView;
    }
}
