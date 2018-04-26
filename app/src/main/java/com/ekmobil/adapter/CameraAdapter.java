package com.ekmobil.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.ekmobil.R;

public class CameraAdapter extends PagerAdapter {
    private Context context;

    public CameraAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((View) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        /*((ViewPager) container).removeView((View) object);*/
    }

    public Object instantiateItem(View collection, int position) {

        int resId = 0;
        switch (position) {
            case 0:
                resId = R.id.view_pager_camera_rl_1;
                break;
            case 1:
                resId = R.id.view_pager_camera_rl_2;
                break;
        }
        return ((Activity) context).findViewById(resId);
    }
}
