package com.asb.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertManager {

	public static void showDialog(final Context context, String title,
			String msg, String btn) {

		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		// alert.setIcon(R.drawable.app_logo);
		alert.setTitle(title);
		alert.setMessage(msg);

		alert.setPositiveButton(btn, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		alert.show();
	}
}
