package com.example.leon.delicacyexploration;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.List;
import java.util.Set;

//Created by leon on 2017/8/6.

public class ShopDetailFragment extends Fragment implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, AsyncResponse
{
    private Shop _data;
    private boolean _isFavorite;
    private GoogleMap _googleMap;
    private final int MY_LOCATION_REQUEST_CODE = 1;
    private final String SHOP_IMAGE_URL = "/android/get_images_by_shop_id.php";

    public void SetShopData(Shop data)
    {
        _data = data;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        // Returning our layout file
        return inflater.inflate(R.layout.shop_detail, container, false); //第一個參數為Fragment的layout
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        ViewHolder viewHolder = new ViewHolder();

        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("店家資訊");

        FloatingActionButton returnButton = (FloatingActionButton) getActivity().findViewById(R.id.returnButton);
        returnButton.show();

        viewHolder.shopDetailName = (TextView) view.findViewById(R.id.shopDetailName);
        viewHolder.shopDetailEvaluation = (TextView) view.findViewById(R.id.shopDetailEvaluation);
        viewHolder.shopDetailAddress = (TextView) view.findViewById(R.id.shopDetailAddress);
        viewHolder.shopDetailName.setText(_data.GetName());
        viewHolder.shopDetailEvaluation.setText(_data.GetEvaluation());
        viewHolder.shopDetailAddress.setText(_data.GetAddress());

        viewHolder.myFavoriteButton = (ImageButton) view.findViewById(R.id.imageButton);
        InitializeMyFavoriteButton(viewHolder.myFavoriteButton);

        viewHolder.mapView = (CustomMapView) view.findViewById(R.id.shopDetailMapView);
        viewHolder.mapView.onCreate(savedInstanceState);
        viewHolder.mapView.onResume();
        viewHolder.mapView.getMapAsync(this);

        SendImageQuery();
    }

    //初始化ImageButton
    private void InitializeMyFavoriteButton(final ImageButton imageButton)
    {
        Set<String> defaultShopIDList = new HashSet<>();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final Set<String> favoriteShopIDList = sharedPreferences.getStringSet(getString(R.string.favorite_shop_id_key), defaultShopIDList);
        _isFavorite = favoriteShopIDList.contains(Integer.toString(_data.GetID()));

        if (_isFavorite)
        {
            imageButton.setBackgroundResource(R.drawable.ic_star_black_24px);
        }
        else
        {
            imageButton.setBackgroundResource(R.drawable.ic_star_border_black_24px);
        }

        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();

                if (_isFavorite)
                {
                    _isFavorite = false;
                    imageButton.setBackgroundResource(R.drawable.ic_star_border_black_24px);
                    favoriteShopIDList.remove(Integer.toString(_data.GetID()));
                    Toast.makeText(getActivity(), "已移除收藏", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    _isFavorite = true;
                    imageButton.setBackgroundResource(R.drawable.ic_star_black_24px);
                    favoriteShopIDList.add(Integer.toString(_data.GetID()));
                    Toast.makeText(getActivity(), "已加入收藏", Toast.LENGTH_SHORT).show();
                }
                editor.putStringSet(getString(R.string.favorite_shop_id_key), favoriteShopIDList);
                editor.apply();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        _googleMap = googleMap;
        EnableMyLocation();
        PositioningAddress();
    }

    //在啟用「我的位置」圖層之前，使用「支援」程式庫檢查權限
    private void EnableMyLocation()
    {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // Show rationale and request permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }
        else if (_googleMap != null)
        {
            // Access to the location has been granted to the app
            _googleMap.setMyLocationEnabled(true);
        }
    }

    //處理Activity的onRequestPermissionsResult()
    public void OnRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == MY_LOCATION_REQUEST_CODE)
        {
            if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                EnableMyLocation();
            }
            else
            {
                // Permission was denied. Display an error message.
                Toast.makeText(getActivity(), "No permission to get my location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //根據店家的地址將位置標記在地圖上
    private void PositioningAddress()
    {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addressList = null;

        try
        {
            addressList = geocoder.getFromLocationName(_data.GetAddress(), 1);
        }
        catch (IOException ioException)
        {
            ioException.printStackTrace();
        }

        if (addressList != null && addressList.size() != 0)
        {
            LatLng latLng = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
            _googleMap.addMarker(new MarkerOptions().position(latLng).title(_data.GetName()));
            _googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
        else
        {
            Toast.makeText(getActivity(), "Can't find location", Toast.LENGTH_SHORT).show();
        }
    }

    private void SendImageQuery()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String shopID = "shop_id=" + Integer.toString(_data.GetID());
        String homeURL = sharedPreferences.getString(getString(R.string.custom_ip_key), getString(R.string.server_ip_address));
        String queryURL = homeURL + SHOP_IMAGE_URL + "?" + shopID;

        new HttpRequestAsyncTask((Fragment) this).execute(queryURL);
    }

    @Override
    public void FinishAsyncProcess(String output)
    {
        JsonParser jsonParser = new JsonParser(getActivity());
        ArrayList<Bitmap> images = jsonParser.ParseShopImage(output);

        InitializeImageView(getView(), images);
    }

    private void InitializeImageView(View view, ArrayList<Bitmap> images)
    {
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.linearLayout);

        for (int index = 0; index < images.size(); index++)
        {
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageBitmap(images.get(index));

            final float scale = getContext().getResources().getDisplayMetrics().density;
            int pixelsX = (int) (images.get(index).getWidth() / 2 * scale + 0.5f);
            int pixelsY = (int) (images.get(index).getHeight() / 2 * scale + 0.5f);

            imageView.setLayoutParams(new LinearLayout.LayoutParams(pixelsX, pixelsY));
            layout.addView(imageView);
        }
    }

    private static class ViewHolder
    {
        TextView shopDetailName;
        TextView shopDetailEvaluation;
        TextView shopDetailAddress;
        ImageButton myFavoriteButton;
        CustomMapView mapView;
    }
}
