package nl.wholesale_iptv.launcher2.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import nl.wholesale_iptv.launcher2.BuildConfig;
import nl.wholesale_iptv.launcher2.R;
import nl.wholesale_iptv.launcher2.helpers.ApkHelper;
import nl.wholesale_iptv.launcher2.helpers.Formatter;
import nl.wholesale_iptv.launcher2.models.UpdateItem;

public class UpdaterActivity extends AppCompatActivity {
    private UpdateItem updateItem;
    private ProgressBar update_progress;
    private TextView update_download_text;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            updateItem = UpdateItem.fromIntent(getIntent());
        } catch(Exception e) {
            Toast.makeText(this, "Er is iets mis gegaan, probeer het later opnieuw", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_updater);
    
        ApkHelper apkHelper = new ApkHelper(this);

        ((TextView) findViewById(R.id.update_app_name)).setText(updateItem.name + " updaten...");
        update_progress = findViewById(R.id.update_progress);
        update_download_text = findViewById(R.id.update_download_text);
        TextView task_description = findViewById(R.id.update_task_description);
        ProgressBar update_spinner = findViewById(R.id.update_spinner);
        LinearLayout download_layout = findViewById(R.id.download_layout);
        
        update_spinner.setVisibility(View.GONE);
        update_progress.setVisibility(View.VISIBLE);
        update_progress.setProgress(0);
        
        task_description.setText("Een moment geduld, we zijn de update aan het downloaden");

        Thread thread = new Thread(() -> {
            Looper.prepare();
            try {
                DownloadTask downloadTask = new DownloadTask();
                String path = downloadTask.execute(updateItem.url).get();
                if(path == null) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Er is iets mis gegaan, probeer het later opnieuw", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                    return;
                }
                downloadTask.setContext(this);
                runOnUiThread(() -> {
                    download_layout.setVisibility(View.GONE);
                    update_spinner.setVisibility(View.VISIBLE);
                    task_description.setText("Een moment geduld, we zijn de update aan het installeren");
                });
                apkHelper.installApk(path);
                runOnUiThread(() -> {
                    Toast.makeText(this, updateItem.name + " is succesvol geüpdatet", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Er is iets mis gegaan, probeer het later opnieuw", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
            
        });
        
        thread.start();
    }
    
    @Override
    public void onBackPressed() {}
    
    public class DownloadTask extends AsyncTask<String, Integer, String> {
        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }
    
        @SuppressLint("SetTextI18n")
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            update_progress.setProgress(values[0]);
            update_progress.setMax(100);
            update_progress.setIndeterminate(false);
            int current_mb = Formatter.bytesToMB(values[2]);
            update_download_text.setText(current_mb + "MB / " + Formatter.bytesToMB(values[1]) + "MB");
        }
    
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.connect();

                String dest_path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + updateItem.package_id + "-" + updateItem.available_build_id + ".apk";
                File output = new File(dest_path);
                if(output.exists())
                    output.delete();

                FileOutputStream os = new FileOutputStream(output);
                InputStream is = c.getInputStream();

                int content_length = c.getContentLength();

                byte[] buffer = new byte[1024];
                int len1 = 0;
                long done = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    done += len1;
                    if(content_length > 0)
                        publishProgress((int) (done * 100 / content_length), content_length, (int) done);
                    os.write(buffer, 0, len1);
                }
                os.close();
                is.close();
                
                return dest_path;
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
