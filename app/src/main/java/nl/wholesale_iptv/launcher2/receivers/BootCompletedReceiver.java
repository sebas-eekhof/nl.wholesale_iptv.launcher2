package nl.wholesale_iptv.launcher2.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.concurrent.TimeUnit;

import nl.wholesale_iptv.launcher2.workers.ProbeWorker;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Run one time probe");
        WorkRequest workRequest = new OneTimeWorkRequest
            .Builder(ProbeWorker.class)
            .setInitialDelay(1, TimeUnit.SECONDS)
            .build();
        WorkManager.getInstance(context.getApplicationContext()).enqueue(workRequest);
    }
}
