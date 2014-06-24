package com.asb.helper;

import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AppValidations {

	/************************************************/
	public static boolean isSdCardPresent() {

		String state = android.os.Environment.getExternalStorageState();

		boolean sdcard_avail = state
				.equals(android.os.Environment.MEDIA_MOUNTED);
		boolean sdcard_readonly = state
				.equals(android.os.Environment.MEDIA_MOUNTED_READ_ONLY);

		if (!sdcard_avail || sdcard_readonly) {
			return false;
		} else {
			return true;
		}
	}

	/************************************************/
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

	/************************************************/
	public final static Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	public static boolean checkEmail(String email) {
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}

	/************************************************/
	public static final int MODE_PRIVATE = 0;

	public static boolean isAppRunningFirstTime(Context con) {
		SharedPreferences pref = null;
		pref = con.getSharedPreferences("RunningState", MODE_PRIVATE);
		boolean isRunningFirstTime = pref.getBoolean("KEY", true);
		if (isRunningFirstTime) {
			return true;
		} else
			return false;
	}

	/*************************************************/

	public static boolean isDeviceCamaraFeature(Context context) {

		PackageManager pm = context.getPackageManager();

		if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			return true;
		}
		return false;
	}

}
