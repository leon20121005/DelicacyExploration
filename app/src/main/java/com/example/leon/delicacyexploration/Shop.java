package com.example.leon.delicacyexploration;

//Created by leon on 2017/8/6.

public class Shop
{
    private int _id;
    private String _name;
    private String _evaluation;
    private String _address;

    public Shop(int id, String name, String evaluation, String address)
    {
        _id = id;
        _name = name;
        _evaluation = evaluation;
        _address = address;
    }

    public int GetID()
    {
        return _id;
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
