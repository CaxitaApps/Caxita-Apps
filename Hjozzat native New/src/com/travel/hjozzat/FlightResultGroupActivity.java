package com.travel.hjozzat;

import java.util.ArrayList;

import com.hjozzat.support.CommonFunctions;
import com.travel.hjozzat.adapter.FlightResultAdapter;
import com.travel.hjozzat.model.FlightResultItem;
import com.travel.hjozzat.FlightResultActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class FlightResultGroupActivity extends Activity implements
OnItemClickListener {

	static ListView lvFlightResult;
	
	ArrayList<FlightResultItem> groupedResult;
	
	String strPrice;
	
	boolean isRoundTrip;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.activity_flight_result_group);
		setHeader();
		
		strPrice = getIntent().getExtras().getString("price");
		isRoundTrip = getIntent().getBooleanExtra("isRoundTrip", false);
		
		groupResult();
		
		lvFlightResult = (ListView) findViewById(R.id.lv_flight_result);
		
		lvFlightResult.setAdapter(new FlightResultAdapter(
				FlightResultGroupActivity.this, groupedResult,isRoundTrip, true));
		
		lvFlightResult.setOnItemClickListener(this);
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
		}
	}
	
	private void groupResult() {
		groupedResult = new ArrayList<FlightResultItem>();
		for (FlightResultItem fitem : FlightResultActivity.flightResultItem) {
			if(String.valueOf(fitem.doubleFlightPrice).equalsIgnoreCase(strPrice)) {
				groupedResult.add(fitem);
			}
			
		}
	}
	
	public static FlightResultItem selectedFItem;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		selectedFItem = (FlightResultItem) lvFlightResult
				.getItemAtPosition(position);

		Intent details = new Intent(FlightResultGroupActivity.this,
				FlightPaxActivity.class);
		details.putExtra("sID", FlightResultActivity.strSessionId+CommonFunctions.getRandomString(4));
		details.putExtra("isRound", isRoundTrip);
		details.putExtra("isGroup", true);
		details.putExtra("tripID", selectedFItem.strTripId);
		startActivity(details);

	}

	
}
