package com.example.leon.delicacyexploration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.annotation.Nullable;

import java.util.ArrayList;

//Created by leon on 2017/8/6.

public class ShopList extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        //Returning our layout file
        return inflater.inflate(R.layout.shop_list, container, false); //第一個參數為Fragment的layout
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        InitializeListView(view);
    }

    //初始化ListView
    private void InitializeListView(View view)
    {
        final ArrayList<Shop> shopList = new ArrayList<>();

        //人工填入商店資料
        for (int index = 1; index <= 15; index++)
        {
            String tag = Integer.toString(index);
            shopList.add(new Shop("兔子咖啡 " + tag, "評價分數: 10/10", "兔子市兔子區兔子路1段" + tag + "號"));
        }

        ListView listView = (ListView) view.findViewById(R.id.shopList);
        ShopListAdapter shopListAdapter = new ShopListAdapter(getActivity(), shopList);
        listView.setAdapter(shopListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                ((MainActivity) getActivity()).DisplayShopDetail(shopList.get(position));
            }
        });
    }
}
