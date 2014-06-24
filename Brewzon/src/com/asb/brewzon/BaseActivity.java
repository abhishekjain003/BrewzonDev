package com.asb.brewzon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.asb.helper.Anim;
import com.asb.helper.MLog;

public class BaseActivity extends Activity implements OnClickListener {

	/*********************************/

	protected ProgressDialog dialog;

	protected Context context;

	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);

		MLog.w("Activity", ": " + getLocalClassName());

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		context = BaseActivity.this;

		// prepare for a progress bar dialog
		dialog = new ProgressDialog(context);
		dialog.setTitle(getString(R.string.loading_title));
		dialog.setMessage(getString(R.string.loading_msg));
		dialog.setCancelable(true);
		dialog.setIndeterminate(true);
	}

	/**
	 * start the progress dialog if not showing.
	 */
	public void showProgressBar() {
		try {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (dialog != null && !dialog.isShowing())
						dialog.show();
				}
			}, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * dismiss the progress dialog if showing.
	 */
	public void dismissProgressBar() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onClick(View view) {
		try {
			Anim.runAlphaAnimation(this, view);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showToast(Context context, String string) {

		Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
	}

	/********************************************************************/

}
