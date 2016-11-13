package com.chanhbc.music.adapter;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chanhbc.music.App;
import com.chanhbc.music.R;
import com.chanhbc.music.model.LayoutPager;


import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter implements View.OnClickListener {
    private LinearLayout ll1;
    private LinearLayout ll2;
    private LinearLayout ll3;
    private ArrayList<LayoutPager> pagers;
    private LayoutInflater inflater;
    public static final String LL1 = "LL1";
    public static final String LL2 = "LL2";
    public static final String LL3 = "LL3";
    private OnReceiveDataListener onReceiveDataListener;

    public ViewPagerAdapter(ArrayList<LayoutPager> pagers) {
        this.pagers = pagers;
        inflater = LayoutInflater.from(App.getContext());
    }

    public LinearLayout getLl1() {
        return ll1;
    }

    public LinearLayout getLl2() {
        return ll2;
    }

    public LinearLayout getLl3() {
        return ll3;
    }

    @Override
    public int getCount() {
        return pagers.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(pagers.get(position).getLayout(), container, false);
        ll1 = (LinearLayout) view.findViewById(R.id.ll_1);
        ll2 = (LinearLayout) view.findViewById(R.id.ll_2);
        ll3 = (LinearLayout) view.findViewById(R.id.ll_3);
        ll1.setOnClickListener(this);
        ll2.setOnClickListener(this);
        ll3.setOnClickListener(this);
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_1:
                onReceiveDataListener.onReceiveData(LL1);
                break;

            case R.id.ll_2:
                onReceiveDataListener.onReceiveData(LL2);
                break;

            case R.id.ll_3:
                onReceiveDataListener.onReceiveData(LL3);
                break;

            default:
                break;
        }
    }

    public void setOnReceiveDataListener(OnReceiveDataListener onReceiveDataListener) {
        this.onReceiveDataListener = onReceiveDataListener;
    }

    public interface OnReceiveDataListener {
        void onReceiveData(String receiveData);
    }
}
