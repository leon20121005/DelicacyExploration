package com.example.leon.delicacyexploration;

//Created by leon on 2017/8/6.

public class Shop
{
    private String _name;
    private String _evaluation;
    private String _address;

    public Shop(String name, String evaluation, String address)
    {
        _name = name;
        _evaluation = evaluation;
        _address = address;
    }

    public String GetName()
    {
        return _name;
    }

    public String GetEvaluation()
    {
        return _evaluation;
    }

    public String GetAddress()
    {
        return _address;
    }
}
