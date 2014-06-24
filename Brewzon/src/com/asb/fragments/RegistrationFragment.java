package com.asb.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.asb.brewzon.R;
import com.asb.details.RegistrationDetail;
import com.asb.helper.AppConstants;
import com.asb.helper.AppValidations;
import com.asb.helper.GPSTracker;
import com.asb.helper.MLog;
import com.asb.helper.MethodUtills;
import com.asb.helper.PlaceJSONParser;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

@SuppressLint({ "NewApi", "ValidFragment" })
public class RegistrationFragment extends BaseFragment {

	// Obtain browser key from https://code.google.com/apis/console
	private static final String key = "AIzaSyA_bZr6xT8gRwOQeyOrCwoWK18QLBTaC4M";

	public static final int START_DATE_DIALOG_ID = 0, END_DATE_DIALOG_ID = 1;
	private int DIALOG_ID = -1;

	private int mStartYear, mEndYear;

	private Button bSignUp;

	private EditText eEmail, ePassword, eConfirmPassword, eFName, eLName;

	private AutoCompleteTextView eCityCountry;

	private PlacesTask placesTask;

	private ParserTask parserTask;

	private TextView tStartYear, tEndYear;

	private double latitude, longitude;

	private ArrayList<Double> latlon = new ArrayList<Double>();

	private int profileType = -1;

	public RegistrationFragment(int pType) {
		profileType = pType;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View rootView = inflater.inflate(R.layout.registration_fragment,
				container, false);

		inItView(rootView);
		getGPS();

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
		case AppConstants.BREWZON_REGISTRATION:

			rootView.findViewById(R.id.layout_date).setVisibility(View.GONE);

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

		bSignUp = (Button) rootView.findViewById(R.id.btn_reg_sign_up);

		eEmail = (EditText) rootView.findViewById(R.id.et_reg_email);
		ePassword = (EditText) rootView.findViewById(R.id.et_reg_password);
		eConfirmPassword = (EditText) rootView
				.findViewById(R.id.et_reg_confirm_password);
		eFName = (EditText) rootView.findViewById(R.id.et_reg_first_name);
		eLName = (EditText) rootView.findViewById(R.id.et_reg_last_name);
		tStartYear = (TextView) rootView.findViewById(R.id.tv_reg_start_year);
		tEndYear = (TextView) rootView.findViewById(R.id.tv_reg_end_year);

		/**************/
		eCityCountry = (AutoCompleteTextView) rootView
				.findViewById(R.id.et_reg_city_country);
		eCityCountry.setThreshold(1);

		eCityCountry.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				placesTask = new PlacesTask();
				placesTask.execute(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		/*****************/

		bSignUp.setOnClickListener(this);
		tStartYear.setOnClickListener(this);
		tEndYear.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);

		if (view.equals(bSignUp)) {

			if (TextUtils.isEmpty(eEmail.getText())) {

				eEmail.setError(getString(R.string.v_email_empty));
				eEmail.requestFocus();
				return;
			}

			if (!AppValidations.checkEmail(eEmail.getText().toString())) {

				eEmail.setError(getString(R.string.v_email_invalid));
				eEmail.requestFocus();
				return;
			}

			if (TextUtils.isEmpty(ePassword.getText())) {

				ePassword.setError(getString(R.string.v_password_empty));
				ePassword.requestFocus();
				return;
			}

			if (TextUtils.isEmpty(eConfirmPassword.getText())) {

				eConfirmPassword
						.setError(getString(R.string.v_confirm_password_empty));
				eConfirmPassword.requestFocus();
				return;
			}

			if (!ePassword.getText().toString().trim()
					.equals(eConfirmPassword.getText().toString().trim())) {

				showToast(getActivity(),
						getString(R.string.v_password_not_same));
				return;
			}

			if (TextUtils.isEmpty(eFName.getText())) {

				eFName.setError(getString(R.string.v_first_name_empty));
				eFName.requestFocus();
				return;
			}

			if (TextUtils.isEmpty(eLName.getText())) {

				eLName.setError(getString(R.string.v_last_name_empty));
				eLName.requestFocus();
				return;
			}

			if (profileType != AppConstants.BREWZON) {

				if (mEndYear < mStartYear) {

					showToast(getActivity(), getString(R.string.v_invalid_year));
					return;
				}

				Calendar c = Calendar.getInstance();
				int currYear = c.get(Calendar.YEAR);
				MLog.v("Curr Year", "" + currYear);

				if (currYear < mEndYear || currYear < mStartYear) {

					showToast(getActivity(),
							getString(R.string.v_less_curr_year));
					return;
				}
			}

			if (TextUtils.isEmpty(eCityCountry.getText())) {

				eCityCountry.setError(getString(R.string.v_city_country_empty));
				eCityCountry.requestFocus();
				return;
			}

			mSendRegistrationToParse();

		} else if (view.equals(tStartYear)) {

			DIALOG_ID = START_DATE_DIALOG_ID;

			DialogFragment picker = new DatePickerFragment(DIALOG_ID);
			picker.show(getFragmentManager(), "datePicker");

		} else if (view.equals(tEndYear)) {

			DIALOG_ID = END_DATE_DIALOG_ID;

			DialogFragment picker = new DatePickerFragment(DIALOG_ID);
			picker.show(getFragmentManager(), "datePicker");
		}
	}

	private void mSendRegistrationToParse() {
		// TODO Auto-generated method stub

		try {
			String verifyCode = MethodUtills.randomString(4);

			final RegistrationDetail detail = new RegistrationDetail();
			detail.setPassword(ePassword.getText().toString());
			detail.setEmail(eEmail.getText().toString());
			detail.setUsername(eEmail.getText().toString().split("@")[0]);
			detail.setFirstName(eFName.getText().toString());
			detail.setLastName(eLName.getText().toString());
			detail.setUserLocation(eCityCountry.getText().toString());
			detail.setVerificationCode(verifyCode);

			// continue registration
			ParseUser objUser = new ParseUser();
			objUser.setPassword(detail.getPassword());
			objUser.setUsername(detail.getEmail());
			objUser.setEmail(detail.getEmail());

			objUser.put("firstname", detail.getFirstName());
			objUser.put("lastname", detail.getLastName());
			objUser.put("userLocation", detail.getUserLocation());

			switch (profileType) {
			case AppConstants.UNIVERSITY:

				detail.setStartYear(Integer.parseInt(tStartYear.getText()
						.toString()));
				detail.setEndYear(Integer.parseInt(tEndYear.getText()
						.toString()));
				detail.setUniversityId(detail.getEmail());

				objUser.put("UniversityId", detail.getEmail());

				break;

			case AppConstants.PROFESSIONAL:

				detail.setStartYear(Integer.parseInt(tStartYear.getText()
						.toString()));
				detail.setEndYear(Integer.parseInt(tEndYear.getText()
						.toString()));
				detail.setWorkId(detail.getEmail());

				objUser.put("WorkId", detail.getEmail());

				break;

			case AppConstants.BREWZON:

				detail.setStartYear(0);
				detail.setEndYear(0);
				break;

			default:
				break;
			}

			objUser.put("StartYear", detail.getStartYear());
			objUser.put("EndYear", detail.getEndYear());

			objUser.put("VerificationCode", verifyCode);
			// objUser.put("emailVerified", false);
			// Todo: Fill geo point of user

			ArrayList<Double> mLatLon = getLatLonFromAddress(eCityCountry
					.getText().toString());

			MLog.v("TAG", "LatLon list size: " + mLatLon.size());

			if (!mLatLon.isEmpty()) {

				latitude = mLatLon.get(0);
				longitude = mLatLon.get(1);
			}

			ParseGeoPoint LocationPoint = new ParseGeoPoint(latitude, longitude);

			objUser.put("userLocationPoint", LocationPoint);

			objUser.signUpInBackground(new SignUpCallback() {
				public void done(ParseException e) {
					if (e == null) {
						// TODO Mail send to the user for verification
						// email.

						switch (profileType) {
						case AppConstants.UNIVERSITY:

							replaceFragment(AppConstants.UNIVERSITY, detail);

							setDefault();
							break;

						case AppConstants.PROFESSIONAL:

							replaceFragment(AppConstants.PROFESSIONAL, detail);

							setDefault();
							break;

						case AppConstants.BREWZON:

							replaceFragment(AppConstants.BREWZON, detail);

							setDefault();
							break;

						default:
							break;
						}

					} else {
						// Sign up didn't succeed. Look at the
						// ParseException
						// to figure out what went wrong
						showToast(getActivity(), e.getMessage());
						setDefault();
					}
				}
			});

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showToast(getActivity(), e.getMessage());
			// setDefault();
		}
	}

	private void replaceFragment(int mProfileType, RegistrationDetail detail) {

		Fragment fragment = null;

		switch (mProfileType) {
		case AppConstants.UNIVERSITY:

			fragment = new LoginConfirmationFragment(profileType, detail);
			break;

		case AppConstants.PROFESSIONAL:

			fragment = new LoginConfirmationFragment(profileType, detail);
			break;

		case AppConstants.BREWZON:

			fragment = new LoginConfirmationFragment(profileType, detail);
			break;

		default:
			break;
		}

		if (fragment != null) {

			replaceFragment(fragment);
		}

	}

	private void setDefault() {

		final Calendar c = Calendar.getInstance();
		mStartYear = c.get(Calendar.YEAR);
		mEndYear = c.get(Calendar.YEAR);

		MLog.v("mStartYear: " + mStartYear, "mEndYear: " + mEndYear);

		eEmail.setText("");
		ePassword.setText("");
		eConfirmPassword.setText("");
		eFName.setText("");
		eLName.setText("");
		tStartYear.setText(new StringBuilder().append(mStartYear));
		tEndYear.setText(new StringBuilder().append(mEndYear));
		eCityCountry.setText("");

		eEmail.requestFocus();
	}

	/********************************************************************/

	private void getGPS() {

		setDefault();

		try {

			GPSTracker gps = new GPSTracker(getActivity());

			latitude = gps.getLatitude();
			longitude = gps.getLongitude();

			// check if GPS enabled
			if (gps.canGetLocation()) {

				MLog.v("LOCATION", "Your Location is - \nLat: " + latitude
						+ "\nLong: " + longitude);

				new CityCountryAsyncTask().execute(String.valueOf(latitude),
						String.valueOf(longitude));

			} else {
				// can't get location
				// GPS or Network is not enabled
				// Ask user to enable GPS/network in settings
				gps.showSettingsAlert();
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/*************************** Auto Location *******************************************/

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			MLog.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	// Fetches all places from GooglePlaces AutoComplete Web Service
	private class PlacesTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... place) {
			// For storing data from web service
			String data = "";

			try {

				String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
						+ place[0] + "&types=geocode&sensor=false&key=" + key;

				MLog.v("URL: ", "API: " + url);

				// Fetching the data from we service
				data = downloadUrl(url);
			} catch (Exception e) {
				MLog.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			try {

				// Creating ParserTask
				parserTask = new ParserTask();

				// Starting Parsing the JSON string returned by Web Service
				parserTask.execute(result);

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;

		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			List<HashMap<String, String>> places = null;

			try {

				PlaceJSONParser placeJsonParser = new PlaceJSONParser();

				jObject = new JSONObject(jsonData[0]);

				// Getting the parsed data as a List construct
				places = placeJsonParser.parse(jObject);

			} catch (Exception e) {
				MLog.d("Exception", e.toString());
			}
			return places;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {

			try {

				String[] from = new String[] { "description" };
				int[] to = new int[] { android.R.id.text1 };

				// Creating a SimpleAdapter for the AutoCompleteTextView
				SimpleAdapter adapter = new SimpleAdapter(getActivity()
						.getBaseContext(), result,
						android.R.layout.simple_list_item_1, from, to);

				// Setting the adapter
				eCityCountry.setAdapter(adapter);

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	/**************************************************************/

	public class CityCountryAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			showProgressBar();
		}

		@Override
		protected String doInBackground(String... params) {

			return getAddressFromLatLon(Double.parseDouble(params[0]),
					Double.parseDouble(params[1]));
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			try {

				dismissProgressBar();

				eCityCountry.setText(result);

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	/************************************************************************/

	public ArrayList<Double> getLatLonFromAddress(String address) {

		Geocoder coder = new Geocoder(getActivity());

		try {
			ArrayList<Address> adresses = (ArrayList<Address>) coder
					.getFromLocationName(address, 1);

			if (!adresses.isEmpty()) {

				double lat = adresses.get(0).getLongitude();
				double lon = adresses.get(0).getLatitude();

				MLog.v("Lat: " + lat, "Lon: " + lon);

				latlon.add(lat);
				latlon.add(lon);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return latlon;
	}

	public String getAddressFromLatLon(double lat, double lon) {
		// TODO Auto-generated method stub

		String result = "";

		try {

			MLog.v("LAT: " + lat, "LON: " + lon);

			Geocoder geocoder = new Geocoder(getActivity()
					.getApplicationContext(), Locale.getDefault());

			List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);

			MLog.v("Addresses", "-->" + addresses);

			Address address = addresses.get(0);
			result = address.getFeatureName() + ", " + address.getLocality()
					+ ", " + address.getCountryName();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
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
			case START_DATE_DIALOG_ID:

				DatePickerDialog startDatePickerDialog = MethodUtills
						.customDatePicker(getActivity(), DIALOG_ID,
								START_DATE_DIALOG_ID, this, mStartYear,
								mEndYear);
				return startDatePickerDialog;

			case END_DATE_DIALOG_ID:

				DatePickerDialog endDatePickerDialog = MethodUtills
						.customDatePicker(getActivity(), DIALOG_ID,
								START_DATE_DIALOG_ID, this, mStartYear,
								mEndYear);
				return endDatePickerDialog;

			default:
				break;
			}

			return null;
		}

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {

			switch (dialogType) {
			case START_DATE_DIALOG_ID:

				mStartYear = year;
				tStartYear.setText(new StringBuilder().append(mStartYear));
				break;

			case END_DATE_DIALOG_ID:

				mEndYear = year;
				tEndYear.setText(new StringBuilder().append(mEndYear));
				break;

			default:
				break;
			}

		}
	}

}
