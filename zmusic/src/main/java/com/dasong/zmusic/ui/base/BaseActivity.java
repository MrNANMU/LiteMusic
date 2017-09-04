package com.dasong.zmusic.ui.base;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dasong.zmusic.R;

/**
 * Created by dason on 2017/8/6 0006.
 */

public class BaseActivity extends AppCompatActivity {

    protected <T extends View> T findView(int id){
        return (T)this.findViewById(id);
    }
}
