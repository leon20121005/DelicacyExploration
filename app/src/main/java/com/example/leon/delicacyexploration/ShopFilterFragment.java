package com.example.leon.delicacyexploration;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
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
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.ArrayList;

//Created by leon on 2017/8/22.

public class ShopFilterFragment extends Fragment implements AsyncResponse, SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener
{
    private final String SHOP_LIST_URL = "/android/get_all_shops.php";
    private ArrayList<Shop> _shopList;
    private ArrayList<Shop> _filteredShopList;
    private ShopListAdapter _shopListAdapter;
    private ListView _listView;

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
        return inflater.inflate(R.layout.shop_filter, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("店家過濾");

        FloatingActionButton returnButton = (FloatingActionButton) getActivity().findViewById(R.id.returnButton);
        returnButton.hide();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String homeURL = sharedPreferences.getString(getString(R.string.custom_ip_key), getString(R.string.server_ip_address));

        new HttpRequestAsyncTask((Fragment) this).execute(homeURL + SHOP_LIST_URL);
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
        TextView emptyTextView = (TextView) view.findViewById(R.id.filterEmpty);
        _listView = (ListView) view.findViewById(R.id.filterList);
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
