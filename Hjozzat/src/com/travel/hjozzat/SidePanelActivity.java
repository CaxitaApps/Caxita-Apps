package com.travel.hjozzat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toolbar;

public class SidePanelActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		loadAppBar();
		setContentView(R.layout.activity_side_panel);
		
		if(CommonFunctions.loggedIn){
			((LinearLayout) findViewById(R.id.ll_login)).setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.ll_logout)).setVisibility(View.VISIBLE);
		} else{
			((LinearLayout) findViewById(R.id.ll_login)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.ll_logout)).setVisibility(View.GONE);
		}
		
	}
	
	public void clicker(View v){
		Intent intent = null;
		switch (v.getId()) {
		
		case R.id.ll_login: 
			finish();
			intent = new Intent(SidePanelActivity.this, SearchPage.class);
			intent.putExtra("url", CommonFunctions.main_url+CommonFunctions.lang+"/Shared/AppAccount?isRegisterReq=false");
			startActivity(intent);
		  	break;
		
		case R.id.ll_register: 
			finish();
			intent = new Intent(SidePanelActivity.this, SearchPage.class);
			intent.putExtra("url", CommonFunctions.main_url+CommonFunctions.lang+"/Shared/AppAccount?isRegisterReq=true");
			startActivity(intent);
		  	break;
		  	
		case R.id.ll_htu: 
			intent = new Intent(SidePanelActivity.this, HowToUseAcitvity.class);
			startActivity(intent);
		  	break;

		case R.id.ll_faq:
			intent = new Intent(SidePanelActivity.this, FAQActivity.class);
			startActivity(intent);
		  	break;
			
		case R.id.ll_about:
			intent = new Intent(SidePanelActivity.this, AboutActivity.class);
			startActivity(intent);
			break;
			
		case R.id.ll_contact:
			intent = new Intent(SidePanelActivity.this, ContactActivity.class);
			startActivity(intent);
			break;
			
		case R.id.ll_logout: 
			finish();
			intent = new Intent(SidePanelActivity.this, SearchPage.class);
			intent.putExtra("url", CommonFunctions.main_url+CommonFunctions.lang+"/Shared/Logout");
			startActivity(intent);
		  	break;
			
		case R.id.btn_call:
			String number = "tel:" + getResources().getString(R.string.contact_number);
			intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse(number));
			startActivity(intent);
			break;
			
		case R.id.btn_email:
			String email = getResources().getString(R.string.contact_email);
			intent = new Intent(Intent.ACTION_SEND);
			intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Enquiry");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            Intent mailer = Intent.createChooser(intent, "Send Mail");
            startActivity(mailer);
			break;
		default:
			break;
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
		homeBtn.setVisibility(View.INVISIBLE);
		
//		homeBtn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				finish();
//				Intent intent = new Intent(SidePanelActivity.this, MainActivity.class);
//				startActivity(intent);
//			}
//		});
		
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
	}
	
}
