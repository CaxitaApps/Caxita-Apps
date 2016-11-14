/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.travel.hjozzat;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.travel.hjozzat.R;
import com.travel.hjozzat.model.HotelResultItem;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

/**
 * This shows how to place markers on a map.
 */
public class MarkeActivity extends FragmentActivity implements
		OnMarkerClickListener, OnInfoWindowClickListener, OnMapReadyCallback {

	private static LatLng HOTEL = null;
	private static List<LatLng> positions = new ArrayList<LatLng>();

	/** Demonstrates customizing the info window and/or its contents. */
	class CustomInfoWindowAdapter implements InfoWindowAdapter {
		// These a both viewgroups containing an ImageView with id "badge" and
		// two TextViews with id
		// "title" and "snippet".
		private final View mWindow;
		private final View mContents;

		CustomInfoWindowAdapter() {
			mWindow = getLayoutInflater().inflate(R.layout.custom_info_window,
					null);
			mContents = getLayoutInflater().inflate(
					R.layout.custom_info_contents, null);
		}

		@Override
		public View getInfoWindow(Marker marker) {
			render(marker, mWindow);
			return mWindow;
		}

		@Override
		public View getInfoContents(Marker marker) {
			render(marker, mContents);
			return mContents;
		}

		private void render(Marker marker, View view) {

			String title = marker.getTitle();
			TextView titleUi = ((TextView) view.findViewById(R.id.title));
			if (title != null) {
				// Spannable string allows us to edit the formatting of the
				// text.
				SpannableString titleText = new SpannableString(title);
				titleText.setSpan(
						new ForegroundColorSpan(Color.parseColor("#0072BC")),
						0, titleText.length(), 0);
				titleUi.setText(titleText);
			} else {
				titleUi.setText("");
			}

			String snippet = marker.getSnippet();
			TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
			if (snippet != null && snippet.length() > 12) {
				SpannableString snippetText = new SpannableString(snippet);
				snippetText.setSpan(
						new ForegroundColorSpan(Color.parseColor("#0072BC")),
						0, 10, 0);
				snippetUi.setText(snippetText);
			} else {
				snippetUi.setText("");
			}
		}
	}

	private GoogleMap mMap;

	// private Marker mHotel;

	/**
	 * Keeps track of the last selected marker (though it may no longer be
	 * selected). This is useful for refreshing the info window.
	 */

	double lat, lan;
	boolean isSingle = false;

	String name, address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_marker);

		lat = getIntent().getExtras().getDouble("Latitude", 0.0);
		lan = getIntent().getExtras().getDouble("Langitude", 0.0);
		isSingle = getIntent().getBooleanExtra("isSingle", false);
		name = getIntent().getExtras().getString("HotelName", null);
		address = getIntent().getExtras().getString("HotelAddress", null);

		HOTEL = new LatLng(lat, lan);

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	public void clicker(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onMapReady(GoogleMap map) {
		mMap = map;

		// Hide the zoom controls as the button panel will cover it.
		mMap.getUiSettings().setZoomControlsEnabled(true);

		// // Add lots of markers to the map.
		if (isSingle)
			addMarkersToMap();
		else
			for (HotelResultItem hItem : HotelResultActivity.hotelResultItem) {
				LatLng position = new LatLng(hItem.strHotelLattitude,
						hItem.strHotelLongitude);
				positions.add(position);
				map.addMarker(new MarkerOptions()
						.position(position)
						.title(hItem.strHotelName)
						.snippet(hItem.strHotelAddress)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.ic_marker)));
				// .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
			}

		// Setting an info window adapter allows us to change the both the
		// contents and look of the
		// info window.
		mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

		// Set listeners for marker events. See the bottom of this class for
		// their behavior.
		mMap.setOnMarkerClickListener(this);
		mMap.setOnInfoWindowClickListener(this);
		// mMap.setOnMarkerDragListener(this);

		// Pan to see all markers in view.
		// Cannot zoom to bounds until the map has a size.
		final View mapView = getSupportFragmentManager().findFragmentById(
				R.id.map).getView();
		if (mapView.getViewTreeObserver().isAlive()) {
			mapView.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {
						@SuppressWarnings("deprecation")
						// We use the new method when supported
						@SuppressLint("NewApi")
						// We check which build version we are using.
						@Override
						public void onGlobalLayout() {
							LatLngBounds bounds = new LatLngBounds
								.Builder()
								.include((isSingle) ? HOTEL : positions.get(0))
								.build();
							
							if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
								mapView.getViewTreeObserver()
										.removeGlobalOnLayoutListener(this);
							} else {
								mapView.getViewTreeObserver()
										.removeOnGlobalLayoutListener(this);
							}
							mMap.moveCamera(CameraUpdateFactory
									.newLatLngBounds(bounds, 50));
							mMap.animateCamera(CameraUpdateFactory
									.zoomTo((isSingle) ? 16.0f : 10.0f));
						}
					});
		}
	}

	private void addMarkersToMap() {
		// Uses a colored icon.
		mMap.addMarker(new MarkerOptions()
				.position(HOTEL)
				.title(name)
				.snippet(address)
				.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.ic_marker)));
//				.icon(BitmapDescriptorFactory
//						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

		// Uses a custom icon with the info window popping out of the center of
		// the icon.
		// mSydney = mMap.addMarker(new MarkerOptions()
		// .position(SYDNEY)
		// .title("Sydney")
		// .snippet("Population: 4,627,300")
		// .icon(BitmapDescriptorFactory.fromResource(R.drawable.seek_thumb_normal))
		// .infoWindowAnchor(0.5f, 0.5f));
		//
		// // Creates a draggable marker. Long press to drag.
		// mMelbourne = mMap.addMarker(new MarkerOptions()
		// .position(MELBOURNE)
		// .title("Melbourne")
		// .snippet("Population: 4,137,400")
		// .draggable(true));
		//
		// // A few more markers for good measure.
		// mPerth = mMap.addMarker(new MarkerOptions()
		// .position(PERTH)
		// .title("Perth")
		// .snippet("Population: 1,738,800"));
		// mAdelaide = mMap.addMarker(new MarkerOptions()
		// .position(ADELAIDE)
		// .title("Adelaide")
		// .snippet("Population: 1,213,000"));

		// Creates a marker rainbow demonstrating how to create default marker
		// icons of different
		// hues (colors).
		// float rotation = mRotationBar.getProgress();
		// boolean flat = mFlatBox.isChecked();

		// int numMarkersInRainbow = 12;
		// for (int i = 0; i < numMarkersInRainbow; i++) {
		// mMarkerRainbow.add(mMap.addMarker(new MarkerOptions()
		// .position(new LatLng(
		// -30 + 10 * Math.sin(i * Math.PI / (numMarkersInRainbow - 1)),
		// 135 - 10 * Math.cos(i * Math.PI / (numMarkersInRainbow - 1))))
		// .title("Marker " + i)
		// .icon(BitmapDescriptorFactory.defaultMarker(i * 360 /
		// numMarkersInRainbow))
		// .flat(false)
		// .rotation(0)));
		// }
	}

	// private boolean checkReady() {
	// if (mMap == null) {
	// Toast.makeText(this, R.string.no_result, Toast.LENGTH_SHORT).show();
	// return false;
	// }
	// return true;
	// }

	// /** Called when the Clear button is clicked. */
	// public void onClearMap(View view) {
	// if (!checkReady()) {
	// return;
	// }
	// mMap.clear();
	// }
	//
	// /** Called when the Reset button is clicked. */
	// public void onResetMap(View view) {
	// if (!checkReady()) {
	// return;
	// }
	// // Clear the map because we don't want duplicates of the markers.
	// mMap.clear();
	// addMarkersToMap();
	// }

	// /** Called when the Reset button is clicked. */
	// public void onToggleFlat(View view) {
	// if (!checkReady()) {
	// return;
	// }
	// boolean flat = mFlatBox.isChecked();
	// for (Marker marker : mMarkerRainbow) {
	// marker.setFlat(flat);
	// }
	// }

	// @Override
	// public void onProgressChanged(SeekBar seekBar, int progress, boolean
	// fromUser) {
	// if (!checkReady()) {
	// return;
	// }
	// float rotation = seekBar.getProgress();
	// for (Marker marker : mMarkerRainbow) {
	// marker.setRotation(rotation);
	// }
	// }

	// @Override
	// public void onStartTrackingTouch(SeekBar seekBar) {
	// // Do nothing.
	// }

	// @Override
	// public void onStopTrackingTouch(SeekBar seekBar) {
	// // Do nothing.
	// }

	//
	// Marker related listeners.
	//

	@Override
	public boolean onMarkerClick(final Marker marker) {

		// if (marker.equals(mPerth)) {
		// // This causes the marker at Perth to bounce into position when it is
		// clicked.
		// final Handler handler = new Handler();
		// final long start = SystemClock.uptimeMillis();
		// final long duration = 1500;
		//
		// final Interpolator interpolator = new BounceInterpolator();
		//
		// handler.post(new Runnable() {
		// @Override
		// public void run() {
		// long elapsed = SystemClock.uptimeMillis() - start;
		// float t = Math.max(
		// 1 - interpolator.getInterpolation((float) elapsed / duration), 0);
		// marker.setAnchor(0.5f, 1.0f + 2 * t);
		//
		// if (t > 0.0) {
		// // Post again 16ms later.
		// handler.postDelayed(this, 16);
		// }
		// }
		// });
		// } else if (marker.equals(mAdelaide)) {
		// // This causes the marker at Adelaide to change color and alpha.
		// marker.setIcon(BitmapDescriptorFactory.defaultMarker(mRandom.nextFloat()
		// * 360));
		// marker.setAlpha(mRandom.nextFloat());
		// }

		// mLastSelectedMarker = marker;
		// We return false to indicate that we have not consumed the event and
		// that we wish
		// for the default behavior to occur (which is for the camera to move
		// such that the
		// marker is centered and for the marker's info window to open, if it
		// has one).
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		// Toast.makeText(this, "Click Info Window", Toast.LENGTH_SHORT).show();

		if (marker.isInfoWindowShown())
			marker.hideInfoWindow();

	}

	// @Override
	// public void onMarkerDragStart(Marker marker) {
	// // mTopText.setText("onMarkerDragStart");
	// }

	// @Override
	// public void onMarkerDragEnd(Marker marker) {
	// // mTopText.setText("onMarkerDragEnd");
	// }

	// @Override
	// public void onMarkerDrag(Marker marker) {
	// // mTopText.setText("onMarkerDrag.  Current Position: " +
	// marker.getPosition());
	// }
}
