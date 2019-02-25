package com.uploadimage.component.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by EDZ on 2018/10/16.
 */

public class DisplayUtil {
    public DisplayUtil() {
    }

    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5F);
    }

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5F);
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(1, (float)dp, context.getResources().getDisplayMetrics());
    }

    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(pxValue / fontScale + 0.5F);
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(spValue * fontScale + 0.5F);
    }
}
