package nl.wholesale_iptv.launcher2.workers;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Date;

import nl.wholesale_iptv.launcher2.enums.LogTag;
import nl.wholesale_iptv.launcher2.helpers.ApiHelper;
import nl.wholesale_iptv.launcher2.helpers.LogHelper;
import nl.wholesale_iptv.launcher2.models.ApiResponse;

public class CheckUpdateWorker extends Worker {
	private final Context context;
	
	public CheckUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
		super(context, workerParams);
		this.context = context;
	}
	
	@NonNull
	@Override
	public Result doWork() {
		ApiHelper apiHelper = new ApiHelper(context);
		ApiResponse res = apiHelper.getAppVersions();
		if(!res.success || res.res == null) {
			LogHelper.error(LogTag.CHECK_UPDATE_WORKER, (res.error == null) ? "Unknown error occurred" : res.error.message);
			return Result.failure();
		}
		context.getSharedPreferences("_", Context.MODE_PRIVATE)
			.edit()
			.putString("get_versions_json", res.res.toString())
			.putLong("get_versions_time", (new Date()).getTime())
			.apply();
		context.sendBroadcast(new Intent("nl.wholesale_iptv.launcher2.check_update"));
		return Result.success();
	}
}
