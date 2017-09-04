package com.dasong.zmusic.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dasong.zmusic.R;
import com.dasong.zmusic.ui.base.BaseFragment;

/**
 * Created by dason on 2017/8/19 0019.
 */

public class MusicListFragment extends BaseFragment {



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_musiclist,container,false);
        return view;
    }
}
