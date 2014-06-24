package com.asb.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.asb.brewzon.LoginActivity;
import com.asb.brewzon.R;
import com.asb.helper.AppConstants;
import com.asb.helper.MLog;
import com.asb.utils.GPlusLogin;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.RefreshCallback;

@SuppressLint("NewApi")
public class MainFragment extends GPlusLogin {

	/************************************************************/
	// FACEBOOK: https://developers.facebook.com/apps
	// GOOGLE PLUS: https://code.google.com/apis/console/
	private Button bSignInUniversity, bSignInProfessioinal, bSignUpBrewzon,
			bLoginBrewzon;

	private ImageButton btnFbLogin, btnGPlusLogin;

	public MainFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		getActivity().getActionBar().hide();

		checkIfCurrentFacebookLogin();

		View rootView = inflater.inflate(R.layout.main_login_fragment,
				container, false);

		inItView(rootView);

		return rootView;
	}

	private void inItView(View rootView) {
		// TODO Auto-generated method stub

		btnFbLogin = (ImageButton) rootView.findViewById(R.id.btn_fblogin);
		btnGPlusLogin = (ImageButton) rootView.findViewById(R.id.btn_sign_in);

		bSignInUniversity = (Button) rootView
				.findViewById(R.id.btn_sign_in_university);
		bSignInProfessioinal = (Button) rootView
				.findViewById(R.id.btn_sign_in_professional);
		bSignUpBrewzon = (Button) rootView
				.findViewById(R.id.btn_sign_up_brewzon);
		bLoginBrewzon = (Button) rootView.findViewById(R.id.btn_login_brewzon);

		btnFbLogin.setOnClickListener(this);
		btnGPlusLogin.setOnClickListener(this);

		bSignInUniversity.setOnClickListener(this);
		bSignInProfessioinal.setOnClickListener(this);
		bSignUpBrewzon.setOnClickListener(this);
		bLoginBrewzon.setOnClickListener(this);

		initializeGooglePlusAPI();
	}

	/************************/
	private void checkIfCurrentFacebookLogin() {
		// TODO Auto-generated method stub

		if (ParseUser.getCurrentUser() != null) {

			ParseUser.getCurrentUser().refreshInBackground(
					new RefreshCallback() {
						public void done(ParseObject object, ParseException e) {
							if (e == null) {
								// Success!

								// Check if there is a currently logged in user
								// and they are linked to a Facebook account.
								ParseUser currentUser = ParseUser
										.getCurrentUser();

								MLog.v("Current User", ": " + currentUser);

								if ((currentUser != null)
										&& ParseFacebookUtils
												.isLinked(currentUser)) {
									// Go to the user info activity
									showUserDetailsActivity();
								}

							} else {
								// Failure!
								MLog.v("Failure", ": " + e.getMessage());
							}
						}
					});
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);

		if (view.equals(btnFbLogin)) {

			MLog.v("Facebook", "button Clicked");
			onLoginButtonClicked();

		} else if (view.equals(btnGPlusLogin)) {

			MLog.v("Google Plus", "button Clicked");
			signInWithGplus();

		} else if (view.equals(bSignInUniversity)) {

			startActivity(new Intent(getActivity(), LoginActivity.class)
					.putExtra(AppConstants.PROFILE_TYPE,
							AppConstants.UNIVERSITY));

		} else if (view.equals(bSignInProfessioinal)) {

			startActivity(new Intent(getActivity(), LoginActivity.class)
					.putExtra(AppConstants.PROFILE_TYPE,
							AppConstants.PROFESSIONAL));

		} else if (view.equals(bSignUpBrewzon)) {

			startActivity(new Intent(getActivity(), LoginActivity.class)
					.putExtra(AppConstants.PROFILE_TYPE,
							AppConstants.BREWZON_REGISTRATION));

		} else if (view.equals(bLoginBrewzon)) {

			startActivity(new Intent(getActivity(), LoginActivity.class)
					.putExtra(AppConstants.PROFILE_TYPE, AppConstants.BREWZON));
		}
	}

	/***************************************************************************/

	/******************************** FACEBOOK *********************************/

	private void onLoginButtonClicked() {

		showProgressBar();

		ParseFacebookUtils.logIn(AppConstants.fbPermissions, getActivity(),
				new LogInCallback() {
					@Override
					public void done(ParseUser user, ParseException err) {

						dismissProgressBar();

						if (user == null) {

							MLog.d("TAG",
									"Uh oh. The user cancelled the Facebook login.");
						} else if (user.isNew()) {

							MLog.d("TAG",
									"User signed up and logged in through Facebook!");
							showUserDetailsActivity();
						} else {

							MLog.d("TAG", "User logged in through Facebook!");
							showUserDetailsActivity();
						}
					}
				});
	}

	private void showUserDetailsActivity() {

		MLog.v("TAG", "Login facebook success");
		replaceFragment(new ListingLogicFragment());
	}

	/**********************************************************************/

}
