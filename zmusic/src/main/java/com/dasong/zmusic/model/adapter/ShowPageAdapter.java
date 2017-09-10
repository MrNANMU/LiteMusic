package com.dasong.zmusic.model.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dasong.zmusic.ui.base.BaseFragment;

/**
 * Created by dason on 2017/9/6 0006.
 */

public class ShowPageAdapter extends FragmentStatePagerAdapter {

    private BaseFragment fragment;

    public <T extends BaseFragment>ShowPageAdapter(FragmentManager fm,T t) {
        super(fm);
        this.fragment = (T)t;
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragment;
    }

    @Override
    public int getCount() {
        return 1;
    }
}
