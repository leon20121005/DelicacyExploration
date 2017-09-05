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
import android.widget.TextView;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

//Created by leon on 2017/9/5.

public class ShopFavorite extends Fragment implements AsyncResponse
{
    private final String FAVORITE_SHOP_URL = "/android/get_favorite_shops.php";
    private ArrayList<Shop> _shopList = new ArrayList<>();

    private double _latitude;
    private double _longitude;

    private final String DEFAULT_QUERY_ORDER = "依距離顯示";
    private String _queryOrder = DEFAULT_QUERY_ORDER;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        // Returning our layout file
        return inflater.inflate(R.layout.shop_favorite, container, false); //第一個參數為Fragment的layout
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("我的最愛");

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
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        Set<String> defaultShopIDList = new HashSet<>();
        Set<String> favoriteShopIDList = sharedPreferences.getStringSet("FavoriteShopIDList", defaultShopIDList);

        //把已收藏的店家ID存到JSON陣列並轉換成字串
        JSONArray jsonArray = new JSONArray();
        for (String id : favoriteShopIDList)
        {
            jsonArray.put(Integer.parseInt(id));
        }
        String idList = jsonArray.toString();

        //把已收藏的店家ID和經緯度提交到伺服器
        HashMap<String, String> postData = new HashMap<>();
        postData.put("id_list", idList);
        postData.put("latitude", Double.toString(_latitude));
        postData.put("longitude", Double.toString(_longitude));

        String queryURL = getString(R.string.server_ip_address) + FAVORITE_SHOP_URL;

        new HttpRequestAsyncTask((Fragment) this, postData, "POST").execute(queryURL);
    }

    //初始化ListView
    private void InitializeListView(View view)
    {
        TextView emptyTextView = (TextView) view.findViewById(R.id.favoriteEmpty);
        ListView listView = (ListView) view.findViewById(R.id.favoriteList);
        listView.setEmptyView(emptyTextView);

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
    }

    //初始化Spinner
    private void InitializeSpinner(View view)
    {
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        String[] order = {DEFAULT_QUERY_ORDER};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, order);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setSelection(0, false); //第二個參數為false讓這個設定不會實際觸發onItemSelected()
        if (_queryOrder.equals(DEFAULT_QUERY_ORDER)) //只有在預設條件 (初始化完)時才主動送出Query
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
                        _queryOrder = DEFAULT_QUERY_ORDER;
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
