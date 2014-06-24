package com.asb.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.asb.brewzon.R;
import com.asb.details.RegistrationDetail;
import com.asb.helper.AppConstants;
import com.asb.helper.AppValidations;
import com.asb.helper.LoadProfileImage;
import com.asb.helper.MLog;
import com.asb.helper.MethodUtills;
import com.asb.utils.GPlusUtill;
import com.asb.utils.ProfessionalUtil;
import com.asb.utils.UniversityUtil;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

@SuppressLint({ "ValidFragment", "NewApi" })
public class ProfileFragment extends GPlusUtill {

	private ProfilePictureView userProfilePictureView;

	private ImageView ivPhoto;

	private TextView userNameView;

	private Button bFacebook, bGooglePlus, bUniversity, bProfessional;

	private int DIALOG_ID = -1;

	private int mStartYear, mEndYear;

	private TextView tStartYear, tEndYear;

	/***************/

	private static Uri mCapturedImageURI;

	public static final int IMAGE_PICK_INDEX = 100, IMAGE_CAPTURE_INDEX = 200,
			FACEBOOK_REQ_CODE = 300;

	private ViewFlipper flipper;

	private int profileType = -1;

	private RegistrationDetail detailGPlus;

	public ProfileFragment(int pType, RegistrationDetail dGPlus) {
		profileType = pType;
		detailGPlus = dGPlus;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		getActivity().getActionBar().show();

		View rootView = inflater.inflate(R.layout.profile_fragment, container,
				false);

		inItView(rootView);

		switch (profileType) {
		case AppConstants.FACEBOOK:

			flipper.setDisplayedChild(0);
			fetchFacebookUserIfSessionActive();
			break;

		case AppConstants.GOOGLE_PLUS:

			flipper.setDisplayedChild(1);

			if (detailGPlus != null) {

				MLog.v("TAG", "UserName: " + detailGPlus.getUsername());
				userNameView.setText(detailGPlus.getUsername());

				new LoadProfileImage(getActivity(), ivPhoto)
						.execute(detailGPlus.getImageUrl());
			}
			break;

		case AppConstants.BREWZON:

			flipper.setDisplayedChild(1);

			if (detailGPlus != null) {

				MLog.v("TAG", "UserName: " + detailGPlus.getUsername());
				userNameView.setText(detailGPlus.getUsername());
			}

			break;

		default:
			break;
		}

		return rootView;
	}

	private void inItView(View rootView) {
		// TODO Auto-generated method stub

		userProfilePictureView = (ProfilePictureView) rootView
				.findViewById(R.id.userProfilePicture);

		ivPhoto = (ImageView) rootView.findViewById(R.id.iv_profile_photo);

		userNameView = (TextView) rootView.findViewById(R.id.tv_user_name);

		bFacebook = (Button) rootView.findViewById(R.id.btn_link_facebook);
		bGooglePlus = (Button) rootView.findViewById(R.id.btn_link_google_plus);
		bUniversity = (Button) rootView.findViewById(R.id.btn_link_university);
		bProfessional = (Button) rootView
				.findViewById(R.id.btn_link_professional);

		flipper = (ViewFlipper) rootView.findViewById(R.id.flipper);

		ivPhoto.setOnClickListener(this);
		userProfilePictureView.setOnClickListener(this);

		bFacebook.setOnClickListener(this);
		bGooglePlus.setOnClickListener(this);
		bUniversity.setOnClickListener(this);
		bProfessional.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);

		if (view.equals(ivPhoto) || view.equals(userProfilePictureView)) {

			// profile image clicked
			selectProfileImage();
		} else if (view.equals(bFacebook)) {

			MLog.v("FB", "Link button clicked");
			linkFacebook();
		} else if (view.equals(bGooglePlus)) {

			MLog.v("Google Plus", "Link button clicked");

			if (isLinked()) {

				showConfirmationForUnlink("Alert",
						"Do you want to unlink google plus ?",
						AppConstants.GOOGLE_PLUS);
			} else {

				if (linked()) {

					bGooglePlus.setText("Linked");
				}
			}

		} else if (view.equals(bUniversity)) {

			MLog.v("University", "Link button clicked");
			if (UniversityUtil.isLinked()) {

				showConfirmationForUnlink("Alert",
						"Do you want to unlink university ?",
						AppConstants.UNIVERSITY);
			} else {

				showUniAndProfDialog("Link University",
						getString(R.string.hint_univ_id),
						"University id can not be empty.",
						AppConstants.UNIVERSITY);
			}

		} else if (view.equals(bProfessional)) {

			MLog.v("Professional", "Link button clicked");
			if (ProfessionalUtil.isLinked()) {

				showConfirmationForUnlink("Alert",
						"Do you want to unlink professional ?",
						AppConstants.PROFESSIONAL);
			} else {

				showUniAndProfDialog("Link Professional",
						getString(R.string.hint_prof_id),
						"Work id can not be empty.", AppConstants.PROFESSIONAL);
			}

		}
	}

	private void showUniAndProfDialog(String title, String msg,
			final String val, final int type) {
		// TODO Auto-generated method stub

		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(title);
		alert.setMessage(msg);

		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.confirmation_dialog, null);

		final EditText eUnivId = (EditText) v.findViewById(R.id.et_univ_id);
		tStartYear = (TextView) v.findViewById(R.id.tv_reg_start_year);
		tEndYear = (TextView) v.findViewById(R.id.tv_reg_end_year);

		final Calendar c = Calendar.getInstance();
		mStartYear = c.get(Calendar.YEAR);
		mEndYear = c.get(Calendar.YEAR);

		MLog.v("mStartYear: " + mStartYear, "mEndYear: " + mEndYear);

		tStartYear.setText(new StringBuilder().append(mStartYear));
		tEndYear.setText(new StringBuilder().append(mEndYear));

		tStartYear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				DIALOG_ID = RegistrationFragment.START_DATE_DIALOG_ID;

				DialogFragment picker = new DatePickerFragment(DIALOG_ID);
				picker.show(getFragmentManager(), "datePicker");
			}
		});
		tEndYear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				DIALOG_ID = RegistrationFragment.END_DATE_DIALOG_ID;

				DialogFragment picker = new DatePickerFragment(DIALOG_ID);
				picker.show(getFragmentManager(), "datePicker");
			}
		});

		alert.setView(v);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				if (TextUtils.isEmpty(eUnivId.getText())) {

					eUnivId.setError(val);
					eUnivId.requestFocus();
					return;
				}

				if (mEndYear < mStartYear) {

					showToast(getActivity(), getString(R.string.v_invalid_year));
					return;
				}

				Calendar c = Calendar.getInstance();
				int currYear = c.get(Calendar.YEAR);

				if (currYear < mEndYear || currYear < mStartYear) {

					showToast(getActivity(),
							getString(R.string.v_less_curr_year));
					return;
				}

				switch (type) {
				case AppConstants.UNIVERSITY:

					if (UniversityUtil.linked(eUnivId.getText().toString(),
							mStartYear, mEndYear)) {

						bUniversity.setText("Linked");
					}
					break;

				case AppConstants.PROFESSIONAL:

					if (ProfessionalUtil.linked(eUnivId.getText().toString(),
							mStartYear, mEndYear)) {

						bProfessional.setText("Linked");
					}
					break;

				default:
					break;
				}

			}
		});

		alert.setNegativeButton("Cancel", null);
		alert.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		try {

			switch (requestCode) {

			case FACEBOOK_REQ_CODE:

				if (ParseFacebookUtils.getSession() != null) {
					MLog.d("Facebook", "Linking");
					ParseFacebookUtils.finishAuthentication(requestCode,
							resultCode, data);
				}
				break;

			case IMAGE_PICK_INDEX:

				flipper.setDisplayedChild(1);

				Uri targetImgUri = data.getData();

				String[] projImg = { MediaStore.Images.Media.DATA };
				String pickImgPath = getPath(targetImgUri, projImg);

				MLog.v("IMAGE_PICK_PATH: ", "" + getPath(targetImgUri, projImg));
				Bitmap pickBitmap = reduceImageSize(pickImgPath);

				if (pickBitmap != null) {

					Bitmap bitmap = MethodUtills.getRoundedShape(pickBitmap);
					ivPhoto.setImageBitmap(bitmap);

				}
				break;

			case IMAGE_CAPTURE_INDEX:

				flipper.setDisplayedChild(1);

				String captureImagePath = getPath(mCapturedImageURI);

				MLog.v("IMAGE_CAPTURE_PATH: ", "" + captureImagePath);
				Bitmap captureBitmap = reduceImageSize(captureImagePath);

				if (captureBitmap != null) {

					Bitmap bitmap = MethodUtills.getRoundedShape(captureBitmap);
					ivPhoto.setImageBitmap(bitmap);
				}
				break;

			default:
				break;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/*****************************************************************/
	public String getPath(Uri uri, String[] projection) {

		Cursor cursor = getActivity().managedQuery(uri, projection, null, null,
				null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public String getPath(Uri uri) {

		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getActivity().managedQuery(uri, projection, null, null,
				null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public Bitmap reduceImageSize(String mSelectedImagePath) {

		Bitmap bm = null;
		try {
			File f = new File(mSelectedImagePath);

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 150;

			// Find the correct scale value. It should be the power of 2.
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			bm = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			showToast(getActivity(), getString(R.string.v_image_size));
		}
		return bm;
	}

	/*****************************************************************/
	private void selectProfileImage() {
		// TODO Auto-generated method stub

		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity())
				.setTitle(getString(R.string.alert_title)).setMessage(
						getString(R.string.choose_option));

		alert.setPositiveButton(getString(R.string.camera),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (AppValidations.isDeviceCamaraFeature(getActivity())) {

							MLog.v("SD CARD PRESENCE: ",
									"" + AppValidations.isSdCardPresent());
							if (AppValidations.isSdCardPresent())
								TakePhoto();
							else
								showToast(getActivity(),
										getString(R.string.v_sdcard));
						} else {
							showToast(getActivity(),
									getString(R.string.v_camera));
						}

					}
				});

		alert.setNegativeButton(getString(R.string.gallery),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						Intent pickImageIntent = new Intent(
								Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

						startActivityForResult(pickImageIntent,
								IMAGE_PICK_INDEX);

					}
				});

		alert.setNeutralButton(getString(R.string.cancel), null);

		alert.show();
	}

	public void TakePhoto() {

		String fileName = "capturedImage.jpg";
		ContentValues values = new ContentValues();

		values.put(MediaStore.Images.Media.TITLE, fileName);

		mCapturedImageURI = getActivity().getContentResolver().insert(

		MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

		startActivityForResult(cameraIntent, IMAGE_CAPTURE_INDEX);
	}

	/*****************************************************************/

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case 0:

			switch (profileType) {
			case AppConstants.FACEBOOK:

				showToast(getActivity(), "Facebook logout !");

				facebookLogout();
				break;

			case AppConstants.GOOGLE_PLUS:

				showToast(getActivity(), "Google Plus logout !");

				MainFragment.googlePlusLogout();

				startLoginActivity();
				break;

			case AppConstants.BREWZON:

				showToast(getActivity(), "Brewzon logout !");

				logoutBrewzon();
				break;

			default:
				break;
			}

			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/************************* FACEBOOK **********************************/

	@Override
	public void onResume() {
		super.onResume();

		setLinkedButtons();

		switch (profileType) {
		case AppConstants.FACEBOOK:

			ParseUser currentUser = ParseUser.getCurrentUser();
			if (currentUser != null) {
				// Check if the user is currently logged
				// and show any cached content
				MLog.v("TAG", "User currently login");
				updateViewsWithProfileInfo();
			} else {
				// If the user is not logged in, go to the
				// activity showing the login view.
				MLog.v("TAG", "User currently not login");
				facebookLogout();
			}
			break;

		default:
			break;
		}
	}

	private void setLinkedButtons() {
		// TODO Auto-generated method stub
		try {

			MLog.v("FB isLinked",
					""
							+ ParseFacebookUtils.isLinked(ParseUser
									.getCurrentUser()));
			if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {

				bFacebook.setText("Linked");
			}

			MLog.v("GPlus isLinked", "" + isLinked());
			if (isLinked()) {

				bGooglePlus.setText("Linked");
			}

			MLog.v("University isLinked", "" + isLinked());
			if (UniversityUtil.isLinked()) {

				bUniversity.setText("Linked");
			}

			MLog.v("Professional isLinked", "" + isLinked());
			if (ProfessionalUtil.isLinked()) {

				bProfessional.setText("Linked");
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void fetchFacebookUserIfSessionActive() {
		// TODO Auto-generated method stub

		// Fetch Facebook user info if the session is active
		Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {

			MLog.v("TAG", "Fetch user info if session is active");
			makeMeRequest();
		}
	}

	private void makeMeRequest() {
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if (user != null) {

							JSONObject userProfile = new JSONObject();
							try {

								MLog.v("facebookId: ", ": " + user.getId());

								userProfile.put("facebookId", user.getId());

								MLog.v("firstname: ",
										": " + user.getFirstName());

								userProfile.put("firstname",
										user.getFirstName());

								MLog.v("lastname: ", ": " + user.getLastName());

								userProfile.put("lastname", user.getLastName());

								userProfile
										.put("pickurl", user.asMap().get(""));

								ParseUser currentUser = ParseUser
										.getCurrentUser();

								MLog.v("emailVerified: ", ": "
										+ user.asMap().get("verified"));

								currentUser.put("emailVerified", user.asMap()
										.get("verified"));

								if (user.getFirstName() != null) {

									currentUser.put("firstname",
											user.getFirstName());
								}

								if (user.getLastName() != null) {

									currentUser.put("lastname",
											user.getLastName());
								}

								if (user.asMap().get("email") != null) {

									MLog.v("email: ",
											": "
													+ user.asMap().get("email")
															.toString());

									currentUser.setEmail(user.asMap()
											.get("email").toString());

									currentUser.put("email",
											user.asMap().get("email")
													.toString());
								}

								MLog.v("User Name: ", ": " + user.getName());

								if (user.getName() != null) {

									currentUser.setUsername(user.getName());

									currentUser.put("username", user.getName());
								}

								MLog.v("profile: ", ": " + userProfile);

								currentUser.put("profile", userProfile);

								currentUser.saveInBackground();

							} catch (JSONException e) {
								MLog.d("TAG",
										"Error parsing returned user data.");
							}
						}
					}
				});
		request.executeAsync();
	}

	private void updateViewsWithProfileInfo() {

		ParseUser currentUser = ParseUser.getCurrentUser();

		if (currentUser.get("profile") != null) {

			JSONObject userProfile = currentUser.getJSONObject("profile");
			try {

				if (userProfile.getString("facebookId") != null) {

					String facebookId = userProfile.get("facebookId")
							.toString();

					MLog.v("TAG", "Facebook Id: " + facebookId);
					userProfilePictureView.setProfileId(facebookId);

				} else {

					MLog.v("TAG", "Default profile");

					// Show the default, blank user profile picture
					userProfilePictureView.setProfileId(null);
				}

				if (userProfile.getString("name") != null) {

					MLog.v("TAG", "User Name: " + userProfile.getString("name"));
					userNameView.setText(userProfile.getString("name"));
				} else {

					userNameView.setText("");
				}

			} catch (JSONException e) {
				MLog.d("TAG", "Error parsing saved user data.");
			}

		}
	}

	private void linkFacebook() {
		try {

			MLog.v("ParseUser curr user: ", ": "
					+ ParseUser.getCurrentUser().getUsername());

			MLog.v("FB LINK: ",
					""
							+ ParseFacebookUtils.isLinked(ParseUser
									.getCurrentUser()));

			if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {

				MLog.v("FB", "Already linked");

				showConfirmationForUnlink("Alert",
						"Do you want to unlink facebook ?",
						AppConstants.FACEBOOK);

			} else {

				ParseFacebookUtils.link(ParseUser.getCurrentUser(),
						AppConstants.fbPermissions, getActivity(),
						FACEBOOK_REQ_CODE, new SaveCallback() {

							@Override
							public void done(ParseException e) {
								MLog.d("Facebook", "Save Callback");
								if (ParseFacebookUtils.isLinked(ParseUser
										.getCurrentUser())) {
									MLog.d("Facebook", "Linked Succesfully");
									bFacebook.setText("Linked");
								} else {
									MLog.d("Facebook", "Link Failed");
								}
								if (e != null) {
									MLog.d("FacebookError", e.getCause() + " "
											+ e.getMessage());
								}

							}
						});
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void facebookLogout() {
		// MLog the user out
		ParseUser.logOut();

		startLoginActivity();
	}

	private void showConfirmationForUnlink(String title, String msg,
			final int type) {

		AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
		ad.setTitle(title);
		ad.setMessage(msg);

		ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				switch (type) {
				case AppConstants.FACEBOOK:

					ParseFacebookUtils.unlinkInBackground(
							ParseUser.getCurrentUser(), new SaveCallback() {
								@Override
								public void done(ParseException ex) {

									if (ex == null) {
										MLog.d("TAG",
												"The user is no longer associated with their Facebook account.");

										bFacebook.setText("Link to");
									} else {

										MLog.v("Unlink FB",
												"MSG: " + ex.getMessage());
									}
								}
							});
					break;

				/*****************************************/

				case AppConstants.GOOGLE_PLUS:

					if (unLinked()) {
						bGooglePlus.setText("Link to");
					}
					break;

				/*****************************************/

				case AppConstants.UNIVERSITY:

					if (UniversityUtil.unLinked()) {
						bUniversity.setText("Link to");
					}
					break;

				default:
					break;
				}
			}
		});

		ad.setNegativeButton("No", null);
		ad.show();
	}

	/***********************************************************************/

	private void logoutBrewzon() {

		startLoginActivity();
	}

	private void startLoginActivity() {
		// TODO Auto-generated method stub

		replaceFragment(new MainFragment());
	}

	/***************************************************************************/

	public class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		int dialogType;

		public DatePickerFragment(int dialogType) {
			super();
			this.dialogType = dialogType;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker

			switch (dialogType) {
			case RegistrationFragment.START_DATE_DIALOG_ID:

				DatePickerDialog startDatePickerDialog = MethodUtills
						.customDatePicker(getActivity(), DIALOG_ID,
								RegistrationFragment.START_DATE_DIALOG_ID,
								this, mStartYear, mEndYear);
				return startDatePickerDialog;

			case RegistrationFragment.END_DATE_DIALOG_ID:

				DatePickerDialog endDatePickerDialog = MethodUtills
						.customDatePicker(getActivity(), DIALOG_ID,
								RegistrationFragment.START_DATE_DIALOG_ID,
								this, mStartYear, mEndYear);
				return endDatePickerDialog;

			default:
				break;
			}

			return null;
		}

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {

			switch (dialogType) {
			case RegistrationFragment.START_DATE_DIALOG_ID:

				mStartYear = year;
				tStartYear.setText(new StringBuilder().append(mStartYear));
				break;

			case RegistrationFragment.END_DATE_DIALOG_ID:

				mEndYear = year;
				tEndYear.setText(new StringBuilder().append(mEndYear));
				break;

			default:
				break;
			}

		}
	}

}
