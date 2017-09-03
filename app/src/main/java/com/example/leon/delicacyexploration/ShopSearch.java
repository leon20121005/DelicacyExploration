package com.example.leon.delicacyexploration;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

//Created by leon on 2017/9/3.

public class ShopSearch extends Fragment implements AsyncResponse, SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener
{
    private final String SEARCH_SHOP_URL = "/android/search_shops.php";
    private SearchView _searchView;
    private ArrayList<Shop> _shopList;

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
        return inflater.inflate(R.layout.shop_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("店家搜尋");

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.show();
        FloatingActionButton returnButton = (FloatingActionButton) getActivity().findViewById(R.id.returnButton);
        returnButton.hide();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.actionSearch);
        _searchView = (SearchView) searchItem.getActionView();
        _searchView.setOnQueryTextListener(this);
        _searchView.setQueryHint("輸入店家名稱或地址");
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        if (query == null || query.trim().isEmpty())
        {
            return false;
        }

        String queryKeyword = "keyword=" + query.trim();
        String queryURL = getString(R.string.server_ip_address) + SEARCH_SHOP_URL + "?" + queryKeyword;
        new HttpRequestAsyncTask((Fragment) this).execute(queryURL);

        _searchView.clearFocus(); //提交查詢之後關閉鍵盤

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem)
    {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem)
    {
        return true;
    }

    @Override
    public void FinishAsyncProcess(String output)
    {
        JsonParser jsonParser = new JsonParser();
        _shopList = jsonParser.ParseShopList(getActivity(), output);

        InitializeListView(getView());
    }

    //初始化ListView
    private void InitializeListView(View view)
    {
        TextView emptyTextView = (TextView) view.findViewById(R.id.searchEmpty);
        ListView listView = (ListView) view.findViewById(R.id.searchList);
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
}
