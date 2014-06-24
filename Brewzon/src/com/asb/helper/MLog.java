package com.asb.helper;

import android.util.Log;

import com.asb.brewzon.BuildConfig;

public class MLog {

	public static void v(String TAG, String MSG) {
		if (BuildConfig.DEBUG)
			Log.v(TAG, MSG);
	}

	public static void w(String TAG, String MSG) {
		if (BuildConfig.DEBUG)
			Log.w(TAG, MSG);
	}

	public static void d(String TAG, String MSG) {
		if (BuildConfig.DEBUG)
			Log.d(TAG, MSG);
	}

	public static void e(String TAG, String MSG) {
		if (BuildConfig.DEBUG)
			Log.e(TAG, MSG);
	}

	public static void i(String TAG, String MSG) {
		if (BuildConfig.DEBUG)
			Log.i(TAG, MSG);
	}
}
