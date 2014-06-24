package com.asb.utils;

import com.asb.helper.MLog;
import com.parse.ParseUser;

public class ProfessionalUtil {

	public static boolean isLinked() {

		try {

			ParseUser user = ParseUser.getCurrentUser();
			String workId = user.getString("WorkId");

			MLog.v("Professional Id", ": " + workId);

			if (workId == null) {

				MLog.v("Professional isLinked", "false");
				return false;
			} else if (workId.length() <= 0) {
				return false;
			}

			MLog.v("Professional isLinked", "true");

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return true;
	}

	public static boolean linked(String workId, int startYear, int endYear) {

		try {

			ParseUser user = ParseUser.getCurrentUser();

			if (workId != null && startYear != 0) {

				user.put("WorkId", workId);
				user.put("StartYear", startYear);
				user.put("EndYear", endYear);

				user.save();

				MLog.v("Professional linked", "true");
				return true;
			} else {

				MLog.v("Professional linked", "false");
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

			user.put("WorkId", "");
			user.put("StartYear", 0);
			user.put("EndYear", 0);

			user.save();

			MLog.v("Professional unLinked", "true");
			return true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		MLog.v("Professional unLinked", "false");
		return false;
	}
}
