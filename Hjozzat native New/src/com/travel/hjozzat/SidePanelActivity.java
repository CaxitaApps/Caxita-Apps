package com.travel.hjozzat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.hjozzat.support.CommonFunctions;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toolbar;

public class SidePanelActivity extends Activity {

	private Locale myLocale;
	Dialog loaderDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		loadLocale();
		loadAppBar();
		setContentView(R.layout.activity_side_panel);

		loaderDialog = new Dialog(this, android.R.style.Theme_Translucent);
		loaderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loaderDialog.getWindow().setGravity(Gravity.TOP);
		loaderDialog.setContentView(R.layout.dialog_loader);
		loaderDialog.setCancelable(false);

		if (CommonFunctions.loggedIn) {
			((LinearLayout) findViewById(R.id.ll_login))
					.setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.ll_logout))
					.setVisibility(View.VISIBLE);
		} else {
			((LinearLayout) findViewById(R.id.ll_login))
					.setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.ll_logout))
					.setVisibility(View.GONE);
		}
		
		new backLoginCheck().execute();

	}

	public void clicker(View v) {
		Intent intent = null;
		switch (v.getId()) {

		case R.id.ll_login:
			finish();
			intent = new Intent(SidePanelActivity.this, LoginActivity.class);
			intent.putExtra("url", CommonFunctions.main_url
					+ CommonFunctions.lang
					+ "/Shared/AppAccount?isRegisterReq=false");
			startActivity(intent);
			break;

		case R.id.ll_register:
			finish();
			intent = new Intent(SidePanelActivity.this, RegisterActivity.class);
			intent.putExtra("url", CommonFunctions.main_url
					+ CommonFunctions.lang
					+ "/Shared/AppAccount?isRegisterReq=true");
			startActivity(intent);
			break;

		case R.id.ll_htu:
			intent = new Intent(SidePanelActivity.this, HowToUseAcitvity.class);
			startActivity(intent);
			break;

		case R.id.ll_faq:
			intent = new Intent(SidePanelActivity.this, FAQActivity.class);
			startActivity(intent);
			break;

		case R.id.ll_about:
			intent = new Intent(SidePanelActivity.this, AboutActivity.class);
			startActivity(intent);
			break;

		case R.id.ll_contact:
			intent = new Intent(SidePanelActivity.this, ContactActivity.class);
			startActivity(intent);
			break;

		case R.id.ll_logout:
			new LogoutBackService().execute();
			break;

		case R.id.btn_call:
			String number = "tel:"
					+ getResources().getString(R.string.contact_number);
			intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse(number));
			startActivity(intent);
			break;

		case R.id.btn_email:
			String email = getResources().getString(R.string.contact_email);
			intent = new Intent(Intent.ACTION_SEND);
			intent.setType("message/rfc822");
			intent.putExtra(Intent.EXTRA_SUBJECT, "Enquiry");
			intent.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
			Intent mailer = Intent.createChooser(intent, "Send Mail");
			startActivity(mailer);
			break;
		default:
			break;
		}
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

	private class LogoutBackService extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			loaderDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			URL url = null;
			try {
				String request = CommonFunctions.main_url
						+ CommonFunctions.lang + "/MyAccountApi/AppLogOut";
				url = new URL(request);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);
				conn.setRequestMethod("GET");

				conn.setUseCaches(false);
				conn.setDoInput(true);
				conn.setDoOutput(true);

				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.setAcceptCookie(true);
				String cookie = cookieManager.getCookie(url.toString());

				conn.setRequestProperty("Cookie", cookie);

				conn.connect();
				// Get cookies from responses and save into the cookie manager
				List<String> cookieList = conn.getHeaderFields().get(
						"Set-Cookie");
				if (cookieList != null) {
					for (String cookieTemp : cookieList) {
						cookieManager.setCookie(conn.getURL().toString(),
								cookieTemp);
					}
				}

				InputStream in = new BufferedInputStream(conn.getInputStream());

				String res = convertStreamToString(in);

				System.out.println("res" + res);

				conn.disconnect();

				if (res != null)
					return res;

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				handler.sendEmptyMessage(2);
			} catch (NullPointerException e) {
				// Log exception
				e.printStackTrace();
				handler.sendEmptyMessage(3);
			} catch (IOException e) {
				// Log exception
				e.printStackTrace();
				handler.sendEmptyMessage(1);
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (result != null) {
				finish();
			}
			if (loaderDialog.isShowing())
				loaderDialog.dismiss();
			super.onPostExecute(result);
		}
	}

	private class backLoginCheck extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			HttpURLConnection urlConnection = null;
			try {
				
				URL url = new URL(CommonFunctions.main_url
						+ "en/shared/UserDetails");
				CookieManager cookieManager = CookieManager.getInstance();
				String cookie = cookieManager.getCookie(url.toString());
				Log.i("url", url.toString());
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestProperty("Cookie", cookie);
				urlConnection.setConnectTimeout(15000);
				urlConnection.setRequestMethod("GET");
				InputStream in = new BufferedInputStream(
						urlConnection.getInputStream());
				String resultString = convertStreamToString(in);
				urlConnection.disconnect();
				parseResult(resultString);
			} catch (SocketTimeoutException e) {
				// Log exception
				e.printStackTrace();
				urlConnection.disconnect();
			} catch (NullPointerException e) {
				// Log exception
				e.printStackTrace();
				urlConnection.disconnect();
			} catch (IOException e) {
				// Log exception
				e.printStackTrace();
				urlConnection.disconnect();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (CommonFunctions.loggedIn) {
				((LinearLayout) findViewById(R.id.ll_login))
						.setVisibility(View.GONE);
				((LinearLayout) findViewById(R.id.ll_logout))
						.setVisibility(View.VISIBLE);
			} else {
				((LinearLayout) findViewById(R.id.ll_login))
						.setVisibility(View.VISIBLE);
				((LinearLayout) findViewById(R.id.ll_logout))
						.setVisibility(View.GONE);
			}
			super.onPostExecute(result);
		}
		
	}

	private void parseResult(String result) {
		// Parse String to JSON object
		try {
			JSONObject json = new JSONObject(result);
			String status = json.getString("Status");
			// String name = json.getString("Name");

			if (status.equalsIgnoreCase("true")) {
				CommonFunctions.loggedIn = true;
				System.out.println("Logged in");
			} else {
				CommonFunctions.loggedIn = false;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
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

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				showAlert("There is a problem on your Network. Please try again later.");

			} else if (msg.what == 2) {

				showAlert("There is a problem on your application. Please report it.");

			} else if (msg.what == 3) {
				showAlert(getResources().getString(R.string.no_result));
			}

		}
	};

	public void showAlert(String msg) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		alertDialog.setTitle(getResources().getString(R.string.error_title));
		alertDialog.setMessage(msg);

		alertDialog.setPositiveButton(getResources().getString(R.string.ok),
				new AlertDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});

		alertDialog.setCancelable(false);
		alertDialog.show();
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
