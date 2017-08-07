package com.example.leon.delicacyexploration;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.support.annotation.NonNull;

import java.util.ArrayList;

//Created by leon on 2017/8/5.

public class ShopListAdapter extends ArrayAdapter<Shop>
{
    private Context _context;

    public ShopListAdapter(Context context, ArrayList<Shop> shopList)
    {
        super(context, R.layout.shoplist_item, shopList); //第二個參數為ListView內每個元素的layout
        _context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = new ViewHolder();
        Shop shop = getItem(position);

        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.shoplist_item, parent, false);

            //設定ListView內每個元素裡面的TextView元件
            viewHolder.shopName = (TextView) convertView.findViewById(R.id.shopName);
            viewHolder.shopEvaluation = (TextView) convertView.findViewById(R.id.shopEvaluation);
            viewHolder.shopAddress = (TextView) convertView.findViewById(R.id.shopAddress);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.shopName.setText(shop.GetName()); //設定ListView內每個元素的商店名字
        viewHolder.shopEvaluation.setText(shop.GetEvaluation()); //設定ListView內每個元素的商店評價
        viewHolder.shopAddress.setText(shop.GetAddress()); //設定ListView內每個元素的商店地址

        return convertView;
    }

    private static class ViewHolder
    {
        TextView shopName;
        TextView shopEvaluation;
        TextView shopAddress;
    }
}
