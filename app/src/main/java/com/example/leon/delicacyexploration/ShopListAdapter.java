package com.example.leon.delicacyexploration;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Created by leon on 2017/8/5.

public class ShopListAdapter extends ArrayAdapter<Shop>
{
    private final Map<String, Bitmap> _bitmapCache;
    private Context _context;

    public ShopListAdapter(Context context, ArrayList<Shop> shopList)
    {
        super(context, R.layout.shoplist_item, shopList); //第二個參數為ListView內每個元素的layout
        _bitmapCache = new HashMap<>();
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
            viewHolder.shopImage = (ImageView) convertView.findViewById(R.id.shopImage);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.shopName.setText(shop.GetName()); //設定ListView內每個元素的商店名字
        viewHolder.shopEvaluation.setText(shop.GetEvaluation()); //設定ListView內每個元素的商店評價
        viewHolder.shopAddress.setText(shop.GetAddress()); //設定ListView內每個元素的商店地址
        viewHolder.shopImage.setImageResource(R.drawable.ic_camera_alt_gray_24px); //設定ImageView預設的圖片

        String url = shop.GetThumbLink();
        if (!url.equals("null"))
        {
            Bitmap bitmap = _bitmapCache.get(url);
            if (bitmap == null)
            {
                (new ImageDownloaderAsyncTask(viewHolder.shopImage, _bitmapCache)).execute(url);
            }
            else
            {
                viewHolder.shopImage.setImageBitmap(bitmap);
            }
        }

        return convertView;
    }

    private static class ViewHolder
    {
        TextView shopName;
        TextView shopEvaluation;
        TextView shopAddress;
        ImageView shopImage;
    }
}
