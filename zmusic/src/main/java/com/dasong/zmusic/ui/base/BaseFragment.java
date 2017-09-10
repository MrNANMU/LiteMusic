package com.dasong.zmusic.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by dason on 2017/8/19 0019.
 */

public class BaseFragment extends Fragment {

    protected View view;
    private int layoutId;

    protected <T extends View> T findView(View v,int id){
        return (T)v.findViewById(id);
    }

}
