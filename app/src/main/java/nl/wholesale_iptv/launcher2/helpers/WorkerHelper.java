package nl.wholesale_iptv.launcher2.helpers;

import android.content.Context;

import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import nl.wholesale_iptv.launcher2.workers.CheckUpdateWorker;
import nl.wholesale_iptv.launcher2.workers.ProbeWorker;

public class WorkerHelper {
	public static Data exceptionToData(Exception e) {
		return new Data.Builder()
			.putString("message", e.getMessage())
			.build();
	}
	
	public static void enqueueWorkers(Context context) {
		WorkManager workManager = WorkManager.getInstance(context.getApplicationContext());
		workManager.enqueueUniquePeriodicWork(
			"check_update_worker",
			ExistingPeriodicWorkPolicy.KEEP,
			new PeriodicWorkRequest.Builder(CheckUpdateWorker.class, 15, TimeUnit.MINUTES)
				.build()
		);
		workManager.enqueueUniquePeriodicWork(
			"probe_worker",
			ExistingPeriodicWorkPolicy.KEEP,
			new PeriodicWorkRequest.Builder(ProbeWorker.class, 15, TimeUnit.MINUTES)
				.build()
		);
	}
}
