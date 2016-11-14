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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class HotelPaymentActivity extends Activity {

	private Locale myLocale;
	String strSessionId = null;
	String sID = null, strJson, deepUrl, strCurrency, strImageUrl,
			strHotelName, strHotelAddress, strRooms, strNights, strCheckin,
			strCheckout, strRedirectUrl = null;
	int adultCount, childCount;
	boolean blIsRoundTrip;
	JSONObject jobj, jObj;
	JSONArray jArrayPassenger;
	RadioGroup rgPayment;
	RadioButton rbKnet, rbMigs;
	ImageView ivHotelLogo;
	String selectedPayment = "2";
	String strResponseType = null;

	LinearLayout llPassengers;
	Dialog loaderDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		loadLocale();
		setContentView(R.layout.activity_hotel_payment);

		loaderDialog = new Dialog(HotelPaymentActivity.this,
				android.R.style.Theme_Translucent);
		loaderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loaderDialog.getWindow().setGravity(Gravity.TOP);
		loaderDialog.setContentView(R.layout.dialog_loader);
		loaderDialog.setCancelable(false);
		((ImageView) loaderDialog.findViewById(R.id.iv_loader))
				.setImageResource(R.drawable.hotel_loader);

		llPassengers = (LinearLayout) findViewById(R.id.ll_passengers);

		rgPayment = (RadioGroup) findViewById(R.id.rg_payment_methods);
		rbKnet = (RadioButton) findViewById(R.id.rb_knet);
		rbMigs = (RadioButton) findViewById(R.id.rb_visa_master);

		getIntentValues();
		setHoteDetails();
		showSummary();
		showPassengers();

	}

	private void showPassengers() {
		// TODO Auto-generated method stub
		try {
			jObj = null;
			int i = 0;
			for (i = 0; i < jArrayPassenger.length(); ++i) {
				jObj = jArrayPassenger.getJSONObject(i);

				final View v = getLayoutInflater().inflate(
						R.layout.item_payment_passengers, null);
				((TextView) v.findViewById(R.id.tv_passenger_name))
						.setText(jObj.getString("FirstName") + " "
								+ jObj.getString("LastName"));
				((TextView) v.findViewById(R.id.tv_passenger_type))
						.setText(jObj.getString("Title"));

				((ImageView) v.findViewById(R.id.iv_passenger_type))
						.setImageResource(jObj.getString("PassengerType")
								.equalsIgnoreCase("adult") ? R.drawable.ic_adult
								: R.drawable.ic_child);

				llPassengers.addView(v);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getIntentValues() {
		strImageUrl = getIntent().getExtras().getString("img_url");
		strHotelName = getIntent().getExtras().getString("strHotelName");
		strHotelAddress = getIntent().getExtras().getString("strHotelAddress");
		strRooms = getIntent().getExtras().getString("RoomCount");
		strNights = getIntent().getExtras().getString("NightCount");
		strCheckin = getIntent().getExtras().getString("Checkin");
		strCheckout = getIntent().getExtras().getString("Checkout");
		adultCount = getIntent().getExtras().getInt("TotalAdultCount");
		childCount = getIntent().getExtras().getInt("TotalChildCount");

		strJson = getIntent().getExtras().getString("json", "");
		try {
			jobj = new JSONObject(strJson);
			strJson = null;
			strJson = getIntent().getExtras().getString("passengerJson", "");
			jArrayPassenger = new JSONArray(strJson);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setHoteDetails() {
		((TextView) findViewById(R.id.tv_hotel_name)).setText(strHotelName);
		((TextView) findViewById(R.id.tv_hotel_address))
				.setText(strHotelAddress);
		((TextView) findViewById(R.id.tv_check_in_date)).setText(strCheckin);
		((TextView) findViewById(R.id.tv_check_out_date)).setText(strCheckout);
		((TextView) findViewById(R.id.tv_room_count)).setText(strRooms);
		((TextView) findViewById(R.id.tv_night_count)).setText(strNights);
		((TextView) findViewById(R.id.tv_adult_count)).setText(String
				.valueOf(adultCount));
		((TextView) findViewById(R.id.tv_child_count)).setText(String
				.valueOf(childCount));
		((TextView) findViewById(R.id.tv_child_count))
				.setVisibility(childCount != 0 ? View.VISIBLE : View.GONE);
		((TextView) findViewById(R.id.tv_child_text))
				.setVisibility(childCount != 0 ? View.VISIBLE : View.GONE);

	}

	public void clicker(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;

		case R.id.btn_proceed:
			if (((CheckBox) findViewById(R.id.cb_terms)).isChecked())
				new backService().execute();
			break;

		case R.id.tv_rules:
			Intent rules = new Intent(HotelPaymentActivity.this,
					WebActivity.class);
			rules.putExtra("url", CommonFunctions.main_url
					+ CommonFunctions.lang + "/Shared/Terms");
			startActivity(rules);
			break;

		default:
			break;
		}
	}

	Double totalPriceKnet, totalPriceMigs, ServKnet, servMigs,
			dblConversionRate = 1.0;

	private void showSummary() {
		try {
			deepUrl = jobj.getString("ProceedPayUrl");
			strCurrency = jobj.getString("DisplayCurrency");

			JSONArray jArray = jobj.getJSONArray("AvailablePaymentGateways");
			for (int i = 0; i < jArray.length(); ++i) {
				jObj = jArray.getJSONObject(i);
				if (jObj.getInt("PaymentGateWayId") == 2) {

					dblConversionRate = jObj.getDouble("ConvertionRate");

					ServKnet = jObj.getBoolean("IsPercentage") ? jObj
							.getDouble("ServiceCharge")
							* jobj.getDouble("TotalAmount")
							* dblConversionRate
							/ 100 : jObj.getDouble("ServiceCharge")
							* dblConversionRate;
					totalPriceKnet = ServKnet + jobj.getDouble("TotalAmount")
							* dblConversionRate;

				} else if (jObj.getInt("PaymentGateWayId") == 8) {

					dblConversionRate = jObj.getDouble("ConvertionRate");

					servMigs = jObj.getBoolean("IsPercentage") ? jObj
							.getDouble("ServiceCharge")
							* jobj.getDouble("TotalAmount")
							* dblConversionRate
							/ 100 : jObj.getDouble("ServiceCharge")
							* dblConversionRate;
					totalPriceMigs = servMigs + jobj.getDouble("TotalAmount")
							* dblConversionRate;

				}
			}

			((TextView) findViewById(R.id.tv_price)).setText(strCurrency
					+ " "
					+ String.format(new Locale("en"), "%.3f",
							jobj.getDouble("TotalAmount") * dblConversionRate));
			((TextView) findViewById(R.id.tv_hotel_price)).setText(strCurrency
					+ " "
					+ String.format(new Locale("en"), "%.3f",
							jobj.getDouble("TotalAmount") * dblConversionRate));

			rgPayment.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					// TODO Auto-generated method stub
					switch (group.getCheckedRadioButtonId()) {
					case R.id.rb_knet:
						String temp = String.format(new Locale("en"), "%.3f",
								ServKnet);
						((TextView) findViewById(R.id.tv_trans_fees))
								.setText(strCurrency + " " + temp);

						temp = String.format(new Locale("en"), "%.3f",
								totalPriceKnet);
						((TextView) findViewById(R.id.tv_total_price))
								.setText(strCurrency + " " + temp);
						selectedPayment = "2";
						break;
					case R.id.rb_visa_master:
						temp = String
								.format(new Locale("en"), "%.3f", servMigs);
						((TextView) findViewById(R.id.tv_trans_fees))
								.setText(strCurrency + " " + temp);

						temp = String.format(new Locale("en"), "%.3f",
								totalPriceMigs);
						((TextView) findViewById(R.id.tv_total_price))
								.setText(strCurrency + " " + temp);
						selectedPayment = "8";
						break;
					default:
						break;
					}
				}
			});

			rbMigs.setVisibility(jobj.getBoolean("IsMigsPaymentGatewayActive") ? View.VISIBLE
					: View.GONE);

			if(Integer.parseInt(selectedPayment) == 2) {
				rbKnet.setChecked(true);
				String temp = String.format(new Locale("en"), "%.3f",
						totalPriceKnet);
				((TextView) findViewById(R.id.tv_total_price))
						.setText(strCurrency + " " + temp);
			}
			else
				rbMigs.setChecked(true);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class backService extends AsyncTask<Void, Void, String> {

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
				String tempURL = deepUrl
						.replace(
								"{0}",
								"{\"TotalAmount\":0,\"TotalBoardingFee\":0,\"TotalAmountInBaseCurrency\""
										+ ":0,\"BaseFare\":0,\"ServiceCharge\":0,\"RoomCount\":0,\""
										+ "Currency\":null,\"DecimalPoint\":0,\"PaymentGateway\":\""
										+ selectedPayment
										+ "\",\""
										+ "PaymentGatewayType\":null,\"IsMigsPaymentGatewayActive\":false,\""
										+ "IsPayTabsPaymentGatewayActive\":false,\"IsPaymentAtHomeActive\":false,\""
										+ "IsPaymentAtStoreActive\":false,\"TnCChecked\":true,\""
										+ "IsInsurance\":false,\"faresummary\":null,\"AvailablePaymentGateways\":null,\""
										+ "IsCashOnDeliveryActive\":false,\"IsCashOnDelivery\":false,\""
										+ "CashOnDeliveryInfo\":null,\"CodServiceList\":null,\""
										+ "CompanyGenQuoteDetailsModel\":null,\"CompanyGenQuoteForHotel\":null,\""
										+ "KentCharge\":0,\"MigsCharge\":0,\"PayTabsCharge\":0,\"CashUCharge\":0,\""
										+ "IsFlightExcluded\":false,\"NeedInsurance\":false,\"NeedVisa\":false,\""
										+ "ApiId\":0,\"InsuranceAmount\":0,\"CardDetails\":null,\"ConversionRate\":0,\""
										+ "Address1\":null,\"Address2\":null,\"RedeemPoint\":0,\"IsRedeemPoint\":false,\""
										+ "TransactionTypeId\":3}");
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
						cookieManager.setCookie(urlConnection.getURL()
								.toString(), cookieTemp);
					}
				}

				InputStream in = new BufferedInputStream(
						urlConnection.getInputStream());
				String resultString = convertStreamToString(in);
				urlConnection.disconnect();

				System.out
						.println("------------------Received result-------------"
								+ resultString);

				urlConnection.disconnect();

				JSONObject json = new JSONObject(resultString);
				if (json.getBoolean("IsValid")) {

					if (json.getString("RequestType").equalsIgnoreCase(
							"Redirect")) {
						strResponseType = "success";
						strRedirectUrl = json.getString("ReturnUrl");
					}
					return "success";
				} else if (!json.getBoolean("IsValid") && json.getBoolean("IsFareUpdate")) {
						strResponseType = "Fare update";
						strJson = resultString;
						jobj = new JSONObject(strJson);
						
						return json.getString("DisplayCurrency") + " " + String.format(new Locale("en"), "%.3f",
								jobj.getDouble("TotalAmount") * dblConversionRate);
				} else {
					strResponseType = "failed";
					return json.getString("Message");
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
			if (loaderDialog.isShowing())
				loaderDialog.dismiss();

			if (result != null) {
				if (result.equalsIgnoreCase("success")) {
					Intent web = new Intent(HotelPaymentActivity.this,
							WebActivity.class);
					web.putExtra("url", strRedirectUrl);
					startActivity(web);
				} else if(strResponseType.equalsIgnoreCase("Fare update")) {
					showAlert(getResources().getString(R.string.hotel_fare_update, result));
				} else {
					showAlert(result);
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
						if (strResponseType.equalsIgnoreCase("Fare update")) {
							showSummary();
							new backService().execute();
						}
						else {
							finishAffinity();
							Intent home = new Intent(HotelPaymentActivity.this,
									MainActivity.class);
							home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(home);
						}
					}
				});

		if (strResponseType.equalsIgnoreCase("Fare update"))
			alertDialog.setNegativeButton(
					getResources().getString(R.string.cancel),
					new AlertDialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							finish();
							HotelPaxActivity.activityHotelPax.finish();
							HotelDetailsActivity.activityHoteDetails.finish();
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
