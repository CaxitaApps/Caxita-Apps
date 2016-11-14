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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class HotelPaxActivity extends Activity {

	private Locale myLocale;
	TextView txt_continewithoutlogin, txt_Email, txt_countrycode, txt_mobileno;
	String jsonvalue, strRequestUrl, strRoomCom, strProceedUrl = null;
	JSONObject response;
	LinearLayout llHotelDetails = null;
	int room = 1, TotalAdultCount = 0, TotalChildCount = 0;
	String[] Countrycode;
	Spinner spCountrycode;
	String strCtryCode, sID;
	String JSonHotelDetails = null;
	TextView tvCheckin, tvCheckout, tvPassengers, tvRoomcount, tvPrice,
			tvHotelname, tvHotelAddr;
	String strCheckin, strCheckout, strPassengers, strRoomCount, strPrice,
			strImgUrl, strNights, strHotelName, strHotelAddress;
	Dialog loaderDialog;

	LinearLayout llLoginLayout;
	EditText etEmailAddress, EtMobileNo, ll_layoutetEmailAddress,
			ll_layoutetPassword;

	String[] arrTitle = { "Title", "Male", "Female" };

	public static Activity activityHotelPax; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		activityHotelPax = this;
		
		getActionBar().hide();
		loadLocale();
		setContentView(R.layout.activity_hotel_paxpage);

		initilize();
		parseJson();
		setSummary();
		new GetPaxDetails().execute();
	}

	private void setSummary() {
		// TODO Auto-generated method stub
		ImageView ivRoomLogo = (ImageView) findViewById(R.id.iv_hotel_logo);
		Bitmap bmp = HotelResultActivity.getBitmapFromMemCache(strImgUrl);

		if (bmp == null)
			ivRoomLogo.setImageResource(R.drawable.ic_no_image);
		else
			ivRoomLogo.setImageBitmap(bmp);

		((TextView) findViewById(R.id.tv_hotel_name)).setText(getIntent()
				.getExtras().getString("strHotelName", null));
		((TextView) findViewById(R.id.tv_hotel_address)).setText(getIntent()
				.getExtras().getString("strHotelAddress", null));
		tvCheckin.setText(strCheckin);
		tvCheckout.setText(strCheckout);
		tvPassengers.setText(strPassengers);
		tvRoomcount.setText(strRoomCount);
		tvPrice.setText(CommonFunctions.strCurrency + " " + strPrice);
	}

	@SuppressWarnings("unchecked")
	public void initilize() {

		loaderDialog = new Dialog(HotelPaxActivity.this,
				android.R.style.Theme_Translucent);
		loaderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loaderDialog.getWindow().setGravity(Gravity.TOP);
		loaderDialog.setContentView(R.layout.dialog_loader);
		loaderDialog.setCancelable(false);

		((ImageView) loaderDialog.findViewById(R.id.iv_loader))
				.setImageResource(R.drawable.hotel_loader);

		Double temp;
		strRequestUrl = getIntent().getExtras().getString("request_url", null);
		temp = getIntent().getExtras().getDouble("total_price");
		strPrice = String.format(new Locale("en"), "%.3f", temp);
		strImgUrl = getIntent().getExtras().getString("strImgUrl", null);
		jsonvalue = getIntent().getExtras().getString("jRoomTypeDetails", null);
		strRoomCom = getIntent().getExtras().getString("roomCombination", null);
		strNights = getIntent().getExtras().getString("NightCount", null);
		strHotelName = getIntent().getExtras().getString("strHotelName", null);
		strHotelAddress = getIntent().getExtras().getString("strHotelAddress",
				null);

		sID = strRequestUrl.substring(Math.max(strRequestUrl.length() - 11, 0));

		txt_continewithoutlogin = (TextView) findViewById(R.id.txt_contine);
		txt_countrycode = (TextView) findViewById(R.id.txt_ccode);
		txt_mobileno = (TextView) findViewById(R.id.txt_mobileno);
		txt_Email = (TextView) findViewById(R.id.txt_email);
		etEmailAddress = (EditText) findViewById(R.id.et_emailid);
		EtMobileNo = (EditText) findViewById(R.id.et_mobilnos);

		llLoginLayout = (LinearLayout) findViewById(R.id.ll_loginlayout);
		ll_layoutetEmailAddress = (EditText) findViewById(R.id.edt_email);
		ll_layoutetPassword = (EditText) findViewById(R.id.edt_password);

		tvCheckin = (TextView) findViewById(R.id.tv_check_in_date);
		tvCheckout = (TextView) findViewById(R.id.tv_check_out_date);
		tvPassengers = (TextView) findViewById(R.id.tv_passengers);
		tvRoomcount = (TextView) findViewById(R.id.tv_room_count);
		tvPrice = (TextView) findViewById(R.id.tv_hotel_price);
		tvHotelname = (TextView) findViewById(R.id.tv_hotel_name);
		tvHotelAddr = (TextView) findViewById(R.id.tv_hotel_address);

		tvHotelname.setText(strHotelName);
		tvHotelAddr.setText(strHotelAddress);

		strCheckin = HotelResultActivity.strCheckinDate;
		strCheckout = HotelResultActivity.strCheckoutDate;

		SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy");

		Date date;
		try {
			date = inputFormat.parse(strCheckin);
			strCheckin = outputFormat.format(date);

			date = inputFormat.parse(strCheckout);
			strCheckout = outputFormat.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		llLoginLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (llLoginLayout.getVisibility() == View.VISIBLE) {
					// Its visible
					llLoginLayout.setVisibility(View.GONE);
				} else {
					// Either gone or invisible
					llLoginLayout.setVisibility(View.VISIBLE);
				}
			}
		});
		TextView check_in = (TextView) findViewById(R.id.txt_checkin);
		check_in.setText(strCheckin);
		TextView check_out = (TextView) findViewById(R.id.txt_checkout);
		check_out.setText(strCheckout);

		TextView night = (TextView) findViewById(R.id.txt_night);
		night.setText(strNights);

		TextView pax = (TextView) findViewById(R.id.txt_pax);
		int Paxcount = 0;
		JSONArray items;
		try {
			items = new JSONArray(jsonvalue);
			int len = items.length();
			for (int j = 0; j < len; j++) {
				JSONObject object = items.getJSONObject(j);
				int AdultCountI = object.getInt("AdultCount");
				int ChildCountI = object.getInt("ChildCount");
				Log.e("AdultCount  ", AdultCountI + "" + ChildCountI);
				Paxcount = AdultCountI + ChildCountI;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		pax.setText(String.valueOf(Paxcount));

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
				if (source != null
						&& blockCharacterSet1.contains(("" + source))) {
					return "";
				}
				return null;
			}
		};
		EtMobileNo.setFilters(new InputFilter[] { filter });

		String text = "<font color=#000000>Or</font><font color=#0072bc> Continue without Login</font>";
		txt_continewithoutlogin.setText(Html.fromHtml(text));

		String textemail = "<font color=#000000>Email ID</font><font color=#e32c18> *</font>";
		txt_Email.setText(Html.fromHtml(textemail));

		String textccode = "<font color=#000000>Country Code</font><font color=#e32c18> *</font>";
		txt_countrycode.setText(Html.fromHtml(textccode));

		String textmobileno = "<font color=#000000>Mobile Number</font><font color=#e32c18> *</font>";
		txt_mobileno.setText(Html.fromHtml(textmobileno));

		if (!CommonFunctions.lang.equals("en")) {
			String textar = "<font color=#000000>أو </font><font color=#0072bc> الاستمرار من دون تسجيل دخول</font>";

			txt_continewithoutlogin.setText(Html.fromHtml(textar));

			String textemailar = "<font color=#000000>عنوان البريد الالكتروني</font><font color=#e32c18> *</font>";
			txt_Email.setText(Html.fromHtml(textemailar));

			String textccodear = "<font color=#000000>رقم البلد</font><font color=#e32c18> *</font>";
			txt_countrycode.setText(Html.fromHtml(textccodear));

			String textmobilenoar = "<font color=#000000>رقم المحمول</font><font color=#e32c18> *</font>";
			txt_mobileno.setText(Html.fromHtml(textmobilenoar));
		}
		spCountrycode = (Spinner) findViewById(R.id.spn_countrycode);
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

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				HotelPaxActivity.this, R.layout.tv_spinner, Countrycode);
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		spCountrycode.setAdapter(adapter);
		spCountrycode
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						strCtryCode = spCountrycode.getSelectedItem()
								.toString();
						Log.e("Countrycode", strCtryCode);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});
		spCountrycode.setSelection(((ArrayAdapter<String>) spCountrycode
				.getAdapter()).getPosition("+965"));
	}

	public void clicker(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;

		case R.id.btn_pay:
			submit();
			break;

		case R.id.txt_forgotpassword:
			Intent forgot = new Intent(HotelPaxActivity.this,
					LoginActivity.class);
			forgot.putExtra("from_pax", true);
			startActivity(forgot);
			break;

		case R.id.tv_signup:
			Intent register = new Intent(HotelPaxActivity.this,
					RegisterActivity.class);
			register.putExtra("from_pax", true);
			startActivity(register);
			break;

		case R.id.btn_login:
			if (Loginvalidate()) {
				new LoginService().execute();
			}
			break;

		case R.id.btn_pls_login:
			if (llLoginLayout.getVisibility() == View.VISIBLE) {
				// Its visible
				llLoginLayout.setVisibility(View.GONE);
			} else {
				// Either gone or invisible
				llLoginLayout.setVisibility(View.VISIBLE);
			}
			break;

		default:
			break;
		}
	}

	public boolean Loginvalidate() {
		boolean valid = true;

		String email = ll_layoutetEmailAddress.getText().toString();
		String password = ll_layoutetPassword.getText().toString();

		if (email.isEmpty()
				|| !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
						.matches()) {
			ll_layoutetEmailAddress.setError(getResources().getString(R.string.error_invalid_email));
			valid = false;
		} else {
			ll_layoutetEmailAddress.setError(null);
		}

		if (password.isEmpty()) {
			ll_layoutetPassword
					.setError(getResources().getString(R.string.error_invalid_pass));
			valid = false;
		} else {
			ll_layoutetPassword.setError(null);
		}

		return valid;
	}

	public void parseJson() {
		// TODO Auto-generated method stub
		LinearLayout llHeader = null;
		Log.e("items  ", jsonvalue);

		try {

			int tempAdultCount = 0, tempChildCount = 0;

			JSONArray items = new JSONArray(jsonvalue);
			int len = items.length();
			for (int j = 0; j < len; j++) {
				JSONObject object = items.getJSONObject(j);
				String AdultCount = object.getString("AdultCount");
				String ChildCount = object.getString("ChildCount");
				Log.e("AdultCount  ", AdultCount + "" + ChildCount);

				tempAdultCount = tempAdultCount + Integer.valueOf(AdultCount);
				tempChildCount = tempChildCount + Integer.valueOf(ChildCount);
				switch (j) {
				case 0:
					llHeader = (LinearLayout) findViewById(R.id.ll_paxheader1);
					llHotelDetails = (LinearLayout) findViewById(R.id.ll_hotelpax1);
					((TextView) findViewById(R.id.txt_adultcount1))
							.setText(String.valueOf(AdultCount));
					((TextView) findViewById(R.id.txt_childcount1))
							.setText(String.valueOf(ChildCount));
					layoutview(AdultCount, ChildCount, llHotelDetails);

					((TextView) findViewById(R.id.txt_childcount1))
							.setVisibility(Integer.valueOf(ChildCount) == 0 ? View.GONE
									: View.VISIBLE);

					((TextView) findViewById(R.id.tv_child_count1))
							.setVisibility(Integer.valueOf(ChildCount) == 0 ? View.GONE
									: View.VISIBLE);

					break;
				case 1:
					llHeader = (LinearLayout) findViewById(R.id.ll_paxheader2);
					llHeader.setVisibility(View.VISIBLE);
					llHotelDetails = (LinearLayout) findViewById(R.id.ll_hotelpax2);
					((TextView) findViewById(R.id.txt_adultcount2))
							.setText(String.valueOf(AdultCount));
					((TextView) findViewById(R.id.txt_childcount2))
							.setText(String.valueOf(ChildCount));

					((TextView) findViewById(R.id.txt_childcount2))
							.setVisibility(Integer.valueOf(ChildCount) == 0 ? View.GONE
									: View.VISIBLE);

					((TextView) findViewById(R.id.tv_child_count2))
							.setVisibility(Integer.valueOf(ChildCount) == 0 ? View.GONE
									: View.VISIBLE);

					layoutview(AdultCount, ChildCount, llHotelDetails);
					break;
				case 2:
					llHeader = (LinearLayout) findViewById(R.id.ll_paxheader3);
					llHeader.setVisibility(View.VISIBLE);
					llHotelDetails = (LinearLayout) findViewById(R.id.ll_hotelpax3);
					((TextView) findViewById(R.id.txt_adultcount3))
							.setText(String.valueOf(AdultCount));
					((TextView) findViewById(R.id.txt_childcount3))
							.setText(String.valueOf(ChildCount));

					((TextView) findViewById(R.id.txt_childcount3))
							.setVisibility(Integer.valueOf(ChildCount) == 0 ? View.GONE
									: View.VISIBLE);

					((TextView) findViewById(R.id.tv_child_count3))
							.setVisibility(Integer.valueOf(ChildCount) == 0 ? View.GONE
									: View.VISIBLE);

					layoutview(AdultCount, ChildCount, llHotelDetails);
					break;
				case 3:
					llHeader = (LinearLayout) findViewById(R.id.ll_paxheader4);
					llHeader.setVisibility(View.VISIBLE);
					llHotelDetails = (LinearLayout) findViewById(R.id.ll_hotelpax4);
					((TextView) findViewById(R.id.txt_adultcount4))
							.setText(String.valueOf(AdultCount));
					((TextView) findViewById(R.id.txt_childcount4))
							.setText(String.valueOf(ChildCount));

					((TextView) findViewById(R.id.txt_childcount4))
							.setVisibility(Integer.valueOf(ChildCount) == 0 ? View.GONE
									: View.VISIBLE);

					((TextView) findViewById(R.id.tv_child_count4))
							.setVisibility(Integer.valueOf(ChildCount) == 0 ? View.GONE
									: View.VISIBLE);

					layoutview(AdultCount, ChildCount, llHotelDetails);
					break;
				case 4:
					llHeader = (LinearLayout) findViewById(R.id.ll_paxheader5);
					llHeader.setVisibility(View.VISIBLE);
					llHotelDetails = (LinearLayout) findViewById(R.id.ll_hotelpax5);
					((TextView) findViewById(R.id.txt_adultcount5))
							.setText(String.valueOf(AdultCount));
					((TextView) findViewById(R.id.txt_childcount5))
							.setText(String.valueOf(ChildCount));

					((TextView) findViewById(R.id.txt_childcount5))
							.setVisibility(Integer.valueOf(ChildCount) == 0 ? View.GONE
									: View.VISIBLE);

					((TextView) findViewById(R.id.tv_child_count5))
							.setVisibility(Integer.valueOf(ChildCount) == 0 ? View.GONE
									: View.VISIBLE);

					layoutview(AdultCount, ChildCount, llHotelDetails);
					break;
				default:
					break;
				}

			}

			if (tempChildCount != 0)
				strPassengers = tempAdultCount + " "
						+ getString(R.string.adult) + " " + tempChildCount
						+ " " + getString(R.string.children);
			else
				strPassengers = tempAdultCount + " "
						+ getString(R.string.adult);
			strRoomCount = String.valueOf(len);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void layoutview(String adultCount, String childCount,
			LinearLayout HotelllDetails) {
		// TODO Auto-generated method stub

		int Adultcount = Integer.valueOf(adultCount);
		int Childcount = Integer.valueOf(childCount);
		TotalAdultCount += Adultcount;
		TotalChildCount += Childcount;

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				HotelPaxActivity.this, R.array.title_spinner_items,
				R.layout.tv_spinner);
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);

		int id = 1;
		TextView tv;
		String strTemp = null;

		for (int i = 1; i <= Adultcount; i++) {
			final View view = getLayoutInflater().inflate(
					R.layout.hotel_pax_detail_items, null);
			view.setId(id);
			id++;
			view.setTag("Adult");

			((TextView) view.findViewById(R.id.txt_caption))
					.setText(getString(R.string.adult) + " " + i);

			((Spinner) view.findViewById(R.id.Spn_title)).setAdapter(adapter);

			tv = (TextView) view.findViewById(R.id.txt_title);
			strTemp = "<font color=#000000>Title</font><font color=#e32c18> *</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_fname);
			strTemp = "<font color=#000000>First Name</font><font color=#e32c18> *</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_lname);
			strTemp = "<font color=#000000>Last Name</font><font color=#e32c18> *</font>";
			tv.setText(Html.fromHtml(strTemp));
			if (!CommonFunctions.lang.equals("en")) {
				tv = (TextView) view.findViewById(R.id.txt_title);
				strTemp = "<font color=#000000>اللقب</font><font color=#e32c18> *</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_fname);
				strTemp = "<font color=#000000>الاسم الأول </font><font color=#e32c18> *</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_lname);
				strTemp = "<font color=#000000>اسم العائلة</font><font color=#e32c18> *</font>";
				tv.setText(Html.fromHtml(strTemp));
			}
			HotelllDetails.addView(view);
		}
		for (int i = 1; i <= Childcount; i++) {
			final View view = getLayoutInflater().inflate(
					R.layout.hotel_pax_detail_items, null);
			view.setId(id);
			id++;
			view.setTag("Child");

			((TextView) view.findViewById(R.id.txt_caption))
					.setText(getString(R.string.children) + " " + i);

			((Spinner) view.findViewById(R.id.Spn_title)).setAdapter(adapter);

			tv = (TextView) view.findViewById(R.id.txt_title);
			strTemp = "<font color=#000000>Title</font><font color=#e32c18> *</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_fname);
			strTemp = "<font color=#000000>First Name</font><font color=#e32c18> *</font>";
			tv.setText(Html.fromHtml(strTemp));

			tv = (TextView) view.findViewById(R.id.txt_lname);
			strTemp = "<font color=#000000>Last Name</font><font color=#e32c18> *</font>";
			tv.setText(Html.fromHtml(strTemp));
			if (!CommonFunctions.lang.equals("en")) {
				tv = (TextView) view.findViewById(R.id.txt_title);
				strTemp = "<font color=#000000>اللقب</font><font color=#e32c18> *</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_fname);
				strTemp = "<font color=#000000>الاسم الأول </font><font color=#e32c18> *</font>";
				tv.setText(Html.fromHtml(strTemp));

				tv = (TextView) view.findViewById(R.id.txt_lname);
				strTemp = "<font color=#000000>اسم العائلة</font><font color=#e32c18> *</font>";
				tv.setText(Html.fromHtml(strTemp));
			}
			HotelllDetails.addView(view);
		}
	}

	private void submit() {
		String strFirstName, strLastName, strFLName, strPassengerType;
		EditText etFirstName = null, etLastName = null;
		Spinner spGender = null;
		JSONObject obj;
		int flag = 0;
		LinearLayout llHotelDetails = null;
		JSONArray HotelInfo = new JSONArray();
		String strGender;
		ArrayList<String> arrayFLList = new ArrayList<String>();

		try {
			JSONArray items = new JSONArray(jsonvalue);
			int len = items.length(), k = 0, j = 0, count = 0, intGender;
			for (j = 0; j < len; j++) {
				JSONObject object = items.getJSONObject(j);
				int AdultCountI = object.getInt("AdultCount");
				int ChildCountI = object.getInt("ChildCount");
				count = AdultCountI + ChildCountI;
				Log.e("AdultCount  ", AdultCountI + "" + ChildCountI);
				switch (j) {
				case 0:
					llHotelDetails = (LinearLayout) findViewById(R.id.ll_hotelpax1);
					break;
				case 1:
					llHotelDetails = (LinearLayout) findViewById(R.id.ll_hotelpax2);
					break;
				case 2:
					llHotelDetails = (LinearLayout) findViewById(R.id.ll_hotelpax3);
					break;
				case 3:
					llHotelDetails = (LinearLayout) findViewById(R.id.ll_hotelpax4);
					break;
				case 4:
					llHotelDetails = (LinearLayout) findViewById(R.id.ll_hotelpax5);
					break;
				default:
					break;
				}
				for (k = 1; k <= count; k++) {

					View view1 = llHotelDetails.findViewById(k);
					strPassengerType = view1.getTag().toString();

					spGender = (Spinner) view1.findViewById(R.id.Spn_title);
					intGender = spGender.getSelectedItemPosition();
					strGender = arrTitle[intGender];
					Log.e("checking ", strGender);

					etFirstName = (EditText) view1
							.findViewById(R.id.edt_firstname);
					strFirstName = etFirstName.getText().toString();
					etLastName = (EditText) view1
							.findViewById(R.id.edt_lastname);
					strLastName = etLastName.getText().toString();
					strFLName = strFirstName + " " + strLastName;

					String email = etEmailAddress.getText().toString();
					String mobileno = EtMobileNo.getText().toString();

					if (email.isEmpty()
							|| !android.util.Patterns.EMAIL_ADDRESS.matcher(
									email).matches()) {
						etEmailAddress.setError(getResources().getString(
								R.string.error_invalid_email));
						if (flag == 0)
							etEmailAddress.requestFocus();
						flag = 1;
					} else {
						etEmailAddress.setError(null);
					}

					if (mobileno.isEmpty() || mobileno.length() < 5
							|| mobileno.length() > 15) {
						EtMobileNo.setError(getResources().getString(
								R.string.error_invalid_number));
						if (flag == 0)
							EtMobileNo.requestFocus();
						flag = 1;
					} else {
						EtMobileNo.setError(null);
					}

					if (intGender == 0) {
						if (flag == 0) {
							etFirstName.requestFocus();
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.err_title_req),
									Toast.LENGTH_SHORT).show();
						}
						flag = 1;
					}

					if (strFirstName.equals("") || strFirstName.length() < 2
							|| strFirstName.length() > 19) {
						Log.e(" FirstName.equals Checking ",
								String.valueOf(flag));
						if (flag == 0)
							etFirstName.requestFocus();
						flag = 1;
						etFirstName.setError(getResources().getString(
								R.string.error_firstname_length));

					}
					if (strLastName.equals("") || strLastName.length() < 2
							|| strLastName.length() > 21) {
						if (flag == 0)
							etLastName.requestFocus();
						flag = 1;
						etLastName.setError(getResources().getString(
								R.string.error_lastname_length));
						Log.e("LastName.equals Checking ", String.valueOf(flag));
					}
					if (strFLName.length() > 23) {
						if (flag == 0)
							etFirstName.requestFocus();
						flag = 1;
						etLastName.setError(getResources().getString(
								R.string.err_fl_name_length));
					}

					if (!arrayFLList.contains(strFLName)) {
						arrayFLList.add(strFLName);
					} else {
						if (flag == 0)
							etFirstName.requestFocus();
						flag = 1;
						etFirstName.setError(getResources().getString(
								R.string.err_same_fl_name));
						etLastName.setError(getResources().getString(
								R.string.err_same_fl_name));
					}

					if (flag == 0) {
						obj = new JSONObject();
						try {

							if (strPassengerType.equalsIgnoreCase("adult"))
								strGender = strGender.equalsIgnoreCase("male") ? "Mr"
										: "Ms";
							else
								strGender = strGender.equalsIgnoreCase("male") ? "Mstr"
										: "Miss";

							obj.put("FirstName", strFirstName);
							obj.put("MiddleName", "");
							obj.put("LastName", strLastName);
							obj.put("Email", etEmailAddress.getText()
									.toString());
							obj.put("MobileNumber", EtMobileNo.getText()
									.toString());
							obj.put("MobileCode", strCtryCode);
							obj.put("Citizenship", "KW");
							obj.put("Title", strGender);
							obj.put("PassengerType", strPassengerType);
							obj.put("DateOfBirth", "09/16/1990");
							obj.put("Age", 26);
							HotelInfo.put(obj);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			}
			if (flag == 0) {
				JSonHotelDetails = HotelInfo.toString();
				new PaxDetailSubmission().execute();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class PaxDetailSubmission extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			URL url = null;
			try {
				String urlParameters = "paxListString="
						+ URLEncoder.encode(JSonHotelDetails, "UTF-8");

				System.out.print(urlParameters + "\n \n" + JSonHotelDetails
						+ "\n \n \n");

				url = new URL(strProceedUrl);
				Log.e("Proceed url", url.toString());
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

				if (json.getBoolean("IsValid")) {
					return json.getString("RequestPayUrl");
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
			if (result != null) {
				new ProceedPax().execute(result);
			}

			super.onPostExecute(result);
		}

	}

	private class ProceedPax extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			loaderDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			URL url = null;
			try {

				url = new URL(params[0]);

				Log.e("procced pax url", url.toString());

				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				conn.setUseCaches(false);
				conn.setDoInput(true);
				conn.setDoOutput(true);

				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.setAcceptCookie(true);
				String cookie = cookieManager.getCookie(url.toString());

				conn.setRequestProperty("Cookie", cookie);
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

				if (json.getBoolean("IsValid")) {
					return res;
				}

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
				handler.sendEmptyMessage(3);
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
				Intent i = new Intent(HotelPaxActivity.this,
						HotelPaymentActivity.class);
				i.putExtra("img_url", strImgUrl);
				i.putExtra("strHotelName", strHotelName);
				i.putExtra("strHotelAddress", strHotelAddress);
				i.putExtra("strImgUrl", strImgUrl);
				i.putExtra("NightCount", strNights);
				i.putExtra("RoomCount", strRoomCount);
				i.putExtra("Checkin", strCheckin);
				i.putExtra("Checkout", strCheckout);
				i.putExtra("TotalAdultCount", TotalAdultCount);
				i.putExtra("TotalChildCount", TotalChildCount);
				i.putExtra("json", result);
				i.putExtra("passengerJson", JSonHotelDetails);
				startActivity(i);
			}
			super.onPostExecute(result);
		}

	}

	private class GetPaxDetails extends AsyncTask<Void, Void, String> {
		Boolean blIsloggedIn = false;

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
				url = new URL(strRequestUrl);
				Log.e("req url", url.toString());
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);
				conn.setRequestMethod("POST");
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
				System.out.println("res getpaxdetails=" + res);
				conn.disconnect();

				JSONObject json = new JSONObject(res);
				JSONObject jPassList = null;
				if (json.getBoolean("IsValid")) {
					blIsloggedIn = json.getBoolean("IsLoggedIn");
					strProceedUrl = json.getString("ProceedPaxUrl");

					JSONArray jsonItem = json.getJSONArray("pax");

					for (int j = 0; j < jsonItem.length(); j++) {
						jPassList = jsonItem.getJSONObject(j);
					}
					jPassList = jsonItem.getJSONObject(0);
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

			if (result != null && blIsloggedIn) {
				try {
					LinearLayout ll_LogHead = (LinearLayout) findViewById(R.id.ll_login_header);
					ll_LogHead.setVisibility(View.GONE);
					llLoginLayout.setVisibility(View.GONE);
					JSONObject jPassList = new JSONObject(result);

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

					llHotelDetails = (LinearLayout) findViewById(R.id.ll_hotelpax1);
					View view = llHotelDetails.findViewById(1);
					if (view.getTag().toString().equalsIgnoreCase("adult")) {
						((EditText) view.findViewById(R.id.edt_firstname))
								.setText(jPassList.getString("FirstName"));
						((EditText) view.findViewById(R.id.edt_lastname))
								.setText(jPassList.getString("LastName"));

						Spinner Spn = (Spinner) view
								.findViewById(R.id.Spn_title);
						if (jPassList.getString("tittle") != null
								&& !jPassList.getString("tittle").equals("")) {
							Spn.setSelection(jPassList.getString("tittle")
									.equalsIgnoreCase("mr") ? 1 : 2);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			super.onPostExecute(result);
		}

	}

	private class LoginService extends AsyncTask<Void, Void, String> {
		Boolean blIsloggedIn = false;

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
				String urlParameters = "UserName="
						+ URLEncoder.encode(ll_layoutetEmailAddress.getText()
								.toString(), "UTF-8")
						+ "&Password="
						+ URLEncoder.encode(ll_layoutetPassword.getText()
								.toString(), "UTF-8") + "&ReturnPaxDetails="
						+ URLEncoder.encode("true", "UTF-8") + "&TransType="
						+ URLEncoder.encode("3", "UTF-8");

				String request = CommonFunctions.main_url
						+ CommonFunctions.lang + "/MyAccountApi/AppLogIn?sID="
						+ sID;
				url = new URL(request);
				Log.e("req url", url.toString());
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
				JSONObject jPassList = null;
				JSONObject json = new JSONObject(res);

				if (json.getBoolean("IsValid") && json.getBoolean("IsLoggedIn")) {
					blIsloggedIn = json.getBoolean("IsLoggedIn");
					JSONObject jsonpax = json.getJSONObject("pax");
					JSONArray jsonItem = jsonpax.getJSONArray("pax");
					for (int j = 0; j < jsonItem.length(); j++) {
						jPassList = jsonItem.getJSONObject(j);
					}
					jPassList = jsonItem.getJSONObject(0);
					return jPassList.toString();
				} else {
					blIsloggedIn = false;
					return json.getString("LogInMessage");
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
			if (result != null && blIsloggedIn) {
				try {
					LinearLayout ll_LogHead = (LinearLayout) findViewById(R.id.ll_login_header);
					ll_LogHead.setVisibility(View.GONE);
					llLoginLayout.setVisibility(View.GONE);
					JSONObject jPassList = new JSONObject(result);

					SharedPreferences pref = getApplicationContext()
							.getSharedPreferences("MyLoginPref", 0);
					SharedPreferences.Editor logineditor = pref.edit();
					logineditor.putString("Email", ll_layoutetEmailAddress
							.getText().toString());
					logineditor.commit();

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

					llHotelDetails = (LinearLayout) findViewById(R.id.ll_hotelpax1);
					View view = llHotelDetails.findViewById(1);
					if (view.getTag().toString().equalsIgnoreCase("adult")) {
						((EditText) view.findViewById(R.id.edt_firstname))
								.setText(jPassList.getString("FirstName"));
						((EditText) view.findViewById(R.id.edt_lastname))
								.setText(jPassList.getString("LastName"));

						Spinner Spn = (Spinner) view
								.findViewById(R.id.Spn_title);

						if (jPassList.getString("tittle") != null
								&& !jPassList.getString("tittle").equals("")) {
							Spn.setSelection(jPassList.getString("tittle")
									.equalsIgnoreCase("mr") ? 1 : 2);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (result != null && !blIsloggedIn) {
				Toast.makeText(getApplicationContext(), result,
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(),
						"Something went wrong. Please try again later",
						Toast.LENGTH_LONG).show();
			}
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

}
