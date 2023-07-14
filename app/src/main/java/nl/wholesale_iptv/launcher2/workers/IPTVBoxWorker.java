package nl.wholesale_iptv.launcher2.workers;

import android.Manifest;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import nl.wholesale_iptv.launcher2.enums.LogTag;
import nl.wholesale_iptv.launcher2.helpers.DeviceHelper;
import nl.wholesale_iptv.launcher2.helpers.LogHelper;
import nl.wholesale_iptv.launcher2.helpers.RootShellHelper;
import nl.wholesale_iptv.launcher2.helpers.WorkerHelper;

public class IPTVBoxWorker extends Worker {
	private final Context context;

	public IPTVBoxWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
		super(context, workerParams);
		this.context = context;
	}

	@NonNull
	@Override
	public Result doWork() {
		if(!IPTVBoxWorker.canRun(context))
			return Result.failure();
		try {
			RootShellHelper.exec(new String[]{
				"ime set com.google.android.leanback.ime/com.google.leanback.ime.LeanbackImeService",
				"appops set nl.wholesale_iptv.launcher2 REQUEST_INSTALL_PACKAGES allow"
			});
		} catch(Exception e) {
			e.printStackTrace();
			LogHelper.error(LogTag.IPTV_BOX_WORKER, e);
			return Result.failure(WorkerHelper.exceptionToData(e));
		}
		try {
			RootShellHelper.grantPermissions(new String[]{
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.ACCESS_COARSE_LOCATION,
				Manifest.permission.ACCESS_FINE_LOCATION
			});
		} catch(Exception e) {
			e.printStackTrace();
			LogHelper.error(LogTag.IPTV_BOX_WORKER, e);
			return Result.failure(WorkerHelper.exceptionToData(e));
		}
		return Result.success();
	}

	public static boolean canRun(Context context) {
		return (new DeviceHelper(context)).isPrixonPrismaWithRoot();
	}
}
