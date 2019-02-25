package com.uploadimage.component.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.bilibili.boxing.loader.IBoxingCrop;
import com.bilibili.boxing.model.config.BoxingCropOption;
import com.yalantis.ucrop.UCrop;

/**
 * Created by EDZ on 2018/10/16.
 */

public class BoxingUcrop implements IBoxingCrop {

    @Override
    public void onStartCrop(Context context, Fragment fragment, @NonNull BoxingCropOption cropConfig,
                            @NonNull String path, int requestCode) {
        Uri uri = new Uri.Builder()
                .scheme("file")
                .appendPath(path)
                .build();
        UCrop.Options crop = new UCrop.Options();
        // do not copy exif information to crop pictures
        // because png do not have exif and png is not Distinguishable
        crop.setCompressionFormat(Bitmap.CompressFormat.PNG);
        crop.setToolbarColor(Color.parseColor("#000000"));
        crop.setStatusBarColor(Color.parseColor("#000000"));
        crop.setHideBottomControls(true);
        crop.withMaxResultSize(cropConfig.getMaxWidth(), cropConfig.getMaxHeight());
        crop.withAspectRatio(cropConfig.getAspectRatioX(), cropConfig.getAspectRatioY());
        UCrop.of(uri, cropConfig.getDestination())
                .withOptions(crop)
                .start(context, fragment, requestCode);
    }

    @Override
    public Uri onCropFinish(int resultCode, Intent data) {
        if (data == null) {
            return null;
        }
        Throwable throwable = UCrop.getError(data);
        if (throwable != null) {
            return null;
        }
        return UCrop.getOutput(data);
    }
}
