package com.example.leon.delicacyexploration;

//Created by leon on 2017/8/6.

public class Shop
{
    private int _id;
    private String _name;
    private String _evaluation;
    private String _address;
    private double _latitude;
    private double _longitude;
    private String _thumbLink;

    public Shop(int id, String name, String evaluation, String address, double latitude, double longitude, String thumbLink)
    {
        _id = id;
        _name = name;
        _evaluation = evaluation;
        _address = address;
        _latitude = latitude;
        _longitude = longitude;
        _thumbLink = thumbLink;
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

    public double GetLatitude()
    {
        return _latitude;
    }

    public double GetLongitude()
    {
        return _longitude;
    }

    public String GetThumbLink()
    {
        return _thumbLink;
    }
}
