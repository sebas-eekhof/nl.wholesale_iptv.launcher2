package nl.wholesale_iptv.launcher2.helpers;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

import nl.wholesale_iptv.launcher2.models.UpdateItem;

public class ApkHelper {
    private final Context context;

    public ApkHelper(Context context) {
        this.context = context;
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
}
