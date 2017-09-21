package com.example.leon.delicacyexploration;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//Created by leon on 2017/8/26.

public class JsonParser
{
    public JsonParser()
    {
    }

    public ArrayList<Shop> ParseShopList(Context context, String jsonText)
    {
        ArrayList<Shop> shopList = new ArrayList<>();
        JSONObject jsonObject;
        JSONArray shops;

        try
        {
            jsonObject = new JSONObject(jsonText);
            int isSuccess = jsonObject.getInt("success");

            if (isSuccess == 1)
            {
                shops = jsonObject.getJSONArray("shops");

                for (int index = 0; index < shops.length(); index++)
                {
                    JSONObject tupleJSON = shops.getJSONObject(index);

                    int shopID = tupleJSON.getInt("id");
                    String shopName = tupleJSON.getString("name");
                    String shopEvaluation = Integer.toString(tupleJSON.getInt("evaluation"));
                    String shopAddress = tupleJSON.getString("address");
                    double latitude = tupleJSON.getDouble("latitude");
                    double longitude = tupleJSON.getDouble("longitude");

                    shopList.add(new Shop(shopID, shopName, "評價分數: " + shopEvaluation + "/10", shopAddress, latitude, longitude));
                }
            }
            else
            {
                Toast.makeText(context, "No shops found", Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException exception)
        {
            exception.printStackTrace();
        }

        return shopList;
    }

    public ArrayList<Bitmap> ParseShopImage(Context context, String jsonText)
    {
        ArrayList<Bitmap> imageList = new ArrayList<>();
        JSONObject jsonObject;
        JSONArray images;

        try
        {
            jsonObject = new JSONObject(jsonText);
            int isSuccess = jsonObject.getInt("success");

            if (isSuccess == 1)
            {
                images = jsonObject.getJSONArray("images");

                for (int index = 0; index < images.length(); index++)
                {
                    JSONObject tupleJSON = images.getJSONObject(index);

                    String imageData = tupleJSON.getString("image");
                    Bitmap bitmap;

                    try
                    {
                        byte[] encodeByte = Base64.decode(imageData, Base64.DEFAULT);
                        bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                    }
                    catch(Exception e)
                    {
                        bitmap = null;
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    imageList.add(bitmap);
                }
            }
            else
            {
                Toast.makeText(context, "No images found", Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException exception)
        {
            exception.printStackTrace();
        }

        return imageList;
    }
}
