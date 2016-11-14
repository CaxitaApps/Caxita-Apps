package com.travel.hjozzat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hjozzat.support.CommonFunctions;
import com.hjozzat.support.ImageDownloaderTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ViewBookigActivity extends Activity {

	private Locale myLocale;
	Context ctx;
	LinearLayout lldeparture_1, lldeparture_2, lldeparture_3, lldeparture_4;
	String AirlineName, FromTime, FromDate, AirlineLogo, FlightNo, Origin,
			OriginCity, EndTime, EndDate, Destination, DestinationCity;
	JSONObject response;
	Dialog loaderDialog;

	String strRefNo, strEmail, strPhno, strSearchType, strCCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		loadLocale();
		setContentView(R.layout.activity_view_booking);

		strRefNo = getIntent().getStringExtra("refno");
		strEmail = getIntent().getStringExtra("email");
		strPhno = getIntent().getStringExtra("phonno");
		strCCode = getIntent().getStringExtra("ctrycode");

		lldeparture_1 = (LinearLayout) findViewById(R.id.departure_1);
		lldeparture_2 = (LinearLayout) findViewById(R.id.departure_2);
		lldeparture_3 = (LinearLayout) findViewById(R.id.departure_3);
		lldeparture_4 = (LinearLayout) findViewById(R.id.departure_4);

		loaderDialog = new Dialog(this, android.R.style.Theme_Translucent);
		loaderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loaderDialog.getWindow().setGravity(Gravity.TOP);
		loaderDialog.setContentView(R.layout.dialog_loader);
		loaderDialog.setCancelable(false);

		new GetBookingService().execute();
	}

	public void clicker(View v) {
		if (v.getId() == R.id.iv_back)
			finish();
	}

	private void parseJsonData(String strJsonData) {
		// TODO Auto-generated method stub
		try {
			response = new JSONObject(strJsonData);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {

			JSONObject object = response.getJSONObject("data");
			Log.e("object  ", object.toString());

			JSONArray items = object.getJSONArray("Item");

			object = items.getJSONObject(1);

			strSearchType = object.getString("SearchType");

			if (strSearchType.equalsIgnoreCase("flight")) {

				object = items.getJSONObject(0);

				JSONArray Itenary = object
						.getJSONArray("ItenaryDetailsInfoList");

				Log.e("ItenaryDetailsInfoList  ", Itenary.toString());
				parseFlightData(Itenary);

				String strTcktStatus = object.getString("TransactionStatus");

				if (strTcktStatus
						.equalsIgnoreCase("CancelAndRefundRequestedToHO"))
					((TextView) findViewById(R.id.tv_status))
							.setText(getResources().getString(
									R.string.waiting_cancellation));
				else if (strTcktStatus
						.equalsIgnoreCase("CancelAndRefundRequestedToSupplier"))
					((TextView) findViewById(R.id.tv_status))
							.setText(getResources().getString(
									R.string.waiting_refund));
				else
					((TextView) findViewById(R.id.tv_status))
							.setText(strTcktStatus);

				Button btnCancelTicket = (Button) findViewById(R.id.btn_cancel_ticket);
				Button btnViewTicket = (Button) findViewById(R.id.btn_view_ticket);

				if (strTcktStatus.toLowerCase().contains("ticketed")) {

					final String urlParameters = "transDetailId="
							+ URLEncoder
									.encode(object
											.getString("TransactionTypeDetailId"),
											"UTF-8")
							+ "&bookingId="
							+ URLEncoder.encode(
									object.getString("FlightBookingId"),
									"UTF-8")
							+ "&supplierPnr="
							+ URLEncoder.encode(
									object.getString("SupplierPnr"), "UTF-8");

					btnCancelTicket.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(),
									urlParameters, Toast.LENGTH_SHORT).show();
							new CancelBackService().execute(urlParameters);
						}
					});

					final String strSelectedTransactionID = object
							.getString("FlightBookingId");

					btnViewTicket.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent voucher = new Intent(
									getApplicationContext(), WebViewActivity.class);
							voucher.putExtra("url", CommonFunctions.main_url
									+ CommonFunctions.lang
									+ "/Flight/PrintTicketMyBooking?bookingId="
									+ strSelectedTransactionID);
							startActivity(voucher);
						}
					});

					btnCancelTicket.setVisibility(View.VISIBLE);
					btnViewTicket.setVisibility(View.VISIBLE);
				} else {
					btnCancelTicket.setVisibility(View.GONE);
					btnViewTicket.setVisibility(View.GONE);
				}

				((LinearLayout) findViewById(R.id.ll_flight_details_lv))
						.setVisibility(View.VISIBLE);

			} else {

				object = items.getJSONObject(0);

				object = object.getJSONObject("Data");

				object = object.getJSONObject("Voucher");

				parseHotelData(object);
				((LinearLayout) findViewById(R.id.ll_hotel_details_lv))
						.setVisibility(View.VISIBLE);
			}

			Log.e("else if", response.toString());
		} catch (Exception e) {

		}
	}

	private void parseFlightData(JSONArray jarray) {
		try {

			LinearLayout flight = null;

			String[] classArray = this.getResources().getStringArray(
					R.array.class_spinner_items);
			String strClass = null;
			// veh_type = new String[jarray.length()];
			int k = 0;
			for (int i = 0; i < jarray.length(); i++) {

				final View view = getLayoutInflater().inflate(
						R.layout.mybooking_flight_item, null);

				JSONObject object = jarray.getJSONObject(i);

				if (!object.getString("TransitTime").equalsIgnoreCase("")) {
					((TextView) view.findViewById(R.id.tv_transit_time))
							.setText(getString(R.string.layover) + " : "
									+ object.getString("TransitTime"));
					((TextView) view.findViewById(R.id.tv_transit_time))
							.setVisibility(View.VISIBLE);
				} else {
					++k;
					switch (k) {
					case 1:
						flight = (LinearLayout) findViewById(R.id.ll_flight1);
						break;

					case 2:
						lldeparture_2.setVisibility(View.VISIBLE);
						flight = (LinearLayout) findViewById(R.id.ll_flight2);
						break;

					case 3:
						lldeparture_3.setVisibility(View.VISIBLE);
						flight = (LinearLayout) findViewById(R.id.ll_flight3);
						break;

					case 4:
						lldeparture_4.setVisibility(View.VISIBLE);
						flight = (LinearLayout) findViewById(R.id.ll_flight4);
						break;

					default:
						break;
					}
				}

				ImageView ivFlightLogo = (ImageView) view
						.findViewById(R.id.iv_flight_logo);
				InputStream ims;
				Drawable d;
				try {
					// get input stream
					ims = getAssets().open(object.getString("AirlineLogo"));
					// load image as Drawable
					d = Drawable.createFromStream(ims, null);
					// set image to ImageView
					ivFlightLogo.setImageDrawable(d);
					ims.close();
					d = null;
				} catch (IOException ex) {
					ex.printStackTrace();
					ivFlightLogo.setImageResource(R.drawable.ic_no_image);
				}

				FromTime = object.getString("FromTime");
				FromDate = object.getString("FromDate");
				AirlineLogo = object.getString("AirlineLogo");
				FlightNo = object.getString("FlightNo");
				AirlineName = object.getString("AirlineName");
				Origin = object.getString("Origin");
				OriginCity = object.getString("OriginCity");
				EndTime = object.getString("EndTime");
				EndDate = object.getString("EndDate");
				Destination = object.getString("Destination");
				DestinationCity = object.getString("DestinationCity");

				((TextView) view.findViewById(R.id.tv_flight_name))
						.setText(AirlineName + " " + FlightNo);
				((TextView) view.findViewById(R.id.tv_depart_time))
						.setText(FromTime);
				((TextView) view.findViewById(R.id.tv_depart_date))
						.setText(FromDate);
				((TextView) view.findViewById(R.id.tv_depart_airport))
						.setText(Origin + " " + OriginCity);
				((TextView) view.findViewById(R.id.tv_arrival_time))
						.setText(EndTime);
				((TextView) view.findViewById(R.id.tv_arrival_date))
						.setText(EndDate);
				((TextView) view.findViewById(R.id.tv_arrival_airport))
						.setText(Destination + " " + DestinationCity);

				if (object.getBoolean("ReturnFlightIndicator")) {
					((TextView) findViewById(R.id.tv_flight_type))
							.setText(getString(R.string.retur));
					((ImageView) findViewById(R.id.iv_flight_type))
							.setImageResource(R.drawable.ic_arrival);
					((ImageView) view.findViewById(R.id.flightlogo))
							.setImageResource(R.drawable.ic_arrival);
				}

				switch (object.getString("FlightSearchClass")) {
				case "Y":
					strClass = classArray[0];
					break;
				case "W":
					strClass = classArray[1];
					break;

				case "C":
					strClass = classArray[2];
					break;

				case "F":
					strClass = classArray[3];
					break;

				default:
					break;
				}

				((TextView) view.findViewById(R.id.tv_flight_class))
						.setText(strClass);

				flight.addView(view);
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("@json_parser", e.getMessage());
		}
	}

	private void parseHotelData(JSONObject jObj) {

		try {
			ImageView ivRoomLogo = (ImageView) findViewById(R.id.iv_hotel_logo);
			if (jObj.getString("ImageUrl") != null
					&& !jObj.getString("ImageUrl").contains("no_image"))
				new ImageDownloaderTask(ivRoomLogo).execute(jObj
						.getString("ImageUrl"));
			else
				ivRoomLogo.setImageResource(R.drawable.ic_no_image);

			((TextView) findViewById(R.id.tv_hotel_name)).setText(jObj
					.getString("PropName"));
			((TextView) findViewById(R.id.tv_hotel_address)).setText(jObj
					.getString("HotelAddress"));
			((TextView) findViewById(R.id.tv_check_in_date)).setText(jObj
					.getString("Checkin"));
			((TextView) findViewById(R.id.tv_check_out_date)).setText(jObj
					.getString("CheckOut"));
			((TextView) findViewById(R.id.tv_room_count)).setText(jObj
					.getString("NumOfRooms"));
			((TextView) findViewById(R.id.tv_night_count)).setText(jObj
					.getString("NumOfNights"));
			((TextView) findViewById(R.id.tv_adult_count)).setText(jObj
					.getString("NumOfAdult"));
			((TextView) findViewById(R.id.tv_child_count)).setText(jObj
					.getString("NumOfChild"));
			((TextView) findViewById(R.id.tv_price))
					.setText(jObj.getString("Curency") + " "
							+ jObj.getString("TotalAmount"));
			((TextView) findViewById(R.id.tv_status_hotel)).setText(jObj
					.getString("Voucherstatus"));

			Button btnCancelTicket = (Button) findViewById(R.id.btn_cancel_ticket_hotel);
			final Button btnViewTicket = (Button) findViewById(R.id.btn_view_ticket_hotel);

			if (jObj.getString("Voucherstatus").toLowerCase()
					.contains("booked")) {

				final String strCancelUrl = jObj
						.getString("CancelVoucherUrl");

				btnCancelTicket.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						new CancelBackService().execute(strCancelUrl);
					}
				});

				btnViewTicket.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent voucher = new Intent(getApplicationContext(),
								WebViewActivity.class);
						voucher.putExtra(
								"url",
								CommonFunctions.main_url
										+ CommonFunctions.lang
										+ "/Hotel/ViewMyVoucher?transDetailId="
										+ strRefNo
										+ "&AmountVisibility=true&PrintwithFare=true&showzoption=");
						startActivity(voucher);
					}
				});

				btnCancelTicket.setVisibility(View.VISIBLE);
				btnViewTicket.setVisibility(View.VISIBLE);
			} else {
				btnCancelTicket.setVisibility(View.GONE);
				btnViewTicket.setVisibility(View.GONE);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private class GetBookingService extends AsyncTask<Void, Void, String> {

		boolean blNoRecords = false;
		
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
				String urlParameters = null;
				if(!strEmail.equals("")) {
					urlParameters = "referenceNumber="
							+ URLEncoder.encode(strRefNo, "UTF-8")
							+ "&emailId="
							+ URLEncoder.encode(strEmail, "UTF-8");
				} else {
					urlParameters = "referenceNumber="
							+ URLEncoder.encode(strRefNo, "UTF-8")
							+ "&mobileNumber="
							+ URLEncoder.encode(strPhno, "UTF-8")
							+ "&countryCode="
							+ URLEncoder.encode(strCCode, "UTF-8");
				}
				
				String request = CommonFunctions.main_url
						+ CommonFunctions.lang
						+ "/MyAccountApi/ReferenceNumber";
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

				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.setAcceptCookie(true);
				String cookie = cookieManager.getCookie(url.toString());

				conn.setRequestProperty("Cookie", cookie);

				// Send request
				DataOutputStream wr = new DataOutputStream(
						conn.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.flush();
				wr.close();

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

				conn.disconnect();

				JSONObject json = new JSONObject(res);

				JSONObject object = json.getJSONObject("data");
				Log.e("object  ", object.toString());

				if (object.getBoolean("Isvalid")) {
					return res;

				} else {
					Log.e("error  ", object.getString("messages"));
					blNoRecords = true;
					return object.getString("messages");
				}

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
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				handler.sendEmptyMessage(2);
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			System.out.println("result" + result);
			if(result != null) {
				if (blNoRecords) {
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewBookigActivity.this);

					alertDialog.setMessage(result);

					alertDialog.setPositiveButton(getResources().getString(R.string.ok),
							new AlertDialog.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									Intent manage = new Intent(ViewBookigActivity.this, ManageBookingActivity.class);
									startActivity(manage);
									finish();
								}
							});

					alertDialog.setCancelable(false);
					alertDialog.show();
				} else {
					parseJsonData(result);
				}
			}
			
			if (loaderDialog.isShowing())
				loaderDialog.dismiss();
			super.onPostExecute(result);
		}

	}

	private class CancelBackService extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			loaderDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String request = null;

			URL url = null;
			try {

				if (strSearchType.equalsIgnoreCase("flight")) {
					request = CommonFunctions.main_url + CommonFunctions.lang
							+ "/MyAccountApi/CancelTicket?" + params[0];

				} else {
					request = params[0];
				}

				url = new URL(request);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(25000);
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");

				conn.setRequestProperty("Content-Language", "en-US");

				conn.setUseCaches(false);
				conn.setDoInput(true);
				conn.setDoOutput(true);

				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.setAcceptCookie(true);
				String cookie = cookieManager.getCookie(url.toString());

				conn.setRequestProperty("Cookie", cookie);

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
				in.close();

				if (strSearchType.equalsIgnoreCase("flight")) {
					JSONObject jObj = new JSONObject(res);

					jObj = jObj.getJSONObject("data");
					Log.d("object  ", jObj.toString());

					if (jObj.getBoolean("Isvalid")) {
						return jObj.getString("Confirmationmessage");
					}
				} else {
					return res.replace("\"", "");
				}

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub

			if (result != null) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						ViewBookigActivity.this);

				alertDialog.setMessage(result);

				alertDialog.setPositiveButton(
						getResources().getString(R.string.ok),
						new AlertDialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								new GetBookingService().execute();
							}
						});

				alertDialog.setCancelable(false);
				alertDialog.show();
			} else {

			}

			if (loaderDialog.isShowing())
				loaderDialog.dismiss();
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

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				loaderDialog.dismiss();
				showAlert("There is a problem on your Network. Please try again later.");

			} else if (msg.what == 2) {

				if (loaderDialog.isShowing())
					loaderDialog.dismiss();
				showAlert("There is a problem on your application. Please report it.");

			} else if (msg.what == 3) {
				if (loaderDialog.isShowing())
					loaderDialog.dismiss();
				showAlert(getResources().getString(R.string.no_result));
			}

		}
	};

	public void showAlert(String msg) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

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
