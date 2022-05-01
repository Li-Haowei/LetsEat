package com.example.letseat.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/*
This class is used in Restaurant Adapter class to load online url into local imageView
 */
public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
    //call this like new ImageLoadTask(url, imageView).execute();
    /*
    For example:
            images[i] = new ImageView(this);
            images[i].setLayoutParams(params1);
            new ImageLoadTask(ImageURLs[i], images[i]).execute();
            linearLayout.addView(images[i]);
     */
    private String url;
    private ImageView imageView;

    public ImageLoadTask(String url, ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL urlConnection = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        imageView.setImageBitmap(result);
    }

}