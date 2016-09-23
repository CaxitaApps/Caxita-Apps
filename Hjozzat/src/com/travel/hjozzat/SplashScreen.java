package com.travel.hjozzat;

import java.util.Locale;

import com.travel.hjozzat.R;

import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;

public class SplashScreen extends Activity {

	private static int SPLASH_TIME_OUT = 2000;
	private Locale myLocale;
	private static boolean flag = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadLocale();
		getActionBar().hide();
		setContentView(R.layout.activity_splash_screen);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		flag = true;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(flag)
		{
			splash();
			flag = false;
		}
			
	}
	
	public void splash()
	{
//		if(cd.isConnectingToInternet())
//		{
			new Handler().postDelayed(new Runnable() {
				
	            /*
	             * Showing splash screen with a timer. This will be useful when you
	             * want to show case your app logo / company
	             */
	 
	            @Override
	            public void run() {
	                // This method will be executed once the timer is over
	                // Start your app main activity
	            	
	            	Intent i = new Intent(SplashScreen.this, MainActivity.class);
	                startActivity(i);
	                // close this activity
	                finish();
	            }
			}, SPLASH_TIME_OUT);
//		}
//		else
//		{
//			noInternetAlert();
//		}
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
        alertDialog.setCancelable(false);
        alertDialog.show();
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
	
	public void saveLocale(String lang)
	{
		CommonFunctions.lang = lang;
		String langPref = "Language";
		SharedPreferences prefs = this.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(langPref, lang);
		editor.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

}
