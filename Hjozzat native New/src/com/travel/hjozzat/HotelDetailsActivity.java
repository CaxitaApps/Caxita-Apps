package com.travel.hjozzat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hjozzat.support.CommonFunctions;
import com.hjozzat.support.ImageDownloaderTask;

public class HotelDetailsActivity extends FragmentActivity {

	private Locale myLocale;
	String strRequestUrl = "", strImgUrl = null, strReqPaxUrl = "",
			strRoomCombination = "";
	Double pri;
	boolean blCancellation = false, isZoomed = false;

	// private ViewFlipper mViewFlipper;
	ImageView ivHotel, ivHotelImage;
	ImageButton ibZoom;
	TextView tvHotelDesc;

	RelativeLayout rlContinue, rlHotelImage;

	LinearLayout llHotelRoomsList;
	Dialog loaderDialog;

	static Bitmap bmp = null;

	int[] roomCom = new int[5];
	String[] roomPrice = new String[5];
	int roomCount = 0, tempCount = 0;;

	String strSessionId = null, strNightCount;

	String strHotelName = null, strHotelAddress = null;

	JSONArray jRoomTypeDetails = null;

	public static Activity activityHoteDetails;

	Bitmap bmpHotelImage;
	Dialog dialogImg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		activityHoteDetails = this;

		getActionBar().hide();
		loadLocale();
		setContentView(R.layout.activity_hotel_details);

		initialize();
		
		setViewValues();
		
		if (!strRequestUrl.equalsIgnoreCase("")) {
			new backMethod().execute();
			loaderDialog.show();
		} else
			finishAffinity();
	}

	private void initialize() {
		strRequestUrl = getIntent().getExtras().getString("url", "");
		strImgUrl = getIntent().getExtras().getString("img_url", "");
		strSessionId = getIntent().getExtras().getString("sessionID", "");
		float rating = getIntent().getFloatExtra("hotelRating", 0);
		((RatingBar) findViewById(R.id.rb_hotel_ratng)).setRating(rating);
		final Double lat = getIntent().getExtras().getDouble("latitude");
		final Double lon = getIntent().getExtras().getDouble("longitude");

		final LatLng HOTE = new LatLng(lat, lon);

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);

		mapFragment.getMapAsync(new OnMapReadyCallback() {

			@Override
			public void onMapReady(final GoogleMap map) {
				// TODO Auto-generated method stub
				map.addMarker(new MarkerOptions()
						.position(new LatLng(lat, lon)).icon(
								BitmapDescriptorFactory
										.fromResource(R.drawable.ic_marker)));
				final View mapView = getSupportFragmentManager()
						.findFragmentById(R.id.map).getView();
				map.getUiSettings().setZoomControlsEnabled(false);
				map.setOnMarkerClickListener(new OnMarkerClickListener() {

					@Override
					public boolean onMarkerClick(Marker arg0) {
						// TODO Auto-generated method stub
						Intent marker = new Intent(HotelDetailsActivity.this,
								MarkeActivity.class);
						marker.putExtra("HotelName", strHotelName);
						marker.putExtra("HotelAddress", strHotelAddress);
						marker.putExtra("Latitude", lat);
						marker.putExtra("Langitude", lon);
						marker.putExtra("isSingle", true);
						startActivity(marker);
						return false;
					}
				});
				map.setOnMapClickListener(new OnMapClickListener() {

					@Override
					public void onMapClick(LatLng arg0) {
						// TODO Auto-generated method stub
						Intent marker = new Intent(HotelDetailsActivity.this,
								MarkeActivity.class);
						marker.putExtra("HotelName", strHotelName);
						marker.putExtra("HotelAddress", strHotelAddress);
						marker.putExtra("Latitude", lat);
						marker.putExtra("Langitude", lon);
						marker.putExtra("isSingle", true);
						startActivity(marker);
					}
				});
				if (mapView.getViewTreeObserver().isAlive()) {
					mapView.getViewTreeObserver().addOnGlobalLayoutListener(
							new OnGlobalLayoutListener() {
								@SuppressWarnings("deprecation")
								// We use the new method when supported
								@SuppressLint("NewApi")
								// We check which build version we are using.
								@Override
								public void onGlobalLayout() {
									LatLngBounds bounds = new LatLngBounds.Builder()
											.include(HOTE).build();
									if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
										mapView.getViewTreeObserver()
												.removeGlobalOnLayoutListener(
														this);
									} else {
										mapView.getViewTreeObserver()
												.removeOnGlobalLayoutListener(
														this);
									}
									map.moveCamera(CameraUpdateFactory
											.newLatLngBounds(bounds, 50));
									map.animateCamera(CameraUpdateFactory
											.zoomTo(16.0f));
								}
							});
				}
			}
		});

		loaderDialog = new Dialog(HotelDetailsActivity.this,
				android.R.style.Theme_Translucent);
		loaderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loaderDialog.getWindow().setGravity(Gravity.TOP);
		loaderDialog.setContentView(R.layout.dialog_loader);
		loaderDialog.setCancelable(false);

		((ImageView) loaderDialog.findViewById(R.id.iv_loader))
				.setImageResource(R.drawable.hotel_loader);

		rlHotelImage = (RelativeLayout) findViewById(R.id.rl_hotel_image);
		rlContinue = (RelativeLayout) findViewById(R.id.rl_continue);

		ivHotel = (ImageView) findViewById(R.id.iv_hotel);
		ibZoom = (ImageButton) findViewById(R.id.ib_zoom);
		
		tvHotelDesc = (TextView) findViewById(R.id.tv_hotel_description);

		ivHotelImage = new ImageView(HotelDetailsActivity.this);
		ivHotelImage.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
		ivHotelImage.setScaleType(ScaleType.FIT_XY);
		
		dialogImg = new Dialog(HotelDetailsActivity.this, android.R.style.Theme_DeviceDefault_Dialog_MinWidth);
		dialogImg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogImg.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
		dialogImg.setContentView(ivHotelImage);
	}
	
	private void setViewValues() {
		TextView tvCity = (TextView) findViewById(R.id.tv_Hotel_city);
		TextView tvCheckinDate = (TextView) findViewById(R.id.tv_checkin_date);
		TextView tvCheckoutDate = (TextView) findViewById(R.id.tv_checkout_date);
		TextView tvpassengerCount = (TextView) findViewById(R.id.tv_passenger_count);
		TextView tvroomCount = (TextView) findViewById(R.id.tv_room_count);
		tvCity.setText(HotelResultActivity.strCity);
		tvCheckinDate.setText(HotelResultActivity.strCheckinDate);
		tvCheckoutDate.setText(HotelResultActivity.strCheckoutDate);
		tvpassengerCount.setText(String.valueOf(HotelResultActivity.passengerCount));
		tvroomCount.setText(String.valueOf(HotelResultActivity.roomCount));
	}
	
	public void clicker(View v) {
		switch (v.getId()) {
		case R.id.rl_back:
			finish();
			break;

		case R.id.btn_continue:
			Intent hotelpax = new Intent(HotelDetailsActivity.this,
					HotelPaxActivity.class);
			hotelpax.putExtra("strHotelName", strHotelName);
			hotelpax.putExtra("strHotelAddress", strHotelAddress);
			hotelpax.putExtra("strImgUrl", strImgUrl);
			hotelpax.putExtra("total_price", pri);
			hotelpax.putExtra("NightCount", strNightCount);
			hotelpax.putExtra("roomCombination", strRoomCombination);
			hotelpax.putExtra("jRoomTypeDetails", jRoomTypeDetails.toString());
			hotelpax.putExtra("request_url", strReqPaxUrl);
			startActivity(hotelpax);
			break;

		case R.id.ib_zoom:
			dialogImg.show();
			
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		super.onBackPressed();
	}

	private class backMethod extends AsyncTask<Void, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			loaderDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String resultString = makePostRequest();

			return resultString != null ? resultString : null;

		}

		@Override
		protected void onPostExecute(final String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null && !blCancellation)
				printResult(result);
			else if (result != null && blCancellation)
				parseCancellation(result);
			if (loaderDialog.isShowing())
				loaderDialog.dismiss();
		}

	}

	private String makePostRequest() {

		// making POST request.
		try {
			URL url = new URL(strRequestUrl);
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

			return resultString;
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

	private void printResult(String result) {
		try {
			if (result != null) {
				final JSONObject jObj = new JSONObject(result);
				JSONArray jArray = null;
				JSONObject jRooms = null;
				String temp = null;
				int i = 0;
				Resources resources = getResources();
				DisplayMetrics metrics = resources.getDisplayMetrics();
				float px = 5 * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);

				strHotelName = jObj.getString("PropName");

				strHotelAddress = jObj.getString("HotelAddress");

				((TextView) findViewById(R.id.tv_hotel_name))
						.setText(strHotelName);
				((TextView) findViewById(R.id.tv_hotel_address))
						.setText(strHotelAddress);

				jArray = jObj.getJSONArray("ImageUrls");
				// String[] imageUrls = { jArray.get(0).toString(),
				// jArray.get(1).toString() };

//				bmpHotelImage = HotelResultActivity
//						.getBitmapFromMemCache(strImgUrl);

//				if (bmpHotelImage == null)
				
				new ImageDownloaderTask(ivHotel).execute(jArray.get(1)
						.toString());
				
				new ImageDownloaderTask(ivHotelImage).execute(jArray.get(1)
						.toString());
//				else
//					ivHotel.setImageBitmap(bmpHotelImage);

				temp = jObj.getString("HotelDescription");
				tvHotelDesc
						.setVisibility(temp == null || temp.equals("") ? View.GONE
								: View.VISIBLE);
				tvHotelDesc.setText(temp);

				if (tvHotelDesc.getLineCount() > 10) {
					tvHotelDesc.setMaxLines(10);
					tvHotelDesc
							.setEllipsize(android.text.TextUtils.TruncateAt.END);

					tvHotelDesc.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (tvHotelDesc.getLineCount() > 10) {
								tvHotelDesc.setMaxLines(10);
								tvHotelDesc
										.setEllipsize(android.text.TextUtils.TruncateAt.END);
							} else {
								tvHotelDesc.setEllipsize(null);
								tvHotelDesc.setMaxLines(Integer.MAX_VALUE);
							}
						}
					});
				}

				temp = "";

				strReqPaxUrl = jObj.getString("ProceedPaxUrl");

				// ImageView ivRoomLogo = null;
				ImageView ivAdult = null;
				ImageView ivChild = null;

				jRoomTypeDetails = jObj.getJSONArray("RoomTypeDetails");

				if (jObj.getBoolean("IsCombination")) {
					llHotelRoomsList = (LinearLayout) findViewById(R.id.ll_hotel_list_1);
					JSONObject jRoomcombination = null;
					JSONArray jRoomcombinationDetails = jObj
							.getJSONArray("Roomcombination");
					// for separate block count
					roomCount = jRoomcombinationDetails.length();

					LinearLayout[] parent = new LinearLayout[roomCount];
					android.widget.LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);
					lparams.setMargins(5, 5, 5, 5);

					int tempLenth, count = 0;
					String[] tempArr;
					LinearLayout parentLayout = null;

					System.out.println("roomCount" + roomCount);

					for (count = 0; count < roomCount; ++count) {

						jRoomcombination = jRoomcombinationDetails
								.getJSONObject(count);

						parent[count] = new LinearLayout(this); // base layout
						parent[count].setLayoutParams(lparams);
						parent[count].setOrientation(LinearLayout.VERTICAL);
						parent[count].setTag(jRoomcombination
								.getString("RoomCombination"));
						parent[count]
								.setBackgroundResource(R.drawable.white_bag_curved_border);

						llHotelRoomsList.addView(parent[count]);
					}

					parent = null;

					jArray = jObj.getJSONArray("Rooms");

					tempArr = jRoomcombination.get("RoomCombination")
							.toString().split(",");
					tempLenth = tempArr.length;

					System.out.println("Rooms Array length " + jArray.length());

					View vRooms = null;
					for (i = 0; i < jArray.length(); ++i) {
						jRooms = jArray.getJSONObject(i);

						parentLayout = (LinearLayout) llHotelRoomsList
								.findViewWithTag(jRooms.getString("RoomTag"));

						vRooms = getLayoutInflater().inflate(
								R.layout.hotel_details_item, null);

						((TextView) vRooms.findViewById(R.id.tv_room_name))
								.setText(jRooms.getString("RoomName"));

						final LinearLayout llPassenger = (LinearLayout) vRooms
								.findViewById(R.id.ll_passenger_icons);

						int tempNos = 0;
						for (tempNos = 0; tempNos < jRooms.getInt("AdultCount"); ++tempNos) {
							ivAdult = new ImageView(getApplicationContext());
							ivAdult.setScaleType(ImageView.ScaleType.FIT_CENTER);
							ivAdult.setImageResource(R.drawable.ic_adult);

							llPassenger.addView(ivAdult);
						}

						for (tempNos = 0; tempNos < jRooms.getInt("ChildCount"); ++tempNos) {
							ivChild = new ImageView(getApplicationContext());
							ivChild.setScaleType(ImageView.ScaleType.FIT_CENTER);
							ivChild.setImageResource(R.drawable.ic_adult);
							ivChild.setPadding(0, (int) px, 0, 0);
							llPassenger.addView(ivChild);
						}

						temp = String
								.format(new Locale("en"), "%.3f", Double
										.parseDouble(jRooms
												.getString("PerNightPrice")));
						temp = jRooms.getString("Currency") + " " + temp;
						// + "<strong><font color='#0072bc' size='3'>"
						// + " " + temp + "</font></strong>";
						((TextView) vRooms
								.findViewById(R.id.tv_room_price_per_night))
								.setText(temp);

						temp = jRooms.getString("Currency")
								+ " "
								+ String.format(new Locale("en"), "%.3f",
										Double.parseDouble(jRooms
												.getString("TotalAmount")));

						((TextView) vRooms
								.findViewById(R.id.tv_room_price_per_night))
								.setTag(temp);

						// temp = jRooms.getString("Currency") + " " + temp ;
						// + "<strong><font color='#0072bc' size='3'>"
						// + "</font></strong>";
						// ((TextView) vRooms
						// .findViewById(R.id.tv_room_total_price))
						// .setText(temp);

						// temp = String.format(
						// getResources().getString(
						// R.string.total_price_night),
						// jRooms.getInt("TotalAmount")
						// / jRooms.getInt("PerNightPrice"));
						// ((TextView) vRooms
						// .findViewById(R.id.tv_room_total_price_per_night))
						// .setText(temp);

						final Button btnBuy = (Button) vRooms
								.findViewById(R.id.btn_buy);

						final Button btnCancellation = (Button) vRooms
								.findViewById(R.id.btn_cancellation_policy);

						if (tempLenth != (parentLayout.getChildCount() + 1)) {
							btnCancellation.setVisibility(View.GONE);
							btnBuy.setVisibility(View.GONE);
						} else {
							final String str = jObj.getString(
									"CancellationPolicyUrl")
									.replace(
											"{0}",
											String.valueOf(jRooms
													.getString("RoomTag")));

							btnCancellation
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated
											// method
											// stub
											blCancellation = true;
											strRequestUrl = str;
											new backMethod().execute();
										}
									});

							btnBuy.setTag(jRooms.getString("RoomTag"));
							btnBuy.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method
									// stub
									handleCombinationBuy(btnBuy.getTag()
											.toString());

								}
							});
						}

						parentLayout.addView(vRooms);
					}
					vRooms = null;
					jArray = null;
					jRoomcombinationDetails = null;
				} else {
					JSONObject jRoomType = null;
					roomCount = jRoomTypeDetails.length();
					jArray = jObj.getJSONArray("Rooms");
					for (int j = 0; j < jRoomTypeDetails.length(); ++j) {
						jRoomType = jRoomTypeDetails.getJSONObject(j);

						switch (j) {
						case 0:
							llHotelRoomsList = (LinearLayout) findViewById(R.id.ll_hotel_list_1);
							llHotelRoomsList.setTag("Room list 1");
							break;
						case 1:
							((TextView) findViewById(R.id.tv_choose_first_room))
									.setVisibility(View.VISIBLE);
							((LinearLayout) findViewById(R.id.ll_room2))
									.setVisibility(View.VISIBLE);
							llHotelRoomsList = (LinearLayout) findViewById(R.id.ll_hotel_list_2);
							llHotelRoomsList.setTag("Room list 2");
							break;
						case 2:
							llHotelRoomsList = (LinearLayout) findViewById(R.id.ll_hotel_list_3);
							((LinearLayout) findViewById(R.id.ll_room3))
									.setVisibility(View.VISIBLE);
							llHotelRoomsList.setTag("Room list 3");
							break;
						case 3:
							llHotelRoomsList = (LinearLayout) findViewById(R.id.ll_hotel_list_4);
							((LinearLayout) findViewById(R.id.ll_room4))
									.setVisibility(View.VISIBLE);
							llHotelRoomsList.setTag("Room list 4");
							break;
						case 4:
							llHotelRoomsList = (LinearLayout) findViewById(R.id.ll_hotel_list_5);
							((LinearLayout) findViewById(R.id.ll_room5))
									.setVisibility(View.VISIBLE);
							llHotelRoomsList.setTag("Room list 5");
							break;
						default:
							break;
						}

						for (i = 0; i < jArray.length(); ++i) {
							jRooms = jArray.getJSONObject(i);

							if (jRooms.getString("RoomTag").contains(
									jRoomType.getString("RoomTypeIdentifier"))) {
								final View vRooms = getLayoutInflater()
										.inflate(R.layout.hotel_details_item,
												null);

								((TextView) vRooms
										.findViewById(R.id.tv_room_name))
										.setText(jRooms.getString("RoomName"));
								vRooms.setTag(jRooms.getString("RoomId")
										+ llHotelRoomsList.getTag().toString());

								final String str = jObj.getString(
										"CancellationPolicyUrl")
										.replace(
												"{0}",
												String.valueOf(jRooms
														.getInt("RoomId")));

								((Button) vRooms
										.findViewById(R.id.btn_cancellation_policy))
										.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {
												// TODO Auto-generated method
												// stub
												blCancellation = true;
												strRequestUrl = str;
												new backMethod().execute();
											}
										});

								final LinearLayout llPassenger = (LinearLayout) vRooms
										.findViewById(R.id.ll_passenger_icons);

								int tempNos = 0;
								for (tempNos = 0; tempNos < jRooms
										.getInt("AdultCount"); ++tempNos) {
									ivAdult = new ImageView(
											getApplicationContext());
									ivAdult.setScaleType(ImageView.ScaleType.FIT_CENTER);
									ivAdult.setImageResource(R.drawable.ic_adult);

									llPassenger.addView(ivAdult);
								}

								for (tempNos = 0; tempNos < jRooms
										.getInt("ChildCount"); ++tempNos) {
									ivChild = new ImageView(
											getApplicationContext());
									ivChild.setScaleType(ImageView.ScaleType.FIT_CENTER);
									ivChild.setImageResource(R.drawable.ic_child);
									ivChild.setPadding(0, (int) px, 0, 0);

									llPassenger.addView(ivChild);
								}

								temp = String.format(new Locale("en"), "%.3f",
										Double.parseDouble(jRooms
												.getString("PerNightPrice")));
								temp = jRooms.getString("Currency") + " "
										+ temp;

								((TextView) vRooms
										.findViewById(R.id.tv_room_price_per_night))
										.setText(temp);

								temp = jRooms.getString("Currency")
										+ " "
										+ String.format(
												new Locale("en"),
												"%.3f",
												Double.parseDouble(jRooms
														.getString("TotalAmount")));

								((TextView) vRooms
										.findViewById(R.id.tv_room_price_per_night))
										.setTag(temp);

								final Button btnBuy = (Button) vRooms
										.findViewById(R.id.btn_buy);
								btnBuy.setId(jRooms.getInt("RoomId"));
								btnBuy.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										if (btnBuy
												.getText()
												.toString()
												.equals(getResources()
														.getString(
																R.string.book))) {
											++tempCount;
											handleNonCombinationBuy(
													vRooms.getTag().toString(),
													true,
													btnBuy.getId(),
													((TextView) vRooms
															.findViewById(R.id.tv_room_price_per_night))
															.getTag()
															.toString());
											btnBuy.setText(getResources()
													.getString(R.string.change));
										} else {
											--tempCount;
											handleNonCombinationBuy(
													vRooms.getTag().toString(),
													false,
													btnBuy.getId(),
													((TextView) vRooms
															.findViewById(R.id.tv_room_price_per_night))
															.getTag()
															.toString());
											btnBuy.setText(getResources()
													.getString(R.string.book));
										}

									}
								});

								llHotelRoomsList.addView(vRooms);
							}
						}
					}
				}

				strNightCount = String.valueOf(jRooms.getInt("TotalAmount")
						/ jRooms.getInt("PerNightPrice"));

			}
			System.out
					.println("------------------Parsing finished-------------");
			if (loaderDialog.isShowing())
				loaderDialog.dismiss();

		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("JSON Parser", "Error parsing data " + e.toString());
			handler.sendEmptyMessage(3);
		} catch (NullPointerException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(3);
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessage(2);
		}
	}

	protected void handleCombinationBuy(String strRoomCombination) {
		// TODO Auto-generated method stub

		LinearLayout parentLayout = null;
		parentLayout = (LinearLayout) llHotelRoomsList
				.findViewWithTag(strRoomCombination);
		int i = 0, lenth = parentLayout.getChildCount();
		String temp = null;
		pri = 0.0;
		for (i = 0; i < lenth; ++i) {
			View v = parentLayout.getChildAt(i);
			temp = ((TextView) v.findViewById(R.id.tv_room_price_per_night))
					.getTag().toString();
			temp = temp.substring(4, temp.length());
			pri = Double.parseDouble(temp);
		}

		strReqPaxUrl = strReqPaxUrl.replace("{0},", strRoomCombination);
		strRoomCombination = strRoomCombination.substring(0,
				strRoomCombination.length() - 1);

		Intent hotelpax = new Intent(HotelDetailsActivity.this,
				HotelPaxActivity.class);
		hotelpax.putExtra("strHotelName", strHotelName);
		hotelpax.putExtra("strHotelAddress", strHotelAddress);
		hotelpax.putExtra("strImgUrl", strImgUrl);
		hotelpax.putExtra("total_price", pri);
		hotelpax.putExtra("NightCount", strNightCount);
		hotelpax.putExtra("roomCombination", strRoomCombination);
		hotelpax.putExtra("jRoomTypeDetails", jRoomTypeDetails.toString());
		hotelpax.putExtra("request_url", strReqPaxUrl);
		startActivity(hotelpax);

	}

	private void handleNonCombinationBuy(String id, boolean hide, int RoomId,
			String price) {
		LinearLayout llTemp;
		price = price.substring(4, price.length());
		if (id.contains("Room list 1")) {
			llTemp = (LinearLayout) findViewById(R.id.ll_hotel_list_1);
			roomCom[0] = hide ? RoomId : -1;
			roomPrice[0] = hide ? price : null;
		} else if (id.contains("Room list 2")) {
			llTemp = (LinearLayout) findViewById(R.id.ll_hotel_list_2);
			roomCom[1] = hide ? RoomId : -1;
			roomPrice[1] = hide ? price : null;
		} else if (id.contains("Room list 3")) {
			llTemp = (LinearLayout) findViewById(R.id.ll_hotel_list_3);
			roomCom[2] = hide ? RoomId : -1;
			roomPrice[2] = hide ? price : null;
		} else if (id.contains("Room list 4")) {
			llTemp = (LinearLayout) findViewById(R.id.ll_hotel_list_4);
			roomCom[3] = hide ? RoomId : -1;
			roomPrice[3] = hide ? price : null;
		} else {
			llTemp = (LinearLayout) findViewById(R.id.ll_hotel_list_5);
			roomCom[4] = hide ? RoomId : -1;
			roomPrice[4] = hide ? price : null;
		}

		if (hide) {
			for (int i = 0; i < llTemp.getChildCount(); ++i) {
				View vRooms = llTemp.getChildAt(i);
				if (!vRooms.getTag().toString().equalsIgnoreCase(id))
					vRooms.setVisibility(View.GONE);
			}
		} else {
			for (int i = 0; i < llTemp.getChildCount(); ++i) {
				View vRooms = llTemp.getChildAt(i);
				vRooms.setVisibility(View.VISIBLE);
			}
		}

		if (hide && tempCount == roomCount) {
			pri = 0.0;
			for (int no = 0; no < roomCount; ++no) {
				strRoomCombination = strRoomCombination + roomCom[no] + ",";
				pri = pri + Double.parseDouble(roomPrice[no]);
			}

			strRoomCombination = (String) strRoomCombination.subSequence(0,
					strRoomCombination.length() - 1);

			strReqPaxUrl = strReqPaxUrl.replace("{0},", strRoomCombination);

			rlContinue.setVisibility(View.VISIBLE);
		} else
			rlContinue.setVisibility(View.GONE);
	}

	private void parseCancellation(String result) {
		try {
			if (result != null) {
				JSONObject jObj = new JSONObject(result);
				JSONArray jArray = jObj.getJSONArray("room");
				jObj = jArray.getJSONObject(0);

				String temp = jObj.getString("CancellationPolicy");

				if (jObj.has("TariffNotes")) {
					temp = temp + "\n\n"
							+ getResources().getString(R.string.tariff_notes)
							+ "\n\n" + jObj.getString("TariffNotes");
				}

				if (jObj.has("Remarks")) {

					temp = temp + "\n\n"
							+ getResources().getString(R.string.hotel_remarks)
							+ "\n\n" + jObj.getString("Remarks");

				}
				showAlert(temp);
			}

			System.out
					.println("------------------Parsing finished-------------");

		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("JSON Parser", "Error parsing data " + e.toString());
			handler.sendEmptyMessage(3);
		} catch (NullPointerException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(3);
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessage(2);
		}
	}

	private static String convertStreamToString(InputStream is) {
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
				if (loaderDialog.isShowing())
					loaderDialog.dismiss();
				showAlert("There is a problem on your Network. Please try again later.");

			} else if (msg.what == 2) {

				if (loaderDialog.isShowing())
					loaderDialog.dismiss();
				showAlert("There is a problem on your application. Please report it.");

			} else if (msg.what == 3) {
				if (loaderDialog.isShowing())
					loaderDialog.dismiss();
				showAlert("Something went wrong. Please try again later");
			}

		}
	};

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

	public void showAlert(String msg) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		if (blCancellation)
			alertDialog.setTitle(getResources().getString(
					R.string.cancellation_policy));
		alertDialog.setMessage(msg);

		alertDialog.setPositiveButton(getResources().getString(R.string.ok),
				new AlertDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (!blCancellation)
							finish();
					}
				});

		alertDialog.setCancelable(false);
		alertDialog.show();
	}

}
