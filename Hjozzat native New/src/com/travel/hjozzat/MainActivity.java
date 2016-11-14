package com.travel.hjozzat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.hjozzat.support.CommonFunctions;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

public class MainActivity extends Activity {

	private boolean doubleBackToExitPressedOnce = false;

	Fragment fragment;
	FragmentManager fragmentManager;
	FragmentTransaction fragmentTransaction;
	static int menu_pos = 0;

//	ImageButton menuBtn;
	ImageView ivBack;
	String lang = "en";
	TextView tvCurrency, tvToggle;
	private Locale myLocale;
	Dialog curr;
	CommonFunctions cf;

	String mainUrl;

	LinearLayout llHome, llContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		loadLocale();
		cf = new CommonFunctions(this);
		setContentView(R.layout.activity_main);
		loadAppBar();
		
		if(CommonFunctions.updateApp) {
			AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);

	        // Setting Dialog Title
	        alertDialog.setTitle("New update available");

	        // Setting Dialog Message
	        alertDialog.setMessage("New version of application is availabale, please update application to continue..");

	        // Setting OK Button
	        alertDialog.setPositiveButton(getResources().getString(R.string.ok), new AlertDialog.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//intent to move mobile settings
					final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
					try {
						finishAffinity();
					    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
					    
					} catch (android.content.ActivityNotFoundException anfe) {
						finishAffinity();
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
					}
				}});
	        alertDialog.setNegativeButton(getResources().getString(R.string.error_no_internet_close_app), new DialogInterface.OnClickListener() {
	    		
	    		@Override
	    		public void onClick(DialogInterface dialog, int which) {
	    			// TODO Auto-generated method stub
	    			finishAffinity();
	    		}
	    	});

	        // Showing Alert Message
	        alertDialog.setCancelable(false);
	        alertDialog.show();
		} else {
			fragmentManager = getFragmentManager();
			fragment = new HomeFragment();
			fragmentTransaction = fragmentManager
					.beginTransaction();
			fragmentTransaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			fragmentTransaction.replace(R.id.frame_container, fragment,
					"Home");
			fragmentTransaction.commit();
			
			new backLoginCheck().execute();
		}
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		if (CommonFunctions.modify)
			getActionBar().hide();
		else {
			getActionBar().show();
			tvCurrency.setText(CommonFunctions.strCurrency);
			new backLoginCheck().execute();
		}
		super.onRestart();
	}

	private void loadAppBar() {
		// ============== Define a Custom Header for Navigation
		// drawer=================//
		LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.header, null);

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

		llHome = (LinearLayout) findViewById(R.id.ll_home_layout);
		llContainer = (LinearLayout) findViewById(R.id.frame_container);
//		menuBtn = (ImageButton) findViewById(R.id.ib_menu);
		ivBack = (ImageView) v.findViewById(R.id.iv_back);
		tvToggle = (TextView) v.findViewById(R.id.tb_lang);
		tvCurrency = (TextView) v.findViewById(R.id.tv_currency);

//		menuBtn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent slide = new Intent(MainActivity.this,
//						SidePanelActivity.class);
//				startActivity(slide);
//			}
//		});

		ivBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llContainer.removeAllViews();
				llContainer.setVisibility(View.GONE);
				llHome.setVisibility(View.VISIBLE);
				fragment = null;
				ivBack.setVisibility(View.GONE);
//				menuBtn.setVisibility(View.VISIBLE);
			}
		});

		tvToggle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (CommonFunctions.lang.equalsIgnoreCase("en"))
					lang = "ar";
				else
					lang = "en";

				changeLang(lang);
			}
		});

		tvCurrency.setText(CommonFunctions.strCurrency);
		tvCurrency.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogCurrency();
			}
		});

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

	public void clicker(View v) {
		Intent next;
		if (cf.isConnectingToInternet()) {
			switch (v.getId()) {
			case R.id.ll_flight: {
				llHome.setVisibility(View.GONE);
				llContainer.setVisibility(View.VISIBLE);
				ivBack.setVisibility(View.VISIBLE);
//				menuBtn.setVisibility(View.GONE);
				fragment = new FlightFragment();
				fragmentTransaction = fragmentManager
						.beginTransaction();
				fragmentTransaction
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				fragmentTransaction.replace(R.id.frame_container, fragment,
						"flight");
				fragmentTransaction.commit();
				break;
			}
			case R.id.ll_hotel: {
				llHome.setVisibility(View.GONE);
				llContainer.setVisibility(View.VISIBLE);
				ivBack.setVisibility(View.VISIBLE);
//				menuBtn.setVisibility(View.GONE);
				fragment = new HotelFragment();
				fragmentTransaction = fragmentManager
						.beginTransaction();
				fragmentTransaction
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				fragmentTransaction.replace(R.id.frame_container, fragment,
						"hotel");
				fragmentTransaction.commit();
				break;
			}

			case R.id.ll_visa:
				next = new Intent(MainActivity.this, WebActivity.class);
				next.putExtra("url", CommonFunctions.main_url
						+ CommonFunctions.lang + "/shared/VisaApplication");
				startActivity(next);
				break;
			case R.id.ll_my_booking:
				next = new Intent(MainActivity.this, ManageBookingActivity.class);
				startActivity(next);
				break;
				
			case R.id.ll_call:
				String number = "tel:" + getResources().getString(R.string.contact_number);
				next = new Intent(Intent.ACTION_DIAL);
				next.setData(Uri.parse(number));
				startActivity(next);
				break;
				
			case R.id.ib_menu:
				next = new Intent(MainActivity.this, SidePanelActivity.class);
				startActivity(next);
				
			default:
				break;
			}
		}

		else
			cf.noInternetAlert();
	}

	public void changeLang(String lang) {
		if (lang.equalsIgnoreCase(""))
			return;
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

		updateTexts();
		loadAppBar();
		if (fragment != null && !fragment.getTag().equalsIgnoreCase("home")) {
			llHome.setVisibility(View.GONE);
			llContainer.setVisibility(View.VISIBLE);
			ivBack.setVisibility(View.VISIBLE);
//			menuBtn.setVisibility(View.GONE);
			if (fragment.getTag().equalsIgnoreCase("hotel")) {
				fragment = null;
				fragment = new HotelFragment();
				fragmentTransaction = fragmentManager
						.beginTransaction();
				fragmentTransaction
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				fragmentTransaction.replace(R.id.frame_container, fragment,
						"hotel");
				fragmentTransaction.commit();
			} else if (fragment.getTag().equalsIgnoreCase("flight")) {
				fragment = null;
				fragment = new FlightFragment();
				fragmentTransaction = fragmentManager
						.beginTransaction();
				fragmentTransaction
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				fragmentTransaction.replace(R.id.frame_container, fragment,
						"flight");
				fragmentTransaction.commit();
			}
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

	private void updateTexts() {
		setContentView(R.layout.activity_main);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		if (!CommonFunctions.modify) {
			if (fragment != null && !fragment.getTag().contains("Home")) {
				// add your code here
				llContainer.removeAllViews();
				llContainer.setVisibility(View.GONE);
				llHome.setVisibility(View.VISIBLE);
				fragment = null;
				ivBack.setVisibility(View.GONE);
//				menuBtn.setVisibility(View.VISIBLE);
			}

			else if (doubleBackToExitPressedOnce) {
				finishAffinity();
			} else {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.exit_msg),
						Toast.LENGTH_SHORT).show();
				doubleBackToExitPressedOnce = true;
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						doubleBackToExitPressedOnce = false;
					}
				}, 3000);
			}
		}
	}

	private void dialogCurrency() {
		curr = new Dialog(MainActivity.this, android.R.style.Theme_Translucent);
		curr.requestWindowFeature(Window.FEATURE_NO_TITLE);
		curr.getWindow().setGravity(Gravity.TOP);
		curr.setContentView(R.layout.dialog_currency);
		curr.setCancelable(false);

		final ImageView close;
		final LinearLayout llKWD, llINR, llUSD, llQAR, llEUR, llAED, llSAR, llIQD, llGBP;
		final LinearLayout llGEL, llBHD, llOMR;
		close = (ImageView) curr.findViewById(R.id.iv_close);
		llKWD = (LinearLayout) curr.findViewById(R.id.ll_KWD);
		llINR = (LinearLayout) curr.findViewById(R.id.ll_INR);
		llUSD = (LinearLayout) curr.findViewById(R.id.LL_USD);
		llQAR = (LinearLayout) curr.findViewById(R.id.ll_QAR);
		llEUR = (LinearLayout) curr.findViewById(R.id.ll_EUR);
		llAED = (LinearLayout) curr.findViewById(R.id.LL_AED);
		llSAR = (LinearLayout) curr.findViewById(R.id.LL_SAR);
		llIQD = (LinearLayout) curr.findViewById(R.id.ll_IQD);
		llGBP = (LinearLayout) curr.findViewById(R.id.ll_GBP);
		llGEL = (LinearLayout) curr.findViewById(R.id.ll_GEL);
		llBHD = (LinearLayout) curr.findViewById(R.id.ll_BHD);
		llOMR = (LinearLayout) curr.findViewById(R.id.ll_OMR);

		// default

		if (CommonFunctions.strCurrency.equalsIgnoreCase("KWD"))
			llKWD.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("INR"))
			llINR.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("USD"))
			llUSD.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("QAR"))
			llQAR.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("EUR"))
			llEUR.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("AED"))
			llAED.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("SAR"))
			llSAR.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("IQD"))
			llIQD.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("GBP"))
			llGBP.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("GEL"))
			llGEL.setBackgroundResource(R.drawable.border_with_background);
		else if (CommonFunctions.strCurrency.equalsIgnoreCase("OMR"))
			llOMR.setBackgroundResource(R.drawable.border_with_background);
		else
			llBHD.setBackgroundResource(R.drawable.border_with_background);

		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				curr.dismiss();
			}
		});

		llKWD.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border_with_background);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);

				CommonFunctions.strCurrency = "KWD";
				tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llINR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border_with_background);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);

				CommonFunctions.strCurrency = "INR";
				tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llUSD.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border_with_background);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);

				CommonFunctions.strCurrency = "USD";
				tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llQAR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border_with_background);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);

				CommonFunctions.strCurrency = "QAR";
				tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llEUR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border_with_background);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);

				CommonFunctions.strCurrency = "EUR";
				tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llAED.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border_with_background);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);

				CommonFunctions.strCurrency = "AED";
				tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llSAR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border_with_background);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);

				CommonFunctions.strCurrency = "SAR";
				tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llIQD.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border_with_background);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);

				CommonFunctions.strCurrency = "IQD";
				tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llGBP.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border_with_background);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);

				CommonFunctions.strCurrency = "GBP";
				tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llGEL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border_with_background);
				llBHD.setBackgroundResource(R.drawable.border);

				CommonFunctions.strCurrency = "GEL";
				tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llBHD.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border_with_background);

				CommonFunctions.strCurrency = "BHD";
				tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		llOMR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llKWD.setBackgroundResource(R.drawable.border);
				llINR.setBackgroundResource(R.drawable.border);
				llUSD.setBackgroundResource(R.drawable.border);
				llQAR.setBackgroundResource(R.drawable.border);
				llEUR.setBackgroundResource(R.drawable.border);
				llAED.setBackgroundResource(R.drawable.border);
				llSAR.setBackgroundResource(R.drawable.border);
				llIQD.setBackgroundResource(R.drawable.border);
				llGBP.setBackgroundResource(R.drawable.border);
				llGEL.setBackgroundResource(R.drawable.border);
				llBHD.setBackgroundResource(R.drawable.border);
				llOMR.setBackgroundResource(R.drawable.border_with_background);

				CommonFunctions.strCurrency = "OMR";
				tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		curr.show();

	}

	private class backLoginCheck extends AsyncTask<Void, Void, String>
	{

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			makePostRequest();
			return null;
		}
	}
	
	HttpURLConnection urlConnection = null;
	private void makePostRequest() 
	{
		if(cf.isConnectingToInternet())
        //making POST request.
        try {
        	URL url = new URL(CommonFunctions.main_url+"en/shared/UserDetails");
        	CookieManager cookieManager = CookieManager.getInstance();
            String cookie = cookieManager.getCookie(url.toString());
        	Log.i("url", url.toString());
        	urlConnection = (HttpURLConnection) url.openConnection();
        	urlConnection.setRequestProperty("Cookie",cookie);
        	urlConnection.setConnectTimeout(15000);
        	urlConnection.setRequestMethod("GET");
        	InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        	String resultString = convertStreamToString(in);
        	urlConnection.disconnect();
        	parseResult(resultString);
        } 
		catch(SocketTimeoutException e)
        {
        	// Log exception
            e.printStackTrace();
            urlConnection.disconnect();
        }
        catch (NullPointerException e) {
            // Log exception
            e.printStackTrace();
            urlConnection.disconnect();
        } 
        catch (IOException e) {
            // Log exception
            e.printStackTrace();
            urlConnection.disconnect();
        }
	}
	

	
	private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
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
	
	private void parseResult(String result)
	{
		// Parse String to JSON object 
		try {
			JSONObject json = new JSONObject(result); 
			String status 	= json.getString("Status");
//			String name 	= json.getString("Name");
			
			if(status.equalsIgnoreCase("true"))
			{
				CommonFunctions.loggedIn = true;
				System.out.println("Logged in");
			}
			else
			{
				CommonFunctions.loggedIn = false;
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
}
