package com.hjozzat.support;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

import com.travel.hjozzat.HotelResultActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    String ur;
    public ImageDownloaderTask(ImageView imageView) {
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
    	ur = params[0];
        return downloadBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                	imageView.setImageBitmap(HotelResultActivity.bmp);
                }
//                HotelDetailsActivity.addBitmapToMemoryCache(ur, bitmap);
            }
        }
    }
    
    private Bitmap downloadBitmap(String url) {
//        HttpURLConnection urlConnection = null;
        Bitmap bitmap = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        try {
            URL uri = new URL(url);
            bmOptions.inSampleSize = 1;
            bitmap = BitmapFactory.decodeStream((InputStream)new URL(uri.toString()).getContent(),
            		null, bmOptions);
//            bitmap = getResizedBitmap(bitmap, 500, bitmap.getWidth());
        } catch(OutOfMemoryError e){
        	return null;
        }
        
        catch(NullPointerException e) {
        	return null;
        } catch (Exception e) {
//            urlConnection.disconnect();
            Log.w("ImageDownloader", "Error downloading image from " + url);
            
        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
        }
        return bitmap;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }
    
}


