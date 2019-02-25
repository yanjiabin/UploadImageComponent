package com.uploadimage.component;

import android.app.Application;
import android.content.Context;

import com.bilibili.boxing.BoxingCrop;
import com.bilibili.boxing.BoxingMediaLoader;
import com.bilibili.boxing.loader.IBoxingMediaLoader;
import com.uploadimage.component.utils.BoxingGlideLoader;
import com.uploadimage.component.utils.BoxingUcrop;
import com.uploadimage.component.utils.DefaultMediaLoader;
import com.uploadimage.component.utils.FixDefaultMediaLoader;

/**
 * Created by EDZ on 2019/2/22.
 */

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        initBoxing();
    }


    private void initBoxing() {
        IBoxingMediaLoader loader = new BoxingGlideLoader();
        BoxingMediaLoader.getInstance().init(loader);
        BoxingCrop.getInstance().init(new BoxingUcrop());
        ((DefaultMediaLoader) DefaultMediaLoader.getInstance()).init(new FixDefaultMediaLoader());
    }
    public static Context getContext() {
        return context;
    }
}
