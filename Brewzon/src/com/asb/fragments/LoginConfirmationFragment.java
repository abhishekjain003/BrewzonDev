package com.asb.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.asb.brewzon.R;
import com.asb.details.RegistrationDetail;
import com.asb.helper.MLog;

@SuppressLint({ "NewApi", "ValidFragment" })
public class LoginConfirmationFragment extends BaseFragment {

	private TextView tLoginConfirm;

	private EditText eVerCode;

	private Button bSUbmit;

	private RegistrationDetail detail;

	private int profileType = -1;

	public LoginConfirmationFragment(int pType, RegistrationDetail mDetail) {
		profileType = pType;
		detail = mDetail;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View rootView = inflater.inflate(R.layout.login_confirmation_fragment,
				container, false);

		inItView(rootView);

		MLog.v("INTENT DATA", "UserName: " + detail.getUsername() + "\nEmail: "
				+ detail.getEmail());

		tLoginConfirm.setText(getString(R.string.hi) + " "
				+ detail.getUsername() + getString(R.string.thank_with_brewzon)
				+ " " + detail.getEmail() + " "
				+ getString(R.string.active_your_account) + " "
				+ getString(R.string.ver_code) + " "
				+ detail.getVerificationCode());

		return rootView;
	}

	private void inItView(View rootView) {
		// TODO Auto-generated method stub

		tLoginConfirm = (TextView) rootView
				.findViewById(R.id.tv_login_confirmation);

		eVerCode = (EditText) rootView.findViewById(R.id.et_ver_code);
		bSUbmit = (Button) rootView.findViewById(R.id.btn_submit);

		bSUbmit.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);

		if (view.equals(bSUbmit)) {

			if (TextUtils.isEmpty(eVerCode.getText())) {

				eVerCode.setError("Verification code can not be empty.");
				eVerCode.requestFocus();
				return;
			}

			if (detail != null) {

				if (detail.getVerificationCode().trim()
						.equals(eVerCode.getText().toString().trim())) {

					replaceFragment(new ProfileFragment(profileType, detail));

				} else {
					showToast(getActivity(), "Invalid verification code.");
					eVerCode.setText("");
				}
			}
		}
	}
}
