package nl.wholesale_iptv.launcher2.helpers;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import nl.wholesale_iptv.launcher2.enums.LogTag;
import nl.wholesale_iptv.launcher2.models.UpdateItem;

public class ApkHelper {
    private final Context context;

    public ApkHelper(Context context) {
        this.context = context;
    }
    
    public Date getLastUpdateCheckDate() {
        long time = context.getSharedPreferences("_", Context.MODE_PRIVATE).getLong("get_versions_time", 0);
        if(time == 0)
            return null;
        return new Date(time);
    }
    
    public ArrayList<UpdateItem> getAppVersions() {
        ArrayList<UpdateItem> update_items = new ArrayList<>();
        try {
            String raw_res = context.getSharedPreferences("_", Context.MODE_PRIVATE).getString("get_versions_json", null);
            if(raw_res == null) {
                LogHelper.error(LogTag.CHECK_UPDATE, "Versions not fetched yet");
                return update_items;
            }
            
            PackageManager pm = context.getPackageManager();
            
            JSONArray apps = (new JSONObject(raw_res)).getJSONArray("apps");
    
            for(int i = 0; i < apps.length(); i++) {
                JSONObject item = apps.getJSONObject(i);
                try {
                    PackageInfo info = pm.getPackageInfo(item.getString("package_id"), PackageManager.GET_ACTIVITIES);
                    update_items.add(
                        new UpdateItem(
                            item.getString("name"),
                            item.getString("package_id"),
                            info.versionName,
                            item.getString("version"),
                            info.versionCode,
                            item.getInt("build"),
                            item.getString("url")
                        )
                    );
                } catch(Exception e) {
                    update_items.add(
                        new UpdateItem(
                            item.getString("name"),
                            item.getString("package_id"),
                            "Niet geïnstalleerd",
                            item.getString("version"),
                            0,
                            item.getInt("build"),
                            item.getString("url")
                        )
                    );
                }
            }
            return update_items;
        } catch(Exception e) {
            e.printStackTrace();
            LogHelper.error(LogTag.CHECK_UPDATE, e);
            return update_items;
        }
    }
    
    public boolean hasUpdate() {
        ArrayList<UpdateItem> apps = getAppVersions();
        for(UpdateItem item: apps)
            if(item.hasUpdate())
                return true;
        return false;
    }

    public void downloadApk(UpdateItem item) {
        String filename = item.package_id + "-" + item.available_build_id + ".apk";
        final String dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + filename;
        final Uri dest_uri = Uri.parse("file://" + dest);

        File file = new File(dest);
        if(file.exists())
            file.delete();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(item.url));
        request.setDescription("Test");
        request.setTitle("Test title");
        request.setDestinationUri(dest_uri);

        final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = manager.enqueue(request);

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                install.setDataAndType(dest_uri, manager.getMimeTypeForDownloadedFile(downloadId));
                context.startActivity(install);

                context.unregisterReceiver(this);
            }
        };

        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    
    public void installApk(String path) {
        DeviceHelper deviceHelper = new DeviceHelper(context);
        if(deviceHelper.isPrixonPrisma() && deviceHelper.hasRootAccess()) {
            try {
                RootShellHelper.exec("pm install -g -r " + path);
                return;
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    
    
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(path)), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
