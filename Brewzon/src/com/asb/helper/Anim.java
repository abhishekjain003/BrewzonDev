package com.asb.helper;

import com.asb.brewzon.R;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Anim {

	public static void runAlphaAnimation(Activity act, View view) {

		int viewId = getViewID(view);
		// load animation XML resource under res/anim
		Animation animation = AnimationUtils.loadAnimation(act, R.anim.alpha);
		if (animation == null) {
			return; // here, we don't care
		}
		// reset initialization state
		animation.reset();
		// find View by its id attribute in the XML
		View v = act.findViewById(viewId);
		// cancel any pending animation and start this one
		if (v != null) {
			v.clearAnimation();
			v.startAnimation(animation);
		}
	}

	/**
	 * 
	 * @param v
	 * @return
	 */
	private static int getViewID(View v) {

		if (v instanceof Button) {
			Button obj = (Button) v;
			return obj.getId();
		}

		if (v instanceof ImageButton) {
			ImageButton obj = (ImageButton) v;
			return obj.getId();
		}

		if (v instanceof ImageView) {
			ImageView obj = (ImageView) v;
			return obj.getId();
		}

		if (v instanceof RelativeLayout) {
			RelativeLayout obj = (RelativeLayout) v;
			return obj.getId();
		}

		if (v instanceof LinearLayout) {
			LinearLayout obj = (LinearLayout) v;
			return obj.getId();
		}

		if (v instanceof TableLayout) {
			TableLayout obj = (TableLayout) v;
			return obj.getId();
		}

		if (v instanceof TableRow) {
			TableRow obj = (TableRow) v;
			return obj.getId();
		}

		if (v instanceof FrameLayout) {
			FrameLayout obj = (FrameLayout) v;
			return obj.getId();
		}

		if (v instanceof TextView) {
			TextView obj = (TextView) v;
			return obj.getId();
		}

		if (v instanceof EditText) {
			EditText obj = (EditText) v;
			return obj.getId();
		}

		return v.getId();
	}
}
