package com.asb.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.asb.brewzon.R;
import com.asb.details.RegistrationDetail;
import com.asb.helper.AppConstants;
import com.asb.helper.MLog;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

@SuppressLint({ "NewApi", "ValidFragment" })
public class LoginFragment extends BaseFragment {

	private Button bSignIn, bSignUp;

	private EditText eEmail, ePassword;

	private int profileType = -1;

	public LoginFragment(int pType) {
		profileType = pType;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.login_fragment, container,
				false);

		inItView(rootView);

		switch (profileType) {
		case AppConstants.UNIVERSITY:

			bSignUp.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getResources().getDrawable(R.drawable.icon_1), null);
			break;

		case AppConstants.PROFESSIONAL:

			bSignUp.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getResources().getDrawable(R.drawable.icon_2), null);
			break;

		case AppConstants.BREWZON:

			bSignUp.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getResources().getDrawable(R.drawable.icon_3), null);
			break;

		default:
			break;
		}

		return rootView;
	}

	private void inItView(View rootView) {
		// TODO Auto-generated method stub

		bSignIn = (Button) rootView.findViewById(R.id.btn_sign_in);
		bSignUp = (Button) rootView.findViewById(R.id.btn_sign_up);

		eEmail = (EditText) rootView.findViewById(R.id.et_login_email);
		ePassword = (EditText) rootView.findViewById(R.id.et_login_password);

		bSignIn.setOnClickListener(this);
		bSignUp.setOnClickListener(this);
	}

	private void sendLoginToParse() {
		// TODO Auto-generated method stub

		String emailAddress = eEmail.getText().toString();
		String passwd = ePassword.getText().toString();

		ParseUser.logInInBackground(emailAddress, passwd, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException exc) {
				// TODO Auto-generated method stub
				if (user != null) {
					// login successfully
					showToast(getActivity().getApplicationContext(),
							"Congrates! You are successfully login.");

					RegistrationDetail detail = new RegistrationDetail();
					detail.setUsername(eEmail.getText().toString().split("@")[0]);
					detail.setEmail(eEmail.getText().toString());

					MLog.v("TAG", "UserName: " + detail.getUsername()
							+ "\nEmail: " + detail.getEmail());

					replaceFragment(new ProfileFragment(AppConstants.BREWZON,
							detail));

					setDefault();

				} else {
					// login failed
					showToast(getActivity().getApplicationContext(),
							"Login failed.");
					setDefault();

				}
			}
		});
	}

	private void setDefault() {

		eEmail.setText("");
		ePassword.setText("");

		eEmail.requestFocus();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);

		if (view.equals(bSignIn)) {

			if (TextUtils.isEmpty(eEmail.getText())) {

				eEmail.setError(getString(R.string.v_email_empty));
				eEmail.requestFocus();
				return;
			}

			if (TextUtils.isEmpty(ePassword.getText())) {

				ePassword.setError(getString(R.string.v_password_empty));
				ePassword.requestFocus();
				return;
			}

			sendLoginToParse();
		} else if (view.equals(bSignUp)) {

			switch (profileType) {
			case AppConstants.UNIVERSITY:

				replaceFragment(profileType);
				break;

			case AppConstants.PROFESSIONAL:

				replaceFragment(profileType);
				break;

			case AppConstants.BREWZON:

				replaceFragment(profileType);
				break;

			default:
				break;
			}
		}
	}

	private void replaceFragment(int mProfileType) {

		Fragment fragment = null;

		switch (mProfileType) {
		case AppConstants.UNIVERSITY:

			fragment = new RegistrationFragment(profileType);
			break;

		case AppConstants.PROFESSIONAL:

			fragment = new RegistrationFragment(profileType);
			break;

		case AppConstants.BREWZON:

			fragment = new RegistrationFragment(profileType);
			break;

		default:
			break;
		}

		if (fragment != null) {

			replaceFragment(fragment);
		}

	}
}
