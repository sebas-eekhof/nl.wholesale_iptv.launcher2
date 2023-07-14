package nl.wholesale_iptv.launcher2.models;

import android.content.Context;
import android.content.Intent;

import nl.wholesale_iptv.launcher2.activities.UpdaterActivity;

public class UpdateItem {
    public final String name;
    public final String package_id;
    public final String current_version;
    public final String available_version;
    public final int current_build_id;
    public final int available_build_id;
    public final String url;

    public UpdateItem(String name, String package_id, String current_version, String available_version, int current_build_id, int available_build_id, String url) {
        this.name = name;
        this.package_id = package_id;
        this.current_version = current_version;
        this.available_version = available_version;
        this.current_build_id = current_build_id;
        this.available_build_id = available_build_id;
        this.url = url;
    }

    public boolean hasUpdate() {
        return (current_build_id != available_build_id);
    }

    public static UpdateItem fromIntent(Intent intent) throws Exception {
        String name = intent.getStringExtra("name");
        String package_id = intent.getStringExtra("package_id");
        String current_version = intent.getStringExtra("current_version");
        String available_version = intent.getStringExtra("available_version");
        int current_build_id = intent.getIntExtra("current_build_id", -1);
        int available_build_id = intent.getIntExtra("available_build_id", -1);
        String url = intent.getStringExtra("url");
        if(
            name == null ||
            package_id == null ||
            current_version == null ||
            available_version == null ||
            current_build_id == -1 ||
            available_build_id == -1 ||
            url == null
        ) throw new Exception("Invalid intent data");
        return new UpdateItem(
            name,
            package_id,
            current_version,
            available_version,
            current_build_id,
            available_build_id,
            url
        );
    }

    public Intent getUpdaterIntent(Context context) {
        Intent intent = new Intent(context, UpdaterActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("package_id", package_id);
        intent.putExtra("current_version", current_version);
        intent.putExtra("available_version", available_version);
        intent.putExtra("current_build_id", current_build_id);
        intent.putExtra("available_build_id", available_build_id);
        intent.putExtra("url", url);
        return intent;
    }
}
