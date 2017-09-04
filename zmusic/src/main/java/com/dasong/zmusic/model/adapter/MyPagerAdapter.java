package com.dasong.zmusic.model.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;

import com.dasong.zmusic.ui.base.BaseFragment;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by dason on 2017/8/19 0019.
 */

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    private List<BaseFragment> fragments;
    private List<String> titles;

    public MyPagerAdapter(FragmentManager fm, List<BaseFragment> fragments, List<String> titles) {
        super(fm);
        this.fragments = fragments;
        this.titles = titles;
    }


    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        //Log.d("ZLogcat","ClassName->" + this.fragments.get(position).getClass().getName());
        return this.fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.titles.get(position);
    }
}
