package com.travel.hjozzat;

import java.util.Locale;

import com.hjozzat.support.CommonFunctions;
import com.travel.hjozzat.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toolbar;
import android.widget.ViewFlipper;

public class HowToUseAcitvity extends Activity{

	private Locale myLocale;
	private ViewFlipper viewFlipper;
    private float lastX;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		loadLocale();
		loadAppBar();
		setContentView(R.layout.activity_how_to_use);
        viewFlipper = (ViewFlipper) findViewById(R.id.vf_how_to_use);
        
	}
	
	private void loadAppBar()
	{
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
		
		ImageView backBtn = (ImageView) v.findViewById(R.id.iv_back);
		ImageView homeBtn = (ImageView) v.findViewById(R.id.iv_home);
		
		homeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				Intent intent = new Intent(HowToUseAcitvity.this, MainActivity.class);
				startActivity(intent);
			}
		});
		
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
	}
	
	public void clicker(View v)
	{
		switch(v.getId())
		{
		case R.id.ib_next:
			// If there is a child (to the left), kust break.
//	       	 if (viewFlipper.getDisplayedChild() == 1)
//	       		 break;
	
	       	 // Next screen comes in from right.
	       	 viewFlipper.setInAnimation(this, R.anim.left_in);
	       	// Current screen goes out from left. 
	       	 viewFlipper.setOutAnimation(this, R.anim.left_out);
	            
	       	// Display previous screen.
            viewFlipper.showNext();
            break;
		case R.id.ib_previous:	
			// If there aren't any other children, just break.
//            if (viewFlipper.getDisplayedChild() == 0)
//            	break;
            // Next screen comes in from left.
            viewFlipper.setInAnimation(this, R.anim.right_in);
            // Current screen goes out from right. 
            viewFlipper.setOutAnimation(this, R.anim.right_out);
            viewFlipper.showPrevious();
            break;
		}
	}
	
	// Using the following method, we will handle all screen swaps.
    public boolean onTouchEvent(MotionEvent touchevent) {
    	switch (touchevent.getAction()) {
        
        case MotionEvent.ACTION_DOWN: 
        	lastX = touchevent.getX();
            break;
        case MotionEvent.ACTION_UP: 
            float currentX = touchevent.getX();
            
            // Handling left to right screen swap.
            if (lastX < currentX) {
            	
//            	// If there aren't any other children, just break.
//                if (viewFlipper.getDisplayedChild() == 0)
//                	break;
                
                // Next screen comes in from left.
                viewFlipper.setInAnimation(this, R.anim.right_in);
                // Current screen goes out from right. 
                viewFlipper.setOutAnimation(this, R.anim.right_out);
                
                // Display next screen.
                viewFlipper.showPrevious();
             }
                                     
            // Handling right to left screen swap.
             if (lastX > currentX) {
            	 
//            	 // If there is a child (to the left), kust break.
//            	 if (viewFlipper.getDisplayedChild() == 1)
//            		 break;
    
            	 // Next screen comes in from right.
            	 viewFlipper.setInAnimation(this, R.anim.left_in);
            	// Current screen goes out from left. 
            	 viewFlipper.setOutAnimation(this, R.anim.left_out);
                 
                 viewFlipper.showNext();
             }
             break;
    	 }
         return false;
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
