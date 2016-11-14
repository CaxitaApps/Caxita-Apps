package com.travel.hjozzat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hjozzat.support.CommonFunctions;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

public class RegisterActivity extends Activity {

	private Locale myLocale;
	EditText etFirstName, etLastName, etPhoneNo, etEmailAddress, etPassword,
			etConfirmPassword;
	Spinner spCountrycode;
	String[] Countrycode;
	JSONObject json;
	JSONArray postjson;
	String fname, lname, ccode, phneno, emailaddr, pass, cpass;

	Boolean fromPax = false;
	Dialog loaderDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		loadLocale();
		setContentView(R.layout.activity_register);

		loadAppBar();
		initilize();

	}

	private void loadAppBar() {
		// ============== Define a Custom Header for Navigation
		// drawer=================//
		LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.header_1, null);

		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setCustomView(v);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			try {
				Toolbar parent = (Toolbar) v.getParent();
				parent.setContentInsetsAbsolute(0, 0);
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}

		ImageView backBtn = (ImageView) v.findViewById(R.id.iv_back);
		ImageView homeBtn = (ImageView) v.findViewById(R.id.iv_home);
		homeBtn.setVisibility(View.INVISIBLE);

		// homeBtn.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// finish();
		// Intent intent = new Intent(SidePanelActivity.this,
		// MainActivity.class);
		// startActivity(intent);
		// }
		// });

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	@SuppressWarnings("unchecked")
	private void initilize() {

		loaderDialog = new Dialog(this, android.R.style.Theme_Translucent);
		loaderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loaderDialog.getWindow().setGravity(Gravity.TOP);
		loaderDialog.setContentView(R.layout.dialog_loader);
		loaderDialog.setCancelable(false);
		
		etFirstName = (EditText) findViewById(R.id.edt_fname);
		etLastName = (EditText) findViewById(R.id.edt_lname);
		etPhoneNo = (EditText) findViewById(R.id.edt_phoneno);
		etEmailAddress = (EditText) findViewById(R.id.edt_emailaddress);
		etPassword = (EditText) findViewById(R.id.edt_password);
		spCountrycode = (Spinner) findViewById(R.id.spn_countrycode);
		etConfirmPassword = (EditText) findViewById(R.id.edt_cpassword);

		fromPax = getIntent().getBooleanExtra("from_pax", false);
		
		final String blockCharacterSet = " ";

		InputFilter filter = new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				// TODO Auto-generated method stub
				if (source != null && blockCharacterSet.contains(("" + source))) {
					return "";
				}
				return null;
			}
		};
		
		etEmailAddress.setFilters(new InputFilter[] { filter });
		final String blockCharacterSet1 = " `~!@#$%^&*()_={}|[]:'<>?,/*';\\¡¢£¤¥¦§¨©ª«®¯°±²³´µ¶·¹º»¼½¾¿÷×€ƒ¬™";
		filter = new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				// TODO Auto-generated method stub
				if (source != null && blockCharacterSet1.contains(("" + source))) {
					return "";
				}
				return null;
			}
		};
		etPhoneNo.setFilters(new InputFilter[] { filter });

		Countrycode = new String[] { "+1", "+1 284", "+1 340", "+1 345",
				"+1 649", "+1 670", "+1 758", "+1 784", "+1 869", "+1242",
				"+1246", "+1264", "+1268", "+1441", "+1473", "+1664", "+1671",
				"+1684", "+1767", "+1809", "+1876", "+20", "+212", "+213",
				"+216", "+218", "+220", "+221", "+222", "+223", "+224", "+225",
				"+226", "+227", "+228", "+229", "+230", "+231", "+232", "+233",
				"+234", "+235", "+236", "+237", "+238", "+239", "+240", "+241",
				"+242", "+243", "+244", "+245", "+246", "+248", "+249", "+250",
				"+251", "+252", "+253", "+254", "+255", "+256", "+257", "+258",
				"+260", "+261", "+262", "+263", "+264", "+265", "+266", "+267",
				"+268", "+269", "+27", "+290", "+291", "+297", "+298", "+299",
				"+30", "+31", "+32", "+33", "+34", "+350", "+351", "+352",
				"+353", "+354", "+355", "+356", "+357", "+358", "+359", "+36",
				"+370", "+371", "+372", "+373", "+374", "+375", "+376", "+378",
				"+380", "+381", "+382", "+385", "+386", "+387", "+389", "+39",
				"+40", "+41", "+420", "+421", "+423", "+43", "+44", "+45",
				"+46", "+47", "+48", "+49", "+500", "+501", "+502", "+503",
				"+504", "+505", "+506", "+507", "+508", "+509", "+51", "+52",
				"+53", "+54", "+55", "+56", "+57", "+58", "+590", "+591",
				"+592", "+593", "+594", "+595", "+596", "+597", "+598", "+599",
				"+60", "+61", "+62", "+63", "+64", "+65", "+66", "+670",
				"+672", "+673", "+674", "+675", "+676", "+677", "+678", "+679",
				"+680", "+681", "+682", "+683", "+685", "+686", "+687", "+688",
				"+689", "+690", "+691", "+692", "+699", "+7", "+81", "+82",
				"+84", "+850", "+852", "+853", "+855", "+856", "+86", "+880",
				"+886", "+90", "+91", "+92", "+93", "+94", "+95", "+960",
				"+961", "+962", "+963", "+964", "+965", "+966", "+967", "+968",
				"+970", "+971", "+972", "+973", "+974", "+975", "+976", "+977",
				"+98", "+992", "+993", "+994", "+995", "+996", "+998" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.tv_spinner, Countrycode);
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		spCountrycode.setAdapter(adapter);
		spCountrycode
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						ccode = spCountrycode.getSelectedItem().toString();
						Log.e("Countrycode", ccode);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});
		spCountrycode.setSelection(((ArrayAdapter<String>) spCountrycode.getAdapter())
				.getPosition("+965"));
	}

	public void clicker(View v) {
		switch (v.getId()) {
		case R.id.btn_register:
			if (validate()) {
				onValidationSuccess();
			}
			break;

		case R.id.tv_signin:
			finish();
			if (!fromPax) {
				Intent login = new Intent(getApplicationContext(),
						LoginActivity.class);
				startActivity(login);
			}
			break;

		default:
			break;
		}
	}

	private void onValidationSuccess() {

		String url = CommonFunctions.main_url + CommonFunctions.lang
				+ "/MyAccountApi/AppUserNameExistCheck?UserId="
				+ etEmailAddress.getText().toString() + "";

		RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
		JsonObjectRequest jsonObjReq = new JsonObjectRequest(
				Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							Log.e("response", response.toString());
							String IsValid = response.getString("IsValid");

							if (IsValid.equals("true")) {
								Log.e("IsValid", "True");

								new RegisterBackService().execute();

							} else {

								etEmailAddress
										.setError(getResources()
												.getString(
														R.string.error_msg_email_alrdy_reg));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					public void onErrorResponse(VolleyError error) {

						Toast.makeText(
								getApplicationContext(),
								"Warning: No match for E-Mail Address and/or Password.",
								Toast.LENGTH_SHORT).show();
						VolleyLog.d("TAg", "Error: " + error.getMessage()); // hide
																			// the
																			// progress
																			// dialog
					}
				});
		queue.add(jsonObjReq);

	}

	public boolean validate() {
		boolean valid = true;
		fname = etFirstName.getText().toString();
		lname = etLastName.getText().toString();
		phneno = etPhoneNo.getText().toString();
		emailaddr = etEmailAddress.getText().toString();
		pass = etPassword.getText().toString();
		cpass = etConfirmPassword.getText().toString();
		
		if (fname.isEmpty()) {
			etFirstName.setError(getString(R.string.error_first_name_req));
			valid = false;
		} else if (fname.length() < 2) {
			etFirstName.setError(getString(R.string.error_firstname_length));
			valid = false;
		} else {
			etFirstName.setError(null);
		}

		if (lname.isEmpty()) {
			etLastName.setError(getString(R.string.error_last_name_req));
			valid = false;
		} else if (lname.length() < 2) {
			etLastName.setError(getString(R.string.error_lastname_length));
			valid = false;
		} else {
			etLastName.setError(null);
		}

		if (phneno.isEmpty()) {
			etPhoneNo.setError(getString(R.string.error_phn_no_req));
			valid = false;
		} else if (phneno.length() < 5) {
			etPhoneNo.setError(getString(R.string.error_invalid_number));
			valid = false;
		} else {
			etPhoneNo.setError(null);
		}
		if (emailaddr.isEmpty()
				|| !android.util.Patterns.EMAIL_ADDRESS.matcher(emailaddr)
						.matches()) {
			etEmailAddress.setError(getString(R.string.error_invalid_email));
			valid = false;
		} else {
			etEmailAddress.setError(null);
		}

		if (pass.isEmpty()) {
			etPassword.setError(getString(R.string.error_invalid_pass));
			valid = false;
		} else {
			etPassword.setError(null);
		}

		if (cpass.isEmpty()) {
			etConfirmPassword
					.setError(getString(R.string.error_confirm_pass_req));
			valid = false;
		} else {
			etConfirmPassword.setError(null);
		}

		if (valid && !pass.matches(cpass)) {
			etConfirmPassword.setError(getString(R.string.error_pass_mismatch));
			valid = false;
		} else if (valid) {
			etPassword.setError(null);
			etConfirmPassword.setError(null);
		}
		return valid;
	}

	private class RegisterBackService extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			loaderDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			URL url = null;
			try {
				json = new JSONObject();

				json.put("FirstName", fname);
				json.put("LastName", lname);
				json.put("MobileNumber", phneno);
				json.put("CountryCode", ccode);
				json.put("MiddleName", "");
				json.put("Email", emailaddr);
				json.put("Password", pass);
				json.put("ConfirmPassword", cpass);

				postjson = new JSONArray();
				postjson.put(json);

				String urlParameters = "RegisterDataString=" + json.toString();
				System.out.print(urlParameters);

				String request = CommonFunctions.main_url
						+ CommonFunctions.lang + "/MyAccountApi/AppRegister";
				url = new URL(request);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");

				conn.setRequestProperty("Content-Length",
						"" + Integer.toString(urlParameters.getBytes().length));
				conn.setRequestProperty("Content-Language", "en-US");

				conn.setUseCaches(false);
				conn.setDoInput(true);
				conn.setDoOutput(true);

				DataOutputStream wr = new DataOutputStream(
						conn.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.flush();
				wr.close();

				InputStream in = new BufferedInputStream(conn.getInputStream());

				String res = convertStreamToString(in);

				System.out.println("res" + res);

				conn.disconnect();

				JSONObject json = new JSONObject(res);

				if (json.getBoolean("IsRegistered")) {

					return json.getString("IsRegistered");
				} else {
					
				}

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if(loaderDialog.isShowing())
				loaderDialog.dismiss();
			
			if (result.equalsIgnoreCase("true")) {
				finish();
				if(!fromPax) {
					Intent i = new Intent(RegisterActivity.this,
							LoginActivity.class);
					startActivity(i);
				}
				Toast.makeText(
						getApplicationContext(),
						getResources().getString(R.string.success_msg_register),
						Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}

		private String convertStreamToString(InputStream is) {
			/*
			 * To convert the InputStream to String we use the
			 * BufferedReader.readLine() method. We iterate until the
			 * BufferedReader return null which means there's no more data to
			 * read. Each line will appended to a StringBuilder and returned as
			 * String.
			 */
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return sb.toString();
		}

	}

	private void loadLocale() {
		// TODO Auto-generated method stub
		SharedPreferences sharedpreferences = this.getSharedPreferences(
				"CommonPrefs", Context.MODE_PRIVATE);
		String lang = sharedpreferences.getString("Language", "en");
		System.out.println("Default lang: " + lang);
		if (lang.equalsIgnoreCase("ar")) {
			myLocale = new Locale(lang);
			saveLocale(lang);
			Locale.setDefault(myLocale);
			android.content.res.Configuration config = new android.content.res.Configuration();
			config.locale = myLocale;
			this.getBaseContext()
					.getResources()
					.updateConfiguration(
							config,
							this.getBaseContext().getResources()
									.getDisplayMetrics());
			CommonFunctions.lang = "ar";
		} else {
			myLocale = new Locale(lang);
			saveLocale(lang);
			Locale.setDefault(myLocale);
			android.content.res.Configuration config = new android.content.res.Configuration();
			config.locale = myLocale;
			this.getBaseContext()
					.getResources()
					.updateConfiguration(
							config,
							this.getBaseContext().getResources()
									.getDisplayMetrics());
			CommonFunctions.lang = "en";
		}
	}

	public void saveLocale(String lang) {
		CommonFunctions.lang = lang;
		String langPref = "Language";
		SharedPreferences prefs = this.getSharedPreferences("CommonPrefs",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(langPref, lang);
		editor.commit();
	}
	
}
