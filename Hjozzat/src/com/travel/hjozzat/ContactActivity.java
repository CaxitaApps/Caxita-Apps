package com.travel.hjozzat;

import com.travel.hjozzat.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toolbar;

public class ContactActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		loadAppBar();
		setContentView(R.layout.activity_contact);
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
				Intent intent = new Intent(ContactActivity.this, MainActivity.class);
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
	
}
