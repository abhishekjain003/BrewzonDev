package com.asb.helper;

import java.util.Arrays;
import java.util.List;

public class AppConstants {

	public static final int FACEBOOK = 0, GOOGLE_PLUS = 1, UNIVERSITY = 2,
			PROFESSIONAL = 3, BREWZON = 4, BREWZON_REGISTRATION = 5,
			MAIN_LOGIN = 6, PROFILE = 7, LISTING_LOGIC = 8;

	public static final String PROFILE_TYPE = "profile_type";

	public static final List<String> fbPermissions = Arrays.asList("email",
			"public_profile", "user_friends");

}
