package nl.wholesale_iptv.launcher2.activities;

import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import nl.wholesale_iptv.launcher2.R;
import nl.wholesale_iptv.launcher2.adapters.UpdatesAdapter;
import nl.wholesale_iptv.launcher2.helpers.ApiHelper;
import nl.wholesale_iptv.launcher2.helpers.ApkHelper;
import nl.wholesale_iptv.launcher2.models.UpdateItem;

public class UpdatesActivity extends AppCompatActivity {
    private final int REQUEST_STORAGE_CODE = 99241;
    @Nullable
    private UpdateItem last_update_item = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updates);

        ListView packages_list = findViewById(R.id.update_packages_list);

        ApiHelper apiHelper = new ApiHelper(this);

        apiHelper.getAppVersionsAsync(res -> {
            if(!res.success || res.res == null) {
                runOnUiThread(() -> Toast.makeText(this, "Er is iets mis gegaan", Toast.LENGTH_SHORT).show());
                return;
            }

            ArrayList<UpdateItem> update_items = new ArrayList<>();

            try {
                JSONArray apps = res.res.getJSONArray("apps");

                PackageManager pm = getPackageManager();

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

                runOnUiThread(() -> {
                    packages_list.setOnItemClickListener((adapt, view, index, ll) -> updatePackage(update_items.get(index)));
                    packages_list.setAdapter(new UpdatesAdapter(update_items, this));
                });
            } catch(JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void updatePackage(UpdateItem item) {
        last_update_item = null;
        if(!item.hasUpdate()) return;
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_STORAGE_CODE);
            last_update_item = item;
            return;
        }
        ApkHelper apkHelper = new ApkHelper(this);
        apkHelper.downloadApk(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_STORAGE_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(last_update_item != null)
                    updatePackage(last_update_item);
            } else
                Toast.makeText(this, "Geen rechten om pakketten te updaten", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
