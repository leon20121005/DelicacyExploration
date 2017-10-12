package com.example.leon.delicacyexploration;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

//Created by leon on 2017/10/10.

public class ImageDownloaderAsyncTask extends AsyncTask<String, Void, Bitmap>
{
    private final WeakReference<ImageView> _imageViewReference;
    private final Map<String, Bitmap> _bitmapCache;
    private String _url;

    public ImageDownloaderAsyncTask(ImageView imageView, Map<String, Bitmap> bitmapCache)
    {
        _imageViewReference = new WeakReference<>(imageView);
        _bitmapCache = bitmapCache;
    }

    //背景執行緒要執行的任務(下載圖片)
    @Override
    protected Bitmap doInBackground(String... params)
    {
        _url = params[0];
        return DownloadBitmap(_url);
    }

    //執行完下載設定ImageView的圖片並且把結果加入快取
    @Override
    protected void onPostExecute(Bitmap bitmap)
    {
        if (isCancelled())
        {
            bitmap = null;
        }

        ImageView imageView = _imageViewReference.get();
        if (imageView != null)
        {
            if (bitmap != null)
            {
                imageView.setImageBitmap(bitmap);
                _bitmapCache.put(_url, bitmap);
            }
        }
    }

    //根據URL下載縮圖並回傳
    private Bitmap DownloadBitmap(String requestURL)
    {
        try
        {
            URL url = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);

            int statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK)
            {
                InputStream inputStream = connection.getInputStream();
                if (inputStream != null)
                {
                    return BitmapFactory.decodeStream(inputStream);
                }
            }
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
        catch (OutOfMemoryError error) //如果下載縮圖時記憶體不夠則清除縮圖快取
        {
            _bitmapCache.clear();
        }
        return null;
    }
}
