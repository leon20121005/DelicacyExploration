package com.example.leon.delicacyexploration;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//Created by leon on 2017/8/22.

public class SearchShop extends Fragment implements AsyncResponse, SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener
{
    private final String SHOP_LIST_URL = "http://36.231.104.82/android/get_all_shops.php";
    private ArrayList<Shop> _shopList = new ArrayList<>();
    private ArrayList<Shop> _filteredShopList;
    private ShopListAdapter _shopListAdapter;
    private ListView _listView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Returning our layout file
        return inflater.inflate(R.layout.search_shop, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("店家搜尋");

        new HttpRequestAsyncTask((Fragment) this).execute(SHOP_LIST_URL);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.actionSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("輸入店家名稱");
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        if (newText == null || newText.trim().isEmpty())
        {
            ResetSearch();
            return false;
        }

        _filteredShopList = new ArrayList<>(_shopList);
        for (int index = 0; index < _filteredShopList.size(); index++)
        {
            if (!_filteredShopList.get(index).GetName().toLowerCase().contains(newText.toLowerCase()))
            {
                _filteredShopList.remove(index);
                index--;
            }
        }

        _shopListAdapter = new ShopListAdapter(getActivity(), _filteredShopList);
        _listView.setAdapter(_shopListAdapter);
        return false;
    }

    private void ResetSearch()
    {
        _shopListAdapter = new ShopListAdapter(getActivity(), _shopList);
        _listView.setAdapter(_shopListAdapter);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item)
    {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item)
    {
        return true;
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
        TextView emptyTextView = (TextView) view.findViewById(R.id.searchEmpty);
        _listView = (ListView) view.findViewById(R.id.searchList);
        _listView.setEmptyView(emptyTextView);

        ShopListAdapter shopListAdapter = new ShopListAdapter(getActivity(), _shopList);
        _listView.setAdapter(shopListAdapter);

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                if (_filteredShopList != null)
                {
                    ((MainActivity) getActivity()).DisplayShopDetail(_filteredShopList.get(position));
                }
                else
                {
                    ((MainActivity) getActivity()).DisplayShopDetail(_shopList.get(position));
                }
            }
        });
    }
}