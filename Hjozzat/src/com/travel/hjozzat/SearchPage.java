package com.travel.hjozzat;

import java.util.Locale;

import com.travel.hjozzat.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.RenderPriority;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toolbar;

public class SearchPage extends Activity {

	private Locale myLocale;
	ProgressBar pbLine;
	String url;
	WebView wvWeb;
	ImageView ivBack, ivLogo, ivHome;
	CommonFunctions cf;

	@SuppressLint({ "SetJavaScriptEnabled", "InflateParams" })
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		loadLocale();
		setContentView(R.layout.activity_web);
		cf = new CommonFunctions(this);
		url = getIntent().getExtras().getString("url","Empty");
		
		//============== Define a Custom Header for Navigation drawer=================//
		LayoutInflater inflator=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.header_1, null);
		
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
		
		wvWeb	= (WebView) findViewById(R.id.wvContact);
		pbLine	= (ProgressBar) findViewById(R.id.pb_line);
		
		ivBack = (ImageView) v.findViewById(R.id.iv_back);
		ivLogo = (ImageView) v.findViewById(R.id.iv_logo);
		ivHome = (ImageView) v.findViewById(R.id.iv_home);
		
		ivBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (wvWeb.canGoBack()) {
			        wvWeb.goBack();
				}else 
				{
					wvWeb.clearCache(true);
					finish();
			    }
			}
		});
		
		ivHome.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		wvWeb.getSettings().setRenderPriority(RenderPriority.HIGH);
		wvWeb.getSettings().setJavaScriptEnabled(true);

		wvWeb.setWebViewClient(new WebViewClient(){
			
			@Override
			public void onPageStarted(final WebView view, final String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				Log.d("Started","Loading");
				System.out.println("Loading url"+url);
				if(url.equalsIgnoreCase(CommonFunctions.main_url+CommonFunctions.lang) ||
						url.equalsIgnoreCase(CommonFunctions.main_url))
					finish();
				else
					pbLine.setVisibility(View.VISIBLE);
				super.onPageStarted(view, null, favicon);
			}

			@Override
			public void onPageFinished(final WebView view, String url) {
				// TODO Auto-generated method stub
					
				Log.d("Finished","Loading"+url);
				pbLine.setVisibility(View.GONE);
				super.onPageFinished(view, url);
					
			}
			
			@Override 
			public void onLoadResource(WebView view, String url) {
				super.onLoadResource(view, url);
				
//				if(wvWeb.getProgress() > 80){
//					pbLine.setVisibility(View.GONE);
//				}
				pbLine.setProgress(wvWeb.getProgress());
			}
		});
		 
		wvWeb.loadUrl(url);
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		wvWeb.clearCache(true);
		wvWeb.clearFormData();
		wvWeb.destroy();
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (wvWeb.canGoBack()) {
	        wvWeb.goBack();
		}else 
		{
			finish();
	    }
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
	
}
