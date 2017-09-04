package com.dasong.zmusic.ui.base;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by dason on 2017/8/19 0019.
 */

public class BaseFragment extends Fragment {

    protected <T extends View> T findView(View v,int id){
        return (T)v.findViewById(id);
    }
}
