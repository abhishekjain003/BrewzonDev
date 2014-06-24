package com.asb.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asb.helper.MLog;
import com.parse.ParseUser;

public class GPlusUtill extends GPlusLogin {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public boolean isLinked() {

		try {

			ParseUser user = ParseUser.getCurrentUser();
			String userId = user.getString("GplusUserId");

			MLog.v("User Id", ": " + userId);

			if (userId == null) {

				MLog.v("isLinked", "false");
				return false;
			} else if (userId.length() <= 0) {
				return false;
			}

			MLog.v("isLinked", "true");

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return true;
	}

	public boolean linked() {

		try {

			ParseUser user = ParseUser.getCurrentUser();

			signInWithGplus();

			if (GplusUserId != null && GPlusAuthcode != null) {

				user.put("GplusUserId", GplusUserId);
				user.put("GPlusAuthcode", GPlusAuthcode);

				user.save();

				MLog.v("linked", "true");
				return true;
			} else {

				MLog.v("linked", "false");
				return false;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}

	public boolean unLinked() {

		try {

			ParseUser user = ParseUser.getCurrentUser();

			user.put("GplusUserId", "");
			user.put("GPlusAuthcode", "");

			user.save();

			MLog.v("unLinked", "true");
			return true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		MLog.v("unLinked", "false");
		return false;
	}
}
