package com.example.leon.delicacyexploration;

import android.content.Context;
import android.view.MotionEvent;
import android.util.AttributeSet;

import com.google.android.gms.maps.MapView;

//Created by leon on 2017/9/11.

public class CustomMapView extends MapView
{
    public CustomMapView(Context context)
    {
        super(context);
    }

    public CustomMapView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }

    public CustomMapView(Context context, AttributeSet attributeSet, int style)
    {
        super(context, attributeSet, style);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        int action = event.getAction();

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                // Disallow ScrollView to intercept touch events.
                this.getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_UP:
                // Allow ScrollView to intercept touch events.
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        // Handle MapView's touch events.
        super.dispatchTouchEvent(event);
        return true;
    }
}
