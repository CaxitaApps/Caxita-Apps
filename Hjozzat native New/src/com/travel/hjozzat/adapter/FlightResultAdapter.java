package com.travel.hjozzat.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hjozzat.support.CommonFunctions;
import com.travel.hjozzat.FlightResultGroupActivity;
import com.travel.hjozzat.R;
import com.travel.hjozzat.model.FlightResultItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class FlightResultAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<FlightResultItem> flightResultItem;
	private boolean isRoundTrip = false, isGroup = false;
	String strBaggageDetails = null;
	CommonFunctions cf;

	public FlightResultAdapter(Context context,
			ArrayList<FlightResultItem> flightResultItem, boolean isRoundTrip) {
		this.context = context;
		this.flightResultItem = flightResultItem;
		this.isRoundTrip = isRoundTrip;
	}
	
	public FlightResultAdapter(Context context,
			ArrayList<FlightResultItem> flightResultItem, boolean isRoundTrip, boolean isGroup) {
		this.context = context;
		this.flightResultItem = flightResultItem;
		this.isRoundTrip = isRoundTrip;
		this.isGroup = isGroup;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		return flightResultItem.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return flightResultItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		boolean blStart = false;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.search_result_item_flight,
					null);
			blStart = true;
		} else {
			((LinearLayout) convertView
			.findViewById(R.id.ll_flight_details_list)).removeAllViews();
		}

		final FlightResultItem fItem = flightResultItem.get(position);
		cf = new CommonFunctions(context);
		JSONObject allJourney, listFlight; // , segment;
		JSONArray jarray, listFlightArray; // , segmentArray;
		ImageView ivFlightLogo;
		InputStream ims;
		Drawable d;
		LinearLayout llFlightResult;
		String stops;
		// flightName, flightCode, flightNumber, stops, equipmentNo;

		jarray = fItem.jarray;

		String price = String.format(new Locale("en"), "%.3f",
				Double.parseDouble(fItem.strDisplayRate));
		((TextView) convertView.findViewById(R.id.tv_flight_price))
				.setText(CommonFunctions.strCurrency + " " + price);

		final LinearLayout llMore = ((LinearLayout) convertView.findViewById(R.id.ll_more));
		
		if(!isGroup) {
			if (fItem.samePriceCount > 1) {
				((TextView) convertView.findViewById(R.id.tv_more)).setText(String
						.valueOf(fItem.samePriceCount-1));
				llMore.setVisibility(View.VISIBLE);
				llMore.setTag(fItem.strDisplayRate);
			} else {
				llMore.setVisibility(View.INVISIBLE);
			}
			
			llMore.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent group = new Intent(context, FlightResultGroupActivity.class);
					group.putExtra("price", llMore.getTag().toString());
					group.putExtra("isRoundTrip", isRoundTrip);
					context.startActivity(group);
				}
			});
		} else
			llMore.setVisibility(View.GONE);
		
		llFlightResult = (LinearLayout) convertView
				.findViewById(R.id.ll_flight_items);

		try {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			for (int i = 0; i < jarray.length(); i++) {
				View vFlightItem = null;
				if (blStart)
					vFlightItem = mInflater.inflate(
							R.layout.item_flight_result, null);
				else {
					vFlightItem = llFlightResult.getChildAt(i);
				}

				TextView tvFlightType = new TextView(context);
				tvFlightType.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));
				tvFlightType.setText(context.getResources().getString(
						R.string.onward).toUpperCase());
				tvFlightType.setTypeface(Typeface.create("sans-serif-condensed",
						Typeface.NORMAL));
				tvFlightType.setPadding(5, 5, 5, 5);
				tvFlightType.setTextColor(Color.parseColor("#E60072bb"));
				tvFlightType.setBackgroundColor(Color.parseColor("#E0E0E0"));
				
				if (i == 1) {
					if (isRoundTrip)
						((TextView) vFlightItem
								.findViewById(R.id.tv_flight_type))
								.setText(context.getResources().getString(
										R.string.retur));
					tvFlightType.setText(context.getResources().getString(
							R.string.retur).toUpperCase());
				}

				allJourney = jarray.getJSONObject(i);
				listFlightArray = allJourney.getJSONArray("ListFlight");
				listFlight = listFlightArray.getJSONObject(0);

				ivFlightLogo = (ImageView) vFlightItem
						.findViewById(R.id.iv_flight_logo);
				try {
					// get input stream
					ims = context.getAssets().open(
							listFlight.getString("FlightLogo"));
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

				((TextView) vFlightItem.findViewById(R.id.tv_depart_time))
						.setText(listFlight.getString("DepartureTimeString"));

				String totalDuration = allJourney.getString("TotalDuration");

				if (allJourney.getInt("stops") == 0)
					stops = context.getResources().getString(R.string.non_stop);
				else if (allJourney.getInt("stops") == 1)
					stops = allJourney.getInt("stops")
							+ " "
							+ context.getResources().getString(
									R.string.one_stop);
				else
					stops = allJourney.getInt("stops")
							+ " "
							+ context.getResources().getString(
									R.string.more_stop);

				((TextView) vFlightItem.findViewById(R.id.tv_flight_time_stops))
						.setText(totalDuration + " " + stops);

				listFlight = listFlightArray.getJSONObject(listFlightArray
						.length() - 1);

				((TextView) vFlightItem.findViewById(R.id.tv_arrival_time))
						.setText(listFlight.getString("ArrivalTimeString"));

				String strFlightDetails = "";
				final LinearLayout llFlightDetails = (LinearLayout) convertView
						.findViewById(R.id.ll_flight_details_list);

				llFlightDetails.addView(tvFlightType);
				
				for (int j = 0; j < listFlightArray.length(); ++j) {
					 View vFlightDetails = mInflater.inflate(
							R.layout.item_flight_details, null);

					listFlight = listFlightArray.getJSONObject(j);

					strFlightDetails = context.getResources().getString(
							R.string.from)
							+ " "
							+ listFlight.getString("DepartureAirportName")
							+ " "
							+ context.getResources().getString(R.string.to)
							+ " " + listFlight.getString("ArrivalAirportName");
					((TextView) vFlightDetails
							.findViewById(R.id.tv_airport_details))
							.setText(strFlightDetails);

					strFlightDetails = listFlight.getString("FlightName")
							+ " | "
							+ listFlight.getString("EquipmentNumber")
							+ " | "
							+ context.getResources().getString(
									R.string.booking_class) + " : "
							+ listFlight.getString("BookingCode") + " | "
							+ context.getResources().getString(R.string.meals)
							+ " : " + listFlight.getString("MealCode");

					((TextView) vFlightDetails
							.findViewById(R.id.tv_flight_info))
							.setText(strFlightDetails);
					((TextView) vFlightDetails
							.findViewById(R.id.tv_depart_date))
							.setText(listFlight
									.getString("DepartureDateString"));
					((TextView) vFlightDetails
							.findViewById(R.id.tv_arrival_date))
							.setText(listFlight.getString("ArrivalDateString"));
					((TextView) vFlightDetails
							.findViewById(R.id.tv_depart_time))
							.setText(listFlight
									.getString("DepartureTimeString"));
					((TextView) vFlightDetails
							.findViewById(R.id.tv_arrival_time))
							.setText(listFlight.getString("ArrivalTimeString"));
					((TextView) vFlightDetails.findViewById(R.id.tv_duration))
							.setText(listFlight.getString("DurationPerLeg"));

					if (listFlightArray.length() == 1) {
					} else {
						if (listFlight.getString("TransitTime").equals("")) {
							((LinearLayout) vFlightDetails
									.findViewById(R.id.ll_transit_time))
									.setVisibility(View.GONE);
						} else {
							((TextView) vFlightDetails
									.findViewById(R.id.tv_transit_time))
									.setText(listFlight
											.getString("TransitTime"));
							((LinearLayout) vFlightDetails
									.findViewById(R.id.ll_transit_time))
									.setVisibility(View.VISIBLE);
						}
					}

					llFlightDetails.addView(vFlightDetails);
				}


				((TextView) convertView.findViewById(R.id.tv_details))
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								llFlightDetails.setVisibility(llFlightDetails
										.getVisibility() == View.GONE ? View.VISIBLE
										: View.GONE);
							}
						});

				if (blStart)
					llFlightResult.addView(vFlightItem);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return convertView;
	}

}
