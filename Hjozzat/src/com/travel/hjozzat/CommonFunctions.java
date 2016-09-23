package com.travel.hjozzat;

import java.util.Random;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class CommonFunctions {
    
	public static boolean loggedIn = false;
	public static String lang = "en";
	public static String strCurrency = "KWD";
    private Context _context;
//    public static String main_url = "http://hjozzat.caxita.ca/";
//    public static String main_url = "http://113.193.253.130/royalline/";
    public static String main_url = "http://www.hjozzat.com/";
     
    public CommonFunctions(Context context){
        this._context = context;
    }
 
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null) 
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null) 
                  for (int i = 0; i < info.length; i++) 
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
 
          }
          return false;
    }
    
    public void showToast(String msg)
    {
    	Toast.makeText(_context, msg, Toast.LENGTH_SHORT).show();
    }
    
    public static String getRandomString(final int sizeOfRandomString)
	{
		final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";
		final Random random=new Random();
		final StringBuilder sb=new StringBuilder(sizeOfRandomString);
		for(int i=0;i<sizeOfRandomString;++i)
			sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
		return sb.toString()+"_";
	}
    
}
