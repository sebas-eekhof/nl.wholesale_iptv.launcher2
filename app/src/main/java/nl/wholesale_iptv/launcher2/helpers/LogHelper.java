package nl.wholesale_iptv.launcher2.helpers;

import android.util.Log;

import nl.wholesale_iptv.launcher2.enums.LogTag;

public class LogHelper {
	
	public static void debug(LogTag logTag, String message) {
		Log.d(logTag.TAG, message);
	}
	
	public static void error(LogTag logTag, String message) {
		Log.e(logTag.TAG, message);
	}
	
	public static void error(LogTag logTag, Exception e) {
		error(logTag, e.getMessage());
	}
}
