package com.travel.hjozzat;

import com.travel.hjozzat.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

public class FAQActivity extends Activity {
	
	String[] strFAQ;
	final int y = 0;
	ScrollView sv;
	View view;
	TextView tvFaq;
	AlertDialog.Builder alertDialog;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		loadAppBar();
		setContentView(R.layout.activity_faq);
		
		strFAQ = getResources().getStringArray(R.array.faq_array);
		
		sv	= (ScrollView) findViewById(R.id.sv_faq);
		LinearLayout llFAQ 	= (LinearLayout) findViewById(R.id.ll_faq);
		
		for(int i=0; i < strFAQ.length; i=i+2)
		{
			final View view = getLayoutInflater().inflate(R.layout.faq_item, null);
			
			tvFaq = (TextView) view.findViewById(R.id.tv_faq_content);
			tvFaq.setText(strFAQ[i]);
			
			llFAQ.addView(view);
			
			final int y = i;
			
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					noResultAlert(strFAQ[y], strFAQ[y+1]);
				}
			});
		}
		
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
				Intent intent = new Intent(FAQActivity.this, MainActivity.class);
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
	
	public void noResultAlert(String qstn, String ans)
	{
		alertDialog=new AlertDialog.Builder(this);
		alertDialog.setTitle(qstn);
        alertDialog.setMessage(ans);
        alertDialog.setPositiveButton(getResources().getString(R.string.ok), null);

        alertDialog.setCancelable(false);
        alertDialog.show();
	}
	
}