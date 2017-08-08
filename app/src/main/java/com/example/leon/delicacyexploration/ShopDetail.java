package com.example.leon.delicacyexploration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

//Created by leon on 2017/8/6.

public class ShopDetail extends Fragment implements OnMapReadyCallback
{
    private Shop _data;
    private GoogleMap _googleMap;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        //Returning our layout file
        return inflater.inflate(R.layout.shop_detail, container, false); //第一個參數為Fragment的layout
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        ViewHolder viewHolder = new ViewHolder();

        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("ShopDetail");

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
    }

    @Override
    public void onResume()
    {
        super.onResume();
        (new ViewHolder()).mapView.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        (new ViewHolder()).mapView.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        (new ViewHolder()).mapView.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        (new ViewHolder()).mapView.onLowMemory();
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
