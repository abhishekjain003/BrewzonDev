package com.asb.utils;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asb.details.RegistrationDetail;
import com.asb.fragments.BaseFragment;
import com.asb.fragments.ProfileFragment;
import com.asb.helper.AppConstants;
import com.asb.helper.MLog;
import com.asb.helper.MethodUtills;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.Plus.PlusOptions;
import com.google.android.gms.plus.model.people.Person;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

@SuppressLint("NewApi")
public class GPlusLogin extends BaseFragment implements ConnectionCallbacks,
		OnConnectionFailedListener {

	/********************* Google Plus ***************************/

	public static final String SCOPES = "https://www.googleapis.com/auth/plus.login ";

	// Google client to interact with Google API
	public static GoogleApiClient mGoogleApiClient;

	public static final int RC_SIGN_IN = 0;

	// Profile pic image size in pixels
	public static final int PROFILE_PIC_SIZE = 400;

	/**
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	public boolean mIntentInProgress;

	public ConnectionResult mConnectionResult;

	public String gPlusToken = "";

	public static final int REQUEST_CODE_TOKEN_AUTH = 400;

	public String personPhotoUrl;

	/*****************************************************/

	public String GplusUserId, GPlusAuthcode;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	/**************************** GOOGLE PLUS *****************************/

	public void initializeGooglePlusAPI() {

		PlusOptions.Builder pl = PlusOptions.builder();
		PlusOptions options = pl.build();

		mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API, options)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
	}

	@Override
	public void onConnected(Bundle arg0) {
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {

		if (!result.hasResolution()) {

			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
					getActivity(), 0).show();
			return;
		}

		if (!mIntentInProgress) {

			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			resolveSignInError();
		}

	}

	/**
	 * Fetching user's information name, email, profile pic
	 * */
	@SuppressLint("NewApi")
	private void getProfileInformation() {
		try {

			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {

				Person currentPerson = Plus.PeopleApi
						.getCurrentPerson(mGoogleApiClient);

				final String gPlusId = currentPerson.getId();
				MLog.v("GPlus Id", ": " + gPlusId);

				final String personName = currentPerson.getDisplayName();
				MLog.v("Display Name", ": " + personName);

				personPhotoUrl = currentPerson.getImage().getUrl();
				MLog.v("Image Url", ": " + personPhotoUrl);

				String personGooglePlusProfile = currentPerson.getUrl();
				MLog.v("Person Url", ": " + personGooglePlusProfile);

				final String email = Plus.AccountApi
						.getAccountName(mGoogleApiClient);
				MLog.v("Email", ": " + email);

				AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
					@Override
					protected String doInBackground(Void... params) {

						try {

							gPlusToken = GoogleAuthUtil.getToken(getActivity(),
									Plus.AccountApi
											.getAccountName(mGoogleApiClient),
									"oauth2:" + SCOPES);

							MLog.v("GPlus Token", ": " + gPlusToken);

						} catch (IOException transientEx) {
							// Network or server error, try later

							MLog.e("TAG", transientEx.toString());
						} catch (UserRecoverableAuthException e) {
							// Recover (with e.getIntent())

							MLog.e("TAG", e.toString());
							Intent recover = e.getIntent();
							startActivityForResult(recover,
									REQUEST_CODE_TOKEN_AUTH);
						} catch (GoogleAuthException authEx) {
							// The call is not ever expected to succeed
							// assuming you have already verified that
							// Google Play services is installed.

							MLog.e("TAG", authEx.toString());
						}

						return gPlusToken;
					}

					@Override
					protected void onPostExecute(String token) {
						MLog.v("TAG", "Access token retrieved:" + token);
						gPlusToken = token;

						// by default the profile url gives 50x50 px image only
						// we can replace the value with whatever dimension we
						// want by
						// replacing sz=X
						personPhotoUrl = personPhotoUrl.substring(0,
								personPhotoUrl.length() - 2) + PROFILE_PIC_SIZE;

						RegistrationDetail detail = new RegistrationDetail();
						detail.setGplusUserId(gPlusId);
						detail.setUsername(personName);
						detail.setEmail(email);
						detail.setGPlusAuthcode(gPlusToken);
						detail.setImageUrl(personPhotoUrl);

						if (ParseUser.getCurrentUser() == null) {
							sendGPlusDetailToParse(detail);
						} else {
							MLog.v("ParseUser.getCurrentUser()", "Not NULL");

							GPlusAuthcode = detail.getGPlusAuthcode();
							GplusUserId = detail.getGplusUserId();

							MLog.v("GPlusAuthcode: " + GPlusAuthcode,
									"GplusUserId: " + GplusUserId);
						}
					}

				};
				task.execute();

			} else {
				showToast(getActivity(), "Person information is null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to resolve any signin errors
	 * */
	private void resolveSignInError() {

		MLog.v("GPlus", "hasResolution: " + mConnectionResult.hasResolution());

		if (mConnectionResult.hasResolution()) {

			try {

				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(getActivity(),
						RC_SIGN_IN);
			} catch (SendIntentException e) {

				e.printStackTrace();
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	/**
	 * Sign-in into google
	 * */
	public void signInWithGplus() {

		MLog.v("signInWithGplus",
				"isConnected: " + mGoogleApiClient.isConnected());

		if (mGoogleApiClient.isConnected()) {

			showToast(getActivity(), "User is connected!");

			// Get user's information
			getProfileInformation();
		} else if (!mGoogleApiClient.isConnected()) {

			mGoogleApiClient.connect();
		} else if (!mGoogleApiClient.isConnecting()) {

			resolveSignInError();
		}
	}

	/**
	 * Sign-out from google
	 * */
	public static void googlePlusLogout() {

		MLog.v("GPlus", "googlePlusLogout");

		if (mGoogleApiClient.isConnected()) {

			MLog.v("GPlus", "isConnected: " + mGoogleApiClient.isConnected());

			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();
		}
	}

	/**
	 * Revoking access from google
	 * */
	private void revokeGplusAccess() {

		MLog.v("GPlus", "revokeGplusAccess");

		if (mGoogleApiClient.isConnected()) {

			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);

			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
					.setResultCallback(new ResultCallback<Status>() {
						@Override
						public void onResult(Status arg0) {

							MLog.v("TAG", "User access revoked!");
							mGoogleApiClient.connect();
						}

					});
		}
	}

	private void sendGPlusDetailToParse(final RegistrationDetail detail) {

		try {

			final String pwd = MethodUtills.randomString(6);

			ParseUser objUser = new ParseUser();

			MLog.v("GPlus User Id: ", ": " + detail.getGplusUserId());
			objUser.put("GplusUserId", detail.getGplusUserId());

			MLog.v("GPlus Auth Code: ", ": " + detail.getGPlusAuthcode());
			objUser.put("GPlusAuthcode", detail.getGPlusAuthcode());

			MLog.v("User Name: ", ": " + detail.getUsername());
			objUser.setUsername(detail.getUsername());

			MLog.v("Email: ", ": " + detail.getEmail());
			objUser.setEmail(detail.getEmail());

			MLog.v("Password: ", ": " + pwd);
			objUser.setPassword(pwd);

			MLog.v("Image Url: ", ": " + detail.getImageUrl());
			objUser.put("imageUrl", detail.getImageUrl());

			objUser.signUpInBackground(new SignUpCallback() {
				public void done(ParseException e) {
					if (e == null) {
						// TODO Mail send to the user for verification
						// email.

						MLog.v("GPlus login", "Success");

						AlertDialog.Builder ad = new AlertDialog.Builder(
								getActivity());
						ad.setTitle("Alert");
						ad.setMessage("Hi "
								+ detail.getUsername()
								+ ",\nThanks for registration on Brewzon,\nYour Login Email: "
								+ detail.getEmail() + "\nPass:  " + pwd
								+ "\nClick OK to proceed your profile");

						ad.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub

										if (ParseUser.getCurrentUser() == null) {

											ParseUser.logInInBackground(
													detail.getUsername(), pwd,
													new LogInCallback() {

														@Override
														public void done(
																ParseUser arg0,
																ParseException arg1) {
															// TODO
															// Auto-generated
															// method stub

															if (ParseUser
																	.getCurrentUser() != null) {

																replaceFragment(new ProfileFragment(
																		AppConstants.GOOGLE_PLUS,
																		detail));
															}

														}
													});
										} else {

											replaceFragment(new ProfileFragment(
													AppConstants.GOOGLE_PLUS,
													detail));
										}
									}
								});

						ad.show();

					} else if (e.getMessage().equalsIgnoreCase(
							"username " + detail.getUsername()
									+ " already taken")) {

						replaceFragment(new ProfileFragment(
								AppConstants.GOOGLE_PLUS, detail));

					} else {
						// Sign up didn't succeed. Look at the
						// ParseException
						// to figure out what went wrong
						showToast(getActivity(), e.getMessage());
					}
				}
			});

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	/**********************************************************************/

}
