package com.example.leon.delicacyexploration;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.annotation.Nullable;

//Created by leon on 2017/10/9.

public class HomeFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        // Returning our layout file
        return inflater.inflate(R.layout.home, container, false); //第一個參數為Fragment的layout
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        ViewHolder viewHolder = new ViewHolder();

        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.app_name));

        FloatingActionButton returnButton = (FloatingActionButton) getActivity().findViewById(R.id.returnButton);
        returnButton.hide();

        viewHolder.listButton = (Button) view.findViewById(R.id.list_button);
        viewHolder.nearbyButton = (Button) view.findViewById(R.id.nearby_button);
        viewHolder.searchButton = (Button) view.findViewById(R.id.search_button);
        viewHolder.favoriteButton = (Button) view.findViewById(R.id.favorite_button);
        viewHolder.aboutButton = (Button) view.findViewById(R.id.about_button);

        viewHolder.listButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ((MainActivity) getActivity()).DisplayShopList();
            }
        });
        viewHolder.nearbyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ((MainActivity) getActivity()).DisplayNearbyShop();
            }
        });
        viewHolder.searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ((MainActivity) getActivity()).DisplaySearchShop();
            }
        });
        viewHolder.favoriteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ((MainActivity) getActivity()).DisplayFavoriteShop();
            }
        });
        viewHolder.aboutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getActivity(), AboutActivity.class));
            }
        });
    }

    private static class ViewHolder
    {
        Button listButton;
        Button nearbyButton;
        Button searchButton;
        Button favoriteButton;
        Button aboutButton;
    }
}
