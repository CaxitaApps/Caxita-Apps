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
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hjozzat.support.CommonFunctions;
import com.hjozzat.support.CustomDatePickerDialog;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class FlightPaxActivity extends Activity {
	Calendar dateSelected = Calendar.getInstance();
	private DatePickerDialog datePickerDialog;

	int Adultcount, Childcount, Infantcount;
	String strAdult, strChild, strInfant;
	String[] Countrycode;
	AssetManager am;
	ArrayList<String> arrayCountry;
	LinearLayout llDetails, llBaggagePrice;
	Spinner spCountrycode;
	TextView txt_continewithoutlogin, txt_Email, txt_countrycode, txt_mobileno,
			mDateDisplay, tvBaggage, tvPrice;
	EditText etEmailAddress, EtMobileNo;
	String JSonPassengerDetails = null;
	String jsonBaggageList;
	Dialog loaderDialog;

	final Calendar c = Calendar.getInstance();
	int mYear = c.get(Calendar.YEAR);
	int mMonth = c.get(Calendar.MONTH);
	int mDay = c.get(Calendar.DAY_OF_MONTH);
	int maxmnth = c.get(Calendar.DECEMBER);

	int maxYear = mYear - 12;
	int maxMonth = mMonth;
	int maxDay = mDay + 1;

	int minYear = mYear - 100;
	int minMonth = mMonth;
	int minDay = mDay;

	int childmaxYear = mYear - 2;
	int childmaxMonth = mMonth;
	int childmaxDay = mDay + 1;

	int childminYear = mYear - 12;
	int childminMonth = mMonth;
	int childminDay = mDay + 2;

	int infantmaxYear = mYear;
	int infantmaxMonth = mMonth;
	int infantmaxDay = mDay;

	int infantminYear = mYear - 2;
	int infantminMonth = mMonth;
	int infantminDay = mDay + 2;

	int passportmaxYear = mYear + 25;
	int passportmaxMonth = mMonth;
	int passportmaxDay = mDay;

	int passportminYear = mYear;
	int passportminMonth = mMonth;
	int passportminDay = mDay;

	String s = null, strCntryCode, sID = null, tripId = null;
	int y = 1980, m = 01, d = 01;
	JSONObject json, fareObj;
	JSONArray postjson, jarray;
	public String[] arrTitle = { "Title", "Male", "Female" };
	ArrayList<String> arrayChild, arrayAdult;
	Double dblBaggage = 0.0, dblFlightPrice = 0.0;
	ArrayAdapter<String> CountryCodeAdapter;
	public static Activity activityFlightPax;
	String email, mobileno;
	ValueAnimator mAnimator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().hide();

		activityFlightPax = this;

		setContentView(R.layout.activity_flight_paxpage);

		sID = getIntent().getExtras().getString("sID", null);
		tripId = getIntent().getExtras().getString("tripID", null);
		Log.e("tripId", tripId + "sID" + sID);

		initilize();
		setHeader();
		loadAssets();

		new GetPaxDetails().execute();

	}


	public void initilize() {

		Adultcount = FlightResultActivity.adultCount;
		Childcount = FlightResultActivity.childCount;
		Infantcount = FlightResultActivity.infantCount;
		
		strAdult = "Adult";
		strChild = "Child";
		strInfant = "Infant In Lap";
		
		((TextView) findViewById(R.id.txt_count)).setText(String
				.valueOf(Adultcount + Childcount + Infantcount));
		
		loaderDialog = new Dialog(FlightPaxActivity.this,
				android.R.style.Theme_Translucent);
		loaderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loaderDialog.getWindow().setGravity(Gravity.TOP);
		loaderDialog.setContentView(R.layout.dialog_loader);
		loaderDialog.setCancelable(false);

		((ImageView) loaderDialog.findViewById(R.id.iv_loader))
				.setImageResource(R.drawable.flight_loader);

		txt_continewithoutlogin = (TextView) findViewById(R.id.txt_contine);

		arrayAdult = new ArrayList<String>();
		arrayChild = new ArrayList<String>();

		llDetails = (LinearLayout) findViewById(R.id.ll_details);
		

		llBaggagePrice = (LinearLayout) findViewById(R.id.ll_baggage);
		tvBaggage = (TextView) findViewById(R.id.tv_baggage_fees);
		
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

		case R.id.btn_pay:
			submit();
			break;

		case R.id.txt_forgotpassword:
			Intent forgot = new Intent(FlightPaxActivity.this,
					LoginActivity.class);
			forgot.putExtra("from_pax", true);
			startActivity(forgot);
			break;

		case R.id.tv_signup:
			Intent register = new Intent(FlightPaxActivity.this,
					RegisterActivity.class);
			register.putExtra("from_pax", true);
			startActivity(register);
			break;

		// case R.id.login_btn:
		// if (!Loginvalidate()) {
		// Toast.makeText(getApplicationContext(),
		// "Something went wrong. Please try again later",
		// Toast.LENGTH_LONG).show();
		//
		// } else {
		// new LoginService().execute();
		// }
		// break;

//		case R.id.btn_pls_login:
//			if (llLoginLayout.getVisibility() == View.VISIBLE) {
//				// Its visible
//				llLoginLayout.setVisibility(View.GONE);
//			} else {
//				// Either gone or invisible
//				llLoginLayout.setVisibility(View.VISIBLE);
//			}
//			break;

		case R.id.tv_rules:
			Intent rules = new Intent(FlightPaxActivity.this, WebActivity.class);
			rules.putExtra("url", CommonFunctions.main_url
					+ CommonFunctions.lang + "/Shared/Terms");
			startActivity(rules);

		default:
			break;
		}
	}

	private void setBaggage() {

		llBaggagePrice.setVisibility(dblBaggage > 0 ? View.VISIBLE : View.GONE);
		tvBaggage.setText(CommonFunctions.strCurrency + " "
				+ String.format(new Locale("en"), "%.3f", dblBaggage));

		tvPrice.setText(CommonFunctions.strCurrency
				+ " "
				+ String.format(new Locale("en"), "%.3f", dblFlightPrice
						+ dblBaggage));
	}

	@SuppressWarnings("unchecked")
	public void layoutView() {
		// TODO Auto-generated method stub

		int id = 1;
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				FlightPaxActivity.this, R.array.title_spinner_items,
				R.layout.tv_spinner);
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);

		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
				FlightPaxActivity.this, R.layout.tv_spinner, arrayCountry);
		adapter2.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);

		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(
				FlightPaxActivity.this, android.R.layout.simple_list_item_1, Countrycode);
		adapter3.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
		Spinner Spn = null;
		LinearLayout ll,llHeader;
		
		LinearLayout llBaggage;
		TextView tv;
		String strTemp = null;

		for (int i = 1; i <= Adultcount; i++) {
			final View view = getLayoutInflater().inflate(
					R.layout.flight_pax_detail_items, null);
			ll = (LinearLayout) view.findViewById(R.id.ll_email_ph);
			if (i == 1) {

				ll.setVisibility(View.VISIBLE);
			} else {
				ll.setVisibility(View.GONE);
			}
			view.setId(id);
			id++;
			view.setTag(strAdult);
			
			llHeader=(LinearLayout)view.findViewById(R.id.header);
			final LinearLayout llExpandable=(LinearLayout)view.findViewById(R.id.expandable);
			llHeader.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(llExpandable.getVisibility()==View.VISIBLE){
						llExpandable.setVisibility(View.GONE);
						((ImageView)view.findViewById(R.id.iv_updown)).setImageResource(R.drawable.ic_down_arrow);
			}else{
				((ImageView)view.findViewById(R.id.iv_updown)).setImageResource(R.drawable.ic_top_arrow);
						llExpandable.setVisibility(View.VISIBLE);
						}
				}
			});
			
			String strTraveller = getString(R.string.traveller) + " "
					+ String.valueOf(i);

			strTemp = "<font color=#181818>" + strTraveller
					+ "</font><font color=#919090>(" + strAdult + ")</font>";
			((TextView) view.findViewById(R.id.txt_caption)).setText(Html
					.fromHtml(strTemp));
			if (!CommonFunctions.lang.equals("en")) {

				((TextView) view.findViewById(R.id.txt_caption))
						.setText("بالغ " + " " + i);
			}
			Log.e("arrayAdult",
					arrayAdult.toString() + "  \n Lenght="
							+ String.valueOf(arrayAdult.size()));

			if (arrayAdult.size() > 0) {
				llBaggage = (LinearLayout) view
						.findViewById(R.id.ll_checkedbaggage);
				llBaggage.setVisibility(View.VISIBLE);
				Spn = (Spinner) view.findViewById(R.id.selectbaggage);
				ArrayAdapter<String> Adultadapter = new ArrayAdapter<String>(
						FlightPaxActivity.this, R.layout.tv_spinner, arrayAdult);
				Adultadapter
						.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
				Spn.setAdapter(Adultadapter);

				Spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						if (position != 0) {
							String baggage = parent.getSelectedItem()
									.toString();
							baggage = baggage.substring(baggage
									.indexOf(CommonFunctions.strCurrency) + 4,
									baggage.length());
							dblBaggage = dblBaggage
									+ Double.parseDouble(baggage);
						}
						setBaggage();
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});
			}

			String Strpmonth = String.valueOf(passportminMonth + 1);
			if (Strpmonth.length() == 1) {
				Strpmonth = "0" + String.valueOf(passportminMonth + 1);
			}
			String strCuurentDay=String.valueOf(mDay);
			if (strCuurentDay.length() == 1) {
				strCuurentDay = "0" + String.valueOf(mDay);
			}
			((TextView) view.findViewById(R.id.txt_passportday)).setText(strCuurentDay
					+ "/" + Strpmonth + "/" + String.valueOf(passportminYear));
			((TextView) view.findViewById(R.id.edt_day)).setText("01/01/"
					+ String.valueOf(y));

			Spn = (Spinner) view.findViewById(R.id.spn_countrycode);
			Spn.setAdapter(adapter3);

			Spn.setSelection(((ArrayAdapter<String>) Spn.getAdapter())
					.getPosition("+965"));

			Spn = (Spinner) view.findViewById(R.id.Spn_title);

			Spn.setAdapter(adapter);

			Spn = (Spinner) view.findViewById(R.id.spn_passport_issued);

			Spn.setAdapter(adapter2);
			Spn.setSelection(((ArrayAdapter<String>) Spn.getAdapter())
					.getPosition("KUWAIT - KW"));
			Spn = (Spinner) view.findViewById(R.id.spn_nationality);
			Spn.setAdapter(adapter2);
			Spn.setSelection(((ArrayAdapter<String>) Spn.getAdapter())
					.getPosition("KUWAIT - KW"));

			ll = (LinearLayout) view.findViewById(R.id.ll_passportexpiry);
			ll.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					// TODO Auto-generated method stub
					datePickerDialog = new CustomDatePickerDialog(
							FlightPaxActivity.this,
							new OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker datepicker,
										int year, int monthOfYear,
										int dayOfMonth) {
									// TODO Auto-generated method stub
									try {
										Log.e("DAta SEt",
												String.valueOf(dayOfMonth
														+ "  " + monthOfYear
														+ "  " + year));
										String str_day = String
												.valueOf(dayOfMonth);
										if (str_day.length() == 1) {
											str_day = "0"
													+ String.valueOf(dayOfMonth);
										}
										String str_month = String
												.valueOf(monthOfYear + 1);
										if (str_month.length() == 1) {
											str_month = "0"
													+ String.valueOf(monthOfYear + 1);
										}
										String str_year = String.valueOf(year);
										if (str_year.length() == 1) {
											str_day = "0"
													+ String.valueOf(year);
										}
										TextView txt_date = (TextView) view
												.findViewById(R.id.txt_passportday);

										s = String.valueOf(str_day + "/"
												+ str_month + "/" + str_year);
										txt_date.setText(s);
										
										
									} catch (Exception e) {

									}

								}

							}, passportminYear, passportminMonth,
							passportminDay, passportmaxYear, passportmaxMonth,
							passportmaxDay);
					datePickerDialog.show();

				}
			});
			
			ll = (LinearLayout) view.findViewById(R.id.ll_dateofbirth);
			ll.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					// TODO Auto-generated method stub
					datePickerDialog = new CustomDatePickerDialog(
							FlightPaxActivity.this,
							new OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker datepicker,
										int year, int monthOfYear,
										int dayOfMonth) {
									// TODO Auto-generated method stub
									try {
										Log.e("DAta SEt",
												String.valueOf(dayOfMonth
														+ "  " + monthOfYear
														+ "  " + year));
										String str_day = String
												.valueOf(dayOfMonth);
										if (str_day.length() == 1) {
											str_day = "0"
													+ String.valueOf(dayOfMonth);
										}
										String str_month = String
												.valueOf(monthOfYear + 1);
										if (str_month.length() == 1) {
											str_month = "0"
													+ String.valueOf(monthOfYear + 1);
										}
										String str_year = String.valueOf(year);
										if (str_year.length() == 1) {
											str_day = "0"
													+ String.valueOf(year);
										}
										TextView txt_date = (TextView) view
												.findViewById(R.id.edt_day);

										s = String.valueOf(str_day + "/"
												+ str_month + "/" + str_year);

										txt_date.setText(s);						
																														
									} catch (Exception e) {

									}

								}

							}, minYear, minMonth, minDay, maxYear, maxMonth,
							maxDay);
					datePickerDialog.show();
					datePickerDialog.updateDate(y, m - 1, d);

				}
			});	
			
			
			
			tv = (TextView) view.findViewById(R.id.txt_title);
			strTemp = "<font color=#000000>Title</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_fname);
			strTemp = "<font color=#000000>First Name</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_lname);
			strTemp = "<font color=#000000>Last Name</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_emailadddress);
			strTemp = "<font color=#000000>Email ID</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_phoneno);
			strTemp = "<font color=#000000>Phone Number</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_nationality);
			strTemp = "<font color=#000000>Nationality</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_dob);
			strTemp = "<font color=#000000>Date Of Birth</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_passportno);
			strTemp = "<font color=#000000>Passport Number</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_passpoprtissued);
			strTemp = "<font color=#000000>Passport Issued</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_passportexpiry);
			strTemp = "<font color=#000000>Passport Expiry</font>";
			tv.setText(Html.fromHtml(strTemp));

			if (!CommonFunctions.lang.equals("en")) {
				tv = (TextView) view.findViewById(R.id.txt_title);
				strTemp = "<font color=#000000>اللقب</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_fname);
				strTemp = "<font color=#000000>الاسم الأول </font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_lname);
				strTemp = "<font color=#000000>اسم العائلة</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_emailadddress);
				strTemp = "<font color=#000000>عنوان البريد الالكتروني</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_phoneno);
				strTemp = "<font color=#000000>رقم الهاتف النقال</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_nationality);
				strTemp = "<font color=#000000>الجنسية</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_dob);
				strTemp = "<font color=#000000>تاريخ الميلاد</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_passportno);
				strTemp = "<font color=#000000>رقم الجواز السفر</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_passpoprtissued);
				strTemp = "<font color=#000000>جواز سفر</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_passportexpiry);
				strTemp = "<font color=#000000>تاريخ انتهاء جواز السفر</font>";
				tv.setText(Html.fromHtml(strTemp));
			}
			llDetails.addView(view);
		}
		for (int j = 1; j <= Childcount; j++) {
			final View view = getLayoutInflater().inflate(
					R.layout.flight_pax_detail_items, null);
			ll = (LinearLayout) view.findViewById(R.id.ll_email_ph);
			ll.setVisibility(View.GONE);
			view.setId(id);
			id++;
			view.setTag(strChild);
			String strTraveller = getString(R.string.traveller) + " "
					+ String.valueOf(j);

			strTemp = "<font color=#181818>" + strTraveller
					+ "</font><font color=#919090>(" + strChild + ")</font>";
			((TextView) view.findViewById(R.id.txt_caption)).setText(Html
					.fromHtml(strTemp));
			

			if (!CommonFunctions.lang.equals("en")) {

				((TextView) view.findViewById(R.id.txt_caption))
						.setText("طفل  " + " " + j);
			}

			llHeader=(LinearLayout)view.findViewById(R.id.header);
			final LinearLayout llExpandable=(LinearLayout)view.findViewById(R.id.expandable);
			llHeader.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(llExpandable.getVisibility()==View.VISIBLE){
						llExpandable.setVisibility(View.GONE);
						((ImageView)view.findViewById(R.id.iv_updown)).setImageResource(R.drawable.ic_down_arrow);;
			}else{
				((ImageView)view.findViewById(R.id.iv_updown)).setImageResource(R.drawable.ic_top_arrow);
						llExpandable.setVisibility(View.VISIBLE);
						}
				}
			});
			
			if (arrayChild.size() > 0) {
				llBaggage = (LinearLayout) view
						.findViewById(R.id.ll_checkedbaggage);
				llBaggage.setVisibility(View.VISIBLE);
				Spn = (Spinner) view.findViewById(R.id.selectbaggage);
				ArrayAdapter<String> Childadapter = new ArrayAdapter<String>(
						FlightPaxActivity.this, R.layout.tv_spinner, arrayChild);
				Childadapter
						.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
				Spn.setAdapter(Childadapter);
				Spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						if (position != 0) {
							String baggage = parent.getSelectedItem()
									.toString();
							baggage = baggage.substring(baggage
									.indexOf(CommonFunctions.strCurrency) + 4,
									baggage.length());
							dblBaggage = dblBaggage
									+ Double.parseDouble(baggage);
						}
						setBaggage();
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});
			}

			String Strpmonth = String.valueOf(passportminMonth + 1);
			if (Strpmonth.length() <= 1) {
				Strpmonth = "0" + String.valueOf(passportminMonth + 1);
			}

			String strCuurentDay=String.valueOf(mDay);
			if (strCuurentDay.length() == 1) {
				strCuurentDay = "0" + String.valueOf(mDay);
			}
			((TextView) view.findViewById(R.id.txt_passportday)).setText(strCuurentDay
					+ "/" + Strpmonth + "/" + String.valueOf(passportminYear));
			
			((TextView) view.findViewById(R.id.edt_day)).setText("01/01/"
					+ String.valueOf(mYear - 2));

			Spn = (Spinner) view.findViewById(R.id.Spn_title);
			Spn.setAdapter(adapter);
			Spn = (Spinner) view.findViewById(R.id.spn_passport_issued);
			Spn.setAdapter(adapter2);
			Spn.setSelection(((ArrayAdapter<String>) Spn.getAdapter())
					.getPosition("KUWAIT - KW"));
			Spn = (Spinner) view.findViewById(R.id.spn_nationality);
			Spn.setAdapter(adapter2);
			Spn.setSelection(((ArrayAdapter<String>) Spn.getAdapter())
					.getPosition("KUWAIT - KW"));
			ll = (LinearLayout) view.findViewById(R.id.ll_passportexpiry);
			ll.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					// TODO Child Passport Expiry(Date Picker)
					datePickerDialog = new CustomDatePickerDialog(
							FlightPaxActivity.this,
							new OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker datepicker,
										int year, int monthOfYear,
										int dayOfMonth) {
									try {
										Log.e("DAta SEt",
												String.valueOf(dayOfMonth
														+ "  " + monthOfYear
														+ "  " + year));
										String str_day = String
												.valueOf(dayOfMonth);
										if (str_day.length() == 1) {
											str_day = "0"
													+ String.valueOf(dayOfMonth);
										}
										String str_month = String
												.valueOf(monthOfYear + 1);
										if (str_month.length() == 1) {
											str_month = "0"
													+ String.valueOf(monthOfYear + 1);
										}
										String str_year = String.valueOf(year);
										if (str_year.length() == 1) {
											str_day = "0"
													+ String.valueOf(year);
										}
										TextView txt_date = (TextView) view
												.findViewById(R.id.txt_passportday);

										s = String.valueOf(str_day + "/"
												+ str_month + "/" + str_year);
										txt_date.setText(s);
									} catch (Exception e) {

									}

								}

							}, passportminYear, passportminMonth,
							passportminDay, passportmaxYear, passportmaxMonth,
							passportmaxDay);
					datePickerDialog.show();

				}
			});

			ll = (LinearLayout) view.findViewById(R.id.ll_dateofbirth);
			ll.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					// TODO Child Date Of Birth (Date Picker)
					datePickerDialog = new CustomDatePickerDialog(
							FlightPaxActivity.this,
							new OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker datepicker,
										int year, int monthOfYear,
										int dayOfMonth) {
									try {
										Log.e("DAta SEt",
												String.valueOf(dayOfMonth
														+ "  " + monthOfYear
														+ "  " + year));
										String str_day = String
												.valueOf(dayOfMonth);
										if (str_day.length() == 1) {
											str_day = "0"
													+ String.valueOf(dayOfMonth);
										}
										String str_month = String
												.valueOf(monthOfYear + 1);
										if (str_month.length() == 1) {
											str_month = "0"
													+ String.valueOf(monthOfYear + 1);
										}
										String str_year = String.valueOf(year);
										if (str_year.length() == 1) {
											str_day = "0"
													+ String.valueOf(year);
										}
										TextView txt_date = (TextView) view
												.findViewById(R.id.edt_day);

										txt_date.setText(str_day);

										s = String.valueOf(str_day + "/"
												+ str_month + "/" + str_year);
										txt_date.setText(s);
									} catch (Exception e) {

									}

								}

							}, childminYear, childminMonth, childminDay,
							childmaxYear, childmaxMonth, childmaxDay);
					datePickerDialog.show();
					datePickerDialog.updateDate(mYear - 2, m - 1, d);

				}
			});

			tv = (TextView) view.findViewById(R.id.txt_title);
			strTemp = "<font color=#000000>Title</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_fname);
			strTemp = "<font color=#000000>First Name</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_lname);
			strTemp = "<font color=#000000>Last Name</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_nationality);
			strTemp = "<font color=#000000>Nationality</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_dob);
			strTemp = "<font color=#000000>Date Of Birth</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_passportno);
			strTemp = "<font color=#000000>passport Number</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_passpoprtissued);
			strTemp = "<font color=#000000>Passport Issued</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_passportexpiry);
			strTemp = "<font color=#000000>Passport Expiry</font>";
			tv.setText(Html.fromHtml(strTemp));
			if (!CommonFunctions.lang.equals("en")) {
				tv = (TextView) view.findViewById(R.id.txt_title);
				strTemp = "<font color=#000000>اللقب</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_fname);
				strTemp = "<font color=#000000>الاسم الأول </font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_lname);
				strTemp = "<font color=#000000>اسم العائلة</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_nationality);
				strTemp = "<font color=#000000>الجنسية</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_dob);
				strTemp = "<font color=#000000>تاريخ الميلاد</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_passportno);
				strTemp = "<font color=#000000>رقم الجواز السفر</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_passpoprtissued);
				strTemp = "<font color=#000000>جواز سفر</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_passportexpiry);
				strTemp = "<font color=#000000>تاريخ انتهاء جواز السفر</font>";
				tv.setText(Html.fromHtml(strTemp));
			}
			llDetails.addView(view);
		}

		for (int k = 1; k <= Infantcount; k++) {
			final View view = getLayoutInflater().inflate(
					R.layout.flight_pax_detail_items, null);

			view.setId(id);
			id++;
			ll = (LinearLayout) view.findViewById(R.id.ll_email_ph);
			ll.setVisibility(View.GONE);
			view.setTag(strInfant);

			String strTraveller = getString(R.string.traveller) + " "
					+ String.valueOf(k);

			strTemp = "<font color=#181818>" + strTraveller
					+ "</font><font color=#919090>(" + strInfant + ")</font>";
			((TextView) view.findViewById(R.id.txt_caption)).setText(Html
					.fromHtml(strTemp));

			if (!CommonFunctions.lang.equals("en")) {

				((TextView) view.findViewById(R.id.txt_caption))
						.setText("الرضيع  " + " " + k);
			}

			llHeader=(LinearLayout)view.findViewById(R.id.header);
			final LinearLayout llExpandable=(LinearLayout)view.findViewById(R.id.expandable);
			llHeader.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(llExpandable.getVisibility()==View.VISIBLE){
						llExpandable.setVisibility(View.GONE);
						((ImageView)view.findViewById(R.id.iv_updown)).setImageResource(R.drawable.ic_down_arrow);;
			}else{
				((ImageView)view.findViewById(R.id.iv_updown)).setImageResource(R.drawable.ic_top_arrow);
						llExpandable.setVisibility(View.VISIBLE);
						}
				}
			});
			
			String Strpmonth = String.valueOf(passportminMonth + 1);
			if (Strpmonth.length() <= 1) {
				Strpmonth = "0" + String.valueOf(passportminMonth + 1);
			}

			String strCuurentDay=String.valueOf(mDay);
			if (strCuurentDay.length() == 1) {
				strCuurentDay = "0" + String.valueOf(mDay);
			}
			((TextView) view.findViewById(R.id.txt_passportday)).setText(strCuurentDay
					+ "/" + Strpmonth + "/" + String.valueOf(passportminYear));
			
			((TextView) view.findViewById(R.id.edt_day)).setText("01/01/"
					+ String.valueOf(mYear));

			Spn = (Spinner) view.findViewById(R.id.Spn_title);
			Spn.setAdapter(adapter);
			Spn = (Spinner) view.findViewById(R.id.spn_passport_issued);

			Spn.setAdapter(adapter2);
			Spn.setSelection(((ArrayAdapter<String>) Spn.getAdapter())
					.getPosition("KUWAIT - KW"));
			Spn = (Spinner) view.findViewById(R.id.spn_nationality);
			Spn.setAdapter(adapter2);
			Spn.setSelection(((ArrayAdapter<String>) Spn.getAdapter())
					.getPosition("KUWAIT - KW"));
			ll = (LinearLayout) view.findViewById(R.id.ll_passportexpiry);
			ll.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					// TODO Infant passport expiry date 
					datePickerDialog = new CustomDatePickerDialog(
							FlightPaxActivity.this,
							new OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker datepicker,
										int year, int monthOfYear,
										int dayOfMonth) {
									try {
										Log.e("DAta SEt",
												String.valueOf(dayOfMonth
														+ "  " + monthOfYear
														+ "  " + year));
										String str_day = String
												.valueOf(dayOfMonth);
										if (str_day.length() == 1) {
											str_day = "0"
													+ String.valueOf(dayOfMonth);
										}
										String str_month = String
												.valueOf(monthOfYear + 1);
										if (str_month.length() == 1) {
											str_month = "0"
													+ String.valueOf(monthOfYear + 1);
										}
										String str_year = String.valueOf(year);
										if (str_year.length() == 1) {
											str_day = "0"
													+ String.valueOf(year);
										}
										TextView txt_date = (TextView) view
												.findViewById(R.id.txt_passportday);
										s = String.valueOf(str_day + "/"
												+ str_month + "/" + str_year);
										txt_date.setText(s);
									} catch (Exception e) {

									}

								}

							}, passportminYear, passportminMonth,
							passportminDay, passportmaxYear, passportmaxMonth,
							passportmaxDay);
					datePickerDialog.show();

				}
			});

			ll = (LinearLayout) view.findViewById(R.id.ll_dateofbirth);
			ll.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					// TODO Infant Date Of Birth
					datePickerDialog = new CustomDatePickerDialog(
							FlightPaxActivity.this,
							new OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker datepicker,
										int year, int monthOfYear,
										int dayOfMonth) {
									try {
										Log.e("DAta SEt",
												String.valueOf(dayOfMonth
														+ "  " + monthOfYear
														+ "  " + year));
										String str_day = String
												.valueOf(dayOfMonth);
										if (str_day.length() == 1) {
											str_day = "0"
													+ String.valueOf(dayOfMonth);
										}
										String str_month = String
												.valueOf(monthOfYear + 1);
										if (str_month.length() == 1) {
											str_month = "0"
													+ String.valueOf(monthOfYear + 1);
										}
										String str_year = String.valueOf(year);
										if (str_year.length() == 1) {
											str_year = "0"
													+ String.valueOf(year);
										}
										TextView txt_date = (TextView) view
												.findViewById(R.id.edt_day);

										s = String.valueOf(str_day + "/"
												+ str_month + "/" + str_year);
										txt_date.setText(s);
									} catch (Exception e) {

									}

								}

							}, infantminYear, infantminMonth, infantminDay,
							infantmaxYear, infantmaxMonth, infantmaxDay);
					datePickerDialog.show();
					datePickerDialog.updateDate(mYear, m - 1, d);
				}
			});

			tv = (TextView) view.findViewById(R.id.txt_title);
			strTemp = "<font color=#000000>Title</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_fname);
			strTemp = "<font color=#000000>First Name</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_lname);
			strTemp = "<font color=#000000>Last Name</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_nationality);
			strTemp = "<font color=#000000>Nationality</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_dob);
			strTemp = "<font color=#000000>Date Of Birth</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_passportno);
			strTemp = "<font color=#000000>passport Number</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_passpoprtissued);
			strTemp = "<font color=#000000>Passport Issued</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_passportexpiry);
			strTemp = "<font color=#000000>Passport Expiry</font>";
			tv.setText(Html.fromHtml(strTemp));
			if (!CommonFunctions.lang.equals("en")) {
				tv = (TextView) view.findViewById(R.id.txt_title);
				strTemp = "<font color=#000000>اللقب</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_fname);
				strTemp = "<font color=#000000>الاسم الأول </font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_lname);
				strTemp = "<font color=#000000>اسم العائلة</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_nationality);
				strTemp = "<font color=#000000>الجنسية</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_dob);
				strTemp = "<font color=#000000>تاريخ الميلاد</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_passportno);
				strTemp = "<font color=#000000>رقم الجواز السفر</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_passpoprtissued);
				strTemp = "<font color=#000000>جواز سفر</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_passportexpiry);
				strTemp = "<font color=#000000>تاريخ انتهاء جواز السفر</font>";
				tv.setText(Html.fromHtml(strTemp));
			}
			llDetails.addView(view);
		}
	}

	@SuppressWarnings("deprecation")
	private void submit() {

		JSONArray PassengerInfo = new JSONArray();
		ArrayList<String> PassportNoList = new ArrayList<String>();
		ArrayList<String> arrayFLList = new ArrayList<String>();
		PassportNoList.clear();
		JSONObject obj = null;
		int flag = 0;
		String strFirstName, strLastName, strPassportnumber = "", strFLName, strDOB, strPassportDOB, strNationality, strNationalityPass;
		EditText etFirstName = null, etLastName = null, etPassportNum = null;
		TextView tvDOB = null;
		TextView tvPassDOB = null;
		int FlagEmailPh = 1;
		String StrDay, StrMonth, StrYear, StrPassDay, StrPassMonth, StrPassYear;
		LinearLayout llExpandable;
		Spinner spGender = null, spNationality = null, spNationalityPass = null;
		LinearLayout llBaggage;
		ImageView iv;
		int baggageId = 0, intGender = 0, i;

		for (i = 1; i <= Adultcount + Childcount + Infantcount; i++) {

			View view = llDetails.findViewById(i);
			String strPassengerType = view.getTag().toString();
			
			llExpandable=(LinearLayout)view.findViewById(R.id.expandable);
			iv=(ImageView)view.findViewById(R.id.iv_updown);
			
			etFirstName = (EditText) view.findViewById(R.id.edt_firstname);
			strFirstName = etFirstName.getText().toString();

			etLastName = (EditText) view.findViewById(R.id.edt_lastname);
			strLastName = etLastName.getText().toString();
			strFLName = strFirstName + " " + strLastName;

			etPassportNum = (EditText) view.findViewById(R.id.edt_passportno);
			strPassportnumber = etPassportNum.getText().toString();

			tvDOB = (TextView) view.findViewById(R.id.edt_day);
			strDOB = tvDOB.getText().toString();

			tvPassDOB = (TextView) view.findViewById(R.id.txt_passportday);
			strPassportDOB = tvPassDOB.getText().toString();

			strDOB = new SimpleDateFormat("dd/MM/yyyy", new Locale("en"))
					.format(new Date(strDOB));

			StrDay = new SimpleDateFormat("dd", new Locale("en"))
					.format(new Date(strDOB));
			StrMonth = new SimpleDateFormat("MM", new Locale("en"))
					.format(new Date(strDOB));
			StrYear = new SimpleDateFormat("yyyy", new Locale("en"))
					.format(new Date(strDOB));

			strPassportDOB = new SimpleDateFormat("dd/MM/yyyy",
					new Locale("en")).format(new Date(strPassportDOB));
			StrPassDay = new SimpleDateFormat("dd", new Locale("en"))
					.format(new Date(strPassportDOB));
			StrPassMonth = new SimpleDateFormat("MM", new Locale("en"))
					.format(new Date(strPassportDOB));
			StrPassYear = new SimpleDateFormat("yyyy", new Locale("en"))
					.format(new Date(strPassportDOB));

			Log.e("checking ", StrDay + "/" + StrMonth + "/" + StrYear
					+ "\n \n passport" + StrPassDay + "/" + StrPassMonth + "/"
					+ StrPassYear);

			spGender = (Spinner) view.findViewById(R.id.Spn_title);
			intGender = spGender.getSelectedItemPosition();
			String strGender = arrTitle[intGender];
			Log.e("checking ", strGender);

			spNationality = (Spinner) view.findViewById(R.id.spn_nationality);
			String strNation = spNationality.getSelectedItem().toString();

			strNationality = strNation.substring(Math.max(
					strNation.length() - 2, 0));
			
			Log.e("Checking ", strNationality);
			spNationalityPass = (Spinner) view
					.findViewById(R.id.spn_passport_issued);

			strNationalityPass = spNationalityPass.getSelectedItem().toString();
			strNationalityPass = strNationalityPass.substring(Math.max(
					strNationalityPass.length() - 2, 0));
			if (i == 1) {
				etEmailAddress = (EditText) view
						.findViewById(R.id.edt_emailaddress);
				EtMobileNo = (EditText) view.findViewById(R.id.edt_phoneno);
				spCountrycode = (Spinner) view
						.findViewById(R.id.spn_countrycode);

				email = etEmailAddress.getText().toString();
				strCntryCode = spCountrycode.getSelectedItem().toString();
				mobileno = EtMobileNo.getText().toString();
			}
			llBaggage = (LinearLayout) view
					.findViewById(R.id.ll_checkedbaggage);

			if (llBaggage.getVisibility() == View.VISIBLE) {
				baggageId = ((Spinner) view.findViewById(R.id.selectbaggage))
						.getSelectedItemPosition();
			}
			if (FlagEmailPh == 1) {
				if (email.isEmpty()
						|| !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
								.matches()) {
					etEmailAddress.setError(getResources().getString(
							R.string.error_invalid_email));					
					if (flag == 0){
						etEmailAddress.requestFocus();
						if(llExpandable.getVisibility()==View.GONE)
							llExpandable.setVisibility(View.VISIBLE);
						iv.setImageResource(R.drawable.ic_top_arrow);
					}
					flag = 1;
				} else {
					etEmailAddress.setError(null);
				}

				if (mobileno.isEmpty() || mobileno.length() < 5
						|| mobileno.length() > 15) {
					EtMobileNo.setError(getResources().getString(
							R.string.error_invalid_number));
					if (flag == 0){
						EtMobileNo.requestFocus();
						if(llExpandable.getVisibility()==View.GONE)
							llExpandable.setVisibility(View.VISIBLE);
						iv.setImageResource(R.drawable.ic_top_arrow);
					}
					flag = 1;
				} else {
					EtMobileNo.setError(null);
				}
				FlagEmailPh = 0;
			}
			if (intGender == 0) {
				if (flag == 0) {
					etFirstName.requestFocus();
					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.err_title_req),
							Toast.LENGTH_SHORT).show();
					if(llExpandable.getVisibility()==View.GONE)
						llExpandable.setVisibility(View.VISIBLE);
					iv.setImageResource(R.drawable.ic_top_arrow);
				}
				flag = 1;
			}

			if (spNationality.getSelectedItemPosition() == 0) {
				flag = 1;
			}

			if (strFirstName.equals("") || strFirstName.length() < 2
					|| strFirstName.length() > 19) {
				Log.e(" FirstName.equals Checking ", String.valueOf(flag));
				if (flag == 0){
					etFirstName.requestFocus();
					if(llExpandable.getVisibility()==View.GONE)
						llExpandable.setVisibility(View.VISIBLE);
					iv.setImageResource(R.drawable.ic_top_arrow);
					}
				flag = 1;
				etFirstName.setError(getResources().getString(
						R.string.error_firstname_length));

			}
			if (strLastName.equals("") || strLastName.length() < 2
					|| strLastName.length() > 21) {
				if (flag == 0){
					etLastName.requestFocus();
					if(llExpandable.getVisibility()==View.GONE)
						llExpandable.setVisibility(View.VISIBLE);
					iv.setImageResource(R.drawable.ic_top_arrow);
					}
				flag = 1;
				etLastName.setError(getResources().getString(
						R.string.error_lastname_length));
				Log.e("LastName.equals Checking ", String.valueOf(flag));
			}

			if (strPassportnumber.equals("")) {
				if (flag == 0){
					etPassportNum.requestFocus();
					if(llExpandable.getVisibility()==View.GONE)
						llExpandable.setVisibility(View.VISIBLE);
					iv.setImageResource(R.drawable.ic_top_arrow);
					}
				flag = 1;
				etPassportNum.setError(getResources().getString(
						R.string.err_empty_passport));
			} else {
				etPassportNum.setError(null);
			}

			if (strFLName.length() > 23) {
				if (flag == 0){
					etFirstName.requestFocus();
					if(llExpandable.getVisibility()==View.GONE)
						llExpandable.setVisibility(View.VISIBLE);
					iv.setImageResource(R.drawable.ic_top_arrow);
					}
				flag = 1;
				etLastName.setError(getResources().getString(
						R.string.err_fl_name_length));

			}

			if (PassportNoList.contains(strPassportnumber)) {
				flag = 1;
				etPassportNum.setError(getResources().getString(
						R.string.err_same_passport));
				if(llExpandable.getVisibility()==View.GONE)
					llExpandable.setVisibility(View.VISIBLE);
				iv.setImageResource(R.drawable.ic_top_arrow);
				Log.e("etPassportNum Checking ", String.valueOf(flag));
			} else if (!PassportNoList.contains(strPassportnumber)
					&& !strPassportnumber.equals("")) {
				PassportNoList.add(strPassportnumber);
			}

			if (!arrayFLList.contains(strFLName)) {
				arrayFLList.add(strFLName);
			} else {
				if (flag == 0){
					etFirstName.requestFocus();
					if(llExpandable.getVisibility()==View.GONE)
						llExpandable.setVisibility(View.VISIBLE);
					iv.setImageResource(R.drawable.ic_top_arrow);
				}
				flag = 1;
				etFirstName.setError(getResources().getString(
						R.string.err_same_fl_name));
				etLastName.setError(getResources().getString(
						R.string.err_same_fl_name));
			}

			Log.e(" Checking Flag", String.valueOf(flag));
			if (flag == 0) {
				int PId = 0;

				obj = new JSONObject();
				try {
					System.out.println("Email:  " + email
							+ "\n \n mobile no:  " + mobileno);
					String strGenderUrl = strGender.substring(0, 1);
					strGenderUrl = strPassengerType.toLowerCase().contains(
							"infant") ? strGenderUrl + "i" : strGenderUrl;

					if (strPassengerType.equalsIgnoreCase("adult"))
						strGender = strGender.equalsIgnoreCase("male") ? "Mr"
								: "Ms";
					else
						strGender = strGender.equalsIgnoreCase("male") ? "Mstr"
								: "Miss";

					obj.put("ContactDetail", null);
					obj.put("CompanyGenQuoteDetails", "");
					obj.put("TnCChecked", false);
					obj.put("TotalAmountForDisplay", 0);
					obj.put("TripId", tripId);
					obj.put("IsLoggedIn", CommonFunctions.loggedIn);
					obj.put("CancellationPolicy", "");
					obj.put("IsPassportOptional", true);
					obj.put("IsEmailOptional", false);
					obj.put("IsFlightHotel", false);
					obj.put("ApiId", 0);
					obj.put("IsRoundTrip", false);
					obj.put("LeadPassengerchangeable", false);
					obj.put("IscontactInfofromPaymentPage", false);
					obj.put("RoomTypeDetails", null);
					obj.put("Rooms", null);
					json = new JSONObject();

					json.put("PassengerId", String.valueOf(PId));
					json.put("Email", etEmailAddress.getText().toString());
					json.put("FirstName", strFirstName);
					json.put("LastName", strLastName);
					json.put("MiddleName", "");
					json.put("Age", 0);
					json.put("Gender", strGenderUrl);
					json.put("MobileNumber", EtMobileNo.getText().toString());
					json.put("PassengerType", strPassengerType);
					json.put("FrequentFlyerNo", null);
					json.put("travelDate", "2016-08-23T00:00:00");
					json.put("PassportNumber", strPassportnumber);
					json.put("IsCivilId", false);
					json.put("DateOfBirth", strDOB);
					json.put("PassportExpiryDate", strPassportDOB);
					json.put("PassportPlaceOfIssue", strNationalityPass);
					json.put("PassportIssueCountryCode", null);
					json.put("Citizenship", strNationality);
					json.put("preferenceDetail", null);
					json.put("IsLeadPassenger", false);
					json.put("IsPassenger", false);
					json.put("HasVisa", false);
					json.put("VisaNumber", null);
					json.put("VisaCity", null);
					json.put("VisaIssuedCountry", null);
					json.put("VisaIssuedDate", "0001-01-01T00:00:00");
					json.put("VisaValidCountry", null);
					json.put("MobileCode", strCntryCode);
					json.put("Password", null);
					json.put("ConfirmPassword", null);
					json.put("Tittle", strGender);
					json.put("DOBDate", StrDay);
					json.put("DOBMonth", StrMonth);
					json.put("DOBYear", StrYear);
					json.put("PassportDay", StrPassDay);
					json.put("PassportMonth", StrPassMonth);
					json.put("PassportYear", StrPassYear);
					json.put("addtraveller", false);
					json.put("TravellerId", 0);
					json.put("SpecialRequests", null);

					if (baggageId > 0) {
						JSONObject newobj = new JSONObject();
						JSONArray BoardingTripList = new JSONArray();

						newobj.put("AllowBaggages", false);
						newobj.put("AllowPriorityBoarding", false);
						newobj.put("AllowCheckIn", false);
						newobj.put("AllowHandBaggage", false);
						newobj.put("BaggageList", null);
						newobj.put("CheckInList", null);
						newobj.put("BoardingText", null);
						newobj.put("HandBagText", null);
						newobj.put("BaggageId", String.valueOf(baggageId));
						newobj.put("CheckInId", null);
						newobj.put("IsPriorityBoarding", false);
						newobj.put("IsHandBaggage", false);
						newobj.put("SameCheckinForAllPassenger", false);
						BoardingTripList.put(newobj);
						json.put("BoardingTripList", BoardingTripList);
					} else {
						json.put("BoardingTripList", null);
					}
					JSONArray years = new JSONArray();

					json.put("Years", years);
					json.put("Dates", years);
					json.put("Months", years);
					json.put("DOBYears", years);
					PassengerInfo.put(json);
					obj.put("PassengerInfo", PassengerInfo);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				PId++;
				Log.e("Checking HotelInfo", obj.toString());
			}
		}
		if (flag == 0) {
			JSonPassengerDetails = obj.toString();
			new PaxDetailSubmission().execute();
		}

	}

	public class PaxDetailSubmission extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub+
			loaderDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			URL url = null;
			try {
				String urlParameters = "responseType="
						+ URLEncoder.encode("PaymentRequest", "UTF-8")
						+ "&passengerDetails="
						+ URLEncoder.encode(JSonPassengerDetails, "UTF-8");

				System.out.print(urlParameters + "\n \n" + JSonPassengerDetails
						+ "\n \n \n");

				String request = CommonFunctions.main_url
						+ CommonFunctions.lang
						+ "/FlightApi/ProceedPaxDetails?sID=" + sID;
				url = new URL(request);
				Log.e("url", url.toString());
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

				System.out.println("res" + res);

				conn.disconnect();

				JSONObject json = new JSONObject(res);

				JSONObject object = json.getJSONObject("data");
				Log.e("object  ", object.toString());

				if (object.getBoolean("Isvalid")
						&& object.getString("messages").equalsIgnoreCase(
								"success")) {
					String s = object.toString();
					return s;

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
				Intent i = new Intent(FlightPaxActivity.this,
						FlightPaymentActivity.class);
				i.putExtra("json", result);
				i.putExtra("passengerDetails", JSonPassengerDetails);
				startActivity(i);
			}
			super.onPostExecute(result);
		}

	}

	private void loadAssets() {
		// TODO Auto-generated method stub
		am = getAssets();
		String countrylist = null;
		InputStream file1 = null;
		try {
			file1 = am.open("countrylist.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(file1));
			StringBuilder builder1 = new StringBuilder();
			String line1 = null;
			while ((line1 = reader.readLine()) != null) {
				builder1.append(line1);
			}
			countrylist = builder1.toString();
			arrayCountry = new ArrayList<String>();
			if (countrylist != null) {
				JSONObject json1 = new JSONObject(countrylist);
				JSONArray airlinelist = json1.getJSONArray("countrylist");
				JSONObject c1 = null;
				for (int i = 0; i < airlinelist.length(); i++) {
					c1 = airlinelist.getJSONObject(i);
					arrayCountry.add(c1.getString("CountryName") + " - "
							+ c1.getString("CountryCode"));
					// Log.e("CountryName ", arrayCountry.toString());
				}
				airlinelist = null;
			}
			countrylist = null;
			file1.close();
			reader.close();
			builder1 = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class GetPaxDetails extends AsyncTask<Void, Void, String> {

		Boolean blIsloggedIn = false;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			loaderDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			URL url = null;
			try {
				String request = CommonFunctions.main_url
						+ CommonFunctions.lang
						+ "/FlightApi/GetPaxDetails?tripId=" + tripId
						+ "&responseType=ServiceResponse&sID=" + sID;

				url = new URL(request);
				Log.e("url", url.toString());
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");

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
				jsonBaggageList = res;
				System.out.println("res getpaxdetails=" + res);

				conn.disconnect();

				JSONObject jPassList, jObj = new JSONObject(res);
				jObj = jObj.getJSONObject("data");

				if (jObj.getBoolean("Isvalid")) {
					blIsloggedIn = jObj.getBoolean("IsLoggedIn");
					jObj = jObj.getJSONObject("Item");
					JSONArray jArrayBoarding, jArrayPass = jObj
							.getJSONArray("PassengersInfo");
					for (int j = 0; j < jArrayPass.length(); j++) {

						jPassList = jArrayPass.getJSONObject(j);

						jArrayBoarding = jPassList
								.getJSONArray("BoardingDetails");

						switch (jPassList.getString("PassengerType")) {
						case "Adult":
							JSONObject jBoardingObj;
							for (int i = 0; i < jArrayBoarding.length(); i++) {
								jBoardingObj = jArrayBoarding.getJSONObject(i);
								if (jBoardingObj != null) {
									if (jBoardingObj
											.getBoolean("AllowBaggages")) {
										JSONArray BaggageList = jBoardingObj
												.getJSONArray("BaggageList");
										JSONObject BaggageListobject = null;
										arrayAdult
												.add(getResources()
														.getString(
																R.string.checked_baggage_option_1));
										for (int k = 0; k < BaggageList
												.length(); k++) {
											BaggageListobject = BaggageList
													.getJSONObject(k);
											Log.e("Adult ", BaggageListobject
													.toString());
											arrayAdult.add(BaggageListobject
													.getString("Text"));
										}
									}
								}
							}
							break;
						case "Child":
							jBoardingObj = null;
							for (int i = 0; i < jArrayBoarding.length(); i++) {
								jBoardingObj = jArrayBoarding.getJSONObject(i);
								if (jBoardingObj != null) {
									if (jBoardingObj
											.getBoolean("AllowBaggages")) {
										JSONArray BaggageList = jBoardingObj
												.getJSONArray("BaggageList");
										JSONObject BaggageListobject = null;
										arrayChild
												.add(getResources()
														.getString(
																R.string.checked_baggage_option_1));
										for (int k = 0; k < BaggageList
												.length(); k++) {
											BaggageListobject = BaggageList
													.getJSONObject(k);
											Log.e("Child ", BaggageListobject
													.toString());
											arrayChild.add(BaggageListobject
													.getString("Text"));
										}

									}
								}
							}
							break;
						default:
							break;
						}

					}
					jPassList = jArrayPass.getJSONObject(0);
					return jPassList.toString();
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

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (loaderDialog.isShowing())
				loaderDialog.dismiss();

			layoutView();

			if (result != null && blIsloggedIn) {
				try {
					JSONObject jPassList = new JSONObject(result);

					LinearLayout ll_LogHead = (LinearLayout) findViewById(R.id.ll_login_header);
					ll_LogHead.setVisibility(View.GONE);
//					llLoginLayout.setVisibility(View.GONE);

					etEmailAddress.setText(jPassList.getString("Email"));
					EtMobileNo.setText(jPassList.getString("MobileNumber"));

					String strMobCode = jPassList.getString("MobileCode")
							.contains("+") ? jPassList.getString("MobileCode")
							: "+"
									+ jPassList.getString("MobileCode")
											.replace(" ", "");

					spCountrycode
							.setSelection(((ArrayAdapter<String>) spCountrycode
									.getAdapter()).getPosition(strMobCode));

					View view = llDetails.findViewById(1);
					if (view.getTag().toString().equalsIgnoreCase("adult")) {
						((EditText) view.findViewById(R.id.edt_firstname))
								.setText(jPassList.getString("Firstname"));
						((EditText) view.findViewById(R.id.edt_lastname))
								.setText(jPassList.getString("LastName"));

						Spinner Spn;
						if (jPassList.getString("Gender") != null
								&& !jPassList.getString("Gender").equals("")) {
							Spn = (Spinner) view.findViewById(R.id.Spn_title);

							Spn.setSelection(jPassList.getString("Gender")
									.toLowerCase().contains("m") ? 1 : 2);
						}

						Spn = (Spinner) view.findViewById(R.id.spn_nationality);

						for (String listItem : arrayCountry) {
							if (listItem.substring(
									Math.max(listItem.length() - 2, 0))
									.contains(
											jPassList.getString("Citizenship"))) {
								Spn.setSelection(((ArrayAdapter<String>) Spn
										.getAdapter()).getPosition(listItem));
								break;
							}
						}

						// Spn = (Spinner) view
						// .findViewById(R.id.spn_passport_issued);
						//
						// for (String listItem : arrayCountry) {
						// if (listItem.substring(Math.max(listItem.length() -
						// 2,
						// 0)).contains(jPassList
						// .getString("PassportPlaceOfIssue"))) {
						// Spn.setSelection(((ArrayAdapter<String>) Spn
						// .getAdapter()).getPosition(listItem));
						// break;
						// }
						// }

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			super.onPostExecute(result);
		}

	}

	private String convertStreamToString(InputStream is) {		
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
	

}
