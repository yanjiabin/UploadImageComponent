package com.uploadimage.component.utils;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by EDZ on 2018/10/16.
 */

public class FixDefaultMediaLoader implements IMediaLoader {
    public void displayThumbnail(@NonNull ImageView img, @NonNull String absPath, int width, int height) {
        Glide.with(img.getContext()).load(absPath).into(img);
    }

    public void displayImage(@NonNull ImageView img, @NonNull final String absPath, final IMediaLoadeProgressListener mediaLoadeProgressListener) {
        Glide.with(img.getContext()).load(absPath).into(img);
    }

    public void displayRaw(@NonNull ImageView img, @NonNull String absPath) {
        Glide.with(img.getContext()).load(absPath).into(img);
    }

}
