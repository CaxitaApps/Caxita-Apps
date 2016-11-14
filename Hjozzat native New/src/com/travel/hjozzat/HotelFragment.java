package com.travel.hjozzat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.travel.hjozzat.R;
import com.travel.hjozzat.model.Room;
import com.hjozzat.support.CommonFunctions;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class HotelFragment extends Fragment implements View.OnClickListener {

	TextView tvCity, tvHeader, tvNoResult;
	TextView tvCheckinDate, tvCheckoutDate;
	LinearLayout llCheckinDate, llCheckoutDate;
	LinearLayout llRooms;
	LinearLayout llCityname, llNationality;
	Spinner spnNationality;

	ImageView btnPlus, btnMinus;

	TextView tvRoomCount;
	Button btnSearch;
	ImageButton imgbtnClearNationality, ibClose;
	AutoCompleteTextView actSuggestion;
	String strCity, strNationality;
	String strCheckinDate, strCheckoutDate;
	int roomCount = 1;

	String main_url = null;
	String[] classArray;

	SimpleDateFormat sdfPrint, sdfUrl;
	Date selcheckindate, selCheckoutdate, minCheckout;

	Calendar currday;
	Date nextYearCheckin, nextYearCheckout;
	ArrayList<String> arrayCountry, arrayCity;

	private Dialog dialogDate, dialogSuggestion;
	View rootView;
	AssetManager am;
	CommonFunctions cf;
	Room[] rooms = new Room[5];
	int id = 1;
	Intent inte;

	public HotelFragment() {
		// TODO Auto-generated constructor stub
		rootView = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		cf = new CommonFunctions(getActivity().getApplicationContext());

		rootView = inflater.inflate(R.layout.fragment_hotel, container,
				false);

		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

		am = getActivity().getAssets();
		new backMethod().execute();
		loadAssets();
		initialize();
		setListeners();
		addView();
		return rootView;
	}

	@SuppressWarnings("unchecked")
	private void initialize() {
		// TODO Auto-generated method stub
		llRooms = (LinearLayout) rootView.findViewById(R.id.ll_rooms);
		tvCity = (TextView) rootView.findViewById(R.id.tv_city);
		spnNationality = (Spinner) rootView.findViewById(R.id.spn_nationality);
		tvCheckinDate = (TextView) rootView.findViewById(R.id.tv_check_in_date);
		tvCheckoutDate = (TextView) rootView
				.findViewById(R.id.tv_check_out_date);

		llCheckinDate = (LinearLayout) rootView.findViewById(R.id.ll_check_in);
		llCheckoutDate = (LinearLayout) rootView
				.findViewById(R.id.ll_check_out);
		llCityname = (LinearLayout) rootView.findViewById(R.id.ll_city_name);
		llNationality = (LinearLayout) rootView
				.findViewById(R.id.ll_nationality);
		btnPlus = (ImageView) rootView.findViewById(R.id.iv_room_count_plus);
		btnMinus = (ImageView) rootView
				.findViewById(R.id.iv_room_count_minus);
		tvRoomCount = (TextView) rootView.findViewById(R.id.tv_room_count);

		btnSearch = (Button) rootView.findViewById(R.id.btn_search);

		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
				R.layout.tv_spinner, arrayCountry);
		adapter2.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
		spnNationality.setAdapter(adapter2);

		spnNationality.setSelection(((ArrayAdapter<String>) spnNationality
				.getAdapter()).getPosition("KUWAIT - KW"));

		strCity = strNationality = "";
		strCheckinDate = strCheckoutDate = null;

		rooms[0] = new Room();
		
		currday = Calendar.getInstance();
		nextYearCheckin = currday.getTime();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 312);
		nextYearCheckin = cal.getTime();

		sdfUrl = new SimpleDateFormat("dd MMM yyyy", new Locale("en"));
		sdfPrint = new SimpleDateFormat("EEE, dd, MMM", new Locale("en"));

		// to set next day
		currday.add(Calendar.DATE, 1);
		tvCheckinDate.setText(sdfPrint.format(currday.getTime()));
		strCheckinDate = sdfUrl.format(currday.getTime());

		selcheckindate = currday.getTime();

		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 2);
		minCheckout = cal.getTime();
		selCheckoutdate = minCheckout;

		tvCheckoutDate.setText(sdfPrint.format(minCheckout.getTime()));
		strCheckoutDate = sdfUrl.format(minCheckout.getTime());

		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 313);
		nextYearCheckout = cal.getTime();

		dialogSuggestion = new Dialog(getActivity(),
				android.R.style.Theme_Translucent);
		dialogSuggestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSuggestion.getWindow().setGravity(Gravity.TOP);
		dialogSuggestion.setContentView(R.layout.dialog_suggestion);
		dialogSuggestion.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
		tvHeader = (TextView) dialogSuggestion.findViewById(R.id.tv_header);
		tvNoResult = (TextView) dialogSuggestion.findViewById(R.id.tv_no_match);
		ibClose = (ImageButton) dialogSuggestion.findViewById(R.id.ib_close);
		actSuggestion = (AutoCompleteTextView) dialogSuggestion
				.findViewById(R.id.act_view);

		dialogDate = new Dialog(getActivity(),
				android.R.style.Theme_Translucent);
		dialogDate.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogDate.getWindow().setGravity(Gravity.TOP);
		dialogDate.setContentView(R.layout.dialog_date_picker);

		strNationality = "KW";

	}

	public void setListeners() {
		// TODO Auto-generated method stub
		llCheckinDate.setOnClickListener(this);
		llCheckoutDate.setOnClickListener(this);
		llCityname.setOnClickListener(this);
		llNationality.setOnClickListener(this);
		btnSearch.setOnClickListener(this);

		btnPlus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btnMinus.setClickable(true);
				if (roomCount >= 5) {
					btnPlus.setClickable(false);
				} else {
					btnPlus.setClickable(true);
					++roomCount;

					addView();

				}
				tvRoomCount.setText(String.valueOf(roomCount));
				// showRooms();

			}
		});
		btnMinus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btnPlus.setClickable(true);
				if (roomCount <= 1) {
					btnMinus.setClickable(false);
				} else {
					btnMinus.setClickable(true);
					--roomCount;

					llRooms.removeViewAt(llRooms.getChildCount() - 1);
				}
				tvRoomCount.setText(String.valueOf(roomCount));

			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ll_check_in:
			showCheckinDialog();
			break;
		case R.id.ll_check_out:
			showCheckoutDialog();
			break;
		case R.id.ll_city_name:
			showCityDialog();
			break;

		case R.id.btn_search:
			searchHotel();
			break;

		}
	}

	public void showCheckinDialog() {
		final CalendarPickerView calendar = (CalendarPickerView) dialogDate
				.findViewById(R.id.calendar_view);
		calendar.init(currday.getTime(), nextYearCheckin)
				.inMode(SelectionMode.SINGLE).withSelectedDate(selcheckindate);
		((TextView) dialogDate.findViewById(R.id.header))
				.setText(getResources().getString(R.string.checkin_date));

		OnDateSelectedListener ondate = new OnDateSelectedListener() {

			@Override
			public void onDateUnselected(Date date) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDateSelected(Date date) {
				selcheckindate = calendar.getSelectedDate();

				tvCheckinDate.setText(sdfPrint.format(date.getTime()));
				strCheckinDate = sdfUrl.format(date.getTime());

				Calendar cal = Calendar.getInstance();
				cal.setTime(calendar.getSelectedDate());
				cal.add(Calendar.DAY_OF_YEAR, 1);
				minCheckout = cal.getTime();

				tvCheckoutDate.setText(sdfPrint.format(minCheckout.getTime()));
				strCheckoutDate = sdfUrl.format(minCheckout.getTime());
				selCheckoutdate = minCheckout;
				dialogDate.dismiss();

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						showCheckoutDialog();
					}
				}, 250);

			}
		};

		calendar.setOnDateSelectedListener(ondate);

		dialogDate.show();
		llCheckinDate.setEnabled(false);
		dialogDate.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				llCheckinDate.setEnabled(true);
			}
		});

	}

	ArrayList<Date> dates = new ArrayList<Date>();

	public void showCheckoutDialog() {
		dates.clear();
		dates.add(selcheckindate);
		dates.add(selCheckoutdate);
		final CalendarPickerView calendar = (CalendarPickerView) dialogDate
				.findViewById(R.id.calendar_view);
		calendar.init(selcheckindate, nextYearCheckout)
				.inMode(SelectionMode.MULTIPLE).withSelectedDates(dates);
		((TextView) dialogDate.findViewById(R.id.header))
				.setText(getResources().getString(R.string.checkout_date));

		OnDateSelectedListener ondate = new OnDateSelectedListener() {

			@Override
			public void onDateUnselected(Date date) {
				// TODO Auto-generated method stub
				calendar.init(selcheckindate, nextYearCheckout)
						.inMode(SelectionMode.MULTIPLE)
						.withSelectedDates(dates);
				List<Date> dts = new ArrayList<Date>();
				dts = calendar.getSelectedDates();
				if (dts.get(1).compareTo(date) == 0) {
					selCheckoutdate = date;

					tvCheckoutDate.setText(sdfPrint.format(date.getTime()));
					strCheckoutDate = sdfUrl.format(date.getTime());

					dialogDate.dismiss();
				}
			}

			@Override
			public void onDateSelected(Date date) {
				selCheckoutdate = date;

				tvCheckoutDate.setText(sdfPrint.format(date.getTime()));
				strCheckoutDate = sdfUrl.format(date.getTime());

				dialogDate.dismiss();
			}
		};

		calendar.setOnDateSelectedListener(ondate);

		dialogDate.show();
		llCheckoutDate.setEnabled(false);
		dialogDate.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				llCheckoutDate.setEnabled(true);
			}
		});

	}

	public void addView() {

		final ImageView ivAdultPlus, ivAdultMinus, ivChildPlus, ivChildMinus;
		final TextView tvAdultCount, tvChildCount, tvGuestRoomCount;

		final Spinner spnChildAge_1, spnChildAge_2, spnChildAge_3;
		final LinearLayout ll_Child_Age1, ll_Child_Age2, ll_Child_Age3;

		final View view = getActivity().getLayoutInflater().inflate(
				R.layout.item_hotel_rooms, null);

		view.setId(id);
		id++;

		tvAdultCount = (TextView) view.findViewById(R.id.tv_adult_count);
		tvChildCount = (TextView) view.findViewById(R.id.tv_child_count);
		tvGuestRoomCount = (TextView) view
				.findViewById(R.id.txt_guestroomcount);

		tvGuestRoomCount.setText(getString(R.string.room)
				+ String.valueOf(roomCount) + " : Guests");

		spnChildAge_1 = (Spinner) view.findViewById(R.id.sp_age_one);
		spnChildAge_2 = (Spinner) view.findViewById(R.id.sp_age_two);
		spnChildAge_3 = (Spinner) view.findViewById(R.id.sp_age_three);

		ll_Child_Age1 = (LinearLayout) view.findViewById(R.id.ll_age_one);
		ll_Child_Age2 = (LinearLayout) view.findViewById(R.id.ll_age_two);
		ll_Child_Age3 = (LinearLayout) view.findViewById(R.id.ll_age_three);

		classArray = new String[] { "1", "2", "3", "4", "5", "6", "7", "8",
				"9", "10", "11" };
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.tvspinner, classArray);

		adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
		
		spnChildAge_1.setAdapter(adapter);

		spnChildAge_2.setAdapter(adapter);

		spnChildAge_3.setAdapter(adapter);

		ivChildPlus = (ImageView) view
				.findViewById(R.id.iv_child_count_plus);
		ivChildMinus = (ImageView) view
				.findViewById(R.id.iv_child_count_minus);

		ivAdultPlus = (ImageView) view
				.findViewById(R.id.iv_adult_count_plus);
		ivAdultMinus = (ImageView) view
				.findViewById(R.id.iv_adult_count_minus);
		ivAdultPlus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ivAdultMinus.setClickable(true);
				int adultcount = Integer.parseInt(tvAdultCount.getText()
						.toString());
				if (adultcount >= 5) {
					ivAdultPlus.setClickable(false);
				} else {
					++adultcount;
					ivAdultPlus.setClickable(true);

				}
				tvAdultCount.setText(String.valueOf(adultcount));
			}
		});
		ivAdultMinus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int adultcount = Integer.parseInt(tvAdultCount.getText()
						.toString());
				ivAdultPlus.setClickable(true);
				if (adultcount <= 1) {
					ivAdultMinus.setClickable(false);
				} else {
					ivAdultMinus.setClickable(true);
					--adultcount;
				}
				tvAdultCount.setText(String.valueOf(adultcount));
			}
		});

		ivChildPlus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int childcount = Integer.parseInt(tvChildCount.getText()
						.toString());
				ivChildMinus.setClickable(true);
				if (childcount >= 3) {
					ivChildPlus.setClickable(false);
				} else {
					++childcount;
					ivChildPlus.setClickable(true);
					if (childcount == 1)

						ll_Child_Age1.setVisibility(View.VISIBLE);
					if (childcount == 2)
						ll_Child_Age2.setVisibility(View.VISIBLE);
					if (childcount == 3)
						ll_Child_Age3.setVisibility(View.VISIBLE);
				}
				tvChildCount.setText(String.valueOf(childcount));
			}
		});
		ivChildMinus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ivChildPlus.setClickable(true);
				int childcount = Integer.parseInt(tvChildCount.getText()
						.toString());
				if (childcount <= 0) {
					ivChildMinus.setClickable(false);
				} else {
					ivChildMinus.setClickable(true);
					--childcount;
					if (childcount == 0) {
						ll_Child_Age1.setVisibility(View.INVISIBLE);
					}
					if (childcount == 1) {

						ll_Child_Age2.setVisibility(View.INVISIBLE);
					}
					if (childcount == 2) {
						ll_Child_Age3.setVisibility(View.INVISIBLE);
					}
				}
				tvChildCount.setText(String.valueOf(childcount));
			}
		});

		llRooms.addView(view);

	}

	public void showCityDialog() {
		tvHeader.setText(R.string.cityorhotel);
		tvNoResult.setVisibility(View.GONE);
		actSuggestion.setText(null);
		actSuggestion.setHint(R.string.search_city_hotel);
		final InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		OnItemClickListener onitem = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

				String resource[], temp = null;
				temp = actSuggestion.getText().toString();
				if (CommonFunctions.lang.equalsIgnoreCase("ar")) {
					resource = temp.split(" ~ ");
					temp = resource[0];
					strCity = resource[1];
					resource = strCity.split(" - ");
					strCity = resource[0];
					resource = temp.split(" - ");
					temp = resource[0];
					tvCity.setText(temp);
				} else {
					resource = temp.split(" - ");
					temp = resource[0];
					strCity = temp;
					tvCity.setText(strCity);
				}
				dialogSuggestion.dismiss();
			}
		};
		actSuggestion.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String filter = actSuggestion.getText().toString()
						.toLowerCase();
				ArrayList<String> listItems = new ArrayList<String>();
				for (String listItem : arrayCity) {
					if (listItem.toLowerCase().contains(filter)) {
						listItems.add(listItem);
					}
				}
				if (listItems.size() == 0) {
					if (tvNoResult.getVisibility() == View.GONE)
						tvNoResult.setVisibility(View.VISIBLE);
				} else {
					if (tvNoResult.getVisibility() == View.VISIBLE)
						tvNoResult.setVisibility(View.GONE);
				}
				ArrayAdapter<String> adapt = new ArrayAdapter<String>(
						getActivity(), R.layout.tv_autocomplete, listItems);
				actSuggestion.setAdapter(adapt);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		ibClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				dialogSuggestion.dismiss();
			}
		});

		actSuggestion.setOnItemClickListener(onitem);
		dialogSuggestion.show();
	}

	private void searchHotel() {
		if (cf.isConnectingToInternet()) {
			TextView tvAdultCount, tvChildCount;
			Spinner spChildAge1, spChildAge2, spChildAge3;
			strNationality = spnNationality.getSelectedItem().toString();
			strNationality = strNationality.substring(Math.max(
					strNationality.length() - 2, 0));
			System.out.println(strNationality);
			if (!strCity.equals("")) {
				if (!strNationality.equals("")) {
					int passengers = 0;
					strCity = strCity.replace(" / ", "%20");
					strCity = strCity.replace("/", "%20");
					strCity = strCity.replace(" ", "%20");
					String strPassenger = "", url = "";
					int RoomCount = Integer.parseInt(tvRoomCount.getText()
							.toString());
					System.out
							.println("Room count" + String.valueOf(RoomCount));

					for (int i = 0; i < RoomCount; ++i) {

						View view = llRooms.getChildAt(i);
						tvAdultCount = (TextView) view
								.findViewById(R.id.tv_adult_count);

						int adultCount = Integer.parseInt(tvAdultCount
								.getText().toString());
						Log.e("adultCount", String.valueOf(adultCount));

						tvChildCount = (TextView) view
								.findViewById(R.id.tv_child_count);
						int ChildCount = Integer.parseInt(tvChildCount
								.getText().toString());
						Log.e("ChildCount", String.valueOf(ChildCount));

						spChildAge1 = (Spinner) view.findViewById(R.id.sp_age_one);
						String CAge1 = spChildAge1.getSelectedItem().toString();
						Log.e("spChildAge1", CAge1);

						spChildAge2 = (Spinner) view.findViewById(R.id.sp_age_two);
						String CAge2 = spChildAge2.getSelectedItem().toString();
						Log.e("spChildAge1", CAge2);

						spChildAge3 = (Spinner) view.findViewById(R.id.sp_age_three);
						String CAge3 = spChildAge3.getSelectedItem().toString();
						Log.e("spChildAge1", CAge3);

						if (i != 0)
							strPassenger = strPassenger + ",";

						if (ChildCount == 0) {
							passengers = passengers + adultCount;
							strPassenger = strPassenger + adultCount + "-"
									+ ChildCount;
						} else if (ChildCount == 1) {
							passengers = passengers + adultCount + ChildCount;
							strPassenger = strPassenger + adultCount + "-"
									+ ChildCount + "-" + CAge1 + "-0-0";
						} else if (ChildCount == 2) {
							passengers = passengers + adultCount + ChildCount;
							strPassenger = strPassenger + adultCount + "-"
									+ ChildCount + "-" + CAge1 + "-" + CAge2
									+ "-0";
						} else {
							passengers = passengers + adultCount + ChildCount;
							strPassenger = strPassenger + adultCount + "-"
									+ ChildCount + "-" + CAge1 + "-" + CAge2
									+ "-" + CAge3;
						}
					}

					url = strCity + "/" + strCheckinDate.replace(" ", "") + "/"
							+ strCheckoutDate.replace(" ", "") + "/" + strNationality + "/"
							+ strPassenger + "/" + CommonFunctions.strCurrency;

					System.out.println("URL " + url);
					System.out.println("city" + tvCity.getText().toString().split(" , ")[0]);
					System.out.println("checkinDate " + strCheckinDate);
					System.out.println("checkoutDate " + strCheckoutDate);
					System.out.println("passengers " + passengers);
					System.out.println("roomCount " + roomCount);

					inte = new Intent(getActivity(), HotelResultActivity.class);
					inte.putExtra("url", url);
					inte.putExtra("city", tvCity.getText().toString().split(" , ")[0]);
					inte.putExtra("checkinDate", strCheckinDate.substring(0, strCheckinDate.length()-5));
					inte.putExtra("checkoutDate", strCheckoutDate.substring(0, strCheckoutDate.length()-5));
					inte.putExtra("passengers", passengers);
					inte.putExtra("roomCount", roomCount);

					if (CommonFunctions.modify) {
						CommonFunctions.modify = false;
						CommonFunctions.HotelResult.clear();
					}

					startActivity(inte);
				} else
					showAlert(getResources().getString(
							R.string.err_msg_select_nation));
			} else
				showAlert(getResources()
						.getString(R.string.err_msg_select_city));
		} else
			noInternetAlert();
	}

	public void showAlert(String errorMsg) {
		String titleMsg = getResources().getString(R.string.error_title);
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		// Setting Dialog Title
		alertDialog.setTitle(titleMsg);

		// Setting Dialog Message
		alertDialog.setMessage(errorMsg);

		// Setting OK Button
		alertDialog.setPositiveButton(getResources().getString(R.string.ok),
				null);

		alertDialog.show();
	}

	public void noInternetAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

		// Setting Dialog Title
		alertDialog.setTitle(getResources().getString(
				R.string.error_no_internet_title));

		// Setting Dialog Message
		alertDialog.setMessage(getResources().getString(
				R.string.error_no_internet_msg));

		// Setting Icon to Dialog

		// Setting OK Button
		alertDialog.setPositiveButton(
				getResources().getString(R.string.error_no_internet_settings),
				new AlertDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// intent to move mobile settings
						getActivity().startActivity(
								new Intent(Settings.ACTION_WIRELESS_SETTINGS));
					}
				});
		alertDialog.setNegativeButton(
				getResources().getString(R.string.error_no_internet_cancel),
				null);

		// Showing Alert Message
		alertDialog.show();
	}

	private void loadAssets() {
		// TODO Auto-generated method stub

		String citylist = null, countrylist = null;
		InputStream file = null, file1 = null;
		try {
			if (CommonFunctions.lang.equalsIgnoreCase("en"))
				file = am.open("citylist.txt");
			else
				file = am.open("citylist_ar.txt");
			file1 = am.open("countrylist.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Log.e("Testing", "Starting to read");
		BufferedReader reader = null, reader1 = null;
		try {
			reader = new BufferedReader(new InputStreamReader(file));
			reader1 = new BufferedReader(new InputStreamReader(file1));

			StringBuilder builder = new StringBuilder();
			StringBuilder builder1 = new StringBuilder();

			String line = null, line1 = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			while ((line1 = reader1.readLine()) != null) {
				builder1.append(line1);
			}

			citylist = builder.toString();
			countrylist = builder1.toString();

			arrayCity = new ArrayList<String>();
			arrayCountry = new ArrayList<String>();

			if (citylist != null) {
				JSONArray jsonArray = new JSONArray(citylist);

				for (int i = 0; i < jsonArray.length(); i++) {
					arrayCity.add(jsonArray.getString(i));
				}
			}
			if (countrylist != null) {
				JSONObject json1 = new JSONObject(countrylist);
				JSONArray airlinelist = json1.getJSONArray("countrylist");
				JSONObject c1 = null;
				for (int i = 0; i < airlinelist.length(); i++) {
					c1 = airlinelist.getJSONObject(i);
					arrayCountry.add(c1.getString("CountryName") + " - "
							+ c1.getString("CountryCode"));
				}
				airlinelist = null;
			}

			citylist = null;
			countrylist = null;
			file.close();
			file1.close();
			reader.close();
			reader1.close();
			builder = null;
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

	public class backMethod extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			loadAssets();
			return null;
		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		rooms[0] = null;
		rooms[1] = null;
		rooms[2] = null;
		rooms[3] = null;
		rooms[4] = null;
		arrayCity = null;
		arrayCountry = null;
		super.onDestroyView();
	}

}
