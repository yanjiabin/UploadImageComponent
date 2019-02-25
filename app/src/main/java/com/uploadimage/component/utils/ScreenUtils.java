package com.uploadimage.component.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by EDZ on 2018/10/16.
 */

public class ScreenUtils {

    public static int[] WHD;

    public ScreenUtils() {
    }

    public static int[] initWHD(Activity act) {
        if(WHD == null) {
            WHD = WHD(act);
        }

        return WHD;
    }

    public static int[] WHD(@NonNull Context context) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        WindowManager mm = (WindowManager)context.getSystemService("window");
        mm.getDefaultDisplay().getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels, (int)outMetrics.density};
    }
}
