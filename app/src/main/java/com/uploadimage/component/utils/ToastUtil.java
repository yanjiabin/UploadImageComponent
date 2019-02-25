package com.uploadimage.component.utils;

import android.widget.Toast;

import com.uploadimage.component.MyApplication;

/**
 * Created by EDZ on 2019/2/22.
 */

public class ToastUtil {
    public static Toast toast;

    public static void setToast(String str) {

        if (toast == null) {
            toast = Toast.makeText(MyApplication.getContext(), str, Toast.LENGTH_SHORT);
        } else {
            toast.setText(str);
        }
        toast.show();
    }
}
