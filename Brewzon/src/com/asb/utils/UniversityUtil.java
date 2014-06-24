package com.asb.utils;

import com.asb.helper.MLog;
import com.parse.ParseUser;

public class UniversityUtil {

	public static boolean isLinked() {

		try {

			ParseUser user = ParseUser.getCurrentUser();
			String univId = user.getString("UniversityId");

			MLog.v("University Id", ": " + univId);

			if (univId == null) {

				MLog.v("University isLinked", "false");
				return false;
			} else if (univId.length() <= 0) {
				return false;
			}

			MLog.v("University isLinked", "true");

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return true;
	}

	public static boolean linked(String universityId, int startYear, int endYear) {

		try {

			ParseUser user = ParseUser.getCurrentUser();

			if (universityId != null && startYear != 0) {

				user.put("UniversityId", universityId);
				user.put("StartYear", startYear);
				user.put("EndYear", endYear);

				user.save();

				MLog.v("University linked", "true");
				return true;
			} else {

				MLog.v("University linked", "false");
				return false;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}

	public static boolean unLinked() {

		try {

			ParseUser user = ParseUser.getCurrentUser();

			user.put("UniversityId", "");
			user.put("StartYear", 0);
			user.put("EndYear", 0);

			user.save();

			MLog.v("University unLinked", "true");
			return true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		MLog.v("University unLinked", "false");
		return false;
	}
}
