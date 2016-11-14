package com.travel.hjozzat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hjozzat.support.CommonFunctions;
import com.hjozzat.support.RangeSeekBar;
import com.hjozzat.support.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.travel.hjozzat.R;
import com.travel.hjozzat.adapter.FlightResultAdapter;
import com.travel.hjozzat.model.FlightResultItem;

public class FlightResultActivity extends Activity implements
		OnItemClickListener {

	private Locale myLocale;
	String strFlightType, strFromCode, strToCode; // , strFromCity, strToCity;
	String strFromDate, strToDate;
	public static int adultCount = 1, childCount = 0, infantCount = 0, tripNo;
	boolean isRoundTrip = false;
	public static String flightClass = null;
	public static boolean blChild;

	TextView tvProgressText, tvCurrency;
	static ListView lvFlightResult;
	ScrollView svResult;
	public static ArrayList<FlightResultItem> flightResultItem, groupedResult,
			flightResultItemsTemp, filteredResult;
	static ArrayList<String> arrayAirline, checkedAirlines;
	static ArrayList<String> arrayAirports, checkedAirports;
//	static ArrayList<String> arraySourceAirports, checkedSourceAirports;
//	static ArrayList<String> arrayDestinationAirports,
//			checkedDestinationAirports;
	
	String str_url = "";
	String main_url = "";

	static String flightName, flightLogo, departureDateTime,
			departureTimeString;
	static String arrivalDateTime, arrivalTimeString, totalDurationInMinutes;
	static int stops;
	Boolean blHasNonStop = false, blHasOneStop = false, blHasMultStop = false;
	Boolean blNonStop = false, blOneStop = false, blMultiStop = false;
	Boolean blSortPrice = true, blSortDep = false, blSortArrival = false,
			blSortDuration = false, blSortAirName = false;
	String strSortPriceType = "Low", strSortDepType = null,
			strSortArrivalType = null, strSortDurationType = null,
			strSortAirNameType = null;

	public static Activity activityResult;

	// sort
	ImageView ivTimeSort, ivDurationSort, ivPriceSort;

	static String str12a6aFromOut, str6a12pFromOut, str12p6pFromOut,
			str6p12aFromOut, str12a6aToOut, str6a12pToOut, str12p6pToOut,
			str6p12aToOut, str12a6aFromRet, str6a12pFromRet, str12p6pFromRet,
			str6p12aFromRet, str12a6aToRet, str6a12pToRet, str12p6pToRet,
			str6p12aToRet;

	static Double filterMinPrice = 0.0, filterMaxPrice = 0.0;
	static Long filterDepLow, filterDepHigh, filterArrLow, filterArrHigh,
			filterLayLow, filterLayHigh;
	Long filterMinDep, filterMaxDep, filterMinArr, filterMaxArr, filterMinLay,
			filterMaxLay;
	Boolean blPriceFilter = false, blDepTimeFilter = false,
			blArrTimeFilter = false, blStopFilter = false,
			blNameFilter = false, blLayoverFilter = false,
			blLayAirportFilter = false;
	Boolean blOutbound = true, blReturn = false, bl12a6aFrom = false,
			bl6a12pFrom = false, bl12p6pFrom = false, bl6p12aFrom = false,
			bl12a6aTo = false, bl6a12pTo = false, bl12p6pTo = false,
			bl6p12aTo = false, blCurr = false;
	Dialog dialogSort, loaderDialog, curr, dialogFilter;
	CommonFunctions cf;

	public static String strSessionId = null;
	public static String strDetails = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		activityResult = this;

		cf = new CommonFunctions(this);
		loadLocale();

		setContentView(R.layout.activity_search_result_flight);

		initialize();
		setHeader();

		if (CommonFunctions.modify) {
			strSessionId = CommonFunctions.strSearchId;
			flightResultItem.addAll(CommonFunctions.flighResult);
			lvFlightResult.setAdapter(new FlightResultAdapter(
					FlightResultActivity.this, flightResultItem, isRoundTrip));
			setDefaultValues();
			CommonFunctions.modify = false;
			CommonFunctions.strSearchId = null;
			CommonFunctions.flighResult.clear();
			for (FlightResultItem fItem : flightResultItem) {
				if (!arrayAirline.contains(fItem.str_AirlineName)
						&& !fItem.str_AirlineName.equalsIgnoreCase(""))
					arrayAirline.add(fItem.str_AirlineName);
			}
		} else {

			new backMethod().execute("");
		}

	}

	private void initialize() {
		// TODO Auto-generated method stub
		lvFlightResult = (ListView) findViewById(R.id.lv_flight_result);

		tvCurrency = (TextView) findViewById(R.id.tv_currency);
		tvCurrency.setText(CommonFunctions.strCurrency);

		loaderDialog = new Dialog(FlightResultActivity.this,
				android.R.style.Theme_Translucent);
		loaderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loaderDialog.getWindow().setGravity(Gravity.TOP);
		loaderDialog.setContentView(R.layout.dialog_loader);
		loaderDialog.setCancelable(false);

		((ImageView) loaderDialog.findViewById(R.id.iv_loader))
				.setImageResource(R.drawable.flight_loader);

		tvProgressText = (TextView) loaderDialog
				.findViewById(R.id.tv_progress_text);
		tvProgressText.setText(getResources().getString(
				R.string.searching_flight));
		tvProgressText.setVisibility(View.VISIBLE);

		dialogSort = new Dialog(FlightResultActivity.this,
				android.R.style.Theme_Translucent);
		dialogSort.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSort.setContentView(R.layout.dialog_sort);

		ivTimeSort = (ImageView) findViewById(R.id.iv_time_sort);
		ivDurationSort = (ImageView) findViewById(R.id.iv_duration_sort);
		ivPriceSort = (ImageView) findViewById(R.id.iv_price_sort);

		flightResultItem = new ArrayList<FlightResultItem>();
		flightResultItemsTemp = new ArrayList<FlightResultItem>();
		arrayAirline = new ArrayList<String>();
		checkedAirlines = new ArrayList<String>();
		arrayAirports = new ArrayList<String>();
		checkedAirports = new ArrayList<String>();
//		arraySourceAirports = new ArrayList<String>();
//		checkedSourceAirports = new ArrayList<String>();
//		arrayDestinationAirports = new ArrayList<String>();
//		checkedDestinationAirports = new ArrayList<String>();

		main_url = CommonFunctions.main_url + CommonFunctions.lang
				+ "/FlightApi/FlightSearchApi?";

	}
	
	public static Bundle bundle;

	private void setHeader() {
		// TODO Auto-generated method stub
		bundle = getIntent().getExtras();
		
		LinearLayout llHeader = (LinearLayout) findViewById(R.id.ll_header_city);
		TextView tvFlightDates = (TextView) findViewById(R.id.tv_date);
		
		str_url = getIntent().getExtras().getString("url", "");
		str_url = str_url + "&searchID=";
		strFlightType = bundle.getString("flight_type", "");
		flightClass = bundle.getString("class", "");
		tripNo = bundle.getInt("trip_nos");

		Resources resources = getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		int px = 5 * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
		LayoutParams llParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		llParams.setMargins(px, px, px, px);

		if (!strFlightType.equalsIgnoreCase("Multicity")) {

			strFromCode = bundle.getString("from1", "");
			strToCode = bundle.getString("to1", "");

			strFromDate = bundle.getString("departure_date",
					"");
			strDetails = strFromDate.substring(4, strFromDate.length());
			strToDate = "";

			if (strFlightType.equalsIgnoreCase("RoundTrip")) {
				strToDate = bundle.getString("arrival_date",
						"");
				strDetails = strDetails + " - " + strToDate.substring(4, strToDate.length());
				isRoundTrip = true;
			}

			TextView tvFrom = new TextView(FlightResultActivity.this);
			tvFrom.setLayoutParams(llParams);
			tvFrom.setText(strFromCode);
			tvFrom.setTypeface(Typeface.create("sans-serif-condensed",
					Typeface.NORMAL));
			tvFrom.setTextColor(Color.WHITE);
			tvFrom.setTextSize(16f);

			ImageView ivFrom = new ImageView(FlightResultActivity.this);
			ivFrom.setLayoutParams(llParams);
			ivFrom.setImageResource(isRoundTrip ? R.drawable.ic_from_to
					: R.drawable.ic_from);
			ivFrom.setRotation(CommonFunctions.lang.equalsIgnoreCase("en") ? 0
					: 180f);

			TextView tvTo = new TextView(FlightResultActivity.this);
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

				TextView tvFrom1 = new TextView(FlightResultActivity.this);
				tvFrom1.setLayoutParams(llParams);
				tvFrom1.setText(temp1);
				tvFrom1.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvFrom1.setTextColor(Color.WHITE);
				tvFrom1.setTextSize(16f);

				ImageView ivFrom1 = new ImageView(FlightResultActivity.this);
				ivFrom1.setLayoutParams(llParams);
				ivFrom1.setImageResource(R.drawable.ic_from);

				TextView tvTo1 = new TextView(FlightResultActivity.this);
				tvTo1.setLayoutParams(llParams);
				tvTo1.setText(temp2 + ",");
				tvTo1.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvTo1.setTextColor(Color.WHITE);
				tvTo1.setTextSize(16f);

				TextView tvFrom2 = new TextView(FlightResultActivity.this);
				tvFrom2.setLayoutParams(llParams);
				tvFrom2.setText(temp3);
				tvFrom2.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvFrom2.setTextColor(Color.WHITE);
				tvFrom2.setTextSize(16f);

				ImageView ivFrom2 = new ImageView(FlightResultActivity.this);
				ivFrom2.setLayoutParams(llParams);
				ivFrom2.setImageResource(R.drawable.ic_from);

				TextView tvTo2 = new TextView(FlightResultActivity.this);
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

				strDetails = bundle.getString("date1", "").substring(4, bundle.getString("date1", "").length())
						+ " - "
						+ bundle.getString("date2", "").substring(4, bundle.getString("date1", "").length());

				int margin = 30 * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
				((LinearLayout) findViewById(R.id.ll_header)).setPadding(0, 0,
						margin, 0);

			} else if (tripNo == 3) {

				px = 3 * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
				llParams.setMargins(px, px, px, px);

				TextView tvFrom1 = new TextView(FlightResultActivity.this);
				tvFrom1.setLayoutParams(llParams);
				tvFrom1.setText(temp1);
				tvFrom1.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvFrom1.setTextColor(Color.WHITE);
				tvFrom1.setTextSize(15f);

				ImageView ivFrom1 = new ImageView(FlightResultActivity.this);
				ivFrom1.setLayoutParams(llParams);
				ivFrom1.setImageResource(R.drawable.ic_from);

				TextView tvTo1 = new TextView(FlightResultActivity.this);
				tvTo1.setLayoutParams(llParams);
				tvTo1.setText(temp2 + ",");
				tvTo1.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvTo1.setTextColor(Color.WHITE);
				tvTo1.setTextSize(15f);

				TextView tvFrom2 = new TextView(FlightResultActivity.this);
				tvFrom2.setLayoutParams(llParams);
				tvFrom2.setText(temp3);
				tvFrom2.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvFrom2.setTextColor(Color.WHITE);
				tvFrom2.setTextSize(15f);

				ImageView ivFrom2 = new ImageView(FlightResultActivity.this);
				ivFrom2.setLayoutParams(llParams);
				ivFrom2.setImageResource(R.drawable.ic_from);

				TextView tvTo2 = new TextView(FlightResultActivity.this);
				tvTo2.setLayoutParams(llParams);
				tvTo2.setText(temp4 + ",");
				tvTo2.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvTo2.setTextColor(Color.WHITE);
				tvTo2.setTextSize(15f);

				TextView tvFrom3 = new TextView(FlightResultActivity.this);
				tvFrom3.setLayoutParams(llParams);
				tvFrom3.setText(bundle.getString("from3", ""));
				tvFrom3.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvFrom3.setTextColor(Color.WHITE);
				tvFrom3.setTextSize(15f);

				ImageView ivFrom3 = new ImageView(FlightResultActivity.this);
				ivFrom3.setLayoutParams(llParams);
				ivFrom3.setImageResource(R.drawable.ic_from);

				TextView tvTo3 = new TextView(FlightResultActivity.this);
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

				strDetails = bundle.getString("date1", "").substring(4, bundle.getString("date1", "").length())
						+ " - "
						+ bundle.getString("date2", "").substring(4, bundle.getString("date1", "").length())
						+ " - "
						+ bundle.getString("date3", "").substring(4, bundle.getString("date1", "").length());

				int margin = 10 * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
				((LinearLayout) findViewById(R.id.ll_header)).setPadding(0, 0,
						margin, 0);

			} else {
				px = 2 * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
				llParams.setMargins(px, px, px, px);

				TextView tvFrom1 = new TextView(FlightResultActivity.this);
				tvFrom1.setLayoutParams(llParams);
				tvFrom1.setText(temp1);
				tvFrom1.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvFrom1.setTextColor(Color.WHITE);
				tvFrom1.setTextSize(14f);

				ImageView ivFrom1 = new ImageView(FlightResultActivity.this);
				ivFrom1.setLayoutParams(llParams);
				ivFrom1.setImageResource(R.drawable.ic_from);

				TextView tvTo1 = new TextView(FlightResultActivity.this);
				tvTo1.setLayoutParams(llParams);
				tvTo1.setText(temp2 + ",");
				tvTo1.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvTo1.setTextColor(Color.WHITE);
				tvTo1.setTextSize(14f);

				TextView tvFrom2 = new TextView(FlightResultActivity.this);
				tvFrom2.setLayoutParams(llParams);
				tvFrom2.setText(temp3);
				tvFrom2.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvFrom2.setTextColor(Color.WHITE);
				tvFrom2.setTextSize(14f);

				ImageView ivFrom2 = new ImageView(FlightResultActivity.this);
				ivFrom2.setLayoutParams(llParams);
				ivFrom2.setImageResource(R.drawable.ic_from);

				TextView tvTo2 = new TextView(FlightResultActivity.this);
				tvTo2.setLayoutParams(llParams);
				tvTo2.setText(temp4 + ",");
				tvTo2.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvTo2.setTextColor(Color.WHITE);
				tvTo2.setTextSize(14f);

				TextView tvFrom3 = new TextView(FlightResultActivity.this);
				tvFrom3.setLayoutParams(llParams);
				tvFrom3.setText(bundle.getString("from3", ""));
				tvFrom3.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvFrom3.setTextColor(Color.WHITE);
				tvFrom3.setTextSize(14f);

				ImageView ivFrom3 = new ImageView(FlightResultActivity.this);
				ivFrom3.setLayoutParams(llParams);
				ivFrom3.setImageResource(R.drawable.ic_from);

				TextView tvTo3 = new TextView(FlightResultActivity.this);
				tvTo3.setLayoutParams(llParams);
				tvTo3.setText(bundle.getString("to3", "")
						+ ",");
				tvTo3.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvTo3.setTextColor(Color.WHITE);
				tvTo3.setTextSize(14f);

				TextView tvFrom4 = new TextView(FlightResultActivity.this);
				tvFrom4.setLayoutParams(llParams);
				tvFrom4.setText(bundle.getString("from3", ""));
				tvFrom4.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvFrom4.setTextColor(Color.WHITE);
				tvFrom4.setTextSize(14f);

				ImageView ivFrom4 = new ImageView(FlightResultActivity.this);
				ivFrom4.setLayoutParams(llParams);
				ivFrom4.setImageResource(R.drawable.ic_from);

				TextView tvTo4 = new TextView(FlightResultActivity.this);
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

				strDetails = bundle.getString("date1", "").substring(4, bundle.getString("date1", "").length())
						+ " - "
						+ bundle.getString("date2", "").substring(4, bundle.getString("date1", "").length())
						+ " - "
						+ bundle.getString("date3", "").substring(4, bundle.getString("date1", "").length())
						+ " - "
						+ bundle.getString("date4", "").substring(4, bundle.getString("date1", "").length());

				int margin = 5 * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
				((LinearLayout) findViewById(R.id.ll_header)).setPadding(0, 0,
						margin, 0);
			}

		}
		adultCount = getIntent().getIntExtra("adult_count", 1);
		childCount = getIntent().getIntExtra("child_count", 0);
		infantCount = getIntent().getIntExtra("infant_count", 0);

		strDetails = strDetails + ", " + adultCount
				+ getResources().getString(R.string.adult);
		strDetails = childCount > 0 ? strDetails + ", " + childCount
				+ getResources().getString(R.string.children) : strDetails;
		strDetails = infantCount > 0 ? strDetails + ", " + infantCount
				+ getResources().getString(R.string.infant) : strDetails;

		tvFlightDates.setText(strDetails);

	}

	private class backMethod extends AsyncTask<String, String, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			flightResultItem.clear();
			flightResultItemsTemp.clear();
			arrayAirline.clear();
			checkedAirlines.clear();
			loaderDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			if (params[0].isEmpty()) {
				String resultString = makePostRequest(false, "");
				if (resultString != null)
					parseFlightResult(resultString);
				groupResult();
			} else if (!params[0].equalsIgnoreCase(CommonFunctions.strCurrency)) {
				String resultString = makePostRequest(true, params[0]);
				if (resultString != null) {
					JSONObject jObj;
					try {
						CommonFunctions.strCurrency = params[0];
						jObj = new JSONObject(resultString);
						jObj = jObj.getJSONObject("data");
						parseFlightResult(jObj.getString("Item"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (loaderDialog.isShowing())
				loaderDialog.dismiss();
			if (groupedResult.size() > 0) {
				blCurr = false;
				tvCurrency.setText(CommonFunctions.strCurrency);
				lvFlightResult.setAdapter(new FlightResultAdapter(
						FlightResultActivity.this, groupedResult, isRoundTrip));
				setDefaultValues();
				System.out
						.println("------------------Finished displaying-------------");
			} else {
				((LinearLayout) findViewById(R.id.ll_filter)).setEnabled(false);
			}
		}
	}

	public void setDefaultValues() {

		flightResultItemsTemp.addAll(groupedResult);
		// setting selected filter vales
		filterMinPrice = (groupedResult).get(0).doubleFlightPrice;
		filterMaxPrice = (groupedResult).get(groupedResult.size() - 1).doubleFlightPrice;

		ArrayList<Long> al = new ArrayList<Long>();
		// ArrayList<Long> a2 = new ArrayList<Long>();
		ArrayList<Long> a3 = new ArrayList<Long>();

		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa",
				new Locale("en"));
		Calendar cal = Calendar.getInstance();

		try {
			cal.setTime(dateFormat.parse("12:00 AM"));
			filterDepLow = cal.getTimeInMillis();
			filterArrLow = cal.getTimeInMillis();

			cal.setTime(dateFormat.parse("11:59 PM"));
			filterDepHigh = cal.getTimeInMillis();
			filterArrHigh = cal.getTimeInMillis();

		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		dateFormat = new SimpleDateFormat("hh:mm aa", new Locale(
				CommonFunctions.lang));

		int minutes = -1;
		for (FlightResultItem fItem : groupedResult) {
			// al.add(fItem.DepartDateTimeOne);
			// a2.add(fItem.ArrivalDateTimeOne);
			a3.add(fItem.longLayoverTimeInMins);

			try {
				cal.setTime(dateFormat.parse(fItem.DepartTimeOne));
				minutes = cal.get(Calendar.HOUR_OF_DAY) * 60
						+ cal.get(Calendar.MINUTE);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (!bl12a6aFrom && minutes > 0 && minutes < 360) {
				str12a6aFromOut = fItem.strDisplayRate;
				bl12a6aFrom = true;
			}
			if (!bl6a12pFrom && minutes > 360 && minutes < 720) {
				str6a12pFromOut = fItem.strDisplayRate;
				bl6a12pFrom = true;
			}
			if (!bl12p6pFrom && minutes > 720 && minutes < 1080) {
				str12p6pFromOut = fItem.strDisplayRate;
				bl12p6pFrom = true;
			}
			if (!bl6p12aFrom && minutes > 1080 && minutes < 1440) {
				str6p12aFromOut = fItem.strDisplayRate;
				bl6p12aFrom = true;
			}

			try {
				cal.setTime(dateFormat.parse(fItem.ArrivalTimeOne));
				minutes = cal.get(Calendar.HOUR_OF_DAY) * 60
						+ cal.get(Calendar.MINUTE);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (!bl12a6aTo && minutes > 0 && minutes < 360) {
				str12a6aToOut = fItem.strDisplayRate;
				bl12a6aTo = true;
			}
			if (!bl6a12pTo && minutes > 360 && minutes < 720) {
				str6a12pToOut = fItem.strDisplayRate;
				bl6a12pTo = true;
			}
			if (!bl12p6pTo && minutes > 720 && minutes < 1080) {
				str12p6pToOut = fItem.strDisplayRate;
				bl12p6pTo = true;
			}
			if (!bl6p12aTo && minutes > 1080 && minutes < 1440) {
				str6p12aToOut = fItem.strDisplayRate;
				bl6p12aTo = true;
			}
		}

		bl12a6aFrom = false;
		bl6a12pFrom = false;
		bl12p6pFrom = false;
		bl6p12aFrom = false;
		bl12a6aTo = false;
		bl6a12pTo = false;
		bl12p6pTo = false;
		bl6p12aTo = false;

		Collections.sort(a3);
		filterLayLow = a3.get(0);
		filterLayHigh = a3.get(a3.size() - 1);

		al.clear();
		for (FlightResultItem fItem : groupedResult) {
			al.add((long) fItem.intFlightStopsOne);
			if (strFlightType.equalsIgnoreCase("RoundTrip")) {
				try {
					cal.setTime(dateFormat.parse(fItem.DepartTimeTwo));
					minutes = cal.get(Calendar.HOUR_OF_DAY) * 60
							+ cal.get(Calendar.MINUTE);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (!bl12a6aFrom && minutes > 0 && minutes < 360) {
					str12a6aFromRet = fItem.strDisplayRate;
					bl12a6aFrom = true;
				}
				if (!bl6a12pFrom && minutes > 360 && minutes < 720) {
					str6a12pFromRet = fItem.strDisplayRate;
					bl6a12pFrom = true;
				}
				if (!bl12p6pFrom && minutes > 720 && minutes < 1080) {
					str12p6pFromRet = fItem.strDisplayRate;
					bl12p6pFrom = true;
				}
				if (!bl6p12aFrom && minutes > 1080 && minutes < 1440) {
					str6p12aFromRet = fItem.strDisplayRate;
					bl6p12aFrom = true;
				}

				try {
					cal.setTime(dateFormat.parse(fItem.ArrivalTimeTwo));
					minutes = cal.get(Calendar.HOUR_OF_DAY) * 60
							+ cal.get(Calendar.MINUTE);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (!bl12a6aTo && minutes > 0 && minutes < 360) {
					str12a6aToRet = fItem.strDisplayRate;
					bl12a6aTo = true;
				}
				if (!bl6a12pTo && minutes > 360 && minutes < 720) {
					str6a12pToRet = fItem.strDisplayRate;
					bl6a12pTo = true;
				}
				if (!bl12p6pTo && minutes > 720 && minutes < 1080) {
					str12p6pToRet = fItem.strDisplayRate;
					bl12p6pTo = true;
				}
				if (!bl6p12aTo && minutes > 1080 && minutes < 1440) {
					str6p12aToRet = fItem.strDisplayRate;
					bl6p12aTo = true;
				}
			}

		}

		bl12a6aFrom = false;
		bl6a12pFrom = false;
		bl12p6pFrom = false;
		bl6p12aFrom = false;
		bl12a6aTo = false;
		bl6a12pTo = false;
		bl12p6pTo = false;
		bl6p12aTo = false;

		blHasNonStop = al.contains((long) 0) ? true : false;
		blHasOneStop = al.contains((long) 1) ? true : false;
		blHasMultStop = (al.contains((long) 2) || al.contains((long) 3)
				|| al.contains((long) 4) || al.contains((long) 5)) ? true
				: false;

		filterMinDep = filterDepLow;
		filterMaxDep = filterDepHigh;
		filterMinArr = filterArrLow;
		filterMaxArr = filterArrHigh;
		filterMinLay = filterLayLow;
		filterMaxLay = filterLayHigh;

//		JSONObject allJourney, listFlight;
//		JSONArray listFlightArray;
//		for (FlightResultItem fItem : groupedResult) {
//				try {
//					allJourney = fItem.jarray.getJSONObject(0);
//					listFlightArray = allJourney.getJSONArray("ListFlight");
//					listFlight = listFlightArray.getJSONObject(0);
//
//					if (!arraySourceAirports.contains(listFlight
//							.getString("DepartureAirportName")))
//						arraySourceAirports.add(listFlight
//								.getString("DepartureAirportName"));
//					
//					listFlight = listFlightArray.getJSONObject(listFlightArray
//							.length() - 1);
//
//					if (!arrayDestinationAirports.contains(listFlight
//							.getString("ArrivalAirportName")))
//						arrayDestinationAirports.add(listFlight
//								.getString("ArrivalAirportName"));
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//		}
		lvFlightResult.setOnItemClickListener(FlightResultActivity.this);

	}

	public void clicker(View v) {
		switch (v.getId()) {
		case R.id.rl_back:
			finish();
			break;

		case R.id.ll_time_sort:
			if (blSortDep) {
				strSortDepType = strSortDepType.equalsIgnoreCase("Low") ? "High"
						: "Low";
				ivTimeSort.setImageResource(strSortDepType
						.equalsIgnoreCase("Low") ? R.drawable.up_arrow
						: R.drawable.down_arrow);
			} else {
				blSortDep = true;
				blSortDuration = false;
				blSortPrice = false;
				strSortDepType = "Low";
				ivTimeSort.setVisibility(View.VISIBLE);
				ivDurationSort.setVisibility(View.INVISIBLE);
				ivPriceSort.setVisibility(View.INVISIBLE);
				ivTimeSort.setImageResource(R.drawable.up_arrow);
			}
			sortArrayList();
			break;
		case R.id.ll_duration_sort:
			if (blSortDuration) {
				strSortDurationType = strSortDurationType
						.equalsIgnoreCase("Low") ? "High" : "Low";
				ivDurationSort.setImageResource(strSortDurationType
						.equalsIgnoreCase("Low") ? R.drawable.up_arrow
						: R.drawable.down_arrow);
			} else {
				blSortDep = false;
				blSortDuration = true;
				blSortPrice = false;
				strSortDurationType = "Low";
				ivTimeSort.setVisibility(View.INVISIBLE);
				ivDurationSort.setVisibility(View.VISIBLE);
				ivPriceSort.setVisibility(View.INVISIBLE);
				ivDurationSort.setImageResource(R.drawable.up_arrow);
			}
			sortArrayList();
			break;
		case R.id.ll_price_sort:

			if (blSortPrice) {
				strSortPriceType = strSortPriceType.equalsIgnoreCase("Low") ? "High"
						: "Low";
				ivPriceSort.setImageResource(strSortPriceType
						.equalsIgnoreCase("Low") ? R.drawable.up_arrow
						: R.drawable.down_arrow);
			} else {
				blSortDep = false;
				blSortDuration = false;
				blSortPrice = true;
				strSortPriceType = "Low";
				ivTimeSort.setVisibility(View.INVISIBLE);
				ivDurationSort.setVisibility(View.INVISIBLE);
				ivPriceSort.setVisibility(View.VISIBLE);
				ivPriceSort.setImageResource(R.drawable.up_arrow);
			}
			sortArrayList();
			break;
		case R.id.ll_filter:
			showFilterDialog();
			break;
		case R.id.ll_currency:
			dialogCurrency();
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
	}

	private String makePostRequest(boolean blCurr, String strCurrency) {

		// making POST request.
		try {

			URL url = null;
			if (blCurr) {
				url = new URL(CommonFunctions.main_url + CommonFunctions.lang
						+ "/FlightApi/CurrencyConvert?currency=" + strCurrency
						+ "&searchID=" + strSessionId);
			} else {
				strSessionId = CommonFunctions.getRandomString(6) + "_";
				url = new URL(main_url + str_url + strSessionId);
			}

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
		} catch (SocketException e) {
			// TODO: handle exception
			e.printStackTrace();
			handler.sendEmptyMessage(1);
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

	private void parseFlightResult(String result) {
		try {
			if (result != null) {
				JSONArray jarray = null;
				// Parse String to JSON object
				jarray = new JSONArray(result);

				if (jarray.length() == 0) {
					handler.sendEmptyMessage(3);
				} else {
					JSONObject c = null, allJourney = null, listFlight = null;
					FlightResultItem fItem = null;

					int length = jarray.length();
					System.out.println("Result count = " + length);
					// if(length > 40)
					// length = 40;

					for (int i = 0; i < length; i++) {
						fItem = new FlightResultItem();
						c = jarray.getJSONObject(i);

						JSONArray allJourneyArray = c
								.getJSONArray("AllJourney");
						for (int j = 0; j < allJourneyArray.length(); j++) {
							allJourney = allJourneyArray.getJSONObject(j);
							JSONArray listFlightArray = allJourney
									.getJSONArray("ListFlight");
							listFlight = listFlightArray.getJSONObject(0);

							flightName = listFlight.getString("FlightName");
							departureDateTime = listFlight
									.getString("DepartureDateTime");
							departureTimeString = listFlight
									.getString("DepartureTimeString");

							listFlight = listFlightArray
									.getJSONObject(listFlightArray.length() - 1);
							arrivalDateTime = listFlight
									.getString("ArrivalDateTime");
							arrivalTimeString = listFlight
									.getString("ArrivalTimeString");

							totalDurationInMinutes = allJourney
									.getString("TotalDurationInMinutes");
							stops = Integer.parseInt(allJourney
									.getString("stops"));

							departureDateTime = departureDateTime.substring(6,
									departureDateTime.length() - 2);
							arrivalDateTime = arrivalDateTime.substring(6,
									arrivalDateTime.length() - 2);

							switch (j) {
							case 0:
								fItem.DepartTimeOne = departureTimeString;
								fItem.ArrivalTimeOne = arrivalTimeString;
								fItem.intFlightStopsOne = stops;

								fItem.longLayoverTimeInMins = listFlightArray
										.getJSONObject(0).getLong(
												"LayoverTimeMinutes");

								if (fItem.intFlightStopsOne > 0) {
									fItem.strLayoverAirport = listFlightArray
											.getJSONObject(0).getString(
													"ArrivalAirportName");
									if (!arrayAirports
											.contains(fItem.strLayoverAirport))
										arrayAirports
												.add(fItem.strLayoverAirport);
								}

								fItem.DepartDateTimeOne = Long
										.parseLong(departureDateTime);
								fItem.ArrivalDateTimeOne = Long
										.parseLong(arrivalDateTime);
								fItem.longFlightDurationInMinsOne = Long
										.parseLong(totalDurationInMinutes);
								fItem.str_AirlineName = flightName;

								if (!arrayAirline
										.contains(fItem.str_AirlineName)
										&& !fItem.str_AirlineName
												.equalsIgnoreCase(""))
									arrayAirline.add(fItem.str_AirlineName);
								break;
							case 1:
								fItem.DepartTimeTwo = departureTimeString;
								fItem.ArrivalTimeTwo = arrivalTimeString;
								// fItem.strFlightStopsTwo = stops;
								// fItem.strFlightDurationTwo = totalDuration;
								fItem.DepartDateTimeTwo = Long
										.parseLong(departureDateTime);
								fItem.ArrivalDateTimeTwo = Long
										.parseLong(arrivalDateTime);
								fItem.longFlightDurationInMinsTwo = Long
										.parseLong(totalDurationInMinutes);

								break;
							case 2:
								fItem.DepartTimeThree = departureTimeString;
								fItem.ArrivalTimeThree = arrivalTimeString;
								// fItem.strFlightStopsThree = stops;
								// fItem.strFlightDurationThree = totalDuration;
								fItem.DepartDateTimeThree = Long
										.parseLong(departureDateTime);
								fItem.ArrivalDateTimeThree = Long
										.parseLong(arrivalDateTime);
								fItem.longFlightDurationInMinsThree = Long
										.parseLong(totalDurationInMinutes);

								break;
							case 3:
								fItem.DepartTimeFour = departureTimeString;
								fItem.ArrivalTimeFour = arrivalTimeString;
								// fItem.strFlightStopsFour = stops;
								// fItem.strFlightDurationFour = totalDuration;
								fItem.DepartDateTimeFour = Long
										.parseLong(departureDateTime);
								fItem.ArrivalDateTimeFour = Long
										.parseLong(arrivalDateTime);
								fItem.longFlightDurationInMinsFour = Long
										.parseLong(totalDurationInMinutes);

								break;
							}
						}
						fItem.intApiId = c.getInt("ApiId");
						fItem.strTripId = c.getString("TripId");
						fItem.doubleFlightPrice = Double.parseDouble(c
								.getString("FinalTotalFare"));
						fItem.strDisplayRate = c.getString("FinalTotalFare");
						fItem.strDeepLink = c.getString("deeplinkURL");
						fItem.blRefundType = c.getBoolean("IsRefundable");
						fItem.jarray = allJourneyArray;
						flightResultItem.add(fItem);

					}
				}

			}
			System.out
					.println("------------------Parsing finished-------------");

		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
			if (blCurr)
				handler.sendEmptyMessage(4);
			else
				handler.sendEmptyMessage(3);
		} catch (NullPointerException e) {
			e.printStackTrace();
			if (blCurr)
				handler.sendEmptyMessage(4);
			else
				handler.sendEmptyMessage(3);
		} catch (Exception e) {
			e.printStackTrace();
			if (blCurr)
				handler.sendEmptyMessage(4);
			else
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

	private void groupResult() {
		int sameCount = 1;
		groupedResult = new ArrayList<FlightResultItem>();
		ArrayList<Double> priceArray = new ArrayList<Double>();
		FlightResultItem fItemTemp = new FlightResultItem();
		Double priceTemp = null;
		for (FlightResultItem fitem : flightResultItem) {

			if (!String.valueOf(priceTemp).equalsIgnoreCase(
					String.valueOf(fitem.doubleFlightPrice))) {
				if (priceArray.contains(fItemTemp.doubleFlightPrice)) {
					fItemTemp.samePriceCount = sameCount;
					sameCount = 1;
					groupedResult.add(fItemTemp);
				}
				fItemTemp = fitem;
				priceTemp = fitem.doubleFlightPrice;
				priceArray.add(fitem.doubleFlightPrice);

			} else {
				sameCount++;
			}
		}
		if (priceArray.contains(fItemTemp.doubleFlightPrice)) {
			fItemTemp.samePriceCount = sameCount;
			sameCount = 1;
			groupedResult.add(fItemTemp);
		}
		System.out.println("groupedResult" + priceArray);
	}

	private void showFilterDialog() {

		final Dialog dialogFilter = new Dialog(this,
				android.R.style.Theme_Translucent);
		dialogFilter.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogFilter.getWindow().setGravity(Gravity.TOP);
		dialogFilter.setContentView(R.layout.dialog_filter_flight);
		dialogFilter.setCancelable(false);

		final RelativeLayout rlBack;
		final LinearLayout llTabPrice, llTabStops, llTabAirline, llTabAirports, llTabTime;
		final ImageView ivTabPrice, ivTabStops, ivTabAirline, ivTabAirports, ivTabTime;
		final TextView tvTabPrice, tvTabStops, tvTabAirline, tvTabAirports, tvTabTime;
		final LinearLayout llPriceLayout, llStopsLayout, llAirlineLayout, llAirportLayout, llTimeLayout;
		final LinearLayout llPriceBar, llLayover;
		final TextView tvPriceMin, tvPriceMax, tvLayoverMin, tvLayoverMax;
		final CheckBox cbNonStop, cbOneStop, cbMultiStop;

		final LinearLayout llOneStop, llNonStop, llMultistop, llArlineList, llAirportList;
		final LinearLayout llTabOutbound, llTabReturn;
		final LinearLayout ll12A6AFrom, ll6A12PFrom, ll12P6PFrom, ll6P12AFrom, ll12A6ATo, ll6A12PTo, ll12P6PTo, ll6P12Ato;
		final CheckBox cbTabOutbound, cbTabReturn; // cbCheckAll

		final LinearLayout llReset, llApply;

		rlBack = (RelativeLayout) dialogFilter.findViewById(R.id.rl_back);

		llTabPrice = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_tab_price);
		llTabStops = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_tab_stops);
		llTabAirline = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_tab_airline);
		llTabAirports = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_tab_airports);
		llTabTime = (LinearLayout) dialogFilter.findViewById(R.id.ll_tab_time);

		ivTabPrice = (ImageView) dialogFilter.findViewById(R.id.iv_tab_price);
		ivTabStops = (ImageView) dialogFilter.findViewById(R.id.iv_tab_stops);
		ivTabAirline = (ImageView) dialogFilter
				.findViewById(R.id.iv_tab_airline);
		ivTabAirports = (ImageView) dialogFilter
				.findViewById(R.id.iv_tab_airports);
		ivTabTime = (ImageView) dialogFilter.findViewById(R.id.iv_tab_time);

		tvTabPrice = (TextView) dialogFilter.findViewById(R.id.tv_tab_price);
		tvTabStops = (TextView) dialogFilter.findViewById(R.id.tv_tab_stops);
		tvTabAirline = (TextView) dialogFilter
				.findViewById(R.id.tv_tab_airline);
		tvTabAirports = (TextView) dialogFilter
				.findViewById(R.id.tv_tab_airports);
		tvTabTime = (TextView) dialogFilter.findViewById(R.id.tv_tab_time);

		llPriceLayout = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_price_layout);
		llStopsLayout = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_stops_layout);
		llAirlineLayout = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_airline_layout);
		llAirportLayout = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_layover_layout);
		llTimeLayout = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_timing_filter);

		llTabOutbound = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_tab_outbound);
		llTabReturn = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_tab_return);

		ll12A6AFrom = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_12a6a_from);
		ll6A12PFrom = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_6a12p_from);
		ll12P6PFrom = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_12p6p_from);
		ll6P12AFrom = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_6p12a_from);
		ll12A6ATo = (LinearLayout) dialogFilter.findViewById(R.id.ll_12a6a_to);
		ll6A12PTo = (LinearLayout) dialogFilter.findViewById(R.id.ll_6a12p_to);
		ll12P6PTo = (LinearLayout) dialogFilter.findViewById(R.id.ll_12p6p_to);
		ll6P12Ato = (LinearLayout) dialogFilter.findViewById(R.id.ll_6p12a_to);

		cbTabOutbound = (CheckBox) dialogFilter.findViewById(R.id.cb_outbound);
		cbTabReturn = (CheckBox) dialogFilter.findViewById(R.id.cb_return);

		llPriceBar = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_price_bar);
		tvPriceMin = (TextView) dialogFilter.findViewById(R.id.tv_range_min);
		tvPriceMax = (TextView) dialogFilter.findViewById(R.id.tv_range_max);

		llLayover = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_layover_bar);
		tvLayoverMin = (TextView) dialogFilter
				.findViewById(R.id.tv_layover_min);
		tvLayoverMax = (TextView) dialogFilter
				.findViewById(R.id.tv_layover_max);

		cbNonStop = (CheckBox) dialogFilter
				.findViewById(R.id.cb_check_non_stop);
		cbOneStop = (CheckBox) dialogFilter
				.findViewById(R.id.cb_check_one_stop);
		cbMultiStop = (CheckBox) dialogFilter
				.findViewById(R.id.cb_check_multi_stop);

		llOneStop = (LinearLayout) dialogFilter.findViewById(R.id.ll_one_stop);
		llNonStop = (LinearLayout) dialogFilter.findViewById(R.id.ll_non_stop);
		llMultistop = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_multi_stop);

		// cbCheckAll = (CheckBox) dialogFilter.findViewById(R.id.cb_check_all);
		llArlineList = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_airline_list);
		llAirportList = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_layover_airport_list);

		llReset = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_reset_filter);

		llApply = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_apply_filter);

		blReturn = cbTabReturn.isChecked();
		blOutbound = cbTabOutbound.isChecked();

		final boolean bl12a6aFromTemp = bl12a6aFrom;
		final boolean bl6a12pFromTemp = bl6a12pFrom;
		final boolean bl12p6pFromTemp = bl12p6pFrom;
		final boolean bl6p12aFromTemp = bl6p12aFrom;

		final boolean bl12a6aToTemp = bl12a6aTo;
		final boolean bl6a12pToTemp = bl6a12pTo;
		final boolean bl12p6pToTemp = bl12p6pTo;
		final boolean bl6p12aToTemp = bl6p12aTo;

		final boolean blNonStopTmp = blNonStop, blOneStopTmp = blOneStop, blMultiStopTmp = blMultiStop;

		final boolean blPriceFilterTemp = blPriceFilter, blLayoverFilterTemp = blLayoverFilter, blNameFilterTemp = blNameFilter, blLayAirportFilterTemp = blLayAirportFilter;

		final Double minValuePrice = filterMinPrice, maxValuePrice = filterMaxPrice;
		final Long minDep = filterMinDep, maxDep = filterMaxDep, minArr = filterMinArr, maxArr = filterMaxArr;
		final Long minLay = filterMinLay, maxLay = filterMaxLay;

		if (strFlightType.equalsIgnoreCase("Multicity"))
			llTabTime.setVisibility(View.GONE);
		else if (strFlightType.equalsIgnoreCase("OneWay")) {
			((LinearLayout) dialogFilter
					.findViewById(R.id.ll_timing_filter_header))
					.setVisibility(View.GONE);
		}

		if (bl12a6aFrom || bl6a12pFrom || bl12p6pFrom || bl6p12aFrom
				|| bl12a6aTo || bl6a12pTo || bl12p6pTo || bl6p12aTo
				|| blNonStop || blOneStop || blMultiStop || blPriceFilter
				|| blLayoverFilter || blNameFilter || blLayAirportFilter)
			llReset.setBackgroundResource(R.drawable.buttonshape);
		else
			llReset.setBackgroundColor(Color.parseColor("#1CA4FF"));

		((TextView) dialogFilter.findViewById(R.id.tv_filter_from))
				.setText(strFromCode);
		((TextView) dialogFilter.findViewById(R.id.tv_filter_to))
				.setText(strToCode);

		OnClickListener tabClicker = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.ll_tab_price:
					llPriceLayout.setVisibility(View.VISIBLE);
					llStopsLayout.setVisibility(View.GONE);
					llAirlineLayout.setVisibility(View.GONE);
					llAirportLayout.setVisibility(View.GONE);
					llTimeLayout.setVisibility(View.GONE);

					tvTabPrice.setTextColor(Color.parseColor("#E60072bb"));
					tvTabStops.setTextColor(Color.BLACK);
					tvTabAirline.setTextColor(Color.BLACK);
					tvTabAirports.setTextColor(Color.BLACK);
					tvTabTime.setTextColor(Color.BLACK);

					ivTabPrice.setColorFilter(Color.parseColor("#E60072bb"));
					ivTabStops.setColorFilter(Color.BLACK);
					ivTabAirline.setColorFilter(Color.BLACK);
					ivTabAirports.setColorFilter(Color.BLACK);
					ivTabTime.setColorFilter(Color.BLACK);

					break;

				case R.id.ll_tab_stops:
					llPriceLayout.setVisibility(View.GONE);
					llStopsLayout.setVisibility(View.VISIBLE);
					llAirlineLayout.setVisibility(View.GONE);
					llAirportLayout.setVisibility(View.GONE);
					llTimeLayout.setVisibility(View.GONE);

					tvTabPrice.setTextColor(Color.BLACK);
					tvTabStops.setTextColor(Color.parseColor("#E60072bb"));
					tvTabAirline.setTextColor(Color.BLACK);
					tvTabAirports.setTextColor(Color.BLACK);
					tvTabTime.setTextColor(Color.BLACK);

					ivTabPrice.setColorFilter(Color.BLACK);
					ivTabStops.setColorFilter(Color.parseColor("#E60072bb"));
					ivTabAirline.setColorFilter(Color.BLACK);
					ivTabAirports.setColorFilter(Color.BLACK);
					ivTabTime.setColorFilter(Color.BLACK);

					break;

				case R.id.ll_tab_airline:
					llPriceLayout.setVisibility(View.GONE);
					llStopsLayout.setVisibility(View.GONE);
					llAirlineLayout.setVisibility(View.VISIBLE);
					llAirportLayout.setVisibility(View.GONE);
					llTimeLayout.setVisibility(View.GONE);

					tvTabPrice.setTextColor(Color.BLACK);
					tvTabStops.setTextColor(Color.BLACK);
					tvTabAirline.setTextColor(Color.parseColor("#E60072bb"));
					tvTabAirports.setTextColor(Color.BLACK);
					tvTabTime.setTextColor(Color.BLACK);

					ivTabPrice.setColorFilter(Color.BLACK);
					ivTabStops.setColorFilter(Color.BLACK);
					ivTabAirline.setColorFilter(Color.parseColor("#E60072bb"));
					ivTabAirports.setColorFilter(Color.BLACK);
					ivTabTime.setColorFilter(Color.BLACK);

					break;

				case R.id.ll_tab_airports:
					llPriceLayout.setVisibility(View.GONE);
					llStopsLayout.setVisibility(View.GONE);
					llAirlineLayout.setVisibility(View.GONE);
					llAirportLayout.setVisibility(View.VISIBLE);
					llTimeLayout.setVisibility(View.GONE);

					tvTabPrice.setTextColor(Color.BLACK);
					tvTabStops.setTextColor(Color.BLACK);
					tvTabAirline.setTextColor(Color.BLACK);
					tvTabAirports.setTextColor(Color.parseColor("#E60072bb"));
					tvTabTime.setTextColor(Color.BLACK);

					ivTabPrice.setColorFilter(Color.BLACK);
					ivTabStops.setColorFilter(Color.BLACK);
					ivTabAirline.setColorFilter(Color.BLACK);
					ivTabAirports.setColorFilter(Color.parseColor("#E60072bb"));
					ivTabTime.setColorFilter(Color.BLACK);

					break;

				case R.id.ll_tab_time:
					llPriceLayout.setVisibility(View.GONE);
					llStopsLayout.setVisibility(View.GONE);
					llAirlineLayout.setVisibility(View.GONE);
					llAirportLayout.setVisibility(View.GONE);
					llTimeLayout.setVisibility(View.VISIBLE);

					tvTabPrice.setTextColor(Color.BLACK);
					tvTabStops.setTextColor(Color.BLACK);
					tvTabAirline.setTextColor(Color.BLACK);
					tvTabAirports.setTextColor(Color.BLACK);
					tvTabTime.setTextColor(Color.parseColor("#E60072bb"));

					ivTabPrice.setColorFilter(Color.BLACK);
					ivTabStops.setColorFilter(Color.BLACK);
					ivTabAirline.setColorFilter(Color.BLACK);
					ivTabAirports.setColorFilter(Color.BLACK);
					ivTabTime.setColorFilter(Color.parseColor("#E60072bb"));

					ll12A6AFrom.setBackgroundColor(bl12a6aFrom ? Color
							.parseColor("#E0E0E0") : Color.TRANSPARENT);
					ll6A12PFrom.setBackgroundColor(bl6a12pFrom ? Color
							.parseColor("#E0E0E0") : Color.TRANSPARENT);
					ll12P6PFrom.setBackgroundColor(bl12p6pFrom ? Color
							.parseColor("#E0E0E0") : Color.TRANSPARENT);
					ll6P12AFrom.setBackgroundColor(bl6p12aFrom ? Color
							.parseColor("#E0E0E0") : Color.TRANSPARENT);
					ll12A6ATo.setBackgroundColor(bl12a6aTo ? Color
							.parseColor("#E0E0E0") : Color.TRANSPARENT);
					ll6A12PTo.setBackgroundColor(bl6a12pTo ? Color
							.parseColor("#E0E0E0") : Color.TRANSPARENT);
					ll12P6PTo.setBackgroundColor(bl12p6pTo ? Color
							.parseColor("#E0E0E0") : Color.TRANSPARENT);
					ll6P12Ato.setBackgroundColor(bl6p12aTo ? Color
							.parseColor("#E0E0E0") : Color.TRANSPARENT);

					break;
				}
			}
		};

		llTabPrice.setOnClickListener(tabClicker);
		llTabStops.setOnClickListener(tabClicker);
		llTabAirline.setOnClickListener(tabClicker);
		llTabAirports.setOnClickListener(tabClicker);
		llTabTime.setOnClickListener(tabClicker);

		OnClickListener clikcr = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {

				case R.id.ll_tab_outbound:
					if (!cbTabOutbound.isChecked()) {
						cbTabOutbound.setChecked(true);
						cbTabReturn.setChecked(false);

						((TextView) dialogFilter
								.findViewById(R.id.tv_filter_from))
								.setText(strFromCode);
						((TextView) dialogFilter
								.findViewById(R.id.tv_filter_to))
								.setText(strToCode);

					}
					break;

				case R.id.ll_tab_return:
					if (!cbTabReturn.isChecked()) {
						cbTabReturn.setChecked(true);
						cbTabOutbound.setChecked(false);

						((TextView) dialogFilter
								.findViewById(R.id.tv_filter_from))
								.setText(strToCode);
						((TextView) dialogFilter
								.findViewById(R.id.tv_filter_to))
								.setText(strFromCode);
					}
					break;

				case R.id.ll_12a6a_from:
					if (bl12a6aFrom) {
						bl12a6aFrom = false;
					} else {
						bl12a6aFrom = true;
						bl6a12pFrom = false;
						bl12p6pFrom = false;
						bl6p12aFrom = false;
					}
					break;

				case R.id.ll_6a12p_from:
					if (bl6a12pFrom) {
						bl6a12pFrom = false;
					} else {
						bl12a6aFrom = false;
						bl6a12pFrom = true;
						bl12p6pFrom = false;
						bl6p12aFrom = false;
					}
					break;

				case R.id.ll_12p6p_from:
					if (bl12p6pFrom) {
						bl12p6pFrom = false;
					} else {
						bl12a6aFrom = false;
						bl6a12pFrom = false;
						bl12p6pFrom = true;
						bl6p12aFrom = false;
					}
					break;

				case R.id.ll_6p12a_from:
					if (bl6p12aFrom) {
						bl6p12aFrom = false;
					} else {
						bl12a6aFrom = false;
						bl6a12pFrom = false;
						bl12p6pFrom = false;
						bl6p12aFrom = true;
					}
					break;

				case R.id.ll_12a6a_to:
					if (bl12a6aTo) {
						bl12a6aTo = false;
					} else {
						bl12a6aTo = true;
						bl6a12pTo = false;
						bl12p6pTo = false;
						bl6p12aTo = false;
					}
					break;

				case R.id.ll_6a12p_to:
					if (bl6a12pTo) {
						bl6a12pTo = false;
					} else {
						bl12a6aTo = false;
						bl6a12pTo = true;
						bl12p6pTo = false;
						bl6p12aTo = false;
					}
					break;

				case R.id.ll_12p6p_to:
					if (bl12p6pTo) {
						bl12p6pTo = false;
					} else {
						bl12a6aTo = false;
						bl6a12pTo = false;
						bl12p6pTo = true;
						bl6p12aTo = false;
					}
					break;

				case R.id.ll_6p12a_to:
					if (bl6p12aTo) {
						bl6p12aTo = false;
					} else {
						bl12a6aTo = false;
						bl6a12pTo = false;
						bl12p6pTo = false;
						bl6p12aTo = true;
					}
					break;

				default:
					break;
				}

				ll12A6AFrom.setBackgroundColor(bl12a6aFrom ? Color
						.parseColor("#E0E0E0") : Color.TRANSPARENT);
				ll6A12PFrom.setBackgroundColor(bl6a12pFrom ? Color
						.parseColor("#E0E0E0") : Color.TRANSPARENT);
				ll12P6PFrom.setBackgroundColor(bl12p6pFrom ? Color
						.parseColor("#E0E0E0") : Color.TRANSPARENT);
				ll6P12AFrom.setBackgroundColor(bl6p12aFrom ? Color
						.parseColor("#E0E0E0") : Color.TRANSPARENT);
				ll12A6ATo.setBackgroundColor(bl12a6aTo ? Color
						.parseColor("#E0E0E0") : Color.TRANSPARENT);
				ll6A12PTo.setBackgroundColor(bl6a12pTo ? Color
						.parseColor("#E0E0E0") : Color.TRANSPARENT);
				ll12P6PTo.setBackgroundColor(bl12p6pTo ? Color
						.parseColor("#E0E0E0") : Color.TRANSPARENT);
				ll6P12Ato.setBackgroundColor(bl6p12aTo ? Color
						.parseColor("#E0E0E0") : Color.TRANSPARENT);

				if (bl12a6aFrom || bl6a12pFrom || bl12p6pFrom || bl6p12aFrom
						|| bl12a6aTo || bl6a12pTo || bl12p6pTo || bl6p12aTo
						|| blNonStop || blOneStop || blMultiStop
						|| blPriceFilter || blLayoverFilter || blNameFilter
						|| blLayAirportFilter)
					llReset.setBackgroundResource(R.drawable.buttonshape);
				else
					llReset.setBackgroundColor(Color.parseColor("#1CA4FF"));
			}
		};

		llTabOutbound.setOnClickListener(clikcr);
		llTabReturn.setOnClickListener(clikcr);
		ll12A6AFrom.setOnClickListener(clikcr);
		ll6A12PFrom.setOnClickListener(clikcr);
		ll12P6PFrom.setOnClickListener(clikcr);
		ll6P12AFrom.setOnClickListener(clikcr);
		ll12A6ATo.setOnClickListener(clikcr);
		ll6A12PTo.setOnClickListener(clikcr);
		ll12P6PTo.setOnClickListener(clikcr);
		ll6P12Ato.setOnClickListener(clikcr);

		if (!blHasNonStop)
			llNonStop.setVisibility(View.GONE);
		if (!blHasOneStop)
			llOneStop.setVisibility(View.GONE);
		if (!blHasMultStop)
			llMultistop.setVisibility(View.GONE);

		cbNonStop.setChecked(blNonStop);
		cbOneStop.setChecked(blOneStop);
		cbMultiStop.setChecked(blMultiStop);

		final RangeSeekBar<Double> priceBar = new RangeSeekBar<Double>(this);
		// Set the range
		priceBar.setRangeValues(
				(flightResultItem).get(0).doubleFlightPrice,
				(flightResultItem).get(flightResultItem.size() - 1).doubleFlightPrice);
		priceBar.setSelectedMinValue(filterMinPrice);
		priceBar.setSelectedMaxValue(filterMaxPrice);
		String price = String.format(new Locale("en"), "%.3f", filterMinPrice);
		tvPriceMin.setText(CommonFunctions.strCurrency + " " + price);
		price = String.format(new Locale("en"), "%.3f", filterMaxPrice);
		tvPriceMax.setText(CommonFunctions.strCurrency + " " + price);

		final RangeSeekBar<Long> layoverBar = new RangeSeekBar<Long>(this);
		layoverBar.setRangeValues(filterLayLow, filterLayHigh);
		layoverBar.setSelectedMinValue(filterMinLay);
		layoverBar.setSelectedMaxValue(filterMaxLay);
		String minLayover = null, maxLayover = null;
		if (CommonFunctions.lang.equalsIgnoreCase("en")) {
			minLayover = filterMinLay / 60 % 24 + " Hrs :" + filterMinLay % 60
					+ " Mins";
			maxLayover = filterMaxLay / 60 % 24 + " Hrs :" + filterMaxLay % 60
					+ " Mins";
		} else {
			minLayover = filterMinLay / 60 % 24 + " ساعة :" + filterMinLay % 60
					+ " دقيقة";
			maxLayover = filterMaxLay / 60 % 24 + " ساعة :" + filterMaxLay % 60
					+ " دقيقة";
		}

		tvLayoverMin.setText(String.valueOf(minLayover));
		tvLayoverMax.setText(String.valueOf(maxLayover));

		priceBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Double>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
					Double minValue, Double maxValue) {
				// handle changed range values
				String price = String
						.format(new Locale("en"), "%.3f", minValue);
				tvPriceMin.setText(CommonFunctions.strCurrency + " " + price);
				price = String.format(new Locale("en"), "%.3f", maxValue);
				tvPriceMax.setText(CommonFunctions.strCurrency + " " + price);
				filterMinPrice = minValue;
				filterMaxPrice = maxValue;
				if (filterMinPrice.equals((flightResultItem).get(0).doubleFlightPrice)
						&& filterMaxPrice.equals((flightResultItem)
								.get(flightResultItem.size() - 1).doubleFlightPrice))
					blPriceFilter = false;
				else
					blPriceFilter = true;

				if (bl12a6aFrom || bl6a12pFrom || bl12p6pFrom || bl6p12aFrom
						|| bl12a6aTo || bl6a12pTo || bl12p6pTo || bl6p12aTo
						|| blNonStop || blOneStop || blMultiStop
						|| blPriceFilter || blLayoverFilter || blNameFilter
						|| blLayAirportFilter)
					llReset.setBackgroundResource(R.drawable.buttonshape);
				else
					llReset.setBackgroundColor(Color.parseColor("#1CA4FF"));

			}
		});

		layoverBar
				.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Long>() {
					@Override
					public void onRangeSeekBarValuesChanged(
							RangeSeekBar<?> bar, Long minValue, Long maxValue) {
						// handle changed range values
						String minlay = null, maxLay = null;
						if (CommonFunctions.lang.equalsIgnoreCase("en")) {
							minlay = minValue / 60 % 24 + " Hrs :" + minValue
									% 60 + " Mins";
							maxLay = maxValue / 60 % 24 + " Hrs :" + maxValue
									% 60 + " Mins";
						} else {
							minlay = minValue / 60 % 24 + " ساعة :" + minValue
									% 60 + " دقيقة";
							maxLay = maxValue / 60 % 24 + " ساعة :" + maxValue
									% 60 + " دقيقة";
						}

						tvLayoverMin.setText(String.valueOf(minlay));
						tvLayoverMax.setText(String.valueOf(maxLay));

						filterMinLay = minValue;
						filterMaxLay = maxValue;

						if (filterMinLay.equals(filterLayLow)
								&& filterMaxLay.equals(filterLayHigh))
							blLayoverFilter = false;
						else
							blLayoverFilter = true;

						if (bl12a6aFrom || bl6a12pFrom || bl12p6pFrom
								|| bl6p12aFrom || bl12a6aTo || bl6a12pTo
								|| bl12p6pTo || bl6p12aTo || blNonStop
								|| blOneStop || blMultiStop || blPriceFilter
								|| blLayoverFilter || blNameFilter
								|| blLayAirportFilter)
							llReset.setBackgroundResource(R.drawable.buttonshape);
						else
							llReset.setBackgroundColor(Color
									.parseColor("#1CA4FF"));
					}
				});

		priceBar.setNotifyWhileDragging(true);
		layoverBar.setNotifyWhileDragging(true);

		llPriceBar.addView(priceBar);
		llLayover.addView(layoverBar);

		if (arrayAirports.size() == 0) {
			llTabAirports.setVisibility(View.GONE);
			((LinearLayout) dialogFilter.findViewById(R.id.ll_layover))
					.setVisibility(View.GONE);
			((LinearLayout) dialogFilter.findViewById(R.id.ll_layover_airports))
					.setVisibility(View.GONE);
		}

		if (filterMinPrice == filterMaxPrice)
			priceBar.setEnabled(false);

		if (filterLayLow == filterLayHigh) {
			layoverBar.setEnabled(false);
		}

		OnClickListener stopListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.ll_one_stop:
					blOneStop = blOneStop ? false : true;
					cbOneStop.setChecked(blOneStop);
					break;
				case R.id.ll_non_stop:
					blNonStop = blNonStop ? false : true;
					cbNonStop.setChecked(blNonStop);
					break;
				case R.id.ll_multi_stop:
					blMultiStop = blMultiStop ? false : true;
					cbMultiStop.setChecked(blMultiStop);
					break;
				}
				if (bl12a6aFrom || bl6a12pFrom || bl12p6pFrom || bl6p12aFrom
						|| bl12a6aTo || bl6a12pTo || bl12p6pTo || bl6p12aTo
						|| blNonStop || blOneStop || blMultiStop
						|| blPriceFilter || blLayoverFilter || blNameFilter
						|| blLayAirportFilter)
					llReset.setBackgroundResource(R.drawable.buttonshape);
				else
					llReset.setBackgroundColor(Color.parseColor("#1CA4FF"));
			}
		};

		llNonStop.setOnClickListener(stopListener);
		llOneStop.setOnClickListener(stopListener);
		llMultistop.setOnClickListener(stopListener);

		llArlineList.removeAllViews();

		if (arrayAirline.size() > 0) {
			for (int i = 0; i < arrayAirline.size(); ++i) {
				final View view = getLayoutInflater().inflate(
						R.layout.custom_check_box_list_item, null);
				((CheckBox) view.findViewById(R.id.cb_check_airline))
						.setClickable(false);
				((TextView) view.findViewById(R.id.tv_airline_name))
						.setText(arrayAirline.get(i).toString());
				if (checkedAirlines.contains(arrayAirline.get(i))) {
					((CheckBox) view.findViewById(R.id.cb_check_airline))
							.setChecked(true);
					// cbCheckAll.setChecked(true);
				}
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (((CheckBox) view
								.findViewById(R.id.cb_check_airline))
								.isChecked())
							((CheckBox) view
									.findViewById(R.id.cb_check_airline))
									.setChecked(false);
						else
							((CheckBox) view
									.findViewById(R.id.cb_check_airline))
									.setChecked(true);

						View view;
						for (int j = 0; j < arrayAirline.size(); ++j) {
							view = (View) llArlineList.findViewById(j);
							if (((CheckBox) view
									.findViewById(R.id.cb_check_airline))
									.isChecked()) {
								blNameFilter = true;
								llReset.setBackgroundResource(R.drawable.buttonshape);
								break;
							} else
								blNameFilter = false;
							if (bl12a6aFrom || bl6a12pFrom || bl12p6pFrom
									|| bl6p12aFrom || bl12a6aTo || bl6a12pTo
									|| bl12p6pTo || bl6p12aTo || blNonStop
									|| blOneStop || blMultiStop
									|| blPriceFilter || blLayoverFilter
									|| blNameFilter || blLayAirportFilter)
								llReset.setBackgroundResource(R.drawable.buttonshape);
							else
								llReset.setBackgroundColor(Color
										.parseColor("#1CA4FF"));
						}
					}
				});
				view.setId(i);
				llArlineList.addView(view);
			}
		}

		if (arrayAirports.size() > 0) {
			for (int i = 0; i < arrayAirports.size(); ++i) {
				final View view = getLayoutInflater().inflate(
						R.layout.custom_check_box_list_item, null);
				((CheckBox) view.findViewById(R.id.cb_check_airline))
						.setClickable(false);
				((TextView) view.findViewById(R.id.tv_airline_name))
						.setText(arrayAirports.get(i).toString());
				if (checkedAirports.contains(arrayAirports.get(i))) {
					((CheckBox) view.findViewById(R.id.cb_check_airline))
							.setChecked(true);
				}
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (((CheckBox) view
								.findViewById(R.id.cb_check_airline))
								.isChecked())
							((CheckBox) view
									.findViewById(R.id.cb_check_airline))
									.setChecked(false);
						else
							((CheckBox) view
									.findViewById(R.id.cb_check_airline))
									.setChecked(true);

						View view;
						for (int j = 0; j < arrayAirports.size(); ++j) {
							view = (View) llAirportList.findViewById(j);
							if (((CheckBox) view
									.findViewById(R.id.cb_check_airline))
									.isChecked()) {
								blLayAirportFilter = true;
								llReset.setBackgroundResource(R.drawable.buttonshape);
								break;
							} else
								blLayAirportFilter = false;
							if (bl12a6aFrom || bl6a12pFrom || bl12p6pFrom
									|| bl6p12aFrom || bl12a6aTo || bl6a12pTo
									|| bl12p6pTo || bl6p12aTo || blNonStop
									|| blOneStop || blMultiStop
									|| blPriceFilter || blLayoverFilter
									|| blNameFilter || blLayAirportFilter)
								llReset.setBackgroundResource(R.drawable.buttonshape);
							else
								llReset.setBackgroundColor(Color
										.parseColor("#1CA4FF"));
						}

					}
				});
				view.setId(i);
				llAirportList.addView(view);
			}
		}

		rlBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				blOneStop = blOneStopTmp;
				blNonStop = blNonStopTmp;
				blMultiStop = blMultiStopTmp;

				filterMinPrice = minValuePrice;
				filterMaxPrice = maxValuePrice;
				filterMinDep = minDep;
				filterMaxDep = maxDep;
				filterMinArr = minArr;
				filterMaxArr = maxArr;
				filterMinLay = minLay;
				filterMaxLay = maxLay;

				bl12a6aFrom = bl12a6aFromTemp;
				bl6a12pFrom = bl6a12pFromTemp;
				bl12p6pFrom = bl12p6pFromTemp;
				bl6p12aFrom = bl6p12aFromTemp;

				bl12a6aTo = bl12a6aToTemp;
				bl6a12pTo = bl6a12pToTemp;
				bl12p6pTo = bl12p6pToTemp;
				bl6p12aTo = bl6p12aToTemp;

				blPriceFilter = blPriceFilterTemp;
				blLayoverFilter = blLayoverFilterTemp;

				blNameFilter = blNameFilterTemp;
				blLayAirportFilter = blLayAirportFilterTemp;

				dialogFilter.dismiss();
			}
		});

		llApply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				blOutbound = cbTabOutbound.isChecked();
				blReturn = cbTabReturn.isChecked();

				checkedAirlines.clear();
				int i = 0;
				for (i = 0; i < arrayAirline.size(); ++i) {
					final View view = (View) llArlineList.findViewById(i);
					if (((CheckBox) view.findViewById(R.id.cb_check_airline))
							.isChecked())
						checkedAirlines.add(arrayAirline.get(i));
				}

				checkedAirports.clear();
				for (i = 0; i < arrayAirports.size(); ++i) {
					final View view = (View) llAirportList.findViewById(i);
					if (((CheckBox) view.findViewById(R.id.cb_check_airline))
							.isChecked())
						checkedAirports.add(arrayAirports.get(i));
				}

				blNameFilter = checkedAirlines.size() > 0 ? true : false;

				blLayAirportFilter = checkedAirports.size() > 0 ? true : false;

				new filter().execute();

				dialogFilter.dismiss();
			}
		});

		llReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				flightResultItemsTemp.clear();
				flightResultItemsTemp.addAll(groupedResult);

				filterMinPrice = (groupedResult).get(0).doubleFlightPrice;
				filterMaxPrice = (groupedResult).get(groupedResult.size() - 1).doubleFlightPrice;

				filterMinDep = filterDepLow;
				filterMaxDep = filterDepHigh;
				filterMinArr = filterArrLow;
				filterMaxArr = filterArrHigh;
				filterMinLay = filterLayLow;
				filterMaxLay = filterLayHigh;
				blOutbound = true;
				blReturn = false;
				bl12a6aFrom = false;
				bl6a12pFrom = false;
				bl12p6pFrom = false;
				bl6p12aFrom = false;
				bl12a6aTo = false;
				bl6a12pTo = false;
				bl12p6pTo = false;
				bl6p12aTo = false;
				blOneStop = false;
				blNonStop = false;
				blMultiStop = false;
				blPriceFilter = false;
				blDepTimeFilter = false;
				blArrTimeFilter = false;
				blLayoverFilter = false;
				blStopFilter = false;
				blNameFilter = false;
				blLayAirportFilter = false;
				checkedAirlines.clear();
				checkedAirports.clear();
				sortArrayList();

				dialogFilter.dismiss();

				showFilterDialog();
			}
		});

		dialogFilter.show();
		llTabPrice.performClick();
	}

	private void sortArrayList() {
		ArrayList<FlightResultItem> temp = new ArrayList<FlightResultItem>();
		if (blPriceFilter || blDepTimeFilter || blArrTimeFilter || blNonStop
				|| blOneStop || blMultiStop || blNameFilter || blLayoverFilter
				|| bl12a6aFrom || bl6a12pFrom || bl12p6pFrom || bl6p12aFrom
				|| bl12a6aTo || bl6a12pTo || bl12p6pTo || bl6p12aTo
				|| blLayAirportFilter) {
			temp = filteredResult;
		} else {
			temp = flightResultItemsTemp;
		}

		if (blSortPrice) {
			if (temp.size() > 0) {
				Collections.sort(temp, new Comparator<FlightResultItem>() {

					@Override
					public int compare(FlightResultItem lhs,
							FlightResultItem rhs) {
						// TODO Auto-generated method stub
						return (lhs.doubleFlightPrice)
								.compareTo(rhs.doubleFlightPrice);
					}
				});
				if (strSortPriceType.equalsIgnoreCase("high")) {
					Collections.reverse(temp);
				}
			}
		} else if (blSortDep) {
			if (temp.size() > 0) {
				Collections.sort(temp, new Comparator<FlightResultItem>() {

					@Override
					public int compare(FlightResultItem lhs,
							FlightResultItem rhs) {
						// TODO Auto-generated method stub
						return (lhs.DepartDateTimeOne)
								.compareTo(rhs.DepartDateTimeOne);
					}
				});
				if (strSortDepType.equalsIgnoreCase("high")) {
					Collections.reverse(temp);
				}
			}
		} else if (blSortArrival) {
			if (temp.size() > 0) {
				Collections.sort(temp, new Comparator<FlightResultItem>() {

					@Override
					public int compare(FlightResultItem lhs,
							FlightResultItem rhs) {
						// TODO Auto-generated method stub
						return (lhs.ArrivalDateTimeOne)
								.compareTo(rhs.ArrivalDateTimeOne);
					}
				});
				if (strSortArrivalType.equalsIgnoreCase("high")) {
					Collections.reverse(temp);
				}
			}
		} else if (blSortDuration) {
			if (temp.size() > 0) {
				Collections.sort(temp, new Comparator<FlightResultItem>() {

					@Override
					public int compare(FlightResultItem lhs,
							FlightResultItem rhs) {
						// TODO Auto-generated method stub
						return (lhs.longFlightDurationInMinsOne)
								.compareTo(rhs.longFlightDurationInMinsOne);
					}
				});
				if (strSortDurationType.equalsIgnoreCase("high")) {
					Collections.reverse(temp);
				}
			}

		} else {
			if (temp.size() > 0) {
				Collections.sort(temp, new Comparator<FlightResultItem>() {

					@Override
					public int compare(FlightResultItem lhs,
							FlightResultItem rhs) {
						// TODO Auto-generated method stub
						return lhs.str_AirlineName
								.compareToIgnoreCase(rhs.str_AirlineName);
					}
				});
				if (strSortAirNameType.equalsIgnoreCase("high")) {
					Collections.reverse(temp);
				}
			}

		}

		lvFlightResult.setAdapter(new FlightResultAdapter(
				FlightResultActivity.this, temp, isRoundTrip));
		// lvFlightResult.setOnItemClickListener(this);
		// if(blPriceFilter || blDepTimeFilter || blArrTimeFilter || blNonStop
		// || blOneStop || blMultiStop )
		// {
		// filteredResult = temp;
		// }
		// else
		// {
		// flightResultItemsTemp = temp;
		// }
	}

	private class filter extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			filteredResult = new ArrayList<FlightResultItem>();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub

			if (blNonStop || blOneStop || blMultiStop) {
				for (FlightResultItem fitem : flightResultItemsTemp) {
					if (fitem.intFlightStopsOne == 0 && blNonStop)
						filteredResult.add(fitem);
					else if (fitem.intFlightStopsOne == 1 && blOneStop)
						filteredResult.add(fitem);
					else if (fitem.intFlightStopsOne > 1 && blMultiStop)
						filteredResult.add(fitem);
				}
			} else
				filteredResult.addAll(flightResultItemsTemp);

			if (!strFlightType.equalsIgnoreCase("Multicity")) {
				if (bl12a6aFrom || bl6a12pFrom || bl12p6pFrom || bl6p12aFrom
						|| bl12a6aTo || bl6a12pTo || bl12p6pTo || bl6p12aTo) {
					ArrayList<FlightResultItem> temp = new ArrayList<FlightResultItem>();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"hh:mm aa", new Locale(CommonFunctions.lang));
					Calendar cal = Calendar.getInstance();
					int minutes = -1;
					if (blOutbound) {
						if (bl12a6aFrom || bl6a12pFrom || bl12p6pFrom
								|| bl6p12aFrom) {
							for (FlightResultItem fitem : filteredResult) {

								try {
									cal.setTime(dateFormat
											.parse(fitem.DepartTimeOne));
									minutes = cal.get(Calendar.HOUR_OF_DAY)
											* 60 + cal.get(Calendar.MINUTE);

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								if (bl12a6aFrom && minutes > 0 && minutes < 360)
									temp.add(fitem);
								else if (bl6a12pFrom && minutes > 360
										&& minutes < 720)
									temp.add(fitem);
								else if (bl12p6pFrom && minutes > 720
										&& minutes < 1080)
									temp.add(fitem);
								else if (bl6p12aFrom && minutes > 1080
										&& minutes < 1440)
									temp.add(fitem);
							}
							filteredResult.clear();
							filteredResult.addAll(temp);
						}

						if (bl12a6aTo || bl6a12pTo || bl12p6pTo || bl6p12aTo) {
							temp.clear();
							for (FlightResultItem fitem : filteredResult) {
								try {
									cal.setTime(dateFormat
											.parse(fitem.ArrivalTimeOne));
									minutes = cal.get(Calendar.HOUR_OF_DAY)
											* 60 + cal.get(Calendar.MINUTE);

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								if (bl12a6aTo && minutes > 0 && minutes < 360)
									temp.add(fitem);
								else if (bl6a12pTo && minutes > 360
										&& minutes < 720)
									temp.add(fitem);
								else if (bl12p6pTo && minutes > 720
										&& minutes < 1080)
									temp.add(fitem);
								else if (bl6p12aTo && minutes > 1080
										&& minutes < 1440)
									temp.add(fitem);
							}
							filteredResult.clear();
							filteredResult.addAll(temp);
						}
					} else if (blReturn) {
						if (bl12a6aFrom || bl6a12pFrom || bl12p6pFrom
								|| bl6p12aFrom) {
							for (FlightResultItem fitem : filteredResult) {

								try {
									cal.setTime(dateFormat
											.parse(fitem.DepartTimeTwo));
									minutes = cal.get(Calendar.HOUR_OF_DAY)
											* 60 + cal.get(Calendar.MINUTE);

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								if (bl12a6aFrom && minutes > 0 && minutes < 360)
									temp.add(fitem);
								else if (bl6a12pFrom && minutes > 360
										&& minutes < 720)
									temp.add(fitem);
								else if (bl12p6pFrom && minutes > 720
										&& minutes < 1080)
									temp.add(fitem);
								else if (bl6p12aFrom && minutes > 1080
										&& minutes < 1440)
									temp.add(fitem);
							}
							filteredResult.clear();
							filteredResult.addAll(temp);
						}
						if (bl12a6aTo || bl6a12pTo || bl12p6pTo || bl6p12aTo) {
							temp.clear();
							for (FlightResultItem fitem : filteredResult) {
								try {
									cal.setTime(dateFormat
											.parse(fitem.ArrivalTimeTwo));
									minutes = cal.get(Calendar.HOUR_OF_DAY)
											* 60 + cal.get(Calendar.MINUTE);

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								if (bl12a6aTo && minutes > 0 && minutes < 360)
									temp.add(fitem);
								else if (bl6a12pTo && minutes > 360
										&& minutes < 720)
									temp.add(fitem);
								else if (bl12p6pTo && minutes > 720
										&& minutes < 1080)
									temp.add(fitem);
								else if (bl6p12aTo && minutes > 1080
										&& minutes < 1440)
									temp.add(fitem);
							}
							filteredResult.clear();
							filteredResult.addAll(temp);
						}
					}

				}
			}

			if (blPriceFilter) {
				ArrayList<FlightResultItem> temp = new ArrayList<FlightResultItem>();
				for (FlightResultItem fitem : filteredResult) {
					if (fitem.doubleFlightPrice >= filterMinPrice
							&& fitem.doubleFlightPrice <= filterMaxPrice) {
						temp.add(fitem);
					}
				}
				filteredResult.clear();
				filteredResult.addAll(temp);
			}

			if (blDepTimeFilter) {
				ArrayList<FlightResultItem> temp = new ArrayList<FlightResultItem>();
				SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa",
						new Locale(CommonFunctions.lang));
				Calendar cal = Calendar.getInstance();
				for (FlightResultItem fitem : filteredResult) {

					try {
						cal.setTime(dateFormat.parse(fitem.DepartTimeOne));
						if (cal.getTimeInMillis() >= filterMinDep
								&& cal.getTimeInMillis() <= filterMaxDep) {
							temp.add(fitem);
						}
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
				filteredResult.clear();
				filteredResult.addAll(temp);
			}

			if (blArrTimeFilter) {
				ArrayList<FlightResultItem> temp = new ArrayList<FlightResultItem>();
				SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa",
						new Locale(CommonFunctions.lang));
				Calendar cal = Calendar.getInstance();
				for (FlightResultItem fitem : filteredResult) {
					try {
						cal.setTime(dateFormat.parse(fitem.DepartTimeTwo));
						if (cal.getTimeInMillis() >= filterMinArr
								&& cal.getTimeInMillis() <= filterMaxArr) {
							temp.add(fitem);
						}
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
				filteredResult.clear();
				filteredResult.addAll(temp);
			}

			if (blLayoverFilter) {
				ArrayList<FlightResultItem> temp = new ArrayList<FlightResultItem>();
				for (FlightResultItem fitem : filteredResult) {
					if (fitem.longLayoverTimeInMins >= filterMinLay
							&& fitem.longLayoverTimeInMins <= filterMaxLay) {
						temp.add(fitem);
					}
				}
				filteredResult.clear();
				filteredResult.addAll(temp);
			}

			if (blNameFilter) {
				ArrayList<FlightResultItem> temp = new ArrayList<FlightResultItem>();
				for (FlightResultItem fitem : filteredResult) {
					if (checkedAirlines.contains(fitem.str_AirlineName)) {
						temp.add(fitem);
					}
				}
				filteredResult.clear();
				filteredResult.addAll(temp);
			}

			if (blLayAirportFilter) {
				ArrayList<FlightResultItem> temp = new ArrayList<FlightResultItem>();
				for (FlightResultItem fitem : filteredResult) {
					if (checkedAirports.contains(fitem.strLayoverAirport)) {
						temp.add(fitem);
					}
				}
				filteredResult.clear();
				filteredResult.addAll(temp);
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (filteredResult.size() > 0)
				sortArrayList();
			else {
				lvFlightResult.setAdapter(null);
				noResultAlert(true, getResources()
						.getString(R.string.no_result));
			}
			super.onPostExecute(result);
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		groupedResult.clear();
		flightResultItem.clear();
		flightResultItemsTemp.clear();
		arrayAirline.clear();
		checkedAirlines.clear();
	}

	public static FlightResultItem selectedFItem;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		selectedFItem = (FlightResultItem) lvFlightResult
				.getItemAtPosition(position);

		new backService().execute(selectedFItem);

	}

	public class backService extends AsyncTask<FlightResultItem, Void, String> {

		FlightResultItem fItem;
		String sessionResult = "";

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// loaderDialog.show();
			// tvProgressText.setText(getResources().getString(
			// R.string.checking_flight));
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(FlightResultItem... params) {
			// TODO Auto-generated method stub
			URL url = null;
			HttpURLConnection urlConnection = null;
			try {
				url = new URL(CommonFunctions.main_url
						+ "en/FlightApi/AvailResult?tripId="
						+ params[0].strTripId + "&searchID=" + strSessionId);
				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.setAcceptCookie(true);
				String cookie = cookieManager.getCookie(url.toString());
				Log.i("url", url.toString());
				urlConnection = (HttpURLConnection) url.openConnection();
				// urlConnection.setReadTimeout(15000);
				urlConnection.setRequestProperty("Cookie", cookie);
				urlConnection.setConnectTimeout(15000);
				urlConnection.setRequestMethod("GET");
				InputStream in;
				in = new BufferedInputStream(urlConnection.getInputStream());
				sessionResult = convertStreamToString(in);
				System.out.println("result" + sessionResult);
				urlConnection.disconnect();
				JSONObject json = new JSONObject(sessionResult);
				sessionResult = json.getString("Status");
				if (sessionResult.equalsIgnoreCase("true"))
					fItem = params[0];

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				urlConnection.disconnect();
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				urlConnection.disconnect();
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				// urlConnection.disconnect();
				e.printStackTrace();
			}
			return params[0].strDeepLink;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			// if (loaderDialog.isShowing())
			// loaderDialog.dismiss();
			if (sessionResult.equalsIgnoreCase("true")) {

				Intent details = new Intent(FlightResultActivity.this,
						FlightPaxActivity.class);
				details.putExtra("sID", strSessionId+CommonFunctions.getRandomString(4));
				details.putExtra("isRound", isRoundTrip);
				// details.putExtra("url", result);
				// details.putExtra("type", "flight");
				 details.putExtra("tripID", selectedFItem.strTripId);
				startActivity(details);
			} else {
				tvProgressText.setText(getResources().getString(
						R.string.flight_expired));
				new backMethod().execute("");
			}
			super.onPostExecute(result);
		}

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if (loaderDialog.isShowing())
					loaderDialog.dismiss();
				noResultAlert(false,
						"There is a problem on your Network. Please try again later.");

			} else if (msg.what == 2) {

				if (loaderDialog.isShowing())
					loaderDialog.dismiss();
				noResultAlert(false,
						"There is a problem on your application. Please report it.");

			} else if (msg.what == 3) {
				if (loaderDialog.isShowing())
					loaderDialog.dismiss();
				noResultAlert(false,
						getResources().getString(R.string.no_result));
			} else if (msg.what == 4) {
				if (loaderDialog.isShowing())
					loaderDialog.dismiss();
				noResultAlert(false,
						"Something went wrong. Please try again later");
			}

		}
	};

	public void noResultAlert(final boolean filter, String msg) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		alertDialog.setMessage(msg);

		if (filter)
			alertDialog.setPositiveButton(
					getResources().getString(R.string.reset_filter),
					new AlertDialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							flightResultItemsTemp.clear();
							flightResultItemsTemp.addAll(groupedResult);

							filterMinPrice = (groupedResult).get(0).doubleFlightPrice;
							filterMaxPrice = (groupedResult).get(groupedResult
									.size() - 1).doubleFlightPrice;

							filterMinDep = filterDepLow;
							filterMaxDep = filterDepHigh;
							filterMinArr = filterArrLow;
							filterMaxArr = filterArrHigh;
							filterMinLay = filterLayLow;
							filterMaxLay = filterLayHigh;
							blOutbound = true;
							blReturn = false;
							bl12a6aFrom = false;
							bl6a12pFrom = false;
							bl12p6pFrom = false;
							bl6p12aFrom = false;
							bl12a6aTo = false;
							bl6a12pTo = false;
							bl12p6pTo = false;
							bl6p12aTo = false;
							blOneStop = false;
							blNonStop = false;
							blMultiStop = false;
							blPriceFilter = false;
							blDepTimeFilter = false;
							blArrTimeFilter = false;
							blLayoverFilter = false;
							blStopFilter = false;
							blNameFilter = false;
							blLayAirportFilter = false;
							checkedAirlines.clear();
							checkedAirports.clear();
							sortArrayList();
						}
					});

		else {
			alertDialog.setPositiveButton(
					getResources().getString(R.string.ok),
					new AlertDialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							if (!blCurr)
								finish();
						}
					});
		}
		alertDialog.setCancelable(false);
		alertDialog.show();
	}

	private void dialogCurrency() {
		curr = new Dialog(FlightResultActivity.this,
				android.R.style.Theme_Translucent);
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

				blCurr = true;
				new backMethod().execute("KWD");

				// tvCurrency.setText(CommonFunctions.strCurrency);
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

				blCurr = true;
				new backMethod().execute("INR");
				// CommonFunctions.strCurrency = "INR";
				// tvCurrency.setText(CommonFunctions.strCurrency);
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

				blCurr = true;
				new backMethod().execute("USD");
				// CommonFunctions.strCurrency = "USD";
				// tvCurrency.setText(CommonFunctions.strCurrency);
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

				blCurr = true;
				new backMethod().execute("QAR");
				// CommonFunctions.strCurrency = "QAR";
				// tvCurrency.setText(CommonFunctions.strCurrency);
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

				blCurr = true;
				new backMethod().execute("EUR");
				// CommonFunctions.strCurrency = "EUR";
				// tvCurrency.setText(CommonFunctions.strCurrency);
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

				blCurr = true;
				new backMethod().execute("AED");
				// CommonFunctions.strCurrency = "AED";
				// tvCurrency.setText(CommonFunctions.strCurrency);
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

				blCurr = true;
				new backMethod().execute("SAR");
				// CommonFunctions.strCurrency = "SAR";
				// tvCurrency.setText(CommonFunctions.strCurrency);
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

				blCurr = true;
				new backMethod().execute("IQD");
				// CommonFunctions.strCurrency = "IQD";
				// tvCurrency.setText(CommonFunctions.strCurrency);
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

				blCurr = true;
				new backMethod().execute("GBP");
				// CommonFunctions.strCurrency = "GBP";
				// tvCurrency.setText(CommonFunctions.strCurrency);
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

				blCurr = true;
				new backMethod().execute("GEL");
				// CommonFunctions.strCurrency = "GEL";
				// tvCurrency.setText(CommonFunctions.strCurrency);
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

				blCurr = true;
				new backMethod().execute("BHD");
				// CommonFunctions.strCurrency = "BHD";
				// tvCurrency.setText(CommonFunctions.strCurrency);
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

				blCurr = true;
				new backMethod().execute("OMR");
				// CommonFunctions.strCurrency = "OMR";
				// tvCurrency.setText(CommonFunctions.strCurrency);
				curr.dismiss();
			}
		});

		curr.show();

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
