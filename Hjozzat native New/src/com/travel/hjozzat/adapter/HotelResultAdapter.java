package com.travel.hjozzat.adapter;

import java.util.ArrayList;
import java.util.Locale;

import com.hjozzat.support.CommonFunctions;
import com.hjozzat.support.CustomImageView;
import com.travel.hjozzat.HotelResultActivity;
import com.travel.hjozzat.R;
import com.travel.hjozzat.model.HotelResultItem;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class HotelResultAdapter extends BaseAdapter{

	
	private Context context;
	private ArrayList<HotelResultItem> hotelResultItem;
	String strSessionId;
	public HotelResultAdapter(Context context, ArrayList<HotelResultItem> hotelResultItem, String strSessionId){
		this.context = context;
		this.hotelResultItem = hotelResultItem;
		this.strSessionId = strSessionId;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		
		return hotelResultItem.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return hotelResultItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.search_result_item_hotel, null);
            
        }
		
		final HotelResultItem hItem = hotelResultItem.get(position);
		
		CustomImageView iv = (CustomImageView) convertView.findViewById(R.id.iv_hotel_logo);
		iv.setImageDrawable(null);
		if(!hItem.strHotelThumbImage.contains("no_image")){
			Bitmap bmp = HotelResultActivity.getBitmapFromMemCache(hItem.strHotelThumbImage);
			iv.setImageBitmap(bmp);
        }
		else
		{
			iv.setImageResource(R.drawable.ic_no_image);
			iv.setBackgroundResource(R.drawable.white_bag_curved_border);
		}
		
		
		((TextView) convertView.findViewById(R.id.tv_hotel_name)).setText(hItem.strHotelName);
        ((TextView) convertView.findViewById(R.id.tv_place)).setText(hItem.strHotelAddress);
        
        String price = String.format(new Locale("en"), "%.3f", Double.parseDouble(hItem.strDisplayRate));
        
        ((TextView) convertView.findViewById(R.id.tv_hotel_cost)).setText(CommonFunctions.strCurrency+" "+price);
        ((RatingBar) convertView.findViewById(R.id.rb_hotel_ratng)).setRating(hItem.floatHotelRating);
        
        if(hItem.strBoardTypes.toLowerCase().contains("breakfast") ||
        		hItem.strBoardTypes.toLowerCase().contains("الافطار")  ||
        		hItem.strBoardTypes.toLowerCase().contains("افطار"))
        	((LinearLayout) convertView.findViewById(R.id.ll_breakfast_included)).setVisibility(View.VISIBLE);
        else
        	((LinearLayout) convertView.findViewById(R.id.ll_breakfast_included)).setVisibility(View.GONE);
        
//        LinearLayout llMap = (LinearLayout) convertView.findViewById(R.id.ll_map);
//        llMap.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent marker = new Intent(context, MarkeActivity.class);
//				marker.putExtra("HotelName", 	hItem.strHotelName);
//				marker.putExtra("HotelAddress", hItem.strHotelAddress);
//				marker.putExtra("Latitude", 	hItem.strHotelLattitude);
//				marker.putExtra("Langitude", 	hItem.strHotelLongitude);
//				context.startActivity(marker);
//				
//			}
//		});
        
//        if(hItem.strHotelLattitude == 0.0 || hItem.strHotelLongitude == 0.0)
//        	llMap.setVisibility(View.GONE);
//        else
//        	llMap.setVisibility(View.VISIBLE);
        
        return convertView;
	}

}
