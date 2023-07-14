package nl.wholesale_iptv.launcher2.services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import nl.wholesale_iptv.launcher2.enums.LogTag;
import nl.wholesale_iptv.launcher2.helpers.ApiHelper;
import nl.wholesale_iptv.launcher2.helpers.DeviceHelper;
import nl.wholesale_iptv.launcher2.helpers.LogHelper;
import nl.wholesale_iptv.launcher2.helpers.RootShellHelper;
import nl.wholesale_iptv.launcher2.workers.CheckUpdateWorker;
import nl.wholesale_iptv.launcher2.workers.ProbeWorker;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
	@Override
	public void onNewToken(@NonNull String token) {
		super.onNewToken(token);
		LogHelper.debug(LogTag.FIREBASE, "Received token: " + token);
		getSharedPreferences("_", MODE_PRIVATE).edit().putString("fcm_token", token).apply();
		Thread thread = new Thread(() -> {
			ApiHelper apiHelper = new ApiHelper(this);
			apiHelper.probe();
		});
		thread.start();
	}
	
	@Override
	public void onMessageReceived(@NonNull RemoteMessage message) {
		super.onMessageReceived(message);
		Map<String, String> data = message.getData();
		if(!data.containsKey("action")) {
			LogHelper.error(LogTag.FIREBASE, "Notification doesn't contain action: " + message.getData());
			return;
		}
		
		LogHelper.debug(LogTag.FIREBASE, "Received message: " + message.getData());
		
		DeviceHelper deviceHelper = new DeviceHelper(this);
		
		switch(Objects.requireNonNull(data.get("action"))) {
			case "probe":
				WorkManager
					.getInstance(getApplicationContext())
					.enqueue(
						new OneTimeWorkRequest
							.Builder(ProbeWorker.class)
							.setInitialDelay(1, TimeUnit.SECONDS)
							.build()
					);
				LogHelper.debug(LogTag.FIREBASE, "FCM triggered API Probe");
				break;
			case "check_update":
				WorkManager
					.getInstance(getApplicationContext())
					.enqueue(
						new OneTimeWorkRequest
							.Builder(CheckUpdateWorker.class)
							.setInitialDelay(1, TimeUnit.SECONDS)
							.build()
					);
				LogHelper.debug(LogTag.FIREBASE, "FCM triggered API Check update");
				break;
			case "reboot":
				if(!deviceHelper.hasRootAccess())
					LogHelper.error(LogTag.FIREBASE, "Cannot call reboot action on non rooted device");
				else {
					LogHelper.debug(LogTag.FIREBASE, "FCM triggered reboot");
					deviceHelper.reboot();
				}
				break;
			case "exec":
				if(!deviceHelper.hasRootAccess())
					LogHelper.error(LogTag.FIREBASE, "Cannot call exec action on non rooted device");
				else {
					String cmd = data.get("cmd");
					if(cmd == null)
						LogHelper.error(LogTag.FIREBASE, "Cannot call exec action, \"cmd\" key not found in data payload");
					else {
						try {
							RootShellHelper.exec(cmd);
							LogHelper.debug(LogTag.FIREBASE, "FCM triggered exec \"" + cmd + "\"");
						} catch (Exception e) {
							LogHelper.error(LogTag.FIREBASE, e);
						}
					}
				}
				break;
		}
	}
	
	@Nullable
	public static String getToken(Context context) {
		return context.getSharedPreferences("_", MODE_PRIVATE).getString("fcm_token", null);
	}
}
