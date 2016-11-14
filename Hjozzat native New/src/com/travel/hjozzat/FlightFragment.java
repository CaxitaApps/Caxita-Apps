package com.travel.hjozzat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.hjozzat.support.CommonFunctions;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;

import java.util.Date;

import org.json.JSONArray;

import android.text.Editable;
import android.text.TextWatcher;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Color;

@SuppressLint({ "ValidFragment", "Assert", "InflateParams" })
public class FlightFragment extends Fragment {

	CommonFunctions cd;

	SimpleDateFormat sdfPrint, sdfUrl;
	Date seldate, selreturn, mcseldate1, mcseldate2, mcseldate3, mcseldate4;

	String strFromCode = "", strToCode = "";
	String strFromCodeMulti1 = "", strToCodeMulti1 = "";
	String strFromCodeMulti2 = "", strToCodeMulti2 = "";
	String strFromCodeMulti3 = "", strToCodeMulti3 = "";
	String strFromCodeMulti4 = "", strToCodeMulti4 = "";

	String strFromCity = null, strToCity = null;
	String strFromCityMulti1 = null, strToCityMulti1 = null;
	String strFromCityMulti2 = null, strToCityMulti2 = null;
	String strFromCityMulti3 = null, strToCityMulti3 = null;
	String strFromCityMulti4 = null, strToCityMulti4 = null;

	String strDate = "", strReturnDate = "";
	String strDateMulti1 = "", strDateMulti2 = "", strDateMulti3 = "",
			strDateMulti4 = "";

	String triptype = "";
	static String main_url = null;
	String url1, url2;
	String strClass = "Economy", strClassMulti = "Economy";
	String strDirect = "False", strDirectMulti = "False";

	String allAirportFrom = "N", allAirportTo = "N", allAirportDetails;
	String allAirportFromMulti_a = "N", allAirportToMulti_a = "N";
	String allAirportFromMulti_b = "N", allAirportToMulti_b = "N";
	String allAirportFromMulti_c = "N", allAirportToMulti_c = "N";
	String allAirportFromMulti_d = "N", allAirportToMulti_d = "N";

	int intAdultCount = 1, intChildCount = 0, intInfantCount = 0;
	int intAdultCountMulti = 1, intChildCountMulti = 0,
			intInfantCountMulti = 0;
	int i = 0, buttonFlag = -1;

	String[] classArray;
	static ArrayList<String> listItems;
	public ArrayList<String> arrayAirportList, prefferedairlinelist;
	Boolean blDirect = false, blDirectMulti = false, blLoadDefault;

	// RotateAnimation rotateAnim;
	TextView tvOneWayTab, tvRoundTripTab, tvMultiTab;
	TextView tvFromCode, tvToCode;
	TextView tvFromCity, tvToCity;
	TextView tvFromCodeMulti_a, tvToCodeMulti_a;
	TextView tvFromCodeMulti_b, tvToCodeMulti_b;
	TextView tvFromCodeMulti_c, tvToCodeMulti_c;
	TextView tvFromCodeMulti_d, tvToCodeMulti_d;
	TextView tvFromCityMulti_a, tvToCityMulti_a;
	TextView tvFromCityMulti_b, tvToCityMulti_b;
	TextView tvFromCityMulti_c, tvToCityMulti_c;
	TextView tvFromCityMulti_d, tvToCityMulti_d;

	TextView tvDepartDate, tvReturnDate;
	TextView tvDepartDateMulti_a, tvDepartDateMulti_b;
	TextView tvDepartDateMulti_c, tvDepartDateMulti_d;

	TextView tvAdultCount, tvChildCount, tvInfantCount;
	TextView tvAdultCountMulti, tvChildCountMulti, tvInfantCountMulti;
	ImageView ivAdultPlus, ivAdultPlusMulti;
	ImageView ivAdultMinus, ivAdultMinusMulti;
	ImageView ivChildPlus, ivChildPlusMulti;
	ImageView ivChildMinus, ivChildMinusMulti;
	ImageView ivInfantPlus, ivInfantPlusMulti;
	ImageView ivInfantMinus, ivInfantMinusMulti;

	ImageView ivSwap, ivSwapMulti_a, ivSwapMulti_b, ivSwapMulti_c,
			ivSwapMulti_d;
	ImageView ivEconomy, ivBusiness, ivFirstClass;
	ImageView ivEconomyMulti, ivBusinessMulti, ivFirstClassMulti;
	TextView tvEconomy, tvBusiness, tvFirstClass;
	TextView tvEconomyMulti, tvBusinessMulti, tvFirstClassMulti;

	CheckBox cbDirect, cbDirectMulti;

	LinearLayout llTrip3, llTrip4;
	LinearLayout llDepartDate, llReturnDate;
	LinearLayout llDepartDateMulti_a, llDepartDateMulti_b, llDepartDateMulti_c,
			llDepartDateMulti_d;
	LinearLayout llRoundTrip, llMulti;
	LinearLayout llFrom, llTo;
	LinearLayout llFromMulti_a, llToMulti_a;
	LinearLayout llFromMulti_b, llToMulti_b;
	LinearLayout llFromMulti_c, llToMulti_c;
	LinearLayout llFromMulti_d, llToMulti_d;
	LinearLayout llEconomy, llBusiness, llFirst;
	LinearLayout llEconomyMulti, llBusinessMulti, llFirstMulti;
	LinearLayout llDirect, llDirectMulti;

	TextView tvDialogHeader, tvNoMatch;
	ImageButton ibClose;
	AutoCompleteTextView actAirports;

	Calendar nextYear, currday;
	Dialog dialogAirport, dialogDate;

	ImageView btnAddTrip, btnRemoveTrip;
	Button btnSearch, btnSearchMulti;

	View rootView;
	Intent inte;

	SharedPreferences sharedpreferences;

	public FlightFragment() {
		rootView = null;
	}

	// /////////////////////on create///////
	@SuppressLint("DefaultarrayAirportListale")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		sharedpreferences = getActivity().getSharedPreferences(
				"default_values", Context.MODE_PRIVATE);
		cd = new CommonFunctions(getActivity().getApplicationContext());

		rootView = inflater.inflate(R.layout.fragment_flight, container, false);

		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

		btnAddTrip = (ImageView) rootView.findViewById(R.id.btn_add_trip);
		btnRemoveTrip = (ImageView) rootView.findViewById(R.id.btn_remove_trip);
		llTrip3 = (LinearLayout) rootView.findViewById(R.id.l2);
		llTrip4 = (LinearLayout) rootView.findViewById(R.id.l3);

		llRoundTrip = (LinearLayout) rootView.findViewById(R.id.ll_roundtrip);
		llMulti = (LinearLayout) rootView.findViewById(R.id.ll_multicity);

		llRoundTrip.setVisibility(View.GONE);
		llMulti.setVisibility(View.GONE);

		tvOneWayTab = (TextView) rootView.findViewById(R.id.tv_oneway);
		tvRoundTripTab = (TextView) rootView.findViewById(R.id.tv_round);
		tvMultiTab = (TextView) rootView.findViewById(R.id.tv_multi);

		// /////////////////// swaping airports //////////////////
		ivSwap = (ImageView) rootView.findViewById(R.id.iv_switch);
		ivSwapMulti_a = (ImageView) rootView
				.findViewById(R.id.iv_switch_multi_a);
		ivSwapMulti_b = (ImageView) rootView
				.findViewById(R.id.iv_switch_multi_b);
		ivSwapMulti_c = (ImageView) rootView
				.findViewById(R.id.iv_switch_multi_c);
		ivSwapMulti_d = (ImageView) rootView
				.findViewById(R.id.iv_switch_multi_d);

		ivSwap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String temp = strFromCode;
				strFromCode = strToCode;
				strToCode = temp;

				temp = strFromCity;
				strFromCity = strToCity;
				strToCity = temp;

				temp = allAirportFrom;
				allAirportFrom = allAirportTo;
				allAirportTo = temp;

				tvFromCode.setText(strFromCode);
				tvFromCity.setText(strFromCity);
				tvToCode.setText(strToCode);
				tvToCity.setText(strToCity);

				Editor editor = sharedpreferences.edit();
				editor.putString("strFromCode", strFromCode);
				editor.putString("strToCode", strToCode);
				editor.putString("allAirportFrom", allAirportFrom);
				editor.putString("allAirportTo", allAirportTo);
				editor.commit();
			}
		});

		ivSwapMulti_a.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String temp = strFromCodeMulti1;
				strFromCodeMulti1 = strToCodeMulti1;
				strToCodeMulti1 = temp;

				temp = strFromCityMulti1;
				strFromCityMulti1 = strToCityMulti1;
				strToCityMulti1 = temp;

				temp = allAirportFromMulti_a;
				allAirportFromMulti_a = allAirportToMulti_a;
				allAirportToMulti_a = temp;

				tvFromCodeMulti_a.setText(strFromCodeMulti1);
				tvFromCityMulti_a.setText(strFromCityMulti1);
				tvToCodeMulti_a.setText(strToCodeMulti1);
				tvToCityMulti_a.setText(strToCityMulti1);

				Editor editor = sharedpreferences.edit();
				editor.putString("strFromCodeMulti1", strFromCodeMulti1);
				editor.putString("strToCodeMulti1", strToCodeMulti1);
				editor.putString("allAirportFromMulti_a", allAirportFromMulti_a);
				editor.putString("allAirportToMulti_a", allAirportToMulti_a);
				editor.commit();
			}
		});

		ivSwapMulti_b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String temp = strFromCodeMulti2;
				strFromCodeMulti2 = strToCodeMulti2;
				strToCodeMulti2 = temp;

				temp = strFromCityMulti2;
				strFromCityMulti2 = strToCityMulti2;
				strToCityMulti2 = temp;

				temp = allAirportFromMulti_b;
				allAirportFromMulti_b = allAirportToMulti_b;
				allAirportToMulti_b = temp;

				tvFromCodeMulti_b.setText(strFromCodeMulti2);
				tvFromCityMulti_b.setText(strFromCityMulti2);
				tvToCodeMulti_b.setText(strToCodeMulti2);
				tvToCityMulti_b.setText(strToCityMulti2);

				Editor editor = sharedpreferences.edit();
				editor.putString("strFromCodeMulti2", strFromCodeMulti2);
				editor.putString("strToCodeMulti2", strToCodeMulti2);
				editor.putString("allAirportFromMulti_b", allAirportFromMulti_b);
				editor.putString("allAirportToMulti_b", allAirportToMulti_b);
				editor.commit();
			}
		});

		ivSwapMulti_c.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String temp = strFromCodeMulti3;
				strFromCodeMulti3 = strToCodeMulti3;
				strToCodeMulti3 = temp;

				temp = strFromCityMulti3;
				strFromCityMulti3 = strToCityMulti3;
				strToCityMulti3 = temp;

				temp = allAirportFromMulti_c;
				allAirportFromMulti_c = allAirportToMulti_c;
				allAirportToMulti_c = temp;

				tvFromCodeMulti_c.setText(strFromCodeMulti3);
				tvFromCityMulti_c.setText(strFromCityMulti3);
				tvToCodeMulti_c.setText(strToCodeMulti3);
				tvToCityMulti_c.setText(strToCityMulti3);

				Editor editor = sharedpreferences.edit();
				editor.putString("strFromCodeMulti3", strFromCodeMulti3);
				editor.putString("strToCodeMulti3", strToCodeMulti3);
				editor.putString("allAirportFromMulti_c", allAirportFromMulti_c);
				editor.putString("allAirportToMulti_c", allAirportToMulti_c);
				editor.commit();
			}
		});

		ivSwapMulti_d.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String temp = strFromCodeMulti4;
				strFromCodeMulti4 = strToCodeMulti4;
				strToCodeMulti4 = temp;

				temp = strFromCityMulti4;
				strFromCityMulti4 = strToCityMulti4;
				strToCityMulti4 = temp;

				temp = allAirportFromMulti_d;
				allAirportFromMulti_d = allAirportToMulti_d;
				allAirportToMulti_d = temp;

				tvFromCodeMulti_d.setText(strFromCodeMulti4);
				tvFromCityMulti_d.setText(strFromCityMulti4);
				tvToCodeMulti_d.setText(strToCodeMulti4);
				tvToCityMulti_d.setText(strToCityMulti4);

				Editor editor = sharedpreferences.edit();
				editor.putString("strFromCodeMulti4", strFromCodeMulti4);
				editor.putString("strToCodeMulti4", strToCodeMulti4);
				editor.putString("allAirportFromMulti_d", allAirportFromMulti_d);
				editor.putString("allAirportToMulti_d", allAirportToMulti_d);
				editor.commit();
			}
		});

		OnClickListener tabs = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.tv_oneway:

					if (buttonFlag != 1) {
						buttonFlag = 1;
						triptype = "OneWay";
						if (!blLoadDefault) {
							loadDefaults();
							llRoundTrip.setVisibility(View.VISIBLE);
							blLoadDefault = true;
						}
						ivSwap.setImageResource(R.drawable.ic_oneway);
						llReturnDate.setVisibility(View.INVISIBLE);
						llMulti.setVisibility(View.GONE);

						tvOneWayTab
								.setBackgroundResource(CommonFunctions.lang.equalsIgnoreCase("en") ? 
										R.drawable.flight_tab_blue_bg_left_curved :
											R.drawable.flight_tab_blue_bg_right_curved);
						tvRoundTripTab.setBackgroundColor(Color.TRANSPARENT);
						tvMultiTab.setBackgroundColor(Color.TRANSPARENT);

						tvOneWayTab.setTextColor(Color.WHITE);
						tvRoundTripTab.setTextColor(Color.BLACK);
						tvMultiTab.setTextColor(Color.BLACK);

					}
					break;
				case R.id.tv_round:
					if (buttonFlag != 0) {
						buttonFlag = 0;
						triptype = "RoundTrip";
						if (!blLoadDefault) {
							loadDefaults();
							llRoundTrip.setVisibility(View.VISIBLE);
							blLoadDefault = true;
						}
						ivSwap.setImageResource(R.drawable.ic_round);
						llReturnDate.setVisibility(View.VISIBLE);
						llMulti.setVisibility(View.GONE);

						tvOneWayTab.setBackgroundColor(Color.TRANSPARENT);
						tvRoundTripTab.setBackgroundColor(Color
								.parseColor("#0072bc"));
						tvMultiTab.setBackgroundColor(Color.TRANSPARENT);

						tvOneWayTab.setTextColor(Color.BLACK);
						tvRoundTripTab.setTextColor(Color.WHITE);
						tvMultiTab.setTextColor(Color.BLACK);
					}
					break;
				case R.id.tv_multi:
					if (buttonFlag != 2) {
						buttonFlag = 2;
						triptype = "Multicity";
						if (blLoadDefault) {
							loadMultiCityDefaults();
							blLoadDefault = false;
						}

						llRoundTrip.setVisibility(View.GONE);
						llMulti.setVisibility(View.VISIBLE);

						tvOneWayTab.setBackgroundColor(Color.TRANSPARENT);
						tvRoundTripTab.setBackgroundColor(Color.TRANSPARENT);
						tvMultiTab
								.setBackgroundResource(CommonFunctions.lang.equalsIgnoreCase("en") ? 
										R.drawable.flight_tab_blue_bg_right_curved :
											R.drawable.flight_tab_blue_bg_left_curved);

						tvOneWayTab.setTextColor(Color.BLACK);
						tvRoundTripTab.setTextColor(Color.BLACK);
						tvMultiTab.setTextColor(Color.WHITE);
					}
					break;

				default:
					break;
				}
			}
		};

		tvOneWayTab.setOnClickListener(tabs);
		tvRoundTripTab.setOnClickListener(tabs);
		tvMultiTab.setOnClickListener(tabs);

		// /////////////////////plus / minus button///////
		btnAddTrip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (i) {
				case 0: {
					llTrip3.setVisibility(View.VISIBLE);
					i++;
					btnRemoveTrip.setVisibility(View.VISIBLE);
					break;
				}
				case 1: {
					llTrip4.setVisibility(View.VISIBLE);
					i++;
					btnAddTrip.setVisibility(View.INVISIBLE);
					break;
				}
				default:
					break;
				}
			}
		});

		btnRemoveTrip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (i) {
				case 2: {
					btnAddTrip.setVisibility(View.VISIBLE);
					llTrip4.setVisibility(View.GONE);
					i--;

					break;
				}
				case 1: {
					llTrip3.setVisibility(View.GONE);
					btnRemoveTrip.setVisibility(View.INVISIBLE);
					i--;

					break;
				}
				default:
					break;
				}
			}
		});

		// /////////////////////end plus / minus button///////

		// /////////////////// from and to listners//////////////////

		llFrom = (LinearLayout) rootView.findViewById(R.id.ll_from);
		llFromMulti_a = (LinearLayout) rootView
				.findViewById(R.id.ll_from_multi_a);
		llFromMulti_b = (LinearLayout) rootView
				.findViewById(R.id.ll_from_multi_b);
		llFromMulti_c = (LinearLayout) rootView
				.findViewById(R.id.ll_from_multi_c);
		llFromMulti_d = (LinearLayout) rootView
				.findViewById(R.id.ll_from_multi_d);

		tvFromCode = (TextView) rootView.findViewById(R.id.tv_from_code);
		tvFromCodeMulti_a = (TextView) rootView
				.findViewById(R.id.tv_from_code_multi_a);
		tvFromCodeMulti_b = (TextView) rootView
				.findViewById(R.id.tv_from_code_multi_b);
		tvFromCodeMulti_c = (TextView) rootView
				.findViewById(R.id.tv_from_code_multi_c);
		tvFromCodeMulti_d = (TextView) rootView
				.findViewById(R.id.tv_from_code_multi_d);

		tvFromCity = (TextView) rootView.findViewById(R.id.tv_from_city);
		tvFromCityMulti_a = (TextView) rootView
				.findViewById(R.id.tv_from_city_multi_a);
		tvFromCityMulti_b = (TextView) rootView
				.findViewById(R.id.tv_from_city_multi_b);
		tvFromCityMulti_c = (TextView) rootView
				.findViewById(R.id.tv_from_city_multi_c);
		tvFromCityMulti_d = (TextView) rootView
				.findViewById(R.id.tv_from_city_multi_d);

		llTo = (LinearLayout) rootView.findViewById(R.id.ll_to);
		llToMulti_a = (LinearLayout) rootView.findViewById(R.id.ll_to_multi_a);
		llToMulti_b = (LinearLayout) rootView.findViewById(R.id.ll_to_multi_b);
		llToMulti_c = (LinearLayout) rootView.findViewById(R.id.ll_to_multi_c);
		llToMulti_d = (LinearLayout) rootView.findViewById(R.id.ll_to_multi_d);

		tvToCode = (TextView) rootView.findViewById(R.id.tv_to_code);
		tvToCodeMulti_a = (TextView) rootView
				.findViewById(R.id.tv_to_code_multi_a);
		tvToCodeMulti_b = (TextView) rootView
				.findViewById(R.id.tv_to_code_multi_b);
		tvToCodeMulti_c = (TextView) rootView
				.findViewById(R.id.tv_to_code_multi_c);
		tvToCodeMulti_d = (TextView) rootView
				.findViewById(R.id.tv_to_code_multi_d);

		tvToCity = (TextView) rootView.findViewById(R.id.tv_to_city);
		tvToCityMulti_a = (TextView) rootView
				.findViewById(R.id.tv_to_city_multi_a);
		tvToCityMulti_b = (TextView) rootView
				.findViewById(R.id.tv_to_city_multi_b);
		tvToCityMulti_c = (TextView) rootView
				.findViewById(R.id.tv_to_city_multi_c);
		tvToCityMulti_d = (TextView) rootView
				.findViewById(R.id.tv_to_city_multi_d);

		dialogAirport = new Dialog(getActivity(),
				android.R.style.Theme_Translucent);
		dialogAirport.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogAirport.getWindow().setGravity(Gravity.TOP);
		dialogAirport.setContentView(R.layout.dialog_suggestion);
		dialogAirport.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

		tvDialogHeader = (TextView) dialogAirport.findViewById(R.id.tv_header);
		actAirports = (AutoCompleteTextView) dialogAirport
				.findViewById(R.id.act_view);
		tvNoMatch = (TextView) dialogAirport.findViewById(R.id.tv_no_match);
		ibClose = (ImageButton) dialogAirport.findViewById(R.id.ib_close);
		actAirports.setHint(R.string.searchfor);

		llFrom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tvDialogHeader.setText(R.string.from);
				tvNoMatch.setVisibility(View.GONE);
				actAirports.setText(null);

				final InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				OnItemClickListener onitem = new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						imm.toggleSoftInput(
								InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						String actvstringfrom = actAirports.getText()
								.toString();

						String resource[];
						// if(CommonFunctions.lang.equalsIgnoreCase("ar")){
						// resource = actvstringfrom.split(" \t ");
						// actvstringfrom = resource[0];
						// }

						strFromCity = actvstringfrom;
						resource = actvstringfrom.split("-");
						strFromCode = resource[3];
						strFromCode = strFromCode.replace(" ", "");

						strFromCity = resource[0];
						
						tvFromCode.setText(strFromCode);
						tvFromCity.setText(strFromCity);

						if (resource[1].toLowerCase().contains("all airport")
								|| resource[1].toLowerCase().contains(
										"جميع المطار"))
							allAirportFrom = "Y";
						else
							allAirportFrom = "N";

						Editor editor = sharedpreferences.edit();
						editor.putString("strFromCode", strFromCode);
						editor.putString("allAirportFrom", allAirportFrom);
						editor.commit();

						dialogAirport.dismiss();

					}
				};
				actAirports.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {

						String filter = s.toString().toLowerCase();
						listItems = new ArrayList<String>();
						for (String listItem : arrayAirportList) {
							if (listItem.toLowerCase().contains(filter)) {
								listItems.add(listItem);
							}

						}
						if (listItems.size() == 0) {
							if (tvNoMatch.getVisibility() == View.GONE)
								tvNoMatch.setVisibility(View.VISIBLE);
						} else {
							if (tvNoMatch.getVisibility() == View.VISIBLE)
								tvNoMatch.setVisibility(View.GONE);
						}
						ArrayAdapter<String> adapt = new ArrayAdapter<String>(
								getActivity(),
								android.R.layout.simple_list_item_1, listItems);
						actAirports.setAdapter(adapt);
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {

					}
				});

				actAirports.setOnItemClickListener(onitem);
				dialogAirport.show();

				ibClose.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						imm.toggleSoftInput(
								InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						dialogAirport.dismiss();
					}
				});

			}
		});

		llFromMulti_a.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tvDialogHeader.setText(R.string.from);
				tvNoMatch.setVisibility(View.GONE);
				actAirports.setText(null);
				final InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				OnItemClickListener onitem = new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						imm.toggleSoftInput(
								InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						String actvstringfrom = actAirports.getText()
								.toString();

						String resource[];
						// if(CommonFunctions.lang.equalsIgnoreCase("ar")){
						// resource = actvstringfrom.split(" ~ ");
						// actvstringfrom = resource[0];
						// }

						strFromCityMulti1 = actvstringfrom;
						resource = actvstringfrom.split("-");
						strFromCodeMulti1 = resource[3];
						strFromCodeMulti1 = strFromCodeMulti1.replace(" ", "");

						strFromCityMulti1 = resource[0];
						
						tvFromCodeMulti_a.setText(strFromCodeMulti1);
						tvFromCityMulti_a.setText(strFromCityMulti1);

						if (resource[1].toLowerCase().contains("all airport")
								|| resource[1].toLowerCase().contains(
										"جميع المطار"))
							allAirportFromMulti_a = "Y";
						else
							allAirportFromMulti_a = "N";

						Editor editor = sharedpreferences.edit();
						editor.putString("strFromCodeMulti1", strFromCodeMulti1); // city
																					// 1
						editor.putString("allAirportFromMulti_a",
								allAirportFromMulti_a);
						editor.commit();

						dialogAirport.dismiss();

					}
				};

				actAirports.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {

						String filter = s.toString().toLowerCase();
						listItems = new ArrayList<String>();
						for (String listItem : arrayAirportList) {
							if (listItem.toLowerCase().contains(filter)) {
								listItems.add(listItem);
							}
						}
						if (listItems.size() == 0) {
							if (tvNoMatch.getVisibility() == View.GONE)
								tvNoMatch.setVisibility(View.VISIBLE);
						} else {
							if (tvNoMatch.getVisibility() == View.VISIBLE)
								tvNoMatch.setVisibility(View.GONE);
						}
						ArrayAdapter<String> adapt = new ArrayAdapter<String>(
								getActivity(),
								android.R.layout.simple_list_item_1, listItems);
						actAirports.setAdapter(adapt);
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {

					}
				});
				actAirports.setOnItemClickListener(onitem);
				dialogAirport.show();

				ibClose.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						imm.toggleSoftInput(
								InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						dialogAirport.dismiss();
					}
				});

			}
		});

		llFromMulti_b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tvDialogHeader.setText(R.string.from);
				tvNoMatch.setVisibility(View.GONE);
				actAirports.setText(null);
				final InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				OnItemClickListener onitem = new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						imm.toggleSoftInput(
								InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						String actvstringfrom = actAirports.getText()
								.toString();

						String resource[];
						// if(CommonFunctions.lang.equalsIgnoreCase("ar")){
						// resource = actvstringfrom.split(" ~ ");
						// actvstringfrom = resource[0];
						// }

						strFromCityMulti2 = actvstringfrom;
						resource = actvstringfrom.split("-");
						strFromCodeMulti2 = resource[3];
						strFromCodeMulti2 = strFromCodeMulti2.replace(" ", "");

						strFromCityMulti2 = resource[0];
						
						tvFromCodeMulti_b.setText(strFromCodeMulti2);
						tvFromCityMulti_b.setText(strFromCityMulti2);

						if (resource[1].toLowerCase().contains("all airport")
								|| resource[1].toLowerCase().contains(
										"جميع المطار"))
							allAirportFromMulti_b = "Y";
						else
							allAirportFromMulti_b = "N";

						Editor editor = sharedpreferences.edit();
						editor.putString("strFromCodeMulti2", strFromCodeMulti2); // city
																					// 2
						editor.putString("allAirportFromMulti_b",
								allAirportFromMulti_b);
						editor.commit();

						dialogAirport.dismiss();
					}
				};

				actAirports.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {

						String filter = s.toString().toLowerCase();
						listItems = new ArrayList<String>();
						for (String listItem : arrayAirportList) {
							if (listItem.toLowerCase().contains(filter)) {
								listItems.add(listItem);
							}
						}

						if (listItems.size() == 0) {
							if (tvNoMatch.getVisibility() == View.GONE)
								tvNoMatch.setVisibility(View.VISIBLE);
						} else {
							if (tvNoMatch.getVisibility() == View.VISIBLE)
								tvNoMatch.setVisibility(View.GONE);
						}
						ArrayAdapter<String> adapt = new ArrayAdapter<String>(
								getActivity(),
								android.R.layout.simple_list_item_1, listItems);
						actAirports.setAdapter(adapt);
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				});
				actAirports.setOnItemClickListener(onitem);
				dialogAirport.show();

				ibClose.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						imm.toggleSoftInput(
								InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						dialogAirport.dismiss();
					}
				});

			}
		});

		llFromMulti_c.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tvDialogHeader.setText(R.string.from);
				tvNoMatch.setVisibility(View.GONE);
				actAirports.setText(null);
				final InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				OnItemClickListener onitem = new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						imm.toggleSoftInput(
								InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						String actvstringfrom = actAirports.getText()
								.toString();

						String resource[];
						// if(CommonFunctions.lang.equalsIgnoreCase("ar")){
						// resource = actvstringfrom.split(" ~ ");
						// actvstringfrom = resource[0];
						// }

						strFromCityMulti3 = actvstringfrom;
						resource = actvstringfrom.split("-");
						strFromCodeMulti3 = resource[3];
						strFromCodeMulti3 = strFromCodeMulti3.replace(" ", "");

						strFromCityMulti3 = resource[0];
						
						tvFromCodeMulti_c.setText(strFromCodeMulti3);
						tvFromCityMulti_c.setText(strFromCityMulti3);

						if (resource[1].toLowerCase().contains("all airport")
								|| resource[1].toLowerCase().contains(
										"جميع المطار"))
							allAirportFromMulti_c = "Y";
						else
							allAirportFromMulti_c = "N";

						Editor editor = sharedpreferences.edit();
						editor.putString("strFromCodeMulti3", strFromCodeMulti3); // city
																					// 3
						editor.putString("allAirportFromMulti_c",
								allAirportFromMulti_c);
						editor.commit();

						dialogAirport.dismiss();

					}
				};

				actAirports.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {

						String filter = s.toString().toLowerCase();
						listItems = new ArrayList<String>();
						for (String listItem : arrayAirportList) {
							if (listItem.toLowerCase().contains(filter)) {
								listItems.add(listItem);
							}
						}
						if (listItems.size() == 0) {
							if (tvNoMatch.getVisibility() == View.GONE)
								tvNoMatch.setVisibility(View.VISIBLE);
						} else {
							if (tvNoMatch.getVisibility() == View.VISIBLE)
								tvNoMatch.setVisibility(View.GONE);
						}

						ArrayAdapter<String> adapt = new ArrayAdapter<String>(
								getActivity(),
								android.R.layout.simple_list_item_1, listItems);
						actAirports.setAdapter(adapt);
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {

					}
				});

				actAirports.setOnItemClickListener(onitem);
				dialogAirport.show();

				ibClose.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						imm.toggleSoftInput(
								InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						dialogAirport.dismiss();
					}
				});

			}
		});

		llFromMulti_d.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tvDialogHeader.setText(R.string.from);
				tvNoMatch.setVisibility(View.GONE);
				actAirports.setText(null);
				final InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				OnItemClickListener onitem = new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						imm.toggleSoftInput(
								InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						String actvstringfrom = actAirports.getText()
								.toString();

						String resource[];
						// if(CommonFunctions.lang.equalsIgnoreCase("ar")){
						// resource = actvstringfrom.split(" ~ ");
						// actvstringfrom = resource[0];
						// }

						strFromCityMulti4 = actvstringfrom;
						resource = actvstringfrom.split("-");
						strFromCodeMulti4 = resource[3];
						strFromCodeMulti4 = strFromCodeMulti4.replace(" ", "");

						strFromCityMulti4 = resource[0];
						
						tvFromCodeMulti_d.setText(strFromCodeMulti4);
						tvFromCityMulti_d.setText(strFromCityMulti4);

						if (resource[1].toLowerCase().contains("all airport")
								|| resource[1].toLowerCase().contains(
										"جميع المطار"))
							allAirportFromMulti_d = "Y";
						else
							allAirportFromMulti_d = "N";

						Editor editor = sharedpreferences.edit();
						editor.putString("strFromCodeMulti4", strFromCodeMulti4); // city
																					// 4
						editor.putString("allAirportFromMulti_d",
								allAirportFromMulti_d);
						editor.commit();

						dialogAirport.dismiss();

					}
				};

				actAirports.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {

						String filter = s.toString().toLowerCase();
						listItems = new ArrayList<String>();
						for (String listItem : arrayAirportList) {
							if (listItem.toLowerCase().contains(filter)) {
								listItems.add(listItem);
							}
						}
						if (listItems.size() == 0) {
							if (tvNoMatch.getVisibility() == View.GONE)
								tvNoMatch.setVisibility(View.VISIBLE);
						} else {
							if (tvNoMatch.getVisibility() == View.VISIBLE)
								tvNoMatch.setVisibility(View.GONE);
						}
						ArrayAdapter<String> adapt = new ArrayAdapter<String>(
								getActivity(),
								android.R.layout.simple_list_item_1, listItems);
						actAirports.setAdapter(adapt);
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {

					}
				});
				actAirports.setOnItemClickListener(onitem);
				dialogAirport.show();

				ibClose.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						imm.toggleSoftInput(
								InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						dialogAirport.dismiss();
					}
				});

			}
		});

		llTo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showToDialog();
			}
		});

		llToMulti_a.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showToDialogMulti_a();
			}
		});

		llToMulti_b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showToDialogMulti_b();
			}
		});

		llToMulti_c.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showToDialogMulti_c();
			}
		});

		llToMulti_d.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showToDialogMulti_d();
			}
		});

		// /////////////////// end of to and from listners//////////////////

		// /////////////////// date//////////////////
		llDepartDate = (LinearLayout) rootView
				.findViewById(R.id.ll_depart_date);
		llReturnDate = (LinearLayout) rootView
				.findViewById(R.id.ll_return_date);
		llDepartDateMulti_a = (LinearLayout) rootView
				.findViewById(R.id.ll_depart_date_multi_a);
		llDepartDateMulti_b = (LinearLayout) rootView
				.findViewById(R.id.ll_depart_date_multi_b);
		llDepartDateMulti_c = (LinearLayout) rootView
				.findViewById(R.id.ll_depart_date_multi_c);
		llDepartDateMulti_d = (LinearLayout) rootView
				.findViewById(R.id.ll_depart_date_multi_d);

		tvDepartDate = (TextView) rootView.findViewById(R.id.tv_depart_date);
		tvReturnDate = (TextView) rootView.findViewById(R.id.tv_return_date);
		tvDepartDateMulti_a = (TextView) rootView
				.findViewById(R.id.tv_depart_date_multi_a);
		tvDepartDateMulti_b = (TextView) rootView
				.findViewById(R.id.tv_depart_date_multi_b);
		tvDepartDateMulti_c = (TextView) rootView
				.findViewById(R.id.tv_depart_date_multi_c);
		tvDepartDateMulti_d = (TextView) rootView
				.findViewById(R.id.tv_depart_date_multi_d);

		currday = Calendar.getInstance();
		nextYear = Calendar.getInstance();
		nextYear.add(Calendar.DATE, 311);

		sdfUrl = new SimpleDateFormat("dd MMM yyyy", new Locale("en"));
		sdfPrint = new SimpleDateFormat("EEE, dd, MMM", new Locale("en"));

		// to set next day
		currday.add(Calendar.DATE, 1);
		tvDepartDate.setText(sdfPrint.format(currday.getTime()));
		tvDepartDateMulti_a.setText(sdfPrint.format(currday.getTime()));
		strDate = sdfUrl.format(currday.getTime());
		strDateMulti1 = sdfUrl.format(currday.getTime());

		seldate = currday.getTime();
		mcseldate1 = currday.getTime();

		// to set two days later
		currday = Calendar.getInstance();
		currday.add(Calendar.DATE, 2);
		tvReturnDate.setText(sdfPrint.format(currday.getTime()));
		tvDepartDateMulti_b.setText(sdfPrint.format(currday.getTime()));
		tvDepartDateMulti_c.setText(sdfPrint.format(currday.getTime()));
		tvDepartDateMulti_d.setText(sdfPrint.format(currday.getTime()));

		strReturnDate = sdfUrl.format(currday.getTime());
		strDateMulti2 = sdfUrl.format(currday.getTime());
		strDateMulti3 = sdfUrl.format(currday.getTime());
		strDateMulti4 = sdfUrl.format(currday.getTime());

		selreturn = currday.getTime();
		mcseldate2 = currday.getTime();
		mcseldate3 = currday.getTime();
		mcseldate4 = currday.getTime();

		currday = Calendar.getInstance();

		// ///////////////////end date//////////////////

		// //////////////////// date listeners ///////////////////////
		dialogDate = new Dialog(getActivity(),
				android.R.style.Theme_Translucent);
		dialogDate.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogDate.getWindow().setGravity(Gravity.TOP);
		dialogDate.setContentView(R.layout.dialog_date_picker);
		// dialogDate.getWindow().getAttributes().windowAnimations =
		// R.style.DialogAnimation;

		// one way and roundtrip dates

		llDepartDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDepartDate();
			}
		});

		llReturnDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showReturnDialog();
			}

		});

		// /////////////////// multiple city date//////////////////
		llDepartDateMulti_a.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDepartDateMulti_a();
			}
		});

		llDepartDateMulti_b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDepartDateMulti_b();
			}
		});

		llDepartDateMulti_c.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDepartDateMulti_c();
			}
		});

		llDepartDateMulti_d.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDepartDateMulti_d();
			}
		});

		// ////////////////passenger details /////////////////
		// ////////////////passenger details /////////////////
		tvAdultCount = (TextView) rootView.findViewById(R.id.tv_adult_count);
		tvChildCount = (TextView) rootView.findViewById(R.id.tv_child_count);
		tvInfantCount = (TextView) rootView.findViewById(R.id.tv_infant_count);
		tvAdultCountMulti = (TextView) rootView
				.findViewById(R.id.tv_adult_count_multi);
		tvChildCountMulti = (TextView) rootView
				.findViewById(R.id.tv_child_count_multi);
		tvInfantCountMulti = (TextView) rootView
				.findViewById(R.id.tv_infant_count_multi);

		// /////////////////// adult//////////////////
		ivAdultMinus = (ImageView) rootView
				.findViewById(R.id.iv_minus_adult_count);
		ivAdultMinusMulti = (ImageView) rootView
				.findViewById(R.id.iv_minus_adult_count_multi);

		ivAdultPlus = (ImageView) rootView
				.findViewById(R.id.iv_plus_adult_count);
		ivAdultPlusMulti = (ImageView) rootView
				.findViewById(R.id.iv_plus_adult_count_multi);

		// ///////////////////child//////////////////
		ivChildMinus = (ImageView) rootView
				.findViewById(R.id.iv_minus_child_count);
		ivChildMinusMulti = (ImageView) rootView
				.findViewById(R.id.iv_minus_child_count_multi);

		ivChildPlus = (ImageView) rootView
				.findViewById(R.id.iv_plus_child_count);
		ivChildPlusMulti = (ImageView) rootView
				.findViewById(R.id.iv_plus_child_count_multi);

		// /////////////////// infant//////////////////
		ivInfantMinus = (ImageView) rootView
				.findViewById(R.id.iv_minus_infant_count);
		ivInfantMinusMulti = (ImageView) rootView
				.findViewById(R.id.iv_minus_infant_count_multi);

		ivInfantPlus = (ImageView) rootView
				.findViewById(R.id.iv_plus_infant_count);
		ivInfantPlusMulti = (ImageView) rootView
				.findViewById(R.id.iv_plus_infant_count_multi);

		// /////////////////// tvAdultMinusOneway//////////////////
		ivAdultMinus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (intAdultCount > 1) {
					intAdultCount--;
					if (intInfantCount > intAdultCount) {
						intInfantCount--;
						tvInfantCount.setText(String.valueOf(intInfantCount));
					}
				}

				Log.d("Adult Count", "" + intAdultCount);
				tvAdultCount.setText(String.valueOf(intAdultCount));
			}
		});

		ivAdultMinusMulti.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (intAdultCountMulti > 1) {
					intAdultCountMulti--;
					if (intInfantCountMulti > intAdultCountMulti) {
						intInfantCountMulti--;
						tvInfantCountMulti.setText(String
								.valueOf(intInfantCountMulti));
					}
				}
				Log.d("Adult Count", "" + intAdultCountMulti);
				tvAdultCountMulti.setText(String.valueOf(intAdultCountMulti));
			}
		});

		// ///////////////////end tvAdultMinusOneway//////////////////

		// /////////////////// tvAdultPlusOneway//////////////////
		ivAdultPlus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (intAdultCount < 9
						&& (intAdultCount + intChildCount + intInfantCount) < 9) {
					intAdultCount++;
				}
				Log.d("Adult Count", "" + intAdultCount);
				tvAdultCount.setText(String.valueOf(intAdultCount));
			}
		});

		ivAdultPlusMulti.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (intAdultCountMulti < 9
						&& (intAdultCountMulti + intChildCountMulti + intInfantCountMulti) < 9) {
					intAdultCountMulti++;
				}
				Log.d("Adult Count", "" + intAdultCountMulti);
				tvAdultCountMulti.setText(String.valueOf(intAdultCountMulti));
			}
		});

		// ///////////////////end tvAdultPlusOneway//////////////////

		// /////////////////// tvChildMinusOneway//////////////////
		ivChildMinus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (intChildCount > 0) {
					intChildCount--;
				}
				tvChildCount.setText(String.valueOf(intChildCount));
			}
		});

		ivChildMinusMulti.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (intChildCountMulti > 0) {
					intChildCountMulti--;
				}
				tvChildCountMulti.setText(String.valueOf(intChildCountMulti));
			}
		});
		// ///////////////////end tvChildMinusOneway//////////////////

		// /////////////////// tvChildPlusOneway//////////////////
		ivChildPlus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (intChildCount < 8
						&& (intAdultCount + intChildCount + intInfantCount) < 9) {
					intChildCount++;
				}
				tvChildCount.setText(String.valueOf(intChildCount));
			}
		});

		ivChildPlusMulti.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (intChildCountMulti < 8
						&& (intAdultCountMulti + intChildCountMulti + intInfantCountMulti) < 9) {
					intChildCountMulti++;
				}
				tvChildCountMulti.setText(String.valueOf(intChildCountMulti));
			}
		});

		// /////////////////// endchildcountplus//////////////////

		// /////////////////// tvInfantMinusOneway//////////////////
		ivInfantMinus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (intInfantCount > 0) {
					intInfantCount--;
				}
				tvInfantCount.setText(String.valueOf(intInfantCount));
			}
		});

		ivInfantMinusMulti.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (intInfantCountMulti > 0) {
					intInfantCountMulti--;
				}
				tvInfantCountMulti.setText(String.valueOf(intInfantCountMulti));
			}
		});
		// ///////////////////end tvInfantMinusOneway//////////////////

		// /////////////////// tvInfantPlusOneway//////////////////
		ivInfantPlus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (intInfantCount < 4
						&& (intAdultCount + intChildCount + intInfantCount) < 9
						&& intInfantCount < intAdultCount) {
					intInfantCount++;
				}

				tvInfantCount.setText(String.valueOf(intInfantCount));
			}
		});

		ivInfantPlusMulti.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (intInfantCountMulti < 4
						&& (intAdultCountMulti + intChildCountMulti + intInfantCountMulti) < 9
						&& intInfantCountMulti < intAdultCountMulti) {
					intInfantCountMulti++;
				}
				tvInfantCountMulti.setText(String.valueOf(intInfantCountMulti));
			}
		});

		// ///////////////////end tvInfantPlusOneway//////////////////

		// /////////////////// Flight Class//////////////////
		ivEconomy = (ImageView) rootView.findViewById(R.id.iv_economy);
		ivBusiness = (ImageView) rootView.findViewById(R.id.iv_business);
		ivFirstClass = (ImageView) rootView.findViewById(R.id.iv_first_class);

		ivEconomyMulti = (ImageView) rootView
				.findViewById(R.id.iv_economy_multi);
		ivBusinessMulti = (ImageView) rootView
				.findViewById(R.id.iv_business_multi);
		ivFirstClassMulti = (ImageView) rootView
				.findViewById(R.id.iv_first_class_multi);

		tvEconomy = (TextView) rootView.findViewById(R.id.tv_economy);
		tvBusiness = (TextView) rootView.findViewById(R.id.tv_business);
		tvFirstClass = (TextView) rootView.findViewById(R.id.tv_first_class);

		tvEconomyMulti = (TextView) rootView
				.findViewById(R.id.tv_economy_multi);
		tvBusinessMulti = (TextView) rootView
				.findViewById(R.id.tv_business_multi);
		tvFirstClassMulti = (TextView) rootView
				.findViewById(R.id.tv_first_class_multi);

		llEconomy = (LinearLayout) rootView.findViewById(R.id.ll_economy);
		llBusiness = (LinearLayout) rootView.findViewById(R.id.ll_business);
		llFirst = (LinearLayout) rootView.findViewById(R.id.ll_first_class);

		llEconomyMulti = (LinearLayout) rootView
				.findViewById(R.id.ll_economy_multi);
		llBusinessMulti = (LinearLayout) rootView
				.findViewById(R.id.ll_business_multi);
		llFirstMulti = (LinearLayout) rootView
				.findViewById(R.id.ll_first_class_multi);

		OnClickListener classListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.ll_economy:
					strClass = "Economy";
					tvEconomy.setTextColor(Color.parseColor("#0072bc"));
					tvBusiness.setTextColor(Color.BLACK);
					tvFirstClass.setTextColor(Color.BLACK);
					
					ivEconomy.setColorFilter(Color.parseColor("#E60072bb"));
					ivBusiness.setColorFilter(Color.BLACK);
					ivFirstClass.setColorFilter(Color.BLACK);
					
					break;

				case R.id.ll_business:
					strClass = "Business Class";
					strClass = strClass.replace(" ", "%20");
					tvEconomy.setTextColor(Color.BLACK);
					tvBusiness.setTextColor(Color.parseColor("#0072bc"));
					tvFirstClass.setTextColor(Color.BLACK);
					
					ivEconomy.setColorFilter(Color.BLACK);
					ivBusiness.setColorFilter(Color.parseColor("#E60072bb"));
					ivFirstClass.setColorFilter(Color.BLACK);
					
					break;

				case R.id.ll_first_class:
					strClass = "First Class";
					strClass = strClass.replace(" ", "%20");
					tvEconomy.setTextColor(Color.BLACK);
					tvBusiness.setTextColor(Color.BLACK);
					tvFirstClass.setTextColor(Color.parseColor("#0072bc"));
					
					ivEconomy.setColorFilter(Color.BLACK);
					ivBusiness.setColorFilter(Color.BLACK);
					ivFirstClass.setColorFilter(Color.parseColor("#E60072bb"));
					
					break;

				case R.id.ll_economy_multi:
					strClassMulti = "Economy";
					tvEconomyMulti.setTextColor(Color.parseColor("#0072bc"));
					tvBusinessMulti.setTextColor(Color.BLACK);
					tvFirstClassMulti.setTextColor(Color.BLACK);
					
					ivEconomyMulti.setColorFilter(Color.parseColor("#E60072bb"));
					ivBusinessMulti.setColorFilter(Color.BLACK);
					ivFirstClassMulti.setColorFilter(Color.BLACK);
					
					break;

				case R.id.ll_business_multi:
					strClassMulti = "Business Class";
					strClassMulti = strClass.replace(" ", "%20");
					tvEconomyMulti.setTextColor(Color.BLACK);
					tvBusinessMulti.setTextColor(Color.parseColor("#0072bc"));
					tvFirstClassMulti.setTextColor(Color.BLACK);
					
					ivEconomyMulti.setColorFilter(Color.BLACK);
					ivBusinessMulti.setColorFilter(Color.parseColor("#E60072bb"));
					ivFirstClassMulti.setColorFilter(Color.BLACK);
					
					break;

				case R.id.ll_first_class_multi:
					strClassMulti = "First Class";
					strClassMulti = strClass.replace(" ", "%20");
					tvEconomyMulti.setTextColor(Color.BLACK);
					tvBusinessMulti.setTextColor(Color.BLACK);
					tvFirstClassMulti.setTextColor(Color.parseColor("#0072bc"));
					
					ivEconomyMulti.setColorFilter(Color.BLACK);
					ivBusinessMulti.setColorFilter(Color.BLACK);
					ivFirstClassMulti.setColorFilter(Color.parseColor("#E60072bb"));
					
					break;

				default:
					break;
				}
			}
		};

		llEconomy.setOnClickListener(classListener);
		llBusiness.setOnClickListener(classListener);
		llFirst.setOnClickListener(classListener);

		llEconomyMulti.setOnClickListener(classListener);
		llBusinessMulti.setOnClickListener(classListener);
		llFirstMulti.setOnClickListener(classListener);

		strClass = "Economy";
		strClassMulti = "Economy";
		// ////////////////// check box ////////////////
		llDirect = (LinearLayout) rootView.findViewById(R.id.ll_non_stop);
		llDirectMulti = (LinearLayout) rootView
				.findViewById(R.id.ll_non_stop_multi);

		cbDirect = (CheckBox) rootView.findViewById(R.id.cb_non_stop);
		cbDirectMulti = (CheckBox) rootView
				.findViewById(R.id.cb_non_stop_multi);

		cbDirect.setClickable(false);
		cbDirectMulti.setClickable(false);

		llDirect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (cbDirect.isChecked()) {
					blDirect = false;
					cbDirect.setChecked(blDirect);
					strDirect = "False";
				} else {
					blDirect = true;
					cbDirect.setChecked(blDirect);
					strDirect = "True";
				}
			}
		});

		llDirectMulti.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (cbDirectMulti.isChecked()) {
					blDirectMulti = false;
					cbDirectMulti.setChecked(blDirectMulti);
					strDirectMulti = "False";
				} else {
					blDirectMulti = true;
					cbDirectMulti.setChecked(blDirectMulti);
					strDirectMulti = "True";
				}
			}
		});

		btnSearch = (Button) rootView.findViewById(R.id.btn_search);
		btnSearchMulti = (Button) rootView.findViewById(R.id.btn_search_multi);

		btnSearchMulti.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Boolean flag = false;
				String from1 = strFromCodeMulti1.trim();
				String from2 = strFromCodeMulti2.trim();
				String from3 = strFromCodeMulti3.trim();
				String from4 = strFromCodeMulti4.trim();

				String to1 = strToCodeMulti1.trim();
				String to2 = strToCodeMulti2.trim();
				String to3 = strToCodeMulti3.trim();
				String to4 = strToCodeMulti4.trim();

				String date1, date2, date3 = null, date4 = null;

				if (!from1.equals("") && !to1.equals("") && !from2.equals("")
						&& !to2.equals("")) {
					flag = true;
					if (i == 1) {
						if (!from3.equals("") && !to3.equals(""))
							flag = true;
						else
							flag = false;
					} else if (i == 2) {
						if (!from3.equals("") && !to3.equals("")
								&& !from4.equals("") && !to4.equals(""))
							flag = true;
						else
							flag = false;
					}
				} else
					flag = false;

				if (!flag)
					showAlert(getResources().getString(
							R.string.error_msg_empty_dep_arr));
				if (flag) {
					flag = false;
					if (!from1.equalsIgnoreCase(to1))
						if (!from2.equalsIgnoreCase(to2)) {
							if (i == 1) {
								if (!from3.equalsIgnoreCase(to3))
									flag = true;
								else
									showAlert(getResources()
											.getString(
													R.string.error_msg_same_dep_arr_multi_3));
							} else if (i == 2) {
								if (!from3.equalsIgnoreCase(to3))
									if (!from4.equalsIgnoreCase(to4))
										flag = true;
									else
										showAlert(getResources()
												.getString(
														R.string.error_msg_same_dep_arr_multi_4));
								else
									showAlert(getResources()
											.getString(
													R.string.error_msg_same_dep_arr_multi_3));
							} else
								flag = true;
						} else
							showAlert(getResources().getString(
									R.string.error_msg_same_dep_arr_multi_2));
					else
						showAlert(getResources().getString(
								R.string.error_msg_same_dep_arr_multi_1));
				}

				if (flag) {
					if (cd.isConnectingToInternet()) {
						if (intInfantCountMulti > intAdultCountMulti) {
							showAlert(getResources().getString(
									R.string.error_infant_cnt));
						} else if (intAdultCountMulti + intChildCountMulti
								+ intInfantCountMulti > 9) {
							showAlert(getResources().getString(
									R.string.error_search_lmt));
						} else {

							inte = new Intent(getActivity(),
									FlightResultActivity.class);
							
							Bundle b = new Bundle();
							b.putString("flight_type", triptype);
							b.putString("class", strClassMulti);
							b.putInt("trip_nos", 2 + i);
							b.putString("from1", from1);
							b.putString("to1", to1);
							b.putString("from2", from2);
							b.putString("to2", to2);
							b.putString("from3", from3);
							b.putString("to3", to3);
							b.putString("from4", from4);
							b.putString("to4", to4);
							b.putString("date1", tvDepartDateMulti_a.getText().toString().replace(",", ""));
							b.putString("date2", tvDepartDateMulti_b.getText().toString().replace(",", ""));
							b.putString("date3", tvDepartDateMulti_c.getText().toString().replace(",", ""));
							b.putString("date4", tvDepartDateMulti_d.getText().toString().replace(",", ""));
							b.putInt("adult_count", intAdultCountMulti);
							b.putInt("child_count", intChildCountMulti);
							b.putInt("infant_count", intInfantCountMulti);

							url1 = "category=" + triptype + "&itinerary=";
							date1 = strDateMulti1.replace(" ", "");
							date2 = strDateMulti2.replace(" ", "");

							switch (i) {
							case 0:
								url1 = url1 + from1 + "-" + to1 + "-" + date1
										+ "_" + from2 + "-" + to2 + "-" + date2
										+ "_";

								allAirportDetails = allAirportFromMulti_a + "-"
										+ allAirportToMulti_a + "_"
										+ allAirportFromMulti_b + "-"
										+ allAirportToMulti_b;
								break;

							case 1:
								date3 = strDateMulti3.replace(" ", "");
								url1 = url1 + from1 + "-" + to1 + "-" + date1
										+ "_" + from2 + "-" + to2 + "-" + date2
										+ "_" + from3 + "-" + to3 + "-" + date3
										+ "_";
								allAirportDetails = allAirportFromMulti_a + "-"
										+ allAirportToMulti_a + "_"
										+ allAirportFromMulti_b + "-"
										+ allAirportToMulti_b + "_"
										+ allAirportFromMulti_c + "-"
										+ allAirportToMulti_c;
								break;

							case 2:
								date3 = strDateMulti3.replace(" ", "");
								date4 = strDateMulti4.replace(" ", "");
								url1 = url1 + from1 + "-" + to1 + "-" + date1
										+ "_" + from2 + "-" + to2 + "-" + date2
										+ "_" + from3 + "-" + to3 + "-" + date3
										+ "_" + from4 + "-" + to4 + "-" + date4
										+ "_";
								allAirportDetails = allAirportFromMulti_a + "-"
										+ allAirportToMulti_a + "_"
										+ allAirportFromMulti_b + "-"
										+ allAirportToMulti_b + "_"
										+ allAirportFromMulti_c + "-"
										+ allAirportToMulti_c + "_"
										+ allAirportFromMulti_d + "-"
										+ allAirportToMulti_d;
								break;
							}

							url1 = url1 + "&passengerdetail=A-"
									+ intAdultCountMulti + "-C-"
									+ intChildCountMulti + "-I-"
									+ intInfantCountMulti + "&seatingClass="
									+ strClassMulti + "&isDirect="
									+ strDirectMulti
									+ "&preferredAirline=&currency="
									+ CommonFunctions.strCurrency;

							url1 = url1 + "&allowedAirport="
									+ allAirportDetails;

							Log.d("Mulicity URL", url1);
							inte.putExtras(b);
							inte.putExtra("url", url1);

							if (CommonFunctions.modify) {
								CommonFunctions.modify = false;
								CommonFunctions.flighResult.clear();
							}

							startActivity(inte);
						}
					} else {
						noInternetAlert();
					}
				}
			}
		});

		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (cd.isConnectingToInternet()) {
					if (!strFromCode.equals("") && !strToCode.equals("")) {
						if (strFromCode.equalsIgnoreCase(strToCode)) {
							showAlert(getResources().getString(
									R.string.error_msg_same_dep_arr));
						} else {
							if (intInfantCount > intAdultCount) {
								showAlert(getResources().getString(
										R.string.error_infant_cnt));
							} else if (intAdultCount + intChildCount
									+ intInfantCount > 9) {
								showAlert(getResources().getString(
										R.string.error_search_lmt));
							} else {
								String from = strFromCode;
								String to = strToCode;
								String fromDate = strDate.replace(" ", "");
								String toDate = strReturnDate.replace(" ", "");

								inte = new Intent(getActivity(),
										FlightResultActivity.class);
								Bundle b = new Bundle();
								b.putString("flight_type", triptype);
								b.putString("class", strClassMulti);
								b.putInt("trip_nos", 2);
								b.putString("from1", strFromCode);
								b.putString("to1", strToCode);
								
								b.putString("from_city", strFromCity.trim());
								b.putString("to_city", strToCity.trim());
								b.putString("departure_date", tvDepartDate.getText().toString().replace(",", ""));

								b.putInt("adult_count", intAdultCount);
								b.putInt("child_count", intChildCount);
								b.putInt("infant_count", intInfantCount);

								if (triptype.equalsIgnoreCase("oneway")) {
									url1 = "category=" + triptype
											+ "&itinerary=" + from + "-" + to
											+ "-" + fromDate + "_"
											+ "&passengerdetail=A-"
											+ intAdultCount + "-C-"
											+ intChildCount + "-I-"
											+ intInfantCount + "&seatingClass="
											+ strClass + "&isDirect="
											+ strDirect
											+ "&preferredAirline=&currency="
											+ CommonFunctions.strCurrency;
									Log.d("URL", url1);

								} else {
									b.putString("arrival_date", tvReturnDate.getText().toString().replace(",", ""));
									url1 = "category=" + triptype
											+ "&itinerary=" + from + "-" + to
											+ "-" + fromDate + "_" + to + "-"
											+ from + "-" + toDate + "_"
											+ "&passengerdetail=A-"
											+ intAdultCount + "-C-"
											+ intChildCount + "-I-"
											+ intInfantCount + "&seatingClass="
											+ strClass + "&isDirect="
											+ strDirect
											+ "&preferredAirline=&currency="
											+ CommonFunctions.strCurrency;
									Log.d("URL", url1);

								}

								allAirportDetails = allAirportFrom + "-"
										+ allAirportTo;

								url1 = url1 + "&allowedAirport="
										+ allAirportDetails;

								inte.putExtra("url", url1);
								inte.putExtras(b);
								Log.d("URL", url1);

								if (CommonFunctions.modify) {
									CommonFunctions.modify = false;
									CommonFunctions.flighResult.clear();
								}

								startActivity(inte);

							}
						}
					} else {
						showAlert(getResources().getString(
								R.string.error_msg_empty_dep_arr));
					}

				} else {
					noInternetAlert();
				}
			}
		});

		loadAssets();
		blLoadDefault = false;
		llEconomy.performClick();
		llEconomyMulti.performClick();
		tvRoundTripTab.performClick();
		return rootView;

	}

	private void loadAssets() {
		// ////////////******jason*************////////////////
		AssetManager am = getActivity().getAssets();
		String airportlist = null;
		InputStream file1 = null;
		try {
			if (CommonFunctions.lang.equalsIgnoreCase("en")) {
				file1 = am.open("airportslist.txt");
			} else {
				file1 = am.open("airportslistarabic.txt");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch barrayAirportListk
			e1.printStackTrace();
		}

		Log.e("Testing", "Starting to read");
		BufferedReader reader = null, reader1 = null;
		try {
			reader1 = new BufferedReader(new InputStreamReader(file1));
			StringBuilder builder1 = new StringBuilder();
			String line1 = null;
			while ((line1 = reader1.readLine()) != null) {
				builder1.append(line1);
			}

			airportlist = builder1.toString();
			arrayAirportList = new ArrayList<String>();

			if (airportlist != null) {
				JSONArray jsonArray = new JSONArray(airportlist);

				for (int i = 0; i < jsonArray.length(); i++) {
					arrayAirportList.add(jsonArray.getString(i));
				}
			}

			airportlist = null;
			file1.close();
			reader1.close();
			line1 = null;

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

	// ------------------------------ Airport dialogs
	// -----------------------------
	private void showToDialog() {
		tvDialogHeader.setText(R.string.to);
		tvNoMatch.setVisibility(View.GONE);
		actAirports.setText(null);

		final InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

		OnItemClickListener onitem = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				String actvstringto = actAirports.getText().toString();

				String resource[];
				// if(CommonFunctions.lang.equalsIgnoreCase("ar")){
				// resource = actvstringto.split(" ~ ");
				// actvstringto = resource[0];
				// }

				strToCity = actvstringto;
				resource = actvstringto.split("-");
				strToCode = resource[3];
				strToCode = strToCode.replace(" ", "");
				strToCity = resource[0];
				
				tvToCode.setText(strToCode);
				tvToCity.setText(strToCity);

				if (resource[1].toLowerCase().contains("all airport")
						|| resource[1].toLowerCase().contains("جميع المطار"))
					allAirportTo = "Y";
				else
					allAirportTo = "N";

				Editor editor = sharedpreferences.edit();
				editor.putString("strToCode", strToCode);
				editor.putString("allAirportTo", allAirportTo);
				editor.commit();

				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

				dialogAirport.dismiss();
			}
		};

		actAirports.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String filter = s.toString().toLowerCase();
				listItems = new ArrayList<String>();
				for (String listItem : arrayAirportList) {
					if (listItem.toLowerCase().contains(filter)) {
						listItems.add(listItem);
					}

				}
				if (listItems.size() == 0) {
					if (tvNoMatch.getVisibility() == View.GONE)
						tvNoMatch.setVisibility(View.VISIBLE);
				} else {
					if (tvNoMatch.getVisibility() == View.VISIBLE)
						tvNoMatch.setVisibility(View.GONE);
				}
				ArrayAdapter<String> adapt = new ArrayAdapter<String>(
						getActivity(), android.R.layout.simple_list_item_1,
						listItems);
				actAirports.setAdapter(adapt);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		actAirports.setOnItemClickListener(onitem);
		dialogAirport.show();
		actAirports.requestFocus();

		ibClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				dialogAirport.dismiss();
			}
		});

	}

	private void showToDialogMulti_a() {
		tvDialogHeader.setText(R.string.to);
		tvNoMatch.setVisibility(View.GONE);
		actAirports.setText(null);
		final InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

		OnItemClickListener onitem = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				String actvstringto = actAirports.getText().toString();

				String resource[];
				// if(CommonFunctions.lang.equalsIgnoreCase("ar")){
				// resource = actvstringto.split(" ~ ");
				// actvstringto = resource[0];
				// }

				strToCityMulti1 = actvstringto;
				resource = actvstringto.split("-");
				strToCodeMulti1 = resource[3];
				strToCodeMulti1 = strToCodeMulti1.replace(" ", "");
				strToCityMulti1 = resource[0];

				tvToCodeMulti_a.setText(strToCodeMulti1);
				tvToCityMulti_a.setText(strToCityMulti1);

				if (resource[1].toLowerCase().contains("all airport")
						|| resource[1].toLowerCase().contains("جميع المطار"))
					allAirportToMulti_a = "Y";
				else
					allAirportToMulti_a = "N";

				Editor editor = sharedpreferences.edit();
				editor.putString("strToCodeMulti1", strToCodeMulti1);
				editor.putString("allAirportToMulti_a", allAirportToMulti_a);
				editor.commit();

				dialogAirport.dismiss();

			}
		};
		actAirports.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String filter = s.toString().toLowerCase();
				listItems = new ArrayList<String>();
				for (String listItem : arrayAirportList) {
					if (listItem.toLowerCase().contains(filter)) {
						listItems.add(listItem);
					}
				}
				if (listItems.size() == 0) {
					if (tvNoMatch.getVisibility() == View.GONE)
						tvNoMatch.setVisibility(View.VISIBLE);
				} else {
					if (tvNoMatch.getVisibility() == View.VISIBLE)
						tvNoMatch.setVisibility(View.GONE);
				}
				ArrayAdapter<String> adapt = new ArrayAdapter<String>(
						getActivity(), android.R.layout.simple_list_item_1,
						listItems);
				actAirports.setAdapter(adapt);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		actAirports.setOnItemClickListener(onitem);
		dialogAirport.show();

		ibClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				dialogAirport.dismiss();
			}
		});

	}

	private void showToDialogMulti_b() {
		tvDialogHeader.setText(R.string.to);
		tvNoMatch.setVisibility(View.GONE);
		actAirports.setText(null);
		final InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

		OnItemClickListener onitem = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				String actvstringto = actAirports.getText().toString();

				String resource[];
				// if(CommonFunctions.lang.equalsIgnoreCase("ar")){
				// resource = actvstringto.split(" ~ ");
				// actvstringto = resource[0];
				// }

				strToCityMulti2 = actvstringto;
				resource = actvstringto.split("-");
				strToCodeMulti2 = resource[3];
				strToCodeMulti2 = strToCodeMulti2.replace(" ", "");
				strToCityMulti2 = resource[0];

				tvToCodeMulti_b.setText(strToCodeMulti2);
				tvToCityMulti_b.setText(strToCityMulti2);

				if (resource[1].toLowerCase().contains("all airport")
						|| resource[1].toLowerCase().contains("جميع المطار"))
					allAirportToMulti_b = "Y";
				else
					allAirportToMulti_b = "N";

				Editor editor = sharedpreferences.edit();
				editor.putString("strToCodeMulti2", strToCodeMulti2);
				editor.putString("allAirportToMulti_b", allAirportToMulti_b);
				editor.commit();

				dialogAirport.dismiss();

			}
		};
		actAirports.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String filter = s.toString().toLowerCase();
				listItems = new ArrayList<String>();
				for (String listItem : arrayAirportList) {
					if (listItem.toLowerCase().contains(filter)) {
						listItems.add(listItem);
					}
				}
				if (listItems.size() == 0) {
					if (tvNoMatch.getVisibility() == View.GONE)
						tvNoMatch.setVisibility(View.VISIBLE);
				} else {
					if (tvNoMatch.getVisibility() == View.VISIBLE)
						tvNoMatch.setVisibility(View.GONE);
				}
				ArrayAdapter<String> adapt = new ArrayAdapter<String>(
						getActivity(), android.R.layout.simple_list_item_1,
						listItems);
				actAirports.setAdapter(adapt);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		actAirports.setOnItemClickListener(onitem);
		dialogAirport.show();

		ibClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				dialogAirport.dismiss();
			}
		});

	}

	private void showToDialogMulti_c() {
		tvDialogHeader.setText(R.string.to);
		tvNoMatch.setVisibility(View.GONE);
		actAirports.setText(null);

		final InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

		OnItemClickListener onitem = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				String actvstringto = actAirports.getText().toString();

				String resource[];
				// if(CommonFunctions.lang.equalsIgnoreCase("ar")){
				// resource = actvstringto.split(" ~ ");
				// actvstringto = resource[0];
				// }

				strToCityMulti3 = actvstringto;
				resource = actvstringto.split("-");
				strToCodeMulti3 = resource[3];
				strToCodeMulti3 = strToCodeMulti3.replace(" ", "");
				strToCityMulti3 = resource[0];

				tvToCodeMulti_c.setText(strToCodeMulti3);
				tvToCityMulti_c.setText(strToCityMulti3);

				if (resource[1].toLowerCase().contains("all airport")
						|| resource[1].toLowerCase().contains("جميع المطار"))
					allAirportToMulti_c = "Y";
				else
					allAirportToMulti_c = "N";

				Editor editor = sharedpreferences.edit();
				editor.putString("strToCodeMulti3", strToCodeMulti3);
				editor.putString("allAirportToMulti_c", allAirportToMulti_c);
				editor.commit();

				dialogAirport.dismiss();

			}
		};
		actAirports.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String filter = s.toString().toLowerCase();
				listItems = new ArrayList<String>();
				for (String listItem : arrayAirportList) {
					if (listItem.toLowerCase().contains(filter)) {
						listItems.add(listItem);
					}
				}

				if (listItems.size() == 0) {
					if (tvNoMatch.getVisibility() == View.GONE)
						tvNoMatch.setVisibility(View.VISIBLE);
				} else {
					if (tvNoMatch.getVisibility() == View.VISIBLE)
						tvNoMatch.setVisibility(View.GONE);
				}
				ArrayAdapter<String> adapt = new ArrayAdapter<String>(
						getActivity(), android.R.layout.simple_list_item_1,
						listItems);
				actAirports.setAdapter(adapt);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		actAirports.setOnItemClickListener(onitem);
		dialogAirport.show();

		ibClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				dialogAirport.dismiss();
			}
		});

	}

	private void showToDialogMulti_d() {
		tvDialogHeader.setText(R.string.to);
		tvNoMatch.setVisibility(View.GONE);
		actAirports.setText(null);

		final InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

		OnItemClickListener onitem = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				String actvstringto = actAirports.getText().toString();

				String resource[];
				// if(CommonFunctions.lang.equalsIgnoreCase("ar")){
				// resource = actvstringto.split(" ~ ");
				// actvstringto = resource[0];
				// }

				strToCityMulti4 = actvstringto;
				resource = actvstringto.split("-");
				strToCodeMulti4 = resource[3];
				strToCodeMulti4 = strToCodeMulti4.replace(" ", "");
				strToCityMulti4 = resource[0];

				tvToCodeMulti_d.setText(strToCodeMulti4);
				tvToCityMulti_d.setText(strToCityMulti4);

				if (resource[1].toLowerCase().contains("all airport")
						|| resource[1].toLowerCase().contains("جميع المطار"))
					allAirportToMulti_d = "Y";
				else
					allAirportToMulti_d = "N";

				Editor editor = sharedpreferences.edit();
				editor.putString("strToCodeMulti4", strToCodeMulti4);
				editor.putString("allAirportToMulti_d", allAirportToMulti_d);
				editor.commit();

				dialogAirport.dismiss();

			}
		};
		actAirports.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String filter = s.toString().toLowerCase();
				listItems = new ArrayList<String>();
				for (String listItem : arrayAirportList) {
					if (listItem.toLowerCase().contains(filter)) {
						listItems.add(listItem);
					}
				}
				if (listItems.size() == 0) {
					if (tvNoMatch.getVisibility() == View.GONE)
						tvNoMatch.setVisibility(View.VISIBLE);
				} else {
					if (tvNoMatch.getVisibility() == View.VISIBLE)
						tvNoMatch.setVisibility(View.GONE);
				}
				ArrayAdapter<String> adapt = new ArrayAdapter<String>(
						getActivity(), android.R.layout.simple_list_item_1,
						listItems);
				actAirports.setAdapter(adapt);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		actAirports.setOnItemClickListener(onitem);
		dialogAirport.show();

		ibClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				dialogAirport.dismiss();
			}
		});

	}

	// ----------------------------------- Date dialogs ------------------------

	private void showDepartDate() {
		final CalendarPickerView calendar = (CalendarPickerView) dialogDate
				.findViewById(R.id.calendar_view);
		calendar.init(currday.getTime(), nextYear.getTime())
				.inMode(SelectionMode.SINGLE).withSelectedDate(seldate);
		((TextView) dialogDate.findViewById(R.id.header))
				.setText(getResources().getString(R.string.depart_date));

		OnDateSelectedListener ondate = new OnDateSelectedListener() {

			@Override
			public void onDateUnselected(Date date) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDateSelected(Date date) {
				seldate = date;
				selreturn = date;
				tvDepartDate.setText(sdfPrint.format(date.getTime()));
				strDate = sdfUrl.format(seldate);
				Log.d("From Date One Way After Setting", strDate);
				setReturnDate();

				dialogDate.dismiss();

				if (triptype.equalsIgnoreCase("Roundtrip")) {
					showReturnDialog();
				}
			}
		};

		dialogDate.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				llDepartDate.setEnabled(true);
			}
		});
		calendar.setOnDateSelectedListener(ondate);
		dialogDate.show();
		llDepartDate.setEnabled(false);
	}

	private void showReturnDialog() {
		// final ArrayList<Date> dates = new ArrayList<Date>();
		// dates.add(seldate);
		// if(seldate.compareTo(selreturn.) != 0)
		// dates.add(selreturn);

		final CalendarPickerView calendar = (CalendarPickerView) dialogDate
				.findViewById(R.id.calendar_view);
		calendar.init(seldate, nextYear.getTime()).inMode(SelectionMode.SINGLE)
				.withSelectedDate(selreturn);
		((TextView) dialogDate.findViewById(R.id.header))
				.setText(getResources().getString(R.string.return_date));
		OnDateSelectedListener ondate = new OnDateSelectedListener() {

			@Override
			public void onDateUnselected(Date date) {
				// TODO Auto-generated method stub

				calendar.init(seldate, nextYear.getTime())
						.inMode(SelectionMode.MULTIPLE)
						.withSelectedDates(dates);
				selreturn = date;
				dialogDate.dismiss();

			}

			@Override
			public void onDateSelected(Date date) {
				selreturn = date;
				dialogDate.dismiss();
			}
		};

		calendar.setOnDateSelectedListener(ondate);
		dialogDate.show();
		llReturnDate.setEnabled(false);
		dialogDate.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				llReturnDate.setEnabled(true);
				setReturnDate();
			}
		});
	}

	private void setReturnDate() {
		tvReturnDate.setText(sdfPrint.format(selreturn));
		strReturnDate = sdfUrl.format(selreturn);
		Log.d("To Date Round Trip After Setting:", strReturnDate);
	}

	private void showDepartDateMulti_a() {
		final CalendarPickerView calendar = (CalendarPickerView) dialogDate
				.findViewById(R.id.calendar_view);
		calendar.init(currday.getTime(), nextYear.getTime())
				.inMode(SelectionMode.SINGLE).withSelectedDate(mcseldate1);
		((TextView) dialogDate.findViewById(R.id.header))
				.setText(getResources().getString(R.string.depart_date));

		OnDateSelectedListener ondate = new OnDateSelectedListener() {

			@Override
			public void onDateUnselected(Date date) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDateSelected(Date date) {
				mcseldate1 = calendar.getSelectedDate();
				mcseldate2 = calendar.getSelectedDate();
				mcseldate3 = calendar.getSelectedDate();
				mcseldate4 = calendar.getSelectedDate();

				dialogDate.dismiss();
			}
		};

		calendar.setOnDateSelectedListener(ondate);

		dialogDate.show();
		llDepartDateMulti_a.setEnabled(false);
		dialogDate.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				llDepartDateMulti_a.setEnabled(true);
				setMultiCityDates();
			}
		});
	}

	ArrayList<Date> dates = new ArrayList<Date>();

	private void showDepartDateMulti_b() {
		dates.clear();
		dates.add(mcseldate1);
		if (mcseldate1.compareTo(mcseldate2) != 0) {
			dates.add(mcseldate2);
		}
		final CalendarPickerView calendar = (CalendarPickerView) dialogDate
				.findViewById(R.id.calendar_view);
		calendar.init(mcseldate1, nextYear.getTime())
				.inMode(SelectionMode.MULTIPLE).withSelectedDates(dates);
		((TextView) dialogDate.findViewById(R.id.header))
				.setText(getResources().getString(R.string.depart_date));
		OnDateSelectedListener ondate = new OnDateSelectedListener() {

			@Override
			public void onDateUnselected(Date date) {
				// TODO Auto-generated method stub

				calendar.init(mcseldate1, nextYear.getTime())
						.inMode(SelectionMode.MULTIPLE)
						.withSelectedDates(dates);
				if (mcseldate2.compareTo(date) == 0) {
					mcseldate2 = date;
					mcseldate3 = date;
					mcseldate4 = date;
					dialogDate.dismiss();
				} else {
					mcseldate1 = date;
					mcseldate2 = date;
					mcseldate3 = date;
					mcseldate4 = date;
					dialogDate.dismiss();
				}
			}

			@Override
			public void onDateSelected(Date date) {
				List<Date> listSelectedDates = calendar.getSelectedDates();
				mcseldate1 = listSelectedDates.get(0);
				if (listSelectedDates.size() > 1) {
					if (mcseldate2.compareTo(listSelectedDates
							.get(listSelectedDates.size() - 1)) != 0) {
						mcseldate2 = listSelectedDates.get(listSelectedDates
								.size() - 1);
						mcseldate3 = listSelectedDates.get(listSelectedDates
								.size() - 1);
						mcseldate4 = listSelectedDates.get(listSelectedDates
								.size() - 1);
					} else {
						mcseldate2 = listSelectedDates.get(1);
						mcseldate3 = listSelectedDates.get(1);
						mcseldate4 = listSelectedDates.get(1);
					}
				}

				dialogDate.dismiss();
			}
		};

		calendar.setOnDateSelectedListener(ondate);

		dialogDate.show();
		llDepartDateMulti_b.setEnabled(false);
		dialogDate.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				llDepartDateMulti_b.setEnabled(true);
				setMultiCityDates();
			}
		});
	}

	private void showDepartDateMulti_c() {
		dates.clear();
		dates.add(mcseldate1);
		if (mcseldate1.compareTo(mcseldate2) != 0) {
			dates.add(mcseldate2);
		}
		if (mcseldate2.compareTo(mcseldate3) != 0) {
			dates.add(mcseldate3);
		}
		final CalendarPickerView calendar = (CalendarPickerView) dialogDate
				.findViewById(R.id.calendar_view);
		calendar.init(mcseldate1, nextYear.getTime())
				.inMode(SelectionMode.MULTIPLE).withSelectedDates(dates);
		((TextView) dialogDate.findViewById(R.id.header))
				.setText(getResources().getString(R.string.depart_date));

		OnDateSelectedListener ondate = new OnDateSelectedListener() {

			@Override
			public void onDateUnselected(Date date) {
				// TODO Auto-generated method stub
				calendar.init(mcseldate1, nextYear.getTime())
						.inMode(SelectionMode.MULTIPLE)
						.withSelectedDates(dates);
				if (mcseldate3.compareTo(date) == 0) {
					mcseldate3 = date;
					mcseldate4 = date;
					dialogDate.dismiss();
				} else if (mcseldate2.compareTo(date) == 0) {
					mcseldate2 = date;
					mcseldate3 = date;
					mcseldate4 = date;
					dialogDate.dismiss();
				} else {
					mcseldate1 = date;
					mcseldate2 = date;
					mcseldate3 = date;
					mcseldate4 = date;
					dialogDate.dismiss();
				}
			}

			@Override
			public void onDateSelected(Date date) {
				List<Date> listSelectedDates = calendar.getSelectedDates();
				if (listSelectedDates.size() == 4) {
					mcseldate1 = listSelectedDates.get(0);
					mcseldate2 = listSelectedDates.get(1);
					if (mcseldate3.compareTo(listSelectedDates
							.get(listSelectedDates.size() - 1)) != 0) {
						mcseldate3 = listSelectedDates.get(listSelectedDates
								.size() - 1);
						mcseldate4 = listSelectedDates.get(listSelectedDates
								.size() - 1);
					} else {
						mcseldate3 = listSelectedDates.get(listSelectedDates
								.size() - 2);
						mcseldate4 = listSelectedDates.get(listSelectedDates
								.size() - 2);
					}
				} else if (listSelectedDates.size() == 3) {
					mcseldate1 = listSelectedDates.get(0);
					mcseldate2 = listSelectedDates.get(1);
					mcseldate3 = listSelectedDates.get(2);
					mcseldate4 = listSelectedDates.get(2);
				}

				else if (listSelectedDates.size() == 2) {
					mcseldate3 = date;
					mcseldate4 = date;
				}

				dialogDate.dismiss();
			}
		};

		calendar.setOnDateSelectedListener(ondate);

		dialogDate.show();
		llDepartDateMulti_c.setEnabled(false);
		dialogDate.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				llDepartDateMulti_c.setEnabled(true);
				setMultiCityDates();
			}
		});
	}

	private void showDepartDateMulti_d() {
		dates.clear();
		dates.add(mcseldate1);
		if (mcseldate1.compareTo(mcseldate2) != 0) {
			dates.add(mcseldate2);
		}
		if (mcseldate2.compareTo(mcseldate3) != 0) {
			dates.add(mcseldate3);
		}
		if (mcseldate3.compareTo(mcseldate4) != 0) {
			dates.add(mcseldate4);
		}

		final CalendarPickerView calendar = (CalendarPickerView) dialogDate
				.findViewById(R.id.calendar_view);
		calendar.init(mcseldate1, nextYear.getTime())
				.inMode(SelectionMode.MULTIPLE).withSelectedDates(dates);

		((TextView) dialogDate.findViewById(R.id.header))
				.setText(getResources().getString(R.string.depart_date));

		OnDateSelectedListener ondate = new OnDateSelectedListener() {

			@Override
			public void onDateUnselected(Date date) {
				// TODO Auto-generated method stub

				calendar.init(mcseldate1, nextYear.getTime())
						.inMode(SelectionMode.MULTIPLE)
						.withSelectedDates(dates);
				if (mcseldate4.compareTo(date) == 0) {
					dialogDate.dismiss();
				} else if (mcseldate3.compareTo(date) == 0) {
					mcseldate3 = date;
					mcseldate4 = date;
					dialogDate.dismiss();
				} else if (mcseldate2.compareTo(date) == 0) {
					mcseldate2 = date;
					mcseldate3 = date;
					mcseldate4 = date;
					dialogDate.dismiss();
				} else {
					mcseldate1 = date;
					mcseldate2 = date;
					mcseldate3 = date;
					mcseldate4 = date;
					dialogDate.dismiss();
				}

			}

			@Override
			public void onDateSelected(Date date) {
				List<Date> listSelectedDates = calendar.getSelectedDates();
				if (listSelectedDates.size() == 5) {
					mcseldate1 = listSelectedDates.get(0);
					mcseldate2 = listSelectedDates.get(1);
					mcseldate3 = listSelectedDates.get(2);
					if (mcseldate4.compareTo(listSelectedDates
							.get(listSelectedDates.size() - 1)) != 0) {
						mcseldate4 = listSelectedDates.get(listSelectedDates
								.size() - 1);
					} else {
						mcseldate4 = listSelectedDates.get(listSelectedDates
								.size() - 2);
					}
				} else if (listSelectedDates.size() == 4) {
					if (mcseldate2.compareTo(mcseldate3) == 0) {
						mcseldate1 = listSelectedDates.get(0);
						mcseldate2 = listSelectedDates.get(1);
						mcseldate3 = listSelectedDates.get(1);
						if (mcseldate4.compareTo(listSelectedDates
								.get(listSelectedDates.size() - 1)) != 0) {
							mcseldate4 = listSelectedDates
									.get(listSelectedDates.size() - 1);
						} else {
							mcseldate4 = listSelectedDates
									.get(listSelectedDates.size() - 2);
						}
					}

					else if (mcseldate1.compareTo(mcseldate2) == 0) {
						mcseldate1 = listSelectedDates.get(0);
						mcseldate2 = listSelectedDates.get(0);
						mcseldate3 = listSelectedDates.get(1);
						if (mcseldate4.compareTo(listSelectedDates
								.get(listSelectedDates.size() - 1)) != 0) {
							mcseldate4 = listSelectedDates
									.get(listSelectedDates.size() - 1);
						} else {
							mcseldate4 = listSelectedDates
									.get(listSelectedDates.size() - 2);
						}
					} else {
						mcseldate1 = listSelectedDates.get(0);
						mcseldate2 = listSelectedDates.get(1);
						mcseldate3 = listSelectedDates.get(2);
						mcseldate4 = listSelectedDates.get(3);
					}
				}

				else if (listSelectedDates.size() == 3) {
					if (mcseldate1.compareTo(mcseldate2) == 0
							&& mcseldate1.compareTo(mcseldate3) == 0) {
						mcseldate4 = date;
					} else if (mcseldate2.compareTo(mcseldate3) == 0) {
						mcseldate1 = listSelectedDates.get(0);
						mcseldate2 = listSelectedDates.get(1);
						mcseldate3 = listSelectedDates.get(1);
						mcseldate4 = listSelectedDates.get(2);
					} else if (mcseldate1.compareTo(mcseldate2) == 0) {
						mcseldate1 = listSelectedDates.get(0);
						mcseldate2 = listSelectedDates.get(0);
						mcseldate3 = listSelectedDates.get(1);
						mcseldate4 = listSelectedDates.get(2);
					}
				}

				else if (listSelectedDates.size() == 2) {
					mcseldate4 = date;
				}

				dialogDate.dismiss();
			}
		};

		calendar.setOnDateSelectedListener(ondate);

		dialogDate.show();
		llDepartDateMulti_d.setEnabled(false);
		dialogDate.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				llDepartDateMulti_d.setEnabled(true);
				setMultiCityDates();
			}
		});
	}

	private void setMultiCityDates() {
		strDateMulti1 = sdfUrl.format(mcseldate1);
		strDateMulti2 = sdfUrl.format(mcseldate2);
		strDateMulti3 = sdfUrl.format(mcseldate3);
		strDateMulti4 = sdfUrl.format(mcseldate4);

		tvDepartDateMulti_a.setText(sdfPrint.format(mcseldate1));
		tvDepartDateMulti_b.setText(sdfPrint.format(mcseldate2));
		tvDepartDateMulti_c.setText(sdfPrint.format(mcseldate3));
		tvDepartDateMulti_d.setText(sdfPrint.format(mcseldate4));
	}

	// ------------------------------ setting defaults ------------------------
	public void loadDefaults() {
		// one way values
		strFromCode = sharedpreferences.getString("strFromCode", "KWI");
		strToCode = sharedpreferences.getString("strToCode", "DXB");

		allAirportFrom = sharedpreferences.getString("allAirportFrom", "N");
		allAirportTo = sharedpreferences.getString("allAirportTo", "N");

		tvFromCode.setText(strFromCode);
		tvToCode.setText(strToCode);

		tvFromCity.setText(strFromCity);
		tvToCity.setText(strToCity);

		String resource[];

		if (!strFromCode.equals("")) {
			strFromCity = getCity(strFromCode, allAirportFrom);
			tvFromCity.setText(strFromCity);
			resource = strFromCity.split("-");
			strFromCity = resource[0];
		}

		if (!strToCode.equals("")) {
			strToCity = getCity(strToCode, allAirportTo);
			tvToCity.setText(strToCity);
			resource = strToCity.split("-");
			strToCity = resource[0];
		}

	}

	public void loadMultiCityDefaults() {
		strFromCodeMulti1 = sharedpreferences.getString("strFromCodeMulti1",
				"KWI"); // city 1
		strToCodeMulti1 = sharedpreferences.getString("strToCodeMulti1", "DXB");

		strFromCodeMulti2 = sharedpreferences.getString("strFromCodeMulti2",
				"KWI"); // city 2
		strToCodeMulti2 = sharedpreferences.getString("strToCodeMulti2", "DXB");

		strFromCodeMulti3 = sharedpreferences.getString("strFromCodeMulti3",
				"KWI"); // city 3
		strToCodeMulti3 = sharedpreferences.getString("strToCodeMulti3", "DXB");

		strFromCodeMulti4 = sharedpreferences.getString("strFromCodeMulti4",
				"KWI"); // city 4
		strToCodeMulti4 = sharedpreferences.getString("strToCodeMulti4", "DXB");

		allAirportFromMulti_a = sharedpreferences.getString(
				"allAirportFromMulti_a", "N");
		allAirportToMulti_a = sharedpreferences.getString(
				"allAirportToMulti_a", "N");

		allAirportFromMulti_b = sharedpreferences.getString(
				"allAirportFromMulti_b", "N");
		allAirportToMulti_b = sharedpreferences.getString(
				"allAirportToMulti_b", "N");

		allAirportFromMulti_c = sharedpreferences.getString(
				"allAirportFromMulti_c", "N");
		allAirportToMulti_c = sharedpreferences.getString(
				"allAirportToMulti_c", "N");

		allAirportFromMulti_d = sharedpreferences.getString(
				"allAirportFromMulti_d", "N");
		allAirportToMulti_d = sharedpreferences.getString(
				"allAirportToMulti_d", "N");

		tvFromCodeMulti_a.setText(strFromCodeMulti1);
		tvFromCodeMulti_b.setText(strFromCodeMulti2);
		tvFromCodeMulti_c.setText(strFromCodeMulti3);
		tvFromCodeMulti_d.setText(strFromCodeMulti4);

		tvToCodeMulti_a.setText(strToCodeMulti1);
		tvToCodeMulti_b.setText(strToCodeMulti2);
		tvToCodeMulti_c.setText(strToCodeMulti3);
		tvToCodeMulti_d.setText(strToCodeMulti4);

		tvFromCityMulti_a.setText(strFromCityMulti1);
		tvFromCityMulti_b.setText(strFromCityMulti2);
		tvFromCityMulti_c.setText(strFromCityMulti3);
		tvFromCityMulti_d.setText(strFromCityMulti4);

		tvToCityMulti_a.setText(strToCityMulti1);
		tvToCityMulti_b.setText(strToCityMulti2);
		tvToCityMulti_c.setText(strToCityMulti3);
		tvToCityMulti_d.setText(strToCityMulti4);

		String resource[];

		if (!strFromCodeMulti1.equals("")) {
			strFromCityMulti1 = getCity(strFromCodeMulti1,
					allAirportFromMulti_a);
			tvFromCityMulti_a.setText(strFromCityMulti1);
			resource = strFromCityMulti1.split("-");
			strFromCityMulti1 = resource[0];
		}

		if (!strToCodeMulti1.equals("")) {
			strToCityMulti1 = getCity(strToCodeMulti1, allAirportToMulti_a);
			tvToCityMulti_a.setText(strToCityMulti1);
			resource = strToCityMulti1.split("-");
			strToCityMulti1 = resource[0];
		}

		if (!strFromCodeMulti2.equals("")) {
			strFromCityMulti2 = getCity(strFromCodeMulti2,
					allAirportFromMulti_b);
			tvFromCityMulti_b.setText(strFromCityMulti2);
			resource = strFromCityMulti2.split("-");
			strFromCityMulti2 = resource[0];
		}

		if (!strToCodeMulti2.equals("")) {
			strToCityMulti2 = getCity(strToCodeMulti2, allAirportToMulti_b);
			tvToCityMulti_b.setText(strToCityMulti2);
			resource = strToCityMulti2.split("-");
			strToCityMulti2 = resource[0];
		}

		if (!strFromCodeMulti3.equals("")) {
			strFromCityMulti3 = getCity(strFromCodeMulti3,
					allAirportFromMulti_c);
			tvFromCityMulti_c.setText(strFromCityMulti3);
			resource = strFromCityMulti3.split("-");
			strFromCityMulti3 = resource[0];
		}

		if (!strToCodeMulti3.equals("")) {
			strToCityMulti3 = getCity(strToCodeMulti3, allAirportToMulti_c);
			tvToCityMulti_c.setText(strToCityMulti3);
			resource = strToCityMulti3.split("-");
			strToCityMulti3 = resource[0];
		}

		if (!strFromCodeMulti4.equals("")) {
			strFromCityMulti4 = getCity(strFromCodeMulti4,
					allAirportFromMulti_d);
			tvFromCityMulti_d.setText(strFromCityMulti4);
			resource = strFromCityMulti4.split("-");
			strFromCityMulti4 = resource[0];
		}

		if (!strToCodeMulti4.equals("")) {
			strToCityMulti4 = getCity(strToCodeMulti4, allAirportToMulti_d);
			tvToCityMulti_d.setText(strToCityMulti4);
			resource = strToCityMulti4.split("-");
			strToCityMulti4 = resource[0];
		}

	}

	public String getCity(String cityCode, String charAllAirport) {
		String cityCode1 = cityCode.toString().toLowerCase();
		for (String listItem : arrayAirportList) {
			if (listItem.toLowerCase().contains(cityCode1)) {
				if (charAllAirport.equalsIgnoreCase("N")
						&& (listItem.toLowerCase().contains("all airport") || listItem
								.toLowerCase().contains("جميع المطار"))) {

				} else {
					String resource[];
					if (CommonFunctions.lang.equalsIgnoreCase("ar")) {
						resource = listItem.split(" ~ ");
						listItem = resource[0];
					}
					resource = listItem.split("-");
					String code = resource[3];
					code = code.replace("\t \n", "");
					code = code.replace(" ", "");
					if (code.equalsIgnoreCase(cityCode))
						return resource[0];
				}
			}
		}
		return null;
	}

	public void showAlert(String errorMsg) {
		String titleMsg = getResources().getString(R.string.error_title);
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.DialogAnimationAlert);
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

		// Setting OK Button
		alertDialog.setPositiveButton(
				getResources().getString(R.string.error_no_internet_settings),
				new AlertDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// intent to move mobile settings
						startActivity(new Intent(
								Settings.ACTION_WIRELESS_SETTINGS));
					}
				});
		alertDialog.setNegativeButton(
				getResources().getString(R.string.error_no_internet_cancel),
				null);

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		arrayAirportList = null;
		prefferedairlinelist = null;
		listItems = null;
		seldate = null;
		selreturn = null;
		mcseldate1 = null;
		mcseldate2 = null;
		mcseldate3 = null;
		mcseldate4 = null;
		super.onDestroy();
	}

}
