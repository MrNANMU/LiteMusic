package com.dasong.zmusic.utils.universal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by dason on 2017/9/1 0001.
 */

public class BitMapUtil {

    public static Bitmap compress(byte[] bt,int reqWidth,int reqHeight){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bt,0,bt.length,options);
        final int height = options.outHeight;
        final int wight = options.outWidth;
        int inSampleSize = 1;
        if(height > reqHeight || wight > reqWidth){
            final int halfHeight = height / 2;
            final int halfWidth = wight / 2 ;
            while((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth){
                inSampleSize *= 2;
            }
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bt,0,bt.length,options);
        return bitmap;
    }
}
