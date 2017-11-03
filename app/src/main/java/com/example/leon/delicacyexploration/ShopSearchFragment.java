package com.example.leon.delicacyexploration;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.ArrayList;

//Created by leon on 2017/9/3.

public class ShopSearchFragment extends Fragment implements AsyncResponse, SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener
{
    private final String SEARCH_SHOP_URL = "/android/search_shops.php";
    private SearchView _searchView;
    private ArrayList<Shop> _shopList;

    private String _previousQueryText;
    private int _listViewPreviousIndex;
    private int _listViewPreviousTop;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
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
        getActivity().setTitle(getString(R.string.title_fragment_search));

        FloatingActionButton returnButton = (FloatingActionButton) getActivity().findViewById(R.id.returnButton);
        returnButton.hide();

        if (_previousQueryText != null)
        {
            SendQuery(_previousQueryText);
        }
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
        _previousQueryText = query.trim();
        _searchView.clearFocus(); //提交查詢之後關閉鍵盤
        SendQuery(_previousQueryText);

        return false;
    }

    private void SendQuery(String query)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String queryKeyword = "keyword=" + query;
        String homeURL = sharedPreferences.getString(getString(R.string.custom_ip_key), getString(R.string.server_ip_address));
        String queryURL = homeURL + SEARCH_SHOP_URL + "?" + queryKeyword;

        new HttpRequestAsyncTask((Fragment) this).execute(queryURL);
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
        JsonParser jsonParser = new JsonParser(getActivity());
        _shopList = jsonParser.ParseShopList(output);

        InitializeListView(getView());
    }

    //初始化ListView
    private void InitializeListView(View view)
    {
        TextView welcomeTextView = (TextView) view.findViewById(R.id.welcomeTextView);
        welcomeTextView.setVisibility(View.GONE);

        final ListView listView = (ListView) view.findViewById(R.id.searchList);
        TextView emptyTextView = (TextView) view.findViewById(R.id.searchEmpty);
        listView.setEmptyView(emptyTextView);

        ShopListAdapter shopListAdapter = new ShopListAdapter(getActivity(), _shopList);
        listView.setAdapter(shopListAdapter);
        listView.setSelectionFromTop(_listViewPreviousIndex, _listViewPreviousTop);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                // Save index and top position
                _listViewPreviousIndex = listView.getFirstVisiblePosition();
                View childView = listView.getChildAt(0);
                _listViewPreviousTop = (childView == null) ? 0 : (childView.getTop() - listView.getPaddingTop());

                ((MainActivity) getActivity()).DisplayShopDetail(_shopList.get(position));
            }
        });
    }
}
