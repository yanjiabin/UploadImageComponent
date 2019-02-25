package com.uploadimage.component.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;

/**
 * Created by EDZ on 2018/10/16.
 */

public interface IMediaLoader {
    void displayThumbnail(@NonNull ImageView var1, @NonNull String var2, int var3, int var4);


    void displayRaw(@NonNull ImageView var1, @NonNull String var2);

    public interface IMediaLoadeProgressListener {
        void onProgressUpdate(int var1, int var2);

        void onLoadingComplete(String var1, Drawable var2);

        void onLoadingFailed(String var1);
    }

}
