package nl.wholesale_iptv.launcher2.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import nl.wholesale_iptv.launcher2.helpers.ApiHelper;
import nl.wholesale_iptv.launcher2.models.ApiResponse;

public class ProbeWorker extends Worker {
    private final Context context;

    public ProbeWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        System.out.println("RUNNING WORKER");
        ApiHelper apiHelper = new ApiHelper(context);
        ApiResponse res = apiHelper.probe();
        if(res.success)
            return Result.success(res.toData());
        else
            return Result.failure(res.toData());
    }
}
