package nl.wholesale_iptv.launcher2.workers;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.DataOutputStream;

import nl.wholesale_iptv.launcher2.helpers.DeviceHelper;

public class KeyboardWorker extends Worker {
    private final Context context;

    public KeyboardWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            outputStream.writeBytes("ime set com.google.android.leanback.ime/com.google.leanback.ime.LeanbackImeService\n");
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            su.waitFor();
            outputStream.close();
            return Result.success();
        } catch(Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }

    public static boolean canRun(Context context) {
        DeviceHelper deviceHelper = new DeviceHelper(context);
        return (deviceHelper.isPrixonPrisma() && deviceHelper.hasRootAccess());
    }
}
