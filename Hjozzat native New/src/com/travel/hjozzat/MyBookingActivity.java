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
import java.text.SimpleDateFormat;
import java.util.Date;
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
import android.graphics.Color;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.travel.hjozzat.R;

public class MyBookingActivity extends Activity {

	private Locale myLocale;
	LinearLayout lldeparture_1, lldeparture_2, lldeparture_3, lldeparture_4;

	Button btnUpcomingFlight, btnCompletedFlight, btnUpcomingHotel,
			btnCompletedHotel;

	LinearLayout llTable, llFlightDetails, llHotelDetails;
	TextView tvNoBooking;
	ScrollView svTable;

	Dialog loaderDialog;

	String strTabType = "Upcoming flight";
	String strSelectedTransactionID = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		loadLocale();
		setContentView(R.layout.activity_mybooking);

		initialize();

		new GetBookingBackMethod().execute();
	}

	public void clicker(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
			
		case R.id.my_profile:
			Intent i = new Intent(getApplicationContext(),
					MyProfileActivity.class);
			startActivity(i);
			break;
		default:
			break;
		}
	}

	private void initialize() {
		// TODO Auto-generated method stub
		btnUpcomingFlight = (Button) findViewById(R.id.btn_upcoming_flights);
		btnCompletedFlight = (Button) findViewById(R.id.btn_completed_flights);
		btnUpcomingHotel = (Button) findViewById(R.id.btn_upcoming_hotels);
		btnCompletedHotel = (Button) findViewById(R.id.btn_completed_hotels);
		
		llTable = (LinearLayout) findViewById(R.id.ll_table);
		llFlightDetails = (LinearLayout) findViewById(R.id.ll_flight_details_lv);
		llHotelDetails = (LinearLayout) findViewById(R.id.ll_hotel_details_lv);
		tvNoBooking = (TextView) findViewById(R.id.tv_no_booking);
		svTable = (ScrollView) findViewById(R.id.sv_table);

		lldeparture_1 = (LinearLayout) findViewById(R.id.departure_1);
		lldeparture_2 = (LinearLayout) findViewById(R.id.departure_2);
		lldeparture_3 = (LinearLayout) findViewById(R.id.departure_3);
		lldeparture_4 = (LinearLayout) findViewById(R.id.departure_4);

		// llHorizontal = (LinearLayout) findViewById(R.id.ll_horizontal_table);

		OnClickListener btnClicker = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.btn_upcoming_flights:
					btnUpcomingFlight
							.setBackgroundResource(R.drawable.buttonfocus);
					btnCompletedFlight
							.setBackgroundResource(R.drawable.white_bag_curved_border);
					btnUpcomingHotel
							.setBackgroundResource(R.drawable.white_bag_curved_border);
					btnCompletedHotel
							.setBackgroundResource(R.drawable.white_bag_curved_border);

					btnUpcomingFlight.setTextColor(Color.WHITE);
					btnCompletedFlight.setTextColor(Color.BLACK);
					btnUpcomingHotel.setTextColor(Color.BLACK);
					btnCompletedHotel.setTextColor(Color.BLACK);

					strTabType = "Upcoming flight";

					break;

				case R.id.btn_completed_flights:
					btnUpcomingFlight
							.setBackgroundResource(R.drawable.white_bag_curved_border);
					btnCompletedFlight
							.setBackgroundResource(R.drawable.buttonfocus);
					btnUpcomingHotel
							.setBackgroundResource(R.drawable.white_bag_curved_border);
					btnCompletedHotel
							.setBackgroundResource(R.drawable.white_bag_curved_border);

					btnUpcomingFlight.setTextColor(Color.BLACK);
					btnCompletedFlight.setTextColor(Color.WHITE);
					btnUpcomingHotel.setTextColor(Color.BLACK);
					btnCompletedHotel.setTextColor(Color.BLACK);

					strTabType = "Completed flight";

					break;

				case R.id.btn_upcoming_hotels:
					btnUpcomingFlight
							.setBackgroundResource(R.drawable.white_bag_curved_border);
					btnCompletedFlight
							.setBackgroundResource(R.drawable.white_bag_curved_border);
					btnUpcomingHotel
							.setBackgroundResource(R.drawable.buttonfocus);
					btnCompletedHotel
							.setBackgroundResource(R.drawable.white_bag_curved_border);

					btnUpcomingFlight.setTextColor(Color.BLACK);
					btnCompletedFlight.setTextColor(Color.BLACK);
					btnUpcomingHotel.setTextColor(Color.WHITE);
					btnCompletedHotel.setTextColor(Color.BLACK);

					strTabType = "Upcoming hotel";

					break;

				case R.id.btn_completed_hotels:
					btnUpcomingFlight
							.setBackgroundResource(R.drawable.white_bag_curved_border);
					btnCompletedFlight
							.setBackgroundResource(R.drawable.white_bag_curved_border);
					btnUpcomingHotel
							.setBackgroundResource(R.drawable.white_bag_curved_border);
					btnCompletedHotel
							.setBackgroundResource(R.drawable.buttonfocus);

					btnUpcomingFlight.setTextColor(Color.BLACK);
					btnCompletedFlight.setTextColor(Color.BLACK);
					btnUpcomingHotel.setTextColor(Color.BLACK);
					btnCompletedHotel.setTextColor(Color.WHITE);

					strTabType = "Completed hotel";

					break;

				default:
					break;
				}
				new GetBookingBackMethod().execute();
			}
		};

		btnUpcomingFlight.setOnClickListener(btnClicker);
		btnCompletedFlight.setOnClickListener(btnClicker);
		btnUpcomingHotel.setOnClickListener(btnClicker);
		btnCompletedHotel.setOnClickListener(btnClicker);

		btnUpcomingFlight.setBackgroundResource(R.drawable.buttonfocus);
		btnUpcomingFlight.setTextColor(Color.WHITE);

		loaderDialog = new Dialog(this, android.R.style.Theme_Translucent);
		loaderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loaderDialog.getWindow().setGravity(Gravity.TOP);
		loaderDialog.setContentView(R.layout.dialog_loader);
		loaderDialog.setCancelable(false);
	}

	private class GetBookingBackMethod extends AsyncTask<Void, Void, JSONArray> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			llTable.removeAllViews();
			llFlightDetails.setVisibility(View.GONE);
			llHotelDetails.setVisibility(View.GONE);
			tvNoBooking.setVisibility(View.GONE);
			svTable.setVisibility(View.GONE);
			loaderDialog.show();
			super.onPreExecute();
		}

		@Override
		protected JSONArray doInBackground(Void... params) {
			// TODO Auto-generated method stub

			String request = null;
			String urlParameters = null;

			URL url = null;
			try {

				if (strTabType.equals("Upcoming flight")) {
					request = CommonFunctions.main_url + CommonFunctions.lang
							+ "/MyAccountApi/GetBookedFlights";

					urlParameters = "Bookstatus="
							+ URLEncoder.encode("UP", "UTF-8");

				} else if (strTabType.equals("Completed flight")) {
					request = CommonFunctions.main_url + CommonFunctions.lang
							+ "/MyAccountApi/GetBookedFlights";

					urlParameters = "Bookstatus="
							+ URLEncoder.encode("CO", "UTF-8");
				} else if (strTabType.equals("Upcoming hotel")) {
					request = CommonFunctions.main_url + CommonFunctions.lang
							+ "/MyAccountApi/HotelBookingList";

					urlParameters = "IsUpComingBooking="
							+ URLEncoder.encode("true", "UTF-8");
				} else if (strTabType.equals("Completed hotel")) {
					request = CommonFunctions.main_url + CommonFunctions.lang
							+ "/MyAccountApi/HotelBookingList";

					urlParameters = "IsUpComingBooking="
							+ URLEncoder.encode("false", "UTF-8");
				}

				url = new URL(request);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(25000);
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

				System.out.println("res" + res);

				conn.disconnect();
				in.close();

				if (strTabType.contains("flight")) {
					JSONObject jObj = new JSONObject(res);

					jObj = jObj.getJSONObject("data");
					Log.d("object  ", jObj.toString());

					if (jObj.getBoolean("Isvalid")) {
						jObj = jObj.getJSONObject("Item");
						JSONArray jArray = jObj.getJSONArray("Bookings");
						if (jArray.length() > 0)
							return jArray;
					}
				} else {
					JSONArray jArray = new JSONArray(res);
					if (jArray.length() > 0)
						return jArray;
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
		protected void onPostExecute(JSONArray result) {
			// TODO Auto-generated method stub
			tvNoBooking.setVisibility(result != null ? View.GONE : View.VISIBLE);
			if (result != null) {
				printTable(result);
			}
			
			if (loaderDialog.isShowing())
				loaderDialog.dismiss();
			super.onPostExecute(result);
		}

	}

	private class GetBookingDetailsBackMethod extends
			AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			llFlightDetails.setVisibility(View.GONE);
			llHotelDetails.setVisibility(View.GONE);
			if (!loaderDialog.isShowing())
				loaderDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String request = null;
			String urlParameters = null;

			URL url = null;
			try {
				strSelectedTransactionID = params[0];
				if (strTabType.contains("flight")) {
					request = CommonFunctions.main_url + CommonFunctions.lang
							+ "/MyAccountApi/FlightDetailWithBookingID";

					urlParameters = "flightBookingId="
							+ URLEncoder.encode(params[0], "UTF-8");

				} else {
					request = CommonFunctions.main_url + CommonFunctions.lang
							+ "/MyAccountApi/GetVoucherMinData";

					urlParameters = "TransID="
							+ URLEncoder.encode(params[0], "UTF-8");

				}

				url = new URL(request);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(25000);
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

				System.out.println("res" + res);

				conn.disconnect();
				in.close();

				JSONObject jObj = new JSONObject(res);

				if (strTabType.contains("flight")) {
					jObj = jObj.getJSONObject("data");
					Log.d("object  ", jObj.toString());

					if (jObj.getBoolean("Isvalid")) {

						JSONArray items = jObj.getJSONArray("Item");
						return items.toString();

					}
				} else {
					return jObj.toString();
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

				if (strTabType.contains("flight")) {

					try {

						JSONArray jArray = new JSONArray(result);
						JSONObject jObj = jArray.getJSONObject(0);

						jArray = jObj.getJSONArray("ItenaryDetailsInfoList");

						Log.e("ItenaryDetailsInfoList  ", jArray.toString());
						llFlightDetails.setVisibility(View.VISIBLE);
						parseFlightData(jArray);

						String strTcktStatus = jObj
								.getString("TransactionStatus");
						
						if(strTcktStatus.equalsIgnoreCase("CancelAndRefundRequestedToHO"))
							((TextView) findViewById(R.id.tv_status)).setText(getResources().getString(R.string.waiting_cancellation));
						else if(strTcktStatus.equalsIgnoreCase("CancelAndRefundRequestedToSupplier"))
							((TextView) findViewById(R.id.tv_status)).setText(getResources().getString(R.string.waiting_refund));
						else
							((TextView) findViewById(R.id.tv_status)).setText(strTcktStatus);

						Button btnCancelTicket = (Button) findViewById(R.id.btn_cancel_ticket);
						Button btnViewTicket = (Button) findViewById(R.id.btn_view_ticket);

						if (strTcktStatus.toLowerCase()
								.contains("ticketed")
								&& strTabType
										.equalsIgnoreCase("Upcoming flight")) {

							final String urlParams = "transDetailId="
									+ URLEncoder
											.encode(jObj
													.getString("TransactionTypeDetailId"),
													"UTF-8")
									+ "&bookingId="
									+ URLEncoder.encode(
											jObj.getString("FlightBookingId"),
											"UTF-8")
									+ "&supplierPnr="
									+ URLEncoder.encode(
											jObj.getString("SupplierPnr"),
											"UTF-8");

							btnCancelTicket
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											 new
											 CancelBackService().execute(urlParams);
										}
									});

							btnViewTicket
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											Intent voucher = new Intent(
													getApplicationContext(),
													WebViewActivity.class);
											voucher.putExtra(
													"url",
													CommonFunctions.main_url
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

					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NullPointerException e) {
						// Log exception
						e.printStackTrace();
						handler.sendEmptyMessage(3);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {

					try {
						JSONObject jObj = new JSONObject(result);
						jObj = jObj.getJSONObject("Voucher");
						parseHotelData(jObj);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (loaderDialog.isShowing())
					loaderDialog.dismiss();
				super.onPostExecute(result);
			}
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

	private void printTable(JSONArray result) {
		// TODO Auto-generated method stub

		int i = 0;
		final int length = result.length();

		JSONObject jObj = null;
		String tempDateTime = null;
		for (i = 0; i < length; ++i) {

			final View view = getLayoutInflater().inflate(
					R.layout.item_mybooking_table, null);

			try {
				view.setId(i);
				jObj = result.getJSONObject(i);
				((TextView) view.findViewById(R.id.tv_reference_id))
						.setText(jObj.getString("TransactionID"));
				tempDateTime = jObj.has("DateOfTravel") ? jObj
						.getString("DateOfTravel") : jObj
						.getString("BookingDate");
				final Button btnView = (Button) view
						.findViewById(R.id.btn_view_booking);
				btnView.setTag(i);

				if (i == 0) {
					btnView.setVisibility(View.GONE);
					new GetBookingDetailsBackMethod().execute(jObj
							.has("FlightBookingId") ? jObj
							.getString("FlightBookingId") : jObj
							.getString("TransactionID"));
					strSelectedTransactionID = jObj.has("TransactionID") ? jObj
							.getString("TransactionID") : null;
				}

				final String strBookingId = jObj.has("FlightBookingId") ? jObj
						.getString("FlightBookingId") : jObj
						.getString("TransactionID");
				btnView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						hideButton(view.getId(), length);
						new GetBookingDetailsBackMethod().execute(strBookingId);
						btnView.setVisibility(View.GONE);
					}
				});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tempDateTime = tempDateTime.substring(6, tempDateTime.length() - 2);

			tempDateTime = new SimpleDateFormat("MMM dd, yyyy",
					new Locale("en")).format(new Date(Long
					.parseLong(tempDateTime)));

			((TextView) view.findViewById(R.id.tv_date_of_travel))
					.setText(tempDateTime);

			llTable.addView(view);
		}

		svTable.setVisibility(View.VISIBLE);
	}

	private void hideButton(int Id, int length) {
		// TODO Auto-generated method stub
		int i = 0;
		for (i = 0; i < length; ++i) {

			final View view = llTable.findViewById(i);

			Button btn = (Button) view.findViewById(R.id.btn_view_booking);

			if (i != Id)
				btn.setVisibility(View.VISIBLE);

		}

	}

	private void parseFlightData(JSONArray jarray) {
		try {

			String AirlineName, FromTime, FromDate, FlightNo, Origin, OriginCity, EndTime, EndDate, Destination, DestinationCity;

			LinearLayout llFlight = null;
			llFlight = (LinearLayout) findViewById(R.id.ll_flight4);
			llFlight.removeAllViews();
			llFlight = (LinearLayout) findViewById(R.id.ll_flight3);
			llFlight.removeAllViews();
			llFlight = (LinearLayout) findViewById(R.id.ll_flight2);
			llFlight.removeAllViews();
			llFlight = (LinearLayout) findViewById(R.id.ll_flight1);
			llFlight.removeAllViews();
			lldeparture_2.setVisibility(View.GONE);
			lldeparture_3.setVisibility(View.GONE);
			lldeparture_4.setVisibility(View.GONE);

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
						llFlight = (LinearLayout) findViewById(R.id.ll_flight1);
						break;

					case 2:
						lldeparture_2.setVisibility(View.VISIBLE);
						llFlight = (LinearLayout) findViewById(R.id.ll_flight2);
						break;

					case 3:
						lldeparture_3.setVisibility(View.VISIBLE);
						llFlight = (LinearLayout) findViewById(R.id.ll_flight3);
						break;

					case 4:
						lldeparture_4.setVisibility(View.VISIBLE);
						llFlight = (LinearLayout) findViewById(R.id.ll_flight4);
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

				llFlight.addView(view);

			}
			llFlightDetails.setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.tv_focus)).requestFocusFromTouch();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private void parseHotelData(JSONObject jObj) {

		try {

			Log.e("hotel object", jObj.toString());

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
					.contains("booked")
					&& strTabType.equalsIgnoreCase("Upcoming hotel")) {

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
										+ strSelectedTransactionID
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
			llHotelDetails.setVisibility(View.VISIBLE);
			llHotelDetails.requestFocusFromTouch();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
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

				if (strTabType.contains("flight")) {
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

				if (strTabType.contains("flight")) {
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
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyBookingActivity.this);
				alertDialog.setMessage(result);

				alertDialog.setPositiveButton(getResources().getString(R.string.ok),
						new AlertDialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								new GetBookingDetailsBackMethod().execute(strSelectedTransactionID);
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

