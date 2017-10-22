package com.example.leon.delicacyexploration;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Stack;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BitmapCacheManager
{
    private Stack<Fragment> _fragmentStack;
    private Map<String, Bitmap> _bitmapCache;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton returnButton = (FloatingActionButton) findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (_fragmentStack.size() > 1)
                {
                    _fragmentStack.pop();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_frame, _fragmentStack.peek());
                    transaction.commit();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        _fragmentStack = new Stack<>();
        _bitmapCache = new HashMap<>();

        DisplayHome();
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (_fragmentStack.size() > 1) //如果在店家細節時按上一頁則回到店家細節的上一個列表
        {
            _fragmentStack.pop();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, _fragmentStack.peek());
            transaction.commit();
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
        getMenuInflater().inflate(R.menu.main, menu);
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
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home)
        {
            DisplayHome();
        }
        else if (id == R.id.nav_shop_list)
        {
            DisplayShopList();
        }
        else if (id == R.id.nav_shop_filter)
        {
            DisplayFilterShop();
        }
        else if (id == R.id.nav_shop_nearby)
        {
            DisplayNearbyShop();
        }
        else if (id == R.id.nav_shop_search)
        {
            DisplaySearchShop();
        }
        else if (id == R.id.nav_shop_favorite)
        {
            DisplayFavoriteShop();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //實作「支援」程式庫中的ActivityCompat.OnRequestPermissionsResultCallback，以處理權限要求的結果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (_fragmentStack.peek().getClass() == ShopDetailFragment.class)
        {
            ((ShopDetailFragment) _fragmentStack.peek()).OnRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        else if (_fragmentStack.peek().getClass() == ShopNearbyFragment.class)
        {
            ((ShopNearbyFragment) _fragmentStack.peek()).OnRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void DisplayHome()
    {
        _fragmentStack.push(new HomeFragment());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, _fragmentStack.peek());
        transaction.commit();
    }

    public void DisplayShopList()
    {
        _fragmentStack.push(new ShopListFragment());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, _fragmentStack.peek());
        transaction.commit();
    }

    public void DisplayFilterShop()
    {
        _fragmentStack.push(new ShopFilterFragment());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, _fragmentStack.peek());
        transaction.commit();
    }

    public void DisplayNearbyShop()
    {
        _fragmentStack.push(new ShopNearbyFragment());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, _fragmentStack.peek());
        transaction.commit();
    }

    public void DisplaySearchShop()
    {
        _fragmentStack.push(new ShopSearchFragment());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, _fragmentStack.peek());
        transaction.commit();
    }

    public void DisplayFavoriteShop()
    {
        _fragmentStack.push(new ShopFavoriteFragment());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, _fragmentStack.peek());
        transaction.commit();
    }

    public void DisplayShopDetail(Shop shop)
    {
        _fragmentStack.push(new ShopDetailFragment());
        ((ShopDetailFragment) _fragmentStack.peek()).SetShopData(shop);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, _fragmentStack.peek());
        transaction.commit();
    }

    public Map<String, Bitmap> GetBitmapCache()
    {
        return _bitmapCache;
    }
}
