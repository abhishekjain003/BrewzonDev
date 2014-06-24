package com.asb.helper;

import android.app.Application;

import com.asb.brewzon.R;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class MyApp extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		Parse.initialize(getApplicationContext(),
				"HNBfzLLMqwcMa96HWkANFyXZFrvF79dva6PCCRey",
				"Ze23dPsgrsid4cXJs5bsjbWEaBUc977ehPcVoQ3F");

		// Set your Facebook App Id in strings.xml
		ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));

	}
}
