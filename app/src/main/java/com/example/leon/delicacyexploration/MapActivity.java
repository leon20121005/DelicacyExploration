package com.example.leon.delicacyexploration;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements AsyncResponse, OnMapReadyCallback
{
    private final String NEARBY_SHOP_URL = "/android/get_nearby_shops.php";
    private ArrayList<Shop> _shopList = new ArrayList<>();

    private final int MY_LOCATION_REQUEST_CODE = 1;
    private double _latitude;
    private double _longitude;

    private final double DEFAULT_QUERY_RANGE = 3;
    private double _queryRange = DEFAULT_QUERY_RANGE;

    private MapView _mapView;
    private GoogleMap _googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        InitializeLocation();
        SendQuery();

        _mapView = (MapView) findViewById(R.id.mapView);
        _mapView.onCreate(savedInstanceState);
        _mapView.onResume();
        _mapView.getMapAsync(this);
    }

    private void InitializeLocation()
    {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (currentLocation != null)
            {
                _latitude = currentLocation.getLatitude();
                _longitude = currentLocation.getLongitude();
            }
        }
        else
        {
            // Show rationale and request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }
    }

    private void SendQuery()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String latitude = "lat=" + Double.toString(_latitude);
        String longitude = "lng=" + Double.toString(_longitude);
        String radius = "radius=" + Double.toString(_queryRange);
        String limit = "limit=" + Integer.toString(20);
        String homeURL = sharedPreferences.getString(getString(R.string.custom_ip_key), getString(R.string.server_ip_address));
        String queryURL = homeURL + NEARBY_SHOP_URL + "?" + latitude + "&" + longitude + "&" + radius + "&" + limit;

        new HttpRequestAsyncTask(this).execute(queryURL);
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //處理Activity的onRequestPermissionsResult()
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == MY_LOCATION_REQUEST_CODE)
        {
            if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                InitializeLocation();
            }
            else
            {
                // Permission was denied. Display an error message.
                Toast.makeText(this, "No permission to initialize location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void FinishAsyncProcess(String output)
    {
        JsonParser jsonParser = new JsonParser();
        _shopList = jsonParser.ParseShopList(this, output);

        InitializeListView();
    }

    //初始化ListView
    private void InitializeListView()
    {
        ListView listView = (ListView) findViewById(R.id.listView);
        ShopListAdapter shopListAdapter = new ShopListAdapter(this, _shopList);
        listView.setAdapter(shopListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                HighLightShop(_shopList.get(position));

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    private void HighLightShop(Shop shop)
    {
        LatLng latLng = new LatLng(shop.GetLatitude(), shop.GetLongitude());
        _googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        _googleMap = googleMap;
        _googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(_latitude, _longitude), 17));
        EnableMyLocation();
        PositioningAddress();
    }

    //在啟用「我的位置」圖層之前，使用「支援」程式庫檢查權限
    private void EnableMyLocation()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // Show rationale and request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }
        else if (_googleMap != null)
        {
            // Access to the location has been granted to the app
            _googleMap.setMyLocationEnabled(true);
        }
    }

    //根據店家的地址將位置標記在地圖上
    private void PositioningAddress()
    {
        Handler handler = new Handler();
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                for (int index = 0; index < _shopList.size(); index++)
                {
                    LatLng latLng = new LatLng(_shopList.get(index).GetLatitude(), _shopList.get(index).GetLongitude());
                    _googleMap.addMarker(new MarkerOptions().position(latLng).title(_shopList.get(index).GetName()));
                }
            }
        });
    }
}
