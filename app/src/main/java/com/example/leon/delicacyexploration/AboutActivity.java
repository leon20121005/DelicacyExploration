package com.example.leon.delicacyexploration;

//Created by leon on 2017/10/15.

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        DisplayInformation((TextView) findViewById(R.id.textView));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void DisplayInformation(TextView textView)
    {
        String information = "Delicacy Explorer\n\n";
        information += "專題編號: 106-CSIE-S022\n\n";
        information += "開發人員: 四電資四資工 商資穎\n";
        information += "leon20121005@gmail.com\n\n";
        information += "開發人員: 四電資四資工 王怡萱\n";
        information += "sweetapplein1996@gmail.com\n\n";
        information += "更新日期: 2017/10/18\n\n";
        information += "資料來源: 痞客邦PIXNET";
        textView.setText(information);
    }
}
