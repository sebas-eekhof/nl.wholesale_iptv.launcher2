package nl.wholesale_iptv.launcher2.enums;

public enum LogTag {
	FIREBASE("Firebase"),
	CHECK_UPDATE("Check_update"),
	IPTV_BOX_WORKER("IPTV_box_worker"),
	CHECK_UPDATE_WORKER("Check_update_worker");
	
	public final String TAG;
	LogTag(String TAG) {
		this.TAG = "iptv_log." + TAG;
	}
}