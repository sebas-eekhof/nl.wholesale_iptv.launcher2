package nl.wholesale_iptv.launcher2.helpers;

import java.io.DataOutputStream;
import java.io.IOException;

import nl.wholesale_iptv.launcher2.BuildConfig;

public class RootShellHelper {
	public static void exec(String command) throws Exception {
		Process su = Runtime.getRuntime().exec("su");
		DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

		outputStream.writeBytes(command + "\n");
		outputStream.flush();

		outputStream.writeBytes("exit\n");
		outputStream.flush();
		su.waitFor();
		outputStream.close();
	}

	public static void exec(String[] commands) throws Exception {
		Process su = Runtime.getRuntime().exec("su");
		DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

		for(String command: commands) {
			outputStream.writeBytes(command + "\n");
			outputStream.flush();
		}

		outputStream.writeBytes("exit\n");
		outputStream.flush();
		su.waitFor();
		outputStream.close();
	}

	public static void grantPermissions(String[] permissions) throws Exception {
		Process su = Runtime.getRuntime().exec("su");
		DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

		for(String permission: permissions) {
			outputStream.writeBytes("pm grant " + BuildConfig.APPLICATION_ID + " " + permission + "\n");
			outputStream.flush();
		}

		outputStream.writeBytes("exit\n");
		outputStream.flush();
		su.waitFor();
		outputStream.close();
	}
}
