package com.dasong.zmusic.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dasong.zmusic.R;

/**
 * Created by dason on 2017/9/6 0006.
 */

abstract public class ShowFragment extends BaseFragment {

    protected int layoutId;

    public ShowFragment(int layoutId){
        this.layoutId = layoutId;
    }

    public ShowFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(this.layoutId,container,false);
        this.initView(this.view);
        return this.view;
    }

    abstract protected void initView(View view);
}
