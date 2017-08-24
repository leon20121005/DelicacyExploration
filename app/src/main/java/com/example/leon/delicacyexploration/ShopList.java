package com.example.leon.delicacyexploration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//Created by leon on 2017/8/6.

public class ShopList extends Fragment implements AsyncResponse
{
    private final String SHOP_LIST_URL = "http://36.231.104.82/android/get_all_shops.php";
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

        new HttpRequestAsyncTask((Fragment) this).execute(SHOP_LIST_URL);
    }

    @Override
    public void FinishAsyncProcess(String output)
    {
        JSONObject jsonObject;
        JSONArray shops;

        try
        {
            jsonObject = new JSONObject(output);
            int isSuccess = jsonObject.getInt("success");

            if (isSuccess == 1)
            {
                shops = jsonObject.getJSONArray("shops");

                for (int index = 0; index < shops.length(); index++)
                {
                    JSONObject tupleJSON = shops.getJSONObject(index);

                    String shopName = tupleJSON.getString("name");
                    String shopEvaluation = Integer.toString(tupleJSON.getInt("evaluation"));
                    String shopAddress = tupleJSON.getString("address");

                    _shopList.add(new Shop(shopName, "評價分數: " + shopEvaluation + "/10", shopAddress));
                }
            }
            else
            {
                Toast.makeText(getActivity(), "No shops found", Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException exception)
        {
            exception.printStackTrace();
        }

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
