package nl.wholesale_iptv.launcher2.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import nl.wholesale_iptv.launcher2.helpers.WorkerHelper;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        WorkerHelper.enqueueWorkers(context);
    }
}
