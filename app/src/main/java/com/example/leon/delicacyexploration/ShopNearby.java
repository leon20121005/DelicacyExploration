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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.support.annotation.Nullable;

import java.util.ArrayList;

//Created by leon on 2017/8/30.

public class ShopNearby extends Fragment implements AsyncResponse
{
    private final String NEARBY_SHOP_URL = "/android/get_nearby_shops.php";
    private ArrayList<Shop> _shopList = new ArrayList<>();

    private double _latitude;
    private double _longitude;

    private final double DEFAULT_QUERY_RANGE = 3;
    private double _queryRange = DEFAULT_QUERY_RANGE;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        // Returning our layout file
        return inflater.inflate(R.layout.shop_nearby, container, false); //第一個參數為Fragment的layout
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
        InitializeSpinner(view);
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
            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (currentLocation != null)
            {
                _latitude = currentLocation.getLatitude();
                _longitude = currentLocation.getLongitude();
            }
        }
    }

    private void SendQuery()
    {
        String latitude = "lat=" + Double.toString(_latitude);
        String longitude = "lng=" + Double.toString(_longitude);
        String radius = "radius=" + Double.toString(_queryRange);
        String limit = "limit=" + Integer.toString(20);
        String queryURL = getString(R.string.server_ip_address) + NEARBY_SHOP_URL + "?" + latitude + "&" + longitude + "&" + radius + "&" + limit;

        new HttpRequestAsyncTask((Fragment) this).execute(queryURL);
    }

    //初始化ListView
    private void InitializeListView(View view)
    {
        ListView listView = (ListView) view.findViewById(R.id.nearbyList);
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

        Toast.makeText(getActivity(), "顯示附近" + Double.toString(_queryRange) + "公里店家", Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), Double.toString(_latitude) + ", " + Double.toString(_longitude), Toast.LENGTH_SHORT).show();
    }

    //初始化Spinner
    private void InitializeSpinner(View view)
    {
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        String[] range = {"200公尺", "500公尺", "1公里", "3公里", "5公里", "10公里"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, range);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setSelection(3, false); //第二個參數為false讓這個設定不會實際觸發onItemSelected()
        if (_queryRange == DEFAULT_QUERY_RANGE) //只有在預設範圍 (初始化完)時才主動送出Query
        {
            SendQuery();
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
            {
                switch (position)
                {
                    case 0:
                        _queryRange = 0.2;
                        break;
                    case 1:
                        _queryRange = 0.5;
                        break;
                    case 2:
                        _queryRange = 1;
                        break;
                    case 3:
                        _queryRange = 3;
                        break;
                    case 4:
                        _queryRange = 5;
                        break;
                    case 5:
                        _queryRange = 10;
                        break;
                }
                SendQuery();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
    }
}
