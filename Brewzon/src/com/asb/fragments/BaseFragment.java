package com.asb.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.asb.brewzon.R;
import com.asb.helper.Anim;
import com.asb.helper.MLog;

@SuppressLint("NewApi")
public class BaseFragment extends Fragment implements OnClickListener {

	public ProgressDialog dialog;

	public Context context;

	public Handler handler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		MLog.w("Activity", ": " + getActivity().getLocalClassName());

		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		context = getActivity();

		// prepare for a progress bar dialog
		dialog = new ProgressDialog(context);
		dialog.setTitle(getString(R.string.loading_title));
		dialog.setMessage(getString(R.string.loading_msg));
		dialog.setCancelable(true);
		dialog.setIndeterminate(true);
	}

	public void replaceFragment(Fragment nFragment) {

		// Create new fragment and transaction
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();

		// Replace whatever is in the fragment_container view with this
		// fragment,
		// and add the transaction to the back stack
		transaction.replace(R.id.frame_container, nFragment);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
		// TODO Auto-generated method stub
	}

	public void onBackClick(View view) {

		Anim.runAlphaAnimation(getActivity(), view);
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
		getActivity().runOnUiThread(new Runnable() {
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
			Anim.runAlphaAnimation(getActivity(), view);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showToast(Context context, String string) {

		Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
	}

}
