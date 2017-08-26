package com.example.leon.delicacyexploration;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.IOException;
import java.util.Locale;
import java.util.List;

//Created by leon on 2017/8/6.

public class ShopDetail extends Fragment implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback
{
    private Shop _data;
    private GoogleMap _googleMap;
    private final int MY_LOCATION_REQUEST_CODE = 1;

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

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.hide();
        FloatingActionButton returnButton = (FloatingActionButton) getActivity().findViewById(R.id.returnButton);
        returnButton.show();

        viewHolder.shopDetailName = (TextView) view.findViewById(R.id.shopDetailName);
        viewHolder.shopDetailEvaluation = (TextView) view.findViewById(R.id.shopDetailEvaluation);
        viewHolder.shopDetailAddress = (TextView) view.findViewById(R.id.shopDetailAddress);
        viewHolder.shopDetailName.setText(_data.GetName());
        viewHolder.shopDetailEvaluation.setText(_data.GetEvaluation());
        viewHolder.shopDetailAddress.setText(_data.GetAddress());

        viewHolder.mapView = (MapView) view.findViewById(R.id.shopDetailMapView);
        viewHolder.mapView.onCreate(savedInstanceState);
        viewHolder.mapView.onResume();
        viewHolder.mapView.getMapAsync(this);
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

    public void SetShopData(Shop data)
    {
        _data = data;
    }

    private static class ViewHolder
    {
        TextView shopDetailName;
        TextView shopDetailEvaluation;
        TextView shopDetailAddress;
        MapView mapView;
    }
}
