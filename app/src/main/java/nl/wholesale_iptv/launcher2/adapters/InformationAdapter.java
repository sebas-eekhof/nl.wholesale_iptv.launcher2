package nl.wholesale_iptv.launcher2.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nl.wholesale_iptv.launcher2.R;
import nl.wholesale_iptv.launcher2.models.InfoItem;

public class InformationAdapter extends ArrayAdapter<InfoItem> implements View.OnClickListener {
    private Context context;
    private ArrayList<InfoItem> data;

    public InformationAdapter(ArrayList<InfoItem> data, Context context) {
        super(context, R.layout.information_item, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public void onClick(View v) {

    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        InfoItem item = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.information_item, parent, false);
        ((TextView) convertView.findViewById(R.id.info_title)).setText(item.title);
        ((TextView) convertView.findViewById(R.id.info_value)).setText(item.value);

        return convertView;
    }
}
