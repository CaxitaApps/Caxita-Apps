package com.travel.hjozzat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.hjozzat.support.CommonFunctions;
import com.hjozzat.support.CustomImageView;
import com.hjozzat.support.ImageDownloader;
import com.hjozzat.support.RangeSeekBar;
import com.hjozzat.support.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.travel.hjozzat.R;
import com.travel.hjozzat.adapter.HotelResultAdapter;
import com.travel.hjozzat.model.HotelResultItem;

public class HotelResultActivity extends Activity {

	private Locale myLocale;
	public static String strCity, strCheckinDate, strCheckoutDate;
	public static int resultCount, passengerCount, roomCount;
	int starCount_0 = 0, starCount_1 = 0, starCount_2 = 0, starCount_3 = 0,
			starCount_4 = 0, starCount_5 = 0;

	ImageView ivClearHotelName;
	TextView tvProgressText, tvCurrency;
	// ScrollView svResult;
	EditText etFilterHotelName;
	static LinearLayout llSort, llMap;
	ListView lvHotelResult;
	LinearLayout llSortLayout, llFilterLayout, llSortDialogLayout;
	public static ArrayList<HotelResultItem> hotelResultItem;
	private static ArrayList<HotelResultItem> hotelResultItemTemp,
			filteredResult;
	private static ArrayList<String> arrayBoardTypes;
	// , arrayCheckedBoardtypes;
	String str_url = "";
	String main_url = "";
	Dialog loaderDialog, dialogSort, curr, dialogFilter;

	// boolean for sort
	Boolean blSortPrice = false, blSortRating = true, blSortHotelName = false;
	String strSortPriceType = "Low", strSortRatingType = "High",
			strSortHotelNameType = "Low";

	// boolean for filter
	Boolean blFilterPrice = false, blFilterName = false,
			blFilterBoardTypes = false, blHasBreakfast = false;;
	Double filterMinPrice, filterMaxPrice;
	Boolean blAllStar = true, blNoStar = false, blOneStar = false,
			blTwoStar = false, blThreeStar = false, blFourStar = false,
			blFiveStar = false;
	String strSearchName = "";
	String strSessionId = null;
	boolean blCurr = false;

	public static LruCache<String, Bitmap> mMemoryCache;
	public static Bitmap bmp = null;
	CommonFunctions cf;

	String[] sortText, sortHeading;

	public static Activity activityHotelResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		activityHotelResult = this;

		getActionBar().hide();
		loadLocale();
		setContentView(R.layout.activity_search_result_hotel);

		initialize();
		getIntentValues();
		setViewValues();

		if (CommonFunctions.modify) {
			strSessionId = CommonFunctions.strSearchId;
			hotelResultItem.addAll(CommonFunctions.HotelResult);
			lvHotelResult.setAdapter(new HotelResultAdapter(
					HotelResultActivity.this, hotelResultItem, strSessionId));
			CommonFunctions.modify = false;
			CommonFunctions.strSearchId = null;
			CommonFunctions.HotelResult.clear();
			for (HotelResultItem hItem : hotelResultItem) {
				if (!arrayBoardTypes.contains(hItem.strBoardTypes)
						&& !hItem.strBoardTypes.equalsIgnoreCase(""))
					arrayBoardTypes.add(hItem.strBoardTypes);
				if (hItem.strBoardTypes.toLowerCase().contains("breakfast")
						|| hItem.strBoardTypes.toLowerCase()
								.contains("الافطار")
						|| hItem.strBoardTypes.toLowerCase().contains("افطار"))
					blHasBreakfast = true;
			}
			setDefaultValues();
			printImage(hotelResultItem);
		} else {
			if (!str_url.equalsIgnoreCase(""))
				new backMethod().execute("");
			else
				finishAffinity();
		}

		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}
		};

	}

	private void initialize() {
		// TODO Auto-generated method stub
		cf = new CommonFunctions(this);

		loaderDialog = new Dialog(HotelResultActivity.this,
				android.R.style.Theme_Translucent);
		loaderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loaderDialog.getWindow().setGravity(Gravity.TOP);
		loaderDialog.setContentView(R.layout.dialog_loader);
		loaderDialog.setCancelable(false);

		((ImageView) loaderDialog.findViewById(R.id.iv_loader))
				.setImageResource(R.drawable.hotel_loader);

		tvProgressText = (TextView) loaderDialog
				.findViewById(R.id.tv_progress_text);
		tvProgressText.setVisibility(View.VISIBLE);
		tvProgressText.setText(getResources().getString(
				R.string.searching_hotel));

		dialogSort = new Dialog(HotelResultActivity.this,
				android.R.style.Theme_Translucent);
		dialogSort.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSort.setContentView(R.layout.dialog_sort);

		llSortDialogLayout = (LinearLayout) dialogSort
				.findViewById(R.id.ll_sort_dialog_layout);

		llSortDialogLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogSort.dismiss();
			}
		});

		dialogFilter = new Dialog(this, android.R.style.Theme_Translucent);
		dialogFilter.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogFilter.getWindow().setGravity(Gravity.TOP);
		dialogFilter.setContentView(R.layout.dialog_filter_hotel);
		dialogFilter.setCancelable(false);
		
		llSort = (LinearLayout) dialogSort.findViewById(R.id.ll_sort_options);
		sortText = getResources().getStringArray(R.array.sort_items_hotel);
		sortHeading = getResources().getStringArray(
				R.array.sort_item_hotel_heading);

		tvCurrency = (TextView) findViewById(R.id.tv_currency);
		tvCurrency.setText(CommonFunctions.strCurrency);

		lvHotelResult = (ListView) findViewById(R.id.lv_hotel_result);
		llSortLayout = (LinearLayout) findViewById(R.id.ll_sort);
		llFilterLayout = (LinearLayout) findViewById(R.id.ll_filter);
		etFilterHotelName = (EditText) findViewById(R.id.et_filter_hotel_name);
		ivClearHotelName = (ImageView) findViewById(R.id.iv_clear_name);
		hotelResultItem = new ArrayList<HotelResultItem>();
		hotelResultItemTemp = new ArrayList<HotelResultItem>();
		filteredResult = new ArrayList<HotelResultItem>();
		arrayBoardTypes = new ArrayList<String>();
		// arrayCheckedBoardtypes = new ArrayList<String>();

		main_url = CommonFunctions.main_url + CommonFunctions.lang
				+ "/HotelApi/HotelApiSearch/";

		etFilterHotelName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				filteredResult.clear();
				String filter = s.toString().toLowerCase();
				for (HotelResultItem listItem : hotelResultItem) {
					if (listItem.strHotelName.toLowerCase().contains(filter)) {
						filteredResult.add(listItem);
					}
				}
				if (s.length() == 0) {
					blFilterName = false;
					ivClearHotelName.setVisibility(View.GONE);
				} else {
					blFilterName = true;
					strSearchName = filter;
					ivClearHotelName.setVisibility(View.VISIBLE);

					// boolean for sort
					blSortPrice = true;
					blSortRating = false;
					blSortHotelName = false;
					strSortPriceType = "Low";
					strSortRatingType = "Low";
					strSortHotelNameType = "Low";

					// boolean for filter
					blFilterPrice = false;
					blFilterBoardTypes = false;
					blNoStar = false;
					blOneStar = false;
					blTwoStar = false;
					blThreeStar = false;
					blFourStar = false;
					blFiveStar = false;
					// arrayCheckedBoardtypes.clear();

				}

				lvHotelResult
						.setAdapter(new HotelResultAdapter(
								HotelResultActivity.this, filteredResult,
								strSessionId));
				printImage(filteredResult);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_no_image);
	}

	private void getIntentValues() {
		// TODO Auto-generated method stub
		str_url = getIntent().getExtras().getString("url", "");
		str_url = str_url + "?searchID=";
		strCity = getIntent().getExtras().getString("city", "");
		strCheckinDate = getIntent().getExtras().getString("checkinDate", "");
		strCheckoutDate = getIntent().getExtras().getString("checkoutDate", "");
		passengerCount = getIntent().getExtras().getInt("passengers");
		roomCount = getIntent().getExtras().getInt("roomCount");
	}

	private void setViewValues() {
		TextView tvCity = (TextView) findViewById(R.id.tv_Hotel_city);
		TextView tvCheckinDate = (TextView) findViewById(R.id.tv_checkin_date);
		TextView tvCheckoutDate = (TextView) findViewById(R.id.tv_checkout_date);
		TextView tvpassengerCount = (TextView) findViewById(R.id.tv_passenger_count);
		TextView tvroomCount = (TextView) findViewById(R.id.tv_room_count);
		tvCity.setText(strCity);
		tvCheckinDate.setText(strCheckinDate);
		tvCheckoutDate.setText(strCheckoutDate);
		tvpassengerCount.setText(String.valueOf(passengerCount));
		tvroomCount.setText(String.valueOf(roomCount));
	}

	public void clicker(View v) {
		switch (v.getId()) {
		case R.id.rl_back:
			finish();
			break;
		case R.id.ll_map:
			Intent marker = new Intent(HotelResultActivity.this, MarkeActivity.class);
			marker.putExtra("HotelName", 	"");
			marker.putExtra("HotelAddress", "");
			marker.putExtra("Latitude", 	0.0);
			marker.putExtra("Langitude", 	0.0);
			marker.putExtra("isSingle", false);
			startActivity(marker);
			break;
		case R.id.ll_filter:
			showFilterDialog();
			break;
		case R.id.ll_sort:
			showSortDialog();
			break;

		case R.id.ll_currency:
			dialogCurrency();
			break;

		case R.id.iv_clear_name:
			ivClearHotelName.setVisibility(View.GONE);
			etFilterHotelName.setText(null);
			blFilterName = false;
			// boolean for sort
			blSortPrice = true;
			blSortRating = false;
			blSortHotelName = false;
			strSortPriceType = "Low";
			strSortRatingType = "Low";
			strSortHotelNameType = "Low";

			// boolean for filter
			blFilterPrice = false;
			blFilterName = false;
			blFilterBoardTypes = false;
			blNoStar = false;
			blOneStar = false;
			blTwoStar = false;
			blThreeStar = false;
			blFourStar = false;
			blFiveStar = false;
			strSearchName = "";
			// arrayCheckedBoardtypes.clear();

			lvHotelResult.setAdapter(new HotelResultAdapter(
					HotelResultActivity.this, hotelResultItem, strSessionId));
			printImage(hotelResultItem);
		default:
			break;
		}
	}

	private class backMethod extends AsyncTask<String, String, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			hotelResultItem.clear();
			hotelResultItemTemp.clear();

			loaderDialog.show();
			llSortLayout.setEnabled(false);
			llFilterLayout.setEnabled(false);
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			if (params[0].isEmpty()) {
				String resultString = makePostRequest(false, "");
				if (resultString != null)
					parseHotelResult(resultString);
			} else if (!params[0].equalsIgnoreCase(CommonFunctions.strCurrency)) {
				String resultString = makePostRequest(true, params[0]);
				if (resultString != null) {
					JSONObject jObj;
					try {
						CommonFunctions.strCurrency = params[0];
						jObj = new JSONObject(resultString);
						jObj = jObj.getJSONObject("data");
						parseHotelResult(jObj.getString("Item"));
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
			if (hotelResultItem.size() > 0) {
				blCurr = false;
				tvCurrency.setText(CommonFunctions.strCurrency);
				// printImage(hotelResultItem);
				// printResult(hotelResultItem);
				System.out
						.println("------------------Finished displaying-------------");
				// startRepeatingTask();
				setDefaultValues();
				llSortLayout.setEnabled(true);
				llFilterLayout.setEnabled(true);
				new sort().execute();
			} else {
				// noResultAlert(false,
				// getResources().getString(R.string.no_result));
				((LinearLayout) findViewById(R.id.ll_filter)).setEnabled(false);
				((LinearLayout) findViewById(R.id.ll_sort)).setEnabled(false);
			}
			if (loaderDialog.isShowing())
				loaderDialog.dismiss();
		}

	}

	public void setDefaultValues() {
		hotelResultItemTemp.addAll(hotelResultItem);
		filterMinPrice = (hotelResultItem).get(0).doubleHotelDisplayRate;
		filterMaxPrice = (hotelResultItem).get(hotelResultItem.size() - 1).doubleHotelDisplayRate;

		for (HotelResultItem hItem : hotelResultItem) {
			switch ((int) hItem.floatHotelRating) {
			case 0:
				starCount_0++;
				break;
			case 1:
				starCount_1++;
				break;
			case 2:
				starCount_2++;
				break;
			case 3:
				starCount_3++;
				break;
			case 4:
				starCount_4++;
				break;
			case 5:
				starCount_5++;
				break;
			default:
				break;
			}
		}
	}

	private String makePostRequest(boolean blCurr, String strCurrency) {

		// making POST request.
		try {
			URL url = null;
			if (blCurr) {
				url = new URL(CommonFunctions.main_url + CommonFunctions.lang
						+ "/HotelApi/CurrencyConvert?currency=" + strCurrency
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

	private void parseHotelResult(String result) {
		try {
			if (result != null) {
				JSONArray jarray = null;
				// Parse String to JSON object
				jarray = new JSONArray(result);

				if (jarray.length() == 0) {
					handler.sendEmptyMessage(3);
				} else {
					JSONObject hotelObj = null, allHotels = null;
					HotelResultItem hItem = null;

					hotelObj = jarray.getJSONObject(0);
					// URL url;
					JSONArray allHotelsArray = hotelObj.getJSONArray("Hotels");

					resultCount = allHotelsArray.length();
					// if(length > 100)
					// length = 100;

					for (int j = 0; j < resultCount; j++) {
						allHotels = allHotelsArray.getJSONObject(j);
						hItem = new HotelResultItem();
						hItem.strHotelName = allHotels.getString("HotelName");
						hItem.strHotelDescription = allHotels
								.getString("HotelDescription");
						hItem.strHotelAddress = allHotels
								.getString("HotelAddress");
						try {
							if (!allHotels.getString("HotelRating").equals(""))
								hItem.floatHotelRating = Float
										.parseFloat(allHotels
												.getString("HotelRating"));
						} catch (Exception e) {

							e.printStackTrace();
						}
						hItem.strHotelThumbImage = allHotels
								.getString("HotelThumbImage");
						hItem.strDisplayRate = allHotels
								.getString("HotelDisplayRate");
						try {
							if (!allHotels.getString("HotelLattitude").equals(
									""))
								hItem.strHotelLattitude = Double
										.parseDouble(allHotels
												.getString("HotelLattitude"));
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							if (!allHotels.getString("HotelLongitude").equals(
									""))
								hItem.strHotelLongitude = Double
										.parseDouble(allHotels
												.getString("HotelLongitude"));
						} catch (Exception e) {
							e.printStackTrace();
						}

						hItem.doubleHotelDisplayRate = Double
								.parseDouble(allHotels
										.getString("HotelDisplayRate"));
						hItem.strHotelLocation = allHotels
								.getString("HotelLocation");
						hItem.strDeepLinkUrl = allHotels
								.getString("DeepLinkUrl");
						hItem.strBoardTypes = allHotels.getString("BoardTypes")
								.toUpperCase();
						hItem.strNativeDeepUrl = allHotels
								.getString("NativeDeepLinkUrl");
						// hItem.strLastBooked =
						// allHotels.getString("LastBooked");
						// hItem.strWatchingCount =
						// allHotels.getString("watchingCount");

						if (!arrayBoardTypes.contains(hItem.strBoardTypes)
								&& !hItem.strBoardTypes.equalsIgnoreCase(""))
							arrayBoardTypes.add(hItem.strBoardTypes);

						if (hItem.strBoardTypes.toLowerCase().contains(
								"breakfast")
								|| hItem.strBoardTypes.toLowerCase().contains(
										"الافطار")
								|| hItem.strBoardTypes.toLowerCase().contains(
										"افطار"))
							blHasBreakfast = true;
						// url = new URL(hItem.strHotelThumbImage);
						// try {
						// hItem.imgHotelLogo =
						// BitmapFactory.decodeStream(url.openConnection().getInputStream());
						// } catch(NullPointerException e) {
						// } catch (Exception e) {
						// Log.w("ImageDownloader",
						// "Error downloading image from " + url);
						// }

						hotelResultItem.add(hItem);
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

	private void showSortDialog() {
		llSort.removeAllViews();
		int i;
		for (i = 0; i < sortText.length; ++i) {
			final View view = getLayoutInflater().inflate(
					R.layout.sort_list_item, null);
			if (i % 2 == 0) {
				final TextView tvView = (TextView) getLayoutInflater().inflate(
						R.layout.tv_autocomplete, null);
				tvView.setText(sortHeading[i / 2]);
				llSort.addView(tvView);
			}
			final RadioButton rb = (RadioButton) view
					.findViewById(R.id.rb_sort_item);
			rb.setClickable(false);
			((TextView) view.findViewById(R.id.tv_sort_item_name))
					.setText(sortText[i]);

			switch (i) {
			case 0:
				rb.setChecked(blSortPrice
						&& strSortPriceType.equalsIgnoreCase("Low") ? true
						: false);
				break;
			case 1:
				rb.setChecked(blSortPrice
						&& strSortPriceType.equalsIgnoreCase("High") ? true
						: false);
				break;
			case 2:
				rb.setChecked(blSortRating
						&& strSortRatingType.equalsIgnoreCase("Low") ? true
						: false);
				break;
			case 3:
				rb.setChecked(blSortRating
						&& strSortRatingType.equalsIgnoreCase("High") ? true
						: false);
				break;
			case 4:
				rb.setChecked(blSortHotelName
						&& strSortHotelNameType.equalsIgnoreCase("Low") ? true
						: false);
				break;
			case 5:
				rb.setChecked(blSortHotelName
						&& strSortHotelNameType.equalsIgnoreCase("High") ? true
						: false);
				break;
			}

			view.setId(i);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (!rb.isChecked()) {
						rb.setChecked(true);

						switch (view.getId()) {

						case 0:
							blSortPrice = true;
							blSortRating = false;
							blSortHotelName = false;
							strSortPriceType = "Low";
							break;

						case 1:
							blSortPrice = true;
							blSortRating = false;
							blSortHotelName = false;
							strSortPriceType = "High";
							break;

						case 2:
							blSortPrice = false;
							blSortRating = true;
							blSortHotelName = false;
							strSortRatingType = "Low";
							break;

						case 3:
							blSortPrice = false;
							blSortRating = true;
							blSortHotelName = false;
							strSortRatingType = "High";
							break;

						case 4:
							blSortPrice = false;
							blSortRating = false;
							blSortHotelName = true;
							strSortHotelNameType = "Low";
							break;

						case 5:
							blSortPrice = false;
							blSortRating = false;
							blSortHotelName = true;
							strSortHotelNameType = "High";
							break;

						}
						new sort()
								.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
					dialogSort.dismiss();
				}
			});
			llSort.addView(view);

		}
		dialogSort.show();

	}

	boolean reset;

	private void showFilterDialog() {
		reset = false;

		final LinearLayout llTabRating, llTabPriceRange;
		final TextView tvTabRating, tvTabPriceRange;
		final ImageView ivTabRating, ivTabPriceRange;
		final ScrollView svRating;
		final LinearLayout llPriceRange;
		final LinearLayout llRangeBar;
		final RelativeLayout rlClose;
		final TextView tvPriceRangeMin, tvPriceRangeMax;
		final TextView tvStar0, tvStar1, tvStar2, tvStar3, tvStar4, tvStar5, tvAll;
		final CheckBox cbStar0, cbStar1, cbStar2, cbStar3, cbStar4, cbStar5, cbAll;
		final LinearLayout llApply, llReset;

		final Double minValuePrice = filterMinPrice;
		final Double maxValuePrice = filterMaxPrice;
		final boolean blAllStarTemp = blAllStar;
		final Boolean blNoStarTemp = blNoStar;
		final Boolean blOneStarTemp = blOneStar;
		final Boolean blTwoStarTemp = blTwoStar;
		final Boolean blThreeStarTemp = blThreeStar;
		final Boolean blFourStarTemp = blFourStar;
		final Boolean blFiveStarTemp = blFiveStar;
		final Boolean blFilterPriceTemp = blFilterPrice;

		llTabRating = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_tab_rating);
		llTabPriceRange = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_tab_price);
		tvTabRating = (TextView) dialogFilter
				.findViewById(R.id.tv_filter_rating);
		tvTabPriceRange = (TextView) dialogFilter
				.findViewById(R.id.tv_filter_price);
		ivTabRating = (ImageView) dialogFilter
				.findViewById(R.id.iv_filter_rating);
		ivTabPriceRange = (ImageView) dialogFilter
				.findViewById(R.id.iv_filter_price);
		svRating = (ScrollView) dialogFilter
				.findViewById(R.id.sv_filter_rating);
		llPriceRange = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_filter_price_range);
		llApply = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_apply_filter);
		llReset = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_reset_filter);
		rlClose = (RelativeLayout) dialogFilter.findViewById(R.id.rl_back);
		llRangeBar = (LinearLayout) dialogFilter
				.findViewById(R.id.ll_range_bar);
		tvPriceRangeMin = (TextView) dialogFilter
				.findViewById(R.id.tv_range_min);
		tvPriceRangeMax = (TextView) dialogFilter
				.findViewById(R.id.tv_range_max);

		cbAll = (CheckBox) dialogFilter.findViewById(R.id.cb_rating_all);
		cbStar0 = (CheckBox) dialogFilter.findViewById(R.id.cb_rating_0);
		cbStar1 = (CheckBox) dialogFilter.findViewById(R.id.cb_rating_1);
		cbStar2 = (CheckBox) dialogFilter.findViewById(R.id.cb_rating_2);
		cbStar3 = (CheckBox) dialogFilter.findViewById(R.id.cb_rating_3);
		cbStar4 = (CheckBox) dialogFilter.findViewById(R.id.cb_rating_4);
		cbStar5 = (CheckBox) dialogFilter.findViewById(R.id.cb_rating_5);

		tvStar0 = (TextView) dialogFilter.findViewById(R.id.tv_0star_count);
		tvStar1 = (TextView) dialogFilter.findViewById(R.id.tv_1star_count);
		tvStar2 = (TextView) dialogFilter.findViewById(R.id.tv_2star_count);
		tvStar3 = (TextView) dialogFilter.findViewById(R.id.tv_3star_count);
		tvStar4 = (TextView) dialogFilter.findViewById(R.id.tv_4star_count);
		tvStar5 = (TextView) dialogFilter.findViewById(R.id.tv_5star_count);
		tvAll = (TextView) dialogFilter.findViewById(R.id.tv_allstar_count);

		cbAll.setChecked(blAllStar);
		cbStar0.setChecked(blNoStar);
		cbStar1.setChecked(blOneStar);
		cbStar2.setChecked(blTwoStar);
		cbStar3.setChecked(blThreeStar);
		cbStar4.setChecked(blFourStar);
		cbStar5.setChecked(blFiveStar);

		tvStar0.setText(String.valueOf(starCount_0));
		tvStar1.setText(String.valueOf(starCount_1));
		tvStar2.setText(String.valueOf(starCount_2));
		tvStar3.setText(String.valueOf(starCount_3));
		tvStar4.setText(String.valueOf(starCount_4));
		tvStar5.setText(String.valueOf(starCount_5));
		tvAll.setText(String.valueOf(resultCount));

		if (blNoStar || blOneStar || blTwoStar || blThreeStar || blFourStar
				|| blFiveStar || blFilterPrice || blFilterName
				|| blFilterBoardTypes)
			llReset.setBackgroundResource(R.drawable.buttonshape);
		else
			llReset.setBackgroundColor(Color.parseColor("#1CA4FF"));

		llTabRating.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llTabRating.setBackgroundColor(Color.WHITE);
				llTabPriceRange.setBackgroundColor(Color.TRANSPARENT);
				svRating.setVisibility(View.VISIBLE);
				llPriceRange.setVisibility(View.GONE);
				tvTabRating.setTextColor(Color.parseColor("#0072BC"));
				tvTabPriceRange.setTextColor(Color.BLACK);
				ivTabRating.setImageResource(R.drawable.star_blue);
				ivTabPriceRange.setImageResource(R.drawable.ic_price_black);
			}
		});

		llTabPriceRange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llTabRating.setBackgroundColor(Color.TRANSPARENT);
				llTabPriceRange.setBackgroundColor(Color.WHITE);
				svRating.setVisibility(View.GONE);
				llPriceRange.setVisibility(View.VISIBLE);
				tvTabRating.setTextColor(Color.BLACK);
				tvTabPriceRange.setTextColor(Color.parseColor("#0072BC"));
				ivTabRating.setImageResource(R.drawable.star_black);
				ivTabPriceRange.setImageResource(R.drawable.ic_price_blue);
			}
		});

		llTabRating.performClick();

		rlClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!reset) {
					filterMinPrice = minValuePrice;
					filterMaxPrice = maxValuePrice;
					blAllStar = blAllStarTemp;
					blNoStar = blNoStarTemp;
					blOneStar = blOneStarTemp;
					blTwoStar = blTwoStarTemp;
					blThreeStar = blThreeStarTemp;
					blFourStar = blFourStarTemp;
					blFiveStar = blFiveStarTemp;
					blFilterPrice = blFilterPriceTemp;
				}

				dialogFilter.dismiss();

			}
		});

		final RangeSeekBar<Double> priceBar = new RangeSeekBar<Double>(this);
		// Set the range
		priceBar.setRangeValues(
				(hotelResultItem).get(0).doubleHotelDisplayRate,
				(hotelResultItem).get(hotelResultItem.size() - 1).doubleHotelDisplayRate);
		priceBar.setSelectedMinValue(filterMinPrice);
		priceBar.setSelectedMaxValue(filterMaxPrice);
		String price = String.format(new Locale("en"), "%.3f", filterMinPrice);
		tvPriceRangeMin.setText(CommonFunctions.strCurrency + " " + price);
		price = String.format(new Locale("en"), "%.3f", filterMaxPrice);
		tvPriceRangeMax.setText(CommonFunctions.strCurrency + " " + price);
		priceBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Double>() {

			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
					Double minValue, Double maxValue) {
				// handle changed range values
				String price = String
						.format(new Locale("en"), "%.3f", minValue);
				tvPriceRangeMin.setText(CommonFunctions.strCurrency + " "
						+ price);
				price = String.format(new Locale("en"), "%.3f", maxValue);
				tvPriceRangeMax.setText(CommonFunctions.strCurrency + " "
						+ price);
				filterMinPrice = minValue;
				filterMaxPrice = maxValue;
				if (filterMinPrice.equals((hotelResultItem).get(0).doubleHotelDisplayRate)
						&& filterMaxPrice.equals((hotelResultItem)
								.get(hotelResultItem.size() - 1).doubleHotelDisplayRate))
					blFilterPrice = false;
				else
					blFilterPrice = true;

				System.out.println("filterMinPrice:"
						+ filterMinPrice
						+ "\nfilterMaxPrice:"
						+ filterMaxPrice
						+ "\n(hotelResultItem).get(0).doubleHotelDisplayRate:"
						+ (hotelResultItem).get(0).doubleHotelDisplayRate
						+ "\n(hotelResultItem).get(hotelResultItem.size()-1).doubleHotelDisplayRate"
						+ (hotelResultItem).get(hotelResultItem.size() - 1).doubleHotelDisplayRate);

				if (!blAllStar || blFilterPrice || blFilterName
						|| blFilterBoardTypes)
					llReset.setBackgroundResource(R.drawable.buttonshape);
				else
					llReset.setBackgroundColor(Color.parseColor("#1CA4FF"));
			}
		});
		llRangeBar.addView(priceBar);
		priceBar.setNotifyWhileDragging(true);

		if (filterMinPrice == filterMaxPrice)
			priceBar.setEnabled(false);

		OnClickListener cbListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				switch (v.getId()) {
				case R.id.ll_rating_0:
					if (blNoStar)
						blNoStar = false;
					else
						blNoStar = true;
					cbStar0.setChecked(blNoStar);
					if (blNoStar || blOneStar || blTwoStar || blThreeStar
							|| blFourStar || blFiveStar)
						blAllStar = false;
					else
						blAllStar = true;
					cbAll.setChecked(blAllStar);
					break;
				case R.id.ll_rating_1:
					if (blOneStar)
						blOneStar = false;
					else
						blOneStar = true;
					cbStar1.setChecked(blOneStar);
					if (blNoStar || blOneStar || blTwoStar || blThreeStar
							|| blFourStar || blFiveStar)
						blAllStar = false;
					else
						blAllStar = true;
					cbAll.setChecked(blAllStar);
					break;
				case R.id.ll_rating_2:
					if (blTwoStar)
						blTwoStar = false;
					else
						blTwoStar = true;
					cbStar2.setChecked(blTwoStar);
					if (blNoStar || blOneStar || blTwoStar || blThreeStar
							|| blFourStar || blFiveStar)
						blAllStar = false;
					else
						blAllStar = true;
					cbAll.setChecked(blAllStar);
					break;
				case R.id.ll_rating_3:
					if (blThreeStar)
						blThreeStar = false;
					else
						blThreeStar = true;
					cbStar3.setChecked(blThreeStar);
					if (blNoStar || blOneStar || blTwoStar || blThreeStar
							|| blFourStar || blFiveStar)
						blAllStar = false;
					else
						blAllStar = true;
					cbAll.setChecked(blAllStar);
					break;
				case R.id.ll_rating_4:
					if (blFourStar)
						blFourStar = false;
					else
						blFourStar = true;
					cbStar4.setChecked(blFourStar);
					if (blNoStar || blOneStar || blTwoStar || blThreeStar
							|| blFourStar || blFiveStar)
						blAllStar = false;
					else
						blAllStar = true;
					cbAll.setChecked(blAllStar);
					break;
				case R.id.ll_rating_5:
					if (blFiveStar)
						blFiveStar = false;
					else
						blFiveStar = true;
					cbStar5.setChecked(blFiveStar);
					if (blNoStar || blOneStar || blTwoStar || blThreeStar
							|| blFourStar || blFiveStar)
						blAllStar = false;
					else
						blAllStar = true;
					cbAll.setChecked(blAllStar);
					break;
				default:
				}

				if (!blAllStar || blFilterPrice || blFilterName
						|| blFilterBoardTypes)
					llReset.setBackgroundResource(R.drawable.buttonshape);
				else
					llReset.setBackgroundColor(Color.parseColor("#1CA4FF"));
			}
		};

		((LinearLayout) dialogFilter.findViewById(R.id.ll_rating_0))
				.setOnClickListener(cbListener);
		((LinearLayout) dialogFilter.findViewById(R.id.ll_rating_1))
				.setOnClickListener(cbListener);
		((LinearLayout) dialogFilter.findViewById(R.id.ll_rating_2))
				.setOnClickListener(cbListener);
		((LinearLayout) dialogFilter.findViewById(R.id.ll_rating_3))
				.setOnClickListener(cbListener);
		((LinearLayout) dialogFilter.findViewById(R.id.ll_rating_4))
				.setOnClickListener(cbListener);
		((LinearLayout) dialogFilter.findViewById(R.id.ll_rating_5))
				.setOnClickListener(cbListener);

		llApply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				filter();
				dialogFilter.dismiss();
			}
		});

		llReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				blFilterBoardTypes = false;
				blFilterPrice = false;
				blFilterName = false;
				filterMinPrice = minValuePrice;
				filterMaxPrice = maxValuePrice;
				blAllStar = true;
				blNoStar = false;
				blOneStar = false;
				blTwoStar = false;
				blThreeStar = false;
				blFourStar = false;
				blFiveStar = false;
				strSearchName = null;

				cbAll.setChecked(blAllStar);
				cbStar0.setChecked(blNoStar);
				cbStar1.setChecked(blOneStar);
				cbStar2.setChecked(blTwoStar);
				cbStar3.setChecked(blThreeStar);
				cbStar4.setChecked(blFourStar);
				cbStar5.setChecked(blFiveStar);

				priceBar.setRangeValues(
						(hotelResultItem).get(0).doubleHotelDisplayRate,
						(hotelResultItem).get(hotelResultItem.size() - 1).doubleHotelDisplayRate);
				filterMinPrice = (hotelResultItem).get(0).doubleHotelDisplayRate;
				filterMaxPrice = (hotelResultItem).get(hotelResultItem.size() - 1).doubleHotelDisplayRate;
				priceBar.setSelectedMinValue(filterMinPrice);
				priceBar.setSelectedMaxValue(filterMaxPrice);
				String price = String.format(new Locale("en"), "%.3f",
						filterMinPrice);
				tvPriceRangeMin.setText(CommonFunctions.strCurrency + " "
						+ price);
				price = String.format(new Locale("en"), "%.3f", filterMaxPrice);
				tvPriceRangeMax.setText(CommonFunctions.strCurrency + " "
						+ price);

				llReset.setBackgroundColor(Color.parseColor("#1CA4FF"));

				reset = true;

				new sort().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		});

		dialogFilter.show();
	}

	private class sort extends AsyncTask<Void, Void, String> {

		ArrayList<HotelResultItem> temp = new ArrayList<HotelResultItem>();

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			tvProgressText.setVisibility(View.GONE);
			loaderDialog.show();
			llSortLayout.setEnabled(false);
			llFilterLayout.setEnabled(false);
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			lvHotelResult.setAdapter(new HotelResultAdapter(
					HotelResultActivity.this, temp, strSessionId));
			printImage(temp);
			// temp.clear();
			llSortLayout.setEnabled(true);
			llFilterLayout.setEnabled(true);
			if (loaderDialog.isShowing()) {
				loaderDialog.dismiss();
				tvProgressText.setVisibility(View.VISIBLE);
			}
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub

			if (blNoStar || blOneStar || blTwoStar || blThreeStar || blFourStar
					|| blFiveStar || blFilterPrice || blFilterName
					|| blFilterBoardTypes) {
				temp.addAll(filteredResult);
			} else {
				temp.addAll(hotelResultItemTemp);
			}

			if (blSortPrice) {
				if (temp.size() > 0) {
					Collections.sort(temp, new Comparator<HotelResultItem>() {

						@Override
						public int compare(HotelResultItem lhs,
								HotelResultItem rhs) {
							// TODO Auto-generated method stub
							return (lhs.doubleHotelDisplayRate)
									.compareTo(rhs.doubleHotelDisplayRate);
						}
					});

					if (strSortPriceType.equalsIgnoreCase("high")) {
						Collections.reverse(temp);
					}
				}
			} else if (blSortRating) {
				if (temp.size() > 0) {
					quickSort(temp, 0, temp.size() - 1);
					if (strSortRatingType.equalsIgnoreCase("high")) {
						Collections.reverse(temp);
					}
				}
			} else {
				if (temp.size() > 0) {
					Collections.sort(temp, new Comparator<HotelResultItem>() {

						@Override
						public int compare(HotelResultItem lhs,
								HotelResultItem rhs) {
							// TODO Auto-generated method stub
							return lhs.strHotelName
									.compareToIgnoreCase(rhs.strHotelName);
						}
					});
					if (strSortHotelNameType.equalsIgnoreCase("high")) {
						Collections.reverse(temp);
					}
				}

			}
			return null;
		}

	}

	/* * This method implements in-place quicksort algorithm recursively. */
	private void quickSort(ArrayList<HotelResultItem> temp, int low, int high) {
		int i = low;
		int j = high;
		// pivot is middle index
		HotelResultItem pivot = temp.get(low + (high - low) / 2);

		// Divide into two arrays
		while (i <= j) {
			/**
			 * * As shown in above image, In each iteration, we will identify a
			 * * number from left side which is greater then the pivot value,
			 * and * a number from right side which is less then the pivot
			 * value. Once * search is complete, we can swap both numbers.
			 */
			while (temp.get(i).floatHotelRating < pivot.floatHotelRating) {
				i++;
			}

			while (temp.get(j).floatHotelRating > pivot.floatHotelRating) {
				j--;
			}

			if (i <= j) {
				Collections.swap(temp, i, j);
				// move index to next position on both sides
				i++;
				j--;
			}
		}
		// calls quickSort() method recursively
		if (low < j) {
			quickSort(temp, low, j);
		}
		if (i < high) {
			quickSort(temp, i, high);
		}
	}

	private void filter() {
		filteredResult = new ArrayList<HotelResultItem>();
		System.out.println("hotelResultItemTemp size"
				+ hotelResultItemTemp.size());
		if ((blNoStar || blOneStar || blTwoStar || blThreeStar || blFourStar || blFiveStar)
				&& !(blNoStar && blOneStar && blTwoStar && blThreeStar
						&& blFourStar && blFiveStar)) {
			for (HotelResultItem hitem : hotelResultItemTemp) {
				if (hitem.floatHotelRating == 0 && blNoStar)
					filteredResult.add(hitem);
				else if (hitem.floatHotelRating == 1 && blOneStar)
					filteredResult.add(hitem);
				else if (hitem.floatHotelRating == 2 && blTwoStar)
					filteredResult.add(hitem);
				else if (hitem.floatHotelRating == 3 && blThreeStar)
					filteredResult.add(hitem);
				else if (hitem.floatHotelRating == 4 && blFourStar)
					filteredResult.add(hitem);
				else if (hitem.floatHotelRating == 5 && blFiveStar)
					filteredResult.add(hitem);
			}
		} else {
			filteredResult.addAll(hotelResultItemTemp);
		}

		if (blFilterPrice) {
			ArrayList<HotelResultItem> tempArray = new ArrayList<HotelResultItem>();
			for (HotelResultItem hitem : filteredResult) {
				if (hitem.doubleHotelDisplayRate >= filterMinPrice
						&& hitem.doubleHotelDisplayRate <= filterMaxPrice) {
					tempArray.add(hitem);
				}
			}
			filteredResult.clear();
			filteredResult.addAll(tempArray);
			tempArray.clear();
		}

		if (blFilterName) {
			ArrayList<HotelResultItem> tempArray = new ArrayList<HotelResultItem>();
			for (HotelResultItem hitem : filteredResult) {
				if ((hitem.strHotelName.toLowerCase()).contains(strSearchName
						.toLowerCase())) {
					tempArray.add(hitem);
				}
			}
			filteredResult.clear();
			filteredResult.addAll(tempArray);
			tempArray.clear();
		}

		if (blFilterBoardTypes) {
			ArrayList<HotelResultItem> tempArray = new ArrayList<HotelResultItem>();
			for (HotelResultItem hItem : filteredResult) {
				if (hItem.strBoardTypes.toLowerCase().contains("breakfast")
						|| hItem.strBoardTypes.toLowerCase()
								.contains("الافطار")
						|| hItem.strBoardTypes.toLowerCase().contains("افطار")) {
					tempArray.add(hItem);
				}
			}
			filteredResult.clear();
			filteredResult.addAll(tempArray);
		}

		if (filteredResult.size() > 0) {
			new sort().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			lvHotelResult.setAdapter(null);
			noResultAlert(true, getResources().getString(R.string.no_result));
		}

	}

	// to show images only in the visible part to optimize speed and avoid OOM
	boolean flag;

	private void printImage(final ArrayList<HotelResultItem> array) {
		final ImageDownloader imageDownloader = new ImageDownloader();
		flag = true;
		final ViewHolder viewHolder = new ViewHolder();
		lvHotelResult.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if (scrollState == SCROLL_STATE_IDLE)
					for (int i = 0; i < view.getChildCount(); ++i) {
						View viewg = (View) view.getChildAt(i);
						HotelResultItem hItem = array.get(view
								.getPositionForView(viewg));
						CustomImageView iv = (CustomImageView) viewg
								.findViewById(R.id.iv_hotel_logo);
						if (!hItem.strHotelThumbImage.contains("no_image")) {
							if (getBitmapFromMemCache(hItem.strHotelThumbImage) == null) {
								viewHolder.imageView = iv;
								imageDownloader.download(
										hItem.strHotelThumbImage, iv);
							}
						} else {
							iv.setImageBitmap(bmp);
						}
					}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				// to show images first time
				if (flag)
					for (int i = 0; i < view.getChildCount(); ++i) {
						View viewg = (View) view.getChildAt(i);
						HotelResultItem hItem = array.get(view
								.getPositionForView(viewg));
						CustomImageView iv = (CustomImageView) viewg
								.findViewById(R.id.iv_hotel_logo);
						if (!hItem.strHotelThumbImage.contains("no_image")) {
							if (getBitmapFromMemCache(hItem.strHotelThumbImage) == null) {
								viewHolder.imageView = iv;
								imageDownloader.download(
										hItem.strHotelThumbImage, iv);
							}
						} else
							iv.setImageBitmap(bmp);
						flag = false;
					}
			}
		});

		lvHotelResult.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				HotelResultItem hItem = (HotelResultItem) lvHotelResult
						.getItemAtPosition(position);
				new backService().execute(hItem);
			}
		});
	}

	public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			if (bitmap == null)
				bitmap = bmp;
			mMemoryCache.put(key, bitmap);
		}
	}

	public static Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	static class ViewHolder {
		ImageView imageView;
	}

	private class backService extends AsyncTask<HotelResultItem, Void, String> {

		HotelResultItem hItem;
		String sessionResult;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			tvProgressText.setText(getResources().getString(
					R.string.checking_hotel));
			if (tvProgressText.getVisibility() == View.GONE)
				tvProgressText.setVisibility(View.VISIBLE);
			loaderDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(HotelResultItem... params) {
			// TODO Auto-generated method stub
			URL url = null;
			HttpURLConnection urlConnection = null;
			try {
				hItem = params[0];
				url = new URL(CommonFunctions.main_url
						+ "en/HotelApi/AvailResult?searchID=" + strSessionId);
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
			return params[0].strDeepLinkUrl;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (loaderDialog.isShowing())
				loaderDialog.dismiss();
			if (sessionResult.equalsIgnoreCase("true")) {
				tvProgressText.setText(getResources().getString(
						R.string.searching_flight));
				Intent details = new Intent(HotelResultActivity.this,
						HotelDetailsActivity.class);
				details.putExtra("url", hItem.strNativeDeepUrl);
				details.putExtra("img_url", hItem.strHotelThumbImage);
				details.putExtra("sessionID", strSessionId);
				details.putExtra("type", "hotel");
				details.putExtra("latitude", hItem.strHotelLattitude);
				details.putExtra("longitude", hItem.strHotelLongitude);
				details.putExtra("hotelRating", hItem.floatHotelRating);
				startActivity(details);
			} else {
				tvProgressText.setText(getResources().getString(
						R.string.hotel_expired));
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

		if (filter) {
			alertDialog.setNegativeButton(
					getResources().getString(R.string.reset_filter),
					new AlertDialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							blFilterPrice = false;
							blFilterName = false;
							filterMinPrice = (hotelResultItem).get(0).doubleHotelDisplayRate;
							filterMaxPrice = (hotelResultItem)
									.get(hotelResultItem.size() - 1).doubleHotelDisplayRate;
							blAllStar = true;
							blNoStar = false;
							blOneStar = false;
							blTwoStar = false;
							blThreeStar = false;
							blFourStar = false;
							blFiveStar = false;
							strSearchName = "";
							blFilterBoardTypes = false;
							// arrayCheckedBoardtypes.clear();
							new sort()
									.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						}
					});
		} else {
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
		curr = new Dialog(HotelResultActivity.this,
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

				tvCurrency.setText(CommonFunctions.strCurrency);
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		hotelResultItem.clear();
		hotelResultItemTemp.clear();
		mMemoryCache.evictAll();
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
