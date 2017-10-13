package com.example.leon.delicacyexploration;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.ArrayList;

//Created by leon on 2017/8/6.

public class ShopListFragment extends Fragment implements AsyncResponse
{
    private final String SHOP_LIST_URL = "/android/get_all_shops.php";
    private ArrayList<Shop> _shopList = new ArrayList<>();

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
        getActivity().setTitle("店家列表");

        FloatingActionButton returnButton = (FloatingActionButton) getActivity().findViewById(R.id.returnButton);
        returnButton.hide();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String homeURL = sharedPreferences.getString(getString(R.string.custom_ip_key), getString(R.string.server_ip_address));

        new HttpRequestAsyncTask((Fragment) this).execute(homeURL + SHOP_LIST_URL);
    }

    @Override
    public void FinishAsyncProcess(String output)
    {
        JsonParser jsonParser = new JsonParser(getActivity());
        _shopList = jsonParser.ParseShopList(output);

        InitializeListView(getView());
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
    }
}
