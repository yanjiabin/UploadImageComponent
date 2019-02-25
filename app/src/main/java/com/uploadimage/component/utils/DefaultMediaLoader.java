package com.uploadimage.component.utils;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


/**
 * Created by EDZ on 2018/10/16.
 */

public class DefaultMediaLoader implements IMediaLoader {

    IMediaLoader mediaLoader = new IMediaLoader() {
        public void displayThumbnail(@NonNull ImageView img, @NonNull String absPath, int width, int height) {
            Glide.with(img.getContext()).load(absPath).into(img);
        }


        public void displayRaw(@NonNull ImageView img, @NonNull String absPath) {
            Glide.with(img.getContext()).load(absPath).into(img);
        }
    };
    static DefaultMediaLoader defaultMediaLoader = new DefaultMediaLoader();

    public DefaultMediaLoader() {
    }

    public void init(IMediaLoader mediaLoader) {
        if(mediaLoader == null) {
            throw new NullPointerException("IMediaLoader is null!");
        } else {
            this.mediaLoader = mediaLoader;
        }
    }

    public static IMediaLoader getInstance() {
        return defaultMediaLoader;
    }

    public void displayThumbnail(@NonNull ImageView img, @NonNull String absPath, int width, int height) {
        this.mediaLoader.displayThumbnail(img, absPath, width, height);
    }


    public void displayRaw(@NonNull ImageView img, @NonNull String absPath) {
        this.mediaLoader.displayRaw(img, absPath);
    }
}
