package com.uploadimage.component.utils;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bilibili.boxing.loader.IBoxingCallback;
import com.bilibili.boxing.loader.IBoxingMediaLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.uploadimage.component.R;

/**
 * Created by EDZ on 2018/10/16.
 */

public class BoxingGlideLoader implements IBoxingMediaLoader {

    @Override
    public void displayThumbnail(@NonNull ImageView img, @NonNull String absPath, int width, int height) {
        String path = "file://" + absPath;
        try {
            // https://github.com/bumptech/glide/issues/1531
            Glide.with(img.getContext()).load(path).placeholder(R.mipmap.ic_default_image).crossFade().centerCrop().into(img);
        } catch (IllegalArgumentException ignore) {
        }

    }

    @Override
    public void displayRaw(@NonNull final ImageView img, @NonNull String absPath, int width, int height, final IBoxingCallback callback) {
        String path = "file://" + absPath;
        Glide.with(img.getContext())
                .load(path)
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        if (callback != null) {
                            callback.onFail(e);
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (resource != null && callback != null) {
                            img.setImageBitmap(resource);
                            callback.onSuccess();
                            return true;
                        }
                        return false;
                    }
                })
                .into(img);
    }


}
