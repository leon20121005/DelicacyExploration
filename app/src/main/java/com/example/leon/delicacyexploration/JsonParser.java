package com.example.leon.delicacyexploration;

import android.content.Context;
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

                    String shopName = tupleJSON.getString("name");
                    String shopEvaluation = Integer.toString(tupleJSON.getInt("evaluation"));
                    String shopAddress = tupleJSON.getString("address");

                    shopList.add(new Shop(shopName, "評價分數: " + shopEvaluation + "/10", shopAddress));
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
}
