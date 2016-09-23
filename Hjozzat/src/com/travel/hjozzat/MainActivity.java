package com.travel.hjozzat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.travel.hjozzat.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Toolbar;


public class MainActivity extends Activity {

	ImageButton menuBtn, ibLang;
	private boolean doubleBackToExitPressedOnce  = false;
	
	String lang = "en";
	private Locale myLocale; 
	
	String mainUrl = "";
	
	CommonFunctions cf;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        loadAppBar();

        cf = new CommonFunctions(this);
        
        setContentView(R.layout.activity_main);
        
        
        // viswas //changes 
        mainUrl = CommonFunctions.main_url+CommonFunctions.lang+"/";
			
        new backLoginCheck().execute();
        
    }
    
    @Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		new backLoginCheck().execute();
		super.onRestart();
	}
    
    public void clicker(View v){
    	Intent web;
    	if(cf.isConnectingToInternet())
    	{
	    	switch (v.getId()) {
			case R.id.ll_flight: web = new Intent(MainActivity.this, SearchPage.class);
								 web.putExtra("url", mainUrl+"?section=app");
								 startActivity(web);
								 break;
			case R.id.ll_hotel: web = new Intent(MainActivity.this, SearchPage.class);
								web.putExtra("url", mainUrl+"Home/HotelTab/HotelTab?section=app");
								startActivity(web);
								break;
			case R.id.ll_sports: web = new Intent(MainActivity.this, SearchPage.class);
								web.putExtra("url", mainUrl+"Home/SportsTab/SportsTab?section=app");
								startActivity(web);
								break;
			case R.id.ll_visa:  web = new Intent(MainActivity.this, SearchPage.class);
								web.putExtra("url", mainUrl+"shared/VisaApplication?section=app");
								startActivity(web);
								break;
			case R.id.ll_my_booking: 	web = new Intent(MainActivity.this, SearchPage.class);
										web.putExtra("url", mainUrl+"MyBooking/ManageMyBookings?section=app");
										startActivity(web);
										break;
			case R.id.btn_call:
				String number = "tel:" + getResources().getString(R.string.contact_number);
				web = new Intent(Intent.ACTION_DIAL);
				web.setData(Uri.parse(number));
				startActivity(web);
				break;
										
			default:
				break;
			}
    	}
    	
    	else
    		noInternetAlert();
    }

    private void loadAppBar()
	{
		//============== Define a Custom Header for Navigation drawer=================//
		LayoutInflater inflator=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.header, null);
			
		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setCustomView(v);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			try{
				Toolbar parent = (Toolbar) v.getParent(); 
				parent.setContentInsetsAbsolute(0, 0);
			} catch(ClassCastException e)
			{
				e.printStackTrace();
			}
		}
		
		
		menuBtn = (ImageButton) v.findViewById(R.id.imgLefttMenu);
		ibLang	= (ImageButton) v.findViewById(R.id.ib_lang);
		
		menuBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent slide = new Intent(MainActivity.this, SidePanelActivity.class);
				startActivity(slide);
			}	
		});
		
		ibLang.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(CommonFunctions.lang.equalsIgnoreCase("en"))
					lang = "ar";
				else
					lang = "en";

				changeLang(lang);
				if(CommonFunctions.lang.equalsIgnoreCase("en"))
		        	mainUrl = CommonFunctions.main_url+"en/";
		        else
		        	mainUrl = CommonFunctions.main_url+"ar/";
			}
		});
		
	}
    
    private void loadLocale() {
		// TODO Auto-generated method stub
		SharedPreferences sharedpreferences  = this.getSharedPreferences("CommonPrefs", Context.MODE_PRIVATE);
		String lang = sharedpreferences.getString("Language", "en");
		System.out.println("Default lang: "+lang);
		if(lang.equalsIgnoreCase("ar"))
		{
			myLocale = new Locale(lang);
			saveLocale(lang);
			Locale.setDefault(myLocale);
			android.content.res.Configuration config = new android.content.res.Configuration();
			config.locale = myLocale;
			this.getBaseContext().getResources().updateConfiguration(config, this.getBaseContext().getResources().getDisplayMetrics());
			CommonFunctions.lang = "ar";
		}
		else{
			myLocale = new Locale(lang);
			saveLocale(lang);
			Locale.setDefault(myLocale);
			android.content.res.Configuration config = new android.content.res.Configuration();
			config.locale = myLocale;
			this.getBaseContext().getResources().updateConfiguration(config, this.getBaseContext().getResources().getDisplayMetrics());
			CommonFunctions.lang = "en";
		}
	}
    
    public void changeLang(String lang)
	{
		if (lang.equalsIgnoreCase(""))
			return;
		myLocale = new Locale(lang);
		saveLocale(lang);
		Locale.setDefault(myLocale);
		android.content.res.Configuration config = new android.content.res.Configuration();
		config.locale = myLocale;
		this.getBaseContext().getResources().updateConfiguration(config, this.getBaseContext().getResources().getDisplayMetrics());
		
		updateTexts();
		loadAppBar();
	}
	public void saveLocale(String lang)
	{
		CommonFunctions.lang = lang;
		String langPref = "Language";
		SharedPreferences prefs = this.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(langPref, lang);
		editor.commit();
	}
	private void updateTexts()
	{
		setContentView(R.layout.activity_main);
	}
    
	private class backLoginCheck extends AsyncTask<Void, Void, String>
	{

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			makePostRequest();
			return null;
		}
	}
	
	HttpURLConnection urlConnection = null;
	int show_handler = 0; 
	private void makePostRequest() 
	{
		if(cf.isConnectingToInternet())
        //making POST request.
        try {
        	URL url = new URL(CommonFunctions.main_url+"en/shared/UserDetails");
        	CookieManager cookieManager = CookieManager.getInstance();
            String cookie = cookieManager.getCookie(url.toString());
        	Log.i("url", url.toString());
        	urlConnection = (HttpURLConnection) url.openConnection();
        	urlConnection.setRequestProperty("Cookie",cookie);
        	urlConnection.setConnectTimeout(15000);
        	urlConnection.setRequestMethod("GET");
        	InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        	String resultString = convertStreamToString(in);
        	urlConnection.disconnect();
        	parseResult(resultString);
        } 
		catch(SocketTimeoutException e)
        {
        	// Log exception
            e.printStackTrace();
            urlConnection.disconnect();
        }
        catch (NullPointerException e) {
            // Log exception
            e.printStackTrace();
            urlConnection.disconnect();
        } 
        catch (IOException e) {
            // Log exception
            e.printStackTrace();
            urlConnection.disconnect();
        }
	}
	

	
	private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
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
	
	private void parseResult(String result)
	{
		// Parse String to JSON object 
		try {
			JSONObject json = new JSONObject(result); 
			String status 	= json.getString("Status");
//			String name 	= json.getString("Name");
			
			if(status.equalsIgnoreCase("true"))
			{
				CommonFunctions.loggedIn = true;
				System.out.println("Logged in");
			}
			else
			{
				CommonFunctions.loggedIn = false;
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void noInternetAlert()
	{
		AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(getResources().getString(R.string.error_no_internet_title));

        // Setting Dialog Message
        alertDialog.setMessage(getResources().getString(R.string.error_no_internet_msg));

        // Setting OK Button
        alertDialog.setPositiveButton(getResources().getString(R.string.error_no_internet_settings), new AlertDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//intent to move mobile settings
				startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
			}});
        alertDialog.setNegativeButton(getResources().getString(R.string.error_no_internet_close_app), new DialogInterface.OnClickListener() {
    		
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			// TODO Auto-generated method stub
    		     
    			finish();
    			Intent intent = new Intent(Intent.ACTION_MAIN);
    			intent.addCategory(Intent.CATEGORY_HOME);
    			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			startActivity(intent);
    			//System.exit(0);
    		}
    	});

        // Showing Alert Message
        alertDialog.show();
	}
	
    @Override
    public void onBackPressed() {
		// TODO Auto-generated method stub

    	if(doubleBackToExitPressedOnce)
    	{
    		finishAffinity();
    	}
		else
		{
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.exit_msg), Toast.LENGTH_SHORT).show();
			doubleBackToExitPressedOnce = true;
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					doubleBackToExitPressedOnce = false;
				}
			}, 3000);
		}
	}
    
}
