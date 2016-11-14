package com.travel.hjozzat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hjozzat.support.CommonFunctions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.CookieManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class FlightPaymentActivity extends Activity {

	private Locale myLocale;
	String strSessionId = null;
	String sID = null, strJson, deepUrl, strCurrency, proceedUrl = null;
	String confirmMsg = null, confirmMsg1 = null;
	boolean blIsRoundTrip;
	JSONObject jobj, jObj;
	ImageView ivKnet, ivMigs;

	Dialog loaderDialog;
	
	String selectedPayment = "2";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		loadLocale();
		setContentView(R.layout.activity_flight_payment);
		setHeader();
		
		loaderDialog = new Dialog(FlightPaymentActivity.this,
				android.R.style.Theme_Translucent);
		loaderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loaderDialog.getWindow().setGravity(Gravity.TOP);
		loaderDialog.setContentView(R.layout.dialog_loader);
		loaderDialog.setCancelable(false);
		((ImageView) loaderDialog.findViewById(R.id.iv_loader))
		.setImageResource(R.drawable.flight_loader);
		
		ivKnet = (ImageView) findViewById(R.id.iv_knet);
		ivMigs = (ImageView) findViewById(R.id.iv_migs);

		OnClickListener payMethodListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.iv_knet:
					String temp = String.format(new Locale("en"), "%.3f",
							ServKnet);
					((TextView) findViewById(R.id.tv_trans_fees))
							.setText(strCurrency + " " + temp);

					temp = String.format(new Locale("en"), "%.3f",
							totalPriceKnet);
					((TextView) findViewById(R.id.tv_total_price))
							.setText(strCurrency + " " + temp);
					selectedPayment = "2";
					
					ivKnet.setBackgroundResource(R.drawable.orange_button_curved_edge);
					ivMigs.setBackgroundColor(Color.TRANSPARENT);
					break;
				case R.id.iv_migs:
					temp = String.format(new Locale("en"), "%.3f", servMigs);
					((TextView) findViewById(R.id.tv_trans_fees))
							.setText(strCurrency + " " + temp);

					temp = String.format(new Locale("en"), "%.3f",
							totalPriceMigs);
					((TextView) findViewById(R.id.tv_total_price))
							.setText(strCurrency + " " + temp);
					selectedPayment = "8";

					ivKnet.setBackgroundColor(Color.TRANSPARENT);
					ivMigs.setBackgroundResource(R.drawable.orange_button_curved_edge);
					break;
				default:
					break;
				}
			}
		};
		
		ivKnet.setOnClickListener(payMethodListener);
		ivMigs.setOnClickListener(payMethodListener);
		
		strJson = getIntent().getExtras().getString("json", "");
		try {
			jobj = new JSONObject(strJson);
			deepUrl = jobj.getString("DeeplinkUrl");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showSummary();

	}

	private void setHeader() {
		// TODO Auto-generated method stub
		Bundle bundle = FlightResultActivity.bundle;
		
		LinearLayout llHeader = (LinearLayout) findViewById(R.id.ll_header_city);
		TextView tvFlightDates = (TextView) findViewById(R.id.tv_date);
		
		String strFlightType = bundle.getString("flight_type", "");
		int tripNo = bundle.getInt("trip_nos");

		Resources resources = getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		int px = 5 * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
		LayoutParams llParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		llParams.setMargins(px, px, px, px);

		if (!strFlightType.equalsIgnoreCase("Multicity")) {

			String strFromCode = bundle.getString("from1", "");
			String strToCode = bundle.getString("to1", "");

			boolean isRoundTrip = strFlightType.equalsIgnoreCase("RoundTrip") ? true : false;
			
			TextView tvFrom = new TextView(this);
			tvFrom.setLayoutParams(llParams);
			tvFrom.setText(strFromCode);
			tvFrom.setTypeface(Typeface.create("sans-serif-condensed",
					Typeface.NORMAL));
			tvFrom.setTextColor(Color.WHITE);
			tvFrom.setTextSize(16f);

			ImageView ivFrom = new ImageView(this);
			ivFrom.setLayoutParams(llParams);
			ivFrom.setImageResource(isRoundTrip ? R.drawable.ic_from_to
					: R.drawable.ic_from);
			ivFrom.setRotation(CommonFunctions.lang.equalsIgnoreCase("en") ? 0
					: 180f);

			TextView tvTo = new TextView(this);
			tvTo.setLayoutParams(llParams);
			tvTo.setText(strToCode);
			tvTo.setTypeface(Typeface.create("sans-serif-condensed",
					Typeface.NORMAL));
			tvTo.setTextColor(Color.WHITE);
			tvTo.setTextSize(16f);

			llHeader.addView(tvFrom);
			llHeader.addView(ivFrom);
			llHeader.addView(tvTo);

			int padding = 30 * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
			((LinearLayout) findViewById(R.id.ll_header)).setPadding(0, 0,
					padding, 0);

		} else {
			String temp1, temp2, temp3, temp4;
			temp1 = bundle.getString("from1", "");
			temp2 = bundle.getString("to1", "");
			temp3 = bundle.getString("from2", "");
			temp4 = bundle.getString("to2", "");
			if (tripNo == 2) {

				TextView tvFrom1 = new TextView(this);
				tvFrom1.setLayoutParams(llParams);
				tvFrom1.setText(temp1);
				tvFrom1.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvFrom1.setTextColor(Color.WHITE);
				tvFrom1.setTextSize(16f);

				ImageView ivFrom1 = new ImageView(this);
				ivFrom1.setLayoutParams(llParams);
				ivFrom1.setImageResource(R.drawable.ic_from);

				TextView tvTo1 = new TextView(this);
				tvTo1.setLayoutParams(llParams);
				tvTo1.setText(temp2 + ",");
				tvTo1.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvTo1.setTextColor(Color.WHITE);
				tvTo1.setTextSize(16f);

				TextView tvFrom2 = new TextView(this);
				tvFrom2.setLayoutParams(llParams);
				tvFrom2.setText(temp3);
				tvFrom2.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvFrom2.setTextColor(Color.WHITE);
				tvFrom2.setTextSize(16f);

				ImageView ivFrom2 = new ImageView(this);
				ivFrom2.setLayoutParams(llParams);
				ivFrom2.setImageResource(R.drawable.ic_from);

				TextView tvTo2 = new TextView(this);
				tvTo2.setLayoutParams(llParams);
				tvTo2.setText(temp4);
				tvTo2.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvTo2.setTextColor(Color.WHITE);
				tvTo2.setTextSize(16f);

				llHeader.addView(tvFrom1);
				llHeader.addView(ivFrom1);
				llHeader.addView(tvTo1);
				llHeader.addView(tvFrom2);
				llHeader.addView(ivFrom2);
				llHeader.addView(tvTo2);

				int margin = 30 * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
				((LinearLayout) findViewById(R.id.ll_header)).setPadding(0, 0,
						margin, 0);

			} else if (tripNo == 3) {

				px = 3 * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
				llParams.setMargins(px, px, px, px);

				TextView tvFrom1 = new TextView(this);
				tvFrom1.setLayoutParams(llParams);
				tvFrom1.setText(temp1);
				tvFrom1.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvFrom1.setTextColor(Color.WHITE);
				tvFrom1.setTextSize(15f);

				ImageView ivFrom1 = new ImageView(this);
				ivFrom1.setLayoutParams(llParams);
				ivFrom1.setImageResource(R.drawable.ic_from);

				TextView tvTo1 = new TextView(this);
				tvTo1.setLayoutParams(llParams);
				tvTo1.setText(temp2 + ",");
				tvTo1.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvTo1.setTextColor(Color.WHITE);
				tvTo1.setTextSize(15f);

				TextView tvFrom2 = new TextView(this);
				tvFrom2.setLayoutParams(llParams);
				tvFrom2.setText(temp3);
				tvFrom2.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvFrom2.setTextColor(Color.WHITE);
				tvFrom2.setTextSize(15f);

				ImageView ivFrom2 = new ImageView(this);
				ivFrom2.setLayoutParams(llParams);
				ivFrom2.setImageResource(R.drawable.ic_from);

				TextView tvTo2 = new TextView(this);
				tvTo2.setLayoutParams(llParams);
				tvTo2.setText(temp4 + ",");
				tvTo2.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvTo2.setTextColor(Color.WHITE);
				tvTo2.setTextSize(15f);

				TextView tvFrom3 = new TextView(this);
				tvFrom3.setLayoutParams(llParams);
				tvFrom3.setText(bundle.getString("from3", ""));
				tvFrom3.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvFrom3.setTextColor(Color.WHITE);
				tvFrom3.setTextSize(15f);

				ImageView ivFrom3 = new ImageView(this);
				ivFrom3.setLayoutParams(llParams);
				ivFrom3.setImageResource(R.drawable.ic_from);

				TextView tvTo3 = new TextView(this);
				tvTo3.setLayoutParams(llParams);
				tvTo3.setText(bundle.getString("to3", ""));
				tvTo3.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvTo3.setTextColor(Color.WHITE);
				tvTo3.setTextSize(15f);

				llHeader.addView(tvFrom1);
				llHeader.addView(ivFrom1);
				llHeader.addView(tvTo1);
				llHeader.addView(tvFrom2);
				llHeader.addView(ivFrom2);
				llHeader.addView(tvTo2);
				llHeader.addView(tvFrom3);
				llHeader.addView(ivFrom3);
				llHeader.addView(tvTo3);

				int margin = 10 * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
				((LinearLayout) findViewById(R.id.ll_header)).setPadding(0, 0,
						margin, 0);

			} else {
				px = 2 * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
				llParams.setMargins(px, px, px, px);

				TextView tvFrom1 = new TextView(this);
				tvFrom1.setLayoutParams(llParams);
				tvFrom1.setText(temp1);
				tvFrom1.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvFrom1.setTextColor(Color.WHITE);
				tvFrom1.setTextSize(14f);

				ImageView ivFrom1 = new ImageView(this);
				ivFrom1.setLayoutParams(llParams);
				ivFrom1.setImageResource(R.drawable.ic_from);

				TextView tvTo1 = new TextView(this);
				tvTo1.setLayoutParams(llParams);
				tvTo1.setText(temp2 + ",");
				tvTo1.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvTo1.setTextColor(Color.WHITE);
				tvTo1.setTextSize(14f);

				TextView tvFrom2 = new TextView(this);
				tvFrom2.setLayoutParams(llParams);
				tvFrom2.setText(temp3);
				tvFrom2.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvFrom2.setTextColor(Color.WHITE);
				tvFrom2.setTextSize(14f);

				ImageView ivFrom2 = new ImageView(this);
				ivFrom2.setLayoutParams(llParams);
				ivFrom2.setImageResource(R.drawable.ic_from);

				TextView tvTo2 = new TextView(this);
				tvTo2.setLayoutParams(llParams);
				tvTo2.setText(temp4 + ",");
				tvTo2.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvTo2.setTextColor(Color.WHITE);
				tvTo2.setTextSize(14f);

				TextView tvFrom3 = new TextView(this);
				tvFrom3.setLayoutParams(llParams);
				tvFrom3.setText(bundle.getString("from3", ""));
				tvFrom3.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvFrom3.setTextColor(Color.WHITE);
				tvFrom3.setTextSize(14f);

				ImageView ivFrom3 = new ImageView(this);
				ivFrom3.setLayoutParams(llParams);
				ivFrom3.setImageResource(R.drawable.ic_from);

				TextView tvTo3 = new TextView(this);
				tvTo3.setLayoutParams(llParams);
				tvTo3.setText(bundle.getString("to3", "")
						+ ",");
				tvTo3.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvTo3.setTextColor(Color.WHITE);
				tvTo3.setTextSize(14f);

				TextView tvFrom4 = new TextView(this);
				tvFrom4.setLayoutParams(llParams);
				tvFrom4.setText(bundle.getString("from3", ""));
				tvFrom4.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvFrom4.setTextColor(Color.WHITE);
				tvFrom4.setTextSize(14f);

				ImageView ivFrom4 = new ImageView(this);
				ivFrom4.setLayoutParams(llParams);
				ivFrom4.setImageResource(R.drawable.ic_from);

				TextView tvTo4 = new TextView(this);
				tvTo4.setLayoutParams(llParams);
				tvTo4.setText(bundle.getString("to3", ""));
				tvTo4.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvTo4.setTextColor(Color.WHITE);
				tvTo4.setTextSize(14f);

				llHeader.addView(tvFrom1);
				llHeader.addView(ivFrom1);
				llHeader.addView(tvTo1);
				llHeader.addView(tvFrom2);
				llHeader.addView(ivFrom2);
				llHeader.addView(tvTo2);
				llHeader.addView(tvFrom3);
				llHeader.addView(ivFrom3);
				llHeader.addView(tvTo3);
				llHeader.addView(tvFrom4);
				llHeader.addView(ivFrom4);
				llHeader.addView(tvTo4);

				int margin = 5 * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
				((LinearLayout) findViewById(R.id.ll_header)).setPadding(0, 0,
						margin, 0);
			}

		}
		String details = FlightResultActivity.strDetails;

		tvFlightDates.setText(details);

	}
	
	public void clicker(View v) {
		switch (v.getId()) {
		case R.id.rl_back:
			finish();
			break;

		case R.id.btn_proceed:
			if (((CheckBox) findViewById(R.id.cb_terms)).isChecked())
				new BackService().execute();
			break;
			
		case R.id.tv_rules :
			Intent rules = new Intent(FlightPaymentActivity.this, WebViewActivity.class);
			rules.putExtra("url", CommonFunctions.main_url+CommonFunctions.lang+ 
					"/Shared/Terms");
			startActivity(rules);
			break;
			
		default:
			break;
		}
	}

	Double totalPriceKnet, totalPriceMigs, ServKnet, servMigs,
			baggageFee = 0.0;

	private void showSummary() {
		try {
			((TextView) findViewById(R.id.tv_class))
					.setText(FlightResultActivity.flightClass);

			jObj = jobj.getJSONObject("Item");
			strCurrency = jObj.getString("Currency");
			String temp = String.format(new Locale("en"), "%.3f",
					jObj.getDouble("TotalAmount"));
			
			((TextView) findViewById(R.id.tv_price)).setText(strCurrency + " "
					+ temp);
			
			JSONArray jFare = jObj.getJSONArray("faresummary");
			jobj = jFare.getJSONObject(0);
			jobj = jobj.getJSONObject("BoardingFares");

			if (jobj.length() > 0) {
				baggageFee = jobj.getDouble("BaggageFee")
						+ jobj.getDouble("HandBagFee")
						+ jobj.getDouble("CheckInFee")
						+ jobj.getDouble("BoardingFee")
						+ jobj.getDouble("SmsNotifyFee")
						+ jobj.getDouble("SupplierPaymentFee");
			}

			((LinearLayout) findViewById(R.id.ll_baggage))
					.setVisibility(baggageFee > 0 ? View.VISIBLE : View.GONE);

			((TextView) findViewById(R.id.tv_baggage_fees))
			.setText(baggageFee > 0 ? strCurrency + " "
					+ String.format(new Locale("en"), "%.3f",
					baggageFee) : null);
			
			JSONArray jArray = jObj.getJSONArray("AvailablePaymentGateways");
			for (int i = 0; i < jArray.length(); ++i) {
				jobj = jArray.getJSONObject(i);
				if (jobj.getInt("PaymentGateWayId") == 2) {
					ServKnet = jobj.getBoolean("IsPercentage") ? jobj
							.getDouble("ServiceCharge")
							* (jObj.getDouble("TotalAmount") + baggageFee) / 100 : jobj
							.getDouble("ServiceCharge") * jobj
							.getDouble("ConvertionRate");
					totalPriceKnet = ServKnet + baggageFee
							+ jObj.getDouble("TotalAmount");

				} else if (jobj.getInt("PaymentGateWayId") == 8) {
					servMigs = jobj.getBoolean("IsPercentage") ? jobj
							.getDouble("ServiceCharge")
							* (jObj.getDouble("TotalAmount") + baggageFee) / 100 : jobj
							.getDouble("ServiceCharge") * jobj
							.getDouble("ConvertionRate");
					totalPriceMigs = servMigs + baggageFee
							+ jObj.getDouble("TotalAmount");

				}
			}
			
			
			ivMigs.setVisibility(jObj.getBoolean("IsMigsPaymentGatewayActive") ? View.VISIBLE
					: View.GONE);
			
			if(Integer.parseInt(selectedPayment) == 2) {
				ivKnet.performClick();
			}
			else {
				ivMigs.performClick();
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class BackService extends AsyncTask<Void, Void, String> {

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
				String tempURL  = deepUrl.replace("{0}", selectedPayment);
				url = new URL(tempURL);

				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.setAcceptCookie(true);
				String cookie = cookieManager.getCookie(url.toString());
				Log.i("url", url.toString());
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				// urlConnection.setReadTimeout(15000);
				urlConnection.setRequestProperty("Cookie", cookie);
				urlConnection.setConnectTimeout(15000);
				urlConnection.setRequestMethod("GET");

				// Get cookies from responses and save into the cookie manager
				List<String> cookieList = urlConnection.getHeaderFields().get(
						"Set-Cookie");
				if (cookieList != null) {
					for (String cookieTemp : cookieList) {
						cookieManager.setCookie(urlConnection.getURL().toString(),
								cookieTemp);
					}
				}

				InputStream in = new BufferedInputStream(
						urlConnection.getInputStream());
				String resultString = convertStreamToString(in);
				urlConnection.disconnect();

				System.out.println("------------------Received result-------------"
						+ resultString);

				urlConnection.disconnect();

				JSONObject json = new JSONObject(resultString);
				json = json.getJSONObject("data");
				if(json.getBoolean("Isvalid")) {
					if (json.get("ResponseType").toString().equalsIgnoreCase("Pay")) {
						proceedUrl = json.getString("DeeplinkUrl");
						proceedUrl = proceedUrl.replace("{0}", selectedPayment);
						return "url";
					} else if(json.get("ResponseType").toString().equalsIgnoreCase("FareChange")) {
						jobj = json;
						confirmMsg = json.getString("Confirmationmessage");
						return "fare change";
					} else if(json.get("ResponseType").toString().equalsIgnoreCase("ProceedBook")) {
						proceedUrl = json.getString("DeeplinkUrl");
						return "url";
					} else if(json.get("ResponseType").toString().equalsIgnoreCase("BookFareChange")) {
						jobj = json;
						confirmMsg = json.getString("Confirmationmessage");
						proceedUrl = json.getString("DeeplinkUrl");
						proceedUrl = proceedUrl.replace("{0}", selectedPayment);
						return "book fare change";
					} else if(json.get("ResponseType").toString().equalsIgnoreCase("BookingFailed")) {
						confirmMsg1 = json.getString("Confirmationmessage");
						return "booking failed";
					} else if(json.get("ResponseType").toString().equalsIgnoreCase("FlyDubaiTimeOut")) {
						confirmMsg1 = json.getString("Confirmationmessage");
						return "redirect";
					} else if(json.get("ResponseType").toString().equalsIgnoreCase("bookFareUnavail")) {
						confirmMsg = json.getString("Confirmationmessage");
						confirmMsg1 = "bookFareUnavail";
						return "bookFareUnavail";
					}
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
			if (result != null) {

				if(result.equalsIgnoreCase("redirect")) {
					showAlert(confirmMsg1);
				} else if(result.equalsIgnoreCase("fare change")) {
					showAlert(confirmMsg);
				} else if(result.equalsIgnoreCase("url")) {
					Intent web = new Intent(FlightPaymentActivity.this, WebActivity.class);
					web.putExtra("url", proceedUrl);
					startActivity(web);
				} else if (result.equalsIgnoreCase("booking failed")) {
					showAlert(confirmMsg1);
				} else if (result.equalsIgnoreCase("bookFareUnavail")) {
					showAlert(confirmMsg);
				} else if (result.equalsIgnoreCase("book fare change")) {
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(FlightPaymentActivity.this);

					alertDialog.setMessage(confirmMsg);

					alertDialog.setPositiveButton(getResources().getString(R.string.ok),
							new AlertDialog.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									showSummary();
									Intent web = new Intent(FlightPaymentActivity.this, WebActivity.class);
									web.putExtra("url", proceedUrl);
									startActivity(web);
									}
							});

					alertDialog.setNegativeButton(
							getResources().getString(R.string.cancel),
							new AlertDialog.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									finish();
									FlightPaxActivity.activityFlightPax.finish();
								}
							});

					alertDialog.setCancelable(false);
					alertDialog.show();
				}
				
			} else
				Toast.makeText(getApplicationContext(),
						"Some thing went wrong", Toast.LENGTH_SHORT).show();

			super.onPostExecute(result);
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

	public void showAlert(String msg) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		alertDialog.setMessage(msg);

		alertDialog.setPositiveButton(getResources().getString(R.string.ok),
				new AlertDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (confirmMsg != null && confirmMsg1 != null) {
							
							finish();
							FlightPaxActivity.activityFlightPax.finish();
							
						}else if(confirmMsg != null) {
							showSummary();
							new BackService().execute();
						}
						else {
							finishAffinity();
							Intent home = new Intent(FlightPaymentActivity.this, MainActivity.class);
							home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(home);
						}
					}
				});

		if(confirmMsg != null && confirmMsg1 == null)

		alertDialog.setNegativeButton(
				getResources().getString(R.string.cancel),
				new AlertDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
						FlightPaxActivity.activityFlightPax.finish();
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
