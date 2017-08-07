package com.example.leon.delicacyexploration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.Nullable;
import android.widget.TextView;

//Created by leon on 2017/8/6.

public class ShopDetail extends Fragment
{
    private Shop _data;

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
    }
}
