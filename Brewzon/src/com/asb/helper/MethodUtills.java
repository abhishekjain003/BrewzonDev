package com.asb.helper;

import java.lang.reflect.Field;
import java.util.Random;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.View;
import android.widget.DatePicker;

public class MethodUtills {

	public static String randomString(int len) {
		String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

	/******************************************************************/

	/*
	 * Making image in circular shape
	 */
	public static Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
		// TODO Auto-generated method stub

		MLog.v("TAG", "Input Bitmap: " + scaleBitmapImage);

		int targetWidth = 100;
		int targetHeight = 100;
		Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
				Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(targetBitmap);
		Path path = new Path();
		path.addCircle(((float) targetWidth - 1) / 2,
				((float) targetHeight - 1) / 2,
				(Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
				Path.Direction.CCW);

		canvas.clipPath(path);
		Bitmap sourceBitmap = scaleBitmapImage;
		canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),
				sourceBitmap.getHeight()), new Rect(0, 0, targetWidth,
				targetHeight), null);

		MLog.v("TAG", "Output Bitmap: " + targetBitmap);
		return targetBitmap;
	}

	/**********************************************************/

	public static DatePickerDialog customDatePicker(Context context,
			int DIALOG_ID, int START_DATE_DIALOG_ID,
			DatePickerDialog.OnDateSetListener mDateSetListner, int mStartYear,
			int mEndYear) {

		DatePickerDialog dpd;

		if (DIALOG_ID == START_DATE_DIALOG_ID)
			dpd = new DatePickerDialog(context, mDateSetListner,
					mStartYear + 1, 0, 0);
		else
			dpd = new DatePickerDialog(context, mDateSetListner, mEndYear + 1,
					0, 0);

		try {

			Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
			for (Field datePickerDialogField : datePickerDialogFields) {

				if (datePickerDialogField.getName().equals("mDatePicker")) {

					datePickerDialogField.setAccessible(true);
					DatePicker datePicker = (DatePicker) datePickerDialogField
							.get(dpd);

					Field datePickerFields[] = datePickerDialogField.getType()
							.getDeclaredFields();

					for (Field datePickerField : datePickerFields) {

						if ("mDayPicker".equals(datePickerField.getName())
								|| "mDaySpinner".equals(datePickerField
										.getName())
								|| "mMonthPicker".equals(datePickerField
										.getName())
								|| "mMonthSpinner".equals(datePickerField
										.getName())) {

							datePickerField.setAccessible(true);
							Object dayPicker = new Object();
							dayPicker = datePickerField.get(datePicker);
							((View) dayPicker).setVisibility(View.GONE);
						}
					}
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dpd;
	}

}
