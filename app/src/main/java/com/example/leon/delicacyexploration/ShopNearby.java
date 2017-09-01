package com.example.leon.delicacyexploration;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.support.annotation.Nullable;

import java.util.ArrayList;

//Created by leon on 2017/8/30.

public class ShopNearby extends Fragment implements AsyncResponse
{
    private final String NEARBY_SHOP_URL = getString(R.string.server_ip_address) + "/android/get_nearby_shops.php";
    private ArrayList<Shop> _shopList = new ArrayList<>();

    private double _latitude;
    private double _longitude;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        // Returning our layout file
        return inflater.inflate(R.layout.shop_list, container, false); //第一個參數為Fragment的layout
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("附近店家");

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.show();
        FloatingActionButton returnButton = (FloatingActionButton) getActivity().findViewById(R.id.returnButton);
        returnButton.hide();

        InitializeLocation();

        String latitude = "lat=" + Double.toString(_latitude);
        String longitude = "lng=" + Double.toString(_longitude);
        String queryURL = NEARBY_SHOP_URL + "?" + latitude + "&" + longitude + "&radius=5&limit=20";
        new HttpRequestAsyncTask((Fragment) this).execute(queryURL);
    }

    @Override
    public void FinishAsyncProcess(String output)
    {
        JsonParser jsonParser = new JsonParser();
        _shopList = jsonParser.ParseShopList(getActivity(), output);

        InitializeListView(getView());
    }

    private void InitializeLocation()
    {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            Location myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (myLocation != null)
            {
                _latitude = myLocation.getLatitude();
                _longitude = myLocation.getLongitude();
            }
        }
    }

    //初始化ListView
    private void InitializeListView(View view)
    {
        ListView listView = (ListView) view.findViewById(R.id.shopList);
        ShopListAdapter shopListAdapter = new ShopListAdapter(getActivity(), _shopList);
        listView.setAdapter(shopListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                ((MainActivity) getActivity()).DisplayShopDetail(_shopList.get(position));
            }
        });

        Toast.makeText(getActivity(), "顯示附近5公里店家", Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), Double.toString(_latitude) + ", " + Double.toString(_longitude), Toast.LENGTH_SHORT).show();
    }
}
