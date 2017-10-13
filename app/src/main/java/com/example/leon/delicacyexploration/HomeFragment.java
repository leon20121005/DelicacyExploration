package com.example.leon.delicacyexploration;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.app_name));

        FloatingActionButton returnButton = (FloatingActionButton) getActivity().findViewById(R.id.returnButton);
        returnButton.hide();
    }
}
