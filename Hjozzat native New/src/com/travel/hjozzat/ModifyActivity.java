package com.travel.hjozzat;

import com.hjozzat.support.CommonFunctions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ModifyActivity extends FragmentActivity {
	
	android.app.Fragment fragment = null;
	static int menu_pos = 0;

	ImageView ivBack;
	CommonFunctions cf;
	
	String mainUrl;
	
	LinearLayout llHome, llContainer;
	
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ivBack		= (ImageView) findViewById(R.id.iv_back);

		ivBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
	}
	
}
